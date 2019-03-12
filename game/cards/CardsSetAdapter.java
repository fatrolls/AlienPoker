package game.cards;

import game.Card;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CardsSetAdapter
  implements CardsSets
{
  public static final int CARDS_TYPE_COMMON = 1;
  public static final int CARDS_TYPE_OWN = 2;
  protected List commonCards = new ArrayList();
  protected List ownCards = new ArrayList();

  protected int gameType = 0;

  public void addOwnCard(Card card) {
    addCard(card, 2);
  }

  public List getOwnCards() {
    return ownCards;
  }

  public List getCommonCards() {
    return commonCards;
  }

  public void addCommonCard(Card card) {
    addCard(card, 1);
  }

  public void addOwnCardsFromArray(List cards) {
    addCardsFromArray(cards, 2);
  }

  public void addCommonCardsFromArray(List cards) {
    addCardsFromArray(cards, 1);
  }

  private void addCard(Card card, int cardsType)
  {
    List list = cardsType == 1 ? commonCards : ownCards;
    list.add(card);
  }

  private void addCardsFromArray(List cards, int cardsType) {
    Iterator it = cards.iterator();
    while (it.hasNext()) {
      Card card = (Card)it.next();
      addCard(card, cardsType);
    }
  }

  private void deleteCards(int cardsType) {
    List list = cardsType == 1 ? commonCards : ownCards;
    list.clear();
  }

  public void deleteCards() {
    commonCards.clear();
    ownCards.clear();
  }

  public int getGameType() {
    return gameType;
  }

  public void setGameType(int gameType) {
    this.gameType = gameType;
  }

  public Combination getCost() {
    return null;
  }
}