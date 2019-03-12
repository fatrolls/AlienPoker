package processors;

import game.Desk;
import game.Player;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import server.Response;
import server.Server;
import tournaments.Tournament;
import utils.xml.XMLTag;

public class CheckTournamentSeatsCommandProcessor
  implements RequestCommandProcessor
{
  private static final String TAG_TOURNAMENTS = "TOURNAMENTS";
  private static final String TAG_TOURNAMENT = "TOURNAMENT";
  private static final String OUT_PARAM_TOURNAMENT_ID = "ID";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("CHECKTOURNAMENTSEATS");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    List list = Tournament.getTournamentsList();
    ArrayList tournList;
    synchronized (list) {
      tournList = new ArrayList(list.size());
      tournList.addAll(list);
    }

    XMLTag tag = new XMLTag("TOURNAMENTS");

    int size = tournList.size();
    for (int i = 0; i < size; i++) {
      XMLTag tagTournament = new XMLTag("TOURNAMENT");

      Tournament t = (Tournament)tournList.get(i);

      List desksList = t.getDesksList();
      String xml = "";
      if (t.isBegin()) {
        synchronized (desksList) {
          int dsize = desksList.size();
          for (int j = 0; j < dsize; j++) {
            Desk d = (Desk)desksList.get(j);
            if (d.getPlayerPlace(currentPlayer) != null) {
              xml = d.toXML();
            }
          }
        }
      }
      if (xml.length() > 0) {
        tagTournament.setTagContent(xml);
        tagTournament.addParam("ID", t.getID());
        tagTournament.addParam("TTYPE", t.getTournamentType());

        tag.addNestedTag(tagTournament);
      }
    }

    response.setResultStatus(true);

    String xml = tag.toString();
    tag.invalidate();

    response.setParametersXML(xml);

    return response;
  }
}