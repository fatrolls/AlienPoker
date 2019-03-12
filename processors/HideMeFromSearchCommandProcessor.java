package processors;

import game.Player;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import org.apache.log4j.Logger;
import server.Response;
import server.Server;
import utils.CommonLogger;

public class HideMeFromSearchCommandProcessor
  implements RequestCommandProcessor
{
  private static final String SQL_STRING = "update " + "users" + " set " + "us_hidden" + " =? where " + "user_id" + " = ?";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("HIDEMEFROMSEARCH");

    Player player = server.getCurrentPlayer();
    if (player != null) {
      player.setHideFromSearch(!player.isHideFromSearch());
      hide(player);
      response.setResultStatus(true);
    } else {
      response.setResultStatus(false, "Authorization first");
    }

    return response;
  }

  private void hide(Player player)
  {
    Connection dbConn = Server.getDbConnection();
    PreparedStatement pstmt = null;
    try
    {
      pstmt = dbConn.prepareStatement(SQL_STRING);
      pstmt.setInt(1, player.isHideFromSearch() ? 1 : 0);
      pstmt.setInt(2, player.getID());

      pstmt.executeUpdate();

      pstmt.close();

      dbConn.close();
    } catch (SQLException e) {
      if (pstmt != null) {
        try {
          pstmt.close();
        } catch (SQLException e1) {
          CommonLogger.getLogger().warn(e1.getMessage(), e1);
        }
      }
      try
      {
        dbConn.close();
      } catch (SQLException e1) {
        CommonLogger.getLogger().warn(e1.getMessage(), e1);
      }

      CommonLogger.getLogger().warn("Class StartTournamentHistory->run() : Error: " + e.getMessage(), e);
      throw new RuntimeException(e.getMessage());
    }
  }
}