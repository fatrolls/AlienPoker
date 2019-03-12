package tournaments;

import java.util.Date;
import java.util.HashMap;
import tournaments.sitandgo.levels.LevelChecker;

public class TournamentLevels
{
  private final HashMap desksMap = new HashMap();
  private final HashMap timeMap = new HashMap();
  private final HashMap gamesMap = new HashMap();
  private Tournament tournament;

  public TournamentLevels(Tournament tournament)
  {
    this.tournament = tournament;
  }

  public GameLevel getDeskLevel(int deskId)
  {
    Integer id = new Integer(deskId);
    GameLevel level;
    synchronized (desksMap)
    {
      GameLevel level;
      if (desksMap.containsKey(id)) {
        level = (GameLevel)desksMap.get(id);
      } else {
        level = LevelChecker.getLevelByGameAndLimit(tournament.getGame(), tournament.getGameType(), 1);
        desksMap.put(id, level);
      }
    }
    return level;
  }

  public GameLevel getNextGameLevel(int currentLevel)
  {
    return LevelChecker.getLevelByGameAndLimit(tournament.getGame(), tournament.getGameType(), currentLevel > 0 ? currentLevel * 10 : 1);
  }

  public GameLevel updateDeskLevel(int deskId, int gamesCount)
  {
    Integer id = new Integer(deskId);
    GameLevel level;
    synchronized (desksMap) {
      level = LevelChecker.getLevelByGameAndLimit(tournament.getGame(), tournament.getGameType(), gamesCount);
      desksMap.put(id, level);
    }
    return level;
  }

  public long getLastLevelTime(int deskId)
  {
    Integer id = new Integer(deskId);
    Long time = new Long(new Date().getTime());
    synchronized (timeMap) {
      if (timeMap.containsKey(id))
        time = (Long)timeMap.get(id);
      else {
        timeMap.put(id, time);
      }
    }
    return time.longValue();
  }

  public void updateLastLevelTime(int deskId, long lastTime)
  {
    Integer id = new Integer(deskId);
    Long time = new Long(lastTime);
    synchronized (timeMap) {
      timeMap.put(id, time);
    }
  }

  public int getGamesCount(int deskId)
  {
    Integer id = new Integer(deskId);
    Integer count = new Integer(1);
    synchronized (gamesMap) {
      if (gamesMap.containsKey(id))
        count = (Integer)gamesMap.get(id);
      else {
        gamesMap.put(id, count);
      }
    }
    return count.intValue();
  }

  public int increaseGamesCount(int deskId)
  {
    Integer id = new Integer(deskId);
    Integer count = new Integer(1);
    synchronized (gamesMap) {
      if (gamesMap.containsKey(id)) {
        count = (Integer)gamesMap.get(id);
      }
      count = new Integer(count.intValue() + 1);
      gamesMap.put(id, count);
    }
    return count.intValue();
  }
}