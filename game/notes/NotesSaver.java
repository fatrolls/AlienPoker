package game.notes;

import game.Player;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import server.Server;
import utils.CommonLogger;

public class NotesSaver
  implements Runnable
{
  public static final String DB_PARAM_PLAYER_OWNER = "player_owner";
  public static final String DB_PARAM_PLAYER_TO = "player_to";
  public static final String DB_PARAM_RATING = "rating";
  public static final String DB_PARAM_MESSAGE = "message";
  public static final String DB_PARAM_CHAT = "chat";
  public static final String DB_TABLE = "notes";
  public static final String SQL_DELETE = "delete from notes where player_owner = ? and player_to =? ";
  public static final String SQL_INSERT = "insert into notes set player_owner = ?, player_to =?, rating = ?, message = ?, chat = ?";
  private Player owner;
  private PlayerNote note;

  public NotesSaver(Player owner, PlayerNote note)
  {
    this.owner = owner;
    this.note = note;
  }

  public void run()
  {
    Connection dbConn = Server.getDbConnection();
    try
    {
      PreparedStatement pstmt = dbConn.prepareStatement("delete from notes where player_owner = ? and player_to =? ");
      pstmt.setInt(1, owner.getID());
      pstmt.setInt(2, note.getPlayer().getID());
      pstmt.executeUpdate();

      PreparedStatement pstmt1 = dbConn.prepareStatement("insert into notes set player_owner = ?, player_to =?, rating = ?, message = ?, chat = ?");
      pstmt1.setInt(1, owner.getID());
      pstmt1.setInt(2, note.getPlayer().getID());
      pstmt1.setInt(3, note.getRating());
      pstmt1.setString(4, note.getNote());
      pstmt1.setBoolean(5, note.isChat());
      pstmt1.execute();

      pstmt.close();
      pstmt1.close();
    }
    catch (SQLException e) {
      CommonLogger.getLogger().warn("Cannot Update/Save Note: ", e);
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