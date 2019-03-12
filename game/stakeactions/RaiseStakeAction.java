package game.stakeactions;

import defaultvalues.DefaultValue;
import game.Desk;
import game.ExecutionState;
import game.Game;
import game.Place;
import game.Player;
import game.Stake;
import game.messages.CommonMessage;
import game.messages.CommonStateMessagesList;
import game.pokerrounds.PokerRound;
import game.pokerrounds.StakesPokerRound;
import game.stats.PlaceSessionStats;
import game.stats.StatsCounter;
import java.math.BigDecimal;

public class RaiseStakeAction extends StakeAction
{
  public RaiseStakeAction(Stake stake, StakesPokerRound owner)
  {
    super(stake, owner);
  }

  public boolean execute()
  {
    StakeActionState state = new StatesFactory(null).getState();
    return state.execute();
  }

  private class PotLimitState
    implements StakeActionState
  {
    private PotLimitState()
    {
    }

    public boolean execute()
    {
      boolean result = false;
      Place place = stake.getPlace();

      if ((getOwner().getCurrentPlaceNumber() == place.getNumber()) && (getOwner().getNeedRaise().floatValue() != 0.0F)) {
        ExecutionState ownerState = getOwner().getExecutionState();
        synchronized (ownerState) {
          if ((!stake.isAllIn()) && (place.getAmount().floatValue() > stake.getAmount().floatValue()) && (stake.getAmount().floatValue() >= getOwner().getNeedRaise().floatValue()) && (stake.getAmount().floatValue() <= getOwner().getGame().getBetsAndGameAmount().add(getOwner().getNeedCall().multiply(new BigDecimal(2)).setScale(2, 5)).floatValue()))
          {
            synchronized (place) {
              BigDecimal oldStakingAmount = place.getStakingAmount();
              place.setStakingAmount(oldStakingAmount.add(stake.getAmount()).setScale(2, 5));
              place.decAmount(stake.getAmount());
            }

            getOwner().getGame().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), 17, place.getNumber(), 2, stake.getAmount());
            result = true;
          }
          else if ((stake.isAllIn()) && (stake.getAmount().compareTo(place.getAmount()) == 0) && (stake.getAmount().floatValue() > 0.0F)) {
            synchronized (place) {
              BigDecimal oldStakingAmount = place.getStakingAmount();
              place.setStakingAmount(oldStakingAmount.add(stake.getAmount()).setScale(2, 5));
              place.setAmount(new BigDecimal(0));

              place.markAsAllIn(oldStakingAmount.add(stake.getAmount()).setScale(2, 5));
              getOwner().getGame().notifyAboutAllIn();
              getOwner().getGame().getCurrentRound().notifyAboutAllIn(place);
            }

            CommonMessage msg = new CommonMessage(place.getPlayer().getLogin(), 17, place.getNumber(), 2, stake.getAmount(), true);
            getOwner().getGame().getPublicStateMessagesList().addMessage(msg);
            getOwner().getGame().notifyAboutAllIn();

            result = true;
          }

          int type = 2;
          if (result)
          {
            getOwner().incReRaise(place.getPlayer());
            if (getOwner().hasReRaise(place.getPlayer()))
              new StatsCounter(getOwner().getGame().getDesk()).counReRaise(place.getPlayer());
            else {
              new StatsCounter(getOwner().getGame().getDesk()).counRaise(place.getPlayer());
            }
            place.getPlaceSessionStats().countTotalBettedForLastHour(stake.getAmount());

            getOwner().setLastStake(stake);
            getOwner().notifyAboutRaise(place);
          }
          else {
            type = 1;
          }

          ownerState.setSignalType(type);
          ownerState.permit();
          ownerState.notifyAll();
        }
      }

