package game.cards;

import game.Card;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class CombinationChecker
{
  private List cards;
  private HashMap valuesMap;
  private HashMap<Integer, ArrayList<Card>> flashes;
  private boolean hasJoker;
  private static final int STRAIGHT_CARDS_COUNT = 5;
  private boolean hasTwoJokers = false;

  private CombinationChecker() {
  }

  public CombinationChecker(List cards) {
    this();
    this.cards = cards;
    prepareCards();
  }

  public List getCards()
  {
    return cards;
  }

  private void prepareCards()
  {
    Collections.sort(cards, new ValueComparator(null));
    valuesMap = new HashMap();
    flashes = new HashMap();
    hasJoker = false;
    hasTwoJokers = false;

    Iterator it = cards.iterator();
    while (it.hasNext()) {
      Card card = (Card)it.next();
      if (card.getValue() == 15) {
        if (hasJoker) {
          hasTwoJokers = true;
        }
        hasJoker = true;
      }

      Integer value = new Integer(card.getValue());
      if (!valuesMap.containsKey(value)) {
        valuesMap.put(value, new Integer(1));
      } else {
        Integer count = (Integer)valuesMap.get(value);
        valuesMap.put(value, new Integer(count.intValue() + 1));
      }

      Integer suite = new Integer(card.getSuite());
      if (!flashes.containsKey(suite)) {
        flashes.put(suite, new ArrayList());
      }

      ((ArrayList)flashes.get(suite)).add(card);
    }
  }

  public ArrayList checkOnStraightFlashes()
  {
    List forCheck = checkOnStraights();
    return checkOnStraightFlashes(forCheck);
  }

  public ArrayList checkOnStraightFlashes(List forCheck) {
    ArrayList combinations = new ArrayList();

    Iterator iter = forCheck.iterator();
    while (iter.hasNext()) {
      Combination comb = (Combination)iter.next();
      Iterator combIter = comb.getCards().iterator();
      int suit = -1;
      boolean flash = true;
      while ((combIter.hasNext()) && (flash)) {
        Card card = (Card)combIter.next();
        if (suit == -1)
          flash = true;
        else if (card.getSuite() != suit) {
          flash = false;
        }
        suit = card.getSuite();
      }
      if (flash) {
        Combination c = (Combination)comb.clone();
        if (comb.getHighCard() == 14)
          c.setCombination(2);
        else {
          c.setCombination(3);
        }

        if (c.getCards().size() > 0) {
          c.setSuit(((Card)c.getCards().get(0)).getSuite());
        }

        combinations.add(c);
      }
    }

    return combinations;
  }

  public ArrayList checkOnFourOfKind()
  {
    ArrayList combinations = new ArrayList();

    Iterator it = valuesMap.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry entry = (Map.Entry)it.next();
      int cardsCount = ((Integer)entry.getValue()).intValue();
      if (cardsCount == 4) {
        int value = ((Integer)entry.getKey()).intValue();
        Combination combination = new Combination(4, value);

        Iterator cardIterator = cards.iterator();
        while (cardIterator.hasNext()) {
          Card card = (Card)cardIterator.next();
          if (card.getValue() == value) {
            combination.addCard(card);
          }
        }
        combinations.add(combination);
      }

    }

    return combinations;
  }

  public ArrayList checkOnFullHouse()
  {
    ArrayList combinations = new ArrayList();

    int highCardValue = 0;
    int lowCardValue = 0;
    Combination hCombination = null;
    Combination lowCombination = null;

    Iterator it = valuesMap.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry entry = (Map.Entry)it.next();
      int cardsCount = ((Integer)entry.getValue()).intValue();
      if (cardsCount == 3) {
        int value = ((Integer)entry.getKey()).intValue();
        if (highCardValue < value) {
          highCardValue = value;

          hCombination = new Combination(8, highCardValue);

          Iterator cardIterator = cards.iterator();
          while (cardIterator.hasNext()) {
            Card card = (Card)cardIterator.next();
            if (card.getValue() == ((Integer)entry.getKey()).intValue()) {
              hCombination.addCard(card);
            }
          }
        }
      }
    }

    it = valuesMap.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry entry = (Map.Entry)it.next();
      int cardsCount = ((Integer)entry.getValue()).intValue();
      if (cardsCount >= 2) {
        int value = ((Integer)entry.getKey()).intValue();
        if ((lowCardValue < value) && (value != highCardValue)) {
          lowCardValue = value;

          lowCombination = new Combination(10, lowCardValue);

          Iterator cardIterator = cards.iterator();
          int i = 0;
          while ((cardIterator.hasNext()) && (i < 2)) {
            Card card = (Card)cardIterator.next();
            if (card.getValue() == ((Integer)entry.getKey()).intValue()) {
              lowCombination.addCard(card);
              i++;
            }
          }
        }
      }

    }

    if ((hCombination != null) && (lowCombination != null)) {
      Combination combination = new Combination(5, highCardValue, lowCardValue);
      combination.addCards(hCombination.getCards());
      combination.addCards(lowCombination.getCards());
      combinations.add(combination);
    }

    return combinations;
  }

  public ArrayList checkOnThreeOfKind()
  {
    ArrayList combinations = new ArrayList();
    int highCardValue = 0;

    Iterator it = valuesMap.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry entry = (Map.Entry)it.next();
      int cardsCount = ((Integer)entry.getValue()).intValue();
      if (cardsCount == 3)
      {
        int value = ((Integer)entry.getKey()).intValue();
        if (highCardValue < value) {
          highCardValue = value;
        }

        Combination comb = new Combination(8, value);
        Iterator cardIterator = cards.iterator();
        while (cardIterator.hasNext()) {
          Card card = (Card)cardIterator.next();
          if (card.getValue() == ((Integer)entry.getKey()).intValue()) {
            comb.addCard(card);
          }
        }

        combinations.add(comb);
      }

    }

    return combinations;
  }

  public Combination checkOnFlush()
  {
    for (Integer suit : flashes.keySet()) {
      ArrayList flushCards = (ArrayList)flashes.get(suit);
      int currentSuit = suit.intValue();

      if (flushCards.size() >= 5) {
        int maxCardValue = 0;
        Iterator cit = flushCards.iterator();
        while (cit.hasNext()) {
          Card card = (Card)cit.next();

          if (maxCardValue < card.getValue()) {
            maxCardValue = card.getValue();
          }
        }
        Combination combination = new Combination(6, maxCardValue);
        combination.setSuit(currentSuit);

        Collections.sort(flushCards, new Comparator() {
          public int compare(Card o1, Card o2) {
            if ((o1 == null) || (o2 == null))
              throw new IllegalArgumentException("Card is null");
            if (o1.getValue() < o2.getValue())
              return 1;
            if (o1.getValue() > o2.getValue())
              return -1;
            return 0;
          }
        });
        for (int i = 0; i < 5; i++) {
          combination.addCard((Card)flushCards.get(i));
        }

        return combination;
      }
    }

    return null;
  }

  public ArrayList checkOnPairs()
  {
    ArrayList combinations = new ArrayList();

    List highCards = new ArrayList();
    List lowCards = new ArrayList();

    int pairsCount = 0;
    int highCardValue = 0;
    int lowCardValue = 0;

    Iterator it = valuesMap.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry entry = (Map.Entry)it.next();
      int cardsCount = ((Integer)entry.getValue()).intValue();
      if (cardsCount >= 2) {
        pairsCount++;

        int value = ((Integer)entry.getKey()).intValue();
        if (value > highCardValue) {
          lowCardValue = highCardValue;
          highCardValue = value;
        } else if ((value > lowCardValue) && (value < highCardValue)) {
          lowCardValue = value;
        }
      }
    }

    if (pairsCount < 2) {
      return combinations;
    }

    Iterator cardIterator = cards.iterator();
    int i = 0;
    while ((cardIterator.hasNext()) && (i < 2)) {
      Card card = (Card)cardIterator.next();
      if (card.getValue() == highCardValue) {
        highCards.add(card);
        i++;
      }

    }

    cardIterator = cards.iterator();
    i = 0;
    while ((cardIterator.hasNext()) && (i < 2)) {
      Card card = (Card)cardIterator.next();
      if (card.getValue() == lowCardValue) {
        lowCards.add(card);
        i++;
      }
    }

    Combination combination = new Combination(9, highCardValue, lowCardValue);
    combination.addCards(highCards);
    combination.addCards(lowCards);
    combinations.add(combination);

    return combinations;
  }

  public ArrayList checkOnPair()
  {
    ArrayList combinations = new ArrayList();

    List highCards = new ArrayList();

    int highCardValue = 0;

    Iterator it = valuesMap.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry entry = (Map.Entry)it.next();
      int cardsCount = ((Integer)entry.getValue()).intValue();
      if (cardsCount >= 2)
      {
        int value = ((Integer)entry.getKey()).intValue();
        if (value > highCardValue) {
          highCardValue = value;
        }
      }

    }

    if (highCardValue == 0) {
      return combinations;
    }

    Iterator cardIterator = cards.iterator();
    int i = 0;
    while ((cardIterator.hasNext()) && (i < 2)) {
      Card card = (Card)cardIterator.next();
      if (card.getValue() == highCardValue) {
        highCards.add(card);
        i++;
      }
    }

    Combination combination = new Combination(10, highCardValue);
    combination.addCards(highCards);
    combinations.add(combination);

    return combinations;
  }

  public ArrayList checkOnHighCard()
  {
    ArrayList combinations = new ArrayList();

    int highCardValue = 0;

    Iterator it = cards.iterator();
    Card card = null;

    while (it.hasNext()) {
      Card tmpCard = (Card)it.next();
      if (card == null) {
        card = tmpCard;
        highCardValue = card.getValue();
      }
      else if (card.getValue() < tmpCard.getValue()) {
        card = tmpCard;
        highCardValue = card.getValue();
      }
      else if ((card.getValue() == tmpCard.getValue()) && (card.getValue() == 14) && (tmpCard.getSuite() == 4))
      {
        card = tmpCard;
        highCardValue = card.getValue();
      }

    }

    if (card != null) {
      Combination combination = new Combination(11, highCardValue);
      combination.addCard(card);
      combinations.add(combination);
    }

    return combinations;
  }

  public ArrayList checkOnStraights()
  {
    ArrayList combinations = new ArrayList();

    int size = cards.size();
    for (int j = 0; j < size; j++) {
      List combs = getStraightCombinations(cards, j);
      Iterator iter = combs.iterator();
      while (iter.hasNext()) {
        combinations.add(iter.next());
      }
    }

    return combinations;
  }

  private List getStraightCombinations(List cards, int from)
  {
    List combinations = new ArrayList();

    if (cards.size() < 5) {
      return combinations;
    }

    List diff1List = new ArrayList();
    List diff2List = new ArrayList();
    List diff3List = new ArrayList();
    List diff4List = new ArrayList();

    Card card = (Card)cards.get(from);
    int highCardValue = card.getValue();

    for (int j = 0; j < cards.size(); j++) {
      if (j == from)
      {
        continue;
      }
      Card currentCard = (Card)cards.get(j);
      int currCardValue = currentCard.getValue();
      if ((currCardValue == 14) && (card.getValue() <= 5)) {
        currCardValue = 1;
      }

      int difference = card.getValue() - currCardValue;

      switch (difference) {
      case 1:
        diff1List.add(currentCard);
        break;
      case 2:
        diff2List.add(currentCard);
        break;
      case 3:
        diff3List.add(currentCard);
        break;
      case 4:
        diff4List.add(currentCard);
      }

    }

    if ((diff1List.size() == 0) || (diff2List.size() == 0) || (diff3List.size() == 0) || (diff4List.size() == 0)) {
      return combinations;
    }

    for (int i1 = 0; i1 < diff1List.size(); i1++) {
      for (int i2 = 0; i2 < diff2List.size(); i2++) {
        for (int i3 = 0; i3 < diff3List.size(); i3++) {
          for (int i4 = 0; i4 < diff4List.size(); i4++)
          {
            Combination comb = new Combination(7, highCardValue, highCardValue - 4 >= 2 ? highCardValue - 4 : 14);
            comb.addCard(card);
            comb.addCard((Card)diff1List.get(i1));
            comb.addCard((Card)diff2List.get(i2));
            comb.addCard((Card)diff3List.get(i3));
            comb.addCard((Card)diff4List.get(i4));

            combinations.add(comb);
          }
        }
      }
    }

    return combinations;
  }

  private class LocksChecker
  {
    private ArrayList cards = new ArrayList(13);

    public LocksChecker(List cards) {
      this.cards.addAll(cards);
    }

    private ArrayList excludeStraightCards(ArrayList allCards)
    {
      ArrayList firstCards = new ArrayList();
      int k = 0;
      Card cardBegin = null;
      while ((k < allCards.size()) && (allCards.size() > 0) && (firstCards.size() < 5)) {
        if (cardBegin == null) {
          cardBegin = (Card)allCards.get(k);
          firstCards.add(cardBegin);
          allCards.remove(k);
          continue;
        }Card card = (Card)allCards.get(k);
        if (isPreviousCard(cardBegin, card)) {
          firstCards.add(card);
          allCards.remove(k);
          cardBegin = card;
        } else {
          k++;
        }

      }

      return firstCards;
    }

    private boolean isNextCard(Card card1, Card card2) {
      if ((card1.getValue() == 14) && (card2.getValue() == 2)) {
        return true;
      }
      return card1.getValue() == card2.getValue() - 1;
    }

    private boolean isPreviousCard(Card card1, Card card2)
    {
      if ((card1.getValue() == 2) && (card2.getValue() == 14)) {
        return true;
      }
      return card1.getValue() == card2.getValue() + 1;
    }

    private boolean hasDuplicates(List cards)
    {
      int size = cards.size();
      for (int i = 0; i < size; i++) {
        Card card1 = (Card)cards.get(i);
        for (int j = 0; j < size; j++) {
          if (i != j) {
            Card card2 = (Card)cards.get(j);
            if (card1.equals(card2)) {
              return true;
            }
          }
        }
      }
      return false;
    }

    private class CardsValueComparator
      implements Comparator
    {
      private CardsValueComparator()
      {
      }

      public int compare(Object o1, Object o2)
      {
        Card card1 = (Card)o1;
        Card card2 = (Card)o2;

        if ((card1.getValue() == 14) && (card2.getValue() != 14)) {
          return -10;
        }

        if (card1.getValue() > card2.getValue()) {
          return -10;
        }

        if (card1.getValue() < card2.getValue()) {
          return 10;
        }
        return 0;
      }
    }

    private class ReverseIntComparator
      implements Comparator
    {
      private ReverseIntComparator()
      {
      }

      public int compare(Object o1, Object o2)
      {
        Integer i1 = (Integer)o1;
        Integer i2 = (Integer)o2;

        if (i1.intValue() > i2.intValue()) {
          return -10;
        }

        if (i1.intValue() < i2.intValue()) {
          return 10;
        }
        return 0;
      }
    }
  }

  private class ValueComparator
    implements Comparator
  {
    private ValueComparator()
    {
    }

    public int compare(Object o1, Object o2)
    {
      Card card1 = (Card)o1;
      Card card2 = (Card)o2;

      if (card1.getValue() > card2.getValue()) {
        return -10;
      }

      if (card1.getValue() < card2.getValue()) {
        return 10;
      }
      return 0;
    }
  }
}