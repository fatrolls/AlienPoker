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

public class TeamTournamentsStageUpdater
{
  public static final String DB_PARAM_TEAM_ID = "team_id";
  public static final String DB_PARAM_PLAYER_ID = "player_id";
  public static final String SQL_INSERT = "update tournaments set tour_status = ?, tour_stage =  ? where tour_id = ? ";
  public static final String SQL_DROP_BAD_TEAMS = "delete from teams where team_id = ? and tour_id = ? ";
  public static final String SQL_DROP_BAD_TEAM_PLAYERS = "delete from team_players where team_id = ?";
  public static final String SQL_PLAYERS_HISTORY_INSERT = "insert into team_players_history set team_id = ?, player_id = ?";
  private TeamTournament teamTournament;
  private ArrayList localCopy;

  public TeamTournamentsStageUpdater(TeamTournament teamTournament)
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
    PreparedStatement pstmtTeams = null;
    PreparedStatement pstmtTeamPlayers = null;
    PreparedStatement pstmtTeamPlayersHistory = null;
    try
    {
      dbConn.setAutoCommit(false);

      pstmt = dbConn.prepareStatement("update tournaments set tour_status = ?, tour_stage =  ? where tour_id = ? ");

      TeamTournamentStage nextStage = teamTournament.getTournamentSheduler().getNextStage();
      int stage;
      int status;
      int stage;
      if (nextStage == null) {
        int status = 3;
        stage = teamTournament.getTournamentSheduler().getCurrentStage().getStage();
      } else {
        status = 7;
        stage = nextStage.getStage();
      }

      pstmt.setInt(1, status);
      pstmt.setInt(2, stage);
      pstmt.setInt(3, teamTournament.getID());
      pstmt.executeUpdate();

      pstmtTeams = dbConn.prepareStatement("delete from teams where team_id = ? and tour_id = ? ");
      pstmtTeamPlayers = dbConn.prepareStatement("delete from team_players where team_id = ?");
      pstmtTeamPlayersHistory = dbConn.prepareStatement("insert into team_players_history set team_id = ?, player_id = ?");

      Iterator iter = localCopy.iterator();
      int size = localCopy.size();
      while (iter.hasNext()) {
        Team team = (Team)iter.next();

        if ((size == 2) || (team.getLooses() > team.getTotalPlayers() / 2)) {
          pstmtTeams.setInt(1, team.getTeamId());
          pstmtTeams.setInt(2, teamTournament.getID());

          pstmtTeams.executeUpdate();

          pstmtTeamPlayers.setInt(1, team.getTeamId());
          pstmtTeamPlayers.executeUpdate();

          Iterator membersIter = team.getMembers().iterator();
          while (membersIter.hasNext()) {
            Player player = (Player)membersIter.next();
            pstmtTeamPlayersHistory.setInt(1, team.getTeamId());
            pstmtTeamPlayersHistory.setInt(2, player.getID());

            pstmtTeamPlayersHistory.executeUpdate();
          }
        }
      }

      dbConn.commit();
      pstmt.close();
      pstmtTeams.close();
      pstmtTeamPlayers.close();
      pstmtTeamPlayersHistory.close();

      dbConn.close();
    } catch (SQLException e) {
      if (pstmt != null) {
        try {
          pstmt.close();
        } catch (SQLException e1) {
          CommonLogger.getLogger().warn(e1);
        }
      }

      if (pstmtTeams != null) {
        try {
          pstmtTeams.close();
        } catch (SQLException e1) {
          CommonLogger.getLogger().warn(e1);
        }
      }

      if (pstmtTeamPlayers != null) {
        try {
          pstmtTeamPlayers.close();
        } catch (SQLException e1) {
          CommonLogger.getLogger().warn(e1);
        }
      }

      if (pstmtTeamPlayersHistory != null) {
        try {
          pstmtTeamPlayersHistory.close();
        } catch (SQLException e1) {
          CommonLogger.getLogger().warn(e1);
        }
      }

      try
      {
        dbConn.rollback();
        dbConn.setAutoCommit(true);
        dbConn.close();
      } catch (SQLException e1) {
        CommonLogger.getLogger().warn(e1);
      }

      CommonLogger.getLogger().warn(e);
      throw new RuntimeException(e.getMessage());
    }
  }
}