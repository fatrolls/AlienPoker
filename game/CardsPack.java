package game;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class CardsPack
{
  private ArrayList cards = new ArrayList();

  private static final int[] suites = { 1, 4, 3, 2 };
  private static final int[] values = { 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2 };
  private static final int SHUFLES_COUNT = 54;

  public CardsPack()
  {
    for (int i = 0; i < suites.length; i++)
      for (int j = 0; j < values.length; j++)
        cards.add(new Card(suites[i], values[j]));
  }

  public int size()
  {
    return cards.size();
  }

  private void tradeCardsPlaces(int from, int to)
  {
    Card fromIndexCard = (Card)cards.get(from);
    Card toIndexCard = (Card)cards.get(to);

    cards.set(from, toIndexCard);
    cards.set(to, fromIndexCard);
  }

  public void shuffle()
  {
    Random randomizer = new Random();
    int packSize = cards.size();

    for (int i = 0; i < 54; i++) {
      int fromIndex = randomizer.nextInt(packSize);
      int toIndex = randomizer.nextInt(packSize);

      tradeCardsPlaces(fromIndex, toIndex);
    }
  }

  public boolean hasMoreCards()
  {
    return cards.size() > 0;
  }

  public Card getNextCard()
  {
    return (Card)cards.remove(0);
  }

  public void invalidate()
  {
    cards.clear();
    cards = null;
  }

  public String toString()
  {
    StringBuffer buffer = new StringBuffer();

    Iterator it = cards.iterator();
    int i = 1;
    while (it.hasNext()) {
      Card c = (Card)it.next();
      buffer.append(i).append(':').append(c.getSuite()).append('-').append(c.getValue()).append('\n');

      i++;
    }

    return buffer.toString();
  }

  public static void main(String[] args)
  {
    CardsPack pack = new CardsPack();

    System.out.println(pack.toString());
    System.out.println("-------------------");

    pack.shuffle();
    System.out.println(pack.toString());
  }
}