package game.pokerrounds;

import game.Desk;
import game.Game;
import java.math.BigDecimal;

public class PreTurnStakesPokerRound extends StakesPokerRound
{
  public void complited()
  {
    getGame().setAsPreTurn();
  }

  public void init()
  {
    setActionCode(15);
    setStakeBase(getGame().getDesk().getMinBet());
    setStartAfterPlaceNumber(getGame().getDealerPlaceNumber());
    setCurrentStake(new BigDecimal(0));
  }
}