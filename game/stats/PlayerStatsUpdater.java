package game.stats;

import game.Player;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import server.Server;
import utils.CommonLogger;

public class PlayerStatsUpdater
  implements Runnable
{
  public static final String DB_PARAM_TABLE = "player_game_stats";
  public static final String DB_PARAM_PLAYER_ID = "player_id";
  public static final String DB_PARAM_SESSION_START = "session_start";
  public static final String DB_PARAM_SESSION_GAMES = "session_games";
  public static final String DB_PARAM_GAMES_WON = "games_won";
  public static final String DB_PARAM_SHOWDOWNS_COUNT = "showdowns_count";
  public static final String DB_PARAM_SHOWDOWNS_WON = "showdowns_won";
  public static final String DB_PARAM_FLOP_SEEN = "flop_seen";
  public static final String DB_PARAM_WIN_IF_FLOP_SEEN = "win_if_flop_seen";
  public static final String DB_PARAM_BETTING_COUNT = "betting_count";
  public static final String DB_PARAM_FOLD = "fold";
  public static final String DB_PARAM_CHECK = "check";
  public static final String DB_PARAM_CALL = "call";
  public static final String DB_PARAM_BET = "bet";
  public static final String DB_PARAM_RAISE = "raise";
  public static final String DB_PARAM_RERAISE = "re_raise";
  public static final String DB_PARAM_FOLDS_COUNT = "folds_count";
  public static final String DB_PARAM_FOLDS_PRE_FLOP = "fold_pre_flop";
  public static final String DB_PARAM_FOLDS_AFTER_FLOP = "fold_after_flop";
  public static final String DB_PARAM_FOLDS_AFTER_TURN = "fold_after_turn";
  public static final String DB_PARAM_FOLDS_AFTER_RIVER = "fold_after_river";
  public static final String DB_PARAM_FOLDS_NO_FOLD = "no_fold";
  public static final String SQL_DELETE = "delete from  " + "player_game_stats" + " where " + "player_id" + " =? ";

  public static final String SQL_INSERT = "insert into " + "player_game_stats" + " set " + "player_id" + " =?, " + "session_start" + " =?, " + "session_games" + " =?, " + "games_won" + " =?, " + "showdowns_count" + " =?, " + "showdowns_won" + " =?, " + "flop_seen" + " =?, " + "win_if_flop_seen" + " =?, " + "betting_count" + " =?, " + "fold" + " =?, " + "`check`" + " =?, " + "call" + " =?, " + "bet" + " =?, " + "raise" + " =?, " + "re_raise" + " =?, " + "folds_count" + " =?, " + "fold_pre_flop" + " =?, " + "fold_after_flop" + " =?, " + "fold_after_turn" + " =?, " + "fold_after_river" + " =?, " + "no_fold" + " =? ";
  private final PlayerStat playerStat;

  public PlayerStatsUpdater(PlayerStat playerStat)
  {
    this.playerStat = playerStat;
  }

  public static void main(String[] s) {
    System.out.println(SQL_INSERT);
  }

  public void run()
  {
    Connection dbConn = Server.getDbConnection();
    try
    {
      dbConn.setAutoCommit(false);
      PreparedStatement pstmt1 = dbConn.prepareStatement(SQL_DELETE);
      pstmt1.setInt(1, playerStat.getPlayer().getID());

      PreparedStatement pstmt = dbConn.prepareStatement(SQL_INSERT);
      synchronized (playerStat) {
        pstmt.setInt(1, playerStat.getPlayer().getID());
        pstmt.setDate(2, new java.sql.Date(playerStat.getSessionStart().getTime()));
        pstmt.setLong(3, playerStat.getSessionGames());
        pstmt.setFloat(4, playerStat.getGamesWon().floatValue());
        pstmt.setLong(5, playerStat.getShowdownsCount());
        pstmt.setFloat(6, playerStat.getShowdownsWon().floatValue());
        pstmt.setFloat(7, playerStat.getFlopSeen().floatValue());
        pstmt.setFloat(8, playerStat.getWinIfFlopSeen().floatValue());
        pstmt.setLong(9, playerStat.getBettingCount());
        pstmt.setFloat(10, playerStat.getFold().floatValue());
        pstmt.setFloat(11, playerStat.getCheck().floatValue());
        pstmt.setFloat(12, playerStat.getCall().floatValue());
        pstmt.setFloat(13, playerStat.getBet().floatValue());
        pstmt.setFloat(14, playerStat.getRaise().floatValue());
        pstmt.setFloat(15, playerStat.getReRaise().floatValue());
        pstmt.setLong(16, playerStat.getFoldsCount());
        pstmt.setFloat(17, playerStat.getFoldPreFlop().floatValue());
        pstmt.setFloat(18, playerStat.getFoldAfterFlop().floatValue());
        pstmt.setFloat(19, playerStat.getFoldAfterTurn().floatValue());
        pstmt.setFloat(20, playerStat.getFoldAfterRiver().floatValue());
        pstmt.setFloat(21, playerStat.getNoFold().floatValue());
      }
      pstmt1.executeUpdate();
      pstmt.execute();

      dbConn.commit();
      pstmt1.close();
      pstmt.close();
    }
    catch (SQLException e) {
      CommonLogger.getLogger().warn("Cannot Update/Save Player Stats: ", e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    finally {
      try {
        dbConn.setAutoCommit(true);
      }
      catch (Exception e) {
        CommonLogger.getLogger().warn("Cannot Set Auto Commit: ", e);
      }
      try {
        dbConn.close();
      } catch (SQLException e) {
        CommonLogger.getLogger().warn("Cannot Close Connection: ", e);
      }
    }
  }
}