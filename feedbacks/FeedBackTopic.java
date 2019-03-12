package feedbacks;

public class FeedBackTopic
{
  public static final String FEEDBACK_TOPICS_TABLE = "feedback_topics";
  public static final String FEEDBACK_TOPIC_ID = "topic_id";
  public static final String FEEDBACK_TOPIC_NAME = "topic_name";
  private int topicId;
  private String topicName;

  public FeedBackTopic(int topicId, String topicName)
  {
    this.topicId = topicId;
    this.topicName = topicName;
  }

  public int getTopicId() {
    return topicId;
  }

  public String getTopicName() {
    return topicName;
  }
}