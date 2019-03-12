package tournaments.multi.levels;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.HashMap;
import tournaments.GameLevel;
import utils.Log;

public class MultiTournamentLevelChecker
{
  private static final HashMap holdemAndOmahaLimits = new HashMap();
  public static final int MAX_HOLDEM_LIMIT_LEVEL = 8;
  public static final int MAX_HOLDEM_LIMIT_LAST_GAME = 71;
  private static final HashMap sevenStudAndRazzLimits = new HashMap();
  public static final int MAX_SEVENSTUD_LIMIT_LEVEL = 8;
  public static final int MAX_SEVENSTUD_LIMIT_LAST_GAME = 70;
  private static final HashMap holdemAndOmahaPotAndNoLimits = new HashMap();
  public static final int MAX_NO_LIMIT_LEVEL = 9;
  public static final int MAX_NO_LIMIT_LAST_GAME = 80;

  public static GameLevel getLevelByGameAndLimit(int gameType, int limit, int currentGame)
  {
    if ((gameType == 3) || (gameType == 4) || (gameType == 1) || (gameType == 5))
    {
      HashMap map = holdemAndOmahaLimits;
      int lastGame = 71;
      if ((limit == 2) || (limit == 3)) {
        map = holdemAndOmahaPotAndNoLimits;
        lastGame = 80;
      }
    }
    else
    {
      int lastGame;
      if ((gameType == 2) || (gameType == 6) || (gameType == 7))
      {
        HashMap map = sevenStudAndRazzLimits;
        lastGame = 70;
      }
      else {
        Log.out("ERROR: LevelChecker.getLevelByGameAndLimit(" + gameType + ", " + limit + ", " + currentGame + ")");
        return null;
      }
    }
    int lastGame;
    HashMap map;
    GameLevel gl;
    GameLevel gl;
    if (map.containsKey(new Integer(currentGame))) {
      gl = (GameLevel)map.get(new Integer(currentGame));
    } else {
      gl = (GameLevel)map.get(new Integer(lastGame));
      if (gl == null) {
        Log.out("ERROR: LevelChecker.getLevelByGameAndLimit(" + gameType + ", " + limit + ", " + currentGame + ") and gl is null");
      }
    }
    return gl;
  }

  public static void main(String[] str) {
    System.out.println("Limit");
    for (int i = 1; i < 100; i++) {
      System.out.println(getLevelByGameAndLimit(1, 1, i));
    }

    System.out.println("No Limit");
    for (int i = 1; i < 100; i++) {
      System.out.println(getLevelByGameAndLimit(1, 2, i));
    }

    System.out.println("Pot Limit");
    for (int i = 1; i < 100; i++) {
      System.out.println(getLevelByGameAndLimit(1, 3, i));
    }

    System.out.println("Limit");
    for (int i = 1; i < 100; i++) {
      System.out.println(getLevelByGameAndLimit(2, 1, i));
    }

    System.out.println("No Limit");
    for (int i = 1; i < 100; i++) {
      System.out.println(getLevelByGameAndLimit(2, 2, i));
    }

    System.out.println("Pot Limit");
    for (int i = 1; i < 100; i++)
      System.out.println(getLevelByGameAndLimit(2, 3, i));
  }

