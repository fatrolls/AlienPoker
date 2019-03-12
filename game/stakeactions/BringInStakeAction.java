package game.stakeactions;

import game.ExecutionState;
import game.Game;
import game.Place;
import game.Player;
import game.Stake;
import game.messages.CommonStateMessagesList;
import game.pokerrounds.StakesPokerRound;
import java.math.BigDecimal;

public class BringInStakeAction extends StakeAction
{
  public BringInStakeAction(Stake stake, StakesPokerRound owner)
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
        BigDecimal needBringIn = getOwner().getNeedBringIn();
        BigDecimal stakeAmount = needBringIn;
        synchronized (place)
        {
          if ((needBringIn.floatValue() > 0.0F) && (stakeAmount.floatValue() < place.getAmount().floatValue())) {
            place.setStakingAmount(stakeAmount);
            place.decAmount(stakeAmount);

            isOk = true;
          }
        }

        getOwner().getGame().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), 21, place.getNumber(), 2, stakeAmount);

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