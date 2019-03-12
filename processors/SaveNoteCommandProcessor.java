package processors;

import game.Player;
import game.notes.NotesStorage;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;

public class SaveNoteCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_PLAYER = "p";
  private static final String PARAM_MESSAGE = "m";
  private static final String MSG_NOTE_SAVED = "Note was saved";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("SAVENOTE");

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
    String message;
    String message;
    if (params.containsKey("m")) {
      message = URLDecoder.decode((String)params.get("m"), "ISO-8859-1").trim();
    }
    else {
      message = "";
    }

    NotesStorage.setNoteForPlayer(server.getCurrentPlayer(), player, message);
    response.setResultStatus(true, "Note was saved");

    return response;
  }
}