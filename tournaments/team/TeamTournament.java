package tournaments.team;

import commands.CmdReadTeamTournament;
import commands.safeupdaters.FinishTournamentHistory;
import commands.safeupdaters.StartTournamentHistory;
import defaultvalues.DefaultValue;
import game.Desk;
import game.ExecutionState;
import game.Game;
import game.Place;
import game.PlacesList;
import game.Player;
import game.amounts.PlayerAmount;
import game.messages.CommonStateMessagesList;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import server.Server;
import tournaments.FeeList;
import tournaments.GameLevel;
import tournaments.Tournament;
import tournaments.TournamentLevels;
import tournaments.team.dbupdaters.TeamTournamentsStageUpdater;
import tournaments.team.dbupdaters.TeamsHistoryUpdater;
import tournaments.team.dbupdaters.TeamsMatchesHistoryUpdater;
import tournaments.winners.PotentialLoosers;
import utils.CommonLogger;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class TeamTournament extends Tournament
{
  private final ExecutionState beginState = new ExecutionState(false);
  private final ExecutionState endState = new ExecutionState(false);
  private String XMLHistory = "";
  private TeamTournamentScheduler tournamentSheduler;
  private final TournamentTeams tournamentTeams = new TournamentTeams(this);
  private final OppositeDesksStorage oppositeDesksStorage = new OppositeDesksStorage(this);
  private static final int TEAM_TOURNAMENT_MAX_PLAYERS_AT_TABLE = 2;
  private static final long DROP_DESK_DELAY = 5000L;
  public static final String DB_PARAM_MIN_TEAMS_QTY = "tour_min_teams_qty";
  public static final String DB_PARAM_MAX_TEAMS_QTY = "tour_max_teams_qty";
  public static final String DB_PARAM_PLAYERS_IN_TEAM_QTY = "tour_players_in_team";
  private static final String TAG_NAME_STAGES = "STG";
  public static final String TAG_NAME_STAGE = "ST";
  public static final String OUT_PARAM_NUMBER = "N";
  public static final String OUT_PARAM_NAME = "NM";
  public static final String OUT_PARAM_SCORE = "SC";
  public static final String OUT_PARAM_BEGIN_DATE = "DT";
  public static final String TAG_NAME_TEAM = "TM";
  public static final String OUT_PARAM_ID = "ID";
  public static final String TAG_NAME_TEAMS = "TS";
  public static final String OUT_PARAM_TOTAL_TEAMS = "TTS";

  public TeamTournament(int id, String name, int tournamentType, int subType, int game, int gameType, int moneyType, BigDecimal tournamentAmount, BigDecimal maxBet, BigDecimal minBet, Date beginDate, Date regDate, BigDecimal buyIn, int increaseLevelAfter, long timeOnLevel, long breakPeriod, long breakLength, int maxPlayersAtTheTable, int reBuys, int addons, BigDecimal addonsAmount, BigDecimal reBuysAmount, BigDecimal fee, int minStartPlayersCount, int tourSpeed, int minTeamsQty, int playersInTeam)
  {
    super(id, name, tournamentType, subType, game, gameType, moneyType, tournamentAmount, maxBet, minBet, beginDate, regDate, buyIn, increaseLevelAfter, timeOnLevel, breakPeriod, breakLength, maxPlayersAtTheTable, reBuys, addons, addonsAmount, reBuysAmount, fee, minStartPlayersCount, tourSpeed);
    teamsQty = minTeamsQty;
    this.playersInTeam = playersInTeam;
    this.maxPlayersAtTheTable = 2;

    currentMaxBet = maxBet;
    currentMinBet = minBet;

    GameLevel newLevel = tournamentLevels.getNextGameLevel(0);
    currentAnte = newLevel.getAnte();
    currentBringIn = newLevel.getBringIn();
    currentMinBet = newLevel.getMinBet();
    currentMaxBet = newLevel.getMaxBet();
  }

  public void run()
  {
    synchronized (this) {
      regStatus = 2;
      status = 0;
      createOppositeDesks();

      updateCashedXML();

      Timer timer = new Timer();

      long delay = tournamentSheduler.getCurrentStage().getDate().getTime() - new Date().getTime();

      timer.schedule(new LaunchTournamentTask(this), Math.abs(delay));

      if (CommonLogger.getLogger().isInfoEnabled()) {
        CommonLogger.getLogger().info("Team Tournament " + name + " will start after " + delay + " milliseconds");
      }
    }

    synchronized (beginState) {
      while (!beginState.canExec()) {
        try {
          beginState.wait();
        } catch (InterruptedException e) {
          CommonLogger.getLogger().fatal("TeamTournament interrupted", e);
        }
      }
    }

    begin = true;
    status = 2;
    new Thread(new StartTournamentHistory(this)).start();
    updateCashedXML();

    synchronized (endState) {
      while (!endState.canExec()) {
        try {
          endState.wait();
        } catch (InterruptedException e) {
          CommonLogger.getLogger().fatal("TeamTournament interrupted", e);
        }
      }
    }

    determineWinners();
    end = true;

    new FinishTournamentHistory(this).run();
    new TeamsHistoryUpdater(this).updateHistory();
    try {
      new TeamTournamentsStageUpdater(this).updateHistory();
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    CommonLogger.getLogger().info("Team Tournament " + name + " was ended.");

    if (status != 4) {
      regStatus = 2;
      status = 3;
    }

    this.timer.cancel();
    updateCashedXML();
    try
    {
      Thread.sleep(10000L);
    } catch (InterruptedException e) {
      CommonLogger.getLogger().warn("Sleep failed", e);
    }

    reloadTournament();
  }

  public void reloadTournament()
  {
    List list = Server.getTournamentsList();
    synchronized (list) {
      Iterator iter = list.iterator();
      while (iter.hasNext()) {
        Tournament t = (Tournament)iter.next();
        if (t.getID() == getID()) {
          iter.remove();
          break;
        }
      }

    }

    Connection db = Server.getDbConnection();
    CmdReadTeamTournament cmd = new CmdReadTeamTournament(list, ID);
    cmd.setDbConnection(db);
    try {
      cmd.execute();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      db.close();
    } catch (SQLException e) {
      CommonLogger.getLogger().fatal("Cannot reload tournament", e);
    }
  }

  private void launch()
  {
    synchronized (beginState) {
      beginState.permit();
      beginState.notifyAll();
    }
  }

  public void determineWinners() {
    if ((oppositeDesksStorage.size() == 0) && (!end))
    {
      if (tournamentTeams.getTeams().size() == 2)
        distributePrizePool();
      else
        createNextStage();
    }
  }

  public synchronized void tryToFinishTournament()
  {
    if ((oppositeDesksStorage.size() == 0) && (!end))
    {
      synchronized (endState) {
        endState.permit();
        endState.notifyAll();
      }
    }
  }

  public void distributePrizePool()
  {
    Team team = tournamentTeams.getBestTeam();
    BigDecimal prizePool = feeList.getFeeAmount();

    if (CommonLogger.getLogger().isDebugEnabled()) {
      CommonLogger.getLogger().debug("Team Tournament complete. Best Team: " + team.getName() + ". Won: " + prizePool.toString());
    }

    List members = team.getMembers();
    synchronized (members) {
      int size = members.size();
      BigDecimal amountForEach = prizePool.divide(new BigDecimal(size), 2, 5);
      for (int i = 0; i < size; i++) {
        Player player = (Player)members.get(i);
        player.increaseAmount(amountForEach, moneyType);

        if (CommonLogger.getLogger().isDebugEnabled())
          CommonLogger.getLogger().debug("Player " + player.getLogin() + " won " + amountForEach.toString() + " in Team Tournament " + name);
      }
    }
  }

  public void createNextStage()
  {
  }

  public void createOppositeDesks()
  {
    List teams = tournamentTeams.getTeams();
    List desks = createTournamentsDesksTables(teams.size() / 2 * playersInTeam, moneyType, gameType, currentMaxBet, currentMinBet, currentAnte, currentBringIn, game, buyIn, minPlayerRate, maxPlayersAtTheTable);

    desksList.addAll(desks);

    TeamPlayersShuffler shuffler = new TeamPlayersShuffler(teams, desks, tournamentAmount);
    List oppositeDesksList = shuffler.shuffle();
    Iterator iter = oppositeDesksList.iterator();
    while (iter.hasNext())
    {
      oppositeDesksStorage.addOppositeDesk((OppositeDesk)iter.next());
    }
  }

  public ArrayList notifyTournamentAboutGameEnd(Game game) {
    ArrayList eliminatedPlayers = new ArrayList(2);

    updateDeskLevel(game.getDesk());
    suggestRebuys(game.getDesk());

    DeskMatchResult deskMatchResult = tryToFinishGame(game.getDesk());
    if (deskMatchResult != null) {
      eliminatedPlayers.add(deskMatchResult.getWinner());
      eliminatedPlayers.add(deskMatchResult.getLooser());
    }

    if (getStatus() == 6)
    {
      game.getPublicStateMessagesList().addCommonMessage(105, (int)getBreakLength());
      if (CommonLogger.getLogger().isInfoEnabled()) {
        CommonLogger.getLogger().info("TeamTournament PauseTime : " + (getNextBreakTime().getTime() - new Date().getTime()));
      }

      while (getStatus() == 6)
        game.waitTwoSeconds();
    }
    if (getStatus() == 5) {
      game.getPublicStateMessagesList().addCommonMessage(106, 0, 0);
    }

    if (game.getDesk().getPlayersCount() == 0) {
      dropDeskAfterSomeTime(game.getDesk());
    }

    return eliminatedPlayers;
  }

  public void dropDeskAfterSomeTime(Desk desk)
  {
    CommonLogger.getLogger().info(TeamTournament.class.getName() + ".dropDeskAfterSomeTime is called : " + desk.getDeskName());
    Timer timer = new Timer();
    timer.schedule(new DropDeskTask(desk, this), 5000L);
  }

  public DeskMatchResult tryToFinishGame(Desk desk)
  {
    Place looser = null;
    Place winner = null;
    ArrayList places = new ArrayList(2);

    synchronized (desk) {
      Iterator iter = desk.getPlacesList().allPlacesIterator();
      while (iter.hasNext()) {
        Place p = (Place)iter.next();
        if ((p.isBusy()) && (p.getAmount().compareTo(desk.getMaxBet()) <= 0))
          looser = p;
        else if (p.isBusy()) {
          winner = p;
        }

        if (p.isBusy()) {
          places.add(p);
        }
      }

    }

    if (places.size() != 2) {
      throw new RuntimeException("Occupied places qty might be 2: " + places.size());
    }

    if (looser == null) {
      return null;
    }

    if (winner == null)
    {
      Place place1 = (Place)places.get(0);
      Place place2 = (Place)places.get(1);

      CommonLogger.getLogger().warn("Level changed : " + currentLevel + ". But 2 players money wasn't enougth for continue. Details below: \n" + " player1: " + place1.getPlayer().getLogin() + " - " + place1.getAmount().toString() + " | player2: " + place2.getPlayer().getLogin() + " - " + place2.getAmount().toString());

      int comparationResult = place1.getAmount().compareTo(place2.getAmount());

      if (comparationResult < 0) {
        winner = place2;
        looser = place1;
      } else if (comparationResult == 0) {
        BigDecimal amount1 = potentialLoosers.getPlayerAmount(place1.getPlayer());
        BigDecimal amount2 = potentialLoosers.getPlayerAmount(place2.getPlayer());
        if (amount1 == null) {
          winner = place2;
          looser = place1;
        } else if (amount2 == null) {
          winner = place1;
          looser = place2;
        } else {
          int comparation2 = amount1.compareTo(amount2);
          if (comparation2 > 0) {
            winner = place2;
            looser = place1;
          } else {
            winner = place1;
            looser = place2;
          }
        }
      } else {
        winner = place1;
        looser = place2;
      }

    }

    if (CommonLogger.getLogger().isDebugEnabled()) {
      CommonLogger.getLogger().debug("Team Desk Match is completed. Desk: " + desk.getID() + " - " + desk.getDeskName() + ". Winner: " + winner.getPlayer().getLogin() + " - amount: " + winner.getAmount().toString() + ". Looser: " + looser.getPlayer().getLogin() + " - amount: " + looser.getAmount());
    }

    DeskMatchResult result = new DeskMatchResult(winner.getPlayer(), looser.getPlayer());
    clearTournamentDesk(desk, result);
    return result;
  }

  private void clearTournamentDesk(Desk desk, DeskMatchResult matchResult)
  {
    synchronized (desk) {
      Iterator iter = desk.getPlacesList().allPlacesIterator();
      while (iter.hasNext()) {
        Place p = (Place)iter.next();
        Player player = p.getPlayer();
        if (player != null)
        {
          Team team = tournamentTeams.getPlayerTeam(player);
          if (team == null) {
            throw new RuntimeException("Player team us null. Player : " + player.getLogin());
          }

          if (player.equals(matchResult.getWinner())) {
            Team opponentTeam = tournamentTeams.getPlayerTeam(matchResult.getLooser());
            team.incWons();
            new Thread(new TeamsMatchesHistoryUpdater(ID, team.getTeamId(), opponentTeam.getTeamId(), matchResult.getWinner().getID(), matchResult.getLooser().getID(), 1, team.getNum(), tournamentSheduler.getCurrentStage().getStage())).start();
            desk.getGame().getPublicStateMessagesList().addCommonMessage(player.getLogin(), 113, p.getNumber(), 1, DefaultValue.ZERO_BIDECIMAL, player.getID());
          } else {
            Team opponentTeam = tournamentTeams.getPlayerTeam(matchResult.getWinner());
            team.incLooses();
            new Thread(new TeamsMatchesHistoryUpdater(ID, team.getTeamId(), opponentTeam.getTeamId(), matchResult.getLooser().getID(), matchResult.getWinner().getID(), 0, team.getNum(), tournamentSheduler.getCurrentStage().getStage())).start();
            desk.getGame().getPublicStateMessagesList().addCommonMessage(player.getLogin(), 113, p.getNumber(), 0, DefaultValue.ZERO_BIDECIMAL, player.getID());
          }

          player.getPlayerAmount().deleteTournamentRecord(this);

          desk.getGame().notifyAboutLeaveDesk(p);
          p.free();
        }
      }
    }
  }

  public int join(Player player, int anInt)
  {
    return join(player);
  }

  public int join(Player player) {
    CommonLogger.getLogger().warn("Player " + player.toString() + " trying to join team tournament ID: " + ID + ", NAME : " + name + " - " + new Date().toString());

    return 3;
  }

  public int getTeamsQty()
  {
    return teamsQty;
  }

  public void setTeamsQty(int teamsQty) {
    this.teamsQty = teamsQty;
  }

  public int getPlayersInTeam() {
    return playersInTeam;
  }

  public void setPlayersInTeam(int playersInTeam) {
    this.playersInTeam = playersInTeam;
  }

  public TournamentTeams getTournamentTeams() {
    return tournamentTeams;
  }

  public TeamTournamentScheduler getTournamentSheduler() {
    return tournamentSheduler;
  }

  public void setTournamentSheduler(TeamTournamentScheduler tournamentSheduler) {
    this.tournamentSheduler = tournamentSheduler;
  }

  public String getXMLHistory() {
    return XMLHistory;
  }

  public void setXMLHistory(String XMLHistory) {
    this.XMLHistory = XMLHistory;
  }

  private String getCurrentStageXML()
  {
    List list = tournamentTeams.getSortedByNumList();

    XMLTag stage = new XMLTag("ST");
    try {
      stage.addParam("N", tournamentSheduler.getCurrentStage().getStage());
      stage.addParam("DT", (float)tournamentSheduler.getCurrentStage().getDate().getTime());
    } catch (Exception ex) {
      return "";
    }

    int size = list.size();

    if (size == 0) {
      return "";
    }

    for (int i = 0; i < size; i += 2) {
      Team team1 = (Team)list.get(i);
      Team team2 = (Team)list.get(i + 1);

      XMLTag teams = new XMLTag("TS");
      XMLTag teamTag = new XMLTag("TM");
      teamTag.addParam("ID", team1.getTeamId());
      teamTag.addParam("NM", team1.getName());
      teamTag.addParam("SC", team1.getWons());
      teams.addNestedTag(teamTag);

      teamTag = new XMLTag("TM");
      teamTag.addParam("ID", team2.getTeamId());
      teamTag.addParam("NM", team2.getName());
      teamTag.addParam("SC", team2.getWons());
      teams.addNestedTag(teamTag);

      stage.addNestedTag(teams);
    }

    String xml = stage.toString();
    stage.invalidate();
    return xml;
  }

  public String getTeamsXML() {
    XMLDoc doc = new XMLDoc();
    XMLTag tag = doc.startTag("STG");
    try
    {
      tag.addParam("TTS", teamsQty);
    } catch (Exception e) {
      return "";
    }

    StringBuffer buffer = new StringBuffer();
    buffer.append(XMLHistory);
    buffer.append(getCurrentStageXML());

    tag.setTagContent(buffer.toString());

    String xml = doc.toString();
    doc.invalidate();
    return xml;
  }

  public String getTeamPlayersXML(int teamId)
  {
    Team team = getTournamentTeams().getTeamById(teamId);
    List list = new ArrayList();
    if (team != null) {
      List members = team.getMembers();
      synchronized (members) {
        Iterator iter = members.iterator();
        while (iter.hasNext()) {
          Player pl = (Player)iter.next();
          list.add(pl);
        }
      }
    }

    XMLDoc doc = new XMLDoc();
    XMLTag tag = doc.startTag("PLAYERS");
    tag.addParam("CNT", list.size());

    Iterator iter = list.iterator();
    while (iter.hasNext()) {
      Player player = (Player)iter.next();
      OppositeDesk desk = oppositeDesksStorage.getPlayerDesk(player);

      XMLTag playerTag = new XMLTag("PL");
      if (desk == null)
        playerTag.addParam("DESK", "");
      else {
        playerTag.addParam("DESK", desk.getDesk().getID());
      }
      playerTag.addParam("TEID", teamId);
      playerTag.addParam("NAME", player.getLogin() + " (" + player.getCountry() + ")");
      BigDecimal deskAmount = DefaultValue.ZERO_BIDECIMAL;
      if (desk != null) {
        deskAmount = desk.getDesk().getPlayerDeskAmount(player);
        if (deskAmount == null) {
          deskAmount = DefaultValue.ZERO_BIDECIMAL;
        }
      }

      playerTag.addParam("STATUS", deskAmount.compareTo(DefaultValue.ZERO_BIDECIMAL) > 0 ? 1 : 0);
      playerTag.addParam("AMOUNT", deskAmount.toString());

      tag.addNestedTag(playerTag);
    }

    String xml = doc.toString();
    doc.invalidate();
    return xml;
  }

  public void updateCashedXML()
  {
    HashMap playersAndDesks = new HashMap();

    XMLTag playersTag = new XMLTag("PLAYERS");
    XMLTag smallDesksTag = new XMLTag("SDESKS");

    Iterator iter = oppositeDesksStorage.unmodifiableIterator();
    while (iter.hasNext()) {
      OppositeDesk oppositeDesk = (OppositeDesk)iter.next();
      Desk desk = oppositeDesk.getDesk();
      BigDecimal small = new BigDecimal(3.402823466385289E+038D);
      BigDecimal big = new BigDecimal(1.401298464324817E-045D);
      int players = 0;

      Iterator placeIter = desk.getPlacesList().allPlacesIterator();
      while (placeIter.hasNext()) {
        Place p = (Place)placeIter.next();
        if (p.isBusy()) {
          Player pl = p.getPlayer();

          playersAndDesks.put(pl, desk);

          if (p.getAmount().floatValue() < small.floatValue()) {
            small = p.getAmount();
          }
          if (p.getAmount().floatValue() > big.floatValue()) {
            big = p.getAmount();
          }
          players++;
        }

      }

      XMLTag deskTag = new XMLTag("DESK");

      deskTag.addParam("ID", desk.getID());
      deskTag.addParam("NAME", desk.getDeskName());
      deskTag.addParam("PLACES", maxPlayersAtTheTable);
      deskTag.addParam("PLAYERS", players);
      deskTag.addParam("BIG", big.floatValue());
      deskTag.addParam("SMALL", small.floatValue());
      deskTag.addParam("LTYPE", gameType);
      deskTag.addParam("PTYPE", game);
      deskTag.addParam("TID", ID);
      deskTag.addParam("SPEED", speedType);

      smallDesksTag.addNestedTag(deskTag);
    }

    List teamsList = tournamentTeams.getUnmodifiableTeamsList();

    iter = teamsList.iterator();
    while (iter.hasNext()) {
      Team team = (Team)iter.next();
      List playersList = team.getMembers();
      Iterator iter1 = playersList.iterator();
      while (iter1.hasNext()) {
        Player player = (Player)iter1.next();

        Desk desk = (Desk)playersAndDesks.get(player);

        XMLTag playerTag = new XMLTag("PL");
        if (desk == null)
          playerTag.addParam("DESK", "");
        else {
          playerTag.addParam("DESK", desk.getID());
        }
        playerTag.addParam("TEID", team.getTeamId());
        playerTag.addParam("NAME", player.getLogin() + " (" + player.getCountry() + ")");
        BigDecimal deskAmount = DefaultValue.ZERO_BIDECIMAL;
        if (desk != null) {
          deskAmount = desk.getPlayerDeskAmount(player);
          if (deskAmount == null) {
            deskAmount = DefaultValue.ZERO_BIDECIMAL;
          }
        }

        if (cashedBigStacks.compareTo(deskAmount) < 0) {
          cashedBigStacks = deskAmount;
        }

        if (cashedSmallStacks.compareTo(deskAmount) > 0) {
          cashedSmallStacks = deskAmount;
        }

        playerTag.addParam("STATUS", deskAmount.compareTo(DefaultValue.ZERO_BIDECIMAL) > 0 ? 1 : 0);
        playerTag.addParam("AMOUNT", deskAmount.toString());

        playersTag.addNestedTag(playerTag);
      }
    }

    cashedAvgStacks = cashedBigStacks.add(cashedSmallStacks).divide(new BigDecimal(2), 2, 5).setScale(2, 5);

    synchronized (this) {
      cashedDesksXML = smallDesksTag.toString();
      cashedPlayersXML = playersTag.toString();
      cashedPrizeTableXML = getPrizeTableXML();
    }

    smallDesksTag.invalidate();
    playersTag.invalidate();
  }

  private class DropDeskTask extends TimerTask
  {
    private Desk desk = null;
    private TeamTournament tournament = null;

    public DropDeskTask(Desk desk, TeamTournament t) {
      this.desk = desk;
      tournament = t;
    }

    public void run() {
      List list = tournament.getDesksList();
      synchronized (list) {
        list.remove(desk);
      }

      tournament.oppositeDesksStorage.remove(desk);
      tournament.tryToFinishTournament();
    }
  }

  private class LaunchTournamentTask extends TimerTask
  {
    private TeamTournament tournament = null;

    public LaunchTournamentTask(TeamTournament t) {
      tournament = t;
    }

    public void run() {
      tournament.launch();
    }
  }
}