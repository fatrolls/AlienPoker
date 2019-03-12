package game.stakeactions;

import game.Card;
import game.ExecutionState;
import game.Game;
import game.Place;
import game.Player;
import game.Stake;
import game.messages.CommonStateMessagesList;
import game.pokerrounds.StakesPokerRound;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import utils.Log;

public class DiscardStakeAction extends StakeAction
{
  private Card card = null;

  public DiscardStakeAction(Stake stake, StakesPokerRound owner, Card card) {
    super(stake, owner);
    this.card = card;
  }

  public boolean execute() {
    Place place = getStake().getPlace();
    if ((getOwner().getCurrentPlaceNumber() == place.getNumber()) && (hasCard(card, place))) {
      ExecutionState ownerState = getOwner().getExecutionState();

      int type = 2;
      boolean result = true;
      synchronized (ownerState)
      {
        synchronized (place) {
          result = false;
          ArrayList cards = place.getCards();
          if (cards != null) {
            Iterator iter = cards.iterator();
            while ((iter.hasNext()) && (!result)) {
              Card c = (Card)iter.next();
              if (c.equals(card))
              {
                iter.remove();

                getOwner().getGame().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), 80, place.getNumber(), 2, new BigDecimal(0));
                result = true;
              }
            }
          }
          else {
            Log.out("Class Pineapple.makeDiscard : Error - cards is null!");
          }

        }

        if (result) {
          getOwner().setLastStake(stake);
          type = 2;
        } else {
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

  private boolean hasCard(Card card, Place place)
  {
    synchronized (place) {
      ArrayList cards = place.getCards();
      if (cards != null) {
        Iterator iter = cards.iterator();
        while (iter.hasNext()) {
          Card c = (Card)iter.next();
          if (c.equals(card)) {
            return true;
          }
        }
      }
    }

    return false;
  }
}