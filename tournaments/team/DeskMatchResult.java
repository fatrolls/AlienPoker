package tournaments.team;

import game.Player;

public class DeskMatchResult
{
  private Player looser;
  private Player winner;

  public DeskMatchResult(Player winner, Player looser)
  {
    this.winner = winner;
    this.looser = looser;
  }

  public Player getLooser()
  {
    return looser;
  }

  public Player getWinner() {
    return winner;
  }
}