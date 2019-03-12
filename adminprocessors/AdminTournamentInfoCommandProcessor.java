package adminprocessors;

import game.Desk;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import org.apache.log4j.Logger;
import processors.DeskCommandProcessor;
import tournaments.Tournament;
import utils.CommonLogger;

public class AdminTournamentInfoCommandProcessor
  implements AdminXMLResponse
{
  public static final String PARAM_TOUR_ID = "t";
  public static final String PARAM_TOUR_TYPE_ID = "p";
  public static final String DEFAULT_ENCODING = "ISO-8859-1";
  private static final String MSG_TOURNAMENT_NOT_FOUND = "Tournament not found";
  private static final String MSG_DESKS_NOT_FOUND = "No tournaments desks was founded";
  private static final String MSG_WRONG_TOURNAMENT_TYPE = "Wrong tournament type";

  public Hashtable getInfo(int tourId, int tourType)
    throws UnsupportedEncodingException
  {
    Hashtable response = new Hashtable();

    Tournament tournament = Tournament.getTournamentByID(tourId);
    if (tournament == null)
    {
      response.put("STATUS", "ERROR");
      response.put("RESPONSE", "Tournament not found");

      return response;
    }

    StringBuffer xml = new StringBuffer();

    switch (tournament.getTournamentType()) {
    case 2:
      ArrayList list = tournament.getDesksList();
      Desk desk = null;
      synchronized (list) {
        if (list.size() > 0) {
          desk = (Desk)list.get(0);
        }
      }

      if (desk == null) {
        CommonLogger.getLogger().warn("AdminTournamentInfoCommandProcessor: desk is null");
        response.put("STATUS", "ERROR");
        response.put("RESPONSE", "No tournaments desks was founded");
        return response;
      }

      DeskCommandProcessor deskCommandProcessor = new DeskCommandProcessor();
      deskCommandProcessor.setDesk(desk);

      xml.append(deskCommandProcessor.getDeskPlayersXML());
      xml.append(deskCommandProcessor.getTournamentLevelXML());

      response.put("STATUS", "OK");
      response.put("RESPONSE", xml.toString());

      break;
    case 1:
      xml.append(tournament.getCashedDesksXML());
      xml.append(tournament.getCashedPlayersXML());
      xml.append(tournament.getCashedPrizeTableXML());
      xml.append(tournament.toXML());

      response.put("STATUS", "OK");
      response.put("RESPONSE", xml.toString());

      break;
    case 5:
      xml.append(tournament.getCashedDesksXML());
      xml.append(tournament.getCashedPlayersXML());
      xml.append(tournament.getCashedPrizeTableXML());
      xml.append(tournament.toXML());

      response.put("STATUS", "OK");
      response.put("RESPONSE", xml.toString());

      break;
    case 3:
    case 4:
    default:
      response.put("STATUS", "ERROR");
      response.put("RESPONSE", "Wrong tournament type");
    }

    return response;
  }
}