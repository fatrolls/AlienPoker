package adminprocessors;

import java.util.Hashtable;
import server.Server;
import server.Server.TotalStats;
import utils.xml.XMLTag;

public class AdminGetTotalStatsCommandProcessor
  implements AdminXMLResponse
{
  public Hashtable getTotalStats()
  {
    Hashtable response = new Hashtable();

    Server.TotalStats t = Server.getTotalStats();
    XMLTag tag = new XMLTag("TSTATS");
    tag.addParam("ADESKS", t.getActiveTables());
    tag.addParam("PLAYERS", t.getTotalPlayers());
    tag.addParam("APLAYERS", t.getActivePlayers());
    tag.addParam("HPLAYERS", t.getHoldemPlayers());
    tag.addParam("NLPLAYERS", t.getNoLimitPlayers());
    tag.addParam("TPLAYERS", t.getTournamentPlayers());

    response.put("STATUS", "OK");
    response.put("RESPONSE", tag.toString());

    tag.invalidate();

    return response;
  }
}