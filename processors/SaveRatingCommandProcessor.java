package processors;

import game.Player;
import game.notes.NotesStorage;
import java.io.IOException;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;

public class SaveRatingCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_PLAYER = "p";
  private static final String PARAM_RATING = "r";
  private static final String MSG_RATING_SAVED = "Rating was saved";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("SAVERATING");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    int playerTo = ParamParser.getInt(params, "p");
    Player player = Player.getPlayerByID(server.getPlayers(), playerTo);
    if ((player == null) || (!params.containsKey("r"))) {
      response.setResultStatus(false, "Bad parameters");
      return response;
    }

    int rating = ParamParser.getInt(params, "r");
    if ((rating == 0) || (rating == 1) || (rating == 2) || (rating == 3))
    {
      NotesStorage.setRatingForPlayer(server.getCurrentPlayer(), player, rating);
      response.setResultStatus(true, "Rating was saved");
      return response;
    }
    response.setResultStatus(false, "Bad parameters");
    return response;
  }
}