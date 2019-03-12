package game;

import commands.safeupdaters.HandIncrease;
import commands.safeupdaters.RakesHistoryUpdater;
import defaultvalues.DefaultValue;
import game.amounts.PlayerAmount;
import game.colorflop.ColorFlop;
import game.gameresults.GameResult;
import game.messages.CommonStateMessagesList;
import game.messages.PrivateStateMessagesList;
import game.playerclub.dirtypoints.DirtyDeskPointsStorage;
import game.pokerrounds.AfterRiverPokerRound;
import game.pokerrounds.AntePokerRound;
import game.pokerrounds.BigBlindPokerRound;
import game.pokerrounds.InviteSitoutsPokerRound;
import game.pokerrounds.PokerRound;
import game.pokerrounds.PreFlopStakesPokerRound;
import game.pokerrounds.PreRiverPokerRound;
import game.pokerrounds.PreTurnStakesPokerRound;
import game.pokerrounds.SmallBlindPokerRound;
import game.pokerrounds.StartGameRound;
import game.rakes.Rake;
import game.speed.GameSpeed;
import game.stats.PlaceSessionStats;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import server.DbConnectionPool;
import tournaments.GameLevel;
import tournaments.Tournament;
import tournaments.TournamentDealerComparator;
import tournaments.TournamentLevels;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public abstract class Game extends Thread
{
  private static long lastGameId = 0L;
  public static final int PT_TEXAS_HOLDEM = 1;
  public static final int PT_SEVEN_CARD_STUD = 2;
  public static final int PT_OMAHA_HI = 3;
  public static final int PT_OMAHA_HI_LO = 4;
  public static final int PT_PINNEAPLE = 5;
  public static final int PT_RAZZ = 6;
  public static final int PT_CHINEZE_POKER = 7;
  public static final int LT_LIMIT = 1;
  public static final int LT_NO_LIMIT = 2;
  public static final int LT_POT_LIMIT = 3;
  public static final int MT_MONEY = 0;
  public static final int MT_FUN = 1;
  public static final String OUT_PARAM_GAME_PLACE = "G_PLACE";
  public static final String OUT_PARAM_GAME_STATE = "G_STATE";
  public static final String OUT_PARAM_GAME_STAKING_AMOUNT = "G_S_AMOUNT";
  public static final String TAG_NAME_STATUS_MESSAGES = "STATUS";
  public static final String TAG_NAME_SERVER_MESSAGES = "SERVER";
  public static final String TAG_NAME_PUBLIC_MESSAGES = "C";
  public static final String TAG_NAME_PERSONAL_MESSAGES = "P";
  public static final String PARAM_NAME_MESSAGES_COUNT = "COUNT";
  public static final String TAG_NAME_PLAYER_CARDS = "PCARDS";
  public static final int MIN_PLAYERS = 2;
  public static final int MAX_SITOUTS_QTY = 3;
  public static final int BEFORE_START_PAUSE = 10;
  public static final int START_ATTENTIONS_COUNT = 1;
  public static final int SERVER_MESSAGE = 0;
  public static final int GS_ENDED = 0;
  public static final int GS_NO_GAME = 1;
  public static final int GS_START_ATTENTION = 2;
  public static final int GS_NEW_HAND = 3;
  public static final int GS_SMALL_BLIND = 4;
  public static final int GS_BIG_BLIND = 5;
  public static final int GS_OWN_CARDS = 6;
  public static final int GS_FLOP = 7;
  public static final int GS_TURN = 8;
  public static final int GS_RIVER = 9;
  public static final int GS_PLAYER_SIT_OUT = 10;
  public static final int GS_SHOW_CARDS = 11;
  public static final int GS_PLAYER_STAKE = 15;
  public static final int GS_CALL = 16;
  public static final int GS_RAISE = 17;
  public static final int GS_CHECK = 18;
  public static final int GS_BET = 19;
  public static final int GS_FOLD = 20;
  public static final int GS_BRING_IN = 21;
  public static final int GS_LOCKS = 22;
  public static final int GS_CHINESE = 23;
  public static final int GS_CHINESE_POSTS = 24;
  public static final int GS_LEAVE_DESK = 30;
  public static final int GS_ANTE = 40;
  public static final int GS_ALL_IN = 50;
  public static final int GS_NEW_PLAYER = 60;
  public static final int GS_SUSPENDED = 70;
  public static final int GS_DISCARD = 80;
  public static final int GS_THIRD_STREET = 90;
  public static final int GS_FOURTH_STREET = 91;
  public static final int GS_FIFTH_STREET = 92;
  public static final int GS_SIXTH_STREET = 93;
  public static final int GS_SEVENTH_STREET = 94;
  public static final int GS_TELLS = 95;
  public static final int GS_BARORDER = 96;
  public static final int GS_TOUR_ONE_CARD = 101;
  public static final int GS_TOUR_ELIMINATED = 102;
  public static final int GS_LEVEL_CHANGED = 103;
  public static final int GS_TOUR_RESEATED = 104;
  public static final int GS_TOUR_BREAK_TIME = 105;
  public static final int GS_TOUR_PAUSED = 106;
  public static final int GS_ADDON = 107;
  public static final int GS_REBUY = 108;
  public static final int GS_ADDON_BUYED = 109;
  public static final int GS_REBUY_BUYED = 110;
  public static final int GS_SEATING = 111;
  public static final int GS_TOUR_LEVEL = 112;
  public static final int GS_TEAM_TOUR_ELIMINATED = 113;
  public static final int GS_COLOR_FLOP = 150;
  public static final int GS_COLOR_FLOP_RETURN_MONEY = 151;
  public static final int GS_COLOR_FLOP_LOOSE = 152;
  public static final int WAIT_TIME = 30;
  public static final int WAIT_PERIOD = 2;
  public static final int RAISES_COUNT = 3;
  protected static final int START = -1;
  public static final int PREFLOP = 1;
  public static final int FLOP = 2;
  public static final int PRETURN = 3;
  public static final int PRERIVER = 4;
  public static final int AFTERRIVER = 5;
  public static final int DISCARD = 6;
  public static final int THIRD_STREET = 7;
  public static final int FOURTH_STREET = 8;
  public static final int FIFTH_STREET = 9;
  public static final int SIXTH_STREET = 10;
  public static final int SEVENTH_STREET = 11;
  protected static final int SUSPENDED = 70;
  protected static final int UNDEFINED = 0;
  public static final long CLEAR_SUSPENDED_DESK_DELAY = 240000L;
  public static final int PAUSE = 2000;
  public static final int BEFORE_RESTART_PAUSE = 20000;
  public static final int BEFORE_CHINESE_RESTART_PAUSE = 32000;
  private static final long NOTIFY_TOURNAMENT_PAUSE = 2000L;
  private static final long TOURNAMENT_ONE_CARD_SLEEP = 6000L;
  public static final long REDISTRIBUTE_PLAYERS_TIMEOUT = 20000L;
  public static final String TAG_NAME_PARAM_VISIBLE_OWN_CARDS = "CARDS";
  private static final String TAG_NAME_TOURNAMENT_ONE_CARD = "TCARDS";
  private static final String TAG_NAME_PLACE_CARDS_COUNT = "COUNT";
  private static final String TAG_NAME_PLACE = "PL";
  private static final String TAG_NAME_PLACE_PARAM = "WHO";
  private static final String TAG_NAME_CURRENT_LEVEL = "LEVEL";
  private static final String LOG_TAG_GAME = "GAME";
  private static final String LOG_OUT_PARAM_GAME_ID = "GAMEID";
  private static final String LOG_OUT_PARAM_GAME_TYPE = "GTYPE";
  private static final String LOG_OUT_PARAM_GAME_LIMIT = "GLIMIT";
  private static final String LOG_TAG_GAME_PLAYERS = "GPLAYERS";
  private static final String LOG_TAG_PLAYER = "PLAYER";
  private static final String LOG_OUT_PARAM_PLAYER_ID = "PID";
  private static final String LOG_OUT_PARAM_PLAYER_LOGIN = "LOGIN";
  private static final String LOG_OUT_PARAM_PLACE_AMOUNT = "PLACEAMOUNT";
  private static final String LOG_TAG_GAME_RESULTS = "GAMERESULTS";
  private static final String LOG_OUT_PARAM_GAME_END_DATE = "GAMEEND";
  private static final String LOG_OUT_END_DATE_PATTERN = "MM.dd.yy H:mm";
  protected long currentGameId;
  protected GameSpeed gameSpeed;
  protected CardsPack cardsPack;
  protected Desk desk;
  public final ExecutionState execState;
  protected int smallBlindPlaceNumber;
  protected int bigBlindPlaceNumber;
  protected int bringInPlaceNumber;
  protected boolean started;
  protected boolean newHand;
  protected boolean smallBlind;
  protected boolean bigBlind;
  protected boolean sitoutsInvited;
  protected boolean preFlop;
  protected boolean flop;
  protected boolean preTurn;
  protected boolean turn;
  protected boolean preRiver;
  protected boolean river;
  protected boolean afterRiver;
  protected boolean ended;
  protected boolean discard;
  protected boolean anteCompleted;
  protected boolean bringIn;
  protected boolean tournamentOneCard;
  protected boolean tournamentGameEnded;
  protected boolean thirdStreet;
  protected boolean fourthStreet;
  protected boolean preFourthStreet;
  protected boolean fifthStreet;
  protected boolean preFifthStreet;
  protected boolean sixthStreet;
  protected boolean preSixthStreet;
  protected boolean seventhStreet;
  protected boolean preSeventhStreet;
  protected boolean afterSeventhStreet;
  protected boolean locksRound;
  protected boolean locksAfterRound;
  protected boolean chineseRound;
  protected BigDecimal gameAmount;
  protected PokerRound currentRound;
  protected int gameState;
  protected final ArrayList commonCards;
  protected GameResult gameResult;
  protected ArrayList blindsSittingOuts;
  private BigDecimal bringInAmount;
  private BigDecimal sumRake;
  protected HashMap postCardsPlaces;
  protected boolean hasAllIn;
  protected Logger log;

  public Game()
  {
    currentGameId = 0L;
    gameSpeed = null;
    cardsPack = null;
    desk = null;
    execState = new ExecutionState(true);
    smallBlindPlaceNumber = 0;
    bigBlindPlaceNumber = 0;
    bringInPlaceNumber = 0;
    started = false;
    newHand = false;
    smallBlind = false;
    bigBlind = false;
    sitoutsInvited = false;
    preFlop = false;
    flop = false;
    preTurn = false;
    turn = false;
    preRiver = false;
    river = false;
    afterRiver = false;
    ended = false;
    discard = false;
    anteCompleted = false;
    bringIn = false;
    tournamentOneCard = false;
    tournamentGameEnded = false;
    thirdStreet = false;
    fourthStreet = false;
    preFourthStreet = false;
    fifthStreet = false;
    preFifthStreet = false;
    sixthStreet = false;
    preSixthStreet = false;
    seventhStreet = false;
    preSeventhStreet = false;
    afterSeventhStreet = false;
    locksRound = false;
    locksAfterRound = false;
    chineseRound = false;
    gameAmount = DefaultValue.ZERO_BIDECIMAL;
    currentRound = null;
    gameState = 0;
    commonCards = new ArrayList();
    gameResult = null;
    blindsSittingOuts = new ArrayList();
    bringInAmount = DefaultValue.ZERO_BIDECIMAL;
    sumRake = DefaultValue.ZERO_BIDECIMAL;
    postCardsPlaces = new HashMap();
    hasAllIn = false;
    log = Logger.getLogger(Game.class);
  }
  public void recordPlayerAmounts() {
    if (desk.getTournamentID() == 0) {
      Iterator localIterator = desk.getPlacesList().allPlacesIterator();

      while (localIterator.hasNext()) {
        Place localPlace = (Place)localIterator.next();
        Player localPlayer = localPlace.getPlayer();

        if (localPlayer != null)
          localPlayer.getPlayerAmount().recordDeskAmount(desk);
      }
    }
  }

  public int getGameState()
  {
    return gameState;
  }

  public boolean isBlinds()
  {
    return (!smallBlind) || (!bigBlind);
  }

  public Place getActivePlace()
  {
    if ((started) && (!ended) && 
      (currentRound != null)) {
      int i = currentRound.getCurrentPlaceNumber();

      return getPlacesList().getPlace(i);
    }

    return null;
  }

  public ExecutionState getActiveTaskState() {
    if (currentRound != null) {
      return currentRound.getExecutionState();
    }

    return null;
  }

  public boolean isStarted() {
    return started;
  }

  public boolean isTournamentOneCard() {
    return tournamentOneCard;
  }

  public void setTournamentOneCard(boolean paramBoolean) {
    tournamentOneCard = paramBoolean;
  }

  public abstract boolean canContinue();

  public boolean canRestart() {
    return (canContinue()) && (gameState == 70);
  }

  public GameSpeed getGameSpeed()
  {
    return gameSpeed;
  }

  public void setGameSpeed(GameSpeed paramGameSpeed) {
    gameSpeed = paramGameSpeed;
  }

  public boolean isLocksAfterRound() {
    return locksAfterRound;
  }

  public void setLocksAfterRound(boolean paramBoolean) {
    locksAfterRound = paramBoolean;
  }

  public boolean isChineseRound() {
    return chineseRound;
  }

  public void setChineseRound(boolean paramBoolean) {
    chineseRound = paramBoolean;
  }

  public void restart() {
    if ((started) && (!newHand)) {
      started = false;
      gameState = 0;
      updateState();
    }
  }

  public abstract int getDealerPlaceNumber();

  public void setSmallBlindPlaceNumber(int paramInt) {
    smallBlindPlaceNumber = paramInt;
  }

  public void setBigBlindPlaceNumber(int paramInt) {
    bigBlindPlaceNumber = paramInt;
  }

  public int getBringInPlaceNumber() {
    return bringInPlaceNumber;
  }

  public void setBringInPlaceNumber(int paramInt) {
    bringInPlaceNumber = paramInt;
  }

  public ArrayList getCommonCards() {
    return commonCards;
  }
  public abstract int getPlayerGamePlace(Player paramPlayer);

  public abstract String getOwnCardsXML(Player paramPlayer);

  public boolean isEnded() { return ended; }

  public String getGameResultXML()
  {
    if (gameResult != null) {
      return gameResult.toXML();
    }

    return null;
  }

  public CommonStateMessagesList getPublicStateMessagesList() {
    return getDesk().getPublicStateMessagesList();
  }

  public BigDecimal getGameAmount() {
    return gameAmount;
  }

  public BigDecimal getTrueGameAmount()
  {
    BigDecimal localBigDecimal = new BigDecimal(0);

    Iterator localIterator = getPlacesList().allPlacesIterator();

    while (localIterator.hasNext())
    {
      Place localPlace = (Place)localIterator.next();

      if (localPlace.isBusy())
      {
        if (localPlace.getStakingAmountCache().floatValue() > 0.0F)
        {
          localBigDecimal = localBigDecimal.add(localPlace.getStakingAmountCache());
        }
      }

    }

    return localBigDecimal;
  }
  public abstract String getPlayerCombination(Player paramPlayer);

  public Desk getDesk() {
    return desk;
  }

  public void notifyAboutLeaveDesk(Place paramPlace) {
    if (currentRound != null)
      if (currentRound.getCurrentPlaceNumber() == paramPlace.getNumber()) {
        ExecutionState localExecutionState = currentRound.getExecutionState();

        synchronized (localExecutionState) {
          localExecutionState.setSignalType(3);
          localExecutionState.permit();
          localExecutionState.notifyAll();
        }
      } else {
        currentRound.notifyAboutLeaveDesk(paramPlace);
      }
  }

  public boolean isSuspended()
  {
    return gameState == 70;
  }

  public boolean acceptSitOut(Player paramPlayer)
  {
    Place localPlace = getDesk().getPlacesList().getPlace(paramPlayer);

    if (localPlace != null) {
      if (currentRound != null) {
        if (localPlace.getNumber() == currentRound.getCurrentPlaceNumber()) {
          ExecutionState localExecutionState = currentRound.getExecutionState();

          synchronized (localExecutionState) {
            localExecutionState.setSignalType(1);
            localExecutionState.permit();
            localExecutionState.notifyAll();
          }
        } else {
          sitoutNotCurrentPlace(localPlace);
        }
      }
      else sitoutNotCurrentPlace(localPlace);

    }

    return true;
  }

  public void sitoutNotCurrentPlace(Place paramPlace) {
    synchronized (paramPlace) {
      paramPlace.markAsSittingOut();
      paramPlace.getStateMessagesList().addPrivateMessage(10, 0);
      getPublicStateMessagesList().addCommonMessage(paramPlace.getPlayer().getLogin(), 10, paramPlace.getNumber(), 2);
    }
  }

  public void setAsStarted()
  {
    started = true;
    updateState();
  }

  public void updateState() {
    synchronized (execState) {
      execState.permit();
      execState.notifyAll();
    }
  }

  public void setAsNewHand() {
    newHand = true;
    updateState();
  }

  public void setAsSmallBlind() {
    smallBlind = true;
    updateState();
  }

  public void setAsBigBlind() {
    bigBlind = true;
    updateState();
  }

  public void begin() {
    if (!isAlive())
      start();
    else
      updateState();
  }

  public void startAttentions()
  {
    desk.getPlacesList().clearStateMessages();

    currentRound = new StartGameRound();
    currentRound.setGame(this);
    currentRound.init();

    Thread localThread = new Thread(currentRound);
    localThread.start();

    gameState = -1;
  }

  public void setDesk(Desk paramDesk) {
    desk = paramDesk;
  }

  public int getMinPlayers() {
    return 2;
  }
  public abstract boolean canStart();

  public ArrayList getOnBlindsSittingOutPlaces() {
    return blindsSittingOuts;
  }

  public void countFlopPercent() {
    Iterator localIterator = getPlacesList().allPlacesIterator();

    while (localIterator.hasNext()) {
      Place localPlace = (Place)localIterator.next();

      if ((localPlace != null) && (localPlace.isBusy()))
        localPlace.getPlaceSessionStats().countFlopPercentForLastHour(flop);
    }
  }

  public boolean addPlayerToLeaveDeskQuery(Player paramPlayer)
  {
    LinkedList localLinkedList = getDesk().getLeaveDeskQuery();
    int i = 1;

    if (paramPlayer != null)
      synchronized (localLinkedList) {
        int j = localLinkedList.size();

        for (int k = 0; k < j; k++) {
          Player localPlayer = (Player)localLinkedList.get(k);

          if (localPlayer.getID() == paramPlayer.getID()) {
            i = 0;

            break;
          }
        }

        localLinkedList.add(paramPlayer);
      }
    else {
      i = 0;
    }

    if (!started) {
      synchronized (execState) {
        execState.notifyAll();
      }
    }

    if ((!isAlive()) || (gameState == 70)) {
      processLeaveDeskQuery();
    }

    return i;
  }

  public void processLeaveDeskQuery()
  {
    getDesk().processLeaveDeskQuery();
  }

  public void notifyAboutAllIn() {
    hasAllIn = true;
  }

  public boolean isHasAllIn() {
    return hasAllIn;
  }

  public void addOnBlindSitttingOutPlace(Place paramPlace) {
    blindsSittingOuts.add(paramPlace);
  }

  protected abstract void reset(long paramLong);

  public void sleepUntillTournamentBegins()
  {
    if (getDesk().getTournamentID() != 0) {
      Tournament localTournament = Tournament.getTournamentByID(getDesk().getTournamentID());

      if (localTournament != null)
        while (!localTournament.isBegin())
          try {
            Thread.sleep(2000L);
          } catch (InterruptedException localInterruptedException) {
            localInterruptedException.printStackTrace();
          }
    }
  }

  public void sleepOneSecond()
  {
    try
    {
      sleep(1000L);
    } catch (InterruptedException localInterruptedException) {
      throw new RuntimeException(localInterruptedException);
    }
  }

  public void prematureAllInEnd() {
    if (!flop) {
      desk.getColorFlop().processFlopNotReached();
      flop(false);
      sleepOneSecond();
    }

    if (!turn) {
      turn(false);
      sleepOneSecond();
    }

    if (!river) {
      river(false);
      sleepOneSecond();
    }

    end(getCurrentGameId());
  }

  public void checkAllInPrizeAmount() {
    if ((currentRound != null) && 
      (currentRound.hasAllInPlaces()))
      currentRound.checkAllInPrizeAmount();
  }

  public PokerRound getCurrentRound()
  {
    return currentRound;
  }

  public BigDecimal getBetsAndGameAmount() {
    BigDecimal localBigDecimal = new BigDecimal(0);

    synchronized (desk) {
      try {
        for (localIterator = desk.getPlacesList().iterator(); localIterator.hasNext(); ) {
          Place localPlace = (Place)localIterator.next();
          localBigDecimal = localBigDecimal.add(localPlace.getStakingAmount()).setScale(2, 5);
        }
      }
      catch (Exception localException)
      {
        Iterator localIterator;
        throw new RuntimeException(localException);
      }
      return localBigDecimal.add(desk.getGame().getGameAmount()).setScale(2, 5);
    }
  }

  public BigDecimal getBetsAmount()
  {
    BigDecimal localBigDecimal1 = new BigDecimal(0);
    Object localObject1 = new BigDecimal(0);
    BigDecimal localBigDecimal2 = new BigDecimal(0);

    if (getDesk().getTournamentID() != 0) {
      Tournament localTournament = Tournament.getTournamentByID(getDesk().getTournamentID());

      localBigDecimal2 = localTournament.getCurrentMaxBet();
    }

    synchronized (desk) {
      try {
        Iterator localIterator = desk.getPlacesList().iterator();

        while (localIterator.hasNext())
        {
          Place localPlace = (Place)localIterator.next();
          localBigDecimal1 = localPlace.getStakingAmount();
          if (((BigDecimal)localObject1).floatValue() < localBigDecimal1.floatValue()) {
            localObject1 = localBigDecimal1;
            if (((BigDecimal)localObject1).floatValue() < localBigDecimal2.floatValue())
              localObject1 = localBigDecimal2;
          }
        }
      }
      catch (Exception localException)
      {
        throw new RuntimeException(localException);
      }
      return localObject1;
    }
  }

  public abstract void end(long paramLong);

  public int getSmallBlindPlaceNumber() {
    return smallBlindPlaceNumber;
  }

  public int getBigBlindPlaceNumber() {
    return bigBlindPlaceNumber;
  }
  public abstract void run();

  public void setAsAfterRiver() {
    afterRiver = true;
    gameState = 0;
    updateGameAmount();
    subtractRake();

    getPublicStateMessagesList().addCommonMessage(11);
    try
    {
      sleep(2000L);
    } catch (InterruptedException localInterruptedException) {
      throw new RuntimeException(localInterruptedException);
    }

    updateState();
  }

  public void afterRiver() {
    currentRound = new AfterRiverPokerRound();
    currentRound.setGame(this);
    currentRound.init();

    Thread localThread = new Thread(currentRound);
    localThread.start();

    gameState = 5;
  }

  public void river() {
    river(true);
  }

  public void river(boolean paramBoolean) {
    synchronized (commonCards) {
      commonCards.add(cardsPack.getNextCard());
    }

    getPublicStateMessagesList().addCommonMessage(9);

    river = true;

    if (paramBoolean)
      updateState();
  }

  public void setAsPreRiver()
  {
    preRiver = true;
    gameState = 0;

    updateGameAmount();
    subtractRake();
    updateState();
  }

  public void preRiver() {
    currentRound = new PreRiverPokerRound();
    currentRound.setGame(this);
    currentRound.init();

    Thread localThread = new Thread(currentRound);
    localThread.start();

    gameState = 4;
  }

  public void setAsPreTurn() {
    preTurn = true;
    gameState = 0;

    updateGameAmount();
    subtractRake();
    updateState();
  }

  public void turn() {
    turn(true);
  }

  public void turn(boolean paramBoolean) {
    turn = true;

    synchronized (commonCards) {
      commonCards.add(cardsPack.getNextCard());
    }

    getPublicStateMessagesList().addCommonMessage(8);

    if (paramBoolean)
      updateState();
  }

  public void preTurn()
  {
    currentRound = new PreTurnStakesPokerRound();
    currentRound.setGame(this);
    currentRound.init();

    Thread localThread = new Thread(currentRound);
    localThread.start();

    gameState = 3;
  }

  public void setAsDiscard() {
    discard = true;
    gameState = 0;

    updateState();
  }

  public void flop() {
    flop(true);
  }

  public void flop(boolean paramBoolean) {
    ArrayList localArrayList = new ArrayList(3);

    synchronized (commonCards) {
      Card localCard = cardsPack.getNextCard();
      commonCards.add(localCard);
      localArrayList.add(localCard);

      localCard = cardsPack.getNextCard();
      commonCards.add(localCard);
      localArrayList.add(localCard);

      localCard = cardsPack.getNextCard();
      commonCards.add(localCard);
      localArrayList.add(localCard);
    }

    flop = true;
    desk.getColorFlop().determineWinners(localArrayList);

    getPublicStateMessagesList().addCommonMessage(7);

    gameState = 2;

    if (paramBoolean)
      updateState();
  }

  public void waitTwoSeconds()
  {
    try
    {
      Thread.sleep(2000L);
    } catch (InterruptedException localInterruptedException) {
      log.error("", localInterruptedException);
    }
  }

  public void notifyTournamentAboutGameEnd()
  {
    ArrayList localArrayList = new ArrayList();

    if (getDesk().getTournamentID() != 0) {
      Tournament localTournament = Tournament.getTournamentByID(getDesk().getTournamentID());

      if (localTournament != null) {
        localArrayList = localTournament.notifyTournamentAboutGameEnd(this);
      }
    }

    if (localArrayList.size() > 0) {
      try {
        Thread.sleep(2000L);
      } catch (InterruptedException localInterruptedException) {
        localInterruptedException.printStackTrace();
      }
    }

    tournamentGameEnded = true;
  }

  public boolean hasAnotherActivePlayers(int paramInt)
  {
    Iterator localIterator = getDesk().getPlacesList().allPlacesIterator();

    while (localIterator.hasNext()) {
      Place localPlace = (Place)localIterator.next();

      if ((localPlace.isActive()) && (localPlace.getNumber() != paramInt)) {
        return true;
      }
    }

    return false;
  }

  public void recordPlayersAmount()
  {
    if (getDesk().getTournamentID() != 0) {
      Tournament localTournament = Tournament.getTournamentByID(getDesk().getTournamentID());

      if (localTournament != null)
        localTournament.recordAmount(getDesk());
    }
  }

  public void updatePlayerAmountStats()
  {
    Iterator localIterator = getDesk().getPlacesList().allPlacesIterator();

    while (localIterator.hasNext()) {
      Place localPlace = (Place)localIterator.next();

      if (localPlace != null) {
        Player localPlayer = localPlace.getPlayer();

        if (localPlayer != null)
          localPlayer.getPlayerAmount().recordDeskAmount(desk);
      }
    }
  }

  public void tournamentOneCard()
  {
    Iterator localIterator = getPlacesList().iterator();
    ArrayList localArrayList = new ArrayList();
    HashMap localHashMap = new HashMap();

    CardsPack localCardsPack = new CardsPack();
    localCardsPack.shuffle();

    while (localIterator.hasNext()) {
      Place localPlace1 = (Place)localIterator.next();
      Card localCard = localCardsPack.getNextCard();

      localPlace1.addCard(localCard);

      localArrayList.add(localCard);
      localHashMap.put(localCard, new Integer(localPlace1.getNumber()));

      localPlace1.getStateMessagesList().addPrivateMessage(101);
    }

    Collections.sort(localArrayList, new TournamentDealerComparator());

    if ((localArrayList.size() > 0) && 
      (localHashMap.containsKey(localArrayList.get(0))));
    getPublicStateMessagesList().addCommonMessage(101);
    try
    {
      Thread.sleep(10000L);
    } catch (InterruptedException localInterruptedException1) {
      localInterruptedException1.printStackTrace();
    }

    tournamentOneCard = true;

    System.out.println(getTournamentOneCardsXML());
    try
    {
      Thread.sleep(6000L);
    } catch (InterruptedException localInterruptedException2) {
      localInterruptedException2.printStackTrace();
    }

    localIterator = getPlacesList().iterator();

    while (localIterator.hasNext()) {
      Place localPlace2 = (Place)localIterator.next();

      synchronized (localPlace2) {
        localPlace2.getCards().clear();
      }
    }

    updateState();
  }

  public boolean acceptStake(Stake paramStake) {
    if (currentRound != null) {
      return currentRound.acceptStake(paramStake);
    }

    return false;
  }

  public void decGameAmount(BigDecimal paramBigDecimal) {
    gameAmount = gameAmount.subtract(paramBigDecimal).setScale(2, 5);
  }

  public void incGameAmount(BigDecimal paramBigDecimal) {
    gameAmount = gameAmount.add(paramBigDecimal).setScale(2, 5);
  }

  public void setAsPreFlop() {
    gameState = 0;
    preFlop = true;

    updateGameAmount();
    subtractRake();

    updateState();
  }

  public abstract void updateGameAmount();

  public void subtractRake() {
    if ((getDesk().getTournamentID() == 0) && (getDesk().getMoneyType() == 0))
    {
      BigDecimal localBigDecimal = Rake.getRake(getDesk());
      sumRake = sumRake.add(localBigDecimal).setScale(2, 5);
      decGameAmount(localBigDecimal);
    }
  }

  public void flushRakes(long paramLong)
  {
    BigDecimal localBigDecimal = DefaultValue.ZERO_BIDECIMAL;

    if (sumRake.compareTo(localBigDecimal) > 0) {
      new Thread(new RakesHistoryUpdater(getDesk(), sumRake, paramLong)).start();
      getDesk().getDirtyDeskPointsStorage().increaseRakesCount();
    }

    sumRake = localBigDecimal;
  }

  public void preFlop() {
    currentRound = new PreFlopStakesPokerRound();
    currentRound.setGame(this);
    currentRound.init();

    Thread localThread = new Thread(currentRound);
    localThread.start();

    gameState = 1;
  }

  public void processAndCheckSitOutPlace(Place paramPlace)
  {
    if ((paramPlace.isSittingOut()) && (getDesk().getTournamentID() == 0)) {
      if (paramPlace.getSitOutsQty() < 3) {
        paramPlace.incSitOutsQty();
      } else {
        Player localPlayer = paramPlace.getPlayer();

        if (localPlayer != null) {
          incGameAmount(paramPlace.getStakingAmount());
          paramPlace.setStakingAmount(new BigDecimal(0));
          localPlayer.increaseAmount(paramPlace.getAmount().setScale(2, 5), desk.getMoneyType());

          getDesk().getPublicStateMessagesList().addCommonMessage(localPlayer.getLogin(), 30, paramPlace.getNumber(), 2);
        }

        notifyAboutLeaveDesk(paramPlace);
        paramPlace.free();

        if (localPlayer != null)
          localPlayer.getPlayerAmount().recordDeskAmount(getDesk());
      }
    }
    else {
      paramPlace.unmarkAsSittingOut();
      paramPlace.clearSitOutsQty();
    }
  }

  public void newHand() {
    gameState = 0;

    setCurrentGameId(generateNextGameId());
    new Thread(new HandIncrease(getCurrentGameId())).start();

    getDesk().getMoneyRequestsList().processRequests();
    getDesk().getPlacesList().sitoutNoMoneyPlayers();

    cardsPack = new CardsPack();
    cardsPack.shuffle();

    getPublicStateMessagesList().clear();
    getPublicStateMessagesList().addCommonMessage(3, currentGameId);
    getPlacesList().nextDealerPlace();

    if (getDesk().getTournamentID() != 0) {
      Tournament localTournament = Tournament.getTournamentByID(getDesk().getTournamentID());

      if (localTournament != null) {
        getPublicStateMessagesList().addCommonMessage(112, localTournament.getCurrentMinBet(), localTournament.getCurrentMaxBet(), localTournament.getCurrentAnte(), localTournament.getCurrentBringIn(), localTournament.getCurrentLevel());
      }

    }

    newHand = true;
    updateState();
  }

  public void smallBlind() {
    currentRound = new SmallBlindPokerRound();
    currentRound.setGame(this);
    currentRound.init();

    Thread localThread = new Thread(currentRound);
    localThread.start();
  }

  public void bigBlind() {
    currentRound = new BigBlindPokerRound();
    currentRound.setGame(this);
    currentRound.init();

    Thread localThread = new Thread(currentRound);
    localThread.start();
  }

  public void setAsSitoutsInvited() {
    sitoutsInvited = true;
    updateState();
  }

  public void setAsAnteCompleted() {
    anteCompleted = true;
    gameState = 0;

    updateGameAmount();
    updateState();
  }

  public void setAsBringIn() {
    bringIn = true;
    updateState();
  }

  public void setAsThirdStreet() {
    thirdStreet = true;
    updateState();
  }

  public void setAsFourthStreet() {
    fourthStreet = true;
    updateState();
  }

  public void setAsPreFourthStreet() {
    preFourthStreet = true;
    gameState = 0;

    updateGameAmount();
    subtractRake();
    updateState();
  }

  public void setAsFifthStreet() {
    fifthStreet = true;
    updateState();
  }

  public void setAsPreFifthStreet() {
    preFifthStreet = true;
    updateGameAmount();
    subtractRake();
    updateState();
  }

  public void setAsSixthStreet() {
    sixthStreet = true;
    updateState();
  }

  public void setAsPreSixthStreet() {
    preSixthStreet = true;
    gameState = 0;

    updateGameAmount();
    subtractRake();
    updateState();
  }

  public void setAsSeventhStreet() {
    seventhStreet = true;
    updateState();
  }

  public void setAsPreSeventhStreet() {
    preSeventhStreet = true;
    gameState = 0;

    updateGameAmount();
    subtractRake();
    updateState();
  }

  public void setAsAfterSeventhStreet() {
    afterSeventhStreet = true;
    gameState = 0;
    updateGameAmount();
    subtractRake();

    getPublicStateMessagesList().addCommonMessage(11);
    try
    {
      sleep(2000L);
    } catch (InterruptedException localInterruptedException) {
      throw new RuntimeException(localInterruptedException);
    }

    updateState();
  }

  public void setAsLocksRound() {
    locksRound = true;
    gameState = 0;

    updateGameAmount();
    subtractRake();
    updateState();
  }

  public void setAsChineseRound() {
    chineseRound = true;
    gameState = 0;

    updateGameAmount();
    updateState();
  }

  public void inviteSitouts() {
    currentRound = new InviteSitoutsPokerRound();
    currentRound.setGame(this);
    currentRound.init();

    Thread localThread = new Thread(currentRound);
    localThread.start();
  }

  public void ante() {
    currentRound = new AntePokerRound();
    currentRound.setGame(this);
    currentRound.init();

    Thread localThread = new Thread(currentRound);
    localThread.start();
  }

  public boolean isBigBlind() {
    return bigBlind;
  }

  public boolean isSmallBlind() {
    return smallBlind;
  }

  public void cancelStart() {
    started = true;
    gameState = 70;
    initiateGameDeskClearing();
  }

  public void logGameResult() {
    XMLDoc localXMLDoc = new XMLDoc();
    XMLTag localXMLTag1 = localXMLDoc.startTag("GAME");
    localXMLTag1.addParam("GAMEID", "" + currentGameId);
    localXMLTag1.addParam("GTYPE", getDesk().getPokerType());
    localXMLTag1.addParam("GLIMIT", getDesk().getLimitType());

    SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("MM.dd.yy H:mm", Locale.ENGLISH);

    localXMLTag1.addParam("GAMEEND", localSimpleDateFormat.format(new Date()));

    XMLTag localXMLTag2 = new XMLTag("GPLAYERS");
    Iterator localIterator = getDesk().getPlacesList().allPlacesIterator();
    Integer[] arrayOfInteger = new Integer[10];
    int i = 0;

    while (localIterator.hasNext()) {
      localObject1 = (Place)localIterator.next();

      if (localObject1 != null) {
        localObject2 = ((Place)localObject1).getPlayer();

        if (localObject2 != null) {
          localObject3 = new XMLTag("PLAYER");
          ((XMLTag)localObject3).addNestedTag(((Place)localObject1).getCardsXMLTag());
          ((XMLTag)localObject3).addParam("PID", ((Player)localObject2).getID());

          if (i <= 9) {
            arrayOfInteger[i] = new Integer(((Player)localObject2).getID());
          }

          ((XMLTag)localObject3).addParam("LOGIN", ((Player)localObject2).getLogin());
          ((XMLTag)localObject3).addParam("PLACEAMOUNT", ((Place)localObject1).getAmount().toString());

          localXMLTag2.addNestedTag((XMLTag)localObject3);
        }
      }

      i++;
    }

    localXMLTag1.addNestedTag(localXMLTag2);

    Object localObject1 = new XMLTag("GAMERESULTS");
    ((XMLTag)localObject1).setTagContent(gameResult.getCachedGameResultXML());
    localXMLTag1.addNestedTag((XMLTag)localObject1);

    Object localObject2 = localXMLDoc.toString();
    localXMLDoc.invalidate();

    Object localObject3 = getDesk().getCommonCardsXML();
    String str = getDesk().getPublicMessagesXML();
    localObject2 = (String)localObject2 + (String)localObject3 + str;

    log.info(localObject2);
    saveHandsHistoryInDB(currentGameId, arrayOfInteger, (String)localObject2);
  }

  private void saveHandsHistoryInDB(long paramLong, Integer[] paramArrayOfInteger, String paramString) {
    Connection localConnection = null;
    try
    {
      localConnection = DbConnectionPool.getDbConnection();

      PreparedStatement localPreparedStatement = localConnection.prepareStatement("insert into hands_history(`hand_id`, `create_date`,  `player0_id` , `player1_id`, `player2_id`, `player3_id`, `player4_id`,   `player5_id`, `player6_id`, `player7_id`, `player8_id`, `player9_id`,   `full_info`) values(?,SYSDATE(),?,?,?,?,?,?,?,?,?,?,?)");

      localPreparedStatement.setLong(1, paramLong);

      for (int i = 0; i <= 9; i++) {
        if ((paramArrayOfInteger.length > i) && (paramArrayOfInteger[i] != null))
          localPreparedStatement.setInt(i + 2, paramArrayOfInteger[i].intValue());
        else {
          localPreparedStatement.setNull(i + 2, 4);
        }
      }

      localPreparedStatement.setString(12, paramString);
      localPreparedStatement.execute();
    } catch (Exception localException) {
      log.error("", localException);
    } finally {
      DbConnectionPool.closeConnection(localConnection);
    }
  }

  public void initiateGameDeskClearing()
  {
    Timer localTimer = new Timer();
    localTimer.schedule(new ClearGameDeskMessageSender(this), 240000L);
  }

  public abstract int getLastStateCode();

  public abstract PlacesList getPlacesList();

  public BigDecimal getBringInAmount()
  {
    return bringInAmount;
  }

  public void setBringInAmount(BigDecimal paramBigDecimal) {
    bringInAmount = paramBigDecimal;
  }

  public String getVisibleOwnCardsXML(Player paramPlayer) {
    XMLTag localXMLTag = new XMLTag("CARDS");
    String str = localXMLTag.toString();
    localXMLTag.invalidate();

    return str;
  }

  public HashMap getPostCardsPlaces() {
    return postCardsPlaces;
  }

  public void setPostCardsPlaces(HashMap paramHashMap) {
    postCardsPlaces = paramHashMap;
  }

  public String getTournamentOneCardsXML() {
    XMLTag localXMLTag1 = new XMLTag("TCARDS");
    int i = 0;

    Iterator localIterator1 = getPlacesList().iterator();

    while (localIterator1.hasNext()) {
      localObject1 = (Place)localIterator1.next();

      if (((Place)localObject1).getCards().size() == 1) {
        ArrayList localArrayList = ((Place)localObject1).getCards();

        if (i == 0) {
          localXMLTag1.addParam("COUNT", localArrayList.size());
          i = 1;
        }

        XMLTag localXMLTag2 = new XMLTag("PL");
        localXMLTag2.addParam("WHO", ((Place)localObject1).getNumber());

        synchronized (localArrayList) {
          Iterator localIterator2 = localArrayList.iterator();

          while (localIterator2.hasNext()) {
            Card localCard = (Card)localIterator2.next();
            localXMLTag2.addNestedTag(localCard.toXMLTag());
          }

        }

        localXMLTag1.addNestedTag(localXMLTag2);
      }
    }

    if (i == 0) {
      localXMLTag1.addParam("COUNT", "0");
    }

    Object localObject1 = localXMLTag1.toString();
    localXMLTag1.invalidate();

    return (String)localObject1;
  }

  public String getTournamentLevelXML() {
    XMLTag localXMLTag = new XMLTag("LEVEL");

    if (getDesk().getTournamentID() != 0) {
      localObject = Tournament.getTournamentByID(getDesk().getTournamentID());

      if (localObject != null) {
        GameLevel localGameLevel = ((Tournament)localObject).getTournamentLevels().getDeskLevel(getDesk().getID());
        localXMLTag.addParam("CLVL", localGameLevel.getLevel());
        localXMLTag.addParam("MAXBET", "" + localGameLevel.getMaxBet().floatValue());

        localXMLTag.addParam("MINBET", "" + localGameLevel.getMinBet().floatValue());

        localXMLTag.addParam("ANTE", "" + localGameLevel.getAnte().floatValue());
        localXMLTag.addParam("BRINGIN", "" + localGameLevel.getBringIn().floatValue());
      }

    }

    Object localObject = localXMLTag.toString();
    localXMLTag.invalidate();

    return (String)localObject;
  }

  public static synchronized long generateNextGameId() {
    lastGameId += 1L;

    return lastGameId;
  }

  public static long getLastGameId() {
    return lastGameId;
  }

  public static void setLastGameId(long paramLong) {
    lastGameId = paramLong;
  }

  public long getCurrentGameId() {
    return currentGameId;
  }

  public void setCurrentGameId(long paramLong) {
    currentGameId = paramLong;
  }

  public class ClearGameDeskMessageSender extends TimerTask
  {
    private Game game = null;

    public ClearGameDeskMessageSender(Game arg2)
    {
      Object localObject;
      game = localObject;
    }

    public void run() {
      if (((game.getGameState() == 70) || (game.getGameState() == 0)) && (game.getDesk().getTournamentID() == 0))
      {
        if (game.getDesk().getPlayersCount() < 2) {
          Iterator localIterator = game.getDesk().getPlacesList().allPlacesIterator();

          while (localIterator.hasNext()) {
            Place localPlace = (Place)localIterator.next();

            if ((localPlace != null) && 
              (localPlace.isBusy())) {
              Player localPlayer = localPlace.getPlayer();

              if (localPlayer != null)
                game.addPlayerToLeaveDeskQuery(localPlayer);
            }
          }
        }
      }
    }
  }
}