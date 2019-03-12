package game.playerclub.dirtypoints;

import game.Player;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import server.Server;
import utils.CommonLogger;

public class DirtyPointsSaver
  implements Runnable
{
  public static final String SQL_UPDATE = "update players_club set points = ? where user_id =? ";
  private Player player;

  public DirtyPointsSaver(Player player)
  {
    this.player = player;
  }

  public void run()
  {
    Connection dbConn = Server.getDbConnection();
    try
    {
      PreparedStatement pstmt = dbConn.prepareStatement("update players_club set points = ? where user_id =? ");
      pstmt.setDouble(1, player.getDirtyPoints().doubleValue());
      pstmt.setInt(2, player.getID());
      pstmt.executeUpdate();

      pstmt.close();
    }
    catch (SQLException e) {
      CommonLogger.getLogger().warn("Cannot Update Dirty Points: ", e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    finally {
      try {
        dbConn.close();
      }
      catch (Exception e) {
        CommonLogger.getLogger().warn("Cannot Close Connection: ", e);
      }
    }
  }
}