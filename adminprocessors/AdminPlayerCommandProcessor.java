package adminprocessors;

import game.Player;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import org.apache.log4j.Logger;
import server.Server;
import utils.CommonLogger;
import utils.xml.XMLParam;

public class AdminPlayerCommandProcessor
  implements AdminXMLResponse
{
  private static final String MSG_PLAYER_NOT_FOUND = "Player not found";
  private static final String MSG_INTERNAL_SERVER_ERROR = "Internal Server Error";

  public Hashtable getPlayer(int playerId)
  {
    Hashtable response = new Hashtable();

    Player p = Player.getPlayerByID(Server.getPlayersList(), playerId);
    if (p == null)
    {
      response.put("STATUS", "ERROR");
      response.put("RESPONSE", "Player not found");

      return response;
    }

    response.put("STATUS", "OK");
    try
    {
      ArrayList xmlParams = new ArrayList();
      xmlParams.add(new XMLParam("FREEAMOUNT", p.getAmount(1).floatValue()));

      response.put("RESPONSE", p.toXML(xmlParams, 0));
    } catch (UnsupportedEncodingException e) {
      CommonLogger.getLogger().warn(e);
      response.put("STATUS", "ERROR");
      response.put("RESPONSE", "Internal Server Error");
    }

    return response;
  }
}