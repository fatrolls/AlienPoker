package commands;

import game.Player;
import game.playerclub.ClubMember;
import game.playerclub.PlayersClub;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import server.Server;
import utils.CommonLogger;

public class CmdReadPlayersClub extends Command
{
  public boolean execute()
    throws IOException
  {
    PreparedStatement statement = null;
    boolean status = false;
    String query = "SELECT * from  players_club";
    try
    {
      statement = getDbConnection().prepareStatement(query);
      ResultSet result = statement.executeQuery();

      while (result.next())
      {
        int playerId = result.getInt("user_id");
        Player player = Player.getPlayerByID(Server.getPlayersList(), playerId);
        if (player == null) {
          CommonLogger.getLogger().fatal("CmdReadPlayersClub - Player with such id is not found! ID: " + playerId);
          continue;
        }

        ClubMember clubMember = new ClubMember(player, new java.util.Date());
        clubMember.setRating(result.getInt("rating"));
        clubMember.loadPoints(new BigDecimal(result.getDouble("points")));
        clubMember.setRegDate(new java.util.Date(result.getDate("reg_date").getTime()));

        PlayersClub.getInstance().loadMember(clubMember);
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