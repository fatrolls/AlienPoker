package game;

import game.cards.CardsSet;
import game.cards.Combination;
import game.colorflop.ColorFlop;
import game.gameresults.GameResult;
import game.messages.CommonStateMessagesList;
import game.messages.PrivateStateMessagesList;
import game.pokerrounds.PokerRound;
import game.stats.PlaceSessionStats;
import game.stats.StatsCounter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.log4j.Logger;
import server.Server;
import server.Server.TotalStats;
import utils.xml.XMLTag;

public class TexasHoldem extends Game
{
  public boolean twoСards = false;

  public boolean canContinue()
  {
    if (getPlacesList().getActivePlayersCount() >= getMinPlayers()) {
      return true;
    }
    return getPlacesList().getPlayersCount() >= getMinPlayers();
  }

  public int getDealerPlaceNumber()
  {
    return getPlacesList().currentDealerPlace();
  }

  public int getPlayerGamePlace(Player paramPlayer)
  {
    Place localPlace = getPlacesList().getPlace(paramPlayer);
    if (localPlace.isActive()) {
      return 1;
    }
    if (localPlace.isFold()) {
      return 2;
    }
    return 0;
  }

  public String getOwnCardsXML(Player paramPlayer)
  {
    Place localPlace = getPlacesList().getPlace(paramPlayer);
    if (localPlace != null) {
      XMLTag localXMLTag = localPlace.getCardsXMLTag();

      String str = localXMLTag.toString();
      localXMLTag.invalidate();

      return str;
    }

    return null;
  }

  public String getPlayerCombination(Player paramPlayer) {
    Place localPlace = getPlacesList().getPlace(paramPlayer);
    if (localPlace != null) {
      CardsSet localCardsSet = new CardsSet();
      localCardsSet.setGameType(1);

      localCardsSet.addCommonCardsFromArray(getCommonCards());
      localCardsSet.addOwnCardsFromArray(localPlace.getCards());

      if (localCardsSet.getCardsCount() >= 2) {
        Combination localCombination = localCardsSet.getCost();
        return localCombination.toXML();
      }
    }

    return null;
  }

  public boolean canStart()
  {
    return (!started) && (!isAlive()) && (getPlacesList().getActivePlayersCount() >= getMinPlayers());
  }

  protected void reset(long paramLong)
  {
    flushRakes(paramLong);

    if (cardsPack != null) {
      cardsPack.invalidate();
      cardsPack = null;
    }

    commonCards.clear();
    blindsSittingOuts.clear();

    gameState = 0;
    gameAmount = new BigDecimal(0);

    smallBlindPlaceNumber = 0;
    bigBlindPlaceNumber = 0;
    currentRound = null;
    hasAllIn = false;

    newHand = false;
    smallBlind = false;
    bigBlind = false;
    sitoutsInvited = false;
    twoСards = false;
    preFlop = false;
    flop = false;
    preTurn = false;
    turn = false;
    preRiver = false;
    river = false;
    afterRiver = false;
    ended = false;
    tournamentGameEnded = false;

    getPlacesList().prepareToNewHand();

    if (canContinue()) {
      updateState();
    } else {
      if (getDesk().getTournamentID() == 0) {
        getDesk().getPublicStateMessagesList().addCommonMessage(70);
        initiateGameDeskClearing();
      }
      gameState = 70;
    }
  }

  public void end(long paramLong)
  {
    if (!flop) {
      desk.getColorFlop().processFlopNotReached();
    }

    gameResult = new GameResult(this);
    gameResult.setGameAmount(getTrueGameAmount());

    Iterator localIterator = getPlacesList().iterator();
    Object localObject2;
    Object localObject3;
    while (localIterator.hasNext())
    {
      localObject1 = (Place)localIterator.next();
      if ((((Place)localObject1).isActive()) || (((Place)localObject1).isAllIn()))
      {
        ((Place)localObject1).getPlaceSessionStats().countAveragePotForLastHour(getGameAmount());
        localObject2 = new CardsSet();
        ((CardsSet)localObject2).setGameType(1);
        ((CardsSet)localObject2).addCommonCardsFromArray(getCommonCards());
        ((CardsSet)localObject2).addOwnCardsFromArray(((Place)localObject1).getCards());
        localObject3 = ((CardsSet)localObject2).getCost();
        gameResult.addPlaceAndCombination((Place)localObject1, (Combination)localObject3);
      }
      else if (((Place)localObject1).isFold())
      {
        gameResult.addLoser((Place)localObject1);
      }

    }

    gameResult.determineWinners();

    gameResult.cacheGameResult();

    logGameResult();

    ended = true;
    getPublicStateMessagesList().addCommonMessage(0);

    Object localObject1 = getPlacesList().allPlacesIterator();
    while (((Iterator)localObject1).hasNext()) {
      localObject2 = (Place)((Iterator)localObject1).next();
      ((Place)localObject2).setStakingAmountCache(new BigDecimal(0));
    }

    synchronized (getDesk().getStats()) {
      getDesk().getStats().addHandPot(getGameAmount(), flop);
    }
    try
    {
      log.debug("TexasHoldem.BEFORE_RESTART_PAUSE gameState=" + gameState);

      sleep(20000L);
    }
    catch (InterruptedException localObject2) {
      throw new RuntimeException((Throwable)???);
    }

    notifyTournamentAboutGameEnd();
    updatePlayerAmountStats();

    synchronized (Server.getTotalStats()) {
      localObject3 = Server.getTotalStats().getMaxPot();
      if (((BigDecimal)localObject3).compareTo(gameAmount) < 0) {
        Server.getTotalStats().setMaxPot(gameAmount.setScale(2, 5));
      }
    }

    reset(paramLong);
  }

