package game.cards.comparators;

import game.Card;
import game.cards.CardsSet;
import game.cards.Combination;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.log4j.Logger;

public class DefaultComparator
  implements Comparator
{
  static Logger log = Logger.getLogger(DefaultComparator.class);

  public int compare(Object paramObject1, Object paramObject2)
  {
    Combination localCombination1 = (Combination)paramObject1;
    Combination localCombination2 = (Combination)paramObject2;

    log.info("comb1:" + localCombination1.toString());
    log.info("comb2:" + localCombination2.toString());

    if (localCombination1.getCombination() > localCombination2.getCombination()) {
      return -10;
    }

    if (localCombination1.getCombination() < localCombination2.getCombination()) {
      return 10;
    }

    if ((localCombination1.getCombination() == 11) || (localCombination1.getCombination() == 10) || (localCombination1.getCombination() == 8))
    {
      if (localCombination1.getHighCard() < localCombination2.getHighCard()) {
        return -1;
      }

      if (localCombination1.getHighCard() > localCombination2.getHighCard()) {
        return 1;
      }

    }

    if ((localCombination1.getCombination() == 7) || (localCombination1.getCombination() == 6) || (localCombination1.getCombination() == 3) || (localCombination1.getCombination() == 2))
    {
      for (int i = 0; i < 5; i++) {
        if (((Card)localCombination1.getCards().get(i)).getValue() > ((Card)localCombination2.getCards().get(i)).getValue())
          return 1;
        if (((Card)localCombination1.getCards().get(i)).getValue() < ((Card)localCombination2.getCards().get(i)).getValue())
          return -1;
      }
      return 0;
    }

    if (localCombination1.getCombination() == 9)
    {
      if (localCombination1.getHighCard() < localCombination2.getHighCard()) {
        return -1;
      }

      if (localCombination1.getHighCard() > localCombination2.getHighCard()) {
        return 1;
      }

      if (localCombination1.getLowCard() < localCombination2.getLowCard()) {
        return -2;
      }

      if (localCombination1.getLowCard() > localCombination2.getLowCard()) {
        return 2;
      }

    }

    if (localCombination1.getCombination() == 5)
    {
      if (localCombination1.getHighCard() < localCombination2.getHighCard()) {
        return -1;
      }

      if (localCombination1.getHighCard() > localCombination2.getHighCard()) {
        return 1;
      }

      if (localCombination1.getLowCard() < localCombination2.getLowCard()) {
        return -2;
      }

      if (localCombination1.getLowCard() > localCombination2.getLowCard()) {
        return 2;
      }

      return 0;
    }

    if (localCombination1.getCombination() == 4)
    {
      if (localCombination1.getHighCard() < localCombination2.getHighCard()) {
        return -1;
      }

      if (localCombination1.getHighCard() > localCombination2.getHighCard()) {
        return 1;
      }

    }

    List localList1 = localCombination1.getOtherCards();
    List localList2 = localCombination2.getOtherCards();
    Collections.sort(localList1, new DefaultOtherCardsSortComparator(null));
    Collections.sort(localList2, new DefaultOtherCardsSortComparator(null));

    int j = localList1.size() < localList2.size() ? localList1.size() : localList2.size();

    if (localCombination1.getCombination() == 11)
      j = 4;
    else if (localCombination1.getCombination() == 10)
      j = 3;
    else if (localCombination1.getCombination() == 8)
      j = 2;
    else if (localCombination1.getCombination() == 9)
      j = 1;
    else if (localCombination1.getCombination() == 4) {
      j = 1;
    }

    for (int k = 0; k < j; k++) {
      Card localCard1 = (Card)localList1.get(k);
      Card localCard2 = (Card)localList2.get(k);

      log.info("othercards1:card" + k + ":" + localCard1.getValue());
      log.info("othercards2:card" + k + ":" + localCard2.getValue());

      if ((localCard1 == null) || (localCard2 == null))
        continue;
      if (localCard1.getValue() < localCard2.getValue()) {
        return -9;
      }

      if (localCard1.getValue() > localCard2.getValue()) {
        return 9;
      }

    }

    if (localList1.size() < localList2.size())
      return -11;
    if (localList1.size() > localList2.size()) {
      return 11;
    }

    return 0;
  }

  public static void main(String[] paramArrayOfString)
  {
    CardsSet localCardsSet1 = new CardsSet();
    localCardsSet1.setGameType(1);
    localCardsSet1.addOwnCard(new Card(1, 11));
    localCardsSet1.addOwnCard(new Card(1, 12));
    localCardsSet1.addCommonCard(new Card(1, 6));
    localCardsSet1.addCommonCard(new Card(1, 7));
    localCardsSet1.addCommonCard(new Card(1, 14));
    localCardsSet1.addCommonCard(new Card(3, 4));
    localCardsSet1.addCommonCard(new Card(1, 4));
    Combination localCombination1 = localCardsSet1.getCost();
    System.out.println("====Combination");
    System.out.println(localCombination1.toString());

    CardsSet localCardsSet2 = new CardsSet();
    localCardsSet2.setGameType(1);
    localCardsSet2.addOwnCard(new Card(1, 11));
    localCardsSet2.addOwnCard(new Card(1, 12));
    localCardsSet2.addCommonCard(new Card(1, 6));
    localCardsSet2.addCommonCard(new Card(1, 7));
    localCardsSet2.addCommonCard(new Card(1, 14));
    localCardsSet2.addCommonCard(new Card(3, 4));
    localCardsSet2.addCommonCard(new Card(1, 4));
    Combination localCombination2 = localCardsSet2.getCost();
    System.out.println("====Combination");
    System.out.println(localCombination2.toString());

    DefaultComparator localDefaultComparator = new DefaultComparator();
    System.out.println("compare=" + localDefaultComparator.compare(localCombination1, localCombination2));
  }
  private class DefaultOtherCardsSortComparator implements Comparator {
    private DefaultOtherCardsSortComparator() {
    }
    public int compare(Object paramObject1, Object paramObject2) {
      Card localCard1 = (Card)paramObject1;
      Card localCard2 = (Card)paramObject2;

      if (localCard1.getValue() < localCard2.getValue()) {
        return 9;
      }

      if (localCard1.getValue() > localCard2.getValue()) {
        return -9;
      }

      return 0;
    }
  }
}