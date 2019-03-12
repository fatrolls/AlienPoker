package processors;

import game.Desk;
import game.Place;
import game.PlacesList;
import game.Player;
import java.io.IOException;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;

public class ShowCardsCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_DESK_ID = "d";
  private static final String PARAM_DESK_TOURNAMET_ID = "t";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("SHOWCARDS");

    if (params.containsKey("d")) {
      int deskID = ParamParser.getInt(params, "d");
      Desk desk;
      Desk desk;
      if (params.containsKey("t"))
        desk = Desk.getDeskByID(server.getTournamentDesks(ParamParser.getInt(params, "t")), deskID);
      else {
        desk = Desk.getDeskByID(server.getDesks(), deskID);
      }

      if (desk != null) {
        Player player = server.getCurrentPlayer();
        if (player != null) {
          Place place = desk.getPlacesList().getPlace(player);
          if ((place != null) && (place.isActive())) {
            place.acceptShowCards();
            response.setResultStatus(true);
          }
          else {
            response.setResultStatus(false, "Bad parameters");
          }
        }
        else {
          response.setResultStatus(false, "Authorization first");
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