  static
  {
    GameLevel level = new GameLevel(1, 1, new BigDecimal(10), new BigDecimal(20), new BigDecimal(10), new BigDecimal(20));
    level.setAnte(new BigDecimal(0));
    holdemAndOmahaLimits.put(new Integer(1), level);
    holdemAndOmahaPotAndNoLimits.put(new Integer(1), level);

    level = new GameLevel(2, 1, new BigDecimal(15), new BigDecimal(30), new BigDecimal(15), new BigDecimal(30));
    level.setAnte(new BigDecimal(0));
    holdemAndOmahaLimits.put(new Integer(2), level);
    holdemAndOmahaPotAndNoLimits.put(new Integer(2), level);

    level = new GameLevel(3, 1, new BigDecimal(25), new BigDecimal(50), new BigDecimal(25), new BigDecimal(50));
    level.setAnte(new BigDecimal(0));
    holdemAndOmahaLimits.put(new Integer(3), level);
    holdemAndOmahaPotAndNoLimits.put(new Integer(3), level);

    level = new GameLevel(4, 1, new BigDecimal(50), new BigDecimal(100), new BigDecimal(50), new BigDecimal(100));
    level.setAnte(new BigDecimal(0));
    holdemAndOmahaLimits.put(new Integer(4), level);
    holdemAndOmahaPotAndNoLimits.put(new Integer(4), level);

    level = new GameLevel(5, 1, new BigDecimal(75), new BigDecimal(175), new BigDecimal(75), new BigDecimal(175));
    level.setAnte(new BigDecimal(0));
    holdemAndOmahaLimits.put(new Integer(5), level);
    holdemAndOmahaPotAndNoLimits.put(new Integer(5), level);

    level = new GameLevel(6, 1, new BigDecimal(100), new BigDecimal(200), new BigDecimal(100), new BigDecimal(200));
    level.setAnte(new BigDecimal(0));
    holdemAndOmahaLimits.put(new Integer(6), level);
    holdemAndOmahaPotAndNoLimits.put(new Integer(6), level);

    level = new GameLevel(7, 1, new BigDecimal(100), new BigDecimal(200), new BigDecimal(100), new BigDecimal(200));
    level.setAnte(new BigDecimal(25));
    holdemAndOmahaLimits.put(new Integer(7), level);
    holdemAndOmahaPotAndNoLimits.put(new Integer(7), level);

    level = new GameLevel(8, 1, new BigDecimal(200), new BigDecimal(400), new BigDecimal(200), new BigDecimal(400));
    level.setAnte(new BigDecimal(25));
    holdemAndOmahaLimits.put(new Integer(8), level);
    holdemAndOmahaPotAndNoLimits.put(new Integer(8), level);

    level = new GameLevel(9, 1, new BigDecimal(300), new BigDecimal(600), new BigDecimal(300), new BigDecimal(600));
    level.setAnte(new BigDecimal(50));
    holdemAndOmahaLimits.put(new Integer(9), level);
    holdemAndOmahaPotAndNoLimits.put(new Integer(9), level);

    level = new GameLevel(10, 1, new BigDecimal(400), new BigDecimal(800), new BigDecimal(400), new BigDecimal(800));
    level.setAnte(new BigDecimal(50));
    holdemAndOmahaLimits.put(new Integer(10), level);
    holdemAndOmahaPotAndNoLimits.put(new Integer(10), level);

    level = new GameLevel(11, 1, new BigDecimal(600), new BigDecimal(1200), new BigDecimal(600), new BigDecimal(1200));
    level.setAnte(new BigDecimal(75));
    holdemAndOmahaLimits.put(new Integer(11), level);
    holdemAndOmahaPotAndNoLimits.put(new Integer(11), level);

    level = new GameLevel(12, 1, new BigDecimal(1000), new BigDecimal(2000), new BigDecimal(1000), new BigDecimal(2000));
    level.setAnte(new BigDecimal(100));
    holdemAndOmahaLimits.put(new Integer(12), level);
    holdemAndOmahaPotAndNoLimits.put(new Integer(12), level);

    level = new GameLevel(13, 1, new BigDecimal(1500), new BigDecimal(3000), new BigDecimal(1500), new BigDecimal(3000));
    level.setAnte(new BigDecimal(150));
    holdemAndOmahaLimits.put(new Integer(13), level);
    holdemAndOmahaPotAndNoLimits.put(new Integer(13), level);

    level = new GameLevel(14, 1, new BigDecimal(2000), new BigDecimal(4000), new BigDecimal(2000), new BigDecimal(4000));
    level.setAnte(new BigDecimal(200));
    holdemAndOmahaLimits.put(new Integer(14), level);
    holdemAndOmahaPotAndNoLimits.put(new Integer(14), level);

    level = new GameLevel(15, 1, new BigDecimal(3000), new BigDecimal(6000), new BigDecimal(3000), new BigDecimal(6000));
    level.setAnte(new BigDecimal(300));
    holdemAndOmahaLimits.put(new Integer(15), level);
    holdemAndOmahaPotAndNoLimits.put(new Integer(15), level);

    level = new GameLevel(16, 1, new BigDecimal(4000), new BigDecimal(8000), new BigDecimal(4000), new BigDecimal(8000));
    level.setAnte(new BigDecimal(400));
    holdemAndOmahaLimits.put(new Integer(16), level);
    holdemAndOmahaPotAndNoLimits.put(new Integer(16), level);

    level = new GameLevel(17, 1, new BigDecimal(6000), new BigDecimal(12000), new BigDecimal(6000), new BigDecimal(12000));
    level.setAnte(new BigDecimal(400));
    holdemAndOmahaLimits.put(new Integer(17), level);
    holdemAndOmahaPotAndNoLimits.put(new Integer(17), level);

    level = new GameLevel(18, 1, new BigDecimal(10000), new BigDecimal(20000), new BigDecimal(10000), new BigDecimal(20000));
    level.setAnte(new BigDecimal(1000));
    holdemAndOmahaLimits.put(new Integer(18), level);
    holdemAndOmahaPotAndNoLimits.put(new Integer(18), level);

    level = new GameLevel(19, 1, new BigDecimal(15000), new BigDecimal(30000), new BigDecimal(15000), new BigDecimal(30000));
    level.setAnte(new BigDecimal(1500));
    holdemAndOmahaLimits.put(new Integer(19), level);
    holdemAndOmahaPotAndNoLimits.put(new Integer(19), level);

    level = new GameLevel(20, 1, new BigDecimal(20000), new BigDecimal(40000), new BigDecimal(20000), new BigDecimal(40000));
    level.setAnte(new BigDecimal(1500));
    holdemAndOmahaLimits.put(new Integer(20), level);
    holdemAndOmahaPotAndNoLimits.put(new Integer(20), level);

    for (int i = 11; i <= 20; i++) {
      level = new GameLevel(2, i, new BigDecimal(25), new BigDecimal(50), new BigDecimal(15), new BigDecimal(20));
      holdemAndOmahaLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 21; i <= 30; i++) {
      level = new GameLevel(3, i, new BigDecimal(50), new BigDecimal(100), new BigDecimal(25), new BigDecimal(50));
      holdemAndOmahaLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 31; i <= 40; i++) {
      level = new GameLevel(4, i, new BigDecimal(100), new BigDecimal(200), new BigDecimal(50), new BigDecimal(100));
      holdemAndOmahaLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 41; i <= 50; i++) {
      level = new GameLevel(5, i, new BigDecimal(200), new BigDecimal(400), new BigDecimal(100), new BigDecimal(200));
      holdemAndOmahaLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 51; i <= 60; i++) {
      level = new GameLevel(6, i, new BigDecimal(400), new BigDecimal(800), new BigDecimal(200), new BigDecimal(400));
      holdemAndOmahaLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 51; i <= 60; i++) {
      level = new GameLevel(7, i, new BigDecimal(600), new BigDecimal(1200), new BigDecimal(300), new BigDecimal(600));
      holdemAndOmahaLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 71; i <= 72; i++) {
      level = new GameLevel(8, i, new BigDecimal(1000), new BigDecimal(2000), new BigDecimal(500), new BigDecimal(1000));
      holdemAndOmahaLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 1; i <= 10; i++) {
      level = new GameLevel(1, i, new BigDecimal(10), new BigDecimal(20));
      level.setAnte(new BigDecimal(1));
      level.setBringIn(new BigDecimal(5));
      sevenStudAndRazzLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 11; i <= 20; i++) {
      level = new GameLevel(2, i, new BigDecimal(20), new BigDecimal(40));
      level.setAnte(new BigDecimal(2));
      level.setBringIn(new BigDecimal(10));
      sevenStudAndRazzLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 21; i <= 30; i++) {
      level = new GameLevel(3, i, new BigDecimal(30), new BigDecimal(60));
      level.setAnte(new BigDecimal(3));
      level.setBringIn(new BigDecimal(15));
      sevenStudAndRazzLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 31; i <= 40; i++) {
      level = new GameLevel(4, i, new BigDecimal(50), new BigDecimal(100));
      level.setAnte(new BigDecimal(5));
      level.setBringIn(new BigDecimal(25));
      sevenStudAndRazzLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 41; i <= 50; i++) {
      level = new GameLevel(5, i, new BigDecimal(100), new BigDecimal(200));
      level.setAnte(new BigDecimal(10));
      level.setBringIn(new BigDecimal(50));
      sevenStudAndRazzLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 51; i <= 60; i++) {
      level = new GameLevel(6, i, new BigDecimal(200), new BigDecimal(400));
      level.setAnte(new BigDecimal(20));
      level.setBringIn(new BigDecimal(100));
      sevenStudAndRazzLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 61; i <= 70; i++) {
      level = new GameLevel(7, i, new BigDecimal(400), new BigDecimal(800));
      level.setAnte(new BigDecimal(40));
      level.setBringIn(new BigDecimal(200));
      sevenStudAndRazzLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 71; i <= 80; i++) {
      level = new GameLevel(8, i, new BigDecimal(800), new BigDecimal(1600));
      level.setAnte(new BigDecimal(80));
      level.setBringIn(new BigDecimal(400));
      sevenStudAndRazzLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 1; i <= 10; i++) {
      level = new GameLevel(1, i, new BigDecimal(15), new BigDecimal(30), new BigDecimal(5), new BigDecimal(10));
      holdemAndOmahaPotAndNoLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 11; i <= 20; i++) {
      level = new GameLevel(2, i, new BigDecimal(25), new BigDecimal(50), new BigDecimal(10), new BigDecimal(20));
      holdemAndOmahaPotAndNoLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 21; i <= 30; i++) {
      level = new GameLevel(3, i, new BigDecimal(50), new BigDecimal(100), new BigDecimal(15), new BigDecimal(30));
      holdemAndOmahaPotAndNoLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 31; i <= 40; i++) {
      level = new GameLevel(4, i, new BigDecimal(100), new BigDecimal(200), new BigDecimal(30), new BigDecimal(60));
      holdemAndOmahaPotAndNoLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 41; i <= 50; i++) {
      level = new GameLevel(5, i, new BigDecimal(200), new BigDecimal(400), new BigDecimal(50), new BigDecimal(100));
      holdemAndOmahaPotAndNoLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 51; i <= 60; i++) {
      level = new GameLevel(6, i, new BigDecimal(400), new BigDecimal(800), new BigDecimal(100), new BigDecimal(200));
      holdemAndOmahaPotAndNoLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 51; i <= 60; i++) {
      level = new GameLevel(7, i, new BigDecimal(600), new BigDecimal(1200), new BigDecimal(150), new BigDecimal(300));
      holdemAndOmahaPotAndNoLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 71; i <= 80; i++) {
      level = new GameLevel(8, i, new BigDecimal(1000), new BigDecimal(2000), new BigDecimal(250), new BigDecimal(500));
      holdemAndOmahaPotAndNoLimits.put(new Integer(level.getGames()), level);
    }

    for (int i = 81; i <= 90; i++) {
      level = new GameLevel(8, i, new BigDecimal(1500), new BigDecimal(3000), new BigDecimal(500), new BigDecimal(1000));
      holdemAndOmahaPotAndNoLimits.put(new Integer(level.getGames()), level);
    }
  }
}