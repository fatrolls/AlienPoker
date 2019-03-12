package commands.safeupdaters;

import game.Player;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import server.Server;
import tournaments.Tournament;
import utils.Log;

public class AddWinnerToHistory
  implements Runnable
{
  private Tournament tournament;
  private int place;
  private BigDecimal won;
  private Player player;
  private static final String DB_TOUR_HISTORY_PLACES_TABLE = "tournaments_history_places";
  private static final String DB_PARAM_HISTORY_ID = "history_id";
  private static final String DB_PARAM_USER_ID = "user_id";
  private static final String DB_PARAM_USER_LOGIN = "us_login";
  private static final String DB_PARAM_USER_PLACE = "us_place";
  private static final String DB_PARAM_USER_WON = "us_winnered_amount";
  private static final String SQL_STRING = "insert into " + "tournaments_history_places" + " set  " + "history_id" + " =?, " + "user_id" + " =?, " + "us_login" + " =?, " + "us_place" + " =?, " + "us_winnered_amount" + " =? ";

  public AddWinnerToHistory(Tournament tournament, int place, BigDecimal won, Player player)
  {
    this.tournament = tournament;
    this.place = place;
    this.won = won;
    this.player = player;
  }

  public void run()
  {
    Connection dbConn = Server.getDbConnection();
    PreparedStatement pstmt = null;
    try
    {
      pstmt = dbConn.prepareStatement(SQL_STRING);
      pstmt.setInt(1, tournament.getHistoryID());
      pstmt.setInt(2, player.getID());
      pstmt.setString(3, player.getLogin());
      pstmt.setInt(4, place);
      pstmt.setDouble(5, won.doubleValue());

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

      Log.out("Class AddWinnerToHistory->run() : Error: " + e.getMessage());
      throw new RuntimeException(e.getMessage());
    }
  }
}