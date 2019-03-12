package game.privatedesks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import server.Server;
import utils.CommonLogger;

public class DropPrivateDeskDbCommand
{
  private static final String INSERT_SQL = "delete from desks where desk_id = ? and d_private= 1 ";

  public int dropDesk(int deskId)
  {
    Connection dbConn = Server.getDbConnection();
    PreparedStatement pstmt = null;
    int status;
    try
    {
      pstmt = dbConn.prepareStatement("delete from desks where desk_id = ? and d_private= 1 ", 1);

      pstmt.setInt(1, deskId);
      status = pstmt.executeUpdate();

      pstmt.close();
      dbConn.close();
    }
    catch (SQLException e) {
      if (pstmt != null) {
        try {
          pstmt.close();
        } catch (SQLException e1) {
          CommonLogger.getLogger().warn(e1);
        }
      }
      try
      {
        dbConn.close();
      } catch (SQLException e1) {
        CommonLogger.getLogger().warn(e1);
      }

      CommonLogger.getLogger().warn("Cannot drop private desk", e);
      throw new RuntimeException(e.getMessage());
    }

    return status;
  }
}