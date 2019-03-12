package game;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;

public class DeskStats
{
  public static final long ONE_HOUR_MILLIS = 3600000L;
  private LinkedList hands;
  private BigDecimal averagePot;
  private int handsCount;
  private BigDecimal flopsPercent;
  private int flopsCount;

  public DeskStats()
  {
    hands = new LinkedList();

    averagePot = new BigDecimal(0);
    handsCount = 0;
    flopsPercent = new BigDecimal(0);
    flopsCount = 0;
  }

  public void addHandPot(BigDecimal pot, boolean isFlop) {
    hands.addLast(new Hand(pot, isFlop, System.currentTimeMillis()));
  }

  public void deleteOldHands(long time)
  {
    if (hands.size() > 0) {
      Hand hand = null;
      while (((hand = (Hand)hands.getFirst()) != null) && 
        (hand.getTime() - time > 3600000L))
        hands.removeFirst();
    }
  }

  public void recalculate()
  {
    handsCount = 0;
    flopsCount = 0;
    flopsPercent = new BigDecimal(0);
    averagePot = new BigDecimal(0);

    BigDecimal totalPot = new BigDecimal(0);

    Iterator it = hands.iterator();
    while (it.hasNext()) {
      Hand hand = (Hand)it.next();

      totalPot = totalPot.add(hand.getPot()).setScale(2, 5);
      handsCount += 1;

      if (hand.isFlop()) {
        flopsCount += 1;
      }
    }

    if (handsCount != 0) {
      averagePot = totalPot.divide(new BigDecimal(handsCount), 2, 5);
      if (flopsCount > 0)
        flopsPercent = new BigDecimal(flopsCount).divide(new BigDecimal(handsCount), 2, 5).multiply(new BigDecimal(100)).setScale(2, 5);
    }
  }

  public BigDecimal getAveragePot()
  {
    return averagePot;
  }

  public int getHandsCount()
  {
    return handsCount;
  }

  public BigDecimal getFlopsPercent()
  {
    return flopsPercent;
  }

  public static void main(String[] args)
  {
    DeskStats stats = new DeskStats();

    stats.addHandPot(new BigDecimal(100), true);
    stats.addHandPot(new BigDecimal(200), false);
    stats.addHandPot(new BigDecimal(10), true);

    stats.recalculate();
  }

  private class Hand
  {
    private long time = 0L;
    private BigDecimal pot = new BigDecimal(0);
    private boolean flop = false;

    public Hand(BigDecimal pot, boolean flop, long time)
    {
      this.time = time;
      this.pot = pot;
      this.flop = flop;
    }

    public boolean isFlop()
    {
      return flop;
    }

    public long getTime()
    {
      return time;
    }

    public BigDecimal getPot()
    {
      return pot;
    }
  }
}