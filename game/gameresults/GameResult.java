package game.gameresults;

import game.Desk;
import game.Game;
import game.Place;
import game.PlacesList;
import game.Player;
import game.cards.Combination;
import game.stats.PlaceSessionStats;
import game.stats.StatsCounter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.log4j.Logger;
import utils.Log;
import utils.xml.XMLTag;

public class GameResult
{
  static Logger log = Logger.getLogger(GameResult.class);
  protected ArrayList winners = new ArrayList();
  protected ArrayList losers = new ArrayList();
  protected BigDecimal gameAmount = new BigDecimal(0);
  protected BigDecimal canGetAmountMax;
  protected PlacesList cacheplaces;
  protected HashMap placesCards = new HashMap();
  protected Game game;
  public static final String TAG_NAME_RESULT = "RESULT";
  public static final String TAG_NAME_WINNERS = "WINERS";
  public static final String TAG_NAME_WINNER = "WINNER";
  public static final String TAG_PARAM_NAME_COUNT = "COUNT";
  public static final String TAG_PARAM_NAME_AMOUNT = "AMOUNT";
  public static final String TAG_PARAM_NAME_PLACE = "PLACE";
  public static final String TAG_PARAM_NAME_LOGIN = "NICK";
  public static final String TAG_NAME_LOSERS = "LOSERS";
  public static final String TAG_NAME_LOSER = "LOSER";
  public static final String TAG_PARAM_LOSER_LOST_MONEY = "LOSER_LOST_MONEY";
  public static final String TAG_PARAM_LOSER_LOGIN = "NICK";
  public static final String TAG_NAME_ACCEPTED_SHOW_CARDS_PLACES = "ASC_PLACES";
  public static final String TAG_NAME_PLACE = "PLACE";
  public static final String TAG_PARAM_NAME = "NUMBER";
  private String cachedGameResultXML = "";

  public GameResult(Game paramGame) {
    game = paramGame;
    canGetAmountMax = new BigDecimal(0);
  }

  public String getCachedGameResultXML() {
    return cachedGameResultXML;
  }

