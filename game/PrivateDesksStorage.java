package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import settings.PokerSettings;

public class PrivateDesksStorage
{
  private static int lastPrivateDeskId = 0;
  private static final List privateDesksList = Collections.synchronizedList(new LinkedList());
  private static final Map privateDesksMap = Collections.synchronizedMap(new HashMap());

  public static void registerPrivateDesk(Desk desk)
  {
    privateDesksList.add(desk);
    privateDesksMap.put(new Integer(desk.getID()), desk);
  }

  public static boolean canPlayerCreateDesk(Player player) {
    return getDesksCreatedByPlayer(player).size() < PokerSettings.getPrivateDesksPerPlayer();
  }

  public static ArrayList getDesksCreatedByPlayer(Player player) {
    ArrayList playerDesks = new ArrayList(PokerSettings.getPrivateDesksPerPlayer());
    synchronized (privateDesksList) {
      Iterator iter = privateDesksList.iterator();
      while (iter.hasNext()) {
        Desk desk = (Desk)iter.next();
        if (desk.getCreator().equals(player)) {
          playerDesks.add(desk);
        }
      }
    }
    return playerDesks;
  }

  public static Desk getDeskById(int id) {
    return (Desk)privateDesksMap.get(new Integer(id));
  }

  public static Desk getDeskById(Integer id) {
    return (Desk)privateDesksMap.get(id);
  }

  public static synchronized void dropDesk(Desk desk) {
    privateDesksList.remove(desk);
    privateDesksMap.remove(new Integer(desk.getID()));
  }

  public static List getPrivateDesksList() {
    return privateDesksList;
  }

  public static void unregisterDesk(Desk desk) {
    privateDesksList.remove(desk);
    privateDesksMap.remove(new Integer(desk.getID()));
  }
}