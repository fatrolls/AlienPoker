package adminprocessors;

import game.Player;
import java.util.Hashtable;
import server.Server;

public class AdminBlockPlayerCommandProcessor
  implements AdminXMLResponse
{
  public static final String PARAM_PLAYER_ID = "p";
  public static final String PARAM_OPTION = "o";
  public static final String MSG_PLAYER_WAS_BLOCKED = "The Player was blocked";
  public static final String MSG_PLAYER_WAS_UN_BLOCKED = "The Player was unblocked";
  public static final String DEFAULT_ENCODING = "ISO-8859-1";
  private static final String MSG_ERROR = "Cannot find this player";

  public Hashtable block(int playerId, int block)
  {
    Hashtable response = new Hashtable();

    Player player = Player.getPlayerByID(Server.getPlayersList(), playerId);
    if (player != null) {
      if (block == 1) {
        player.setActive(true);
        response.put("STATUS", "OK");
        response.put("RESPONSE", "The Player was blocked");
        return response;
      }

      player.setActive(false);
      response.put("STATUS", "OK");
      response.put("RESPONSE", "The Player was unblocked");
      return response;
    }

    response.put("STATUS", "ERROR");
    response.put("RESPONSE", "Cannot find this player");
    return response;
  }
}