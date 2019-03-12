package game.cards.sets;

import game.cards.CardsSetAdapter;
import game.cards.Combination;
import game.cards.CombinationChecker;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TexasHoldemCardsSet extends CardsSetAdapter
{
  private List cards = new ArrayList();

  public Combination getCost()
  {
    Iterator own = ownCards.iterator();
    while (own.hasNext()) {
      cards.add(own.next());
    }
    Iterator common = commonCards.iterator();
    while (common.hasNext()) {
      cards.add(common.next());
    }

    CombinationChecker combinationChecker = new CombinationChecker(cards);
    cards = combinationChecker.getCards();

    List straightsCheck = combinationChecker.checkOnStraights();

    Combination c = getMaxCombination(combinationChecker.checkOnStraightFlashes(straightsCheck));
    if (c != null) {
      return c;
    }

    c = getMaxCombination(combinationChecker.checkOnFourOfKind());
    if (c != null) {
      return c;
    }

    c = getMaxCombination(combinationChecker.checkOnFullHouse());
    if (c != null) {
      return c;
    }

    c = combinationChecker.checkOnFlush();
    if (c != null) {
      return c;
    }

    c = getMaxCombination((ArrayList)straightsCheck);
    if (c != null) {
      return c;
    }

    c = getMaxCombination(combinationChecker.checkOnThreeOfKind());
    if (c != null) {
      return c;
    }

    c = getMaxCombination(combinationChecker.checkOnPairs());
    if (c != null) {
      return c;
    }

    c = getMaxCombination(combinationChecker.checkOnPair());
    if (c != null) {
      return c;
    }

    c = getMaxCombination(combinationChecker.checkOnHighCard());

    if (c == null) {
      c = new Combination(20, 0);
    }

    return c;
  }

  private Combination getMaxCombination(ArrayList combinations)
  {
    Combination combination = null;
    Iterator it = combinations.iterator();
    while (it.hasNext()) {
      Combination c = (Combination)it.next();
      if (combination == null) {
        combination = c;
      }
      else if (combination.getCombination() > c.getCombination()) {
        combination = c;
      }
      else if ((combination.getCombination() == c.getCombination()) && 
        (combination.getHighCard() < c.getHighCard())) {
        combination = c;
      }

    }

    return combination;
  }

  public List getCards()
  {
    return cards;
  }
}