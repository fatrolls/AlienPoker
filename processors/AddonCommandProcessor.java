package processors;

import game.Desk;
import game.Game;
import game.Place;
import game.Player;
import game.messages.CommonStateMessagesList;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import server.ParamParser;
import server.Response;
import server.Server;
import tournaments.Tournament;

public class AddonCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_DESK_ID = "d";
  private static final String PARAM_DESK_TOURNAMET_ID = "t";
  private static final String MSG_CANNOT_MAKE_ADDON = "Cannot make addon";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("ADDON");

    if (server.getCurrentPlayer() != null) {
      if ((params.containsKey("d")) && (params.containsKey("t")))
      {
        int deskID = ParamParser.getInt(params, "d");

        Desk desk = Desk.getDeskByID(server.getTournamentDesks(ParamParser.getInt(params, "t")), deskID);

        Tournament tournament = Tournament.getTournamentByID(ParamParser.getInt(params, "t"));

        if ((desk != null) && (tournament != null))
        {
          if (server.getCurrentPlayer().getAmount(desk.getMoneyType()).compareTo(tournament.getAddonsPayment()) >= 0)
          {
            Place place = desk.getPlayerPlace(server.getCurrentPlayer());
            if (place != null)
            {
              List list = tournament.getCurrentAddonPlayers();
              boolean found;
              synchronized (list) {
                found = list.remove(server.getCurrentPlayer());
              }

              if (found) {
                server.getCurrentPlayer().decreaseAmount(tournament.getAddonsPayment(), tournament.getMoneyType());

                place.incDeskAmount(tournament.getAddonsAmount());
                response.setResultStatus(true);

                desk.getGame().getPublicStateMessagesList().addCommonMessage(server.getCurrentPlayer().getLogin(), 109, place.getNumber(), 2, new BigDecimal(0));
              }
              else {
                response.setResultStatus(false, "Cannot make addon");
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