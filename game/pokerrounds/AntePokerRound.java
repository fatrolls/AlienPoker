package game.pokerrounds;

import game.Desk;
import game.Game;
import game.Place;
import game.PlacesList;
import game.Stake;
import game.stakeactions.AnteStakeAction;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Iterator;

public class AntePokerRound extends StakesPokerRound
{
  public void complited()
  {
    getGame().setAsAnteCompleted();
  }

  public void init() {
    setActionCode(40);

    setNeedAnte(getGame().getDesk().getAnte());
    System.out.println("Ante is: " + getGame().getDesk().getAnte().floatValue());

    setStartAfterPlaceNumber(0);

    int last = 0;
    synchronized (getGame().getPlacesList()) {
      Iterator iter = getGame().getPlacesList().allPlacesIterator();
      while (iter.hasNext()) {
        Place place = (Place)iter.next();
        if (place.isBusy()) {
          last = place.getNumber();
        }
      }
    }
    setRoundEndPlaceNumber(last);
  }

  public void run()
  {
    boolean first = true;
    int from = 0;
    while (true)
    {
      if (getGame().getPlacesList().getPlayersCount() < getGame().getMinPlayers()) {
        getGame().checkAllInPrizeAmount();
        endGame();
        return;
      }

      if (first) {
        from = getStartAfterPlaceNumber();
        first = false;
      } else {
        from = getCurrentPlaceNumber();
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

        if ((place.getNumber() == getRoundEndPlaceNumber()) || (getRoundEndPlaceNumber() == 0))
        {
          break;
        }
        if (place.getAmount().compareTo(getGame().getDesk().getMinBet().multiply(new BigDecimal(2)).setScale(2, 5)) >= 0) {
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

      setCurrentPlaceNumber(place.getNumber());

      boolean isRoundEnd = (place.getNumber() == getRoundEndPlaceNumber()) || (getRoundEndPlaceNumber() == 0);

      if ((place.isBusy()) && (place.getAmount().floatValue() > getNeedAnte().floatValue()))
      {
        setCurrentPlaceNumber(place.getNumber());

        int tourId = getGame().getDesk().getTournamentID();
        if (tourId > 0) {
          getGame().acceptStake(new Stake(6, place));
        }
        else {
          processPlaceAction(place);

          getGame().processAndCheckSitOutPlace(place);
        }

      }

      if (isRoundEnd)
      {
        break;
      }
    }
    if (isNeedEndGame()) {
      endGame();
      return;
    }

    complited();
  }

  public boolean acceptStake(Stake stake) {
    if (stake.getType() == 6) {
      return new AnteStakeAction(stake, this).execute();
    }

    return false;
  }
}