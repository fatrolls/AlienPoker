package processors;

import game.Desk;
import game.Game;
import game.Place;
import game.Player;
import game.messages.CommonStateMessagesList;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;
import tournaments.Tournament;

public class ReBuysCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_DESK_ID = "d";
  private static final String PARAM_DESK_TOURNAMET_ID = "t";
  private static final String MSG_CANNOT_MAKE_REBUYS = "Cannot make rebuys";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("REBUYS");

    if (server.getCurrentPlayer() != null) {
      if ((params.containsKey("d")) && (params.containsKey("t")))
      {
        int deskID = ParamParser.getInt(params, "d");

        Desk desk = Desk.getDeskByID(server.getTournamentDesks(ParamParser.getInt(params, "t")), deskID);

        Tournament tournament = Tournament.getTournamentByID(ParamParser.getInt(params, "t"));

        if ((desk != null) && (tournament != null))
        {
          if (server.getCurrentPlayer().getAmount(desk.getMoneyType()).compareTo(tournament.getReBuysPayment()) >= 0)
          {
            Place place = desk.getPlayerPlace(server.getCurrentPlayer());
            if (place != null)
            {
              HashMap map = tournament.getCurrentReBuysPlayers();
              boolean found;
              synchronized (map) {
                found = map.remove(new Integer(server.getCurrentPlayer().getID())) != null;
              }

              if (found) {
                server.getCurrentPlayer().decreaseAmount(tournament.getReBuysPayment(), tournament.getMoneyType());

                place.incDeskAmount(tournament.getReBuysAmount());

                desk.getGame().getPublicStateMessagesList().addCommonMessage(server.getCurrentPlayer().getLogin(), 110, place.getNumber(), 2, new BigDecimal(0));

                response.setResultStatus(true);
              } else {
                response.setResultStatus(false, "Cannot make rebuys");
                return response;
              }
            }
            else {
              response.setResultStatus(false, "Bad parameters");
            }
          }
          else {
            response.setResultStatus(false, "NEED MORE MONEY");
            return response;
          }
        }
        else
          response.setResultStatus(false, "Bad parameters");
      }
      else {
        response.setResultStatus(false, "Bad parameters");
      }
    }
    else response.setResultStatus(false, "Authorization first");

    return response;
  }
}