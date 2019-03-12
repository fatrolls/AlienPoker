package processors;

import game.Desk;
import game.Game;
import game.Place;
import game.PlacesList;
import game.messages.CommonStateMessagesList;
import java.io.IOException;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;

public class MakeTellCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_DESK_ID = "d";
  private static final String PARAM_DESK_TOURNAMET_ID = "t";
  private static final String PARAM_TELLS = "m";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("MAKETELL");
    if (server.getCurrentPlayer() != null) {
      if ((params.containsKey("d")) && (params.containsKey("m")))
      {
        int tells = ParamParser.getInt(params, "m");

        int deskID = ParamParser.getInt(params, "d");
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
            desk.getGame().getPublicStateMessagesList().addCommonMessage(95, place.getNumber(), tells);
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