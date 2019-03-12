package game.pokerrounds;

import game.ExecutionState;
import game.Game;
import game.Stake;
import game.messages.CommonStateMessagesList;
import game.speed.GameSpeed;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class StartGameRound extends StakesPokerRound
{
  public void complited()
  {
  }

  public void init()
  {
  }

  public void run()
  {
    TimerTask task = new MessagesSenderTimerTask(getExecutionState(), getGame().getGameSpeed());
    Timer timer = new Timer();
    timer.schedule(task, new Date(), 10000L);

    getExecutionState().forbid();
    synchronized (getExecutionState()) {
      while (!getExecutionState().canExec()) {
        try {
          getExecutionState().wait();
        }
        catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }

      if (isNeedEndGame()) {
        task.cancel();
        timer.cancel();

        getGame().cancelStart();
        return;
      }
    }

    getGame().setAsStarted();
  }

  public boolean acceptStake(Stake stake)
  {
    return false;
  }

  private class MessagesSenderTimerTask extends TimerTask
  {
    private int time = 10;
    private int iterations = 0;
    private ExecutionState ownerState = null;

    public MessagesSenderTimerTask(ExecutionState ownerState, GameSpeed speed)
    {
      this.ownerState = ownerState;
    }

    public void run()
    {
      if (iterations < 1) {
        getGame().getPublicStateMessagesList().addCommonMessage(2, time);

        time -= 10;
        iterations += 1;
      }
      else {
        synchronized (ownerState) {
          ownerState.permit();
          ownerState.notifyAll();
        }
      }
    }
  }
}