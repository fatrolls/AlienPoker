package processors;

import game.stats.PlayerStat;
import game.stats.PlayersStats;
import java.io.IOException;
import java.util.HashMap;
import server.Response;
import server.Server;

public class GetPlayerStatsCommandProcessor
  implements RequestCommandProcessor
{
  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response();
    response.setOnCommand("GETPLAYERSTATS");

    if (server.getCurrentPlayer() != null)
    {
      response.setResultStatus(true);
      response.setParametersXML(PlayersStats.getPlayerStat(server.getCurrentPlayer(), 1).toXML(1));
    }
    else
    {
      response.setResultStatus(false, "Authorization first");
    }

    return response;
  }
}