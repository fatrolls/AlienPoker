package game.amounts;

import game.Desk;
import game.Player;
import java.math.BigDecimal;
import tournaments.Tournament;

public class PlayerAmount
{
  private Player player;
  private AmountInUse gameAmount;
  private AmountInUse realAmount;

  public PlayerAmount(Player player)
  {
    this.player = player;
    gameAmount = new AmountInUse(player);
    realAmount = new AmountInUse(player);
  }

  public Player getPlayer() {
    return player;
  }

  public void recalculateAmount() {
    gameAmount.recalculateAmount();
    realAmount.recalculateAmount();
  }

  public void recordDeskAmount(Desk desk)
  {
    switch (desk.getMoneyType()) {
    case 1:
      gameAmount.recordDeskAmount(desk);
      break;
    case 0:
      realAmount.recordDeskAmount(desk);
      break;
    default:
      throw new RuntimeException("PlayerAmount - unknown moneytype " + desk.getMoneyType());
    }
  }

  public void recordTournamentAmount(Tournament tournament) {
    switch (tournament.getMoneyType()) {
    case 1:
      gameAmount.recordTournamentAmount(tournament);
      break;
    case 0:
      realAmount.recordTournamentAmount(tournament);
      break;
    default:
      throw new RuntimeException("PlayerAmount - unknown moneytype " + tournament.getMoneyType());
    }
  }

  public void deleteTournamentRecord(Tournament tournament) {
    switch (tournament.getMoneyType()) {
    case 1:
      gameAmount.deleteTournamentRecord(tournament);
      break;
    case 0:
      realAmount.deleteTournamentRecord(tournament);
      break;
    default:
      throw new RuntimeException("PlayerAmount - unknown moneytype " + tournament.getMoneyType());
    }
  }

  public BigDecimal getInUseAmount(int money)
  {
    switch (money) {
    case 1:
      return gameAmount.getTotalAmount();
    case 0:
      return realAmount.getTotalAmount();
    }
    throw new RuntimeException("PlayerAmount - unknown moneytype " + money);
  }
}