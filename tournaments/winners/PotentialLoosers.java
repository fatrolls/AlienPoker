package tournaments.winners;

import game.Place;
import game.Player;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

public class PotentialLoosers
{
  private final LinkedHashMap winnersMap = new LinkedHashMap();

  public int addPlayerByPlace(Place place) {
    synchronized (winnersMap) {
      synchronized (place) {
        Player p = place.getPlayer();
        if (p != null) {
          winnersMap.put(new Integer(p.getID()), place.getAmount());
        }
      }
      return winnersMap.size();
    }
  }

  public int addPlayer(Player player, BigDecimal amount) {
    synchronized (winnersMap) {
      winnersMap.put(new Integer(player.getID()), amount);
      return winnersMap.size();
    }
  }

  public BigDecimal getPlayerAmount(Player player)
  {
    return getPlayerAmount(player.getID());
  }

  public BigDecimal getPlayerAmount(int player) {
    synchronized (winnersMap) {
      return (BigDecimal)winnersMap.get(new Integer(player));
    }
  }

  public void clear() {
    synchronized (winnersMap) {
      winnersMap.clear();
    }
  }

  public ArrayList getSameLoosersByAmount(int player)
  {
    BigDecimal amount = getPlayerAmount(player);
    ArrayList list = new ArrayList();
    if (amount != null) {
      synchronized (winnersMap) {
        Iterator iter = winnersMap.entrySet().iterator();
        while (iter.hasNext()) {
          Map.Entry v = (Map.Entry)iter.next();
          if (amount.compareTo((BigDecimal)v.getValue()) == 0) {
            list.add(v.getKey());
          }
        }
      }
    }
    return list;
  }
}