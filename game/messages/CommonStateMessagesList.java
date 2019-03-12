package game.messages;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;
import utils.xml.XMLTag;

public class CommonStateMessagesList extends StateMessagesList
{
  private static final int LIST_LENGTH = 20;

  public CommonStateMessagesList()
  {
    super(20);
  }

  public void addCommonMessage(int code)
  {
    addMessage(new CommonMessage(code));
  }

  public void addCommonMessage(int code, long currentGameId) {
    addMessage(new CommonMessage(code, currentGameId));
  }

  public void addCommonMessage(int code, int time)
  {
    CommonMessage msg = new CommonMessage(code);
    msg.setRemainingTime(time);

    addMessage(msg);
  }

  public void addCommonMessage(int code, int who, int status)
  {
    addMessage(new CommonMessage(code, who, status));
  }

  public void addCommonMessage(int code, int who, String order) {
    addMessage(new CommonMessage(code, who, order));
  }

  public void addCommonMessage(int code, BigDecimal minBet, BigDecimal maxBet, BigDecimal ante, BigDecimal bringIn, int level) {
    addMessage(new CommonMessage(code, minBet, maxBet, ante, bringIn, level));
  }

  public void addCommonMessage(String nick, int code, int who, int status) {
    addMessage(new CommonMessage(nick, code, who, status));
  }

  public void addCommonMessage(int code, int who, int status, BigDecimal amount)
  {
    addMessage(new CommonMessage(code, who, status, amount));
  }

  public void addCommonMessage(String nick, int code, int who, int status, BigDecimal amount)
  {
    addMessage(new CommonMessage(nick, code, who, status, amount));
  }

  public void addCommonMessage(String nick, int code, int who, int status, BigDecimal amount, int playerID) {
    addMessage(new CommonMessage(nick, code, who, status, amount, playerID));
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

    StringBuffer buffer = new StringBuffer();
    synchronized (messages) {
      Iterator it = messages.iterator();
      synchronized (it) {
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
    }
    tag.setTagContent(buffer.toString());

    return tag;
  }
}