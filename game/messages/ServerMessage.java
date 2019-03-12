package game.messages;

import java.io.UnsupportedEncodingException;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class ServerMessage extends StateMessage
{
  private static final String PARAM_NAME_MESSAGE_TEXT = "TEXT";
  private String text;

  public ServerMessage(String text)
  {
    super(0, 0);
    this.text = text;
  }

  public String toXML()
    throws UnsupportedEncodingException
  {
    XMLDoc doc = new XMLDoc();
    XMLTag tag = doc.startTag("M");
    tag.addParam("ID", getID());
    tag.addParam("CODE", getCode());
    tag.addParam("TEXT", getText());

    String xml = doc.toString();
    doc.invalidate();

    return xml;
  }

  public String getText()
  {
    return text == null ? "" : text;
  }
}