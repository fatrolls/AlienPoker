package commands;

import feedbacks.FeedBackTopic;
import feedbacks.FeedBackTopics;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import server.Server;

public class CmdLoadFeedBackTopics extends Command
{
  public boolean execute()
    throws IOException
  {
    String query = "select * from feedback_topics order by topic_name";
    boolean status;
    try
    {
      PreparedStatement statement = getDbConnection().prepareStatement(query);
      ResultSet result = statement.executeQuery();

      while (result.next()) {
        int topicId = result.getInt("topic_id");
        String topicName = result.getString("topic_name").trim();

        FeedBackTopic topic = new FeedBackTopic(topicId, topicName);
        Server.getFeedBackTopics().addTopic(topic);
      }

      result.close();
      statement.close();
      status = true;
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }

    return status;
  }
}