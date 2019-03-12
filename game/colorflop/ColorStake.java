package game.colorflop;

import game.Desk;
import game.Player;
import java.math.BigDecimal;

public class ColorStake
{
  private Player player;
  private Desk desk;
  private int type;
  private BigDecimal stake;

  public ColorStake(Player player, Desk desk, int type, BigDecimal stake)
  {
    this.player = player;
    this.desk = desk;
    this.type = type;
    this.stake = stake;
  }

  public Player getPlayer() {
    return player;
  }

  public Desk getDesk() {
    return desk;
  }

  public int getType() {
    return type;
  }

  public int hashCode() {
    return player.getID();
  }

  public boolean equals(Object o) {
    if ((o instanceof ColorStake)) {
      return player.getID() == ((ColorStake)o).getPlayer().getID();
    }
    return false;
  }

  public BigDecimal getStake()
  {
    return stake;
  }
}