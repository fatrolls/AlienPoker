package commands.safeupdaters;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import server.Server;
import tournaments.FeeList;
import tournaments.Tournament;
import utils.Log;

public class FinishTournamentHistory
  implements Runnable
{
  private Tournament tournament;
  private static final String DB_TOUR_HISTORY_TABLE = "tournaments_history";
  private static final String DB_PARAM_END_DATE = "tour_finished_date";
  private static final String DB_PARAM_BUYINPOOL = "tour_buy_in_pool";
  private static final String DB_PARAM_PLAYERS_QTY = "tour_players_qty";
  private static final String DB_PARAM_HISTORY_ID = "history_id";
  private static final String SQL_STRING = "update " + "tournaments_history" + " set " + "tour_finished_date" + " = NOW(),  " + "tour_buy_in_pool" + " =?,  " + "tour_players_qty" + " =?,  " + "tour_status" + " =?  " + " where " + "history_id" + " =? ";

  public FinishTournamentHistory(Tournament tournament)
  {
    this.tournament = tournament;
  }

  public void run()
  {
    Connection dbConn = Server.getDbConnection();
    PreparedStatement pstmt = null;
    try
    {
      pstmt = dbConn.prepareStatement(SQL_STRING);
      pstmt.setDouble(1, tournament.getFeeList().getFeeAmount().floatValue());
      pstmt.setDouble(2, tournament.getPlayersList().size());
      pstmt.setDouble(3, tournament.getStatus());
      pstmt.setInt(4, tournament.getHistoryID());

      pstmt.executeUpdate();

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

      Log.out("Class FinishTournamentHistory->run() : Error: " + e.getMessage());
      throw new RuntimeException(e.getMessage());
    }
  }
}