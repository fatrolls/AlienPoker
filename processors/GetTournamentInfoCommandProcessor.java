package processors;

import game.Player;
import java.io.IOException;
import java.util.HashMap;
import server.Response;
import server.Server;
import tournaments.Tournament;

public class GetTournamentInfoCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_ID = "t";
  public static final String PARAM_OPTION = "p";
  public static final String PARAMS_DELIMITER_REGEX = "\\|";
  public static final int PARAM_OPTION_DESKS = 1;
  public static final int PARAM_OPTION_PLAYERS = 2;
  public static final int PARAM_OPTION_PRIZE_TABLE = 3;
  public static final int PARAM_OPTION_TOUR_BRIEF = 4;

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("GETTOURNAMENTINFO");

    if ((params.containsKey("t")) && (params.containsKey("p")))
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
          String whatInfo = (String)params.get("p");
          String[] values = whatInfo.split("\\|");

          Player player = server.getCurrentPlayer();
          if (player == null) {
            response.setResultStatus(false, "Authorization first");
            return response;
          }

          StringBuffer xml = new StringBuffer();
          for (int i = 0; i < values.length; i++) {
            int param;
            try {
              param = Integer.parseInt(values[i]);
            }
            catch (NumberFormatException e) {
              response.setResultStatus(false, "Bad parameters");
              return response;
            }

            switch (param) {
            case 1:
              xml.append(tournament.getCashedDesksXML());
              break;
            case 2:
              xml.append(tournament.getCashedPlayersXML());
              break;
            case 3:
              xml.append(tournament.getCashedPrizeTableXML());
              break;
            case 4:
              xml.append(tournament.toXML(server.getCurrentPlayer()));
            }

          }

          response.setResultStatus(true);
          response.setParametersXML(xml.toString());
        }
        else {
          response.setResultStatus(false, "Bad parameters");
        }
      }
    }
    else {
      response.setResultStatus(false, "Bad parameters");
    }

    return response;
  }
}