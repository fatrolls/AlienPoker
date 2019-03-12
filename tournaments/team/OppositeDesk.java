package tournaments.team;

import game.Desk;
import java.util.List;

public class OppositeDesk
{
  private Desk desk;
  private List players;

  public OppositeDesk(Desk desk, List players)
  {
    this.desk = desk;
    this.players = players;
  }

  public Desk getDesk() {
    return desk;
  }

  public List getPlayers() {
    return players;
  }

  public int hashCode()
  {
    return desk.getID();
  }

  public boolean equals(Object o) {
    if ((o instanceof OppositeDesk)) {
      return desk.equals(o);
    }
    return false;
  }
}