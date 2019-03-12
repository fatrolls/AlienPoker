package commands.safeupdaters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import server.Server;
import utils.CommonLogger;
import utils.Log;

public class HandIncrease
  implements Runnable
{
  public static final String DB_NAME = "hand";
  public static final String DB_PARAM_LAST = "last";
  private static final String SQL_HAND = "UPDATE " + "hand" + " SET " + "last" + " = if( ? > " + "last" + " , ? , " + "last" + " + 1 ) ";
  private long hand;

  public HandIncrease(long hand)
  {
    this.hand = hand;
  }

  public void run()
  {
    Connection dbConn = Server.getDbConnection();
    PreparedStatement pstmt = null;
    try
    {
      pstmt = dbConn.prepareStatement(SQL_HAND);
      pstmt.setLong(1, hand);
      pstmt.setLong(2, hand);
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

      CommonLogger.getLogger().warn("Cannot update hand number", e);
      throw new RuntimeException(e.getMessage());
    }
  }
}