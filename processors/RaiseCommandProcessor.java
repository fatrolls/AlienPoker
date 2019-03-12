package processors;

import game.Desk;
import game.Game;
import game.Place;
import game.PlacesList;
import game.Stake;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;

public class RaiseCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_DESK_ID = "d";
  private static final String PARAM_ALL_IN = "a";
  private static final String PARAM_STAKE_AMOUNT = "m";
  private static final String PARAM_DESK_TOURNAMET_ID = "t";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("RAISE");

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
          if (place != null) {
            Stake stake = new Stake(2, place);
            if (params.containsKey("m")) {
              BigDecimal amount = new BigDecimal(ParamParser.getFloat(params, "m")).setScale(2, 5);

              synchronized (place) {
                if (amount.compareTo(place.getAmount()) > 0) {
                  amount = place.getAmount();
                  stake.setAsAllIn();
                }
              }

              stake.setAmount(amount);
            }

            if (params.containsKey("a")) {
              int allin = ParamParser.getInt(params, "a");
              if (allin > 0) {
                stake.setAsAllIn();
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