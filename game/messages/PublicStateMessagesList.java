package game.messages;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;
import utils.xml.XMLTag;

public class PublicStateMessagesList extends StateMessagesList
{
  private static final int LIST_LENGTH = 20;

  public PublicStateMessagesList()
  {
    super(20);
  }

  public void addPublicMessage(int code)
  {
    addMessage(new PublicMessage(code));
  }

  public void addPublicMessage(int code, int time)
  {
    PublicMessage msg = new PublicMessage(code);
    msg.setRemainingTime(time);

    addMessage(msg);
  }

  public void addPublicMessage(int code, int who, int status)
  {
    addMessage(new PublicMessage(code, who, status));
  }

  public void addPublicMessage(String nick, int code, int who, int status)
  {
    addMessage(new PublicMessage(nick, code, who, status));
  }

  public void addPublicMessage(int code, int who, int status, BigDecimal amount)
  {
    addMessage(new PublicMessage(code, who, status, amount));
  }

  public void addPublicMessage(String nick, int code, int who, int status, BigDecimal amount)
  {
    addMessage(new PublicMessage(nick, code, who, status, amount));
  }

  public String toXML()
  {
    XMLTag tag = toXMLTag();
    String xml = tag.toString();
    tag.invalidate();

    return xml;
  }

  public XMLTag toXMLTag()
  {
    XMLTag tag = new XMLTag("C");
    Iterator it = messages.iterator();

    StringBuffer buffer = new StringBuffer();
    while (it.hasNext()) {
      StateMessage msg = (StateMessage)it.next();
      try {
        buffer.append(msg.toXML());
      }
      catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
      }

    }

    tag.setTagContent(buffer.toString());

    return tag;
  }
}