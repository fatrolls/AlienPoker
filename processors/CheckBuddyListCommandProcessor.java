package processors;

import game.Player;
import game.buddylist.BuddyList;
import java.io.IOException;
import java.util.HashMap;
import server.Response;
import server.Server;

public class CheckBuddyListCommandProcessor
  implements RequestCommandProcessor
{
  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("CHECKBUDDYLIST");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    response.setResultStatus(true);
    response.setParametersXML(currentPlayer.getByddyList().invitationsToXML());
    return response;
  }
}