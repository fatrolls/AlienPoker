package game.buddylist;

import game.Desk;
import game.Player;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import org.apache.log4j.Logger;
import utils.CommonLogger;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class BuddyInvitation
{
  private Date date;
  private Player player;
  private Desk desk;
  private String message;
  private static final String TAG_NAME_INVITE = "INV";
  private static final String OUT_PARAM_MESSAGE = "MSG";

  public BuddyInvitation(Player player, String message, Desk desk)
  {
    this.player = player;
    this.desk = desk;
    date = new Date();
    this.message = message;
  }

  public Desk getDesk() {
    return desk;
  }

  public Player getPlayer() {
    return player;
  }

  public String toXML() {
    XMLDoc doc = new XMLDoc();
    XMLTag tag = doc.startTag("INV");
    tag.addParam("MSG", message);
    try {
      tag.setTagContent(player.toXML(0) + desk.toXML());
    } catch (UnsupportedEncodingException e) {
      CommonLogger.getLogger().warn(e);
    }
    String xml = doc.toString();
    doc.invalidate();
    return xml;
  }

  public Date getDate() {
    return date;
  }
}