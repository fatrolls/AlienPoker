package processors;

import game.Desk;
import game.Player;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import server.Response;
import server.Server;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;
import waitinglist.Waiter;
import waitinglist.WaitingList;

public class CheckWaitingListCommandProcessor
  implements RequestCommandProcessor
{
  private static final String OUT_PARAM_DESKS = "DESKS";
  private static final String OUT_PARAM_COUNT = "COUNT";
  private static final String INNER_PARAM_TAG_NAME = "DESK";
  private static final String INNER_PARAM_MIN_PLAYERS = "MIN_PLAYERS";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("CHECKWAITINGLIST");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    response.setResultStatus(true);

    WaitingList waitingList = server.getWaitingList();
    List waiters = waitingList.getJoiningWaiters(currentPlayer);

    StringBuffer buff = new StringBuffer();

    int cnt = 0;
    Iterator it = waiters.iterator();
    while (it.hasNext()) {
      Waiter waiter = (Waiter)it.next();
      Desk d = waiter.getJoinDesk();

      XMLDoc xmlDoc = new XMLDoc();
      XMLTag tag = xmlDoc.startTag("DESK");
      tag.addParam("ID", d.getID());
      tag.addParam("NAME", d.getDeskName());
      tag.addParam("PTYPE", d.getPokerType());
      tag.addParam("MTYPE", d.getMoneyType());
      tag.addParam("MINBET", d.getMinBet().floatValue());
      tag.addParam("MAXBET", d.getMaxBet().floatValue());
      tag.addParam("LTYPE", d.getLimitType());
      tag.addParam("PLACES", d.getPlaces());
      tag.addParam("PLAYERS", d.getPlayersCount());
      tag.addParam("AVG_POT", d.getAveragePot().floatValue());
      tag.addParam("FLOP_PERCENT", d.getFlopPercent().floatValue());
      tag.addParam("HANDS_PER_HOUR", d.getHandsPerHour());
      tag.addParam("WAITING_PLAYERS", d.getWaitingPlayersCount());
      tag.addParam("MINAMOUNT", d.getMinAmount().floatValue());
      tag.addParam("MIN_PLAYERS", waiter.getMinPlayers() != -1 ? "" + waiter.getMinPlayers() : "");

      String xml = xmlDoc.toString();
      xmlDoc.invalidate();

      buff.append(xml).append('\n');
      cnt++;
    }

    XMLDoc xmlDoc = new XMLDoc();
    XMLTag tag = xmlDoc.startTag("DESKS");
    tag.addParam("COUNT", cnt);
    tag.setTagContent(buff.toString());

    String xml = xmlDoc.toString();
    xmlDoc.invalidate();

    response.setParametersXML(xml);
    return response;
  }
}