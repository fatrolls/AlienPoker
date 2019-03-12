package adminprocessors;

import game.Desk;
import game.Place;
import game.PlacesList;
import game.Player;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Iterator;
import org.apache.log4j.Logger;
import server.Server;
import utils.CommonLogger;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class AdminDeskPlayersCommandProcessor
  implements AdminXMLResponse
{
  public static final String PARAM_DESK_ID = "d";
  public static final String PARAM_DESK_TOURNAMET_ID = "t";
  private static final String OUT_PARAM_COUNT = "COUNT";
  private static final String OUT_PARAM_PLAYERS = "PLAYERS";

  public Hashtable getDeskPlayers(int tournament, int deskID)
  {
    Hashtable response = new Hashtable();

    if (deskID > 0)
    {
      Desk desk;
      Desk desk;
      if (tournament > 0)
        desk = Desk.getDeskByID(Server.getTournamentDesksList(tournament), deskID);
      else {
        desk = Desk.getDeskByID(Server.getDesksList(), deskID);
      }

      XMLDoc xmlDoc = new XMLDoc();
      XMLTag tag = xmlDoc.startTag("PLAYERS");
      StringBuffer buff = new StringBuffer();
      int count = 0;

      if (desk != null) {
        Iterator iter = desk.getPlacesList().allPlacesIterator();
        while (iter.hasNext()) {
          Place place = (Place)iter.next();
          Player player = place.getPlayer();
          if (player != null) {
            try {
              buff.append(player.toXML(desk.getMoneyType())).append('\n');
            } catch (UnsupportedEncodingException e) {
              CommonLogger.getLogger().warn(e);
            }
            count++;
          }
        }
      }

      tag.addParam("COUNT", count);
      tag.setTagContent(buff.toString());

      String xml = xmlDoc.toString();
      xmlDoc.invalidate();

      response.put("STATUS", "OK");
      response.put("RESPONSE", xml);
    }
    else
    {
      response.put("STATUS", "ERROR");
      response.put("RESPONSE", "Bad parameters");
    }

    return response;
  }
}