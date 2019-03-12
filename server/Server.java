package server;

import commands.CmdLoadBuddyList;
import commands.CmdLoadFeedBackTopics;
import commands.CmdLoadLastHandId;
import commands.CmdLoadNotes;
import commands.CmdReadDesks;
import commands.CmdReadPlayers;
import commands.CmdReadPlayersClub;
import commands.CmdReadPlayersStats;
import commands.CmdReadTournaments;
import feedbacks.FeedBackTopics;
import game.Desk;
import game.DeskStats;
import game.PlacesList;
import game.Player;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import processors.DesksCommandProcessor;
import processors.RequestCommandProcessor;
import processors.RequestCommandProcessorsFactory;
import settings.PokerSettings;
import tournaments.Tournament;
import tournaments.payout.MultiTournamentPayoutTable;
import utils.CommonLogger;
import utils.Log;
import waitinglist.WaitingList;

public class Server extends Thread
{
  public static int PORT;
  private static final int START_THREADS = 500;
  public static final String MSG_HELLO = "You connected to poker server v.1.025";
  public static final String MSG_INVALID_LOGIN = "Invalid login or password";
  public static final String MSG_BAD_PARAMS = "Bad parameters";
  public static final String MSG_GOODBYE = "Goodbye...";
  public static final String MSG_MUST_LOGIN = "Authorization first";
  public static final String MSG_UNRECOGNIZED_CMD = "Unrecognized command";
  public static final String MSG_DESK_FULL = "DESK FULL";
  public static final String MSG_ALREADY_JOINED = "ALREADY JOINED TO DESK";
  public static final String MSG_NO_MONEY = "NOT ENOUGH MONEY";
  public static final String MSG_PLACE_USED = "PLACE ALREADY USED";
  public static final String MSG_NEED_MORE_MONEY = "NEED MORE MONEY";
  public static final String MSG_INVALID_STAKE = "INVALID STAKE";
  public static final String MSG_PLAYER_NOT_ACCEPT_SHOW_CARDS = "NOT ACCEPTED BY PLAYER";
  public static final String MSG_TO_MUCH_MONEY = "TO MUCH MONEY";
  private static ArrayList players = null;
  private static ArrayList desks = null;
  public static final String PARAMS_DEFAULT_ENCODING = "ISO-8859-1";
  public static final int RECALCULATE_STATS_PERIOD = 60000;
  private static ArrayList connectionsPool = new ArrayList();
  private static final WaitingList waitingList = new WaitingList();
  private static final int WAITINGLIST_UPDATE_PERIOD = 1000;
  private static final TotalStats totalStats = new TotalStats();
  private static final FeedBackTopics feedBackTopics = new FeedBackTopics();
  private static final OnLinePlayers onlinePlayers = new OnLinePlayers();
  private static final long RECALCULATE_TOTAL_STATS_PERIOD = 5000L;
  public static final String MSG_CANNOT_JOIN_PRIVATE_DESK = "Cannot join private desk";
  private Player currentPlayer;
  private boolean isListen;
  private boolean controller;

  public Server()
  {
    currentPlayer = null;
    isListen = true;
    controller = false;
  }
  public void stopConnectionListening() {
    isListen = false;
  }

  public ArrayList getDesks() {
    return desks;
  }

  public static ArrayList getDesksList() {
    return desks;
  }

  public Player getCurrentPlayer() {
    return currentPlayer;
  }

  public static TotalStats getTotalStats() {
    return totalStats;
  }

  public void setCurrentPlayer(Player currentPlayer) {
    this.currentPlayer = currentPlayer;
  }

  public void invalidateCurrentPlayer() {
    Player player = currentPlayer;
    currentPlayer = null;
    if (player != null)
      onlinePlayers.unregisterPlayer(player);
  }

  public ArrayList getPlayers()
  {
    return players;
  }

  public static ArrayList getPlayersList() {
    return players;
  }

  public WaitingList getWaitingList() {
    return waitingList;
  }

  public boolean parseParams(ArrayList params, HashMap paramValues) {
    boolean result = true;

    Iterator it = params.iterator();
    while (it.hasNext()) {
      String input = (String)it.next();

      ParamParser parser = new ParamParser(input);
      if (!parser.parse()) {
        result = false;
        break;
      }
      paramValues.put(parser.getParamName(), parser.getParamValue());
    }

    return result;
  }

