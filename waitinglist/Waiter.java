package waitinglist;

import game.Desk;
import game.Player;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class Waiter
{
  public static final int THINKING_TIME = 160000;
  private Player player;
  private WaitingList waitingList;
  private Date enterDate;
  private boolean joining;
  private Date joinDate;
  private Desk joinDesk;
  private int minPlayers = -1;
  private static final String OUT_PARAM_TAG_NAME = "DESK";
  private static final String OUT_PARAM_DESKID = "DESKID";
  private static final String OUT_PARAM_JOINING_STATUS = "JOINING";
  private static final String OUT_PARAM_ENTER_DATE = "ENTERDATE";
  private static final String OUT_PARAM_JOININGDATE = "JOININGDATE";
  private static final String OUT_PARAM_TAG = "DESK";
  private static final String OUT_PARAM_DESKNAME = "DESKNAME";
  private static final String OUT_PARAM_TOTIMEOUT = "TOTIMEOUT";

  public Waiter(Player player, WaitingList waitingList)
  {
    this.player = player;
    this.waitingList = waitingList;
    enterDate = new Date();
    joining = false;
  }

  public void join(Desk desk) {
    joinDesk = desk;
    joining = true;
    joinDate = new Date();
  }

  public void unjoin() {
    joining = false;
  }

  public Player getPlayer() {
    return player;
  }

  public WaitingList getWaitingList() {
    return waitingList;
  }

  public boolean equals(Object obj) {
    Waiter w = (Waiter)obj;
    return w.getPlayer().getID() == player.getID();
  }

  public Date getEnterDate() {
    return enterDate;
  }

  public Date getJoinDate() {
    return joinDate;
  }

  public Desk getJoinDesk() {
    return joinDesk;
  }

  public boolean isJoining() {
    return joining;
  }

  public String toXML() throws UnsupportedEncodingException {
    XMLDoc xmlDoc = new XMLDoc();
    XMLTag tag = xmlDoc.startTag("DESK");
    Desk jd = getJoinDesk();
    if (jd != null) {
      tag.addParam("DESKID", getJoinDesk().getID());
    }

    XMLTag tagPlayer = xmlDoc.startTag("PLAYER");
    if (jd != null)
      tagPlayer.setTagContent(player.toXML(jd.getMoneyType()));
    else {
      tagPlayer.setTagContent(player.toXML(1));
    }
    tag.addNestedTag(tagPlayer);

    String xml = xmlDoc.toString();
    xmlDoc.invalidate();

    return xml;
  }

  public XMLTag toXMLTag() {
    XMLTag tag = new XMLTag("DESK");
    if (getJoinDesk() != null) {
      tag.addParam("DESKID", getJoinDesk().getID());
    }

    return tag;
  }

  public int getMinPlayers() {
    return minPlayers;
  }

  public void setMinPlayers(int minPlayers) {
    this.minPlayers = minPlayers;
  }
}