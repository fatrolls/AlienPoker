package processors;

import game.Player;
import game.notes.NotesStorage;
import java.io.IOException;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;

public class UnBlockChatCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_PLAYER = "p";
  private static final String MSG_CHAT_UNBLOCKED = "Chat was unblocked";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("UNBLOCKCHAT");

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

    NotesStorage.setChatForPlayer(server.getCurrentPlayer(), player, true);
    response.setResultStatus(true, "Chat was unblocked");

    return response;
  }
}