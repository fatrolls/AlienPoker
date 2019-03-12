package processors;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import server.Response;
import server.Server;
import server.Server.TotalStats;
import utils.xml.XMLTag;

public class GetTotalStatsCommandProcessor
  implements RequestCommandProcessor
{
  public static final String TAG_NAME_TOTAL_STATS = "TSTATS";
  public static final String OUT_PARAM_ACTIVE_TABLES = "ADESKS";
  public static final String OUT_PARAM_TOTAL_PLAYERS = "PLAYERS";
  public static final String OUT_PARAM_ACTIVE_PLAYERS = "APLAYERS";
  public static final String OUT_PARAM_HOLDEM_PLAYERS = "HPLAYERS";
  public static final String OUT_PARAM_NOLIMIT_PLAYERS = "NLPLAYERS";
  public static final String OUT_PARAM_TOURNAMENT_PLAYERS = "TPLAYERS";
  public static final String OUT_PARAM_NONEMPTY_TABLES = "NONEMPTYTABLES";
  public static final String OUT_PARAM_MAX_POT = "MAXPOT";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("GETTOTALSSTATS");

    Server.TotalStats t = Server.getTotalStats();
    XMLTag tag = new XMLTag("TSTATS");

    tag.addParam("PLAYERS", t.getTotalPlayers());

    tag.addParam("APLAYERS", t.getActivePlayers());
    tag.addParam("NONEMPTYTABLES", t.getNonEmptyTables());
    tag.addParam("MAXPOT", t.getMaxPot().toString());

    response.setResultStatus(true);
    response.setParametersXML(tag.toString());

    tag.invalidate();

    return response;
  }
}