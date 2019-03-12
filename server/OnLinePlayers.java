package server;

import game.Player;
import java.util.Hashtable;
import org.apache.log4j.Logger;
import utils.CommonLogger;

public class OnLinePlayers
{
  private final Hashtable hash = new Hashtable();

  public synchronized int registerPlayer(Player player)
  {
    Integer i = (Integer)hash.get(player);
    if (i != null) {
      i = new Integer(i.intValue() + 1);
      hash.put(player, i);
      return i.intValue();
    }
    i = new Integer(1);
    hash.put(player, i);
    return 1;
  }

  public synchronized int getPlayerSessions(Player player)
  {
    Integer i = (Integer)hash.get(player);
    if (i != null) {
      return i.intValue();
    }
    return 0;
  }

  public synchronized int unregisterPlayer(Player player)
  {
    Integer i = (Integer)hash.get(player);
    if (i != null) {
      i = new Integer(i.intValue() - 1);
      if (i.intValue() == 0) {
        hash.remove(player);
        return 0;
      }
      hash.put(player, i);
      return i.intValue();
    }

    CommonLogger.getLogger().warn("Cannot unregister player " + player.getLogin());
    return 0;
  }
}