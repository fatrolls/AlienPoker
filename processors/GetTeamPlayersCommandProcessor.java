package processors;

import game.Player;
import java.io.IOException;
import java.util.HashMap;
import server.Response;
import server.Server;
import tournaments.Tournament;
import tournaments.team.TeamTournament;

public class GetTeamPlayersCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_ID = "t";
  public static final String PARAM_TEAM_ID = "e";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("GETTEAMPLAYERS");

    if ((params.containsKey("t")) && (params.containsKey("e")))
    {
      int tournamentID = 0;
      int teamID = 0;
      boolean error = false;
      try {
        tournamentID = Integer.parseInt((String)params.get("t"));
        teamID = Integer.parseInt((String)params.get("e"));
      } catch (Exception ex) {
        error = true;
      }

      if (error) {
        response.setResultStatus(false, "Bad parameters");
      } else {
        Tournament t = Server.getTournamentByID(tournamentID);
        if (t != null) {
          if ((t instanceof TeamTournament)) {
            TeamTournament tournament = (TeamTournament)t;

            Player player = server.getCurrentPlayer();
            if (player == null) {
              response.setResultStatus(false, "Authorization first");
              return response;
            }

            String xml = tournament.getTeamPlayersXML(teamID);
            response.setResultStatus(true);
            response.setParametersXML(xml);
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

    return response;
  }
}