package processors;

import game.Player;
import game.buddylist.BuddyList;
import java.io.IOException;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;

public class BlockBuddyCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_PLAYER = "p";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("BLOCKBUDDY");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    if (params.containsKey("p")) {
      int playerId = ParamParser.getInt(params, "p");
      Player buddyplayer = Player.getPlayerByID(Server.getPlayersList(), playerId);

      if (buddyplayer != null) {
        currentPlayer.getByddyList().blockBuddy(buddyplayer);
        response.setResultStatus(true);
        return response;
      }

    }

    response.setResultStatus(false, "Bad parameters");
    return response;
  }
}