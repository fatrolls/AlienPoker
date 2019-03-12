package processors;

import game.stats.PlayerStat;
import game.stats.PlayersStats;
import java.io.IOException;
import java.util.HashMap;
import server.Response;
import server.Server;

public class ClearPlayerStatsCommandProcessor
  implements RequestCommandProcessor
{
  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response();
    response.setOnCommand("CLEARPLAYERSTATS");

    if (server.getCurrentPlayer() != null)
    {
      PlayersStats.getPlayerStat(server.getCurrentPlayer(), 1).reset();
      response.setResultStatus(true);
    }
    else
    {
      response.setResultStatus(false, "Authorization first");
    }

    return response;
  }
}