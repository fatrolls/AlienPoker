package game.buddylist;

import game.Desk;
import game.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class BuddyList
{
  private Player owner;
  private final List buddies = Collections.synchronizedList(new ArrayList());
  private final List invitations = Collections.synchronizedList(new ArrayList());
  private static final String TAG_NAME_BUDY_LIST = "BLST";
  private static final String TAG_NAME_INVITS = "INVS";

  public BuddyList(Player owner)
  {
    this.owner = owner;
  }

  public Player getOwner() {
    return owner;
  }

  public boolean acceptInvitation(Player player, String message, Desk desk) {
    synchronized (buddies) {
      Iterator iter = buddies.iterator();
      while (iter.hasNext()) {
        Buddy buddy = (Buddy)iter.next();
        if (buddy.getBuddy().equals(player)) {
          if (!buddy.isBlocked()) break;
          return false;
        }

      }

    }

    BuddyInvitation invitation = new BuddyInvitation(player, message, desk);
    addInvite(invitation);

    return true;
  }

  private void addInvite(BuddyInvitation invitation)
  {
    synchronized (invitations) {
      int size = invitations.size();
      boolean flag = false;
      for (int i = size - 1; i >= 0; i--) {
        BuddyInvitation inv = (BuddyInvitation)invitations.get(i);
        if (new Date().getTime() - inv.getDate().getTime() > 60000L) {
          invitations.remove(i);
        }
        else if ((inv.getDesk().equals(invitation.getDesk())) && (inv.getPlayer().equals(invitation.getPlayer()))) {
          flag = true;
          break;
        }

      }

      if (!flag)
        invitations.add(invitation);
    }
  }

  public void blockBuddy(Player player)
  {
    synchronized (buddies) {
      Iterator iter = buddies.iterator();
      while (iter.hasNext()) {
        Buddy buddy = (Buddy)iter.next();
        if (buddy.getBuddy().equals(player)) {
          buddy.block();

          new Thread(new BuddyListUpdateBlockedAtDB(owner, player, 1)).start();
          return;
        }
      }
    }
  }

  public void unblockBuddy(Player player) {
    synchronized (buddies) {
      Iterator iter = buddies.iterator();
      while (iter.hasNext()) {
        Buddy buddy = (Buddy)iter.next();
        if (buddy.getBuddy().equals(player)) {
          buddy.unblock();

          new Thread(new BuddyListUpdateBlockedAtDB(owner, player, 0)).start();
          return;
        }
      }
    }
  }

  public boolean hasBuddy(Player player) {
    synchronized (buddies) {
      Iterator iter = buddies.iterator();
      while (iter.hasNext()) {
        Buddy buddy = (Buddy)iter.next();
        if (buddy.getBuddy().equals(player)) {
          return true;
        }
      }
      return false;
    }
  }

  public boolean hasBuddy(Buddy add) {
    synchronized (buddies) {
      Iterator iter = buddies.iterator();
      while (iter.hasNext()) {
        Buddy buddy = (Buddy)iter.next();
        if (buddy.equals(add)) {
          return true;
        }
      }
      return false;
    }
  }

  public boolean importBuddy(Buddy add)
  {
    synchronized (buddies) {
      if (!hasBuddy(add)) {
        buddies.add(add);
        return true;
      }
      return false;
    }
  }

  public boolean addBuddy(Buddy add) {
    synchronized (buddies) {
      if (!hasBuddy(add)) {
        buddies.add(add);

        new Thread(new BuddyListInsertToDB(owner, add.getBuddy())).start();
        return true;
      }
      return false;
    }
  }

  public String toXML()
  {
    XMLDoc doc = new XMLDoc();
    XMLTag tag = doc.startTag("BLST");
    StringBuffer buffer = new StringBuffer();

    synchronized (buddies) {
      Iterator iter = buddies.iterator();
      while (iter.hasNext()) {
        Buddy buddy = (Buddy)iter.next();
        buffer.append(buddy.toXML());
      }
    }
    tag.setTagContent(buffer.toString());
    String xml = doc.toString();
    doc.invalidate();

    return xml;
  }

  public String invitationsToXML() {
    synchronized (invitations) {
      int size = invitations.size();
      for (int i = size - 1; i >= 0; i--) {
        BuddyInvitation inv = (BuddyInvitation)invitations.get(i);
        if (new Date().getTime() - inv.getDate().getTime() > 60000L) {
          invitations.remove(i);
        }
      }

    }

    XMLDoc doc = new XMLDoc();
    XMLTag tag = doc.startTag("INVS");
    StringBuffer buffer = new StringBuffer();

    synchronized (invitations) {
      Iterator iter = invitations.iterator();
      while (iter.hasNext()) {
        BuddyInvitation inv = (BuddyInvitation)iter.next();
        buffer.append(inv.toXML());
      }

      invitations.clear();
    }
    tag.setTagContent(buffer.toString());
    String xml = doc.toString();
    doc.invalidate();

    return xml;
  }

  public boolean removeBuddy(Player player)
  {
    synchronized (buddies) {
      Iterator iter = buddies.iterator();
      while (iter.hasNext()) {
        Buddy buddy = (Buddy)iter.next();
        if (buddy.getBuddy().equals(player)) {
          iter.remove();

          new Thread(new BuddyListRemoveFromDB(owner, player)).start();
          return true;
        }
      }
      return false;
    }
  }
}