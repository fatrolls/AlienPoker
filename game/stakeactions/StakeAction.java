package game.stakeactions;

import game.Stake;
import game.pokerrounds.StakesPokerRound;

public abstract class StakeAction
{
  protected Stake stake = null;
  protected StakesPokerRound owner = null;

  public Stake getStake()
  {
    return stake;
  }

  public StakesPokerRound getOwner()
  {
    return owner;
  }

  public StakeAction(Stake stake, StakesPokerRound owner)
  {
    this.stake = stake;
    this.owner = owner;
  }

  public abstract boolean execute();
}