package game.pokerrounds.stakescalculators;

public class StakesCalculatorFactory
{
  public static PokerStakesCalculator getStakesCalculator(int limitType)
  {
    switch (limitType) {
    case 1:
      return new LimitPokerStakesCalculator();
    case 2:
      return new NoLimitPokerStakesCalculator();
    case 3:
      return new PotLimitPokerStakesCalculator();
    }
    return null;
  }
}