package processors;

import game.Desk;
import game.Game;
import game.Place;
import game.PlacesList;
import game.messages.CommonStateMessagesList;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;

public class BarOrderCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_DESK_ID = "d";
  private static final String PARAM_DESK_TOURNAMET_ID = "t";
  private static final String PARAM_ORDER = "o";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("BARORDER");
    if (server.getCurrentPlayer() != null) {
      if ((params.containsKey("d")) && (params.containsKey("o")))
      {
        String order = URLDecoder.decode((String)params.get("o"), "ISO-8859-1");

        int deskID = ParamParser.getInt(params, "d");
        Desk desk;
        Desk desk;
        if (params.containsKey("t"))
          desk = Desk.getDeskByID(server.getTournamentDesks(ParamParser.getInt(params, "t")), deskID);
        else {
          desk = Desk.getDeskByID(server.getDesks(), deskID);
        }

        if ((desk != null) && (order != null)) {
          Place place = desk.getPlacesList().getPlace(server.getCurrentPlayer());
          if (place != null) {
            desk.getGame().getPublicStateMessagesList().addCommonMessage(96, place.getNumber(), order);
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
    }
    else {
      response.setResultStatus(false, "Authorization first");
    }

    return response;
  }
}