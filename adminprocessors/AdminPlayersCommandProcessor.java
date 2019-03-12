package adminprocessors;

import game.Player;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import server.Server;
import utils.CommonLogger;
import utils.xml.XMLDoc;
import utils.xml.XMLParam;
import utils.xml.XMLTag;

public class AdminPlayersCommandProcessor
  implements AdminXMLResponse
{
  private static final String OUT_PARAM_COUNT = "COUNT";
  private static final String OUT_PARAM_PLAYERS = "PLAYERS";

  public Hashtable getPlayers()
  {
    Hashtable response = new Hashtable();

    List players = Server.getPlayersList();
    StringBuffer desksbuff = new StringBuffer();

    synchronized (players) {
      Iterator it = players.iterator();
      while (it.hasNext()) {
        Player p = (Player)it.next();

        ArrayList xmlParams = new ArrayList();
        xmlParams.add(new XMLParam("FREEAMOUNT", p.getAmount(1).floatValue()));
        try
        {
          desksbuff.append(p.toXML(xmlParams, 1)).append('\n');
        } catch (UnsupportedEncodingException e) {
          CommonLogger.getLogger().warn(e);
        }
      }
    }

    XMLDoc xmlDoc = new XMLDoc();
    XMLTag tag = xmlDoc.startTag("PLAYERS");
    tag.addParam("COUNT", players.size());
    tag.setTagContent(desksbuff.toString());

    String xml = xmlDoc.toString();
    xmlDoc.invalidate();

    response.put("STATUS", "OK");
    response.put("RESPONSE", xml);

    return response;
  }
}