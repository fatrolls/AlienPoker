package adminprocessors;

import java.util.Hashtable;
import tournaments.Tournament;

public class AdminDeleteTournamentCommandProcessor
  implements AdminXMLResponse
{
  public static final String PARAM_TOURNMENT_ID = "t";
  public static final String DEFAULT_ENCODING = "ISO-8859-1";
  private static final String MSG_TOURNAMENT_WAS_DELETED = "Tournament was deleted";
  private static final String MSG_CANNOT_DELETE_TOURNAMENT = "Can not delete tournament. It is already started";
  private static final String MSG_TOURNAMENT_NOT_FOUND = "Tournament is not found";

  public Hashtable delete(int tournamentId)
  {
    Hashtable response = new Hashtable();
    Tournament tournament = Tournament.getTournamentByID(tournamentId);
    if (tournament != null) {
      int code = tournament.terminateUpcomingTournament();
      if (code == 1) {
        response.put("STATUS", "OK");
        response.put("RESPONSE", "Tournament was deleted");
        return response;
      }
      response.put("STATUS", "ERROR");
      response.put("RESPONSE", "Can not delete tournament. It is already started");
      return response;
    }

    response.put("STATUS", "ERROR");
    response.put("RESPONSE", "Tournament is not found");
    return response;
  }
}