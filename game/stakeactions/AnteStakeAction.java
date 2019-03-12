package game.stakeactions;

import game.ExecutionState;
import game.Game;
import game.Place;
import game.Player;
import game.Stake;
import game.messages.CommonStateMessagesList;
import game.pokerrounds.StakesPokerRound;
import java.math.BigDecimal;

public class AnteStakeAction extends StakeAction
{
  public AnteStakeAction(Stake stake, StakesPokerRound owner)
  {
    super(stake, owner);
  }

  public boolean execute()
  {
    Place place = getStake().getPlace();
    if (place.getNumber() == getOwner().getCurrentPlaceNumber()) {
      boolean isOk = false;
      ExecutionState ownerState = getOwner().getExecutionState();
      synchronized (ownerState) {
        BigDecimal needAmount = getOwner().getNeedAnte();
        synchronized (place) {
          if (needAmount.floatValue() < place.getAmount().floatValue()) {
            place.setStakingAmount(needAmount);
            place.decAmount(needAmount);
            place.unmarkAsSittingOut();

            isOk = true;
          }
        }

        getOwner().getGame().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), 40, place.getNumber(), 2, getOwner().getNeedAnte());

        ownerState.setSignalType(2);
        ownerState.permit();
        ownerState.notifyAll();
      }

      if (isOk) {
        return true;
      }
    }
    return false;
  }
}