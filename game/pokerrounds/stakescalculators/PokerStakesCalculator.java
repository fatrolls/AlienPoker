package game.pokerrounds.stakescalculators;

import game.Place;
import game.pokerrounds.StakesPokerRound;

public abstract interface PokerStakesCalculator
{
  public abstract void setPlace(Place paramPlace);

  public abstract void setStakesPokerRound(StakesPokerRound paramStakesPokerRound);

  public abstract void calculate();
}