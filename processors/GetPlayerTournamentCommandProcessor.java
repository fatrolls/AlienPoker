package processors;

import game.Desk;
import game.Player;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import server.Response;
import server.Server;
import tournaments.Tournament;
import utils.xml.XMLTag;

public class GetPlayerTournamentCommandProcessor
  implements RequestCommandProcessor
{
  private static final String TAG_NAME_TOURNAMENTS = "TOURNAMENTS";
  private static final String TAG_NAME_PLAYER = "T";
  private static final String TAG_PARAM_TOUR_ID = "ID";
  private static final String TAG_PARAM_DESK_ID = "DESK";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("GETPLAYERTOURNAMENT");

    Player player = server.getCurrentPlayer();
    if (player == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    ArrayList tournamentList = new ArrayList();
    List list = Server.getTournamentsList();
    synchronized (list) {
      Iterator iter = list.iterator();
      while (iter.hasNext()) {
        Tournament t = (Tournament)iter.next();
        if (t.hasPlayer(player)) {
          tournamentList.add(t);
        }
      }

    }

    XMLTag tag = new XMLTag("TOURNAMENTS");
    Iterator iter = tournamentList.iterator();
    while (iter.hasNext()) {
      XMLTag innerTag = new XMLTag("T");
      Tournament t = (Tournament)iter.next();
      innerTag.addParam("ID", t.getID());
      Desk d = t.getPlayerDesk(player);
      innerTag.addParam("DESK", "" + d.getID());
      tag.addNestedTag(innerTag);
    }

    response.setResultStatus(true);
    response.setParametersXML(tag.toString());
    tag.invalidate();

    return response;
  }
}