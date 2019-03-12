package game.buddylist;

import game.Player;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import server.Server;
import utils.CommonLogger;

public class BuddyListUpdateBlockedAtDB
  implements Runnable
{
  public static final String SQL_UPDATE = "update buddies_list set blocked=? where owner = ? and buddy =? ";
  private Player owner;
  private Player buddy;
  private int blocked;

  public BuddyListUpdateBlockedAtDB(Player owner, Player buddy, int blocked)
  {
    this.owner = owner;
    this.buddy = buddy;
    this.blocked = blocked;
  }

  public void run()
  {
    Connection dbConn = Server.getDbConnection();
    try
    {
      PreparedStatement pstmt1 = dbConn.prepareStatement("update buddies_list set blocked=? where owner = ? and buddy =? ");
      pstmt1.setInt(1, blocked);
      pstmt1.setInt(2, owner.getID());
      pstmt1.setInt(3, buddy.getID());
      pstmt1.execute();

      pstmt1.close();
    }
    catch (SQLException e) {
      CommonLogger.getLogger().warn("Cannot Update Buddy Status: ", e);
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