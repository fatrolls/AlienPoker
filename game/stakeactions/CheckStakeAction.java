package game.stakeactions;

import game.ExecutionState;
import game.Game;
import game.Place;
import game.Player;
import game.Stake;
import game.messages.CommonStateMessagesList;
import game.pokerrounds.StakesPokerRound;
import game.stats.StatsCounter;

public class CheckStakeAction extends StakeAction
{
  public CheckStakeAction(Stake stake, StakesPokerRound owner)
  {
    super(stake, owner);
  }

  public boolean execute()
  {
    Place place = stake.getPlace();
    if ((place.getNumber() == getOwner().getCurrentPlaceNumber()) && (getOwner().isCanCheck())) {
      ExecutionState ownerState = getOwner().getExecutionState();
      synchronized (ownerState) {
        getOwner().getGame().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), 18, place.getNumber(), 2);

        getOwner().setLastStake(stake);

        ownerState.setSignalType(2);
        ownerState.permit();
        ownerState.notifyAll();

        new StatsCounter(getOwner().getGame().getDesk()).counCheck(place.getPlayer());
      }

      return true;
    }

    return false;
  }
}