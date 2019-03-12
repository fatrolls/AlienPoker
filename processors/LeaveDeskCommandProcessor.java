package processors;

import game.Desk;
import game.Game;
import game.Place;
import game.PlacesList;
import game.Player;
import java.io.IOException;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;

public class LeaveDeskCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_DESK_ID = "d";
  private static final String PARAM_DESK_TOURNAMET_ID = "t";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("LEAVEDESK");

    Player player = server.getCurrentPlayer();
    if (player == null) {
      response.setResultStatus(false, "Authorization first");
    }
    else if (params.containsKey("d")) {
      int deskID = ParamParser.getInt(params, "d");
      Desk desk = null;
      if (!params.containsKey("t"))
      {
        desk = Desk.getDeskByID(server.getDesks(), deskID);
      }

      if (desk != null) {
        Place place = desk.getPlacesList().getPlace(player);
        if (place != null)
        {
          desk.getGame().addPlayerToLeaveDeskQuery(player);

          response.setResultStatus(true);
        }
        else {
          response.setResultStatus(false, "Bad parameters");
        }
      }
      else {
        response.setResultStatus(false, "Bad parameters");
      }
    }
    else {
      response.setResultStatus(false, "Bad parameters");
    }

    return response;
  }
}