package game.pokerrounds;

import game.Desk;
import game.Game;

public class AfterRiverPokerRound extends StakesPokerRound
{
  public void complited()
  {
    getGame().setAsAfterRiver();
  }

  public void init()
  {
    setActionCode(15);
    setStartAfterPlaceNumber(getGame().getDealerPlaceNumber());
    setStakeBase(getGame().getDesk().getMaxBet());
  }
}