  public void processCommand(String input, OutputStream out, InetAddress inetAddress) throws IOException
  {
    StringTokenizer tokenizer = new StringTokenizer(input);

    String command = "";
    ArrayList paramsList = new ArrayList();

    if (tokenizer.hasMoreTokens()) {
      command = tokenizer.nextToken();
      while (tokenizer.hasMoreTokens()) {
        String param = tokenizer.nextToken();
        if (param.indexOf("?") >= 0)
          param = param.substring(param.indexOf("?"));
        paramsList.add(param);
      }
    }

    HashMap paramsMap = new HashMap();
    Response response = null;

    if (parseParams(paramsList, paramsMap)) {
      RequestCommandProcessorsFactory commandsFactory = RequestCommandProcessorsFactory.getFactory();

      RequestCommandProcessor commandProcessor = commandsFactory.getProcessor(command);

      paramsMap.put("INETADDRESS", inetAddress);

      response = commandProcessor.process(paramsMap, this);
    } else {
      response = Response.getUnrecognizedCmdResponse();
    }

    String result = response.getXML();
    if ((CommonLogger.getLogger().isDebugEnabled()) && 
      (!command.equals("DESKS")) && (!command.equals("CHECKBUDDYLIST")) && (!command.equals("CHECKTOURNAMENTSEATS")) && (!command.equals("CHECKWAITINGLIST")) && (!command.equals("CHATREAD")) && (!command.equals("DESK")) && (!command.equals("PROFILE")) && (!command.equals("GETTOTALSSTATS")) && (!command.equals("WHATISWEARED")) && (!command.equals("CHATTIME")) && (!command.equals("GETNOTE")))
    {
      CommonLogger.getLogger().debug("command=" + command + "(" + paramsMap + ")\t result=" + result);
    }

    if (command.equals("GET")) {
      URL url = new URL("http://" + inetAddress.getHostAddress() + "/pokertransfer.asp" + response.getResultMessage());
      URLConnection conn = url.openConnection();
      InputStream is = conn.getInputStream();
      is.close();
    }
    else
    {
      out.write(result.getBytes());
    }
    out.flush();

    paramsMap.clear();
    paramsMap = null;
    response = null;
  }

  public void manageConnections() {
    while (true) {
      Socket connection = null;
      synchronized (connectionsPool) {
        while (connectionsPool.isEmpty())
          try {
            connectionsPool.wait();
          } catch (Exception e) {
          }
        connection = (Socket)connectionsPool.remove(0);
      }
      try {
        OutputStream out = connection.getOutputStream();
        InputStream in = connection.getInputStream();
        InetAddress inetAddress = connection.getInetAddress();

        Response response = Response.getHelloResponse();
        out.write(response.getXML().getBytes());
        out.flush();

        isListen = true;
        do
        {
          String input = readClientInput(in);
          processCommand(input, out, inetAddress);
        }while (isListen);

        in.close();
        out.close();
      } catch (IOException e) {
        System.err.println(e.getMessage());
      } finally {
        try {
          invalidateCurrentPlayer();
          stopConnectionListening();

          connection.close(); } catch (IOException e) {
        }
      }
    }
  }

  public void manageGames() {
  }

  public static OnLinePlayers getOnlinePlayers() {
    return onlinePlayers;
  }

  public void run() {
    if (controller)
      manageGames();
    else
      manageConnections();
  }

  public static void addConnection(Socket connection)
  {
    synchronized (connectionsPool) {
      connectionsPool.add(connection);
      connectionsPool.notifyAll();
    }
  }

  public static List getTournamentsList() {
    return Tournament.getTournamentsList();
  }

  public static Tournament getTournamentByID(int id) {
    return Tournament.getTournamentByID(id);
  }

  public ArrayList getTournamentDesks(int id) {
    Tournament t = getTournamentByID(id);
    if (t == null) {
      return new ArrayList();
    }
    return t.getDesksList();
  }

  public static ArrayList getTournamentDesksList(int id)
  {
    Tournament t = getTournamentByID(id);
    if (t == null) {
      return new ArrayList();
    }
    return t.getDesksList();
  }

  public static void initTournamentsList() throws IOException
  {
    CmdReadTournaments cmd = new CmdReadTournaments();

    Connection db = getDbConnection();
    cmd.setDbConnection(db);
    cmd.execute();
    try {
      db.close();
    } catch (SQLException e) {
      throw new IOException(e.getMessage());
    }
  }

  public static void initDesksList() throws IOException {
    CmdReadDesks cmd = new CmdReadDesks();

    Connection db = getDbConnection();
    cmd.setDbConnection(db);
    cmd.setPlayers(players);
    cmd.execute();
    desks = cmd.getList();
    try {
      db.close();
    } catch (SQLException e) {
      throw new IOException(e.getMessage());
    }
  }

