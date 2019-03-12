package game;

import defaultvalues.DefaultValue;
import game.amounts.PlayerAmount;
import game.chat.Chat;
import game.colorflop.ColorFlop;
import game.messages.CommonStateMessagesList;
import game.playerclub.ClubPlayers;
import game.playerclub.PlayersClub;
import game.playerclub.dirtypoints.DirtyDeskPointsStorage;
import game.speed.GameSpeed;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import server.XMLFormatable;
import tournaments.Tournament;
import utils.xml.XMLDoc;
import utils.xml.XMLParam;
import utils.xml.XMLTag;

public class Desk
  implements XMLFormatable
{
  public static final String DB_DESKS_TABLE = "desks";
  public static final String DB_DESKS_PLAEYRS_TABLE = "desk_users";
  public static final String DB_ID_FIELD = "desk_id";
  public static final String DB_POKER_TYPE_FIELD = "d_poker_type";
  public static final String DB_LIMIT_TYPE_FIELD = "d_limit_type";
  public static final String DB_NAME_FIELD = "d_name";
  public static final String DB_MONEY_TYPE_FIELD = "d_money_type";
  public static final String DB_MIN_BET_FIELD = "d_min_bet";
  public static final String DB_MAX_BET_FIELD = "d_max_bet";
  public static final String DB_ANTE_FIELD = "d_ante";
  public static final String DB_BRING_IN_FIELD = "d_bring_in";
  public static final String DB_RATE_LIMIT_FIELD = "d_rate_limit";
  public static final String DB_MIN_AMOUNT_FIELD = "d_min_amount";
  public static final String DB_PLACES_FIELD = "d_places";
  public static final String DB_SPEED_FIELD = "d_speed";
  public static final String DB_RAKE_FIELD = "d_rake";
  public static final String DB_PASSWORD = "d_password";
  public static final String DB_PRIVATE = "d_private";
  public static final String DB_OWNER = "d_owner";
  public static final String DB_CREATE_DATE = "d_create_date";
  public static final String DB_PLACE_NUMBER_FIELD = "place";
  public static final String DB_DESK_AMOUNT_FIELD = "amount";
  public static final int LEAVE_DESK_DELAY = 1000;
  public static final int DESK_TYPE_EMPTY = 0;
  public static final int DESK_TYPE_FULL = 1;
  public static final int DESK_TYPE_OTHERS = 2;
  public static final String OUT_PARAM_TAG_NAME = "DESK";
  public static final String OUT_PARAM_ID = "ID";
  public static final String OUT_PARAM_NAME = "NAME";
  public static final String OUT_PARAM_POKER_TYPE = "PTYPE";
  public static final String OUT_PARAM_MIN_BET = "MINBET";
  public static final String OUT_PARAM_MONEY_TYPE = "MTYPE";
  public static final String OUT_PARAM_LIMIT_TYPE = "LTYPE";
  public static final String OUT_PARAM_PLACES = "PLACES";
  public static final String OUT_PARAM_PLAYERS = "PLAYERS";
  public static final String OUT_PARAM_MAX_BET = "MAXBET";
  public static final String OUT_PARAM_MINAMOUNT = "MINAMOUNT";
  public static final String OUT_PARAM_ANTE = "ANTE";
  public static final String OUT_PARAM_BRINGIN = "BRINGIN";
  public static final String OUT_PARAM_SPEED = "SPEED";
  public static final String OUT_PARAM_AVERAGE_POT = "AVG_POT";
  public static final String OUT_PARAM_FLOP_PERSENT = "FLOP_PERCENT";
  public static final String OUT_PARAM_WAITING_PLAYERS = "WAITING_PLAYERS";
  public static final String OUT_PARAM_HANDS_PER_HOUR = "HANDS_PER_HOUR";
  public static final String OUT_PARAM_DESK_PLACE = "D_PLACE";
  public static final String OUT_PARAM_DESK_AMOUNT = "D_AMOUNT";
  public static final String OUT_PARAM_COMMON_CARDS_TAG_NAME = "CCARD";
  public static final String OUT_PARAM_TOURNAMENT_ONE_CARDS_TAG_NAME = "TCARD";
  public static final String OUT_PARAM_CREATOR_LOGIN = "CREATOR";
  private int ID;
  private String deskName;
  private BigDecimal minBet = DefaultValue.ZERO_BIDECIMAL;
  private BigDecimal maxBet = DefaultValue.ZERO_BIDECIMAL;
  private int pokerType;
  private int limitType;
  private int moneyType;
  private BigDecimal minAmount = DefaultValue.ZERO_BIDECIMAL;
  private BigDecimal minPlayerRate = DefaultValue.ZERO_BIDECIMAL;
  private BigDecimal ante = DefaultValue.ZERO_BIDECIMAL;
  private BigDecimal bringIn = DefaultValue.ZERO_BIDECIMAL;
  private int places;
  private int tournamentID;
  private int waitingPlayersCount = 0;
  private boolean deleted = false;
  private boolean privateDesk = false;
  private Player creator = null;
  private String password = "";
  private PlacesList placesList = null;
  private BigDecimal rake = DefaultValue.ZERO_BIDECIMAL;
  private int playersCount = 0;
  private ArrayList players = new ArrayList();
  private final LinkedList leaveDeskQuery = new LinkedList();
  private Game game = null;
  private final ColorFlop colorFlop = new ColorFlop(this);
  private CommonStateMessagesList messages = new CommonStateMessagesList();
  private Chat chat = new Chat();
  private MoneyRequestsList moneyRequestsList = new MoneyRequestsList();
  private DeskStats stats = new DeskStats();
  private final DirtyDeskPointsStorage dirtyDeskPointsStorage = new DirtyDeskPointsStorage(this);

  public Desk() {
    moneyRequestsList.setDesk(this);
  }

  public DeskStats getStats() {
    return stats;
  }

  public String getPublicMessagesXML() {
    return messages.toXML();
  }

  public CommonStateMessagesList getPublicStateMessagesList() {
    return messages;
  }

  public String getCommonCardsXML() {
    ArrayList cards = game.getCommonCards();
    XMLTag tag = new XMLTag("CCARD");

    synchronized (cards) {
      Iterator it = cards.iterator();
      while (it.hasNext()) {
        Card card = (Card)it.next();
        tag.addNestedTag(card.toXMLTag());
      }
    }

    String xml = tag.toString();
    tag.invalidate();

    return xml;
  }

  public ColorFlop getColorFlop() {
    return colorFlop;
  }

  public BigDecimal getMaxAmount() {
    if (getLimitType() != 1) {
      return getMaxBet().multiply(new BigDecimal(100)).setScale(2, 5);
    }

    throw new RuntimeException("error: incorrect usage of method");
  }

  public Game getGame()
  {
    return game;
  }

  public PlacesList getPlacesList() {
    return placesList;
  }

  public int getDealerPlace() {
    return game.getDealerPlaceNumber();
  }

  public void startUpGame(GameSpeed gameSpeed) {
    game = GamesFactory.createGame(getPokerType());
    if (game != null) {
      game.setDesk(this);
      game.setGameSpeed(gameSpeed);
    }
  }

  public boolean canStartGame() {
    return game.canStart();
  }

  public String getPrivateMessagesXML(Player player) {
    try {
      Place place = placesList.getPlace(player);
      if (place != null)
        return place.getStateMessagesXML();
    }
    catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }

    return null;
  }

  public static Desk getDeskByID(ArrayList desks, int ID) {
    Desk desk = null;
    Iterator it = desks.iterator();

    synchronized (desks) {
      while (it.hasNext()) {
        Desk d = (Desk)it.next();
        if (d.getID() == ID) {
          desk = d;
          break;
        }
      }
    }

    return desk;
  }

  public static void registerDesk(ArrayList desks, Desk desk) {
    synchronized (desks) {
      desks.add(desk);
    }
  }

  public static void unRegisterDesk(ArrayList desks, Desk desk) {
    synchronized (desks) {
      Iterator iter = desks.iterator();
      while (iter.hasNext()) {
        Desk d = (Desk)iter.next();
        if (desk.equals(d)) {
          iter.remove();
          return;
        }
      }
    }
  }

  public ArrayList getDeskPlayers() {
    return players;
  }

  public boolean isPlaceAvailable(int number) {
    return placesList.isPlaceAvailable(number);
  }

  public boolean isPlayerOnDesk(Player player)
  {
    return placesList.getPlace(player) != null;
  }

  public int getPlayerGamePlace(Player player)
  {
    return game.getPlayerGamePlace(player);
  }

  public Place getPlayerPlace(Player player) {
    return placesList.getPlace(player);
  }

  public int getPlayerDeskPlace(Player player) {
    return placesList.getPlayerPlaceNumber(player);
  }

  public BigDecimal getPlayerDeskAmount(Player player) {
    return placesList.getPlayerDeskAmount(player);
  }

  public void seatPlayer(Player player, int placeNumber, BigDecimal amount) {
    synchronized (placesList) {
      if (!game.isStarted()) {
        placesList.seatPlayer(placeNumber, player, amount);
      }
      else if (game.isSuspended())
        placesList.seatPlayer(placeNumber, player, amount);
      else {
        placesList.seatPlayer(placeNumber, player, amount, true);
      }

    }

    synchronized (game) {
      if (game.canStart())
        game.begin();
      else if (game.canRestart())
        game.restart();
    }
  }

  public void processLeaveDeskQuery()
  {
    synchronized (leaveDeskQuery) {
      int size = leaveDeskQuery.size();
      for (int i = 0; i < size; i++) {
        Player player = (Player)leaveDeskQuery.get(i);
        Place place = getPlacesList().getPlace(player);
        if (place != null) {
          Game game = getGame();
          if (game != null) {
            game.incGameAmount(place.getStakingAmount());
            place.setStakingAmount(new BigDecimal(0));

            if ((getTournamentID() != 0) && (Tournament.getTournamentByID(getTournamentID()).isFreeRoll()) && (PlayersClub.getInstance().getClubPlayers().isAMember(player)))
              player.increaseDirtyPoints(place.getAmount());
            else {
              player.increaseAmount(place.getAmount().setScale(2, 5), getMoneyType());
            }
            game.notifyAboutLeaveDesk(place);
          }
          place.free();
          player.getPlayerAmount().recordDeskAmount(this);

          Timer timer = new Timer();
          timer.schedule(new LeaveDeskMessageSender(this, place.getNumber(), player.getLogin()), 1000L);
        }
      }

      leaveDeskQuery.clear();
    }
  }

  public int getPlayersCount() {
    PlacesList list = getPlacesList();
    if (list != null) {
      return list.getPlayersCount();
    }
    return 0;
  }

  public String getDeskName() {
    return deskName;
  }

  public void setDeskName(String deskName) {
    this.deskName = deskName;
  }

  public BigDecimal getMinBet() {
    return minBet;
  }

  public void setMinBet(BigDecimal minBet) {
    this.minBet = minBet;
  }

  public int getPokerType() {
    return pokerType;
  }

  public void setPokerType(int pokerType) {
    this.pokerType = pokerType;
  }

  public int getLimitType() {
    return limitType;
  }

  public void setLimitType(int limitType) {
    this.limitType = limitType;
  }

  public int getMoneyType() {
    return moneyType;
  }

  public void setMoneyType(int gameType) {
    moneyType = gameType;
  }

  public BigDecimal getMaxBet() {
    return maxBet;
  }

  public void setMaxBet(BigDecimal maxBet) {
    this.maxBet = maxBet;
  }

  public int getID() {
    return ID;
  }

  public void setID(int ID) {
    this.ID = ID;
  }

  public BigDecimal getMinAmount() {
    return minAmount;
  }

  public void setMinAmount(BigDecimal minAmount) {
    this.minAmount = minAmount;
  }

  public BigDecimal getMinPlayerRate() {
    return minPlayerRate;
  }

  public void setMinPlayerRate(BigDecimal minPlayerRate) {
    this.minPlayerRate = minPlayerRate;
  }

  public int getPlaces() {
    return places;
  }

  public void createPlaces(int places) {
    placesList = new PlacesList(places, this);
  }

  public String toXML(ArrayList additionalParams) throws UnsupportedEncodingException {
    XMLDoc xmlDoc = new XMLDoc();
    XMLTag tag = xmlDoc.startTag("DESK");
    tag.addParam("ID", getID());
    tag.addParam("NAME", getDeskName());
    tag.addParam("PTYPE", getPokerType());
    tag.addParam("MTYPE", getMoneyType());
    tag.addParam("MINBET", getMinBet().floatValue());
    tag.addParam("MAXBET", getMaxBet().floatValue());
    tag.addParam("ANTE", getAnte().floatValue() != 0.0F ? "" + getAnte().floatValue() : "");

    tag.addParam("LTYPE", getLimitType());

    tag.addParam("PLACES", getPlacesList().size());
    tag.addParam("PLAYERS", getPlayersCount());
    tag.addParam("AVG_POT", getAveragePot().floatValue());
    tag.addParam("FLOP_PERCENT", getFlopPercent().floatValue());
    tag.addParam("HANDS_PER_HOUR", getHandsPerHour());
    tag.addParam("WAITING_PLAYERS", getWaitingPlayersCount());
    tag.addParam("MINAMOUNT", getMinAmount().floatValue());
    tag.addParam("SPEED", game.getGameSpeed().getType());

    if (creator != null) {
      tag.addParam("CREATOR", creator.getLogin());
    }

    if (additionalParams != null) {
      Iterator it = additionalParams.iterator();
      while (it.hasNext()) {
        XMLParam xmlParam = (XMLParam)it.next();
        tag.addParam(xmlParam);
      }
    }

    String xml = xmlDoc.toString();
    xmlDoc.invalidate();

    return xml;
  }

  public String toXML() throws UnsupportedEncodingException {
    return toXML(null);
  }

  public BigDecimal getAveragePot() {
    return stats.getAveragePot();
  }

  public BigDecimal getFlopPercent() {
    return stats.getFlopsPercent();
  }

  public int getWaitingPlayersCount()
  {
    return waitingPlayersCount;
  }

  public void setWaitingPlayersCount(int count)
  {
    waitingPlayersCount = count;
  }

  public int getHandsPerHour() {
    return stats.getHandsCount();
  }

  public Chat getChat() {
    return chat;
  }

  public MoneyRequestsList getMoneyRequestsList() {
    return moneyRequestsList;
  }

  public boolean isDeleted() {
    return deleted;
  }

  public void setDeleted(boolean deleted) {
    this.deleted = deleted;
  }

  public int getTournamentID() {
    return tournamentID;
  }

  public void setTournamentID(int tournamentID) {
    this.tournamentID = tournamentID;
  }

  public BigDecimal getAnte() {
    return ante;
  }

  public boolean equals(Object desk) {
    if (desk == null) {
      return false;
    }
    if ((desk instanceof Desk)) {
      return ID == ((Desk)desk).getID();
    }
    return false;
  }

  public int hashCode() {
    return getID();
  }

  public void setAnte(BigDecimal ante) {
    this.ante = ante;
  }

  public BigDecimal getBringIn() {
    return bringIn;
  }

  public void setBringIn(BigDecimal bringIn) {
    this.bringIn = bringIn;
  }

  public LinkedList getLeaveDeskQuery() {
    return leaveDeskQuery;
  }

  public boolean isPrivateDesk() {
    return privateDesk;
  }

  public void setPrivateDesk(boolean privateDesk) {
    this.privateDesk = privateDesk;
  }

  public Player getCreator() {
    if (creator == null) {
      throw new IllegalStateException("The creator of privateDesk cannot be null");
    }
    return creator;
  }

  public void setCreator(Player creator) {
    this.creator = creator;
  }

  public BigDecimal getRake() {
    return rake;
  }

  public void setRake(BigDecimal rake) {
    this.rake = rake;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public DirtyDeskPointsStorage getDirtyDeskPointsStorage() {
    return dirtyDeskPointsStorage;
  }

  public class LeaveDeskMessageSender extends TimerTask {
    private int placeNumber = 0;
    private Desk desk = null;
    private String login = null;

    public LeaveDeskMessageSender(Desk desk, int placeNumber, String login) {
      this.placeNumber = placeNumber;
      this.desk = desk;
      this.login = login;
    }

    public void run() {
      desk.getPublicStateMessagesList().addCommonMessage(login, 30, placeNumber, 2);
    }
  }
}