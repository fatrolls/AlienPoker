package tournaments.team.dbupdaters;

import game.Player;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import server.Server;
import tournaments.team.Team;
import tournaments.team.TeamTournament;
import tournaments.team.TeamTournamentScheduler;
import tournaments.team.TeamTournamentStage;
import tournaments.team.TournamentTeams;
import utils.CommonLogger;

public class TeamsHistoryUpdater
{
  public static final String DB_HISTORY_TABLE = "teams_history";
  public static final String DB_PARAM_STAGE = "stage";
  public static final String SQL_INSERT = "insert into teams_history set team_id = ?, team_leader = ?, tour_id=?, stage = ?, team_name = ?, team_won = ?, team_loose = ? ";
  private TeamTournament teamTournament;
  private ArrayList localCopy;

  public TeamsHistoryUpdater(TeamTournament teamTournament)
  {
    this.teamTournament = teamTournament;

    List teams = teamTournament.getTournamentTeams().getTeams();

    synchronized (teams) {
      localCopy = new ArrayList(teams.size());
      localCopy.addAll(teams);
    }
  }

  public void updateHistory()
  {
    Connection dbConn = Server.getDbConnection();
    PreparedStatement pstmt = null;
    try
    {
      pstmt = dbConn.prepareStatement("insert into teams_history set team_id = ?, team_leader = ?, tour_id=?, stage = ?, team_name = ?, team_won = ?, team_loose = ? ");
      Iterator iter = localCopy.iterator();
      while (iter.hasNext()) {
        Team team = (Team)iter.next();
        pstmt.setInt(1, team.getTeamId());
        pstmt.setInt(2, team.getTeamLeader().getID());
        pstmt.setInt(3, teamTournament.getID());
        pstmt.setInt(4, teamTournament.getTournamentSheduler().getCurrentStage().getStage());
        pstmt.setString(5, team.getName());
        pstmt.setInt(6, team.getWons());
        pstmt.setInt(7, team.getLooses());

        pstmt.executeUpdate();
      }

      pstmt.close();

      dbConn.close();
    } catch (SQLException e) {
      if (pstmt != null) {
        try {
          pstmt.close();
        } catch (SQLException e1) {
          CommonLogger.getLogger().warn(e1);
        }
      }
      try
      {
        dbConn.close();
      } catch (SQLException e1) {
        CommonLogger.getLogger().warn(e1);
      }

      CommonLogger.getLogger().warn(e);
      throw new RuntimeException(e.getMessage());
    }
  }
}