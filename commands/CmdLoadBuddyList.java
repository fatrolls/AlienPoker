package commands;

import game.Player;
import game.buddylist.Buddy;
import game.buddylist.BuddyList;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import server.Server;
import utils.CommonLogger;

public class CmdLoadBuddyList extends Command
{
  public boolean execute()
    throws IOException
  {
    String query = "select * from buddies_list order by owner";
    boolean status;
    try
    {
      PreparedStatement statement = getDbConnection().prepareStatement(query);
      ResultSet result = statement.executeQuery();

      ArrayList playersList = Server.getPlayersList();

      while (result.next()) {
        int ownerId = result.getInt("owner");
        int buddyId = result.getInt("buddy");
        int blocked = result.getInt("blocked");

        Player owner = Player.getPlayerByID(playersList, ownerId);
        if (owner == null) {
          CommonLogger.getLogger().warn("CmdLoadBuddy: owner is null " + ownerId);
          continue;
        }
        Player to = Player.getPlayerByID(playersList, buddyId);
        if (to == null) {
          CommonLogger.getLogger().warn("CmdLoadBuddy: buddy is null " + buddyId);
          continue;
        }
        if ((blocked != 0) && (blocked != 1)) {
          CommonLogger.getLogger().warn("CmdLoadBuddy: blocked is " + blocked);
          continue;
        }

        owner.getByddyList().importBuddy(new Buddy(to, blocked == 1));
      }

      result.close();
      statement.close();
      status = true;
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }

    return status;
  }
}