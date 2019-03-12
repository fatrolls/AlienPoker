package processors;

import game.Desk;
import game.Place;
import game.PlacesList;
import java.io.IOException;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;
import utils.xml.XMLTag;

public class CardsCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_DESK_ID = "d";
  public static final String PARAM_PLACE_NUMBER = "p";
  private static final String PARAM_DESK_TOURNAMET_ID = "t";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("CARDS");

    if ((params.containsKey("d")) && (params.containsKey("p"))) {
      int deskID = ParamParser.getInt(params, "d");
      int placeNumber = ParamParser.getInt(params, "p");
      Desk desk;
      Desk desk;
      if (params.containsKey("t"))
        desk = Desk.getDeskByID(server.getTournamentDesks(ParamParser.getInt(params, "t")), deskID);
      else {
        desk = Desk.getDeskByID(server.getDesks(), deskID);
      }

      if (desk != null) {
        Place place = desk.getPlacesList().getPlace(placeNumber);
        if ((place != null) && (place.isActive())) {
          if (place.isAcceptShowCards()) {
            XMLTag tag = place.getCardsXMLTag();
            response.setParametersXML(tag.toString());
            tag.invalidate();

            response.setResultStatus(true);
          }
          else {
            response.setResultStatus(false, "NOT ACCEPTED BY PLAYER");
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

    return response;
  }
}