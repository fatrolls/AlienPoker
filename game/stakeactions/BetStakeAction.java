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

public class BetStakeAction extends StakeAction
{
  public BetStakeAction(Stake stake, StakesPokerRound owner)
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

      if ((getOwner().getCurrentPlaceNumber() == place.getNumber()) && (getOwner().getNeedBet().floatValue() != 0.0F)) {
        ExecutionState ownerState = getOwner().getExecutionState();
        synchronized (ownerState) {
          if ((!stake.isAllIn()) && (place.getAmount().floatValue() > stake.getAmount().floatValue()) && (stake.getAmount().floatValue() >= getOwner().getNeedBet().floatValue()) && (stake.getAmount().floatValue() <= getOwner().getGame().getBetsAndGameAmount().add(getOwner().getNeedCall().multiply(new BigDecimal(2)).setScale(2, 5)).floatValue()))
          {
            synchronized (place) {
              BigDecimal oldStakingAmount = place.getStakingAmount();
              place.setStakingAmount(oldStakingAmount.add(stake.getAmount()).setScale(2, 5));
              place.decAmount(stake.getAmount());
            }

            getOwner().getGame().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), 19, place.getNumber(), 2, stake.getAmount());
            result = true;
          }
          else if ((stake.isAllIn()) && (stake.getAmount().compareTo(place.getAmount()) == 0) && (stake.getAmount().floatValue() > 0.0F))
          {
            synchronized (place)
            {
              BigDecimal oldStakingAmount = place.getStakingAmount();
              place.setStakingAmount(oldStakingAmount.add(stake.getAmount()).setScale(2, 5));
              place.setAmount(new BigDecimal(0));

              place.markAsAllIn(oldStakingAmount.add(stake.getAmount()).setScale(2, 5));
              getOwner().getGame().notifyAboutAllIn();
              getOwner().getGame().getCurrentRound().notifyAboutAllIn(place);
            }

            CommonMessage msg = new CommonMessage(place.getPlayer().getLogin(), 19, place.getNumber(), 2, stake.getAmount(), true);
            getOwner().getGame().getPublicStateMessagesList().addMessage(msg);
            getOwner().getGame().notifyAboutAllIn();

            result = true;
          }

          int type = 2;
          if (result) {
            new StatsCounter(getOwner().getGame().getDesk()).counBet(place.getPlayer());
            place.getPlaceSessionStats().countTotalBettedForLastHour(stake.getAmount());

            getOwner().setLastStake(stake);
            getOwner().notifyAboutBet(place);
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
      BigDecimal heBetted = DefaultValue.ZERO_BIDECIMAL;

      if ((getOwner().getCurrentPlaceNumber() == place.getNumber()) && (getOwner().getNeedBet().floatValue() != 0.0F)) {
        ExecutionState ownerState = getOwner().getExecutionState();
        synchronized (ownerState) {
          if ((!stake.isAllIn()) && (place.getAmount().floatValue() > stake.getAmount().floatValue()) && (stake.getAmount().floatValue() >= getOwner().getNeedBet().floatValue()))
          {
            synchronized (place) {
              BigDecimal oldStakingAmount = place.getStakingAmount();
              place.setStakingAmount(oldStakingAmount.add(stake.getAmount()).setScale(2, 5));
              place.decAmount(stake.getAmount());
            }

            getOwner().getGame().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), 19, place.getNumber(), 2, stake.getAmount());
            result = true;
          }
          else if ((stake.isAllIn()) && (stake.getAmount().compareTo(place.getAmount()) == 0) && (stake.getAmount().floatValue() > 0.0F)) {
            synchronized (place)
            {
              BigDecimal oldStakingAmount = place.getStakingAmount();
              place.setStakingAmount(oldStakingAmount.add(stake.getAmount()).setScale(2, 5));
              place.setAmount(new BigDecimal(0));

              place.markAsAllIn(oldStakingAmount.add(stake.getAmount()).setScale(2, 5));
              getOwner().getGame().notifyAboutAllIn();
              getOwner().getGame().getCurrentRound().notifyAboutAllIn(place);
            }

            CommonMessage msg = new CommonMessage(place.getPlayer().getLogin(), 19, place.getNumber(), 2, stake.getAmount(), true);
            getOwner().getGame().getPublicStateMessagesList().addMessage(msg);
            getOwner().getGame().notifyAboutAllIn();

            result = true;
          }

          int type = 2;
          if (result) {
            new StatsCounter(getOwner().getGame().getDesk()).counBet(place.getPlayer());
            place.getPlaceSessionStats().countTotalBettedForLastHour(stake.getAmount());

            getOwner().setLastStake(stake);
            getOwner().notifyAboutBet(place);
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

      BigDecimal heBetted = DefaultValue.ZERO_BIDECIMAL;

      if ((getOwner().getCurrentPlaceNumber() == place.getNumber()) && (getOwner().getNeedBet().floatValue() != 0.0F)) {
        ExecutionState ownerState = getOwner().getExecutionState();

        int type = 2;
        boolean result = true;
        synchronized (ownerState) {
          if (place.getAmount().floatValue() > getOwner().getNeedBet().floatValue()) {
            synchronized (place) {
              BigDecimal old = place.getStakingAmount();
              place.setStakingAmount(old.add(getOwner().getNeedBet()).setScale(2, 5));
              place.decAmount(getOwner().getNeedBet());
              heBetted = getOwner().getNeedBet();
            }

            getOwner().getGame().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), 19, place.getNumber(), 2, getOwner().getNeedBet());
            result = true;
          }
          else if (getOwner().isAllIn()) {
            BigDecimal availableAmount = DefaultValue.ZERO_BIDECIMAL;
            synchronized (place) {
              availableAmount = place.getAmount();
              heBetted = availableAmount;

              BigDecimal old = place.getStakingAmount();
              place.setStakingAmount(old.add(availableAmount).setScale(2, 5));
              place.setAmount(new BigDecimal(0));

              place.markAsAllIn(old.add(availableAmount).setScale(2, 5));
              getOwner().getGame().notifyAboutAllIn();
              getOwner().getGame().getCurrentRound().notifyAboutAllIn(place);
            }

            CommonMessage msg = new CommonMessage(place.getPlayer().getLogin(), 19, place.getNumber(), 2, availableAmount, true);
            getOwner().getGame().getPublicStateMessagesList().addMessage(msg);
            getOwner().getGame().notifyAboutAllIn();

            result = true;
          }
          else {
            result = false;
          }

          if (result)
          {
            new StatsCounter(getOwner().getGame().getDesk()).counBet(place.getPlayer());
            place.getPlaceSessionStats().countTotalBettedForLastHour(heBetted);

            getOwner().setLastStake(stake);
            getOwner().notifyAboutBet(place);

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
        state = new BetStakeAction.LimitState(BetStakeAction.this, null);
        break;
      case 2:
        state = new BetStakeAction.NoLimitState(BetStakeAction.this, null);
        break;
      case 3:
        state = new BetStakeAction.PotLimitState(BetStakeAction.this, null);
        break;
      default:
        throw new RuntimeException("Error: bad limit type");
      }

      return state;
    }
  }
}