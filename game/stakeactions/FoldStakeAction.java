package game.stakeactions;

import game.ExecutionState;
import game.Game;
import game.Place;
import game.Player;
import game.Stake;
import game.messages.CommonStateMessagesList;
import game.pokerrounds.PokerRound;
import game.pokerrounds.StakesPokerRound;
import game.stats.StatsCounter;

public class FoldStakeAction extends StakeAction
{
  public FoldStakeAction(Stake stake, StakesPokerRound owner)
  {
    super(stake, owner);
  }

  public boolean execute() {
    Place place = getStake().getPlace();
    if ((place.getNumber() == getOwner().getCurrentPlaceNumber()) || (place.isCanPostCards())) {
      ExecutionState ownerState = getOwner().getExecutionState();
      synchronized (ownerState) {
        synchronized (place) {
          place.markAsFold();
          PokerRound pokerRound = getOwner().getGame().getCurrentRound();
          if (pokerRound != null) {
            pokerRound.notifyAboutFold(place);
          }
          place.setCanPostCards(false);
        }

        getOwner().setLastStake(getStake());
        getOwner().getGame().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), 20, place.getNumber(), 2);

        new StatsCounter(getOwner().getGame().getDesk()).countFolds(place);
        new StatsCounter(getOwner().getGame().getDesk()).countFlopSeens(place.getPlayer());
        new StatsCounter(getOwner().getGame().getDesk()).countFourthStreetSeens(place.getPlayer());

        ownerState.setSignalType(2);
        ownerState.permit();
        ownerState.notifyAll();
      }

      return true;
    }

    return false;
  }
}