  public void cacheGameResult()
  {
    StatsCounter localStatsCounter = new StatsCounter(game.getDesk());
    ArrayList localArrayList = new ArrayList(winners.size());

    XMLTag localXMLTag1 = new XMLTag("RESULT");

    XMLTag localXMLTag2 = new XMLTag("WINERS");
    localXMLTag2.addParam("COUNT", winners.size());

    Iterator localIterator = winners.iterator();
    int i = winners.size();
    Object localObject2;
    Object localObject3;
    while (localIterator.hasNext()) {
      localXMLTag3 = new XMLTag("WINNER");

      Winner localWinner = (Winner)localIterator.next();
      localXMLTag3.addParam("PLACE", localWinner.getPlace().getNumber());
      localXMLTag3.addParam("AMOUNT", localWinner.getAmount().floatValue());

      log.info("Winner:" + localWinner.getAmount());

      localObject2 = localWinner.getPlace().getPlayer();
      localWinner.getPlace().getPlaceSessionStats().countTotalWonForLastHour(localWinner.getAmount());

      if (localObject2 != null) {
        localXMLTag3.addParam("NICK", ((Player)localObject2).getLogin());

        localArrayList.add(localObject2);
      }

      if (game.hasAnotherActivePlayers(localWinner.getPlace().getNumber())) {
        localXMLTag3.addNestedTag(localWinner.getPlace().getCardsXMLTag());
        localXMLTag3.addNestedTag(localWinner.getCombination().toXMLTag());
      } else {
        localObject3 = new XMLTag("CARDS");
        localXMLTag3.addNestedTag((XMLTag)localObject3);
      }

      if (localWinner.getPlace().isSittingOut())
        i--;
      localXMLTag2.addNestedTag(localXMLTag3);
    }

    if ((!losers.isEmpty()) && (i != 0)) {
      localXMLTag3 = new XMLTag("LOSERS");
      for (int j = 0; j < losers.size(); j++) {
        localObject2 = (Loser)losers.get(j);
        localObject3 = new XMLTag("LOSER");
        ((XMLTag)localObject3).addParam("NICK", ((Loser)localObject2).getPlace().getPlayer().getLogin());
        float f = ((Loser)localObject2).getStartAmount().floatValue() - ((Loser)localObject2).getAmount().floatValue();
        ((XMLTag)localObject3).addParam("LOSER_LOST_MONEY", new Float(f).toString());
        localXMLTag3.addNestedTag((XMLTag)localObject3);
      }
      localXMLTag1.addNestedTag(localXMLTag3);
    }

    localStatsCounter.countShowDownsWon(localArrayList);
    if (game.getDesk().getPokerType() == 2) {
      localStatsCounter.countWinIfFourthStreetSeen(localArrayList);
      localStatsCounter.countActivePlayersFourthStreetSeens();
    } else {
      localStatsCounter.countWinIfFlopSeen(localArrayList);
      localStatsCounter.countActivePlayersFlopSeens();
    }
    localStatsCounter.countNoFolds();
    localStatsCounter.countGamesWon(localArrayList);

    localXMLTag1.addNestedTag(localXMLTag2);

    XMLTag localXMLTag3 = new XMLTag("ASC_PLACES");
    localXMLTag3.addParam("COUNT", placesCards.size());

    localIterator = placesCards.entrySet().iterator();
    while (localIterator.hasNext()) {
      localObject1 = (Map.Entry)localIterator.next();

      localObject2 = (Place)((Map.Entry)localObject1).getKey();
      localObject3 = (Combination)((Map.Entry)localObject1).getValue();

      XMLTag localXMLTag4 = new XMLTag("PLACE");
      localXMLTag4.addParam("PLACE", ((Place)localObject2).getNumber());

      Player localPlayer = ((Place)localObject2).getPlayer();
      if (localPlayer != null) {
        localXMLTag4.addParam("NICK", localPlayer.getLogin());
      }

      localXMLTag4.addNestedTag(((Place)localObject2).getCardsXMLTag());
      localXMLTag4.addNestedTag(((Combination)localObject3).toXMLTag());

      localXMLTag3.addNestedTag(localXMLTag4);
    }

    localXMLTag1.addNestedTag(localXMLTag3);

    Object localObject1 = localXMLTag1.toString();
    localXMLTag1.invalidate();

    setCachedGameResultXML((String)localObject1);
  }

  public void setCachedGameResultXML(String paramString) {
    cachedGameResultXML = paramString;
  }

  public void setGameAmount(BigDecimal paramBigDecimal) {
    gameAmount = paramBigDecimal;
  }

  public void addPlaceAndCombination(Place paramPlace, Combination paramCombination) {
    winners.add(new Winner(paramPlace, paramCombination));
  }

  public void clearWinners() {
    winners.clear();
  }

  public void prepareWinners(ArrayList paramArrayList) {
    synchronized (paramArrayList) {
      Collections.sort(paramArrayList, new WinnersComparator(null));
    }
  }

