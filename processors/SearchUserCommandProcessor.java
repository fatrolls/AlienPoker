package processors;

import game.Desk;
import game.Player;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import server.OnLinePlayers;
import server.Response;
import server.Server;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class SearchUserCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_NICK = "n";
  private static final String TAG_NAME_DESKS = "DESKS";
  private static final String TAG_PARAM_DESKS_COUNT = "COUNT";
  private static final String TAG_PARAM_PLAYER_STATUS = "STATUS";
  private static final int CONSTANT_PLAYER_FOUND = 1;
  private static final int CONSTANT_PLAYER_NOT_LOGGED_IN = 2;
  private static final int CONSTANT_PLAYER_NOT_FOUND = 3;
  public static final String PARAMS_DELIMITER_REGEX = "\\,";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("SEARCHUSER");

    if (params.containsKey("n"))
    {
      if (server.getCurrentPlayer() != null)
      {
        String nicks = URLDecoder.decode((String)params.get("n"), "ISO-8859-1").trim();

        LinkedHashMap playersAndStaus = new LinkedHashMap();

        StringBuffer buffer = new StringBuffer();
        int count = 0;

        String[] tokens = nicks.split("\\,");
        for (int i = 0; i < tokens.length; i++) {
          String nick = tokens[i].trim();
          if (nick.length() == 0)
          {
            continue;
          }

          Player player = Player.searchPlayerByLogin(server.getPlayers(), nick, true);

          ArrayList desks = server.getDesks();
          ArrayList foundedDesks = new ArrayList();
          if (player != null) {
            synchronized (desks) {
              Iterator iter = desks.iterator();
              while (iter.hasNext()) {
                Desk desk = (Desk)iter.next();
                if (desk.getPlayerPlace(player) != null) {
                  foundedDesks.add(desk);
                }
              }

            }

          }

          Iterator iter = foundedDesks.iterator();
          while (iter.hasNext()) {
            Desk desk = (Desk)iter.next();
            synchronized (desk) {
              buffer.append(desk.toXML()).append("\n");
            }
            count++;
          }

          if (player == null) {
            playersAndStaus.put(player, new Integer(3));
          }
          else if ((player.isHideFromSearch()) || ((count == 0) && (Server.getOnlinePlayers().getPlayerSessions(player) == 0)))
          {
            playersAndStaus.put(player, new Integer(2));
          }
          else playersAndStaus.put(player, new Integer(1));

        }

        Iterator iter = playersAndStaus.entrySet().iterator();
        Player lastPlayer = null;
        int lastStatus = 0;
        while (iter.hasNext()) {
          Map.Entry entry = (Map.Entry)iter.next();
          lastPlayer = (Player)entry.getKey();
          lastStatus = ((Integer)entry.getValue()).intValue();

          if (lastStatus == 1)
          {
            break;
          }
        }

        XMLDoc doc = new XMLDoc();
        XMLTag tag = doc.startTag("DESKS");
        tag.addParam("COUNT", count);
        if (lastPlayer == null) {
          tag.addParam("LOGIN", "");
          tag.addParam("STATUS", 3);
        } else {
          tag.addParam("LOGIN", lastPlayer.getLogin());
          tag.addParam("STATUS", lastStatus);
          tag.setTagContent(buffer.toString());
        }

        String xml = doc.toString();
        doc.invalidate();

        response.setResultStatus(true);
        response.setParametersXML(xml);
      }
      else
      {
        response.setResultStatus(false, "Authorization first");
      }
    }
    else {
      response.setResultStatus(false, "Bad parameters");
    }

    return response;
  }
}