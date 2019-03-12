package commands.safeupdaters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import server.Server;
import tournaments.Tournament;
import utils.Log;

public class StartTournamentHistory
  implements Runnable
{
  private Tournament tournament;
  private static final String DB_TOUR_HISTORY_TABLE = "tournaments_history";
  private static final String SQL_STRING = "insert into " + "tournaments_history" + " select null, tournaments.*, null, null, null from " + "tournaments" + " where " + "tour_id" + "=?";

  public StartTournamentHistory(Tournament tournament)
  {
    this.tournament = tournament;
  }

  public void run()
  {
    Connection dbConn = Server.getDbConnection();
    PreparedStatement pstmt = null;
    try
    {
      pstmt = dbConn.prepareStatement(SQL_STRING, 1);
      pstmt.setInt(1, tournament.getID());

      pstmt.executeUpdate();

      ResultSet res = pstmt.getGeneratedKeys();
      if (res.next()) {
        tournament.setHistoryID(res.getInt(1));
      }
      res.close();
      pstmt.close();

      dbConn.close();
    } catch (SQLException e) {
      if (pstmt != null) {
        try {
          pstmt.close();
        } catch (SQLException e1) {
          Log.out(e1.getMessage());
        }
      }
      try
      {
        dbConn.close();
      } catch (SQLException e1) {
        Log.out(e1.getMessage());
      }

      Log.out("Class StartTournamentHistory->run() : Error: " + e.getMessage());
      throw new RuntimeException(e.getMessage());
    }
  }
}