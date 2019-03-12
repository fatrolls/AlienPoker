package game.chat;

import server.XMLFormatable;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class ChatMessage
  implements XMLFormatable
{
  private long time;
  private int placeNumber;
  private String text;
  private int deskId;
  private static final String OUT_TAG_MESSAGE = "MESSAGE";
  private static final String OUT_PARAM_PLACE = "PLACE";
  private static final String OUT_PARAM_TEXT = "TEXT";
  private static final String OUT_PARAM_TIME = "TIME";

  public ChatMessage()
  {
  }

  public ChatMessage(int deskId)
  {
    this.deskId = deskId;
  }

  public ChatMessage(int placeNumber, long time, String text, int deskId) {
    this.placeNumber = placeNumber;
    this.time = time;
    this.text = ChatFilter.getInstance().filter(text);
    this.deskId = deskId;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public int getPlaceNumber() {
    return placeNumber;
  }

  public void setPlaceNumber(int placeNumber) {
    this.placeNumber = placeNumber;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = ChatFilter.getInstance().filter(text);
  }

  public int getDeskId() {
    return deskId;
  }

  public void setDeskId(int deskId) {
    this.deskId = deskId;
  }

  public String toXML()
  {
    XMLDoc doc = new XMLDoc();
    XMLTag tag = doc.startTag("MESSAGE");
    tag.addParam("PLACE", placeNumber);
    tag.addParam("TEXT", text);
    tag.addParam("TIME", "" + time);
    String xml = doc.toString();
    doc.invalidate();

    return xml;
  }
}