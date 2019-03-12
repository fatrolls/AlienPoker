package processors;

import game.Desk;
import game.Game;
import game.Place;
import game.PlacesList;
import game.Stake;
import game.stats.PlaceSessionStats;
import java.io.IOException;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;

public class AnteCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_DESK_ID = "d";
  private static final String PARAM_DESK_TOURNAMET_ID = "t";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("ANTE");
    if (server.getCurrentPlayer() != null) {
      if (params.containsKey("d"))
      {
        int deskID = ParamParser.getInt(params, "d");

        Desk desk = null;
        if (!params.containsKey("t"))
        {
          desk = Desk.getDeskByID(server.getDesks(), deskID);
        }

        if (desk != null) {
          Place place = desk.getPlacesList().getPlace(server.getCurrentPlayer());
          if (place != null) {
            if (desk.getGame().acceptStake(new Stake(6, place))) {
              place.getPlaceSessionStats().countTotalBettedForLastHour(desk.getAnte());
              response.setResultStatus(true);
            }
            else {
              response.setResultStatus(false, "INVALID STAKE");
            }
          }
          else
            response.setResultStatus(false, "Bad parameters");
        }
        else
        {
          response.setResultStatus(false, "Bad parameters");
        }
      }
      else {
        response.setResultStatus(false, "Bad parameters");
      }
    }
    else {
      response.setResultStatus(false, "Authorization first");
    }

    return response;
  }
}