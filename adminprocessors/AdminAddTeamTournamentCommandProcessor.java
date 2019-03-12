package adminprocessors;

import commands.CmdReadTeamTournament;
import java.sql.Connection;
import java.util.Hashtable;
import org.apache.log4j.Logger;
import server.Server;
import tournaments.Tournament;
import tournaments.team.TeamTournament;
import utils.CommonLogger;

public class AdminAddTeamTournamentCommandProcessor
  implements AdminXMLResponse
{
  private static final String MSG_TOURNAMENT_WAS_ADDED = "Tournament was added";
  private static final String MSG_TOURNAMENT_BAD_TOURNAMENT_STATUS = "Tournament has bad status";

  public Hashtable addTournament(int tournamentId)
  {
    Hashtable response = new Hashtable();

    boolean error = false;
    String reason = "";

    TeamTournament t = (TeamTournament)Server.getTournamentByID(tournamentId);
    if (t != null) {
      if (t.getStatus() == 0) {
        t.reloadTournament();
      } else {
        error = true;
        reason = "Tournament has bad status";
      }
    }
    else {
      CmdReadTeamTournament cmd = new CmdReadTeamTournament(Tournament.getTournamentsList(), tournamentId);
      Connection dbConn = Server.getDbConnection();
      cmd.setDbConnection(dbConn);
      try {
        cmd.execute();
      } catch (Exception ex) {
        error = true;
        reason = e.getMessage();
        CommonLogger.getLogger().warn(e.getMessage(), e);
      } finally {
        try {
          dbConn.close();
        } catch (Exception ex) {
          CommonLogger.getLogger().warn(ex.getMessage(), ex);
        }
      }
    }

    if (error) {
      response.put("STATUS", "ERROR");
      response.put("RESPONSE", reason);
      return response;
    }

    response.put("STATUS", "OK");
    response.put("RESPONSE", "Tournament was added");
    return response;
  }
}