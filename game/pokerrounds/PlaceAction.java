package game.pokerrounds;

import game.Desk;
import game.ExecutionState;
import game.Game;
import game.Place;
import game.PlacesList;
import game.Player;
import game.messages.CommonStateMessagesList;
import game.messages.PrivateMessage;
import game.messages.PrivateStateMessagesList;
import game.speed.GameSpeed;
import game.stats.StatsCounter;
import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

public class PlaceAction
{
  private Place place;
  private StakesPokerRound owner;
  private int actionCode;

  public PlaceAction()
  {
    place = null;
    owner = null;
    actionCode = 0;
  }
  public void setPlace(Place place) {
    this.place = place;
  }

  public void setOwner(StakesPokerRound owner) {
    this.owner = owner;
  }

  public Place getPlace() {
    return place;
  }

  public StakesPokerRound getOwner() {
    return owner;
  }

  public int execute() {
    getOwner().getGame().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), getActionCode(), place.getNumber(), 1);

    LinkedList leaveDeskQuery = getOwner().getGame().getDesk().getLeaveDeskQuery();

    boolean forciblyFoldHim = false;
    Player placePlayer = place.getPlayer();
    synchronized (leaveDeskQuery) {
      int size = leaveDeskQuery.size();
      for (int i = 0; i < size; i++) {
        Player player = (Player)leaveDeskQuery.get(i);
        if (player.getID() == placePlayer.getID()) {
          forciblyFoldHim = true;
          break;
        }

      }

    }

    MessagesSenderTimerTask timerTask = new MessagesSenderTimerTask(getOwner().getGame().getGameSpeed());

    Timer timer = new Timer();
    if (!forciblyFoldHim) {
      timer.schedule(timerTask, new Date(), timerTask.getTimeInterval() * 1000);
    } else {
      ExecutionState ownerState = getOwner().getExecutionState();
      synchronized (ownerState) {
        ownerState.setSignalType(1);
        ownerState.permit();
        ownerState.notifyAll();
      }
    }
    int result;
    synchronized (getOwner().getExecutionState()) {
      while (!getOwner().getExecutionState().canExec()) {
        try {
          getOwner().getExecutionState().wait();
        }
        catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
      getOwner().getExecutionState().forbid();

      timerTask.cancel();
      timer.cancel();
      int result;
      if (getOwner().getExecutionState().isTimerSignal()) {
        int messageCode = 20;
        synchronized (place) {
          if ((getActionCode() == 4) || (getActionCode() == 5) || (getActionCode() == 40))
          {
            messageCode = 10;
            place.markAsSittingOut();
          } else {
            place.markAsFold();
            new StatsCounter(getOwner().getGame().getDesk()).countFolds(place);
            new StatsCounter(getOwner().getGame().getDesk()).countFlopSeens(place.getPlayer());
            new StatsCounter(getOwner().getGame().getDesk()).countFourthStreetSeens(place.getPlayer());
            getOwner().getGame().getPlacesList().getPlace(place.getNumber()).setAsDiscon();
          }

          if (place != null) {
            getOwner().getGame().getPlacesList().getPlace(place.getNumber()).getStateMessagesList().addPrivateMessage(messageCode, 0);
            getOwner().getGame().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), messageCode, place.getNumber(), 2);
          }
        }
        result = -1;
      }
      else
      {
        int result;
        if (getOwner().getExecutionState().isLeaveDeskSignal())
          result = 0;
        else {
          result = 1;
        }
      }
    }
    return result;
  }

  public void setActionCode(int actionCode) {
    this.actionCode = actionCode;
  }

  public int getActionCode() {
    return actionCode;
  }

  private class MessagesSenderTimerTask extends TimerTask {
    private int time = 0;
    private int timeInterval = 0;
    private int iterations = 0;
    private int iterationsCount = 0;

    StakesPokerRound owner = getOwner();
    boolean discon = owner.getGame().getPlacesList().getPlace(getPlace().getNumber()).isDiscon();

    public int getTimeInterval()
    {
      return timeInterval;
    }

    public int getIterationsCount() {
      return iterationsCount;
    }

    public MessagesSenderTimerTask(GameSpeed speed)
    {
      switch (speed.getType()) {
      case 1:
        if (!discon) {
          iterationsCount = 2;
          timeInterval = 5;
        } else {
          iterationsCount = 1;
          timeInterval = 4;
        }
        time = (iterationsCount * timeInterval);
        break;
      case 0:
        if (!discon) {
          iterationsCount = 3;
          timeInterval = 10;
        } else {
          iterationsCount = 2;
          timeInterval = 3;
        }
        time = (iterationsCount * timeInterval);
        break;
      default:
        throw new RuntimeException("PlaceAction.MessagesSenderTimerTask - unknown game speed " + speed.getType());
      }
    }

    public void run()
    {
      StakesPokerRound owner = getOwner();

      if (iterations < iterationsCount) {
        if (iterations == 0) {
          PrivateMessage message = new PrivateMessage(getActionCode(), time, owner.isCanCheck(), owner.getNeedBet(), owner.getNeedCall(), owner.getNeedRaise(), owner.getMaxStake(), owner.isAllIn(), owner.getNeedBringIn());
          owner.getGame().getPlacesList().getPlace(getPlace().getNumber()).getStateMessagesList().addMessage(message);
        }

        time -= timeInterval;

        iterations += 1;
      }
      else {
        ExecutionState ownerState = owner.getExecutionState();
        synchronized (ownerState) {
          ownerState.setSignalType(1);
          ownerState.permit();
          ownerState.notifyAll();
        }
        cancel();
      }
    }
  }
}