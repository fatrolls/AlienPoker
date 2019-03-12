package processors;

import game.Desk;
import game.Place;
import game.stats.PlaceSessionStats;
import java.io.IOException;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;

public class GetSessionStatsCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_DESK_ID = "d";
  private static final String PARAM_DESK_TOURNAMET_ID = "t";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response();
    response.setOnCommand("GETSESSIONSTATS");

    if (server.getCurrentPlayer() != null) {
      if (params.containsKey("d"))
      {
        int deskID = ParamParser.getInt(params, "d");
        Desk desk;
        Desk desk;
        if (params.containsKey("t"))
          desk = Desk.getDeskByID(server.getTournamentDesks(ParamParser.getInt(params, "t")), deskID);
        else {
          desk = Desk.getDeskByID(server.getDesks(), deskID);
        }

        if (desk != null) {
          Place place = desk.getPlayerPlace(server.getCurrentPlayer());
          if (place != null) {
            response.setResultStatus(true);
            response.setParametersXML(place.getPlaceSessionStats().toXML());
          }
        } else {
          response.setResultStatus(false, "Bad parameters");
        }
      } else {
        response.setResultStatus(false, "Bad parameters");
      }
    }
    else response.setResultStatus(false, "Authorization first");

    return response;
  }
}