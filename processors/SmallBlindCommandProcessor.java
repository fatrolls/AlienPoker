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

public class SmallBlindCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_DESK_ID = "d";
  private static final String PARAM_DESK_TOURNAMET_ID = "t";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response();
    response.setOnCommand("SBLIND");

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
            if (desk.getGame().acceptStake(new Stake(7, place))) {
              place.getPlaceSessionStats().countTotalBettedForLastHour(desk.getMinBet());
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