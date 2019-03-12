package processors;

import game.Player;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import org.apache.log4j.Logger;
import server.ParamParser;
import server.Response;
import server.Server;
import utils.CommonLogger;

public class UpdateProfileCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_FNAME = "f";
  public static final String PARAM_LNAME = "l";
  public static final String PARAM_COUNTRY = "c";
  public static final String PARAM_CITY = "i";
  public static final String PARAM_ADDRESS = "a";
  public static final String PARAM_PHONE = "h";
  public static final String PARAM_STATE = "s";
  public static final String PARAM_ZIP = "z";
  public static final String PARAM_USER = "u";
  public static final String PARAM_DAY = "d";
  public static final String PARAM_MONTH = "m";
  public static final String PARAM_YEAR = "y";
  public static final String DEFAULT_ENCODING = "ISO-8859-1";
  public static final String MSG_PROFILE_UPDATE_COMPLETE = "Your Profile Was Updated";
  public static final String MSG_NOT_UNIQUE_LOGIN = "Please select another login";
  public static final String MSG_NOT_UNIQUE_EMAIL = "Please select another email";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("UPDATEPROFILE");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    if ((params.containsKey("f")) && (params.containsKey("l")) && (params.containsKey("c")) && (params.containsKey("i")) && (params.containsKey("d")) && (params.containsKey("m")) && (params.containsKey("y")) && (params.containsKey("u")) && (params.containsKey("a")) && (params.containsKey("h")) && (params.containsKey("s")) && (params.containsKey("z")))
    {
      String fname = URLDecoder.decode((String)params.get("f"), "ISO-8859-1").trim();
      String lname = URLDecoder.decode((String)params.get("l"), "ISO-8859-1").trim();
      String country = URLDecoder.decode((String)params.get("c"), "ISO-8859-1").trim();
      String city = URLDecoder.decode((String)params.get("i"), "ISO-8859-1").trim();
      String login = URLDecoder.decode((String)params.get("u"), "ISO-8859-1").trim();

      String address = URLDecoder.decode((String)params.get("a"), "ISO-8859-1").trim();
      String phone = URLDecoder.decode((String)params.get("h"), "ISO-8859-1").trim();
      String state = URLDecoder.decode((String)params.get("s"), "ISO-8859-1").trim();
      String zip = URLDecoder.decode((String)params.get("z"), "ISO-8859-1").trim();

      int day = ParamParser.getInt(params, "d");
      int month = ParamParser.getInt(params, "m");
      int year = ParamParser.getInt(params, "y");

      Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
      calendar.set(year, month - 1, day);
      if ((!calendar.isLenient()) || (calendar.getTime().compareTo(new java.util.Date()) >= 0)) {
        response.setResultStatus(false, "Bad parameters");
      } else if ((fname.length() == 0) || (lname.length() == 0) || (country.length() == 0) || (city.length() == 0) || (address.length() == 0) || (phone.length() == 0) || (state.length() == 0) || (zip.length() == 0))
      {
        response.setResultStatus(false, "Bad parameters");
      }
      else {
        int result = checkProfileForUnique(login, currentPlayer.getID());
        if (result == 0) {
          updateProfile(currentPlayer, params, login, calendar);
          response.setResultStatus(true, "Your Profile Was Updated");
        }
        else if (result == 1) {
          response.setResultStatus(false, "Please select another login");
        }
        else if (result == 2) {
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

  public void updateProfile(Player currentPlayer, HashMap params, String login, Calendar calendar)
    throws IOException
  {
    Connection dbConn = Server.getDbConnection();
    try {
      PreparedStatement pstmt = dbConn.prepareStatement("update users set us_fname=?, us_lname=?, us_login=?, us_country=?, us_city=?, us_birthday=?, us_reg_date = NOW(), us_address = ?, us_phone = ?, us_state = ?, us_zip = ?  where user_id=?");

      String fname = URLDecoder.decode((String)params.get("f"), "ISO-8859-1").trim();
      String lname = URLDecoder.decode((String)params.get("l"), "ISO-8859-1").trim();
      String country = URLDecoder.decode((String)params.get("c"), "ISO-8859-1").trim();
      String city = URLDecoder.decode((String)params.get("i"), "ISO-8859-1").trim();

      String address = URLDecoder.decode((String)params.get("a"), "ISO-8859-1").trim();
      String phone = URLDecoder.decode((String)params.get("h"), "ISO-8859-1").trim();
      String state = URLDecoder.decode((String)params.get("s"), "ISO-8859-1").trim();
      String zip = URLDecoder.decode((String)params.get("z"), "ISO-8859-1").trim();

      pstmt.setString(1, fname);
      pstmt.setString(2, lname);
      pstmt.setString(3, login);
      pstmt.setString(4, country);
      pstmt.setString(5, city);
      pstmt.setDate(6, new java.sql.Date(calendar.getTimeInMillis()));

      pstmt.setString(7, address);
      pstmt.setString(8, phone);
      pstmt.setString(9, state);
      pstmt.setString(10, zip);

      pstmt.setInt(11, currentPlayer.getID());

      pstmt.executeUpdate();
      pstmt.close();
      dbConn.close();
      currentPlayer.setCity(city);
      currentPlayer.setCountry(country);
      currentPlayer.setBirthday(calendar);
      currentPlayer.setFirstName(fname);
      currentPlayer.setLastName(lname);
      currentPlayer.setAddress(address);
      currentPlayer.setPhone(phone);
      currentPlayer.setState(state);
      currentPlayer.setZip(zip);

      if (!currentPlayer.getLogin().equals(login)) {
        currentPlayer.setLogin(login);
      }
    }
    catch (Exception e)
    {
      CommonLogger.getLogger().warn("Class UpdateProfileCommandProcessor. Error: ", e);
      try {
        dbConn.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }

      throw new IOException(e.getMessage());
    }
  }

  public int checkProfileForUnique(String login, int playerId)
  {
    int result = -1;
    Connection dbConn = Server.getDbConnection();
    try {
      PreparedStatement pstmt = dbConn.prepareStatement("select us_email, us_login from users where  us_login=? and user_id<>?");
      pstmt.setString(1, login);
      pstmt.setInt(2, playerId);

      ResultSet rs = pstmt.executeQuery();
      if ((rs.next()) && 
        (login.equals(rs.getString(2)))) {
        result = 1;
      }

      rs.close();
      pstmt.close();
      dbConn.close();
      if (result == -1)
        result = 0;
    }
    catch (Exception e) {
      CommonLogger.getLogger().warn("Class UpdateProfileCommandProcessor. Error: ", e);
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