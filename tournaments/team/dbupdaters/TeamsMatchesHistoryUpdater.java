package tournaments.team.dbupdaters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import server.Server;
import utils.CommonLogger;

public class TeamsMatchesHistoryUpdater
  implements Runnable
{
  public static final String DB_PARAM_TOUR_ID = "tour_id";
  public static final String DB_PARAM_TEAM_ID = "team_id";
  public static final String DB_PARAM_OPPONENT_TEAM_ID = "opponent_team_id";
  public static final String DB_PARAM_PLAYER_ID = "player_id";
  public static final String DB_PARAM_OPPONENT_ID = "opponent_id";
  public static final String DB_PARAM_WIN = "win";
  public static final String DB_GAME_END = "game_end";
  public static final int WIN = 1;
  public static final int LOOSE = 0;
  public static final String SQL_INSERT = "insert into teams_matches_history set tour_id = ?, team_id = ?, opponent_team_id = ?, player_id=?, opponent_id = ?, win = ?, game_end = NOW(), num =?, stage =? ";
  private int tourId;
  private int teamId;
  private int opponentTeamId;
  private int playerId;
  private int opponentId;
  private int win;
  private int num;
  private int stage;

  public TeamsMatchesHistoryUpdater(int tourId, int teamId, int opponentTeamId, int playerId, int opponentId, int win, int num, int stage)
  {
    this.tourId = tourId;
    this.teamId = teamId;
    this.opponentTeamId = opponentTeamId;
    this.playerId = playerId;
    this.opponentId = opponentId;
    this.win = win;
    this.num = num;
    this.stage = stage;
  }

  public void updateHistory()
  {
    Connection dbConn = Server.getDbConnection();
    PreparedStatement pstmt = null;
    try
    {
      pstmt = dbConn.prepareStatement("insert into teams_matches_history set tour_id = ?, team_id = ?, opponent_team_id = ?, player_id=?, opponent_id = ?, win = ?, game_end = NOW(), num =?, stage =? ");
      pstmt.setInt(1, tourId);
      pstmt.setInt(2, teamId);
      pstmt.setInt(3, opponentTeamId);
      pstmt.setInt(4, playerId);
      pstmt.setInt(5, opponentId);
      pstmt.setInt(6, win);
      pstmt.setInt(7, num);
      pstmt.setInt(8, stage);

      pstmt.executeUpdate();

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

  public void run()
  {
    updateHistory();
  }
}