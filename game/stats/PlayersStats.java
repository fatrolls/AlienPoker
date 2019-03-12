package game.stats;

import game.Player;
import java.util.HashMap;

public class PlayersStats
{
  private static final HashMap holdemMap = new HashMap();
  private static final HashMap sevenStudMap = new HashMap();
  private static final HashMap chineseMap = new HashMap();

  public static PlayerStat getPlayerStat(Player player, int pokerType)
  {
    Integer key = new Integer(player.getID());
    HashMap map;
    switch (pokerType) {
    case 7:
      map = chineseMap;
      break;
    case 2:
      map = sevenStudMap;
      break;
    case 6:
      map = sevenStudMap;
      break;
    default:
      map = holdemMap;
    }
    PlayerStat ps;
    synchronized (map) {
      Object o = map.get(key);
      if (o == null) {
        PlayerStat ps = new PlayerStat(player);
        map.put(key, ps);
      } else {
        ps = (PlayerStat)o;
      }
    }

    return ps;
  }

  public static void updatePlayerStats(Player player) {
    PlayerStat ps = getPlayerStat(player, 1);
    if (ps != null)
      new Thread(new PlayerStatsUpdater(ps)).start();
  }
}