  public void determineWinners()
  {
    synchronized (winners) {
      prepareWinners(winners);

      synchronized (placesCards)
      {
        ArrayList localArrayList1 = new ArrayList();
        placesCards.clear();
        ArrayList localArrayList2 = new ArrayList();

        int i = winners.size();

        log.info("Winners Size: " + i);
        Object localObject2;
        if (i > 1) {
          int j = 0;
          localObject1 = (Winner)winners.get(j);
          localArrayList1.add(localObject1);

          if (((Winner)localObject1).getPlace().isAcceptShowCards()) {
            placesCards.put(((Winner)localObject1).getPlace(), ((Winner)localObject1).getCombination());
          }

          for (int m = 1; m < i; m++) {
            Winner localWinner1 = (Winner)winners.get(m);

            if (localWinner1.getPlace().isAcceptShowCards()) {
              placesCards.put(localWinner1.getPlace(), localWinner1.getCombination());
            }

            if (localWinner1.getCombination().compareTo(((Winner)localObject1).getCombination()) == 0) {
              localArrayList1.add(localWinner1);
            }
            else {
              addLoser(localWinner1.getPlace());

              localObject2 = new ArrayList(localArrayList1.size());
              ((ArrayList)localObject2).addAll(localArrayList1);
              localArrayList2.add(localObject2);

              localArrayList1.clear();
              localArrayList1.add(localWinner1);
              localObject1 = localWinner1;
            }

          }

          if (localArrayList1.size() > 0)
          {
            ArrayList localArrayList4 = new ArrayList(localArrayList1.size());
            localArrayList4.addAll(localArrayList1);
            localArrayList2.add(localArrayList4);
          }

          winners.clear();
          winners = localArrayList1;
        } else if (winners.size() == 1) {
          ArrayList localArrayList3 = new ArrayList(winners.size());
          localArrayList3.addAll(winners);
          localArrayList2.add(localArrayList3);
        }

        winners.clear();

        int k = localArrayList2.size();
        Object localObject1 = gameAmount;
        for (int n = 0; n < k; n++) {
          localObject1 = processWinnersAmount1((ArrayList)localArrayList2.get(n), (BigDecimal)localObject1, false);
          for (int i1 = 0; i1 < ((ArrayList)localArrayList2.get(n)).size(); i1++) {
            localObject2 = (Winner)((ArrayList)localArrayList2.get(n)).get(i1);
            if (((Winner)localObject2).getAmount().floatValue() > 0.0F) {
              winners.add(localObject2);
            }
          }

        }

        if ((((BigDecimal)localObject1).floatValue() > 0.0F) && (k > 0)) {
          processWinnersAmount1((ArrayList)localArrayList2.get(k - 1), (BigDecimal)localObject1, true);
          for (n = 0; n < ((ArrayList)localArrayList2.get(k - 1)).size(); n++) {
            Winner localWinner2 = (Winner)((ArrayList)localArrayList2.get(k - 1)).get(n);
            if (localWinner2.getAmount().floatValue() > 0.0F) {
              int i2 = 0;
              Iterator localIterator = winners.iterator();
              while (localIterator.hasNext()) {
                if ((Winner)(Winner)localIterator.next() == localWinner2) {
                  i2 = 1;
                }
              }
              if (i2 == 0)
                winners.add(localWinner2);
            }
          }
        }
      }
    }
  }

