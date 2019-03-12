package processors;

import game.Player;
import game.notes.NotesStorage;
import java.io.IOException;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;

public class BlockChatCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_PLAYER = "p";
  private static final String MSG_CHAT_BLOCKED = "Chat was blocked";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("BLOCKCHAT");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    int playerTo = ParamParser.getInt(params, "p");
    Player player = Player.getPlayerByID(server.getPlayers(), playerTo);
    if (player == null) {
      response.setResultStatus(false, "Bad parameters");
      return response;
    }

    NotesStorage.setChatForPlayer(server.getCurrentPlayer(), player, false);
    response.setResultStatus(true, "Chat was blocked");

    return response;
  }
}