package processors;

import game.Player;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import server.Response;
import server.Server;
import utils.Log;

public class SaveAvatarCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_AVATAR = "i";
  public static final String DEFAULT_ENCODING = "ISO-8859-1";
  private static final String MSG_SERVER_ERROR = "Cannot save avatar";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("SAVEAVATAR");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    if (params.containsKey("i")) {
      String avatar = URLDecoder.decode((String)params.get("i"), "ISO-8859-1").trim();
      try
      {
        saveAvatar(currentPlayer, avatar);
      } catch (Exception ex) {
        response.setResultStatus(false, "Cannot save avatar");
        return response;
      }

      response.setResultStatus(true);
    }
    else {
      response.setResultStatus(false, "Bad parameters");
    }

    return response;
  }

  public void saveAvatar(Player currentPlayer, String avatar)
    throws IOException
  {
    Connection dbConn = Server.getDbConnection();
    try {
      PreparedStatement pstmt = dbConn.prepareStatement("update users set us_avatar=? where user_id=?");

      pstmt.setString(1, avatar);
      pstmt.setInt(2, currentPlayer.getID());

      pstmt.executeUpdate();
      pstmt.close();
      dbConn.close();
      currentPlayer.setAvatar(avatar);
    }
    catch (Exception e) {
      Log.out("Class SaveAvatarCommandProcessor. Error: " + e.getMessage());
      try {
        dbConn.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      throw new IOException(e.getMessage());
    }
  }
}