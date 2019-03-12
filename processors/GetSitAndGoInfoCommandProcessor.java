package processors;

import game.Player;
import java.io.IOException;
import java.util.HashMap;
import server.Response;
import server.Server;
import tournaments.Tournament;

public class GetSitAndGoInfoCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_ID = "t";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("GETSITANDGOINFO");
    Player player = server.getCurrentPlayer();
    if (player != null) {
      if (params.containsKey("t"))
      {
        int tournamentID = 0;
        boolean error = false;
        try {
          tournamentID = Integer.parseInt((String)params.get("t"));
        } catch (Exception ex) {
          error = true;
        }

        if (error) {
          response.setResultStatus(false, "Bad parameters");
        } else {
          Tournament tournament = Server.getTournamentByID(tournamentID);
          if (tournament != null) {
            if ((tournament.getTournamentType() == 2) || (tournament.getTournamentType() == 5)) {
              response.setResultStatus(true);
              response.setParametersXML(tournament.toDeskMenuXML(player));
            } else {
              response.setResultStatus(false, "Bad parameters");
            }
          }
          else response.setResultStatus(false, "Bad parameters");
        }
      }
      else
      {
        response.setResultStatus(false, "Bad parameters");
      }
    }
    else response.setResultStatus(false, "Authorization first");

    return response;
  }
}