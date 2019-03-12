package game.pokerrounds;

import game.Desk;
import game.Game;
import java.math.BigDecimal;

public class PreFlopStakesPokerRound extends StakesPokerRound
{
  public void complited()
  {
    getGame().setAsPreFlop();
  }

  public boolean canLastPlaceContinueStaking()
  {
    return (getNeedCall().floatValue() == 0.0F) && (getRaisesCount() == 0);
  }

  public void init()
  {
    setActionCode(15);
    setStakeBase(getGame().getDesk().getMinBet());
    setCurrentStake(getGame().getDesk().getMinBet());
    setStartAfterPlaceNumber(getGame().getBigBlindPlaceNumber());
    betProcessed();
  }
}