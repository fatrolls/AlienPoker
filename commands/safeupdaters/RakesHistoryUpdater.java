package commands.safeupdaters;

import game.Desk;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import server.Server;
import utils.CommonLogger;
import utils.Log;

public class RakesHistoryUpdater
  implements Runnable
{
  private Desk desk;
  long handId;
  private BigDecimal rake = new BigDecimal(0);
  private static final String DB_PARAM_HAND = "hand";
  private static final String DB_PARAM_RAKE = "rake";
  private static final String DB_PARAM_RAKE_DATE = "rake_date";
  private static final String DB_NAME = "rakes_history";
  private static final String SQL_HISTORY = "INSERT INTO " + "rakes_history" + " SET " + "hand" + " = ?, " + "rake" + " = ?, " + "desk_id" + " = ?, " + "d_limit_type" + " = ?, " + "d_poker_type" + " = ?, " + "d_name" + " = ?, " + "d_money_type" + " = ?, " + "d_min_bet" + " = ?, " + "d_max_bet" + " = ?, " + "d_ante" + " = ?, " + "d_bring_in" + " = ?, " + "rake_date" + " = NOW(), " + "d_min_amount" + " = ? ";

  public RakesHistoryUpdater(Desk desk, BigDecimal rake, long handId)
  {
    this.desk = desk;
    this.rake = rake;
    this.handId = handId;
  }

  public void run()
  {
    Connection dbConn = Server.getDbConnection();
    PreparedStatement pstmt = null;
    try
    {
      pstmt = dbConn.prepareStatement(SQL_HISTORY);

      pstmt.setLong(1, handId);
      pstmt.setFloat(2, rake.floatValue());
      pstmt.setInt(3, desk.getID());
      pstmt.setInt(4, desk.getLimitType());
      pstmt.setInt(5, desk.getPokerType());
      pstmt.setString(6, desk.getDeskName());
      pstmt.setInt(7, desk.getMoneyType());
      pstmt.setFloat(8, desk.getMinBet().floatValue());
      pstmt.setFloat(9, desk.getMaxBet().floatValue());
      pstmt.setFloat(10, desk.getAnte().floatValue());
      pstmt.setFloat(11, desk.getBringIn().floatValue());
      pstmt.setFloat(12, desk.getMinAmount().floatValue());

      pstmt.executeUpdate();
      pstmt.close();

      dbConn.close();
    }
    catch (SQLException e) {
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

      CommonLogger.getLogger().warn("Cannot update rakes history", e);
      throw new RuntimeException(e.getMessage());
    }
  }
}