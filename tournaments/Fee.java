package tournaments;

import game.Player;
import java.math.BigDecimal;
import java.util.Date;

public class Fee
{
  private BigDecimal amount;
  private Player player;
  private Tournament tournament;
  private Date date;

  public Fee(Player player, Tournament tournament, BigDecimal amount)
  {
    this.amount = amount;
    this.player = player;
    this.tournament = tournament;
    date = new Date();
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public Player getPlayer() {
    return player;
  }

  public Tournament getTournament() {
    return tournament;
  }

  public Date getDate() {
    return date;
  }
}