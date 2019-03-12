package game.stakeactions;

import game.Desk;
import game.ExecutionState;
import game.Game;
import game.Place;
import game.Player;
import game.Stake;
import game.messages.CommonStateMessagesList;
import game.pokerrounds.StakesPokerRound;
import java.math.BigDecimal;

public class BigBlindStakeAction extends StakeAction
{
  public BigBlindStakeAction(Stake stake, StakesPokerRound owner)
  {
    super(stake, owner);
  }

  public boolean execute() {
    StakeActionState state = new StatesFactory(null).getState();
    return state.execute();
  }

  private class LimitState
    implements StakeActionState
  {
    private LimitState()
    {
    }

    public boolean execute()
    {
      Place place = getStake().getPlace();
      if (place == null) return false;

      if (place.getNumber() == getOwner().getCurrentPlaceNumber()) {
        BigDecimal needAmount = getOwner().getGame().getDesk().getMinBet();

        needAmount = SmallBlindStakeAction.changeBigSmallBlindAmmount(place, needAmount);

        synchronized (place)
        {
          if (needAmount.floatValue() <= place.getAmount().floatValue()) {
            ExecutionState ownerState = getOwner().getExecutionState();
            synchronized (ownerState) {
              if (!SmallBlindStakeAction.ifBigSmallBlindAllIn(place, needAmount, getOwner())) {
                place.decAmount(needAmount);
              }
              BigDecimal old = place.getStakingAmount();
              place.setStakingAmount(old.add(needAmount).setScale(2, 5));
              place.unmarkAsSittingOut();

              getOwner().setLastStake(stake);
              getOwner().getGame().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), 5, place.getNumber(), 2, needAmount);

              ownerState.setSignalType(2);
              ownerState.permit();
              ownerState.notifyAll();
            }
            return true;
          }
        }
      }

      return false;
    }
  }

  private class PotLimitState
    implements StakeActionState
  {
    private PotLimitState()
    {
    }

    public boolean execute()
    {
      Place place = getStake().getPlace();
      if (place == null) return false;

      if (place.getNumber() == getOwner().getCurrentPlaceNumber()) {
        BigDecimal needAmount = getOwner().getGame().getDesk().getMaxBet();

        needAmount = SmallBlindStakeAction.changeBigSmallBlindAmmount(place, needAmount);

        synchronized (place)
        {
          if (needAmount.floatValue() <= place.getAmount().floatValue()) {
            ExecutionState ownerState = getOwner().getExecutionState();
            synchronized (ownerState) {
              if (!SmallBlindStakeAction.ifBigSmallBlindAllIn(place, needAmount, getOwner())) {
                place.decAmount(needAmount);
              }
              BigDecimal old = place.getStakingAmount();
              place.setStakingAmount(old.add(needAmount).setScale(2, 5));
              place.unmarkAsSittingOut();

              getOwner().setLastStake(stake);
              getOwner().getGame().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), 5, place.getNumber(), 2, needAmount);

              ownerState.setSignalType(2);
              ownerState.permit();
              ownerState.notifyAll();
            }
            return true;
          }
        }
      }
      return false;
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
      Place place = getStake().getPlace();
      if (place == null) return false;

      if (place.getNumber() == getOwner().getCurrentPlaceNumber()) {
        BigDecimal needAmount = getOwner().getGame().getDesk().getMaxBet();

        needAmount = SmallBlindStakeAction.changeBigSmallBlindAmmount(place, needAmount);

        synchronized (place)
        {
          if (needAmount.floatValue() <= place.getAmount().floatValue()) {
            ExecutionState ownerState = getOwner().getExecutionState();
            synchronized (ownerState) {
              if (!SmallBlindStakeAction.ifBigSmallBlindAllIn(place, needAmount, getOwner())) {
                place.decAmount(needAmount);
              }
              BigDecimal old = place.getStakingAmount();
              place.setStakingAmount(old.add(needAmount).setScale(2, 5));
              place.unmarkAsSittingOut();

              getOwner().setLastStake(stake);
              getOwner().getGame().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), 5, place.getNumber(), 2, needAmount);

              ownerState.setSignalType(2);
              ownerState.permit();
              ownerState.notifyAll();
            }
            return true;
          }
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
        state = new BigBlindStakeAction.LimitState(BigBlindStakeAction.this, null);
        break;
      case 2:
        state = new BigBlindStakeAction.NoLimitState(BigBlindStakeAction.this, null);
        break;
      case 3:
        state = new BigBlindStakeAction.PotLimitState(BigBlindStakeAction.this, null);
        break;
      default:
        throw new RuntimeException("Error: bad limit type");
      }

      return state;
    }
  }
}