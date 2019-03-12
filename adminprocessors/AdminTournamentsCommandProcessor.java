package adminprocessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import tournaments.FeeList;
import tournaments.Tournament;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class AdminTournamentsCommandProcessor
  implements AdminXMLResponse
{
  private static final String OUT_PARAM_TOURNAMENTS = "TOURNAMENTS";
  private static final String OUT_PARAM_ACTIVE_DESKS_COUNT = "ADESKS";

  public Hashtable getInfo()
  {
    Hashtable response = new Hashtable();

    List list = Tournament.getTournamentsList();
    ArrayList tournamentsList;
    synchronized (list) {
      tournamentsList = new ArrayList(list.size());
      tournamentsList.addAll(list);
    }

    XMLDoc xmlDoc = new XMLDoc();
    XMLTag tag = xmlDoc.startTag("TOURNAMENTS");

    Iterator iter = tournamentsList.iterator();
    while (iter.hasNext()) {
      Tournament tournament = (Tournament)iter.next();
      XMLTag tourTag = new XMLTag("TOURNAMENT");
      tourTag.addParam("ID", tournament.getID());
      tourTag.addParam("STATUS", tournament.getStatus());
      tourTag.addParam("STATUSD", tournament.convertTournamentStatusToString(tournament.getStatus()));
      tourTag.addParam("RGS", tournament.getRegStatus());
      tourTag.addParam("CURRENTLVL", tournament.getCurrentLevel());
      tourTag.addParam("ANTE", tournament.getCurrentAnte().toString());
      tourTag.addParam("MAXBET", tournament.getCurrentMaxBet().toString());
      tourTag.addParam("MINBET", tournament.getCurrentMinBet().toString());
      tourTag.addParam("BIA", tournament.getFeeList().getFeeAmount().floatValue());
      tourTag.addParam("SPEED", tournament.getSpeedType());
      synchronized (tournament.getDesksList()) {
        tourTag.addParam("ADESKS", tournament.getDesksList().size());
      }
      synchronized (tournament.getPlayersList()) {
        tourTag.addParam("PLAYERS", tournament.getPlayersList().size());
      }

      tag.addNestedTag(tourTag);
    }

    String xml = xmlDoc.toString();
    xmlDoc.invalidate();

    response.put("STATUS", "OK");
    response.put("RESPONSE", xml);

    return response;
  }
}