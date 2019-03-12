package adminprocessors;

import game.Desk;
import game.speed.GameSpeedFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import server.Server;

public class AdminAddDeskCommandProcessor
  implements AdminXMLResponse
{
  public static final String PARAM_DESK_ID = "d";
  public static final String PARAM_DESK_NAME = "n";
  public static final String PARAM_DESK_MONEY = "m";
  public static final String PARAM_DESK_LIMIT = "l";
  public static final String PARAM_DESK_MIN_BET = "b";
  public static final String PARAM_DESK_MAX_BET = "c";
  public static final String PARAM_DESK_POKER_TYPE = "p";
  public static final String PARAM_DESK_MIN_AMOUNT = "a";
  public static final String PARAM_DESK_MIN_PLAYER_RATE = "r";
  public static final String PARAM_DESK_PLACES = "s";
  public static final String PARAM_ANTE = "x";
  public static final String PARAM_BRING_IN = "y";
  public static final String MSG_DESK_WAS_ADDED = "The desk was added";
  public static final String DEFAULT_ENCODING = "ISO-8859-1";

  public Hashtable addDesk(int deskId, String name, int money, int limit, double minBetD, double maxBetD, int pokerType, double deskMinAmountD, double anteD, double bringInD, double minAmountD, int places, double minPlayerRateD, int speed)
  {
    Hashtable response = new Hashtable();

    BigDecimal minBet = new BigDecimal(minBetD).setScale(2, 5);
    BigDecimal maxBet = new BigDecimal(maxBetD).setScale(2, 5);
    BigDecimal minAmount = new BigDecimal(minAmountD).setScale(2, 5);
    BigDecimal minPlayerRate = new BigDecimal(minPlayerRateD).setScale(2, 5);
    BigDecimal ante = new BigDecimal(anteD).setScale(2, 5);
    BigDecimal bringIn = new BigDecimal(bringInD).setScale(2, 5);

    Desk desk = new Desk();
    desk.setID(deskId);
    desk.setDeskName(name);
    desk.setMoneyType(money);
    desk.setLimitType(limit);
    desk.setMinBet(minBet);
    desk.setMaxBet(maxBet);
    desk.setAnte(ante);
    desk.setBringIn(bringIn);
    desk.setPokerType(pokerType);
    desk.setMinAmount(minAmount);
    desk.setMinPlayerRate(minPlayerRate);

    desk.createPlaces(places);

    desk.setAnte(desk.getMinBet().divide(new BigDecimal(2), 2, 5));
    desk.startUpGame(GameSpeedFactory.getGameSpeed(speed));
    synchronized (Server.getDesksList()) {
      Server.getDesksList().add(desk);
    }

    response.put("STATUS", "OK");
    response.put("RESPONSE", "The desk was added");
    return response;
  }
}