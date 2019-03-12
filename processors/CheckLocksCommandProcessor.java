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

public class CheckLocksCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_DESK_ID = "d";
  private static final String PARAM_DESK_TOURNAMET_ID = "t";
  private static final String MSG_CANNOT_CHECK_LOCKS = "Cannot Check Locks";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response();
    response.setOnCommand("CHECKLOCKS");

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
          Place place = desk.getPlacesList().getPlace(server.getCurrentPlayer());
          if (place != null)
          {
            Stake stake = new Stake(11, place);
            if (desk.getGame().acceptStake(stake))
              response.setResultStatus(true);
            else
              response.setResultStatus(false, "Cannot Check Locks");
          }
          else
          {
            response.setResultStatus(false, "Bad parameters");
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