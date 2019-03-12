package game.cards;

import game.Card;
import game.cards.comparators.DefaultComparator;
import game.cards.comparators.TexasHoldemComparator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import utils.xml.XMLTag;

public class Combination
  implements Comparable
{
  public static final int NO_COMBINATION = 20;
  public static final int FIVE_OF_A_KIND = 1;
  public static final int ROYAL_FLUSH = 2;
  public static final int STRAIGHT_FLUSH = 3;
  public static final int FOUR_OF_A_KIND = 4;
  public static final int FULL_HOUSE = 5;
  public static final int FLUSH = 6;
  public static final int STRAIGHT = 7;
  public static final int THREE_OF_A_KIND = 8;
  public static final int TWO_PAIRS = 9;
  public static final int PAIR = 10;
  public static final int HIGH_CARD = 11;
  protected int combination = 0;
  protected int suit = 0;
  protected int highCard = 0;
  protected int lowCard = 0;
  protected List<Card> cards = new ArrayList();
  protected CardsSets cardsSet = new CardsSetAdapter();
  public static final String PARAM_TAG_NAME_COST = "COST";
  public static final String PARAM_TAG_RAPAM_NAME_COMBINATION = "COMBINATION";
  public static final String PARAM_TAG_RAPAM_NAME_HIGH_CARD = "HCARD";
  public static final String PARAM_TAG_RAPAM_NAME_LOW_CARD = "LCARD";
  public static final String PARAM_TAG_RAPAM_NAME_CARD_SUIT = "SUIT";

  public void addCard(Card card)
  {
    cards.add(card);
    if (((combination == 6) || (combination == 2)) && (suit == 0))
      suit = card.getSuite();
  }

  public void addCards(List cards)
  {
    Iterator iter = cards.iterator();
    while (iter.hasNext())
      addCard((Card)iter.next());
  }

  public Combination(int combination)
  {
    this(combination, 0);
  }

  public Combination(int combination, int highCard)
  {
    this.combination = combination;
    this.highCard = highCard;
  }

  public Combination(int combination, int highCard, int lowCard)
  {
    this(combination, highCard);
    this.lowCard = lowCard;
  }

  public int getCombination()
  {
    return combination;
  }

  public int getHighCard()
  {
    return highCard;
  }

  public int getLowCard()
  {
    return lowCard;
  }

  public XMLTag toXMLTag()
  {
    XMLTag tag = new XMLTag("COST");
    tag.addParam("COMBINATION", getCombination());
    tag.addParam("HCARD", getHighCard());
    tag.addParam("LCARD", getLowCard());
    tag.addParam("SUIT", getSuit());

    Iterator iter = cards.iterator();
    while (iter.hasNext()) {
      tag.addNestedTag(((Card)iter.next()).toXMLTag());
    }

    return tag;
  }

  public String toXML()
  {
    XMLTag tag = toXMLTag();

    String xml = tag.toString();
    tag.invalidate();

    return xml;
  }

  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append(super.toString()).append(" COMBINATION:").append(combination).append(" ").append("HC:").append(highCard).append(" ").append("LC:").append(lowCard).append("SUIT:").append(suit);
    Iterator iter = cards.iterator();
    while (iter.hasNext()) {
      Card card = (Card)iter.next();
      buffer.append("   Card: ").append(card.getValue()).append("").append(suitToString(card.getSuite()));
    }

    return buffer.toString();
  }

  protected String suitToString(int suit) {
    String res = "";
    switch (suit) { case 1:
      res = "CLUBS"; break;
    case 2:
      res = "SPADES"; break;
    case 3:
      res = "HEARTS"; break;
    case 4:
      res = "DIAMONDS";
    }
    return res;
  }

  public void build()
  {
  }

  public Object clone()
  {
    Combination combination = new Combination(this.combination, highCard, lowCard);
    combination.setSuit(suit);
    combination.addCards(cards);

    return combination;
  }

  public List<Card> getCards() {
    return cards;
  }

  public int getCombinationType() {
    return combination;
  }

  public void setCombination(int combination) {
    this.combination = combination;
  }

  public List getOtherCards()
  {
    List list = getCards();
    List result = new ArrayList();

    List allCards = new ArrayList();
    Iterator it = cardsSet.getCommonCards().iterator();
    while (it.hasNext()) {
      allCards.add(it.next());
    }
    it = cardsSet.getOwnCards().iterator();
    while (it.hasNext()) {
      allCards.add(it.next());
    }

    Iterator cardsIter = allCards.iterator();
    while (cardsIter.hasNext()) {
      boolean found = false;
      Card card = (Card)cardsIter.next();
      Iterator iter = list.iterator();
      while ((iter.hasNext()) && (!found)) {
        found = card.equals(iter.next());
      }
      if (!found) {
        result.add(card);
      }
    }
    return result;
  }

  public Card getOtherCardsHightCard() {
    List otherCards = getOtherCards();
    Card tmpCard = null;
    Iterator iter = otherCards.iterator();
    while (iter.hasNext()) {
      if (tmpCard == null) {
        tmpCard = (Card)iter.next();
        continue;
      }Card c = (Card)iter.next();
      if (c.getValue() > tmpCard.getValue()) {
        tmpCard = c;
      }

    }

    return tmpCard;
  }

  public int getSuit()
  {
    return suit;
  }

  public void setSuit(int suit) {
    this.suit = suit;
  }

  public void setCardsSet(CardsSets cardsSet) {
    this.cardsSet = cardsSet;
  }

  public CardsSets getCardsSet() {
    return cardsSet;
  }

  public int compareTo(Object object)
  {
    Comparator comparator;
    switch (cardsSet.getGameType()) {
    case 1:
      comparator = new TexasHoldemComparator();
      break;
    default:
      comparator = new DefaultComparator();
    }

    return comparator.compare(this, object);
  }
}