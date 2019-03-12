package tournaments.payout;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import utils.xml.XMLTag;

public class MultiTournamentPayoutTable
{
  private static final LinkedHashMap table = new LinkedHashMap();
  private static MultiTournamentPayoutTable instance = new MultiTournamentPayoutTable();
  private static final String TAG_NAME_PLACE = "PLACE";
  private static final String TAG_NAME_TABLE = "PRIZES";
  private static final String TAG_PARAM_PLACE_NUMBER = "NUM";
  private static final String TAG_PARAM_PERCENT = "PERCENT";
  private static final int MAX_AVAILABLE_PLACE = 250;

  public static MultiTournamentPayoutTable getInstance()
  {
    return instance;
  }

  private MultiTournamentPayoutTable()
  {
    LinkedHashMap tmpMap = new LinkedHashMap();
    tmpMap.put(new Integer(10), new BigDecimal("0.5"));
    tmpMap.put(new Integer(21), new BigDecimal("0.45"));
    tmpMap.put(new Integer(31), new BigDecimal("0.40"));
    tmpMap.put(new Integer(51), new BigDecimal("0.30"));
    tmpMap.put(new Integer(101), new BigDecimal("0.275"));
    tmpMap.put(new Integer(201), new BigDecimal("0.27"));
    tmpMap.put(new Integer(301), new BigDecimal("0.25"));
    tmpMap.put(new Integer(401), new BigDecimal("0.25"));
    tmpMap.put(new Integer(501), new BigDecimal("0.25"));
    tmpMap.put(new Integer(601), new BigDecimal("0.25"));
    tmpMap.put(new Integer(801), new BigDecimal("0.25"));
    tmpMap.put(new Integer(1001), new BigDecimal("0.25"));
    tmpMap.put(new Integer(1501), new BigDecimal("0.225"));

    table.put(new Integer(1), tmpMap);

    tmpMap = new LinkedHashMap();
    tmpMap.put(new Integer(10), new BigDecimal("0.30"));
    tmpMap.put(new Integer(21), new BigDecimal("0.25"));
    tmpMap.put(new Integer(31), new BigDecimal("0.24"));
    tmpMap.put(new Integer(51), new BigDecimal("0.20"));
    tmpMap.put(new Integer(101), new BigDecimal("0.17"));
    tmpMap.put(new Integer(201), new BigDecimal("0.165"));
    tmpMap.put(new Integer(301), new BigDecimal("0.16"));
    tmpMap.put(new Integer(401), new BigDecimal("0.15"));
    tmpMap.put(new Integer(501), new BigDecimal("0.15"));
    tmpMap.put(new Integer(601), new BigDecimal("0.145"));
    tmpMap.put(new Integer(801), new BigDecimal("0.14"));
    tmpMap.put(new Integer(1001), new BigDecimal("0.14"));
    tmpMap.put(new Integer(1501), new BigDecimal("0.125"));

    table.put(new Integer(2), tmpMap);

    tmpMap = new LinkedHashMap();
    tmpMap.put(new Integer(10), new BigDecimal("0.20"));
    tmpMap.put(new Integer(21), new BigDecimal("0.17"));
    tmpMap.put(new Integer(31), new BigDecimal("0.16"));
    tmpMap.put(new Integer(51), new BigDecimal("0.12"));
    tmpMap.put(new Integer(101), new BigDecimal("0.115"));
    tmpMap.put(new Integer(201), new BigDecimal("0.11"));
    tmpMap.put(new Integer(301), new BigDecimal("0.105"));
    tmpMap.put(new Integer(401), new BigDecimal("0.10"));
    tmpMap.put(new Integer(501), new BigDecimal("0.095"));
    tmpMap.put(new Integer(601), new BigDecimal("0.0925"));
    tmpMap.put(new Integer(801), new BigDecimal("0.09"));
    tmpMap.put(new Integer(1001), new BigDecimal("0.085"));
    tmpMap.put(new Integer(1501), new BigDecimal("0.085"));

    table.put(new Integer(3), tmpMap);

    tmpMap = new LinkedHashMap();
    tmpMap.put(new Integer(21), new BigDecimal("0.13"));
    tmpMap.put(new Integer(31), new BigDecimal("0.12"));
    tmpMap.put(new Integer(51), new BigDecimal("0.0925"));
    tmpMap.put(new Integer(101), new BigDecimal("0.0850"));
    tmpMap.put(new Integer(201), new BigDecimal("0.08"));
    tmpMap.put(new Integer(301), new BigDecimal("0.08"));
    tmpMap.put(new Integer(401), new BigDecimal("0.075"));
    tmpMap.put(new Integer(501), new BigDecimal("0.07"));
    tmpMap.put(new Integer(601), new BigDecimal("0.0675"));
    tmpMap.put(new Integer(801), new BigDecimal("0.0650"));
    tmpMap.put(new Integer(1001), new BigDecimal("0.0650"));
    tmpMap.put(new Integer(1501), new BigDecimal("0.0650"));

    table.put(new Integer(4), tmpMap);

    tmpMap = new LinkedHashMap();
    tmpMap.put(new Integer(31), new BigDecimal("0.08"));
    tmpMap.put(new Integer(51), new BigDecimal("0.0750"));
    tmpMap.put(new Integer(101), new BigDecimal("0.0725"));
    tmpMap.put(new Integer(201), new BigDecimal("0.07"));
    tmpMap.put(new Integer(301), new BigDecimal("0.07"));
    tmpMap.put(new Integer(401), new BigDecimal("0.0650"));
    tmpMap.put(new Integer(501), new BigDecimal("0.06"));
    tmpMap.put(new Integer(601), new BigDecimal("0.0575"));
    tmpMap.put(new Integer(801), new BigDecimal("0.0550"));
    tmpMap.put(new Integer(1001), new BigDecimal("0.0525"));
    tmpMap.put(new Integer(1501), new BigDecimal("0.0525"));

    table.put(new Integer(5), tmpMap);

    tmpMap = new LinkedHashMap();
    tmpMap.put(new Integer(51), new BigDecimal("0.0625"));
    tmpMap.put(new Integer(101), new BigDecimal("0.0575"));
    tmpMap.put(new Integer(201), new BigDecimal("0.0550"));
    tmpMap.put(new Integer(301), new BigDecimal("0.0550"));
    tmpMap.put(new Integer(401), new BigDecimal("0.0550"));
    tmpMap.put(new Integer(501), new BigDecimal("0.05"));
    tmpMap.put(new Integer(601), new BigDecimal("0.0475"));
    tmpMap.put(new Integer(801), new BigDecimal("0.0450"));
    tmpMap.put(new Integer(1001), new BigDecimal("0.0425"));
    tmpMap.put(new Integer(1501), new BigDecimal("0.0425"));

    table.put(new Integer(6), tmpMap);

    tmpMap = new LinkedHashMap();
    tmpMap.put(new Integer(51), new BigDecimal("0.0525"));
    tmpMap.put(new Integer(101), new BigDecimal("0.0450"));
    tmpMap.put(new Integer(201), new BigDecimal("0.0450"));
    tmpMap.put(new Integer(301), new BigDecimal("0.0450"));
    tmpMap.put(new Integer(401), new BigDecimal("0.0450"));
    tmpMap.put(new Integer(501), new BigDecimal("0.04"));
    tmpMap.put(new Integer(601), new BigDecimal("0.0375"));
    tmpMap.put(new Integer(801), new BigDecimal("0.0350"));
    tmpMap.put(new Integer(1001), new BigDecimal("0.0325"));
    tmpMap.put(new Integer(1501), new BigDecimal("0.0325"));

    table.put(new Integer(7), tmpMap);

    tmpMap = new LinkedHashMap();
    tmpMap.put(new Integer(51), new BigDecimal("0.0425"));
    tmpMap.put(new Integer(101), new BigDecimal("0.03"));
    tmpMap.put(new Integer(201), new BigDecimal("0.03"));
    tmpMap.put(new Integer(301), new BigDecimal("0.03"));
    tmpMap.put(new Integer(401), new BigDecimal("0.03"));
    tmpMap.put(new Integer(501), new BigDecimal("0.03"));
    tmpMap.put(new Integer(601), new BigDecimal("0.0275"));
    tmpMap.put(new Integer(801), new BigDecimal("0.0250"));
    tmpMap.put(new Integer(1001), new BigDecimal("0.0225"));
    tmpMap.put(new Integer(1501), new BigDecimal("0.0225"));

    table.put(new Integer(8), tmpMap);

    tmpMap = new LinkedHashMap();
    tmpMap.put(new Integer(51), new BigDecimal("0.0325"));
    tmpMap.put(new Integer(101), new BigDecimal("0.02"));
    tmpMap.put(new Integer(201), new BigDecimal("0.0175"));
    tmpMap.put(new Integer(301), new BigDecimal("0.0175"));
    tmpMap.put(new Integer(401), new BigDecimal("0.0175"));
    tmpMap.put(new Integer(501), new BigDecimal("0.0175"));
    tmpMap.put(new Integer(601), new BigDecimal("0.0175"));
    tmpMap.put(new Integer(801), new BigDecimal("0.0150"));
    tmpMap.put(new Integer(1001), new BigDecimal("0.0150"));
    tmpMap.put(new Integer(1501), new BigDecimal("0.0150"));

    table.put(new Integer(9), tmpMap);

    tmpMap = new LinkedHashMap();
    tmpMap.put(new Integer(51), new BigDecimal("0.0225"));
    tmpMap.put(new Integer(101), new BigDecimal("0.0150"));
    tmpMap.put(new Integer(201), new BigDecimal("0.0125"));
    tmpMap.put(new Integer(301), new BigDecimal("0.0125"));
    tmpMap.put(new Integer(401), new BigDecimal("0.0125"));
    tmpMap.put(new Integer(501), new BigDecimal("0.0125"));
    tmpMap.put(new Integer(601), new BigDecimal("0.0125"));
    tmpMap.put(new Integer(801), new BigDecimal("0.01"));
    tmpMap.put(new Integer(1001), new BigDecimal("0.01"));
    tmpMap.put(new Integer(1501), new BigDecimal("0.01"));

    table.put(new Integer(10), tmpMap);

    tmpMap = new LinkedHashMap();
    tmpMap.put(new Integer(101), new BigDecimal("0.012"));
    tmpMap.put(new Integer(201), new BigDecimal("0.0095"));
    tmpMap.put(new Integer(301), new BigDecimal("0.0095"));
    tmpMap.put(new Integer(401), new BigDecimal("0.0095"));
    tmpMap.put(new Integer(501), new BigDecimal("0.0095"));
    tmpMap.put(new Integer(601), new BigDecimal("0.0095"));
    tmpMap.put(new Integer(801), new BigDecimal("0.0090"));
    tmpMap.put(new Integer(1001), new BigDecimal("0.0085"));
    tmpMap.put(new Integer(1501), new BigDecimal("0.0085"));

    table.put(new Integer(11), tmpMap);

    tmpMap = new LinkedHashMap();
    tmpMap.put(new Integer(101), new BigDecimal("0.011"));
    tmpMap.put(new Integer(201), new BigDecimal("0.0075"));
    tmpMap.put(new Integer(301), new BigDecimal("0.0075"));
    tmpMap.put(new Integer(401), new BigDecimal("0.0075"));
    tmpMap.put(new Integer(501), new BigDecimal("0.0075"));
    tmpMap.put(new Integer(601), new BigDecimal("0.0075"));
    tmpMap.put(new Integer(801), new BigDecimal("0.0070"));
    tmpMap.put(new Integer(1001), new BigDecimal("0.0065"));
    tmpMap.put(new Integer(1501), new BigDecimal("0.0065"));

    table.put(new Integer(16), tmpMap);

    tmpMap = new LinkedHashMap();
    tmpMap.put(new Integer(201), new BigDecimal("0.006"));
    tmpMap.put(new Integer(301), new BigDecimal("0.005"));
    tmpMap.put(new Integer(401), new BigDecimal("0.005"));
    tmpMap.put(new Integer(501), new BigDecimal("0.005"));
    tmpMap.put(new Integer(601), new BigDecimal("0.005"));
    tmpMap.put(new Integer(801), new BigDecimal("0.005"));
    tmpMap.put(new Integer(1001), new BigDecimal("0.0045"));
    tmpMap.put(new Integer(1501), new BigDecimal("0.0045"));

    table.put(new Integer(21), tmpMap);

    tmpMap = new LinkedHashMap();
    tmpMap.put(new Integer(301), new BigDecimal("0.004"));
    tmpMap.put(new Integer(401), new BigDecimal("0.0035"));
    tmpMap.put(new Integer(501), new BigDecimal("0.0035"));
    tmpMap.put(new Integer(601), new BigDecimal("0.0035"));
    tmpMap.put(new Integer(801), new BigDecimal("0.0035"));
    tmpMap.put(new Integer(1001), new BigDecimal("0.0035"));
    tmpMap.put(new Integer(1501), new BigDecimal("0.0035"));

    table.put(new Integer(31), tmpMap);

    tmpMap = new LinkedHashMap();
    tmpMap.put(new Integer(401), new BigDecimal("0.0030"));
    tmpMap.put(new Integer(501), new BigDecimal("0.0030"));
    tmpMap.put(new Integer(601), new BigDecimal("0.0030"));
    tmpMap.put(new Integer(801), new BigDecimal("0.0030"));
    tmpMap.put(new Integer(1001), new BigDecimal("0.0030"));
    tmpMap.put(new Integer(1501), new BigDecimal("0.0030"));

    table.put(new Integer(41), tmpMap);

    tmpMap = new LinkedHashMap();
    tmpMap.put(new Integer(501), new BigDecimal("0.0025"));
    tmpMap.put(new Integer(601), new BigDecimal("0.0025"));
    tmpMap.put(new Integer(801), new BigDecimal("0.0025"));
    tmpMap.put(new Integer(1001), new BigDecimal("0.0025"));
    tmpMap.put(new Integer(1501), new BigDecimal("0.0025"));

    table.put(new Integer(51), tmpMap);

    tmpMap = new LinkedHashMap();

    tmpMap.put(new Integer(601), new BigDecimal("0.002"));
    tmpMap.put(new Integer(801), new BigDecimal("0.002"));
    tmpMap.put(new Integer(1001), new BigDecimal("0.002"));
    tmpMap.put(new Integer(1501), new BigDecimal("0.002"));

    table.put(new Integer(61), tmpMap);

    tmpMap = new LinkedHashMap();
    tmpMap.put(new Integer(801), new BigDecimal("0.0015"));
    tmpMap.put(new Integer(1001), new BigDecimal("0.001"));
    tmpMap.put(new Integer(1501), new BigDecimal("0.0015"));

    table.put(new Integer(71), tmpMap);

    tmpMap = new LinkedHashMap();
    tmpMap.put(new Integer(1001), new BigDecimal("0.00125"));
    tmpMap.put(new Integer(1501), new BigDecimal("0.00125"));

    table.put(new Integer(91), tmpMap);

    tmpMap = new LinkedHashMap();
    tmpMap.put(new Integer(1501), new BigDecimal("0.001"));

    table.put(new Integer(111), tmpMap);
  }

