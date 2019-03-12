package processors;

import game.Player;
import java.io.IOException;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;
import tournaments.Tournament;

public class UnjoinTournamentCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_DESK_TOURNAMET_ID = "t";
  private static final String MSG_NOT_IN_TOURNAMENT = "You are not registered at the tournament";
  private static final String MSG_CANNOT_UNJOIN = "Cannot unjoin tournament";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("UNJOINTOURNAMENT");
    Player currentPlayer = server.getCurrentPlayer();

    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }
    if (params.containsKey("t"))
    {
      int tournamentID = ParamParser.getInt(params, "t");

      Tournament t = Server.getTournamentByID(tournamentID);
      if (t == null) {
        response.setResultStatus(false, "Bad parameters");
        return response;
      }if (!t.hasPlayer(currentPlayer)) {
        response.setResultStatus(false, "You are not registered at the tournament");
        return response;
      }
      if (t.unjoin(currentPlayer)) {
        response.setResultStatus(true);
        return response;
      }
      response.setResultStatus(false, "Cannot unjoin tournament");
      return response;
    }

    response.setResultStatus(false, "Bad parameters");
    return response;
  }
}