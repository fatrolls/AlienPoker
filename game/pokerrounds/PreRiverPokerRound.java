package game.pokerrounds;

import game.Desk;
import game.Game;

public class PreRiverPokerRound extends StakesPokerRound
{
  public void complited()
  {
    getGame().setAsPreRiver();
  }

  public void init()
  {
    setActionCode(15);
    setStartAfterPlaceNumber(getGame().getDealerPlaceNumber());
    setStakeBase(getGame().getDesk().getMaxBet());
  }
}