package processors;

import game.Player;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import server.Response;
import server.Server;
import utils.Log;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class GetProfileCommandProcessor
  implements RequestCommandProcessor
{
  public static final String DEFAULT_ENCODING = "ISO-8859-1";
  public static final String OUT_PARAM_FNAME = "FIRSTNAME";
  public static final String OUT_PARAM_LNAME = "LASTNAME";
  public static final String OUT_PARAM_EMAIL = "EMAIL";
  public static final String OUT_PARAM_COUNTRY = "COUNTRY";
  public static final String OUT_PARAM_CITY = "CITY";
  public static final String OUT_PARAM_ADDRESS = "ADDRESS";
  public static final String OUT_PARAM_PHONE = "PHONE";
  public static final String OUT_PARAM_LOGIN = "LOGIN";
  public static final String OUT_PARAM_PASSWORD = "PASSWORD";
  private static final String OUT_PARAM_PROFILE = "PROFILE";
  private static final String MSG_PROFILE_NOT_FOUND = "Profile wan't founded";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("GETPROFILE");
    Log.out("begining Profile Update...");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    response.setResultStatus(true);

    boolean found = false;
    String fname = null;
    String lname = null;
    String email = null;
    String country = null;
    String city = null;
    String address = null;
    String phone = null;
    String login = null;

    Connection dbConn = Server.getDbConnection();
    try {
      PreparedStatement pstmt = dbConn.prepareStatement("select  us_fname, us_lname, us_email, us_country, us_city, us_address, us_phone, us_login from users where user_id=?");

      pstmt.setInt(1, currentPlayer.getID());
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        found = true;
        fname = rs.getString(1);
        lname = rs.getString(2);
        email = rs.getString(3);
        country = rs.getString(4);
        city = rs.getString(5);
        address = rs.getString(6);
        phone = rs.getString(7);
        login = rs.getString(8);
      }

      rs.close();
      pstmt.close();
      dbConn.close();
    }
    catch (Exception e) {
      Log.out("Class GetProfileCommandProcessor. Error: " + e.getMessage());
      try {
        dbConn.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }

    }

    if (found) {
      XMLDoc doc = new XMLDoc();
      XMLTag tag = doc.startTag("PROFILE");
      tag.addParam("FIRSTNAME", fname == null ? "" : fname);
      tag.addParam("LASTNAME", lname == null ? "" : lname);
      tag.addParam("EMAIL", email == null ? "" : email);
      tag.addParam("COUNTRY", country == null ? "" : country);
      tag.addParam("CITY", city == null ? "" : city);
      tag.addParam("ADDRESS", address == null ? "" : address);
      tag.addParam("PHONE", phone == null ? "" : phone);
      tag.addParam("LOGIN", login == null ? "" : login);

      String xml = doc.toString();
      doc.invalidate();

      response.setParametersXML(xml);
    } else {
      response.setResultStatus(false, "Profile wan't founded");
    }

    return response;
  }
}