  public void run()
  {
    int i = 0;
    while (true)
    {
      synchronized (execState) {
        if (execState.canExec()) continue;
        try {
          execState.wait();
          getDesk().processLeaveDeskQuery();
        }
        catch (InterruptedException localInterruptedException) {
          throw new RuntimeException(localInterruptedException);
        }

        execState.forbid();

        getDesk().processLeaveDeskQuery();

        if (i == 0) {
          sleepUntillTournamentBegins();
          i = 1;
        }

        if (!started) {
          startAttentions();
        } else if ((getDesk().getTournamentID() != 0) && (!tournamentOneCard)) {
          tournamentOneCard();
        } else if (!newHand) {
          if (getDesk().getTournamentID() != 0)
            recordPlayersAmount();
          updatePlayerAmountStats();
          newHand();
          recordPlayerAmounts();
        } else if (!smallBlind) {
          smallBlind();
          updatePlayerAmountStats();
          recordPlayerAmounts();
        } else if (!bigBlind) {
          bigBlind();
          updatePlayerAmountStats();
          recordPlayerAmounts();
        } else if (!sitoutsInvited) {
          inviteSitouts();
          updatePlayerAmountStats();
          recordPlayerAmounts();
        } else if (!twoСards) {
          new StatsCounter(getDesk()).countSessionGames();
          twoCards();
          recordPlayerAmounts();
        } else if (!preFlop) {
          preFlop();
          updatePlayerAmountStats();
          recordPlayerAmounts();
        } else if (!flop) {
          flop();
          recordPlayerAmounts();
        } else if (!preTurn) {
          preTurn();
          updatePlayerAmountStats();
          recordPlayerAmounts();
        } else if (!turn) {
          turn();
          recordPlayerAmounts();
        } else if (!preRiver) {
          preRiver();
          updatePlayerAmountStats();
          recordPlayerAmounts();
        } else if (!river) {
          river();
          recordPlayerAmounts();
        } else if (!afterRiver) {
          afterRiver();
          updatePlayerAmountStats();
          recordPlayerAmounts();
        } else if (!ended) {
          countFlopPercent();
          end(getCurrentGameId());
          recordPlayerAmounts();
        }

        if ((currentRound == null) || 
          (!currentRound.hasAllInPlaces())) continue;
        currentRound.checkAllInPrizeAmount();
      }
    }
  }

  public void updateGameAmount()
  {
    Iterator localIterator = getPlacesList().allPlacesIterator();
    while (localIterator.hasNext()) {
      Place localPlace = (Place)localIterator.next();
      incGameAmount(localPlace.getStakingAmount());
      localPlace.setStakingAmount(new BigDecimal(0));
    }
  }

  public void twoCards() {
    Iterator localIterator = getPlacesList().iterator();
    while (localIterator.hasNext()) {
      Place localPlace = (Place)localIterator.next();

      localPlace.addCard(cardsPack.getNextCard());
      localPlace.addCard(cardsPack.getNextCard());

      localPlace.getStateMessagesList().addPrivateMessage(6);
    }

    getPublicStateMessagesList().addCommonMessage(6);
    twoСards = true;
    updateState();
  }

  public int getLastStateCode()
  {
    int i = 0;

    if (gameState == 70) {
      return 70;
    }

    if (!newHand)
      i = 2;
    else if (!twoСards)
      i = 3;
    else if (!flop)
      i = 6;
    else if (!turn)
      i = 7;
    else if (!river)
      i = 8;
    else if (!ended)
      i = 9;
    else {
      i = 0;
    }

    return i;
  }

  public PlacesList getPlacesList()
  {
    return getDesk().getPlacesList();
  }
}