  public static void loadLastHandId() throws IOException {
    CmdLoadLastHandId cmd = new CmdLoadLastHandId();

    Connection db = getDbConnection();
    cmd.setDbConnection(db);
    cmd.execute();
    try {
      db.close();
    } catch (SQLException e) {
      throw new IOException(e.getMessage());
    }
  }

  public static void initPlayersList() throws IOException {
    CmdReadPlayers cmd = new CmdReadPlayers();
    Connection db = getDbConnection();
    cmd.setDbConnection(db);
    cmd.execute();
    players = cmd.getList();
    try {
      db.close();
    } catch (SQLException e) {
      throw new IOException(e.getMessage());
    }
  }

  public static void initNotes() throws IOException {
    CmdLoadNotes cmd = new CmdLoadNotes();
    Connection db = getDbConnection();
    cmd.setDbConnection(db);
    cmd.execute();
    try {
      db.close();
    } catch (SQLException e) {
      throw new IOException(e.getMessage());
    }
  }

  public static void initPlayersClub() throws IOException {
    CmdReadPlayersClub cmd = new CmdReadPlayersClub();
    Connection db = getDbConnection();
    cmd.setDbConnection(db);
    cmd.execute();
    try {
      db.close();
    } catch (SQLException e) {
      throw new IOException(e.getMessage());
    }
  }

  public static void initPlayersStats() throws IOException {
    CmdReadPlayersStats cmd = new CmdReadPlayersStats();
    Connection db = getDbConnection();
    cmd.setDbConnection(db);
    cmd.execute();
    try {
      db.close();
    } catch (SQLException e) {
      throw new IOException(e.getMessage());
    }
  }

  public static void initBuddies() throws IOException {
    CmdLoadBuddyList cmd = new CmdLoadBuddyList();
    Connection db = getDbConnection();
    cmd.setDbConnection(db);
    cmd.execute();
    try {
      db.close();
    } catch (SQLException e) {
      throw new IOException(e.getMessage());
    }
  }

  public static void initFeedBackTopics() throws IOException {
    CmdLoadFeedBackTopics cmd = new CmdLoadFeedBackTopics();
    Connection db = getDbConnection();
    cmd.setDbConnection(db);
    cmd.execute();
    try {
      db.close();
    } catch (SQLException e) {
      throw new IOException(e.getMessage());
    }
  }

  public static FeedBackTopics getFeedBackTopics() {
    return feedBackTopics;
  }

