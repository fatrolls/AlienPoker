package game.cards;

import game.Card;
import game.cards.sets.TexasHoldemCardsSet;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CardsSet extends CardsSetAdapter
{
  public int getCardsCount()
  {
    return commonCards.size() + ownCards.size();
  }

  public Combination getCost()
  {
    Combination combination = null;
    switch (getGameType()) {
    case 1:
      TexasHoldemCardsSet cardsSet = new TexasHoldemCardsSet();
      cardsSet.setGameType(1);
      cardsSet.addCommonCardsFromArray(commonCards);
      cardsSet.addOwnCardsFromArray(ownCards);
      combination = cardsSet.getCost();
      combination.setCardsSet(cardsSet);
    }

    return combination;
  }

  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    Iterator it = commonCards.iterator();
    while (it.hasNext()) {
      Card card = (Card)it.next();
      buffer.append("SUITE:").append(card.getSuite()).append(" - VALUE:").append(card.getValue()).append('\n');
    }
    return buffer.toString();
  }

  public static void main(String[] args)
  {
    CardsSet set = new CardsSet();
    set.setGameType(1);

    set.addOwnCard(new Card(1, 14));
    set.addOwnCard(new Card(4, 14));
    set.addCommonCard(new Card(2, 11));
    set.addCommonCard(new Card(3, 11));
    set.addCommonCard(new Card(1, 12));
    set.addCommonCard(new Card(3, 12));
    set.addCommonCard(new Card(2, 14));
    set.addCommonCard(new Card(4, 12));

    System.out.println("....Testing pairs....");
    System.out.println();
    Combination combination = set.getCost();
    System.out.println("Combination: " + combination);

    System.out.println(combination.toXML());

    set.deleteCards();

    set.addCommonCard(new Card(1, 14));
    set.addCommonCard(new Card(4, 14));
    set.addCommonCard(new Card(2, 11));
    set.addCommonCard(new Card(3, 14));
    set.addCommonCard(new Card(1, 12));
    set.addCommonCard(new Card(3, 12));
    set.addCommonCard(new Card(2, 12));
    set.addCommonCard(new Card(4, 12));
    set.addCommonCard(new Card(15));

    combination = set.getCost();
    System.out.println("====TESTING FIVE OF A KIND...");
    System.out.println(set.toString());
    System.out.println("====Combination");
    System.out.println(combination.toString());
    if (combination.getCombination() == 1) {
      System.out.println("RESULT:OK");
    }
    else {
      System.out.println("RESULT:ERROR");
    }
    System.out.println();

    set.deleteCards();

    set = new CardsSet();
    set.setGameType(1);

    set.addCommonCard(new Card(1, 14));
    set.addCommonCard(new Card(4, 14));
    set.addCommonCard(new Card(4, 11));
    set.addCommonCard(new Card(4, 13));
    set.addCommonCard(new Card(1, 12));
    set.addCommonCard(new Card(3, 12));
    set.addCommonCard(new Card(4, 12));
    set.addCommonCard(new Card(4, 10));

    System.out.println(set.toString());

    combination = set.getCost();
    System.out.println("TESTING ROYAL FLUSH...");
    System.out.println(set.toString());
    System.out.println("====Combination");
    System.out.println(combination.toString());

    if (combination.getCombination() == 2) {
      System.out.println("RESULT:OK");
    }
    else {
      System.out.println("RESULT:ERROR");
    }

    System.out.println();

    set.deleteCards();
    set.addCommonCard(new Card(1, 14));
    set.addCommonCard(new Card(4, 11));
    set.addCommonCard(new Card(4, 13));
    set.addCommonCard(new Card(1, 12));
    set.addCommonCard(new Card(3, 12));
    set.addCommonCard(new Card(4, 12));
    set.addCommonCard(new Card(4, 10));
    set.addCommonCard(new Card(4, 9));

    combination = set.getCost();
    System.out.println("TESTING STRAIGHT FLUSH...");
    System.out.println(set.toString());
    System.out.println(combination.toString());
    if (combination.getCombination() == 3) {
      System.out.println("RESULT:OK");
    }
    else {
      System.out.println("RESULT:ERROR");
    }

    System.out.println();

    set.deleteCards();
    set.addCommonCard(new Card(4, 14));
    set.addCommonCard(new Card(1, 11));
    set.addCommonCard(new Card(1, 13));
    set.addCommonCard(new Card(1, 12));
    set.addCommonCard(new Card(4, 5));
    set.addCommonCard(new Card(4, 4));
    set.addCommonCard(new Card(4, 3));
    set.addCommonCard(new Card(4, 2));

    combination = set.getCost();
    System.out.println("TESTING STRAIGHT FLUSH WITH ACE AS MINIMUM...");
    System.out.println(set.toString());
    System.out.println(combination.toString());
    if (combination.getCombination() == 3) {
      System.out.println("RESULT:OK");
    }
    else {
      System.out.println("RESULT:ERROR");
    }
    System.out.println();

    set.deleteCards();
    set.addCommonCard(new Card(1, 14));
    set.addCommonCard(new Card(4, 14));
    set.addCommonCard(new Card(2, 11));
    set.addCommonCard(new Card(3, 14));
    set.addCommonCard(new Card(2, 14));
    set.addCommonCard(new Card(1, 12));
    set.addCommonCard(new Card(3, 12));
    set.addCommonCard(new Card(2, 12));
    set.addCommonCard(new Card(4, 12));

    combination = set.getCost();
    System.out.println("TESTING FOUR OF A KIND...");
    System.out.println(set.toString());
    System.out.println(combination.toString());
    if (combination.getCombination() == 4) {
      System.out.println("RESULT:OK");
    }
    else {
      System.out.println("RESULT:ERROR");
    }

    System.out.println();

    set.deleteCards();
    set.addCommonCard(new Card(1, 14));
    set.addCommonCard(new Card(4, 14));
    set.addCommonCard(new Card(2, 11));
    set.addCommonCard(new Card(3, 14));
    set.addCommonCard(new Card(2, 12));
    set.addCommonCard(new Card(1, 12));
    set.addCommonCard(new Card(3, 12));
    set.addCommonCard(new Card(2, 10));
    set.addCommonCard(new Card(4, 10));

    combination = set.getCost();
    System.out.println("TESTING FULL HOUSE...");
    System.out.println(set.toString());
    System.out.println(combination.toString());
    if (combination.getCombination() == 5) {
      System.out.println("RESULT:OK");
    }
    else {
      System.out.println("RESULT:ERROR");
    }

    System.out.println();

    set.deleteCards();
    set.addCommonCard(new Card(1, 2));
    set.addCommonCard(new Card(4, 14));
    set.addCommonCard(new Card(4, 11));
    set.addCommonCard(new Card(3, 14));
    set.addCommonCard(new Card(2, 12));
    set.addCommonCard(new Card(1, 12));
    set.addCommonCard(new Card(4, 3));
    set.addCommonCard(new Card(2, 10));
    set.addCommonCard(new Card(4, 10));
    set.addCommonCard(new Card(4, 8));

    combination = set.getCost();
    System.out.println("TESTING FLUSH...");
    System.out.println(set.toString());
    System.out.println(combination.toString());
    if (combination.getCombination() == 6) {
      System.out.println("RESULT:OK");
    }
    else {
      System.out.println("RESULT:ERROR");
    }
    System.out.println();

    set.deleteCards();
    set.addCommonCard(new Card(1, 2));
    set.addCommonCard(new Card(4, 14));
    set.addCommonCard(new Card(4, 11));
    set.addCommonCard(new Card(3, 14));
    set.addCommonCard(new Card(2, 12));
    set.addCommonCard(new Card(1, 12));
    set.addCommonCard(new Card(2, 9));
    set.addCommonCard(new Card(2, 10));
    set.addCommonCard(new Card(4, 10));
    set.addCommonCard(new Card(4, 8));

    combination = set.getCost();
    System.out.println("TESTING STRAIGHT...");
    System.out.println(set.toString());
    System.out.println(combination.toString());
    if (combination.getCombination() == 7) {
      System.out.println("RESULT:OK");
    }
    else {
      System.out.println("RESULT:ERROR");
    }
    System.out.println();

    set.deleteCards();
    set.addCommonCard(new Card(1, 2));
    set.addCommonCard(new Card(4, 14));
    set.addCommonCard(new Card(4, 7));
    set.addCommonCard(new Card(3, 14));
    set.addCommonCard(new Card(2, 14));
    set.addCommonCard(new Card(1, 12));
    set.addCommonCard(new Card(2, 9));
    set.addCommonCard(new Card(2, 5));
    set.addCommonCard(new Card(4, 10));
    set.addCommonCard(new Card(4, 8));

    combination = set.getCost();
    System.out.println("TESTING THREE_OF_A_KIND...");
    System.out.println(set.toString());
    System.out.println(combination.toString());
    if (combination.getCombination() == 8) {
      System.out.println("RESULT:OK");
    }
    else {
      System.out.println("RESULT:ERROR");
    }
    System.out.println();

    set.deleteCards();
    set.addCommonCard(new Card(1, 2));
    set.addCommonCard(new Card(4, 13));
    set.addCommonCard(new Card(4, 7));
    set.addCommonCard(new Card(3, 13));
    set.addCommonCard(new Card(1, 12));
    set.addCommonCard(new Card(2, 9));
    set.addCommonCard(new Card(2, 5));
    set.addCommonCard(new Card(4, 10));
    set.addCommonCard(new Card(2, 10));

    combination = set.getCost();
    System.out.println("TESTING TWO_PAIRS...");
    System.out.println(set.toString());
    System.out.println(combination.toString());
    if (combination.getCombination() == 9) {
      System.out.println("RESULT:OK");
    }
    else {
      System.out.println("RESULT:ERROR");
    }
    System.out.println();

    set.deleteCards();
    set.addCommonCard(new Card(1, 2));
    set.addCommonCard(new Card(4, 7));
    set.addCommonCard(new Card(3, 12));
    set.addCommonCard(new Card(1, 12));
    set.addCommonCard(new Card(2, 9));
    set.addCommonCard(new Card(2, 5));
    set.addCommonCard(new Card(4, 10));
    set.addCommonCard(new Card(2, 14));

    combination = set.getCost();
    System.out.println("TESTING PAIRS...");
    System.out.println(set.toString());
    System.out.println(combination.toString());
    if (combination.getCombination() == 10) {
      System.out.println("RESULT:OK");
    }
    else {
      System.out.println("RESULT:ERROR");
    }
    System.out.println();

    set.deleteCards();
    set.addCommonCard(new Card(1, 2));
    set.addCommonCard(new Card(4, 7));
    set.addCommonCard(new Card(3, 12));
    set.addCommonCard(new Card(2, 9));
    set.addCommonCard(new Card(2, 5));
    set.addCommonCard(new Card(4, 10));
    set.addCommonCard(new Card(2, 14));

    combination = set.getCost();
    System.out.println("TESTING HIGH CARD...");
    System.out.println(set.toString());
    System.out.println(combination.toString());
    if (combination.getCombination() == 11) {
      System.out.println("RESULT:OK");
    }
    else {
      System.out.println("RESULT:ERROR");
    }
    System.out.println();

    System.out.println();
    System.out.println();
    System.out.println("TESTING ALL pack...");
    set.deleteCards();
    ArrayList values = new ArrayList();
    ArrayList suits = new ArrayList();

    values.add("2");
    values.add("3");
    values.add("4");
    values.add("5");
    values.add("6");
    values.add("7");
    values.add("8");
    values.add("9");
    values.add("10");
    values.add("11");
    values.add("12");
    values.add("13");
    values.add("14");

    suits.add("1");
    suits.add("2");
    suits.add("3");
    suits.add("4");

    int cnt = 0;
    for (int i = 0; i < values.size(); i++) {
      for (int j = 0; j < suits.size(); j++) {
        set.addCommonCard(new Card(Integer.parseInt((String)suits.get(j)), Integer.parseInt((String)values.get(i))));
        cnt++;
      }

    }

    System.out.println("    TOTAL: " + cnt);
    System.out.println(set.toString());

    System.out.println(set.getCost());

    ArrayList cards = new ArrayList();
    Iterator own = set.getOwnCards().iterator();
    while (own.hasNext()) {
      cards.add(own.next());
    }
    Iterator common = set.getCommonCards().iterator();
    while (common.hasNext()) {
      cards.add(common.next());
    }

    CombinationChecker checker = new CombinationChecker(cards);

    System.out.println("  Check For Straight Flash");
    ArrayList l = checker.checkOnStraightFlashes();
    for (int i = 0; i < l.size(); i++) {
      System.out.print(((Combination)l.get(i)).getCombination() == 3 ? "   STRAIGHT FLUSH: " : ((Combination)l.get(i)).getCombination() == 2 ? "   ROYAL FLASH: " : "   UNKNOWN: ");
      System.out.println(l.get(i));
    }

    System.out.println();
    System.out.println();
    System.out.println("  Check For Flash ");
    Combination ll = checker.checkOnFlush();
    if (ll != null) {
      System.out.print(ll.getCombination() == 6 ? "   FLASH: " : "   UNKNOWN: ");
    }
    System.out.println(ll);

    System.out.println();
    System.out.println();
    System.out.println("  Check For Four Of A KIND ");
    l = checker.checkOnFourOfKind();
    for (int i = 0; i < l.size(); i++) {
      System.out.print(((Combination)l.get(i)).getCombination() == 4 ? "   Four of a kind : " : "   UNKNOWN: ");
      System.out.println(l.get(i));
    }

    System.out.println();
    System.out.println();
    System.out.println("  Check For Straight");
    l = checker.checkOnStraights();
    System.out.println("  Total: " + l.size());
    for (int i = 0; i < l.size(); i++) {
      System.out.print(((Combination)l.get(i)).getCombination() == 7 ? "   Straight : " : "   UNKNOWN: ");
      System.out.println(l.get(i));
    }

    System.out.println();
    System.out.println();
    System.out.println("  Check For Full House ");
    l = checker.checkOnFullHouse();
    for (int i = 0; i < l.size(); i++) {
      System.out.print(((Combination)l.get(i)).getCombination() == 5 ? "   Full House : " : "   UNKNOWN: ");
      System.out.println(l.get(i));
    }

    System.out.println();
    System.out.println();
    System.out.println("  Check For 3 of a kind ");
    l = checker.checkOnThreeOfKind();
    for (int i = 0; i < l.size(); i++) {
      System.out.print(((Combination)l.get(i)).getCombination() == 8 ? "   3 of a kind : " : "   UNKNOWN: ");
      System.out.println(l.get(i));
    }

    System.out.println();
    System.out.println();
    System.out.println("  Check For 2 pairs ");
    l = checker.checkOnPairs();
    for (int i = 0; i < l.size(); i++) {
      System.out.print(((Combination)l.get(i)).getCombination() == 9 ? "   2 of a house : " : "   UNKNOWN: ");
      System.out.println(l.get(i));
    }

    System.out.println();
    System.out.println();
    System.out.println("  Check For 1 pair ");
    l = checker.checkOnPairs();
    for (int i = 0; i < l.size(); i++) {
      System.out.print(((Combination)l.get(i)).getCombination() == 10 ? "   1 pair : " : "   UNKNOWN: ");
      System.out.println(l.get(i));
    }

    System.out.println();
    System.out.println();
    System.out.println("  Check For A HIGHT CARD ");
    l = checker.checkOnHighCard();
    for (int i = 0; i < l.size(); i++) {
      System.out.print(((Combination)l.get(i)).getCombination() == 11 ? "   High Card : " : "   UNKNOWN: ");
      System.out.println(l.get(i));
    }

    System.out.println("");
    System.out.println("");
    System.out.println("");
    System.out.println("||| DIFFERENT COMBINATIONS |||");

    CardsSet testSet = new CardsSet();
    testSet.setGameType(3);

    testSet.addCommonCard(new Card(2, 5));

    testSet.addCommonCard(new Card(1, 11));
    testSet.addOwnCard(new Card(1, 14));
    testSet.addOwnCard(new Card(4, 14));
    testSet.addOwnCard(new Card(2, 3));
    testSet.addOwnCard(new Card(2, 2));
    testSet.addCommonCard(new Card(3, 14));
    testSet.addCommonCard(new Card(2, 14));

    System.out.println("....Testing ....");
    System.out.println();
    System.out.println("CardSet: " + testSet);
    Combination comb = testSet.getCost();
    System.out.println("Combination: " + comb);

    System.out.println("");
    System.out.println("");
    System.out.println("");
    System.out.println("||| 2 PAIRS |||");

    testSet = new CardsSet();
    testSet.setGameType(3);

    testSet.addCommonCard(new Card(2, 5));
    testSet.addCommonCard(new Card(2, 11));
    testSet.addCommonCard(new Card(1, 11));
    testSet.addOwnCard(new Card(1, 14));
    testSet.addOwnCard(new Card(4, 6));
    testSet.addCommonCard(new Card(3, 6));
    testSet.addCommonCard(new Card(2, 14));

    System.out.println("....Testing 2 PAIRS ACE AND JACK....");
    System.out.println();
    System.out.println("CardSet: " + testSet);
    comb = testSet.getCost();
    System.out.println("Combination: " + comb);

    System.out.println("");
    System.out.println("");
    System.out.println("");
    System.out.println("||| 1 PAIR |||");

    testSet = new CardsSet();
    testSet.setGameType(3);

    testSet.addCommonCard(new Card(2, 5));
    testSet.addCommonCard(new Card(2, 11));
    testSet.addCommonCard(new Card(1, 7));
    testSet.addOwnCard(new Card(1, 14));
    testSet.addOwnCard(new Card(4, 6));
    testSet.addCommonCard(new Card(3, 6));
    testSet.addCommonCard(new Card(2, 14));

    System.out.println("....Testing 1 PAIR ACE....");
    System.out.println();
    System.out.println("CardSet: " + testSet);
    comb = testSet.getCost();
    System.out.println("Combination: " + comb);

    System.out.println("");
    System.out.println("");
    System.out.println("");
    System.out.println("||| 1 PAIRS |||");

    testSet = new CardsSet();
    testSet.setGameType(3);

    System.out.println("....Testing 1 PAIR SIX....");
    System.out.println();
    System.out.println("CardSet: " + testSet);
    comb = testSet.getCost();
    System.out.println("Combination: " + comb);

    testSet = new CardsSet();
    testSet.setGameType(1);

    testSet.addOwnCard(new Card(1, 14));

    System.out.println("....Testing Hi CARD ACE....");
    System.out.println();
    System.out.println("CardSet: " + testSet);
    comb = testSet.getCost();
    System.out.println("Combination: " + comb);

    testSet = new CardsSet();
    testSet.setGameType(1);

    testSet.addCommonCard(new Card(1, 5));
    testSet.addCommonCard(new Card(1, 11));
    testSet.addCommonCard(new Card(2, 7));
    testSet.addOwnCard(new Card(3, 13));
    testSet.addOwnCard(new Card(3, 6));
    testSet.addCommonCard(new Card(1, 2));
    testSet.addCommonCard(new Card(3, 9));

    System.out.println("....Testing COMB 1....");
    System.out.println();
    System.out.println("CardSet: " + testSet);
    comb = testSet.getCost();
    System.out.println("Combination: " + comb);

    testSet = new CardsSet();
    testSet.setGameType(1);

    testSet.addCommonCard(new Card(2, 5));
    testSet.addCommonCard(new Card(2, 10));
    testSet.addCommonCard(new Card(1, 7));
    testSet.addOwnCard(new Card(1, 13));
    testSet.addOwnCard(new Card(4, 2));
    testSet.addCommonCard(new Card(3, 6));
    testSet.addCommonCard(new Card(2, 9));

    System.out.println("....Testing COMB 2....");
    System.out.println();
    System.out.println("CardSet: " + testSet);
    Combination c = testSet.getCost();
    System.out.println("Combination: " + c);

    System.out.println("Combinations .equals : " + comb.compareTo(c));
  }
}