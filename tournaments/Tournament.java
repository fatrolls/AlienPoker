package tournaments;

import commands.safeupdaters.AddWinnerToHistory;
import defaultvalues.DefaultValue;
import game.Desk;
import game.Game;
import game.Place;
import game.PlacesList;
import game.Player;
import game.amounts.PlayerAmount;
import game.desk.TableNameRandomizer;
import game.messages.CommonStateMessagesList;
import game.playerclub.ClubPlayers;
import game.playerclub.PlayersClub;
import game.speed.GameSpeedFactory;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import server.XMLFormatable;
import tournaments.payout.MultiTournamentPayoutTable;
import tournaments.stakes.StakesStructure;
import tournaments.winners.PotentialLoosers;
import utils.CommonLogger;
import utils.Log;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class Tournament
  implements Runnable, XMLFormatable
{
  private static final LinkedList tournamentsList = new LinkedList();
  private static int lastTourDeskId = 0;
  Logger log;
  public static final int STATUS_ANNOUNCED = 0;
  public static final String STATUS_ANNOUNCED_STRING = "Announced";
  public static final int STATUS_REGISTERING = 1;
  public static final String STATUS_REGISTERING_STRING = "Registering";
  public static final int STATUS_PLAYING = 2;
  public static final String STATUS_PLAYING_STRING = "Playing";
  public static final int STATUS_FINISHED = 3;
  public static final String STATUS_FINISHED_STRING = "Finished";
  public static final int STATUS_CANCELED = 4;
  public static final String STATUS_CANCELED_STRING = "Cancelled";
  public static final int STATUS_PAUSED = 5;
  public static final String STATUS_PAUSED_STRING = "Paused";
  public static final int STATUS_ONBREAK = 6;
  public static final String STATUS_ONBREAK_STRING = "On Break";
  public static final int STATUS_SEATING = 7;
  public static final String STATUS_SEATING_STRING = "Seating";
  public static final int STATUS_WAITING_FOR_ONE = 11;
  public static final String STATUS_WAITING_FOR_ONE_STRING = "Waiting for 1 player";
  public static final int STATUS_WAITING_FOR_TWO = 12;
  public static final String STATUS_WAITING_FOR_TWO_STRING = "Waiting for 2 players";
  public static final int STATUS_WAITING_FOR_THREE = 13;
  public static final String STATUS_WAITING_FOR_THREE_STRING = "Waiting for 3 players";
  public static final int STATUS_WAITING_FOR_FOUR = 14;
  public static final String STATUS_WAITING_FOR_FOUR_STRING = "Waiting for 4 players";
  public static final int STATUS_WAITING_FOR_FIVE = 15;
  public static final String STATUS_WAITING_FOR_FIVE_STRING = "Waiting for 5 players";
  public static final int STATUS_WAITING_FOR_SIX = 16;
  public static final String STATUS_WAITING_FOR_SIX_STRING = "Waiting for 6 players";
  public static final int STATUS_WAITING_FOR_SEVEN = 17;
  public static final String STATUS_WAITING_FOR_SEVEN_STRING = "Waiting for 7 players";
  public static final int STATUS_WAITING_FOR_EIGHT = 18;
  public static final String STATUS_WAITING_FOR_EIGHT_STRING = "Waiting for 8 players";
  public static final int STATUS_WAITING_FOR_NINE = 19;
  public static final String STATUS_WAITING_FOR_NINE_STRING = "Waiting for 9 players";
  public static final int STATUS_WAITING_FOR_TEN = 20;
  public static final String STATUS_WAITING_FOR_TEN_STRING = "Waiting for 10 players";
  public static final int STATUS_LEVEL_ONE = 31;
  public static final String STATUS_LEVEL_ONE_STRING = "Level I";
  public static final int STATUS_LEVEL_TWO = 32;
  public static final String STATUS_LEVEL_TWO_STRING = "Level II";
  public static final int STATUS_LEVEL_THREE = 33;
  public static final String STATUS_LEVEL_THREE_STRING = "Level III";
  public static final int STATUS_LEVEL_FOUR = 34;
  public static final String STATUS_LEVEL_FOUR_STRING = "Level IV";
  public static final int STATUS_LEVEL_FIVE = 35;
  public static final String STATUS_LEVEL_FIVE_STRING = "Level V";
  public static final int STATUS_LEVEL_SIX = 36;
  public static final String STATUS_LEVEL_SIX_STRING = "Level VI";
  public static final int STATUS_LEVEL_SEVEN = 37;
  public static final String STATUS_LEVEL_SEVEN_STRING = "Level VII";
  public static final int STATUS_LEVEL_EIGHT = 38;
  public static final String STATUS_LEVEL_EIGHT_STRING = "Level VIII";
  public static final int STATUS_LEVEL_NINE = 39;
  public static final String STATUS_LEVEL_NINE_STRING = "Level IX";
  public static final int STATUS_LEVEL_TEN = 40;
  public static final String STATUS_LEVEL_TEN_STRING = "Level X";
  public static final int REG_STATUS_OPENED = 1;
  public static final int REG_STATUS_CLOSED = 2;
  public static final int CODE_SUCCESSFULLY_ADDED = 1;
  public static final int CODE_ALREADY_ADDED = 0;
  public static final int CODE_NOT_ENOUGH_MONEY = 2;
  public static final int CODE_TOURNAMENT_ALREADY_STARTED = 3;
  public static final int CODE_BAD_DESK = 4;
  public static final int CODE_PLACE_UNAVAILABLE = 5;
  public static final int CODE_REGISTRATION_IS_CLOSE = 6;
  public static final int CODE_NOT_ENOUGH_DIRTY_POINTS = 7;
  public static final int TOURNAMENT_TYPE_MULTI = 1;
  public static final int TOURNAMENT_TYPE_MINI = 2;
  public static final int TOURNAMENT_TYPE_ONE_ON_ONE = 3;
  public static final int TOURNAMENT_TYPE_TEAM = 4;
  public static final int TOURNAMENT_TYPE_COMPLEX_SIT_AND_GO = 5;
  public static final int TOURNAMENT_SUB_TYPE_ALL = 1;
  public static final long CASHING_DELAY_MILLIS = 2000L;
  public static final int DEFAULT_MIN_PLAYERS_TO_STOP_TOURNMANET = 1;
  public static final int YOURSTATUS_REGISTERED = 1;
  public static final String YOURSTATUS_REGISTEREDD = "Registered";
  public static final int YOURSTATUS_NOT_REGISTERED = 0;
  public static final String YOURSTATUS_NOT_REGISTEREDD = "Not Registered";
  public static final String OUT_PARAM_TAG_NAME = "TT";
  public static final String OUT_PARAM_TOURNAMENT_TYPE = "TTYPE";
  public static final String OUT_PARAM_TOURN_NAME = "NM";
  public static final String OUT_PARAM_TOURN_CHIPS = "CP";
  public static final String OUT_PARAM_PRIZE_POOL = "POOL";
  public static final String OUT_PARAM_COUNT = "CNT";
  public static final String TAG_NAME_PRIZES = "PZ";
  public static final String TAG_NAME_WINNERED_PLACE = "WPL";
  public static final String OUT_PARAM_PLAYER_AMOUNT = "PLA";
  public static final String OUT_PARAM_CURRENT_LEVEL = "CLVL";
  public static final String OUT_PARAM_YOUR_STATUS = "YST";
  public static final String OUT_PARAM_YOUR_STATUSD = "YSTD";
  public static final String OUT_PARAM_PLAYERS_ELIMINATED = "PLE";
  public static final String TAG_STACKS = "STCS";
  public static final String OUT_PARAM_STACK_SMALL = "SML";
  public static final String OUT_PARAM_STACK_AVG = "AVG";
  public static final String OUT_PARAM_STACK_BIG = "BIG";
  public static final String TAG_NEXT_LEVEL = "NLVL";
  public static final String OUT_PARAM_SMALL_BLIND = "SB";
  public static final String OUT_PARAM_BIG_BLIND = "BB";
  public static final String OUT_PARAM_ANTE = "ANTE";
  public static final String OUT_PARAM_NEXT_BREAK = "NBR";
  public static final String OUT_PARAM_NEXT_BREAK_TIME = "NBT";
  public static final String OUT_PARAM_START_TIME = "STT";
  public static final String OUT_PARAM_RUNNING_TIME = "RN";
  public static final String OUT_PARAM_BUYIN = "BYIN";
  public static final String OUT_PARAM_REBUYS = "RBS";
  public static final String OUT_PARAM_ADDONS = "ADS";
  public static final String OUT_PARAM_FEE = "FEE";
  public static final String OUT_PARAM_LEVEL_TIME = "LVLT";
  public static final String OUT_PARAM_LEVEL_TIME_REMAINING = "LVLI";
  public static final String OUT_PARAM_REGISTRATION = "RGS";
  public static final String OUT_PARAM_REGISTRATIOND = "RGSD";
  public static final String OUT_PARAM_STATUS = "STS";
  public static final String OUT_PARAM_WATCHING = "WTC";
  public static final String TAG_BUYIN_POOL = "BIP";
  public static final String OUT_BUYINPOOL_AMOUNT_PARAM = "BIA";
  public static final String OUT_BUYINPOOL_PLACES_PAID_PARAM = "PPD";
  public static final String OUT_BUYINPOOL_ENTRIES_PARAM = "ENT";
  public static final String OUT_BUYINPOOL_REBUYS_PARAM = "RBS";
  public static final String OUT_BUYINPOOL_ADDONS_PARAM = "ADS";
  public static final String OUT_MIN_PLS_TO_START_PARAM = "MPTS";
  public static final String OUT_TEAMS_QTY_PARAM = "TQTY";
  public static final String OUT_FREEROLL_PARAM = "FRRL";
  public static final String OUT_PLS_IN_TEAM_PARAM = "PLST";
  public static final String TAG_DESK = "DESK";
  public static final String TAG_PARAM_PLAYERS_BIG = "BIG";
  public static final String TAG_PARAM_PLAYERS_SMALL = "SMALL";
  public static final String TAG_PARAM_DESK_NAME = "NAME";
  public static final String OUT_PARAM_SMALL_DESKS_TAG = "SDESKS";
  public static final String TAG_PARAM_DESK_ID = "ID";
  public static final String OUT_PARAM_PLAYERS = "PLAYERS";
  public static final String TAG_PLAYER = "PL";
  public static final String TAG_PARAM_RANK = "RANK";
  public static final String TAG_PARAM_TEAMID = "TEID";
  public static final String TAG_PARAM_NAME = "NAME";
  public static final String TAG_PARAM_AMOUNT = "AMOUNT";
  public static final String TAG_PARAM_PLAYER_DESK = "DESK";
  public static final String TAG_NAME_TOURNAMENT = "TOURNAMENT";
  public static final String TAG_PARAM_SHORT_TOUR_ID = "ID";
  public static final String TAG_PARAM_SHORT_NAME = "NAME";
  public static final String TAG_PARAM_SHORT_GAME = "GAME";
  public static final String TAG_PARAM_SHORT_LIMIT = "LIMIT";
  public static final String TAG_PARAM_SHORT_BUYIN = "BUYIN";
  public static final String TAG_PARAM_SHORT_PLAYERS = "PLAYERS";
  public static final String TAG_PARAM_START_DATE = "DSTART";
  public static final String TAG_PARAM_STARTS = "START";
  public static final String TAG_PARAM_STATUS = "STATUS";
  public static final String TAG_PARAM_NEXT_LEVEL_TIME = "NEXT_LEVEL_TIME";
  public static final String TAG_NAME_SHORT_TOURNAMENTS = "TOURNAMENTS";
  public static final String TAG_LEVEL = "CURRENTLVL";
  public static final String TAG_PARAM_PLAYER_STATUS = "STATUS";
  public static final long TOURNAMENT_PAUSE = 4000L;
  public static final String OUT_PARAM_STATUSD = "STATUSD";
  public static final String OUT_PARAM_SMALL_DESKS_TOUR_ID = "TID";
  private static final String TAG_PARAM_WINNERED_AMOUNT = "WAMOUNT";
  protected FeeList feeList = new FeeList(this);
  protected BigDecimal fee = DefaultValue.ZERO_BIDECIMAL;
  protected final ArrayList playersList = new ArrayList();
  protected final ArrayList desksList = new ArrayList();
  protected final TournamentWinners tournamentWinners = new TournamentWinners(this);
  protected TournamentLevels tournamentLevels = new TournamentLevels(this);
  protected PotentialLoosers potentialLoosers = new PotentialLoosers();
  protected int ID;
  protected int tournamentType;
  protected int speedType = 0;
  protected int subType = 1;
  protected String name;
  protected int game;
  protected int gameType;
  protected final Date beginDate;
  protected final Date regDate;
  protected final BigDecimal buyIn;
  protected boolean reBuys;
  protected boolean addons;
  private int historyID = 0;
  private boolean isFreeRoll = false;
  protected BigDecimal freeRollPrizePool = DefaultValue.ZERO_BIDECIMAL;
  protected BigDecimal addonsPayment;
  protected BigDecimal reBuysPayment;
  protected BigDecimal addonsAmount;
  protected BigDecimal reBuysAmount;
  protected int reBuysQty = 1;
  protected int addonsQty = 1;
  protected final List currentAddonPlayers = new LinkedList();
  protected final HashMap currentReBuysPlayers = new HashMap();
  protected int status = 0;
  protected int regStatus = 1;

  protected long timeOnLevel = 3600000L;
  protected long breakPeriod;
  protected long breakLength = 300000L;
  protected Date nextLevelTime = new Date();
  protected int currentLevel = 1;
  protected int increaseLevelAfter;
  protected int maxPlayersAtTheTable;
  protected int minPlayerRate = 0;
  protected int moneyType;
  protected BigDecimal maxBet = new BigDecimal(0);
  protected BigDecimal minBet = new BigDecimal(0);
  protected BigDecimal ante = new BigDecimal(0);
  protected BigDecimal bringIn = new BigDecimal(1);
  protected BigDecimal tournamentAmount = new BigDecimal(0);
  protected BigDecimal currentMaxBet = new BigDecimal(0);
  protected BigDecimal currentMinBet = new BigDecimal(0);
  protected BigDecimal currentAnte = new BigDecimal(0);
  protected BigDecimal currentBringIn = new BigDecimal(0);
  protected int minDeskPlayersCount;
  protected int minStartPlayersCount = 3;
  protected int teamsQty;
  protected int playersInTeam;
  protected ParametersForReset parametersForReset = null;
  protected int playersToStop = 1;
  protected Date nextBreakTime = new Date();

  protected Timer timer = new Timer();
  protected String cashedDesksXML = "";
  protected String cashedPlayersXML = "";
  protected String cashedPrizeTableXML = "";
  protected BigDecimal cashedSmallStacks = new BigDecimal(0);
  protected BigDecimal cashedBigStacks = new BigDecimal(0);
  protected BigDecimal cashedAvgStacks = new BigDecimal(0);

  protected boolean begin = false;
  protected boolean end = false;
  protected Iterator deskNamesIterator = new TableNameRandomizer().iterator();

  public Tournament(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, BigDecimal paramBigDecimal1, BigDecimal paramBigDecimal2, BigDecimal paramBigDecimal3, Date paramDate1, Date paramDate2, BigDecimal paramBigDecimal4, int paramInt7, long paramLong1, long paramLong2, long paramLong3, int paramInt8, int paramInt9, int paramInt10, BigDecimal paramBigDecimal5, BigDecimal paramBigDecimal6, BigDecimal paramBigDecimal7, int paramInt11, int paramInt12)
  {
    ID = paramInt1;
    name = paramString;
    tournamentType = paramInt2;
    subType = paramInt3;
    game = paramInt4;
    gameType = paramInt5;
    moneyType = paramInt6;
    tournamentAmount = paramBigDecimal1;
    minBet = paramBigDecimal3;
    maxBet = paramBigDecimal2;
    beginDate = paramDate1;
    regDate = paramDate2;
    buyIn = paramBigDecimal4;
    addonsPayment = paramBigDecimal4;
    reBuysPayment = paramBigDecimal4;
    increaseLevelAfter = paramInt7;
    timeOnLevel = paramLong1;
    breakPeriod = paramLong2;
    breakLength = paramLong3;
    maxPlayersAtTheTable = paramInt8;
    reBuys = (paramInt9 > 0);
    reBuysQty = paramInt9;
    addons = (paramInt10 > 0);
    addonsQty = paramInt10;
    addonsAmount = paramBigDecimal5;
    reBuysAmount = paramBigDecimal6;
    fee = paramBigDecimal7;
    minStartPlayersCount = paramInt11;
    speedType = paramInt12;

    log = Logger.getLogger(Tournament.class);

    parametersForReset = new ParametersForReset(this, paramInt1, paramString, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramBigDecimal1, paramBigDecimal2, paramBigDecimal3, paramDate1, paramDate2, paramBigDecimal4, paramInt7, paramLong1, paramLong2, paramLong3, paramInt8, paramInt9, paramInt10, paramBigDecimal5, paramBigDecimal6, paramBigDecimal7, paramInt11, paramInt12);

    CashingTimerTask localCashingTimerTask = new CashingTimerTask(null);
    timer.schedule(localCashingTimerTask, new Date(), 2000L);

    synchronized (tournamentsList) {
      tournamentsList.add(this);
    }

    begin = false;
  }

  public BigDecimal getAddonsPayment() {
    return addonsPayment;
  }

  public BigDecimal getAddonsAmount() {
    return addonsAmount;
  }

  public void setAddonsAmount(BigDecimal paramBigDecimal) {
    addonsAmount = paramBigDecimal;
  }

  public ParametersForReset getParametersForReset() {
    return parametersForReset;
  }

  public void setParametersForReset(ParametersForReset paramParametersForReset) {
    parametersForReset = paramParametersForReset;
  }

  public HashMap getCurrentReBuysPlayers() {
    return currentReBuysPlayers;
  }

  public List getCurrentAddonPlayers() {
    return currentAddonPlayers;
  }

  public int getHistoryID() {
    return historyID;
  }

  public void setHistoryID(int paramInt) {
    historyID = paramInt;
  }

  public BigDecimal getReBuysPayment() {
    return reBuysPayment;
  }

  public BigDecimal getReBuysAmount() {
    return reBuysAmount;
  }

  public FeeList getFeeList() {
    return feeList;
  }

  public BigDecimal getFee() {
    return fee;
  }

  public void setFee(BigDecimal paramBigDecimal) {
    fee = paramBigDecimal;
  }

  public int join(Player paramPlayer, int paramInt) {
    return join(paramPlayer);
  }

  public int join(Player paramPlayer)
  {
    BigDecimal localBigDecimal = buyIn.add(fee).setScale(2, 5);
    int i;
    synchronized (paramPlayer) {
      if (paramPlayer.getAmount(getMoneyType()).floatValue() < localBigDecimal.floatValue()) {
        return 2;
      }

      synchronized (this) {
        if ((!begin) && (regStatus == 1))
        {
          Fee localFee = new Fee(paramPlayer, this, localBigDecimal);
          i = feeList.addFee(localFee);

          if (i == 1) {
            paramPlayer.decreaseAmount(localBigDecimal, getMoneyType());
            paramPlayer.getPlayerAmount().recordTournamentAmount(this);
            calculateDirtyPoints(paramPlayer);
            RankingPlayer localRankingPlayer = new RankingPlayer(paramPlayer, tournamentAmount, null);

            localRankingPlayer.setRank(playersList.size() + 1);
            playersList.add(localRankingPlayer);
          }
        }
        else if (begin) {
          i = 3;
        } else {
          i = 6;
        }
      }

    }

    if (i == 1) {
      updateCashedXML();
    }

    return i;
  }

  public void calculateDirtyPoints(Player paramPlayer)
  {
  }

  public boolean canRequestRebuy(Desk paramDesk, Player paramPlayer)
  {
    return false;
  }

  public boolean canRequestAddon(Desk paramDesk)
  {
    return false;
  }

  public void increaseRebuys(Player paramPlayer)
  {
  }

  public void increaseAddons(Desk paramDesk)
  {
  }

  public ArrayList getPlayersList()
  {
    return playersList;
  }

  public int getSubType() {
    return subType;
  }

  public void setSubType(int paramInt) {
    subType = paramInt;
  }

  public TournamentLevels getTournamentLevels() {
    return tournamentLevels;
  }

  public int getSpeedType() {
    return speedType;
  }

  public void setSpeedType(int paramInt) {
    speedType = paramInt;
  }

  public void run()
  {
  }

  protected boolean checkStratUp()
  {
    synchronized (playersList) {
      if (playersList.size() < minStartPlayersCount) {
        return false;
      }
    }

    return true;
  }

  protected void seatPlayers(ArrayList paramArrayList1, ArrayList paramArrayList2, int paramInt)
  {
    int i = paramArrayList1.size();
    int j = paramArrayList2.size();

    if (i * paramInt - j < 0) {
      Log.out("Tournament.seatPlayers : Error : Free lcaces count < 0");
    } else {
      ArrayList localArrayList = new ArrayList(paramArrayList2.size());
      localArrayList.addAll(paramArrayList2);
      Collections.shuffle(localArrayList);

      int k = 1;

      while (localArrayList.size() > 0) {
        for (int m = 0; m < i; m++) {
          if (localArrayList.size() > 0) {
            RedistributablePlayer localRedistributablePlayer = (RedistributablePlayer)localArrayList.remove(0);
            Desk localDesk = (Desk)paramArrayList1.get(m);
            localDesk.seatPlayer(localRedistributablePlayer.getPlayer().getPlayer(), k, localRedistributablePlayer.getAmount());
          }

        }

        k++;
      }
    }
  }

  public int hashCode() {
    return ID;
  }

  public boolean equals(Object paramObject) {
    if (paramObject == null) {
      return false;
    }

    if ((paramObject instanceof Tournament)) {
      return ID == ((Tournament)paramObject).getID();
    }

    return false;
  }

  public int getGame() {
    return game;
  }

  public int getGameType() {
    return gameType;
  }

  public Date getBeginDate() {
    return beginDate;
  }

  public BigDecimal getBuyIn() {
    return buyIn;
  }

  public int getIncreaseLevelAfter() {
    return increaseLevelAfter;
  }

  public int getCurrentLevel() {
    return currentLevel;
  }

  public long getBreakPeriod() {
    return breakPeriod;
  }

  public void setBreakPeriod(long paramLong) {
    breakPeriod = paramLong;
  }

  public long getBreakLength() {
    return breakLength;
  }

  public void setBreakLength(long paramLong) {
    breakLength = paramLong;
  }

  public int getID() {
    return ID;
  }

  public Desk createTournamentDesk(int paramInt1, int paramInt2, BigDecimal paramBigDecimal1, BigDecimal paramBigDecimal2, BigDecimal paramBigDecimal3, BigDecimal paramBigDecimal4, int paramInt3, BigDecimal paramBigDecimal5, int paramInt4, int paramInt5)
  {
    Desk localDesk = new Desk();
    int i = bindTournamentDeskID();
    localDesk.setID(i);

    if (deskNamesIterator.hasNext()) {
      localDesk.setDeskName((String)deskNamesIterator.next());
    } else {
      localDesk.setDeskName("Table " + Math.random());
      Log.out("DeskNamesIterator: Error : noNextElement ");
    }

    localDesk.setMoneyType(paramInt1);
    localDesk.setLimitType(paramInt2);
    localDesk.setMinBet(paramBigDecimal2);
    localDesk.setMaxBet(paramBigDecimal1);
    localDesk.setAnte(paramBigDecimal3);
    localDesk.setBringIn(paramBigDecimal4);
    localDesk.setPokerType(paramInt3);
    localDesk.setMinAmount(paramBigDecimal5);
    localDesk.setMinPlayerRate(new BigDecimal(paramInt4));

    localDesk.setTournamentID(getID());
    localDesk.createPlaces(paramInt5);

    localDesk.setAnte(localDesk.getMinBet().divide(new BigDecimal(2), 2, 5));

    localDesk.startUpGame(GameSpeedFactory.getGameSpeed(speedType));

    return localDesk;
  }

  private static synchronized int bindTournamentDeskID()
  {
    lastTourDeskId += 1;

    return lastTourDeskId;
  }

  public boolean isReBuys() {
    return reBuys;
  }

  public boolean isAddons() {
    return addons;
  }

  public boolean hasPlayer(Player paramPlayer) {
    synchronized (playersList) {
      int i = playersList.size();

      for (int j = 0; j < i; j++) {
        RankingPlayer localRankingPlayer = (RankingPlayer)playersList.get(j);

        if (localRankingPlayer.getPlayer().getID() == paramPlayer.getID()) {
          return true;
        }
      }

      return false;
    }
  }

  public Desk getPlayerDesk(Player paramPlayer) {
    synchronized (playersList) {
      int i = playersList.size();

      for (int j = 0; j < i; j++) {
        RankingPlayer localRankingPlayer = (RankingPlayer)playersList.get(j);

        if (localRankingPlayer.getPlayer().getID() == paramPlayer.getID()) {
          return localRankingPlayer.getDesk();
        }
      }

      return null;
    }
  }

  public BigDecimal getCurrentBringIn() {
    return currentBringIn;
  }

  public void setCurrentBringIn(BigDecimal paramBigDecimal) {
    currentBringIn = paramBigDecimal;
  }

  public long getTimeOnLevel() {
    return timeOnLevel;
  }

  public int getMaxPlayersAtTheTable() {
    return maxPlayersAtTheTable;
  }

  public int getTournamentType() {
    return tournamentType;
  }

  public ArrayList createTournamentsDesksTables(int paramInt1, int paramInt2, int paramInt3, BigDecimal paramBigDecimal1, BigDecimal paramBigDecimal2, BigDecimal paramBigDecimal3, BigDecimal paramBigDecimal4, int paramInt4, BigDecimal paramBigDecimal5, int paramInt5, int paramInt6)
  {
    ArrayList localArrayList = new ArrayList(paramInt1);

    for (int i = 0; i < paramInt1; i++) {
      localArrayList.add(createTournamentDesk(paramInt2, paramInt3, paramBigDecimal1, paramBigDecimal2, paramBigDecimal3, paramBigDecimal4, paramInt4, paramBigDecimal5, paramInt5, paramInt6));
    }

    return localArrayList;
  }

  public int getMinPlayerRate() {
    return minPlayerRate;
  }

  public void setMinPlayerRate(int paramInt) {
    minPlayerRate = paramInt;
  }

  public int getMoneyType() {
    return moneyType;
  }

  public BigDecimal getMaxBet() {
    return maxBet;
  }

  public BigDecimal getMinBet() {
    return minBet;
  }

  public BigDecimal getCurrentMaxBet() {
    return currentMaxBet;
  }

  public BigDecimal getCurrentMinBet() {
    return currentMinBet;
  }

  public BigDecimal getCurrentAnte() {
    return currentAnte;
  }

  public ArrayList getDesksList() {
    return desksList;
  }

  public BigDecimal getTournamentAmount() {
    return tournamentAmount;
  }

  public Date getNextBreakTime() {
    return nextBreakTime;
  }

  public void setNextBreakTime(Date paramDate) {
    nextBreakTime = paramDate;
  }

  public String getDesksXML()
  {
    return cashedDesksXML;
  }

  public String getPlayersXML()
  {
    return cashedPlayersXML;
  }

  public void updateCashedXML()
  {
    HashMap localHashMap = new HashMap();

    BigDecimal localBigDecimal = new BigDecimal(0);
    int i = 0;
    Object localObject2;
    Object localObject4;
    Object localObject5;
    synchronized (desksList) {
      Iterator localIterator1 = desksList.iterator();

      while (localIterator1.hasNext()) {
        Desk localDesk = (Desk)localIterator1.next();
        localObject1 = new BigDecimal(3.402823466385289E+038D);
        localObject2 = new BigDecimal(1.401298464324817E-045D);
        int m = 0;

        synchronized (localDesk) {
          localObject4 = localDesk.getPlacesList().allPlacesIterator();

          while (((Iterator)localObject4).hasNext()) {
            localObject5 = (Place)((Iterator)localObject4).next();

            if (((Place)localObject5).isBusy()) {
              Player localPlayer = ((Place)localObject5).getPlayer();

              RankingPlayer localRankingPlayer = getRankingPlayer(localPlayer);
              localRankingPlayer.setDesk(localDesk);
              localRankingPlayer.setAmount(((Place)localObject5).getAmount());
              localRankingPlayer.setStatus(1);

              if (((Place)localObject5).getAmount().floatValue() < ((BigDecimal)localObject1).floatValue()) {
                localObject1 = ((Place)localObject5).getAmount();
              }

              if (((Place)localObject5).getAmount().floatValue() > ((BigDecimal)localObject2).floatValue()) {
                localObject2 = ((Place)localObject5).getAmount();
              }

              m++;
            }
          }

          localObject5 = new ArrayList(2);
          ((ArrayList)localObject5).add(((BigDecimal)localObject2).floatValue() == 1.4E-45F ? DefaultValue.ZERO_BIDECIMAL : localObject2);

          ((ArrayList)localObject5).add(((BigDecimal)localObject1).floatValue() == 3.4028235E+38F ? DefaultValue.ZERO_BIDECIMAL : localObject1);

          ((ArrayList)localObject5).add(new Integer(m));
          ((ArrayList)localObject5).add(localDesk.getDeskName());
          localHashMap.put(new Integer(localDesk.getID()), localObject5);
        }

      }

    }

    synchronized (playersList) {
      Collections.sort(playersList, new RankingListComparator(null));

      int j = playersList.size();

      for (int k = 0; k < j; k++) {
        localObject1 = (RankingPlayer)playersList.get(k);

        if (((RankingPlayer)localObject1).getStatus() == 0)
        {
          break;
        }
        ((RankingPlayer)localObject1).setRank(k + 1);
      }

      Collections.sort(playersList, new RankingListComparator(null));

      j = playersList.size();

      for (k = 0; k < j; k++) {
        localObject1 = (RankingPlayer)playersList.get(k);

        if (k == 0) {
          cashedBigStacks = ((RankingPlayer)localObject1).getAmount();
        }
        else if (((RankingPlayer)localObject1).getStatus() == 1) cashedSmallStacks = ((RankingPlayer)localObject1).getAmount();

        localBigDecimal = localBigDecimal.add(((RankingPlayer)localObject1).getAmount());

        if (((RankingPlayer)localObject1).getAmount().floatValue() <= 0.0F) continue; i++;
      }

    }

    ??? = new BigDecimal(String.valueOf(i));

    if (((BigDecimal)???).floatValue() > 0.0F)
      cashedAvgStacks = localBigDecimal.divide((BigDecimal)???, 7);
    else {
      cashedAvgStacks = new BigDecimal(0);
    }

    XMLTag localXMLTag = new XMLTag("SDESKS");
    Iterator localIterator2 = localHashMap.entrySet().iterator();

    while (localIterator2.hasNext()) {
      localObject1 = (Map.Entry)localIterator2.next();

      localObject2 = new XMLTag("DESK");
      ArrayList localArrayList = (ArrayList)((Map.Entry)localObject1).getValue();

      ((XMLTag)localObject2).addParam("ID", ((Integer)((Map.Entry)localObject1).getKey()).intValue());

      ((XMLTag)localObject2).addParam("NAME", (String)localArrayList.get(3));
      ((XMLTag)localObject2).addParam("PLACES", maxPlayersAtTheTable);
      ((XMLTag)localObject2).addParam("PLAYERS", ((Integer)localArrayList.get(2)).intValue());

      ((XMLTag)localObject2).addParam("BIG", ((BigDecimal)localArrayList.get(0)).floatValue());

      ((XMLTag)localObject2).addParam("SMALL", ((BigDecimal)localArrayList.get(1)).floatValue());

      ((XMLTag)localObject2).addParam("LTYPE", gameType);
      ((XMLTag)localObject2).addParam("PTYPE", game);
      ((XMLTag)localObject2).addParam("TID", ID);
      ((XMLTag)localObject2).addParam("SPEED", speedType);

      localXMLTag.addNestedTag((XMLTag)localObject2);
    }

    Object localObject1 = new XMLTag("PLAYERS");

    synchronized (playersList) {
      Object localObject3 = playersList.size();

      for (??? = 0; ??? < localObject3; ???++) {
        localObject4 = (RankingPlayer)playersList.get(???);

        localObject5 = new XMLTag("PL");
        ((XMLTag)localObject5).addParam("DESK", "" + ((RankingPlayer)localObject4).getDesk().getID());

        ((XMLTag)localObject5).addParam("RANK", ((RankingPlayer)localObject4).getRank());
        ((XMLTag)localObject5).addParam("NAME", ((RankingPlayer)localObject4).getPlayer().getLogin() + " (" + ((RankingPlayer)localObject4).getPlayer().getCountry() + ")");

        ((XMLTag)localObject5).addParam("STATUS", ((RankingPlayer)localObject4).getStatus());

        if (((RankingPlayer)localObject4).getStatus() == 1) {
          ((XMLTag)localObject5).addParam("AMOUNT", ((RankingPlayer)localObject4).getAmount().floatValue());
        }
        else {
          ((XMLTag)localObject5).addParam("WAMOUNT", ((RankingPlayer)localObject4).getWinneredAmount().floatValue());
        }

        ((XMLTag)localObject1).addNestedTag((XMLTag)localObject5);
      }

    }

    synchronized (this) {
      cashedDesksXML = localXMLTag.toString();
      cashedPlayersXML = ((XMLTag)localObject1).toString();
      cashedPrizeTableXML = getPrizeTableXML();
    }

    localXMLTag.invalidate();
    ((XMLTag)localObject1).invalidate();
  }

  public XMLTag toXMLTag() {
    return new XMLTag("TT");
  }

  public String toXML() {
    return toXML(null);
  }

  public String toXML(Player paramPlayer) {
    XMLDoc localXMLDoc = new XMLDoc();
    XMLTag localXMLTag1 = localXMLDoc.startTag("TT");

    localXMLTag1.addParam("ID", ID);
    localXMLTag1.addParam("TTYPE", tournamentType);
    localXMLTag1.addParam("NM", name);
    localXMLTag1.addParam("PTYPE", game);
    localXMLTag1.addParam("LTYPE", gameType);
    localXMLTag1.addParam("SPEED", speedType);
    localXMLTag1.addParam("MPTS", minStartPlayersCount);

    localXMLTag1.addParam("TQTY", teamsQty);
    localXMLTag1.addParam("PLST", playersInTeam);

    localXMLTag1.addParam("FRRL", isFreeRoll ? 1 : 0);

    synchronized (playersList) {
      localXMLTag1.addParam("PLAYERS", playersList.size());
    }

    if (paramPlayer != null) {
      int i = 0;

      synchronized (playersList) {
        int j = playersList.size();

        for (int k = 0; k < j; k++) {
          RankingPlayer localRankingPlayer = (RankingPlayer)playersList.get(k);

          if (localRankingPlayer.getPlayer().getID() == paramPlayer.getID()) {
            i = 1;
            localXMLTag1.addParam("YST", 1);

            localXMLTag1.addParam("YSTD", "Registered");

            break;
          }
        }
      }

      if (i == 0) {
        localXMLTag1.addParam("YST", 0);
        localXMLTag1.addParam("YSTD", "Not Registered");
      }
    }

    localXMLTag1.addParam("PLE", tournamentWinners.getWinnersList().size());

    XMLTag localXMLTag2 = new XMLTag("STCS");
    localXMLTag2.addParam("SML", cashedSmallStacks.floatValue());
    localXMLTag2.addParam("AVG", cashedAvgStacks.floatValue());
    localXMLTag2.addParam("BIG", cashedBigStacks.floatValue());

    localXMLTag1.addNestedTag(localXMLTag2);

    if ((status == 0) || (status == 1) || (status == 3) || (status == 4))
    {
      ??? = new XMLTag("CURRENTLVL");
      ((XMLTag)???).addParam("SB", "");
      ((XMLTag)???).addParam("BB", "");
      ((XMLTag)???).addParam("ANTE", "");

      localXMLTag1.addNestedTag((XMLTag)???);

      localObject2 = new XMLTag("NLVL");
      ((XMLTag)localObject2).addParam("SB", "");
      ((XMLTag)localObject2).addParam("BB", "");
      ((XMLTag)localObject2).addParam("ANTE", "");

      localXMLTag1.addNestedTag((XMLTag)localObject2);

      localXMLTag1.addParam("NBR", "");
      localXMLTag1.addParam("NBT", "");
    }
    else {
      ??? = new XMLTag("CURRENTLVL");

      ((XMLTag)???).addParam("SB", currentMinBet.toString());

      ((XMLTag)???).addParam("BB", currentMaxBet.toString());

      ((XMLTag)???).addParam("ANTE", currentAnte.toString());

      localXMLTag1.addNestedTag((XMLTag)???);

      localObject2 = new XMLTag("NLVL");
      ((XMLTag)localObject2).addParam("SB", StakesStructure.getSmallBlind(game, currentLevel + 2));

      ((XMLTag)localObject2).addParam("BB", StakesStructure.getBigBlind(game, currentLevel + 2));

      ((XMLTag)localObject2).addParam("ANTE", StakesStructure.getAnte(game, currentLevel + 2).floatValue());

      localXMLTag1.addNestedTag((XMLTag)localObject2);

      localXMLTag1.addParam("NBR", "" + (nextBreakTime.getTime() - beginDate.getTime()));

      localXMLTag1.addParam("NBT", "" + breakLength);
    }

    localXMLTag1.addParam("STT", "" + beginDate.getTime());

    if ((status != 0) && (status != 1) && (status != 3) && (status != 4))
    {
      long l = new Date().getTime() - beginDate.getTime();
      localXMLTag1.addParam("RN", "" + l);
    }

    localXMLTag1.addParam("BYIN", buyIn.floatValue());
    localXMLTag1.addParam("RBS", reBuys ? 1 : 0);
    localXMLTag1.addParam("ADS", addons ? 1 : 0);
    localXMLTag1.addParam("FEE", fee.floatValue());

    localXMLTag1.addParam("LVLT", "" + timeOnLevel);
    localXMLTag1.addParam("LVLI", "" + (nextLevelTime.getTime() - new Date().getTime()));

    localXMLTag1.addParam("RGS", regStatus);
    localXMLTag1.addParam("RGSD", regStatus == 1 ? "Opened" : "Closed");

    localXMLTag1.addParam("STS", status);
    localXMLTag1.addParam("STATUSD", convertTournamentStatusToString(status));

    localXMLTag1.addParam("WTC", 16);

    XMLTag localXMLTag3 = new XMLTag("BIP");
    localXMLTag3.addParam("BIA", getFeeList().getFeeAmount().floatValue());

    localXMLTag3.addParam("PPD", feeList.getSize());

    localXMLTag3.addParam("ENT", feeList.getSize());
    localXMLTag3.addParam("RBS", reBuysQty);
    localXMLTag3.addParam("ADS", addonsQty);

    localXMLTag1.addNestedTag(localXMLTag3);

    Object localObject2 = localXMLDoc.toString();
    localXMLDoc.invalidate();

    return (String)(String)localObject2;
  }

  public String getPrizeTableXML()
  {
    XMLTag localXMLTag = prizeTableToXMLTag();
    String str = localXMLTag.toString();
    localXMLTag.invalidate();

    return str;
  }

  public XMLTag prizeTableToXMLTag()
  {
    MultiTournamentPayoutTable localMultiTournamentPayoutTable = MultiTournamentPayoutTable.getInstance();
    int i;
    synchronized (playersList) {
      i = playersList.size();
    }

    return localMultiTournamentPayoutTable.toXMLTag(i);
  }

  public static List getTournamentsList() {
    return tournamentsList;
  }

  public static Tournament getTournamentByID(int paramInt) {
    synchronized (tournamentsList) {
      Iterator localIterator = tournamentsList.iterator();

      while (localIterator.hasNext()) {
        Tournament localTournament = (Tournament)localIterator.next();

        if (localTournament.getID() == paramInt) {
          return localTournament;
        }
      }
    }

    return null;
  }

  public static ArrayList getTournamentsByType(int paramInt)
  {
    ArrayList localArrayList;
    synchronized (tournamentsList) {
      localArrayList = new ArrayList(tournamentsList.size());

      Iterator localIterator = tournamentsList.iterator();

      while (localIterator.hasNext()) {
        Tournament localTournament = (Tournament)localIterator.next();

        if (localTournament.getTournamentType() == paramInt) {
          localArrayList.add(localTournament);
        }
      }
    }

    localArrayList.trimToSize();

    return localArrayList;
  }

  public static ArrayList getTournamentsByTypeAndGameType(int paramInt1, int paramInt2)
  {
    ArrayList localArrayList;
    synchronized (tournamentsList) {
      localArrayList = new ArrayList(tournamentsList.size());

      Iterator localIterator = tournamentsList.iterator();

      while (localIterator.hasNext()) {
        Tournament localTournament = (Tournament)localIterator.next();

        if ((localTournament.getTournamentType() == paramInt1) && (localTournament.getGameType() == paramInt2))
        {
          localArrayList.add(localTournament);
        }
      }
    }

    localArrayList.trimToSize();

    return localArrayList;
  }

  public String toDeskMenuXML(Player paramPlayer)
  {
    return "";
  }

  public static ArrayList getTournamentsByTypeAndSubType(int paramInt1, int paramInt2)
  {
    ArrayList localArrayList;
    synchronized (tournamentsList) {
      localArrayList = new ArrayList(tournamentsList.size());

      Iterator localIterator = tournamentsList.iterator();

      while (localIterator.hasNext()) {
        Tournament localTournament = (Tournament)localIterator.next();

        if ((localTournament.getTournamentType() == paramInt1) && (localTournament.getSubType() == paramInt2))
        {
          localArrayList.add(localTournament);
        }
      }
    }

    localArrayList.trimToSize();

    return localArrayList;
  }

  private void endTournament()
  {
    synchronized (desksList) {
      if (desksList.size() != 0);
    }
  }

  public boolean isBegin()
  {
    return begin;
  }

  public void dropDeskAfterSomeTime(Desk paramDesk)
  {
  }

  public synchronized int redistributePlayers(Game paramGame)
  {
    return 0;
  }

  public boolean needRedistributePlayers(Game paramGame) {
    return false;
  }

  public boolean canFinishTournament()
  {
    int i = 0;

    synchronized (playersList) {
      int j = playersList.size();

      for (int k = 0; k < j; k++) {
        RankingPlayer localRankingPlayer = (RankingPlayer)playersList.get(k);

        if (localRankingPlayer.getStatus() == 1) {
          i++;
        }
      }
    }

    return i <= playersToStop;
  }

  public static String getTournamentsXML(int paramInt)
  {
    ArrayList localArrayList;
    synchronized (tournamentsList) {
      localArrayList = new ArrayList(tournamentsList.size());
      localArrayList.addAll(tournamentsList);
    }

    ??? = new XMLTag("TOURNAMENTS");

    int i = localArrayList.size();

    for (int j = 0; j < i; j++) {
      Tournament localTournament = (Tournament)localArrayList.get(j);

      if ((paramInt == 0) || (localTournament.getTournamentType() == paramInt)) {
        synchronized (localTournament) {
          XMLTag localXMLTag = new XMLTag("TOURNAMENT");
          localXMLTag.addParam("ID", localTournament.getID());

          localXMLTag.addParam("NAME", localTournament.getName());
          localXMLTag.addParam("GAME", localTournament.getGame());
          localXMLTag.addParam("LIMIT", localTournament.getGameType());

          localXMLTag.addParam("BUYIN", localTournament.getBuyIn().floatValue());

          synchronized (localTournament.getPlayersList()) {
            localXMLTag.addParam("PLAYERS", localTournament.getPlayersList().size());
          }

          localXMLTag.addParam("DSTART", (float)localTournament.getBeginDate().getTime());

          localXMLTag.addParam("START", (float)(localTournament.getBeginDate().getTime() - new Date().getTime()));

          localXMLTag.addParam("STATUS", localTournament.getStatus());
          localXMLTag.addParam("SPEED", localTournament.getSpeedType());

          ((XMLTag)???).addNestedTag(localXMLTag);
        }
      }
    }

    String str = ((XMLTag)???).toString();
    ((XMLTag)???).invalidate();

    return (String)str;
  }

  public void recordAmount(Desk paramDesk)
  {
    synchronized (paramDesk) {
      synchronized (paramDesk.getPlacesList()) {
        Iterator localIterator = paramDesk.getPlacesList().iterator();

        while (localIterator.hasNext()) {
          Place localPlace = (Place)localIterator.next();
          potentialLoosers.addPlayerByPlace(localPlace);
        }
      }
    }
  }

  private ArrayList eliminateChoosenPlayers(Desk paramDesk, HashMap paramHashMap) {
    ArrayList localArrayList1 = new ArrayList();

    tournamentWinners.addWinners(paramHashMap);

    HashMap localHashMap = new HashMap();

    Iterator localIterator = paramHashMap.entrySet().iterator();
    Object localObject2;
    ArrayList localArrayList2;
    while (localIterator.hasNext()) {
      localObject1 = (Map.Entry)localIterator.next();
      localObject2 = (Player)((Map.Entry)localObject1).getKey();
      TournamentWinners.WinnerPlace localWinnerPlace = tournamentWinners.getWinnerPlace((Player)localObject2);

      if (localHashMap.containsKey(new Integer(localWinnerPlace.getPlace()))) {
        localArrayList2 = (ArrayList)localHashMap.get(new Integer(localWinnerPlace.getPlace()));

        localArrayList2.add(localWinnerPlace.getPlayer());
      } else {
        localArrayList2 = new ArrayList();
        localArrayList2.add(localWinnerPlace.getPlayer());
        localHashMap.put(new Integer(localWinnerPlace.getPlace()), localArrayList2);
      }

    }

    Object localObject1 = localHashMap.entrySet().iterator();

    while (((Iterator)localObject1).hasNext()) {
      localObject2 = (Map.Entry)((Iterator)localObject1).next();
      int i = ((Integer)((Map.Entry)localObject2).getKey()).intValue();
      localArrayList2 = (ArrayList)((Map.Entry)localObject2).getValue();

      BigDecimal localBigDecimal = new BigDecimal(0);

      for (int j = 0; j < localArrayList2.size(); j++) {
        localBigDecimal = localBigDecimal.add(getAmountByWinneredPlace(i + j)).setScale(2, 5);
      }

      localBigDecimal = localBigDecimal.divide(new BigDecimal(localArrayList2.size()), 2, 5);

      for (j = 0; j < localArrayList2.size(); j++) {
        Player localPlayer = (Player)localArrayList2.get(j);

        localPlayer.getPlayerAmount().deleteTournamentRecord(this);

        new Thread(new AddWinnerToHistory(this, i, localBigDecimal, localPlayer)).start();

        Place localPlace = paramDesk.getPlayerPlace(localPlayer);

        if (localPlace != null) {
          localArrayList1.add(new EliminatedPlayer(localPlayer, localBigDecimal, i));

          paramDesk.getGame().getPublicStateMessagesList().addCommonMessage(localPlayer.getLogin(), 102, localPlace.getNumber(), i, localBigDecimal, localPlayer.getID());

          paramDesk.getGame().notifyAboutLeaveDesk(localPlace);
          localPlace.free();

          log.info("Player Eliminated: #" + localPlayer.getID() + " " + localPlayer.getLogin() + " : WinneredAmount=" + localBigDecimal.floatValue());

          localPlayer.increaseAmount(localBigDecimal, getMoneyType());
        }

        System.out.println(localPlayer.getLogin() + " NOTIFYED " + i);
      }
    }

    return (ArrayList)(ArrayList)localArrayList1;
  }

  public synchronized ArrayList eliminatePlayers(Desk paramDesk)
  {
    HashMap localHashMap = new HashMap();

    synchronized (paramDesk)
    {
      Iterator localIterator = paramDesk.getPlacesList().allPlacesIterator();

      while (localIterator.hasNext()) {
        Place localPlace = (Place)localIterator.next();

        if ((localPlace.isBusy()) && (localPlace.getAmount().compareTo(new BigDecimal(0)) <= 0))
        {
          localHashMap.put(localPlace.getPlayer(), localPlace.getAmount());
        }

      }

    }

    return eliminateChoosenPlayers(paramDesk, localHashMap);
  }

  public ArrayList eliminateAllPlayers(Desk paramDesk) {
    HashMap localHashMap = new HashMap();

    synchronized (paramDesk)
    {
      Iterator localIterator = paramDesk.getPlacesList().allPlacesIterator();

      while (localIterator.hasNext()) {
        Place localPlace = (Place)localIterator.next();

        if (localPlace.isBusy()) {
          localHashMap.put(localPlace.getPlayer(), localPlace.getAmount());
        }

      }

    }

    return eliminateChoosenPlayers(paramDesk, localHashMap);
  }

  public ArrayList notifyTournamentAboutGameEnd(Game paramGame)
  {
    updateDeskLevel(paramGame.getDesk());

    suggestRebuys(paramGame.getDesk());

    ArrayList localArrayList = eliminatePlayers(paramGame.getDesk());

    if ((getTournamentType() == 1) || (getTournamentType() == 5))
    {
      updateRanking(localArrayList);
    }

    if (redistributePlayers(paramGame) > 0) {
      try
      {
        Thread.sleep(20000L);
      } catch (InterruptedException localInterruptedException) {
        localInterruptedException.printStackTrace();
      }
    }

    if (getStatus() == 6)
    {
      paramGame.getPublicStateMessagesList().addCommonMessage(105, (int)getBreakLength());

      if (CommonLogger.getLogger().isInfoEnabled()) {
        CommonLogger.getLogger().info("<Tour Paused> - PauseTime : " + (getNextBreakTime().getTime() - new Date().getTime()));
      }

      while (getStatus() == 6)
        paramGame.waitTwoSeconds();
    }
    if (getStatus() == 5) {
      paramGame.getPublicStateMessagesList().addCommonMessage(106, 0, 0);
    }

    if (paramGame.getDesk().getPlayersCount() == 0) {
      dropDeskAfterSomeTime(paramGame.getDesk());
    }

    return localArrayList;
  }

  public synchronized ArrayList eliminateOnePlayer(Desk paramDesk, Player paramPlayer)
  {
    HashMap localHashMap = new HashMap();

    synchronized (paramDesk) {
      synchronized (paramDesk.getPlacesList()) {
        Iterator localIterator = paramDesk.getPlacesList().allPlacesIterator();

        while (localIterator.hasNext()) {
          Place localPlace = (Place)localIterator.next();

          if ((localPlace.isBusy()) && (localPlace.getPlayer().getID() == paramPlayer.getID()) && (localPlace.getAmount().compareTo(paramDesk.getMaxBet()) <= 0))
          {
            localHashMap.put(localPlace.getPlayer(), localPlace.getAmount());
          }
        }
      }
    }

    return eliminateChoosenPlayers(paramDesk, localHashMap);
  }

  public BigDecimal getAmountByWinneredPlace(int paramInt) {
    return new BigDecimal(0);
  }

  protected RankingPlayer getRankingPlayer(Player paramPlayer) {
    synchronized (playersList) {
      int i = playersList.size();

      for (int j = 0; j < i; j++) {
        RankingPlayer localRankingPlayer = (RankingPlayer)playersList.get(j);

        if (localRankingPlayer.getPlayer().getID() == paramPlayer.getID()) {
          return localRankingPlayer;
        }
      }
    }

    return null;
  }

  public int getMinDeskPlayersCount() {
    return minDeskPlayersCount;
  }

  public void setMinDeskPlayersCount(int paramInt) {
    minDeskPlayersCount = paramInt;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int paramInt) {
    status = paramInt;
  }

  public int getMinStartPlayersCount() {
    return minStartPlayersCount;
  }

  public void setMinStartPlayersCount(int paramInt) {
    minStartPlayersCount = paramInt;
  }

  public String getCashedDesksXML() {
    return cashedDesksXML;
  }

  public String getCashedPrizeTableXML() {
    return cashedPrizeTableXML;
  }

  public String getCashedPlayersXML() {
    return cashedPlayersXML;
  }

  public BigDecimal getCashedSmallStacks() {
    return cashedSmallStacks;
  }

  public void setCashedSmallStacks(BigDecimal paramBigDecimal) {
    cashedSmallStacks = paramBigDecimal;
  }

  public BigDecimal getCashedBigStacks() {
    return cashedBigStacks;
  }

  public String convertTournamentStatusToString(int paramInt) {
    switch (paramInt) {
    case 0:
      return "Announced";
    case 1:
      return "Registering";
    case 2:
      return "Playing";
    case 3:
      return "Finished";
    case 4:
      return "Cancelled";
    case 5:
      return "Paused";
    case 6:
      return "On Break";
    case 7:
      return "Seating";
    case 11:
      return "Waiting for 1 player";
    case 12:
      return "Waiting for 2 players";
    case 13:
      return "Waiting for 3 players";
    case 14:
      return "Waiting for 4 players";
    case 15:
      return "Waiting for 5 players";
    case 16:
      return "Waiting for 6 players";
    case 17:
      return "Waiting for 7 players";
    case 18:
      return "Waiting for 8 players";
    case 19:
      return "Waiting for 9 players";
    case 20:
      return "Waiting for 10 players";
    case 31:
      return "Level I";
    case 32:
      return "Level II";
    case 33:
      return "Level III";
    case 34:
      return "Level IV";
    case 35:
      return "Level V";
    case 36:
      return "Level VI";
    case 37:
      return "Level VII";
    case 38:
      return "Level VIII";
    case 39:
      return "Level IX";
    case 40:
      return "Level X";
    case 8:
    case 9:
    case 10:
    case 21:
    case 22:
    case 23:
    case 24:
    case 25:
    case 26:
    case 27:
    case 28:
    case 29:
    case 30: } return "";
  }

  public void setCashedBigStacks(BigDecimal paramBigDecimal) {
    cashedBigStacks = paramBigDecimal;
  }

  public BigDecimal getCashedAvgStacks() {
    return cashedAvgStacks;
  }

  public void setCashedAvgStacks(BigDecimal paramBigDecimal) {
    cashedAvgStacks = paramBigDecimal;
  }

  public String getName() {
    return name;
  }

  public int getRegStatus() {
    return regStatus;
  }

  public void setRegStatus(int paramInt) {
    regStatus = paramInt;
  }

  public BigDecimal getAnte() {
    return ante;
  }

  public void setAnte(BigDecimal paramBigDecimal) {
    ante = paramBigDecimal;
  }

  public synchronized void updateDeskLevel(Desk paramDesk) {
    int i = tournamentLevels.increaseGamesCount(paramDesk.getID());
    GameLevel localGameLevel1 = tournamentLevels.getDeskLevel(paramDesk.getID());
    GameLevel localGameLevel2 = tournamentLevels.updateDeskLevel(paramDesk.getID(), i);

    if (CommonLogger.getLogger().isInfoEnabled()) {
      CommonLogger.getLogger().info("Level Changed - ID: " + paramDesk.getID() + " Level: " + localGameLevel2.getLevel() + " Games: " + i + "Max Bet: " + localGameLevel2.getMaxBet() + " Min Bet: " + localGameLevel2.getMinBet());
    }

    if (localGameLevel1.getLevel() != localGameLevel2.getLevel()) {
      paramDesk.setMaxBet(localGameLevel2.getMaxBet());
      paramDesk.setMinBet(localGameLevel2.getMinBet());
      paramDesk.setAnte(localGameLevel2.getAnte());
      paramDesk.setBringIn(localGameLevel2.getBringIn());

      paramDesk.getGame().getPublicStateMessagesList().addCommonMessage(103, localGameLevel2.getMinBet(), localGameLevel2.getMaxBet(), localGameLevel2.getAnte(), localGameLevel2.getBringIn(), localGameLevel2.getLevel());
    }

    currentLevel = localGameLevel2.getLevel();
    currentMaxBet = localGameLevel2.getMaxBet();
    currentMinBet = localGameLevel2.getMinBet();
    currentAnte = localGameLevel2.getAnte();
    currentBringIn = localGameLevel2.getBringIn();
  }

  public boolean unjoin(Player paramPlayer) {
    synchronized (paramPlayer) {
      if (regStatus == 1) {
        if (feeList.removePlayer(paramPlayer)) {
          paramPlayer.getPlayerAmount().deleteTournamentRecord(this);

          synchronized (playersList) {
            for (int i = 0; i < playersList.size(); i++) {
              if (((RankingPlayer)playersList.get(i)).getPlayer().getID() != paramPlayer.getID())
                continue;
              playersList.remove(i);
            }
          }

        }

        updateCashedXML();
      }

    }

    return true;
  }

  public synchronized void updateRanking(ArrayList paramArrayList) {
  }

  public void suggestRebuys(Desk paramDesk) {
  }

  public int terminateUpcomingTournament() {
    synchronized (this) {
      if ((status == 0) || (status == 1) || (status == 3) || (status == 4) || (status == 11) || (status == 12) || (status == 13) || (status == 14) || (status == 15) || (status == 16) || (status == 17) || (status == 18) || (status == 19) || (status == 20))
      {
        returnMoney();

        synchronized (getTournamentsList()) {
          getTournamentsList().remove(this);

          return 1;
        }
      }

      return 0;
    }
  }

  public void returnMoney()
  {
    synchronized (playersList) {
      int i = playersList.size();

      for (int j = 0; j < i; j++) {
        RankingPlayer localRankingPlayer = (RankingPlayer)playersList.get(j);
        if ((isFreeRoll) && (PlayersClub.getInstance().getClubPlayers().isAMember(localRankingPlayer.getPlayer()))) {
          freeRollPrizePool.subtract(fee.add(buyIn)).setScale(2, 5);
        }
        else
        {
          localRankingPlayer.getPlayer().increaseAmount(fee.add(buyIn), moneyType);

          localRankingPlayer.getPlayer().getPlayerAmount().deleteTournamentRecord(this);
        }
      }
      playersList.clear();
    }
  }

  public boolean isFreeRoll() {
    return isFreeRoll;
  }

  public void setFreeRoll(boolean paramBoolean) {
    isFreeRoll = paramBoolean;
  }

  public BigDecimal getFreeRollPrizePool()
  {
    return freeRollPrizePool;
  }

  public void setFreeRollPrizePool(BigDecimal paramBigDecimal) {
    freeRollPrizePool = paramBigDecimal;
  }

  private class RankingListComparator
    implements Comparator
  {
    private RankingListComparator()
    {
    }

    public int compare(Object paramObject1, Object paramObject2)
    {
      Tournament.RankingPlayer localRankingPlayer1 = (Tournament.RankingPlayer)paramObject1;
      Tournament.RankingPlayer localRankingPlayer2 = (Tournament.RankingPlayer)paramObject2;

      if (localRankingPlayer1.getStatus() != localRankingPlayer2.getStatus()) {
        if (localRankingPlayer1.getStatus() == 0) {
          return 8;
        }
        return -8;
      }
      if ((localRankingPlayer1.getStatus() == 1) && (localRankingPlayer2.getStatus() == 1))
      {
        if (localRankingPlayer1.getAmount().compareTo(localRankingPlayer2.getAmount()) > 0)
          return -10;
        if (localRankingPlayer1.getAmount().compareTo(localRankingPlayer2.getAmount()) < 0) {
          return 10;
        }
        if (localRankingPlayer1.getRank() < localRankingPlayer2.getRank())
          return -10;
        if (localRankingPlayer1.getRank() > localRankingPlayer2.getRank()) {
          return 10;
        }
      }
      else if ((localRankingPlayer1.getStatus() == 0) && (localRankingPlayer2.getStatus() == 0))
      {
        if (localRankingPlayer1.getRank() < localRankingPlayer2.getRank())
          return -10;
        if (localRankingPlayer1.getRank() > localRankingPlayer2.getRank()) {
          return 10;
        }
      }

      return 0;
    }
  }

  protected class RankingPlayer
  {
    public static final int STATUS_OUT = 0;
    public static final int STATUS_IN = 1;
    private int rank = 0;
    private BigDecimal amount = new BigDecimal(0);
    private BigDecimal winneredAmount = new BigDecimal(0);
    private Player player;
    private Desk desk;
    private int status = 1;

    public RankingPlayer(Player paramBigDecimal, BigDecimal paramDesk, Desk arg4) {
      player = paramBigDecimal;
      amount = paramDesk;
      Object localObject;
      desk = localObject;
    }

    public Desk getDesk() {
      return desk;
    }

    public void setDesk(Desk paramDesk) {
      desk = paramDesk;
    }

    public int getRank() {
      return rank;
    }

    public void setRank(int paramInt) {
      rank = paramInt;
    }

    public BigDecimal getAmount() {
      return amount;
    }

    public BigDecimal getWinneredAmount() {
      return winneredAmount;
    }

    public void setAmount(BigDecimal paramBigDecimal) {
      amount = paramBigDecimal;
    }

    public void setWinneredAmount(BigDecimal paramBigDecimal) {
      winneredAmount = paramBigDecimal;
    }

    public Player getPlayer() {
      return player;
    }

    public void setPlayer(Player paramPlayer) {
      player = paramPlayer;
    }

    public int getStatus() {
      return status;
    }

    public void setStatus(int paramInt) {
      status = paramInt;
    }
  }

  protected class EliminatedPlayer
  {
    private Player player;
    private BigDecimal amount;
    private int rank;

    public EliminatedPlayer(Player paramBigDecimal, BigDecimal paramInt, int arg4)
    {
      player = paramBigDecimal;
      amount = paramInt;
      int i;
      rank = i;
    }

    public Player getPlayer() {
      return player;
    }

    public BigDecimal getAmount() {
      return amount;
    }

    public int getRank() {
      return rank;
    }
  }

  private class CashingTimerTask extends TimerTask
  {
    private CashingTimerTask()
    {
    }

    public void run()
    {
      try
      {
        updateCashedXML();
      } catch (Exception localException) {
        Log.out("Cashing Timer Task : ERROR : " + localException.getMessage());
      }
    }
  }

  protected class RedistributablePlayer
  {
    private Tournament.RankingPlayer player;
    private BigDecimal amount = new BigDecimal(0);

    public RedistributablePlayer(Tournament.RankingPlayer paramBigDecimal, BigDecimal arg3) {
      player = paramBigDecimal;
      Object localObject;
      amount = localObject;
    }

    public Tournament.RankingPlayer getPlayer() {
      return player;
    }

    public BigDecimal getAmount() {
      return amount;
    }
  }
}