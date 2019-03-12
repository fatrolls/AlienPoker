package game.playerclub.dirtypoints;

import game.Desk;
import game.Player;

public final class DesksDirtyPoints
{
  private int rakeCount = 0;
  private Desk desk;
  private Player player;

  public DesksDirtyPoints(Desk desk, Player player)
  {
    this.desk = desk;
    this.player = player;
  }

  public int hashCode() {
    return player.getID();
  }

  public boolean equals(Object o) {
    if ((o instanceof DesksDirtyPoints)) {
      return o.hashCode() == hashCode();
    }
    return false;
  }

  public int getRakeCount()
  {
    return rakeCount;
  }

  public void setRakeCount(int rakeCount) {
    this.rakeCount = rakeCount;
  }

  public Desk getDesk() {
    return desk;
  }

  public Player getPlayer() {
    return player;
  }
}