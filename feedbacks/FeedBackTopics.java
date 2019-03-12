package feedbacks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class FeedBackTopics
{
  public static final String FEEDBACK_TABLE = "feedbacks";
  public static final String FEEDBACK_TOPIC_ID = "topic_id";
  public static final String FEEDBACK_TOPIC_NAME = "topic_name";
  public static final String FEEDBACK_MESSAGE = "message";
  private final List topics = Collections.synchronizedList(new ArrayList());
  private static final String TAG_FEEDBACK = "FBK";
  private static final String TAG_TOPICS = "TPC";
  private static final String OUT_PARAM_ID = "ID";
  private static final String OUT_PARAM_TITLE = "TTL";

  public void addTopic(FeedBackTopic topic)
  {
    topics.add(topic);
  }

  public boolean hasTopic(int topicId) {
    synchronized (topics) {
      Iterator iter = topics.iterator();
      while (iter.hasNext()) {
        FeedBackTopic ft = (FeedBackTopic)iter.next();
        if (ft.getTopicId() == topicId) {
          return true;
        }
      }
      return false;
    }
  }

  public String getTopicName(int topicId) {
    synchronized (topics) {
      Iterator iter = topics.iterator();
      while (iter.hasNext()) {
        FeedBackTopic ft = (FeedBackTopic)iter.next();
        if (ft.getTopicId() == topicId) {
          return ft.getTopicName();
        }
      }
      return "";
    }
  }

  public String toXML() {
    XMLDoc doc = new XMLDoc();
    XMLTag tag = doc.startTag("FBK");

    XMLTag topics = new XMLTag("TPC");

    synchronized (this.topics) {
      Iterator iter = this.topics.iterator();
      while (iter.hasNext()) {
        FeedBackTopic ft = (FeedBackTopic)iter.next();
        XMLTag topic = new XMLTag("TPC");
        topic.addParam("ID", ft.getTopicId());
        topic.addParam("TTL", ft.getTopicName());
        topics.addNestedTag(topic);
      }
    }

    tag.addNestedTag(topics);
    String xml = doc.toString();
    doc.invalidate();

    return xml;
  }
}