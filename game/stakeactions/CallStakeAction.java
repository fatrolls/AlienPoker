package game.stakeactions;

import defaultvalues.DefaultValue;
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

public class CallStakeAction extends StakeAction
{
  public CallStakeAction(Stake stake, StakesPokerRound owner)
  {
    super(stake, owner);
  }

  public boolean execute()
  {
    Place place = getStake().getPlace();
    BigDecimal heBeted = DefaultValue.ZERO_BIDECIMAL;
    if ((getOwner().getCurrentPlaceNumber() == place.getNumber()) && (getOwner().getNeedCall().floatValue() != 0.0F)) {
      ExecutionState ownerState = getOwner().getExecutionState();

      int type = 2;
      boolean result = true;
      synchronized (ownerState) {
        if (place.getAmount().floatValue() > getOwner().getNeedCall().floatValue()) {
          synchronized (place) {
            BigDecimal old = place.getStakingAmount();
            place.setStakingAmount(old.add(getOwner().getNeedCall()).setScale(2, 5));
            place.decAmount(getOwner().getNeedCall());
            heBeted = getOwner().getNeedCall();
          }

          getOwner().getGame().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), 16, place.getNumber(), 2, getOwner().getNeedCall());
          result = true;
        }
        else if (getOwner().isAllIn()) {
          BigDecimal availableAmount = new BigDecimal(0);
          synchronized (place) {
            BigDecimal old = place.getStakingAmount();
            availableAmount = place.getAmount();
            heBeted = place.getAmount();
            place.setStakingAmount(old.add(availableAmount).setScale(2, 5));
            place.setAmount(new BigDecimal(0));

            place.markAsAllIn(old.add(availableAmount).setScale(2, 5));
            getOwner().getGame().notifyAboutAllIn();
            getOwner().getGame().getCurrentRound().notifyAboutAllIn(place);
          }

          CommonMessage msg = new CommonMessage(place.getPlayer().getLogin(), 16, place.getNumber(), 2, availableAmount, true);
          getOwner().getGame().getPublicStateMessagesList().addMessage(msg);
          getOwner().getGame().notifyAboutAllIn();

          result = true;
        }
        else
        {
          result = false;
        }

        if (result)
        {
          new StatsCounter(getOwner().getGame().getDesk()).counCall(place.getPlayer());
          place.getPlaceSessionStats().countTotalBettedForLastHour(heBeted);

          getOwner().setLastStake(stake);
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