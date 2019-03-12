package game.stats;

import defaultvalues.DefaultValue;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class PlaceSessionStats
{
  private static final long ONEHOUR = 3600000L;
  public static final BigDecimal ONE = new BigDecimal(1).setScale(2, 5);
  public static final BigDecimal ZERO = new BigDecimal(0).setScale(2, 5);
  private Date sessionStart;
  private long sessionGames;
  private BigDecimal averagePotForLastHour;
  private BigDecimal flopPercentForLastHour;
  private int handsPerHourForLastHour;
  private BigDecimal totalBetedForLastHour;
  private BigDecimal totalWonForLastHour;
  private final List avgePotStorage;
  private final List flopStorage;
  private static final String TAG_NAME_PLACE_SESS_STATS = "SESS";
  private static final String OUT_PARAM_SESS_START = "SST";
  private static final String OUT_PARAM_SESS_GAMES = "SESG";
  private static final String OUT_PARAM_AVG_POT = "AVGP";
  private static final String OUT_PARAM_FLOP_PERCENT = "FLP";
  private static final String OUT_PARAM_HANDS = "HANDS";
  private static final String OUT_PARAM_TOTAL_BET = "TBET";
  private static final String OUT_PARAM_TOTAL_WON = "TWON";

  public PlaceSessionStats()
  {
    sessionStart = new Date();
    sessionGames = 0L;

    averagePotForLastHour = DefaultValue.ZERO_BIDECIMAL;
    flopPercentForLastHour = DefaultValue.ZERO_BIDECIMAL;
    handsPerHourForLastHour = 0;
    totalBetedForLastHour = DefaultValue.ZERO_BIDECIMAL;
    totalWonForLastHour = DefaultValue.ZERO_BIDECIMAL;

    avgePotStorage = Collections.synchronizedList(new LinkedList());
    flopStorage = Collections.synchronizedList(new LinkedList());
  }

  public void clear()
  {
    sessionStart = new Date();
    sessionGames = 0L;
    averagePotForLastHour = DefaultValue.ZERO_BIDECIMAL;
    flopPercentForLastHour = DefaultValue.ZERO_BIDECIMAL;
    handsPerHourForLastHour = 0;
    totalBetedForLastHour = DefaultValue.ZERO_BIDECIMAL;
    totalWonForLastHour = DefaultValue.ZERO_BIDECIMAL;

    avgePotStorage.clear();
    flopStorage.clear();
  }

  private void filterCollection(List list)
  {
    long currentDate = new Date().getTime();
    synchronized (list) {
      int size = list.size();
      for (int i = size - 1; i >= 0; i--) {
        DateAndBigDecimal dn = (DateAndBigDecimal)list.get(i);
        if (currentDate - dn.getDate() > 3600000L)
          list.remove(i);
      }
    }
  }

  private BigDecimal countAverage(List avgStorage, BigDecimal bigDecimal)
  {
    filterCollection(avgStorage);
    BigDecimal avg = DefaultValue.ZERO_BIDECIMAL;
    int count = 0;
    synchronized (avgStorage) {
      avgStorage.add(new DateAndBigDecimal(bigDecimal));
      Iterator iter = avgStorage.iterator();
      while (iter.hasNext()) {
        DateAndBigDecimal dab = (DateAndBigDecimal)iter.next();
        count++;
        avg = avg.add(dab.getBigDecimal());
      }
    }

    if (count > 0)
      avg = avg.divide(new BigDecimal(2), 2, 5);
    else {
      avg = DefaultValue.ZERO_BIDECIMAL;
    }

    return avg;
  }

  public void countAveragePotForLastHour(BigDecimal pot)
  {
    averagePotForLastHour = countAverage(avgePotStorage, pot);
  }

  public void countFlopPercentForLastHour(boolean flop)
  {
    flopPercentForLastHour = countAverage(flopStorage, flop ? ONE : ZERO);
  }

  public void countHandsPerHourForLastHour()
  {
    handsPerHourForLastHour += 1;
    sessionGames += 1L;
  }

  public void countTotalBettedForLastHour(BigDecimal amount)
  {
    totalBetedForLastHour = totalBetedForLastHour.add(amount).setScale(2, 5);
  }

  public void countTotalWonForLastHour(BigDecimal wonSum)
  {
    totalWonForLastHour = totalWonForLastHour.add(wonSum).setScale(2, 5);
  }

  public BigDecimal getAveragePotForLastHour()
  {
    return averagePotForLastHour;
  }

  public BigDecimal getFlopPercentForLastHour() {
    return flopPercentForLastHour;
  }

  public int getHandsPerHourForLastHour() {
    return handsPerHourForLastHour;
  }

  public BigDecimal getTotalBetedForLastHour() {
    return totalBetedForLastHour;
  }

  public BigDecimal getTotalWonForLastHour() {
    return totalWonForLastHour;
  }

  public long getSessionGames() {
    return sessionGames;
  }

  public Date getSessionStart() {
    return sessionStart;
  }

  public String toXML()
  {
    XMLDoc doc = new XMLDoc();
    XMLTag tag = doc.startTag("SESS");
    tag.addParam("SST", (float)sessionStart.getTime());
    tag.addParam("SESG", (float)sessionGames);
    tag.addParam("AVGP", averagePotForLastHour.toString());
    tag.addParam("FLP", flopPercentForLastHour.toString());
    tag.addParam("HANDS", handsPerHourForLastHour);
    tag.addParam("TBET", totalBetedForLastHour.toString());
    tag.addParam("TWON", totalWonForLastHour.toString());

    String xml = doc.toString();
    doc.invalidate();
    return xml;
  }

  private class DateAndBigDecimal
  {
    private BigDecimal bigDecimal;
    private long date;

    public DateAndBigDecimal(BigDecimal bigDecimal)
    {
      date = 0L;
      this.bigDecimal = bigDecimal;
    }

    public BigDecimal getBigDecimal() {
      return bigDecimal;
    }

    public long getDate() {
      return date;
    }
  }
}