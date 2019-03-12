package game.cards;

import game.Card;
import java.util.List;

public abstract interface CardsSets
{
  public abstract void setGameType(int paramInt);

  public abstract int getGameType();

  public abstract void addOwnCard(Card paramCard);

  public abstract void addCommonCard(Card paramCard);

  public abstract List getOwnCards();

  public abstract List getCommonCards();

  public abstract void addOwnCardsFromArray(List paramList);

  public abstract void addCommonCardsFromArray(List paramList);

  public abstract Combination getCost();
}