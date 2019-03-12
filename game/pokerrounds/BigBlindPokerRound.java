package game.pokerrounds;

import game.Desk;
import game.Game;
import game.Place;
import game.PlacesList;
import game.Stake;
import game.stakeactions.BigBlindStakeAction;
import game.stakeactions.StakeAction;
import java.math.BigDecimal;

public class BigBlindPokerRound extends StakesPokerRound
{
  public void complited()
  {
    getGame().setAsBigBlind();
  }

  public void init() {
    setStartAfterPlaceNumber(getGame().getSmallBlindPlaceNumber());
    setActionCode(5);
  }

  public void run() {
    int from = getStartAfterPlaceNumber();
    while (true) {
      if (getGame().getPlacesList().getPlayersCount() < getGame().getMinPlayers()) {
        getGame().checkAllInPrizeAmount();
        endGame();
        return;
      }

      Place place = null;

      int i = 0;
      while (i < getGame().getDesk().getPlacesList().size()) {
        place = getGame().getPlacesList().getNextPlace(from, true);
        if (place == null) {
          getGame().checkAllInPrizeAmount();
          endGame();
          return;
        }

        if (place.getNumber() == getGame().getSmallBlindPlaceNumber())
        {
          break;
        }

        if (place.getAmount().compareTo(new BigDecimal(0)) > 0) {
          break;
        }
        from = place.getNumber();
        getGame().processAndCheckSitOutPlace(place);
        i++;
      }

      if (i >= getGame().getDesk().getPlacesList().size())
      {
        break;
      }

      if (place.getNumber() == getGame().getSmallBlindPlaceNumber())
      {
        break;
      }

      setCurrentPlaceNumber(place.getNumber());

      int tourId = getGame().getDesk().getTournamentID();
      if (tourId > 0)
      {
        getGame().acceptStake(new Stake(8, place));
        getGame().setBigBlindPlaceNumber(place.getNumber());
        break;
      }

      int res = processPlaceAction(place);
      getGame().processAndCheckSitOutPlace(place);
      if (res > 0) {
        getGame().setBigBlindPlaceNumber(place.getNumber());
        break;
      }if (res < 0) {
        getGame().addOnBlindSitttingOutPlace(place);
        if (getGame().getPlacesList().getActivePlayersCount() < getGame().getMinPlayers()) {
          getGame().checkAllInPrizeAmount();
          endGame();
          return;
        }
      }

      from = place.getNumber();
    }

    complited();
  }

  public boolean acceptStake(Stake stake) {
    if (8 == stake.getType()) {
      StakeAction stakeAction = new BigBlindStakeAction(stake, this);
      return stakeAction.execute();
    }

    return false;
  }
}