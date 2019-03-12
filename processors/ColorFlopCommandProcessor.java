package processors;

import game.Desk;
import game.Player;
import game.colorflop.ColorFlop;
import game.colorflop.ColorStake;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;

public class ColorFlopCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_DESK_ID = "d";
  private static final String PARAM_STAKE_AMOUNT = "m";
  private static final String PARAM_STAKE_TYPE = "o";
  private static final BigDecimal COLOR_FLOP_MIN_STAKE = new BigDecimal(0.01D).setScale(2, 5);

  public Response process(HashMap params, Server server) throws IOException
  {
    Response response = new Response("COLORFLOP");
    Player currentPlayer = server.getCurrentPlayer();
    if (server.getCurrentPlayer() != null) {
      if (params.containsKey("d")) {
        int deskID = ParamParser.getInt(params, "d");

        Desk desk = Desk.getDeskByID(server.getDesks(), deskID);

        if (desk != null)
        {
          if ((params.containsKey("m")) && (params.containsKey("o")))
          {
            BigDecimal amount = new BigDecimal(ParamParser.getFloat(params, "m")).setScale(2, 5);
            int type = ParamParser.getInt(params, "o");

            if ((type == ColorFlop.FLOP_BLACK) || (type == ColorFlop.FLOP_RED)) {
              if ((amount.compareTo(COLOR_FLOP_MIN_STAKE) >= 0) && (amount.compareTo(currentPlayer.getAmount(desk.getMoneyType())) <= 0))
              {
                currentPlayer.decreaseAmount(amount, desk.getMoneyType());

                desk.getColorFlop().registerStake(new ColorStake(currentPlayer, desk, type, amount));

                response.setResultStatus(true);
              } else {
                response.setResultStatus(false, "NEED MORE MONEY");
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
        else
        {
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