  public static void main(String[] args) throws Exception {
    try {
      if (CommonLogger.getLogger().isDebugEnabled()) {
        CommonLogger.getLogger().debug("Starting server ...");
      }

      PokerSettings.loadSettings();

      DbConnectionPool.init();

      MultiTournamentPayoutTable.getInstance();
      loadLastHandId();
      initPlayersList();
      initDesksList();
      initTournamentsList();
      initNotes();
      initPlayersClub();
      initPlayersStats();
      initBuddies();
      initFeedBackTopics();

      Thread.sleep(5000L);

      startServerThreads();

      AdminServer.startAdminSocketServer();

      startStatsCalculator(60000L);
      startWaitingListUpdater(1000L);
      startTotalStatsCalculator(5000L);

      DesksCommandProcessor.startTimer();

      SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy H:mm:ss:SSS", Locale.ENGLISH);

      System.out.println("Server started at " + formatter.format(new Date()));
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
    try
    {
      ServerSocket server = new ServerSocket(PORT);
      while (true) {
        Socket connection = null;
        try {
          connection = server.accept();
          addConnection(connection);
        } catch (IOException e) {
          System.err.println(e.getMessage());
        }
      }
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  public static void startServerThreads() {
    for (int i = 0; i < 500; i++) {
      Server server = new Server();
      server.start();
    }
  }

  public static void setServerPort(int port) {
    PORT = port;
  }

  public static Connection getDbConnection() {
    Connection dbConn = null;
    try {
      dbConn = DbConnectionPool.getDbConnection();
    } catch (Exception e) {
      CommonLogger.getLogger().warn("Can't connect:", e);
    }
    return dbConn;
  }

  public static String readClientInput(InputStream in) throws IOException {
    StringBuffer request = new StringBuffer();
    while (true) {
      int c = in.read();
      if ((c == 13) || (c == 10) || (c == -1)) {
        break;
      }
      request.append((char)c);
    }
    in.skip(in.available());
    return request.toString();
  }

  public static void startStatsCalculator(long delay) {
    TimerTask task = new ReculculateStatsTimerTask(null);
    Timer timer = new Timer();
    timer.schedule(task, delay, delay);
  }

  public static void startTotalStatsCalculator(long delay) {
    TimerTask task = new TotalStatsTimerTask(null);
    Timer timer = new Timer();
    timer.schedule(task, delay, delay);
  }

  public static void startWaitingListUpdater(long delay) {
    TimerTask task = new UpdateWaitingListTimerTask(null);
    Timer timer = new Timer();
    timer.schedule(task, delay, delay);
  }

  private static class UpdateWaitingListTimerTask extends TimerTask
  {
    public void run()
    {
      try
      {
        Server.waitingList.update(Server.desks);
      } catch (Exception ex) {
        Log.out("CLASS : UpdateWaitingListTimerTask - METHOD : update - ERROR: " + ex.getMessage());

        ex.printStackTrace();
      }
    }
  }

  public static class TotalStats
  {
    private int activeTables = 0;
    private int totalPlayers = 0;
    private int activePlayers = 0;
    private int holdemPlayers = 0;
    private int noLimitPlayers = 0;
    private int tournamentPlayers = 0;
    private int nonEmptyTables = 0;
    private BigDecimal maxPot = new BigDecimal(0);

    public int getActiveTables() {
      return activeTables;
    }

    public void setActiveTables(int activeTables) {
      this.activeTables = activeTables;
    }

    public int getTotalPlayers() {
      return totalPlayers;
    }

    public void setTotalPlayers(int totalPlayers) {
      this.totalPlayers = totalPlayers;
    }

    public int getActivePlayers() {
      return activePlayers;
    }

    public void setActivePlayers(int activePlayers) {
      this.activePlayers = activePlayers;
    }

    public int getHoldemPlayers() {
      return holdemPlayers;
    }

    public void setHoldemPlayers(int holdemPlayers) {
      this.holdemPlayers = holdemPlayers;
    }

    public int getNoLimitPlayers() {
      return noLimitPlayers;
    }

    public void setNoLimitPlayers(int noLimitPlayers) {
      this.noLimitPlayers = noLimitPlayers;
    }

    public int getTournamentPlayers() {
      return tournamentPlayers;
    }

    public void setTournamentPlayers(int tournamentPlayers) {
      this.tournamentPlayers = tournamentPlayers;
    }

    public int getNonEmptyTables() {
      return nonEmptyTables;
    }

    public void setNonEmptyTables(int nonEmptyTables) {
      this.nonEmptyTables = nonEmptyTables;
    }

    public BigDecimal getMaxPot() {
      return maxPot;
    }

    public void setMaxPot(BigDecimal maxPot) {
      this.maxPot = maxPot;
    }
  }

  private static class TotalStatsTimerTask extends TimerTask
  {
    public void run()
    {
      int activeTables = 0;
      int nonEmptyTables = 0;
      int totalPlayers = 0;
      int activePlayers = 0;
      int holdemPlayers = 0;
      int noLimitPlayers = 0;
      int tournamentPlayers = 0;

      synchronized (Server.players) {
        totalPlayers = Server.players.size();
      }

      Iterator it = Server.desks.iterator();
      synchronized (Server.desks) {
        activeTables = Server.desks.size();

        while (it.hasNext()) {
          Desk desk = (Desk)it.next();
          int count = desk.getPlacesList().getPlayersCount();
          activePlayers += count;
          if (count > 0) {
            nonEmptyTables++;
          }
          if (desk.getPokerType() == 1) {
            holdemPlayers += count;
          }
          if (desk.getLimitType() == 2) {
            noLimitPlayers += count;
          }
        }
      }

      List list = Tournament.getTournamentsList();
      synchronized (list) {
        int size = list.size();
        for (int $i = 0; $i < size; $i++) {
          Tournament t = (Tournament)list.get($i);
          synchronized (t.getPlayersList()) {
            tournamentPlayers += t.getPlayersList().size();
          }
        }
      }

      synchronized (Server.totalStats) {
        Server.totalStats.setActiveTables(activeTables);
        Server.totalStats.setTotalPlayers(totalPlayers);
        Server.totalStats.setActivePlayers(activePlayers);
        Server.totalStats.setHoldemPlayers(holdemPlayers);
        Server.totalStats.setNoLimitPlayers(noLimitPlayers);
        Server.totalStats.setTournamentPlayers(tournamentPlayers);
        Server.totalStats.setNonEmptyTables(nonEmptyTables);
      }
    }
  }

  private static class ReculculateStatsTimerTask extends TimerTask
  {
    public void run()
    {
      Iterator it = Server.desks.iterator();
      while (it.hasNext()) {
        Desk desk = (Desk)it.next();

        if (desk.getPlacesList().getPlayersCount() > 0)
          synchronized (desk.getStats()) {
            desk.getStats().deleteOldHands(System.currentTimeMillis());
            desk.getStats().recalculate();
          }
      }
    }
  }
}