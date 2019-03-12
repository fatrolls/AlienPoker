package game.pokerrounds;

import game.ExecutionState;
import game.Game;
import game.Place;
import game.Stake;

public abstract interface PokerRound extends Runnable
{
  public abstract void setGame(Game paramGame);

  public abstract void setStartAfterPlaceNumber(int paramInt);

  public abstract ExecutionState getExecutionState();

  public abstract int getCurrentPlaceNumber();

  public abstract boolean acceptStake(Stake paramStake);

  public abstract void notifyAboutLeaveDesk(Place paramPlace);

  public abstract void notifyAboutAllIn(Place paramPlace);

  public abstract void notifyAboutFold(Place paramPlace);

  public abstract void checkAllInPrizeAmount();

  public abstract boolean hasAllInPlaces();

  public abstract void init();
}