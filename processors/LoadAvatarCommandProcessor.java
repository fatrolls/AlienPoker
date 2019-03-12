package processors;

import game.Player;
import java.io.IOException;
import java.util.HashMap;
import server.Response;
import server.Server;
import utils.xml.XMLTag;

public class LoadAvatarCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_USER = "u";
  public static final String DEFAULT_ENCODING = "ISO-8859-1";
  private static final String TAG_AVATAR = "AVATAR";
  private static final String TAG_PARAM_USER = "USER_ID";
  private static final String TAG_PARAM_INFO = "INFO";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("LOADAVATAR");

    if (params.containsKey("u"))
    {
      Player currentPlayer = server.getCurrentPlayer();
      if (currentPlayer == null) {
        response.setResultStatus(false, "Authorization first");
        return response;
      }

      try
      {
        int id = Integer.parseInt((String)params.get("u"));
        Player p = Player.getPlayerByID(server.getPlayers(), id);
        if (p == null) {
          response.setResultStatus(false, "Bad parameters");
          return response;
        }

        XMLTag tag = new XMLTag("AVATAR");
        tag.addParam("USER_ID", p.getID());
        tag.addParam("INFO", p.getAvatar());

        response.setResultStatus(true);
        response.setParametersXML(tag.toString());
        tag.invalidate();
      }
      catch (Exception ex) {
        response.setResultStatus(false, "Bad parameters");
        return response;
      }
    }
    else {
      response.setResultStatus(false, "Bad parameters");
    }

    return response;
  }
}