  public BigDecimal getPercent(int place, int playersCount)
  {
    if (playersCount < 10)
      playersCount = 10;
    else if (playersCount < 21)
      playersCount = 10;
    else if (playersCount < 31)
      playersCount = 21;
    else if (playersCount < 51)
      playersCount = 31;
    else if (playersCount < 101)
      playersCount = 51;
    else if (playersCount < 201)
      playersCount = 101;
    else if (playersCount < 301)
      playersCount = 201;
    else if (playersCount < 401)
      playersCount = 301;
    else if (playersCount < 501)
      playersCount = 401;
    else if (playersCount < 601)
      playersCount = 501;
    else if (playersCount < 801)
      playersCount = 601;
    else if (playersCount < 1001)
      playersCount = 801;
    else if (playersCount < 1501)
      playersCount = 1001;
    else {
      playersCount = 1501;
    }

    int k = place;

    if (k < 11)
      place = k;
    else if (k < 16)
      place = 11;
    else if (k < 21)
      place = 16;
    else if (k < 31)
      place = 21;
    else if (k < 41)
      place = 31;
    else if (k < 51)
      place = 41;
    else if (k < 61)
      place = 51;
    else if (k < 71)
      place = 61;
    else if (k < 91)
      place = 71;
    else if (k < 111)
      place = 91;
    else if (k < 151)
      place = 111;
    else {
      place = 151;
    }

    Integer pl = new Integer(playersCount);

    if (table.containsKey(new Integer(place))) {
      LinkedHashMap entry = (LinkedHashMap)table.get(new Integer(place));
      if (entry.containsKey(pl)) {
        return (BigDecimal)entry.get(pl);
      }
    }

    return new BigDecimal(0);
  }

  public XMLTag toXMLTag(int playersCount)
  {
    XMLTag tag = new XMLTag("PRIZES");

    for (int i = 0; i < 250; i++)
    {
      BigDecimal percent = getPercent(i + 1, playersCount);
      if (percent.floatValue() == 0.0F)
      {
        break;
      }
      XMLTag tagPlace = new XMLTag("PLACE");
      tagPlace.addParam("NUM", i + 1);
      tagPlace.addParam("PERCENT", percent.multiply(new BigDecimal(100)).setScale(2, 5).floatValue());
      tag.addNestedTag(tagPlace);
    }

    return tag;
  }

  public String toXML(int playersCount)
  {
    XMLTag tag = toXMLTag(playersCount);

    String xml = tag.toString();
    tag.invalidate();

    return xml;
  }

  public static void main(String[] args)
  {
    MultiTournamentPayoutTable mt = getInstance();
    System.out.println(mt.toXML(12));
  }
}