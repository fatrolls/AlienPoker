package processors;

import game.Player;
import game.buddylist.Buddy;
import game.buddylist.BuddyList;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import server.Response;
import server.Server;
import utils.xml.XMLTag;

public class AddBuddyCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_USER = "u";
  private static final String TAG_NAME_CODE = "CODE";
  private static final String OUT_PARAM_VALUE = "VALUE";
  private static final String VALUE_NOT_FOUND = "0";
  private static final String VALUE_SUCCESS = "1";
  private static final String VALUE_ALREADY_PRESENTS = "2";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("ADDBUDDY");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    if (params.containsKey("u")) {
      String login = URLDecoder.decode((String)params.get("u"), "ISO-8859-1").trim();
      Player buddyplayer = Player.searchPlayerByLogin(Server.getPlayersList(), login, false);

      if (buddyplayer != null) {
        Buddy buddy = new Buddy(buddyplayer);
        if (currentPlayer.getByddyList().addBuddy(buddy))
        {
          XMLTag tag = new XMLTag("CODE");
          tag.addParam("VALUE", "1");
          response.setResultStatus(true);
          response.setParametersXML(tag.toString());
          tag.invalidate();
          return response;
        }

        XMLTag tag = new XMLTag("CODE");
        tag.addParam("VALUE", "2");
        response.setResultStatus(true);
        response.setParametersXML(tag.toString());
        tag.invalidate();
        return response;
      }

      XMLTag tag = new XMLTag("CODE");
      tag.addParam("VALUE", "0");
      response.setResultStatus(true);
      response.setParametersXML(tag.toString());
      tag.invalidate();
      return response;
    }

    response.setResultStatus(false, "Bad parameters");
    return response;
  }
}