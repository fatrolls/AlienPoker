package processors;

import game.Desk;
import game.MoneyRequestsList;
import game.PlacesList;
import game.Player;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;

public class MoreMoneyCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_DESK_ID = "d";
  private static final String PARAM_NEED_AMOUNT = "m";
  private static final String PARAM_DESK_TOURNAMET_ID = "t";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("MOREMONEY");

    Player player = server.getCurrentPlayer();
    if (player == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    if ((params.containsKey("d")) && (params.containsKey("m"))) {
      int deskID = ParamParser.getInt(params, "d");
      BigDecimal amount = new BigDecimal(ParamParser.getFloat(params, "m"));
      Desk desk;
      Desk desk;
      if (params.containsKey("t"))
        desk = Desk.getDeskByID(server.getTournamentDesks(ParamParser.getInt(params, "t")), deskID);
      else {
        desk = Desk.getDeskByID(server.getDesks(), deskID);
      }

      if (desk != null) {
        if (desk.getPlacesList().getPlace(player) != null) {
          if (player.getAmount(desk.getMoneyType()).floatValue() < amount.floatValue()) {
            response.setResultStatus(false, "NOT ENOUGH MONEY");
          }
          else {
            desk.getMoneyRequestsList().addMoneyRequest(player, amount);
            response.setResultStatus(true);
          }
        }
        else {
          response.setResultStatus(false, "Bad parameters");
        }
      }
      else
        response.setResultStatus(false, "Bad parameters");
    }
    else
    {
      response.setResultStatus(false, "Bad parameters");
    }

    return response;
  }
}