package commands;

import game.Player;
import game.stats.PlayerStat;
import game.stats.PlayersStats;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import server.Server;
import utils.CommonLogger;

public class CmdReadPlayersStats extends Command
{
  public boolean execute()
    throws IOException
  {
    PreparedStatement statement = null;
    boolean status = false;
    String query = "SELECT * from  player_game_stats";
    try
    {
      statement = getDbConnection().prepareStatement(query);
      ResultSet result = statement.executeQuery();

      while (result.next())
      {
        int playerId = result.getInt("player_id");
        Player player = Player.getPlayerByID(Server.getPlayersList(), playerId);
        if (player == null) {
          CommonLogger.getLogger().fatal("CmdReadPlayersStats - Player with such id is not found! ID: " + playerId);
          continue;
        }

        PlayerStat playerStat = PlayersStats.getPlayerStat(player, 1);

        playerStat.setSessionStart(result.getDate("session_start"));
        playerStat.loadSessionGames(result.getLong("session_games"));
        playerStat.loadGamesWon(new BigDecimal(result.getDouble("games_won")).setScale(2, 5));
        playerStat.loadShowdownsCount(result.getLong("showdowns_count"));
        playerStat.loadShowdownsWon(new BigDecimal(result.getDouble("showdowns_won")).setScale(2, 5));
        playerStat.loadFlopSeen(new BigDecimal(result.getDouble("flop_seen")).setScale(2, 5));
        playerStat.loadWinIfFlopSeen(new BigDecimal(result.getDouble("win_if_flop_seen")).setScale(2, 5));
        playerStat.setBettingCount(result.getLong("betting_count"));
        playerStat.loadFold(new BigDecimal(result.getDouble("fold")).setScale(2, 5));
        playerStat.loadCheck(new BigDecimal(result.getDouble("check")).setScale(2, 5));
        playerStat.loadCall(new BigDecimal(result.getDouble("call")).setScale(2, 5));
        playerStat.loadBet(new BigDecimal(result.getDouble("bet")).setScale(2, 5));
        playerStat.loadRaise(new BigDecimal(result.getDouble("raise")).setScale(2, 5));
        playerStat.loadReRaise(new BigDecimal(result.getDouble("re_raise")).setScale(2, 5));
        playerStat.loadFoldsCount(result.getLong("folds_count"));
        playerStat.loadFoldPreFlop(new BigDecimal(result.getDouble("fold_pre_flop")).setScale(2, 5));
        playerStat.loadFoldAfterFlop(new BigDecimal(result.getDouble("fold_after_flop")).setScale(2, 5));
        playerStat.loadFoldAfterTurn(new BigDecimal(result.getDouble("fold_after_turn")).setScale(2, 5));
        playerStat.loadFoldAfterRiver(new BigDecimal(result.getDouble("fold_after_river")).setScale(2, 5));
        playerStat.loadNoFold(new BigDecimal(result.getDouble("no_fold")).setScale(2, 5));
      }

      result.close();
      status = true;
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    finally {
      try {
        if (statement != null)
          statement.close();
      }
      catch (Exception e)
      {
        CommonLogger.getLogger().warn(e);
      }
    }

    return status;
  }
}