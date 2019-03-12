package processors;

import game.Player;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import org.apache.log4j.Logger;
import server.Response;
import server.Server;
import utils.CommonLogger;

public class UpdateEmailCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_EMAIL = "e";
  public static final String DEFAULT_ENCODING = "ISO-8859-1";
  public static final String MSG_PROFILE_UPDATE_COMPLETE = "Your Email Was Updated";
  public static final String MSG_NOT_UNIQUE_EMAIL = "Please select another email";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("UPDATEEMAIL");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    if (params.containsKey("e"))
    {
      String email = URLDecoder.decode((String)params.get("e"), "ISO-8859-1").trim();

      if ((email.length() == 0) || (!email.matches("^[a-zA-Z0-9\\-_\\.]+@[a-zA-Z0-9\\-_\\.]+\\.[a-zA-Z]{2,4}$"))) {
        response.setResultStatus(false, "Bad parameters");
      }
      else {
        int result = checkProfileForUnique(email, currentPlayer.getID());
        if (result == 0) {
          updateEmail(currentPlayer, email);
          response.setResultStatus(true, "Your Email Was Updated");
        }
        else if (result == 1) {
          response.setResultStatus(false, "Please select another email");
        } else {
          CommonLogger.getLogger().warn("Class UpdateProfileCommandProcessor. Error: checkProfileForUnique returns unknown code");
        }
      }
    }
    else
    {
      response.setResultStatus(false, "Bad parameters");
    }

    return response;
  }

  public void updateEmail(Player currentPlayer, String email)
    throws IOException
  {
    Connection dbConn = Server.getDbConnection();
    try {
      PreparedStatement pstmt = dbConn.prepareStatement("update users set us_email=? where user_id=?");

      pstmt.setString(1, email);
      pstmt.setInt(2, currentPlayer.getID());

      pstmt.executeUpdate();
      currentPlayer.setEmail(email);
      pstmt.close();
      dbConn.close();
    }
    catch (Exception e) {
      CommonLogger.getLogger().warn("Class UpdateProfileCommandProcessor. Error: ", e);
      try {
        dbConn.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }

      throw new IOException(e.getMessage());
    }
  }

  public int checkProfileForUnique(String email, int playerId)
  {
    int result = -1;
    Connection dbConn = Server.getDbConnection();
    try {
      PreparedStatement pstmt = dbConn.prepareStatement("select us_email, us_login from users where  us_email=? and user_id<>?");
      pstmt.setString(1, email);
      pstmt.setInt(2, playerId);

      ResultSet rs = pstmt.executeQuery();
      if ((rs.next()) && 
        (email.equals(rs.getString(1)))) {
        result = 1;
      }

      rs.close();
      pstmt.close();
      dbConn.close();
      if (result == -1)
        result = 0;
    }
    catch (Exception e) {
      CommonLogger.getLogger().warn("Class UpdateEmailCommandProcessor. Error: ", e);
      try {
        dbConn.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      e.printStackTrace();
    }
    return result;
  }
}