  private BigDecimal processWinnersAmount1(ArrayList paramArrayList, BigDecimal paramBigDecimal, boolean paramBoolean)
  {
    int i = paramArrayList.size();
    BigDecimal localBigDecimal1 = paramBigDecimal;
    BigDecimal localBigDecimal2 = getcanGetAmountMax();

    if (i > 0)
    {
      BigDecimal localBigDecimal3 = paramBigDecimal;
      Object localObject1;
      if (localBigDecimal3.floatValue() > 0.0F)
      {
        int j = 0;

        log.info("processWinnersAmount1a: gameAmount=" + paramBigDecimal.floatValue() + " maxBet=" + game.getDesk().getMaxBet().floatValue());

        log.info("RealWinners Size: " + i);

        localObject1 = paramArrayList.iterator();
        Object localObject2;
        Object localObject3;
        Object localObject4;
        while (((Iterator)localObject1).hasNext())
        {
          if (localBigDecimal1.floatValue() <= 0.0F)
            break;
          localObject2 = (Winner)((Iterator)localObject1).next();

          if (((Winner)localObject2).getPlace().isAllIn())
          {
            if (((Winner)localObject2).getPlace().getAllInPretendedAmount().floatValue() > 0.0F)
            {
              localObject3 = new BigDecimal(0);

              if (((Winner)localObject2).getPlace().getAllInPretendedAmount().floatValue() >= paramBigDecimal.floatValue())
              {
                localObject3 = paramBigDecimal;

                log.info("processWinnersAmount1b: gameAmount=" + paramBigDecimal.floatValue() + " allInPretendedAmount=" + ((Winner)localObject2).getPlace().getAllInPretendedAmount().floatValue() + " stakingAmount=" + ((Winner)localObject2).getPlace().getStakingAmountCache().floatValue() + " maxBet=" + game.getDesk().getMaxBet().floatValue());

                if (i > 1)
                {
                  localObject3 = splithandle((BigDecimal)localObject3, paramArrayList, paramBigDecimal, (Winner)localObject2);
                }

              }
              else
              {
                log.info("processWinnersAmount1c: gameAmount=" + paramBigDecimal.floatValue() + " allInPretendedAmount=" + ((Winner)localObject2).getPlace().getAllInPretendedAmount().floatValue() + " stakingAmount=" + ((Winner)localObject2).getPlace().getStakingAmountCache().floatValue() + " maxBet=" + game.getDesk().getMaxBet().floatValue());

                localObject4 = getPlacesList().allPlacesIterator();

                while (((Iterator)localObject4).hasNext())
                {
                  Place localPlace = (Place)((Iterator)localObject4).next();

                  if (localPlace.isBusy())
                  {
                    log.info("processWinnersAmount1preloop: " + localPlace.getPlayer().getLogin() + " stakingamount:" + localPlace.getStakingAmountCache().floatValue());

                    if (localPlace.getStakingAmountCache().floatValue() > 0.0F)
                    {
                      if (localPlace.getStakingAmountCache().floatValue() < ((Winner)localObject2).getPlace().getStakingAmountCache().floatValue())
                      {
                        log.info("processWinnersAmount1loop: Test 1 " + localPlace.getPlayer().getLogin() + " stakingamount1:" + localPlace.getStakingAmountCache().floatValue());
                        localObject3 = ((BigDecimal)localObject3).add(localPlace.getStakingAmountCache());
                      }
                      else
                      {
                        log.info("processWinnersAmount1loop: Test 2 " + localPlace.getPlayer().getLogin() + " stakingamount2:" + ((Winner)localObject2).getPlace().getStakingAmountCache().floatValue());
                        localObject3 = ((BigDecimal)localObject3).add(((Winner)localObject2).getPlace().getStakingAmountCache());
                      }
                    }
                  }
                }

                log.info("processWinnersAmount1loop: canGetAmount=" + ((BigDecimal)localObject3).floatValue());

                if (i > 1)
                {
                  localObject3 = splithandle((BigDecimal)localObject3, paramArrayList, paramBigDecimal, (Winner)localObject2);
                }

                if (((BigDecimal)localObject3).floatValue() > ((Winner)localObject2).getPlace().getAllInPretendedAmount().floatValue())
                {
                  localObject3 = ((Winner)localObject2).getPlace().getAllInPretendedAmount();
                }

                if (((BigDecimal)localObject3).floatValue() > paramBigDecimal.floatValue())
                {
                  localObject3 = paramBigDecimal;
                }

              }

              localObject4 = ((BigDecimal)localObject3).setScale(2, 5);

              log.info(((Winner)localObject2).getPlace().getPlayer().getLogin() + " WONa: " + localObject4);

              localBigDecimal1 = localBigDecimal1.subtract((BigDecimal)localObject4).setScale(2, 5);
              ((Winner)localObject2).setAmount((BigDecimal)localObject4);
              ((Winner)localObject2).getPlace().incDeskAmount((BigDecimal)localObject4);
              ((Winner)localObject2).getPlace().setStartAmount((BigDecimal)localObject4);
            }
            else
            {
              Log.out("Class GameResult: Error: win.getPlace().getAllInPretendedAmount() == 0");
            }

          }
          else
          {
            localObject3 = paramBigDecimal;

            if (i > 1)
            {
              localObject3 = splithandle((BigDecimal)localObject3, paramArrayList, paramBigDecimal, (Winner)localObject2);
            }

            localObject4 = ((BigDecimal)localObject3).setScale(2, 5);

            localBigDecimal1 = localBigDecimal1.subtract((BigDecimal)localObject4).setScale(2, 5);
            ((Winner)localObject2).setAmount((BigDecimal)localObject4);
            ((Winner)localObject2).getPlace().incDeskAmount((BigDecimal)localObject4);
            ((Winner)localObject2).getPlace().setStartAmount((BigDecimal)localObject4);
            log.info(((Winner)localObject2).getPlace().getPlayer().getLogin() + " WONb: " + localObject4);
            j++;
          }

        }

        if ((j > 0) && (localBigDecimal1.floatValue() > 0.0F))
        {
          localObject2 = localBigDecimal1.divide(new BigDecimal(j), 2, 5);
          localObject3 = paramArrayList.iterator();
          while (((Iterator)localObject3).hasNext()) {
            localObject4 = (Winner)((Iterator)localObject3).next();
            if (!((Winner)localObject4).getPlace().isAllIn()) {
              ((Winner)localObject4).setAmount((BigDecimal)localObject2);
              log.info(((Winner)localObject4).getPlace().getPlayer().getLogin() + " WON PART: " + localObject2);
              ((Winner)localObject4).getPlace().incDeskAmount((BigDecimal)localObject2);
              ((Winner)localObject4).getPlace().setStartAmount((BigDecimal)localObject2);
            }
          }
          localBigDecimal1 = new BigDecimal(0);
        }

        if ((paramBoolean) && (localBigDecimal1.floatValue() > 0.0F))
        {
          localObject2 = localBigDecimal1.divide(new BigDecimal(i), 2, 5);
          localObject3 = paramArrayList.iterator();
          while (((Iterator)localObject3).hasNext()) {
            localObject4 = (Winner)((Iterator)localObject3).next();
            ((Winner)localObject4).setAmount((BigDecimal)localObject2);
            ((Winner)localObject4).getPlace().incDeskAmount((BigDecimal)localObject2);
            ((Winner)localObject4).getPlace().setStartAmount((BigDecimal)localObject2);
            log.info(((Winner)localObject4).getPlace().getPlayer().getLogin() + " WON PART1: " + localObject2);
          }
          localBigDecimal1 = new BigDecimal(0);
        }

      }
      else
      {
        Log.out("GameResults - All amount is 0");
      }

      Iterator localIterator = paramArrayList.iterator();
      while (localIterator.hasNext())
      {
        localObject1 = (Winner)localIterator.next();
        ((Winner)localObject1).getPlace().setStakingAmountCache(new BigDecimal(0));
      }

    }

    return (BigDecimal)(BigDecimal)(BigDecimal)(BigDecimal)localBigDecimal1;
  }

