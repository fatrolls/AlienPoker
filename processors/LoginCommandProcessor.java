package processors;

import game.Player;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import org.apache.log4j.Logger;
import server.DbConnectionPool;
import server.OnLinePlayers;
import server.Response;
import server.Server;
import utils.xml.XMLTag;

public class LoginCommandProcessor
  implements RequestCommandProcessor
{
  static Logger log = Logger.getLogger(LoginCommandProcessor.class);
  public static final String PARAM_USER = "u";
  public static final String PARAM_PASSWORD = "p";
  private static final String TAG_NAME_CODE = "CODE";
  private static final String OUT_PARAM_VALUE = "VALUE";
  private static final int VALUE_LOGGED_IN = 1;
  private static final int VALUE_BLOCKED = 2;

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("LOGIN");

    if ((params.containsKey("u")) && (params.containsKey("p"))) {
      server.invalidateCurrentPlayer();

      String login = (String)params.get("u");
      String password = (String)params.get("p");

      login = URLDecoder.decode(login, "ISO-8859-1");
      password = URLDecoder.decode(password, "ISO-8859-1");

      Player player = Player.getPlayerByLoginAndPass(server.getPlayers(), login, password);
      if (player != null) {
        if (player.isActive()) {
          server.setCurrentPlayer(player);
          Server.getOnlinePlayers().registerPlayer(player);
          response.setResultStatus(true);

          XMLTag tag = new XMLTag("CODE");
          tag.addParam("VALUE", 1);
          response.setParametersXML(tag.toString());
          tag.invalidate();
          Connection con = null;
          try {
            con = DbConnectionPool.getDbConnection();
            con.createStatement().execute("update users set us_last_client_login=SYSDATE() where user_id=" + player.getID());
            Object inetAddress = params.get("INETADDRESS");
            if ((inetAddress != null) && ((inetAddress instanceof InetAddress))) {
              String host = ((InetAddress)inetAddress).getHostAddress();
              con.createStatement().execute("update users set us_last_client_ip='" + host + "' where user_id=" + player.getID());
            }
          }
          catch (Exception e) {
            log.error("", e);
          }
          finally {
            DbConnectionPool.closeConnection(con);
          }
        } else {
          response.setResultStatus(true);

          XMLTag tag = new XMLTag("CODE");
          tag.addParam("VALUE", 2);
          response.setParametersXML(tag.toString());
          tag.invalidate();
        }
      }
      else response.setResultStatus(false, "Invalid login or password"); 
    }
    else
    {
      response.setResultStatus(false, "Bad parameters");
    }

    return response;
  }
}