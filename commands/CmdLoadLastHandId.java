package commands;

import game.Game;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import utils.CommonLogger;

public class CmdLoadLastHandId extends Command
{
  public boolean execute()
    throws IOException
  {
    PreparedStatement statement = null;
    boolean status = false;
    String query = "select last from hand";
    try
    {
      statement = getDbConnection().prepareStatement(query);
      ResultSet result = statement.executeQuery();

      if (result.next()) {
        Game.setLastGameId(result.getLong("last"));
      }

      if (Game.getLastGameId() <= 0L) {
        CommonLogger.getLogger().warn("Last Hand ID = " + Game.getLastGameId());
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
        statement.close();
      }
      catch (Exception e)
      {
      }
    }
    return status;
  }
}