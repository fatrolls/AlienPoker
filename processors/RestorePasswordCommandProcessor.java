package processors;

import emails.RestorePasswordMailSender;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import server.Response;
import server.Server;
import utils.Log;

public class RestorePasswordCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_EMAIL = "e";
  public static final String DEFAULT_ENCODING = "ISO-8859-1";
  public static final String MSG_NOT_REGISTERED_EMAIL = "User with such a email is not registered in the system";
  public static final String MSG_RESTORE_COMPLETE = "Password has been send to your email";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("RESTOREPASSWORD");

    if (params.containsKey("e")) {
      String email = URLDecoder.decode((String)params.get("e"), "ISO-8859-1").trim();

      if ((email.length() == 0) || (!email.matches("^[a-zA-Z0-9\\-_\\.]+@[a-zA-Z0-9\\-_\\.]+\\.[a-zA-Z]{2,4}$"))) {
        response.setResultStatus(false, "Bad parameters");
      }
      else if (checkEmail(email)) {
        try {
          emailPassword(email);
        } catch (Exception e) {
          e.printStackTrace();
        }
        response.setResultStatus(true, "Password has been send to your email");
      } else {
        response.setResultStatus(false, "User with such a email is not registered in the system");
      }
    }
    else {
      response.setResultStatus(false, "Bad parameters");
    }

    return response;
  }

  public boolean checkEmail(String email)
  {
    boolean result = false;
    Connection dbConn = Server.getDbConnection();
    try {
      PreparedStatement pstmt = dbConn.prepareStatement("select count(*) from users where us_email=? ");
      pstmt.setString(1, email);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        result = rs.getInt(1) > 0;
      }
      rs.close();
      pstmt.close();
      dbConn.close();
    }
    catch (Exception e) {
      Log.out("Class RestorePasswordCommandProcessor. Error: " + e.getMessage());
      try {
        dbConn.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      e.printStackTrace(System.out);
    }
    return result;
  }

  public void emailPassword(String email)
    throws Exception
  {
    RestorePasswordMailSender r = new RestorePasswordMailSender();
    r.sendEmail(email);
  }
}