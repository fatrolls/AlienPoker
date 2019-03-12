package game.stakeactions;

import game.Desk;
import game.ExecutionState;
import game.Game;
import game.Place;
import game.PlacesList;
import game.Player;
import game.Stake;
import game.messages.CommonStateMessagesList;
import game.pokerrounds.PokerRound;
import game.pokerrounds.StakesPokerRound;
import java.math.BigDecimal;
import java.util.Iterator;

public class SmallBlindStakeAction extends StakeAction
{
  public SmallBlindStakeAction(Stake stake, StakesPokerRound owner)
  {
    super(stake, owner);
  }

  public boolean execute() {
    StakeActionState state = new StatesFactory(null).getState();
    return state.execute();
  }

  public static BigDecimal changeBigSmallBlindAmmount(Place place, BigDecimal needAmount) {
    if (needAmount.floatValue() >= place.getAmount().floatValue()) {
      needAmount = place.getAmount();
    }
    return needAmount;
  }

  public static boolean ifBigSmallBlindAllIn(Place place, BigDecimal needAmount, StakesPokerRound pokerRound) {
    if (needAmount.floatValue() == place.getAmount().floatValue()) {
      BigDecimal oldStakingAmount = place.getStakingAmount();
      place.setAmount(new BigDecimal(0));
      place.markAsAllIn(oldStakingAmount.add(needAmount).setScale(2, 5));
      pokerRound.getGame().notifyAboutAllIn();
      pokerRound.getGame().getCurrentRound().notifyAboutAllIn(place);

      Iterator iter = pokerRound.getGame().getPlacesList().iterator();
      BigDecimal totalBet = new BigDecimal(0);
      while (iter.hasNext()) {
        Place p = (Place)iter.next();
        if (p.getPlayer() != null) {
          totalBet = totalBet.add(needAmount).setScale(2, 5);
        }
      }
      place.setAllInPretendedAmount(pokerRound.getGame().getGameAmount().add(totalBet).setScale(2, 5));
      return true;
    }
    return false;
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
              getOwner().getGame().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), 4, place.getNumber(), 2, needAmount);

              ownerState.setSignalType(2);
              ownerState.permit();
              ownerState.notifyAll();
            }
          }
          return true;
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
              getOwner().getGame().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), 4, place.getNumber(), 2, needAmount);

              ownerState.setSignalType(2);
              ownerState.permit();
              ownerState.notifyAll();
            }
          }
          return true;
        }
      }

      return false;
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
      Place place = getStake().getPlace();
      if (place == null) return false;

      if (place.getNumber() == getOwner().getCurrentPlaceNumber()) {
        BigDecimal needAmount = getOwner().getGame().getDesk().getMinBet().divide(new BigDecimal(2), 2, 5);

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
              getOwner().getGame().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), 4, place.getNumber(), 2, needAmount);

              ownerState.setSignalType(2);
              ownerState.permit();
              ownerState.notifyAll();
            }
          }
          return true;
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
        state = new SmallBlindStakeAction.LimitState(SmallBlindStakeAction.this, null);
        break;
      case 2:
        state = new SmallBlindStakeAction.NoLimitState(SmallBlindStakeAction.this, null);
        break;
      case 3:
        state = new SmallBlindStakeAction.PotLimitState(SmallBlindStakeAction.this, null);
        break;
      default:
        throw new RuntimeException("Error: bad limit type");
      }

      return state;
    }
  }
}