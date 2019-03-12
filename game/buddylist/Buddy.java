package game.buddylist;

import game.Player;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class Buddy
{
  public static final String DB_PARAM_OWNER = "owner";
  public static final String DB_PARAM_BUDDY = "buddy";
  public static final String DB_PARAM_BLOCKED = "blocked";
  public static final String DB_TABLE = "buddies_list";
  private Player buddy;
  private boolean blocked;
  private static final String TAG_NAME_BUDDY = "BDY";
  private static final String OUT_PARAM_BLOCKED = "BLCK";

  public Buddy(Player buddy)
  {
    this.buddy = buddy;
  }

  public Buddy(Player buddy, boolean blocked) {
    this.buddy = buddy;
    this.blocked = blocked;
  }

  public Player getBuddy() {
    return buddy;
  }

  public void block() {
    blocked = true;
  }

  public void unblock() {
    blocked = false;
  }

  public boolean isBlocked() {
    return blocked;
  }

  public boolean equals(Object o) {
    if ((o instanceof Buddy)) {
      return buddy.equals(((Buddy)o).getBuddy());
    }
    return false;
  }

  public int hashCode() {
    return buddy.hashCode();
  }

  public String toXML() {
    XMLDoc doc = new XMLDoc();
    XMLTag tag = doc.startTag("BDY");
    tag.addParam("ID", buddy.getID());
    tag.addParam("LOGIN", buddy.getLogin());
    tag.addParam("COUNTRY", buddy.getCountry());
    tag.addParam("CITY", buddy.getCity());
    tag.addParam("BLCK", blocked ? 1 : 0);

    String xml = doc.toString();
    doc.invalidate();
    return xml;
  }
}