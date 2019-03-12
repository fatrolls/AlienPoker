package game.playerclub.dirtypoints;

import game.Desk;
import game.Place;
import game.PlacesList;
import game.Player;
import game.playerclub.ClubPlayers;
import game.playerclub.PlayersClub;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;
import utils.CommonLogger;

public final class DirtyDeskPointsStorage
{
  private Desk desk;
  private final Map map = new HashMap();

  public DirtyDeskPointsStorage(Desk desk) {
    this.desk = desk;
  }

  public Desk getDesk() {
    return desk;
  }

  public synchronized void increaseRakesCount() {
    Iterator iter = desk.getPlacesList().allPlacesIterator();
    while (iter.hasNext()) {
      Place place = (Place)iter.next();
      Player player = place.getPlayer();
      if ((player != null) && (!place.isSittingOut())) {
        if (CommonLogger.getLogger().isDebugEnabled()) {
          CommonLogger.getLogger().debug("increaseRakesCount achieved...");
        }
        if (PlayersClub.getInstance().getClubPlayers().isAMember(player))
        {
          DesksDirtyPoints desksDirtyPoints;
          synchronized (map) {
            desksDirtyPoints = (DesksDirtyPoints)map.get(player);
          }
          if (desksDirtyPoints == null) {
            desksDirtyPoints = new DesksDirtyPoints(desk, player);
            desksDirtyPoints.setRakeCount(1);
            synchronized (map) {
              map.put(player, desksDirtyPoints);
            }

            if (CommonLogger.getLogger().isDebugEnabled())
              CommonLogger.getLogger().debug(player.getLogin() + " was added to desk dirty points list");
          }
          else {
            desksDirtyPoints.setRakeCount(desksDirtyPoints.getRakeCount() + 1);
            DeskPointsCounter.calculatePoints(desksDirtyPoints, desk);
          }

        }
        else if (CommonLogger.getLogger().isDebugEnabled()) {
          CommonLogger.getLogger().debug(player.getLogin() + " is not a member of player club");
        }
      }
    }
  }

  public void nullifyPoints()
  {
    synchronized (map) {
      map.clear();
    }
  }
}