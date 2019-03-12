package processors;

import defaultvalues.DefaultValue;
import game.Desk;
import game.Player;
import game.PrivateDesksStorage;
import game.privatedesks.CreatePrivateDeskDbCommand;
import game.speed.GameSpeed;
import game.speed.GameSpeedFactory;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import server.Response;
import server.Server;
import settings.PokerSettings;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class CreatePrivateDeskCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_DESK_NAME = "n";
  public static final String PARAM_DESK_LIMIT = "l";
  public static final String PARAM_DESK_MIN_BET = "b";
  public static final String PARAM_DESK_MAX_BET = "c";
  public static final String PARAM_DESK_MIN_AMOUNT = "a";
  public static final String PARAM_DESK_SPEED = "s";
  public static final String PARAM_PASSWORD = "p";
  public static final String MSG_DESK_WAS_ADDED = "The desk was added";
  private static final String TAG_NAME_CODE = "CODE";
  private static final String OUT_PARAM_VALUE = "VALUE";
  private static final String OUT_PARAM_SUCCESS = "SUCCESS";
  private static final int CODE_VALUE_ERROR_NO_MORE_TABLES = 1;
  private static final int CODE_VALUE_ERROR_NAME_ALREADY_EXISTS = 2;
  private static final String MSG_CANNOT_CREATE_DESK = "Cannot create desk";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("CREATEPRIVATEDESK");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    if ((params.containsKey("n")) && (params.containsKey("l")) && (params.containsKey("b")) && (params.containsKey("c")) && (params.containsKey("a")) && (params.containsKey("s"))) { String password;
      int pokerType;
      int places;
      int limit;
      int speed;
      int money;
      BigDecimal minBet;
      BigDecimal maxBet;
      BigDecimal minAmount;
      try { password = URLDecoder.decode((String)params.get("p"), "ISO-8859-1").trim();
        if (password.length() == 0) {
          response.setResultStatus(false, "Bad parameters");
          return response;
        }

        pokerType = 1;
        places = 10;
        limit = Integer.parseInt(URLDecoder.decode((String)params.get("l"), "ISO-8859-1").trim());
        if ((limit != 1) && (limit != 2) && (limit != 3)) {
          response.setResultStatus(false, "Bad parameters");
          return response;
        }

        speed = Integer.parseInt(URLDecoder.decode((String)params.get("s"), "ISO-8859-1").trim());
        if (!GameSpeed.isCorrectGameSpeed(speed)) {
          response.setResultStatus(false, "Bad parameters");
          return response;
        }

        money = 0;
        minBet = new BigDecimal(Float.parseFloat(URLDecoder.decode((String)params.get("b"), "ISO-8859-1").trim())).setScale(2, 5);
        maxBet = new BigDecimal(Float.parseFloat(URLDecoder.decode((String)params.get("c"), "ISO-8859-1").trim())).setScale(2, 5);

        if ((minBet.compareTo(new BigDecimal(0.01D)) < 0) || (minBet.compareTo(new BigDecimal(90000000)) > 0)) {
          response.setResultStatus(false, "Bad parameters");
          return response;
        }

        if (minBet.multiply(new BigDecimal(2)).setScale(2, 5).compareTo(maxBet) != 0) {
          response.setResultStatus(false, "Bad parameters");
          return response;
        }

        minAmount = new BigDecimal(Float.parseFloat(URLDecoder.decode((String)params.get("a"), "ISO-8859-1").trim())).setScale(2, 5);
        if (minAmount.compareTo(minBet) < 0) {
          response.setResultStatus(false, "Bad parameters");
          return response;
        }
      }
      catch (Exception ex1)
      {
        response.setResultStatus(false, "Bad parameters");
        return response;
      }

      String name = URLDecoder.decode((String)params.get("n"), "ISO-8859-1").trim();

      if (!PrivateDesksStorage.canPlayerCreateDesk(currentPlayer))
      {
        XMLDoc doc = new XMLDoc();
        XMLTag tag = doc.startTag("CODE");
        tag.addParam("SUCCESS", 0);
        tag.addParam("VALUE", 1);

        response.setResultStatus(true);
        response.setParametersXML(doc.toString());
        doc.invalidate();
        return response;
      }

      ArrayList list = PrivateDesksStorage.getDesksCreatedByPlayer(currentPlayer);

      Iterator iter = list.iterator();
      while (iter.hasNext()) {
        Desk desk = (Desk)iter.next();
        if (desk.getDeskName().equalsIgnoreCase(name))
        {
          XMLDoc doc = new XMLDoc();
          XMLTag tag = doc.startTag("CODE");
          tag.addParam("SUCCESS", 0);
          tag.addParam("VALUE", 2);

          response.setResultStatus(true);
          response.setParametersXML(doc.toString());
          doc.invalidate();
          return response;
        }

      }

      BigDecimal rake = PokerSettings.getDefaultRakePercent();

      CreatePrivateDeskDbCommand cmd = new CreatePrivateDeskDbCommand();
      int id = cmd.createDesk(limit, pokerType, name, money, minBet, maxBet, DefaultValue.ZERO_BIDECIMAL, DefaultValue.ZERO_BIDECIMAL, DefaultValue.ZERO_BIDECIMAL, minAmount, places, speed, rake, password, 1, currentPlayer.getID());

      if (id <= 0) {
        response.setResultStatus(false, "Cannot create desk");
        return response;
      }

      Desk desk = new Desk();
      desk.setID(id);

      desk.setDeskName(name);
      desk.setMoneyType(money);
      desk.setLimitType(limit);
      desk.setMinBet(minBet);
      desk.setMaxBet(maxBet);
      desk.setPokerType(pokerType);
      desk.setMinAmount(minAmount);

      desk.createPlaces(places);
      desk.setPrivateDesk(true);
      desk.setCreator(currentPlayer);
      desk.setRake(rake);
      desk.setPassword(password);

      desk.startUpGame(GameSpeedFactory.getGameSpeed(speed));

      PrivateDesksStorage.registerPrivateDesk(desk);
      Desk.registerDesk(Server.getDesksList(), desk);

      XMLDoc doc = new XMLDoc();
      XMLTag tag = doc.startTag("CODE");
      tag.addParam("SUCCESS", 1);
      tag.addParam("VALUE", desk.getID());

      response.setResultStatus(true);
      response.setParametersXML(doc.toString());
      doc.invalidate();
      return response;
    }

    response.setResultStatus(false, "Bad parameters");
    return response;
  }
}