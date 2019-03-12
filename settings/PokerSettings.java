package settings;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.log4j.Logger;
import server.AdminServer;
import server.DbConnectionPool;
import server.Server;

public class PokerSettings
{
  static Logger log = Logger.getLogger(PokerSettings.class);
  public static final String BUNDLE_NAME = "server";
  private static String config;
  static ResourceBundle bundle = null;
  public static BigDecimal DEFAULT_RAKE_PERCENT = new BigDecimal(0.03D);
  public static final String EMAIL_FOR_SYSTEM_MESSAGES = "email_for_system_messages";
  public static final String EMAIL_FOR_CONTACTS = "email_for_contacts";

  public static String getString(String key)
  {
    try
    {
      return bundle.getString(key);
    }
    catch (MissingResourceException e) {
      try {
        return bundle.getString(config + "." + key);
      } catch (MissingResourceException ee) {
        log.error("", ee);
      }
    }return null;
  }

  public static BigDecimal getDefaultRakePercent()
  {
    return DEFAULT_RAKE_PERCENT;
  }

  public static void loadSettings() {
    Server.setServerPort(Integer.parseInt(getString("server.port")));
    AdminServer.setServerPort(Integer.parseInt(getString("server.adminport")));
  }

  public static int getPrivateDesksPerPlayer() {
    return Integer.parseInt(getString("default.rake"));
  }

  public static String getValueByName(String settingName) {
    Connection con = null;
    try {
      con = DbConnectionPool.getDbConnection();
      ResultSet rs = con.createStatement().executeQuery("select setting_value from settings where setting_name='" + settingName + "'");
      if (rs.next()) {
        str = rs.getString(1);
        return str;
      }
      throw new NullPointerException("SETTING_NAME=" + settingName + " does not exist");
    }
    catch (Exception e)
    {
      log.error("", e);
      String str = null;
      return str;
    }
    finally
    {
      DbConnectionPool.closeConnection(con);
    }throw localObject;
  }

  static
  {
    bundle = ResourceBundle.getBundle("server");
    config = bundle.getString("config");
  }
}