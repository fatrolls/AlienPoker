package feedbacks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import server.Server;
import utils.CommonLogger;

public class AddFeedBackToDB
{
  private final String INSERT_SQL = "insert into " + "feedbacks" + " set " + "topic_id" + " = ?, " + "topic_name" + " =?, " + "message" + " = ? ";
  private int topicId;
  private String topicName;
  private String message;

  public AddFeedBackToDB(int topicId, String topicName, String message)
  {
    this.topicId = topicId;
    this.topicName = topicName;
    this.message = message;
  }

  public AddFeedBackToDB(String topicName, String message) {
    topicId = 0;
    this.topicName = topicName;
    this.message = message;
  }

  public void execute()
  {
    Connection dbConn = Server.getDbConnection();
    PreparedStatement pstmt = null;
    try
    {
      pstmt = dbConn.prepareStatement(INSERT_SQL);

      pstmt.setInt(1, topicId);
      pstmt.setString(2, topicName.trim());
      pstmt.setString(3, message.trim());
      pstmt.executeUpdate();

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

      CommonLogger.getLogger().warn("Cannot Add FeedBack To DB", e);
      throw new RuntimeException(e.getMessage());
    }
  }
}