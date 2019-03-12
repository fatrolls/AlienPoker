package processors;

import game.Player;
import game.playerclub.PlayersClub;
import java.io.IOException;
import java.util.HashMap;
import server.Response;
import server.Server;

public class JoinPlayersClubCommandProcessor
  implements RequestCommandProcessor
{
  private static final String MSG_JOINED = "You have been successfully joined.";
  private static final String MSG_ALREADY_REGISTERED = "Sorry, but you are already registered member";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("JOINPLAYERSCLUB");
    Player currentPlayer = server.getCurrentPlayer();

    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    PlayersClub playersClub = PlayersClub.getInstance();
    int result = playersClub.join(currentPlayer);
    switch (result) {
    case 1:
      response.setResultStatus(false, "Sorry, but you are already registered member");
      return response;
    case 0:
      response.setResultStatus(true, "You have been successfully joined.");
      return response;
    }
    response.setResultStatus(false, "Bad parameters");
    return response;
  }
}