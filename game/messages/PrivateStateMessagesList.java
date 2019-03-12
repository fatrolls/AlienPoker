package game.messages;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;
import utils.xml.XMLTag;

public class PrivateStateMessagesList extends StateMessagesList
{
  public void addPrivateMessage(int code, int time)
  {
    addMessage(new PrivateMessage(code, time));
  }

  public void addPrivateMessage(int code, int time, boolean check, BigDecimal bet, BigDecimal call, BigDecimal raise, BigDecimal bringIn, BigDecimal max)
  {
    addMessage(new PrivateMessage(code, time, check, bet, call, raise, bringIn, max));
  }

  public void addPrivateMessage(int code)
  {
    addMessage(new PrivateMessage(code, 0));
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
    XMLTag tag = new XMLTag("P");

    StringBuffer buffer = new StringBuffer();
    synchronized (messages) {
      Iterator it = messages.iterator();
      while (it.hasNext()) {
        StateMessage msg = (StateMessage)it.next();
        try {
          buffer.append(msg.toXML());
        }
        catch (UnsupportedEncodingException e) {
          throw new RuntimeException(e);
        }
      }
    }
    tag.setTagContent(buffer.toString());

    return tag;
  }
}