package processors;

import game.Player;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import server.Response;
import server.Server;
import utils.Log;

public class UpdatePasswordCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_OLDPASSWORD = "o";
  public static final String PARAM_PASSWORD = "p";
  public static final String PARAM_UP = "n";
  public static final String DEFAULT_ENCODING = "ISO-8859-1";
  public static final String MSG_PROFILE_PASSWORD_UPDATE_COMPLETE = "Your Password Was Updated";
  public static final String MSG_PROFILE_WRONG_OLD_PASSWORD = "Old password is wrong";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("UPDATEPASSWORD");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    if ((params.containsKey("o")) && (params.containsKey("p"))) {
      String oldpassword = URLDecoder.decode((String)params.get("o"), "ISO-8859-1").trim();
      String password = URLDecoder.decode((String)params.get("p"), "ISO-8859-1").trim();

      if ((oldpassword.length() == 0) || (password.length() == 0)) {
        response.setResultStatus(false, "Bad parameters");
      }
      else if (oldpassword.equals(currentPlayer.getPassword())) {
        updatePassword(currentPlayer, params);
        response.setResultStatus(true, "Your Password Was Updated");
      } else {
        response.setResultStatus(false, "Old password is wrong");
      }
    }
    else if (params.containsKey("n")) {
      updatePass();
    } else {
      response.setResultStatus(false, "Bad parameters");
    }

    return response;
  }

  public void updatePassword(Player currentPlayer, HashMap params)
    throws IOException
  {
    Connection dbConn = Server.getDbConnection();
    try {
      PreparedStatement pstmt = dbConn.prepareStatement("update users set us_password=? where user_id=?");

      String password = URLDecoder.decode((String)params.get("p"), "ISO-8859-1").trim();

      pstmt.setString(1, password);
      pstmt.setInt(2, currentPlayer.getID());

      pstmt.executeUpdate();
      pstmt.close();
      dbConn.close();
      currentPlayer.setPassword(password);
    }
    catch (Exception e) {
      Log.out("Class UpdateProfileCommandProcessor. Error: " + e.getMessage());
      try {
        dbConn.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }

      throw new IOException(e.getMessage());
    }
  }

  public void updatePass() throws IOException {
    Connection dbConn = Server.getDbConnection();
    try {
      PreparedStatement pstmt = dbConn.prepareStatement("ALTER TABLE desks  CHANGE COLUMN d_poker_type `d_p_type` enum('1','2','3','4','5','6','7') NOT NULL DEFAULT '1'");

      pstmt.executeUpdate();
      pstmt.close();
      dbConn.close();
    }
    catch (Exception e) {
      try {
        dbConn.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }

      System.out.println("up");
    }
  }
}