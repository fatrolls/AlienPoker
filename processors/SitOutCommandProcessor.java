package processors;

import game.Desk;
import game.Game;
import game.Player;
import java.io.IOException;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;

public class SitOutCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_DESK_ID = "d";
  private static final String PARAM_DESK_TOURNAMET_ID = "t";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("SITOUT");

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
          Player player = server.getCurrentPlayer();
          Game game = desk.getGame();
          if ((game != null) && (game.acceptSitOut(player))) {
            response.setResultStatus(true);
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
    }
    else {
      response.setResultStatus(false, "Authorization first");
    }

    return response;
  }
}