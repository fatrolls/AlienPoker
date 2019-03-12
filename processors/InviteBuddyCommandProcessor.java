package processors;

import game.Desk;
import game.Player;
import game.buddylist.BuddyList;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;

public class InviteBuddyCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_PLAYER = "p";
  public static final String PARAM_DESK = "d";
  public static final String PARAM_MESSAGE = "m";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("INVITEBUDDY");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    int deskId = ParamParser.getInt(params, "d");
    Desk desk = Desk.getDeskByID(Server.getDesksList(), deskId);
    if (desk == null) {
      response.setResultStatus(false, "Bad parameters");
      return response;
    }
    String message;
    String message;
    if (params.containsKey("m"))
      message = URLDecoder.decode((String)params.get("m"), "ISO-8859-1").trim();
    else {
      message = "";
    }

    if (params.containsKey("p")) {
      int playerId = ParamParser.getInt(params, "p");
      Player buddyplayer = Player.getPlayerByID(Server.getPlayersList(), playerId);

      if (buddyplayer != null) {
        buddyplayer.getByddyList().acceptInvitation(currentPlayer, message, desk);
        response.setResultStatus(true);
        return response;
      }

    }

    response.setResultStatus(false, "Bad parameters");
    return response;
  }
}