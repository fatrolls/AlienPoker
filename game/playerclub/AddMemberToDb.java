package game.playerclub;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import server.Server;
import utils.CommonLogger;

public class AddMemberToDb
  implements Runnable
{
  public static final String SQL_INSERT = "insert into players_club set rating =?, points = ?, user_id = ?, reg_date = NOW()";
  private ClubMember clubMember;

  public AddMemberToDb(ClubMember clubMember)
  {
    this.clubMember = clubMember;
  }

  public void run()
  {
    Connection dbConn = Server.getDbConnection();
    try
    {
      PreparedStatement pstmt = dbConn.prepareStatement("insert into players_club set rating =?, points = ?, user_id = ?, reg_date = NOW()");
      pstmt.setInt(1, clubMember.getRating());
      pstmt.setFloat(2, clubMember.getPoints().floatValue());
      pstmt.setFloat(3, clubMember.getId());
      pstmt.executeUpdate();

      pstmt.close();
    }
    catch (SQLException e) {
      CommonLogger.getLogger().warn("Cannot Update/Save ClubMember: ", e);
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