  public BigDecimal splithandle(BigDecimal paramBigDecimal1, ArrayList paramArrayList, BigDecimal paramBigDecimal2, Winner paramWinner)
  {
    BigDecimal localBigDecimal1 = new BigDecimal(0);
    Object localObject1 = paramBigDecimal1;
    BigDecimal localBigDecimal2 = paramBigDecimal1;

    int i = paramArrayList.size();

    Iterator localIterator1 = getPlacesList().allPlacesIterator();
    while (localIterator1.hasNext())
    {
      localObject2 = (Place)localIterator1.next();

      if (((Place)localObject2).isBusy())
      {
        if (((Place)localObject2).getStakingAmountCache().floatValue() > 0.0F)
        {
          if (((Place)localObject2).getStakingAmountCache().floatValue() < paramWinner.getPlace().getStakingAmountCache().floatValue())
          {
            localBigDecimal1 = localBigDecimal1.add(((Place)localObject2).getStakingAmountCache());
          }
          else
          {
            localBigDecimal1 = localBigDecimal1.add(paramWinner.getPlace().getStakingAmountCache());
          }
        }

      }

    }

    if (localBigDecimal1.floatValue() > paramBigDecimal2.floatValue()) localBigDecimal1 = paramBigDecimal2;

    log.info("Splithandle: gameAmount=" + paramBigDecimal2.floatValue() + " canGetAmount=" + paramBigDecimal1.floatValue() + " size=" + i + " win=" + paramWinner.getPlace().getPlayer().getLogin() + " stakingamountcache=" + paramWinner.getPlace().getStakingAmountCache().floatValue());

    Object localObject2 = paramArrayList.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      BigDecimal localBigDecimal3 = new BigDecimal(0);

      Winner localWinner = (Winner)((Iterator)localObject2).next();
      if (localWinner.getPlace().getAllInPretendedAmount().floatValue() >= localBigDecimal2.floatValue())
      {
        localBigDecimal2 = localWinner.getPlace().getAllInPretendedAmount();

        if (localBigDecimal2.floatValue() > paramBigDecimal2.floatValue()) localBigDecimal2 = paramBigDecimal2;

      }

      Iterator localIterator2 = getPlacesList().allPlacesIterator();
      while (localIterator2.hasNext())
      {
        Place localPlace = (Place)localIterator2.next();

        if (localPlace.isBusy())
        {
          if (localPlace.getStakingAmountCache().floatValue() > 0.0F)
          {
            if (localPlace.getStakingAmountCache().floatValue() < localWinner.getPlace().getStakingAmountCache().floatValue())
            {
              localBigDecimal3 = localBigDecimal3.add(localPlace.getStakingAmountCache());
            }
            else
            {
              localBigDecimal3 = localBigDecimal3.add(localWinner.getPlace().getStakingAmountCache());
            }
          }

        }

      }

      if (localBigDecimal3.floatValue() < ((BigDecimal)localObject1).floatValue()) localObject1 = localBigDecimal3;

    }

