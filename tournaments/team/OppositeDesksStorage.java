package tournaments.team;

import game.Desk;
import game.Player;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OppositeDesksStorage
{
  private TeamTournament teamTournament;
  private final List desks = new ArrayList();

  public OppositeDesksStorage(TeamTournament teamTournament) {
    this.teamTournament = teamTournament;
  }

  public TeamTournament getTeamTournament() {
    return teamTournament;
  }

  public void addOppositeDesk(OppositeDesk oppositeDesk) {
    synchronized (desks) {
      if (!hasDesk(oppositeDesk))
        desks.add(oppositeDesk);
      else
        throw new IllegalArgumentException("Cannot add OppositeDesk twice");
    }
  }

  public boolean remove(Desk desk)
  {
    synchronized (desks) {
      Iterator iter = desks.iterator();
      while (iter.hasNext()) {
        OppositeDesk d = (OppositeDesk)iter.next();
        if (d.getDesk().equals(desk)) {
          iter.remove();
          return true;
        }
      }
      return false;
    }
  }

  public boolean hasDesk(OppositeDesk oppositeDesk) {
    synchronized (desks) {
      Iterator iter = desks.iterator();
      while (iter.hasNext()) {
        if (oppositeDesk.equals(iter.next())) {
          return true;
        }
      }
      return false;
    }
  }

  public int size() {
    synchronized (desks) {
      return desks.size();
    }
  }

  public OppositeDesk getPlayerDesk(Player player) {
    synchronized (desks) {
      Iterator iter = desks.iterator();
      while (iter.hasNext()) {
        OppositeDesk d = (OppositeDesk)iter.next();
        if (d.getDesk().getPlayerPlace(player) != null) {
          return d;
        }
      }
      return null;
    }
  }

  public Iterator unmodifiableIterator()
  {
    List desksList;
    synchronized (desks) {
      desksList = new ArrayList(desks.size());
      desksList.addAll(desks);
    }
    return desksList.iterator();
  }
}