      return result;
    }
  }

  private class NoLimitState
    implements StakeActionState
  {
    private NoLimitState()
    {
    }

    public boolean execute()
    {
      boolean result = false;
      Place place = stake.getPlace();

      if ((getOwner().getCurrentPlaceNumber() == place.getNumber()) && (getOwner().getNeedRaise().floatValue() != 0.0F)) {
        ExecutionState ownerState = getOwner().getExecutionState();
        synchronized (ownerState) {
          if ((!stake.isAllIn()) && (place.getAmount().floatValue() > stake.getAmount().floatValue()) && (stake.getAmount().floatValue() >= getOwner().getNeedRaise().floatValue()))
          {
            synchronized (place) {
              BigDecimal oldStakingAmount = place.getStakingAmount();
              place.setStakingAmount(oldStakingAmount.add(stake.getAmount()).setScale(2, 5));
              place.decAmount(stake.getAmount());
            }

            getOwner().getGame().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), 17, place.getNumber(), 2, stake.getAmount());
            result = true;
          }
          else if ((stake.isAllIn()) && (stake.getAmount().compareTo(place.getAmount()) == 0) && (stake.getAmount().floatValue() > 0.0F)) {
            synchronized (place) {
              BigDecimal oldStakingAmount = place.getStakingAmount();
              place.setStakingAmount(oldStakingAmount.add(stake.getAmount()).setScale(2, 5));
              place.setAmount(new BigDecimal(0));

              place.markAsAllIn(oldStakingAmount.add(stake.getAmount()).setScale(2, 5));
              getOwner().getGame().notifyAboutAllIn();
              getOwner().getGame().getCurrentRound().notifyAboutAllIn(place);
            }

            CommonMessage msg = new CommonMessage(place.getPlayer().getLogin(), 17, place.getNumber(), 2, stake.getAmount(), true);
            getOwner().getGame().getPublicStateMessagesList().addMessage(msg);
            getOwner().getGame().notifyAboutAllIn();

            result = true;
          }

          int type = 2;
          if (result)
          {
            getOwner().incReRaise(place.getPlayer());
            if (getOwner().hasReRaise(place.getPlayer()))
              new StatsCounter(getOwner().getGame().getDesk()).counReRaise(place.getPlayer());
            else {
              new StatsCounter(getOwner().getGame().getDesk()).counRaise(place.getPlayer());
            }
            place.getPlaceSessionStats().countTotalBettedForLastHour(stake.getAmount());

            getOwner().setLastStake(stake);
            getOwner().notifyAboutRaise(place);
          }
          else {
            type = 1;
          }

          ownerState.setSignalType(type);
          ownerState.permit();
          ownerState.notifyAll();
        }

      }

      return result;
    }
  }

  private class LimitState
    implements StakeActionState
  {
    private LimitState()
    {
    }

    public boolean execute()
    {
      Place place = stake.getPlace();
      BigDecimal heBeted = DefaultValue.ZERO_BIDECIMAL;
      if ((getOwner().getCurrentPlaceNumber() == place.getNumber()) && (getOwner().getNeedRaise().floatValue() != 0.0F))
      {
        int type = 2;
        boolean result = true;

        ExecutionState ownerState = getOwner().getExecutionState();
        synchronized (ownerState) {
          if (place.getAmount().floatValue() > getOwner().getNeedRaise().floatValue()) {
            synchronized (place) {
              BigDecimal old = place.getStakingAmount();
              place.setStakingAmount(old.add(getOwner().getNeedRaise()).setScale(2, 5));
              place.decAmount(getOwner().getNeedRaise());
              heBeted = getOwner().getNeedRaise();
            }

            getOwner().getGame().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), 17, place.getNumber(), 2, getOwner().getNeedRaise());
            result = true;
          }
          else if ((getOwner().isAllIn()) && (place.getAmount().floatValue() <= getOwner().getNeedRaise().floatValue())) {
            BigDecimal avalableAmount = new BigDecimal(0);
            synchronized (place) {
              avalableAmount = place.getAmount();
              heBeted = avalableAmount;
              BigDecimal old = place.getStakingAmount();
              place.setStakingAmount(old.add(avalableAmount).setScale(2, 5));
              place.setAmount(new BigDecimal(0));

              place.markAsAllIn(old.add(avalableAmount).setScale(2, 5));
              getOwner().getGame().notifyAboutAllIn();
              getOwner().getGame().getCurrentRound().notifyAboutAllIn(place);
            }

            CommonMessage msg = new CommonMessage(place.getPlayer().getLogin(), 17, place.getNumber(), 2, avalableAmount, true);
            getOwner().getGame().getPublicStateMessagesList().addMessage(msg);
            getOwner().getGame().notifyAboutAllIn();
            result = true;
          }

          if (result)
          {
            getOwner().incReRaise(place.getPlayer());
            if (getOwner().hasReRaise(place.getPlayer()))
              new StatsCounter(getOwner().getGame().getDesk()).counReRaise(place.getPlayer());
            else {
              new StatsCounter(getOwner().getGame().getDesk()).counRaise(place.getPlayer());
            }
            place.getPlaceSessionStats().countTotalBettedForLastHour(heBeted);

            getOwner().setLastStake(stake);
            getOwner().notifyAboutRaise(place);
            type = 2;
          }
          else {
            type = 1;
          }

          ownerState.setSignalType(type);
          ownerState.permit();
          ownerState.notifyAll();

          return result;
        }
      }

      return false;
    }
  }

  private class StatesFactory
  {
    private StatesFactory()
    {
    }

    public StakeActionState getState()
    {
      StakeActionState state = null;
      int limitType = getOwner().getGame().getDesk().getLimitType();
      switch (limitType) {
      case 1:
        state = new RaiseStakeAction.LimitState(RaiseStakeAction.this, null);
        break;
      case 2:
        state = new RaiseStakeAction.NoLimitState(RaiseStakeAction.this, null);
        break;
      case 3:
        state = new RaiseStakeAction.PotLimitState(RaiseStakeAction.this, null);
        break;
      default:
        throw new RuntimeException("Error: bad limit type");
      }

      return state;
    }
  }
}