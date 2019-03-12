package game.stats;

import defaultvalues.DefaultValue;
import game.Desk;
import game.Game;
import game.Place;
import game.PlacesList;
import game.Player;
import game.pokerrounds.AfterRiverPokerRound;
import game.pokerrounds.PokerRound;
import game.pokerrounds.PreFlopStakesPokerRound;
import game.pokerrounds.PreRiverPokerRound;
import game.pokerrounds.PreTurnStakesPokerRound;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StatsCounter
{
  private Desk desk;
  private static final BigDecimal HUNDRED_PERCENTS = new BigDecimal(100);
  private static final BigDecimal ZERO_PERCENTS = DefaultValue.ZERO_BIDECIMAL;

  public StatsCounter(Desk desk) {
    this.desk = desk;
  }

  public void countSessionGames()
  {
    Iterator iter = desk.getPlacesList().allPlacesIterator();
    while (iter.hasNext()) {
      Place place = (Place)iter.next();
      Player player = place.getPlayer();
      if ((player != null) && 
        (!place.isSittingOut())) {
        PlayersStats.getPlayerStat(player, desk.getPokerType()).incSessionGames();
        place.getPlaceSessionStats().countHandsPerHourForLastHour();
      }
    }
  }

  public void countGamesWon(ArrayList winners)
  {
    Iterator iter = desk.getPlacesList().allPlacesIterator();
    while (iter.hasNext()) {
      Place place = (Place)iter.next();
      Player player = place.getPlayer();
      if ((player != null) && 
        (!place.isSittingOut()))
        if (winners.contains(player))
          PlayersStats.getPlayerStat(player, desk.getPokerType()).incGamesWon(HUNDRED_PERCENTS);
        else
          PlayersStats.getPlayerStat(player, desk.getPokerType()).incGamesWon(ZERO_PERCENTS);
    }
  }

  public void countShowDownsWon(ArrayList winners)
  {
    Iterator iter = desk.getPlacesList().allPlacesIterator();
    int players = 0;
    ArrayList loosersArray = new ArrayList();
    ArrayList winnersArray = new ArrayList();
    while (iter.hasNext()) {
      Place place = (Place)iter.next();
      Player player = place.getPlayer();
      if ((player != null) && (!place.isFold()) && (!place.isSittingOut())) {
        players++;
        if (winners.contains(player))
          winnersArray.add(player);
        else {
          loosersArray.add(player);
        }
      }
    }

    if (players != 2) {
      return;
    }

    iter = winnersArray.iterator();
    while (iter.hasNext()) {
      Player player = (Player)iter.next();
      PlayersStats.getPlayerStat(player, desk.getPokerType()).incShowdownsWon(HUNDRED_PERCENTS);
    }

    iter = loosersArray.iterator();
    while (iter.hasNext()) {
      Player player = (Player)iter.next();
      PlayersStats.getPlayerStat(player, desk.getPokerType()).incShowdownsWon(ZERO_PERCENTS);
    }
  }

  public void countActivePlayersFlopSeens()
  {
    Iterator iter = desk.getPlacesList().allPlacesIterator();
    while (iter.hasNext()) {
      Place place = (Place)iter.next();
      Player player = place.getPlayer();
      if ((player != null) && (!place.isFold()) && (!place.isSittingOut()))
        countFlopSeens(player);
    }
  }

  public void countFlopSeens(Player player)
  {
    PokerRound round = desk.getGame().getCurrentRound();
    if (round == null) {
      PlayersStats.getPlayerStat(player, desk.getPokerType()).incFlopSeen(ZERO_PERCENTS);
    }

    if ((round instanceof PreFlopStakesPokerRound))
      PlayersStats.getPlayerStat(player, desk.getPokerType()).incFlopSeen(ZERO_PERCENTS);
    else
      PlayersStats.getPlayerStat(player, desk.getPokerType()).incFlopSeen(HUNDRED_PERCENTS);
  }

  public void countActivePlayersFourthStreetSeens()
  {
    Iterator iter = desk.getPlacesList().allPlacesIterator();
    while (iter.hasNext()) {
      Place place = (Place)iter.next();
      Player player = place.getPlayer();
      if ((player != null) && (!place.isFold()) && (!place.isSittingOut()))
        countFourthStreetSeens(player);
    }
  }

  public void countFourthStreetSeens(Player player)
  {
    PokerRound round = desk.getGame().getCurrentRound();
    if (round == null) {
      PlayersStats.getPlayerStat(player, desk.getPokerType()).incFlopSeen(ZERO_PERCENTS);
    }

    PlayersStats.getPlayerStat(player, desk.getPokerType()).incFourthStreetSeen(HUNDRED_PERCENTS);
  }

  public void countWinIfFlopSeen(List winners)
  {
    PokerRound round = desk.getGame().getCurrentRound();
    if ((round instanceof PreFlopStakesPokerRound)) {
      return;
    }

    Iterator iter = desk.getPlacesList().allPlacesIterator();
    while (iter.hasNext()) {
      Place place = (Place)iter.next();
      Player player = place.getPlayer();
      if ((player != null) && 
        (!place.isFold()) && (!place.isSittingOut()))
        if (winners.contains(player))
          PlayersStats.getPlayerStat(player, desk.getPokerType()).incWinIfFlopSeen(HUNDRED_PERCENTS);
        else
          PlayersStats.getPlayerStat(player, desk.getPokerType()).incWinIfFlopSeen(ZERO_PERCENTS);
    }
  }

  public void countWinIfFourthStreetSeen(List winners)
  {
    Iterator iter = desk.getPlacesList().allPlacesIterator();
    while (iter.hasNext()) {
      Place place = (Place)iter.next();
      Player player = place.getPlayer();
      if ((player != null) && 
        (!place.isFold()) && (!place.isSittingOut()))
        if (winners.contains(player))
          PlayersStats.getPlayerStat(player, desk.getPokerType()).incWinIfFourthStreetSeen(HUNDRED_PERCENTS);
        else
          PlayersStats.getPlayerStat(player, desk.getPokerType()).incWinIfFourthStreetSeen(ZERO_PERCENTS);
    }
  }

  public void countFolds(Place place)
  {
    PokerRound round = desk.getGame().getCurrentRound();

    Player player = place.getPlayer();
    if ((player != null) && 
      (place.isFold()) && (!place.isSittingOut()))
    {
      PlayersStats.getPlayerStat(player, desk.getPokerType()).incFold();

      if ((round instanceof PreFlopStakesPokerRound))
        PlayersStats.getPlayerStat(player, desk.getPokerType()).incFoldPreFlop();
      else if ((round instanceof PreTurnStakesPokerRound))
        PlayersStats.getPlayerStat(player, desk.getPokerType()).incFoldAfterFlop();
      else if ((round instanceof PreRiverPokerRound))
        PlayersStats.getPlayerStat(player, desk.getPokerType()).incFoldAfterTurn();
      else if ((round instanceof AfterRiverPokerRound))
        PlayersStats.getPlayerStat(player, desk.getPokerType()).incFoldAfterRiver();
    }
  }

  public void countNoFolds()
  {
    Iterator iter = desk.getPlacesList().allPlacesIterator();
    while (iter.hasNext()) {
      Place place = (Place)iter.next();
      Player player = place.getPlayer();
      if ((player != null) && 
        (!place.isFold()) && (!place.isSittingOut()))
        PlayersStats.getPlayerStat(player, desk.getPokerType()).incNoFold();
    }
  }

  public void counCheck(Player player)
  {
    if (player != null)
      PlayersStats.getPlayerStat(player, desk.getPokerType()).incCheck();
  }

  public void counCall(Player player)
  {
    if (player != null)
      PlayersStats.getPlayerStat(player, desk.getPokerType()).incCall();
  }

  public void counBet(Player player)
  {
    if (player != null)
      PlayersStats.getPlayerStat(player, desk.getPokerType()).incBet();
  }

  public void counRaise(Player player)
  {
    if (player != null)
      PlayersStats.getPlayerStat(player, desk.getPokerType()).incRaise();
  }

  public void counReRaise(Player player)
  {
    if (player != null)
      PlayersStats.getPlayerStat(player, desk.getPokerType()).incReRaise();
  }
}