    log.info("Splithandle2: gameAmount=" + paramBigDecimal2.floatValue() + " mintest=" + ((BigDecimal)localObject1).floatValue() + " maxtest=" + localBigDecimal2.floatValue() + " thistest=" + localBigDecimal1.floatValue());

    if (localBigDecimal1.floatValue() == ((BigDecimal)localObject1).floatValue()) {
      paramBigDecimal1 = localBigDecimal1.divide(new BigDecimal(i), 5);
    } else {
      paramBigDecimal1 = ((BigDecimal)localObject1).divide(new BigDecimal(i), 5);
      paramBigDecimal1 = paramBigDecimal1.add(localBigDecimal1.subtract((BigDecimal)localObject1));
    }

    log.info("Splithandle3: canGetAmount=" + paramBigDecimal1.floatValue());

    return (BigDecimal)(BigDecimal)paramBigDecimal1;
  }

  public String toXML()
  {
    return getCachedGameResultXML();
  }

  public void setcanGetAmountMax(BigDecimal paramBigDecimal)
  {
    canGetAmountMax = paramBigDecimal;
  }

  public BigDecimal getcanGetAmountMax()
  {
    return canGetAmountMax;
  }

  public ArrayList getLosers() {
    return losers;
  }

  public void addLoser(Place paramPlace) {
    int i = 1;
    for (int j = 0; j < losers.size(); j++) {
      Loser localLoser2 = (Loser)losers.get(j);
      Place localPlace = localLoser2.getPlace();
      if (localPlace.getPlayer().getID() == paramPlace.getPlayer().getID()) {
        i = 0;
        break;
      }
    }
    if (i != 0) {
      Loser localLoser1 = new Loser(paramPlace);
      losers.add(localLoser1);
    }
  }

  public PlacesList getPlacesList()
  {
    return game.getDesk().getPlacesList();
  }

  private class WinnersComparator
    implements Comparator
  {
    private WinnersComparator()
    {
    }

    public int compare(Object paramObject1, Object paramObject2)
    {
      GameResult.Winner localWinner1 = (GameResult.Winner)paramObject1;
      GameResult.Winner localWinner2 = (GameResult.Winner)paramObject2;
      return localWinner1.getCombination().compareTo(localWinner2.getCombination()) * -1;
    }
  }

  private class Loser
  {
    private BigDecimal amount = new BigDecimal(0);
    private BigDecimal startAmount = new BigDecimal(0);
    private Place place = null;

    public Loser(Place arg2)
    {
      Object localObject;
      place = localObject;
      amount = localObject.getAmount();
      startAmount = localObject.getStartAmount();
      localObject.setStartAmount(amount);
    }

    public BigDecimal getAmount() {
      return amount;
    }

    public Place getPlace() {
      return place;
    }

    public BigDecimal getStartAmount() {
      return startAmount;
    }
  }

  private class Winner
  {
    private Place place = null;
    private Combination combination = null;
    private BigDecimal amount = new BigDecimal(0);

    public Winner(Place paramCombination, Combination arg3) {
      place = paramCombination;
      Object localObject;
      combination = localObject;
    }

    public Combination getCombination() {
      return combination;
    }

    public void invalidate() {
      place = null;
      combination = null;
    }

    public void setAmount(BigDecimal paramBigDecimal) {
      amount = paramBigDecimal;
    }

    public Place getPlace() {
      return place;
    }

    public BigDecimal getAmount() {
      return amount;
    }
  }
}