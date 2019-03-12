package processors;

import game.Desk;
import game.Game;
import game.Place;
import game.PlacesList;
import game.Stake;
import java.io.IOException;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;

public class CheckCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_DESK_ID = "d";
  private static final String PARAM_DESK_TOURNAMET_ID = "t";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("CHECK");

    if (server.getCurrentPlayer() != null) {
      if (params.containsKey("d")) { String dID = (String)params.get("d");
        int deskID;
        try { deskID = Integer.parseInt(dID);
        } catch (Exception e)
        {
          response.setResultStatus(false, "Bad parameters");
          return response;
        }
        Desk desk;
        Desk desk;
        if (params.containsKey("t"))
          desk = Desk.getDeskByID(server.getTournamentDesks(ParamParser.getInt(params, "t")), deskID);
        else {
          desk = Desk.getDeskByID(server.getDesks(), deskID);
        }

        if (desk != null) {
          Place place = desk.getPlacesList().getPlace(server.getCurrentPlayer());
          if (place != null) {
            if (desk.getGame().acceptStake(new Stake(3, place))) {
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
      } else
      {
        response.setResultStatus(false, "Bad parameters");
      }
    }
    else {
      response.setResultStatus(false, "Invalid login or password");
    }

    return response;
  }
}