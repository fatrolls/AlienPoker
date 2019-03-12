package game.buddylist;

import game.Player;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import server.Server;
import utils.CommonLogger;

public class BuddyListInsertToDB
  implements Runnable
{
  public static final String SQL_DELETE = "delete from buddies_list where owner = ? and buddy =? ";
  public static final String SQL_INSERT = "insert into buddies_list set owner = ?, buddy =? ";
  private Player owner;
  private Player buddy;

  public BuddyListInsertToDB(Player owner, Player buddy)
  {
    this.owner = owner;
    this.buddy = buddy;
  }

  public void run()
  {
    Connection dbConn = Server.getDbConnection();
    try
    {
      dbConn.setAutoCommit(false);

      PreparedStatement pstmt = dbConn.prepareStatement("delete from buddies_list where owner = ? and buddy =? ");
      pstmt.setInt(1, owner.getID());
      pstmt.setInt(2, buddy.getID());
      pstmt.executeUpdate();

      PreparedStatement pstmt1 = dbConn.prepareStatement("insert into buddies_list set owner = ?, buddy =? ");
      pstmt1.setInt(1, owner.getID());
      pstmt1.setInt(2, buddy.getID());
      pstmt1.execute();

      dbConn.commit();

      pstmt.close();
      pstmt1.close();
    }
    catch (SQLException e) {
      CommonLogger.getLogger().warn("Cannot Update/Save Buddy: ", e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    finally {
      try {
        dbConn.setAutoCommit(true);
        dbConn.close();
      }
      catch (Exception e) {
        CommonLogger.getLogger().warn("Cannot Close Connection: ", e);
      }
    }
  }
}