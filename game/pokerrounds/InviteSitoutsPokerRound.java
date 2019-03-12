package game.pokerrounds;

import game.Desk;
import game.Game;
import game.Place;
import game.PlacesList;
import game.Stake;
import game.stakeactions.AnteStakeAction;
import java.math.BigDecimal;
import java.util.ArrayList;

public class InviteSitoutsPokerRound extends StakesPokerRound
{
  public void complited()
  {
    getGame().setAsSitoutsInvited();
  }

  public void init()
  {
    setActionCode(40);
    if (getGame().getDesk().getTournamentID() != 0)
      setNeedAnte(new BigDecimal(0));
    else {
      setNeedAnte(getGame().getDesk().getMinBet());
    }
    setStartAfterPlaceNumber(getGame().getBigBlindPlaceNumber());
    setRoundEndPlaceNumber(getGame().getDealerPlaceNumber());
  }

  public void run()
  {
    ArrayList localArrayList = getGame().getOnBlindsSittingOutPlaces();

    int i = 1;
    int j = 0;
    while (true)
    {
      if (isNeedEndGame()) {
        getGame().checkAllInPrizeAmount();
        endGame();
        return;
      }

      if (i != 0) {
        j = getStartAfterPlaceNumber();
        i = 0;
      }
      else {
        j = getCurrentPlaceNumber();
      }

      Place localPlace = null;

      int k = 0;
      while (k < getGame().getDesk().getPlacesList().size()) {
        localPlace = getGame().getPlacesList().getNextPlace(j, true);
        if (localPlace == null) {
          getGame().checkAllInPrizeAmount();
          endGame();
          return;
        }

        if (localPlace.getNumber() == getRoundEndPlaceNumber())
        {
          break;
        }
        if (localPlace.getAmount().compareTo(getGame().getDesk().getMinBet().multiply(new BigDecimal(2)).setScale(2, 5)) >= 0) {
          break;
        }
        j = localPlace.getNumber();
        getGame().processAndCheckSitOutPlace(localPlace);
        k++;
      }

      if (k >= getGame().getDesk().getPlacesList().size())
      {
        break;
      }

      setCurrentPlaceNumber(localPlace.getNumber());

      if (localPlace.getNumber() == getRoundEndPlaceNumber())
      {
        break;
      }
      if ((localPlace.isBusy()) && (localPlace.isSittingOut()) && (!localArrayList.contains(localPlace)) && (localPlace.getAmount().floatValue() > getNeedAnte().floatValue())) {
        setCurrentPlaceNumber(localPlace.getNumber());

        int m = getGame().getDesk().getTournamentID();
        if (m > 0)
          getGame().acceptStake(new Stake(6, localPlace));
        else {
          processPlaceAction(localPlace);
        }

      }

    }

    complited();
  }

  public boolean acceptStake(Stake paramStake)
  {
    if (paramStake.getType() == 6) {
      return new AnteStakeAction(paramStake, this).execute();
    }

    return false;
  }
}