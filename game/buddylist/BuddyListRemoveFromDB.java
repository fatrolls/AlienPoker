package game.buddylist;

import game.Player;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import server.Server;
import utils.CommonLogger;

public class BuddyListRemoveFromDB
  implements Runnable
{
  public static final String SQL_DELETE = "delete from buddies_list where owner = ? and buddy =? ";
  private Player owner;
  private Player buddy;

  public BuddyListRemoveFromDB(Player owner, Player buddy)
  {
    this.owner = owner;
    this.buddy = buddy;
  }

  public void run()
  {
    Connection dbConn = Server.getDbConnection();
    try
    {
      PreparedStatement pstmt = dbConn.prepareStatement("delete from buddies_list where owner = ? and buddy =? ");
      pstmt.setInt(1, owner.getID());
      pstmt.setInt(2, buddy.getID());
      pstmt.executeUpdate();

      pstmt.close();
    }
    catch (SQLException e) {
      CommonLogger.getLogger().warn("Cannot Remove Buddy: ", e);
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