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
import utils.Log;

public class CallCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_DESK_ID = "d";
  private static final String PARAM_ALL_IN = "a";
  private static final String PARAM_DESK_TOURNAMET_ID = "t";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("CALL");

    if (server.getCurrentPlayer() != null) {
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
          Place place = desk.getPlacesList().getPlace(server.getCurrentPlayer());
          if (place != null) {
            Stake stake = new Stake(1, place);
            if (params.containsKey("a")) {
              int allin = ParamParser.getInt(params, "a");
              if (allin > 0) {
                stake.setAsAllIn();
                Log.out("---- EBANY V ROT NAHUI ---- USTANAVILI BLIAT KAK POLOZENO -----");
              }
            }

            if (desk.getGame().acceptStake(stake)) {
              response.setResultStatus(true);
            }
            else
              response.setResultStatus(false, "INVALID STAKE");
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
        response.setResultStatus(false, "Bad parameters");
      }
    }
    else {
      response.setResultStatus(false, "Invalid login or password");
    }

    return response;
  }
}