package processors;

import game.Desk;
import game.Player;
import java.io.IOException;
import java.util.HashMap;
import org.apache.log4j.Logger;
import server.ParamParser;
import server.Response;
import server.Server;
import tournaments.Tournament;
import tournaments.multi.ComplexSitAndGo;

public class JoinTournamentCommandProcessor
  implements RequestCommandProcessor
{
  static Logger log = Logger.getLogger("processors/JoinTournamentCommandProcessor");
  private static final String PARAM_DESK_TOURNAMET_ID = "t";
  public static final String PARAM_PLACE_NUMBER = "p";
  public static final String PARAM_DESK = "d";
  private static final String MSG_NOT_ENOUGH_MONEY = "Not Enough Money";
  private static final String MSG_ALREADY_ADDED = "Already Joined";
  private static final String MSG_TOURNAMENT_ALREADY_STARTED = "Tournament already started";
  private static final String MSG_TOURNAMENT_BAD_DESK = "There are no tournament desks yet";
  private static final String MSG_PLACE_UNAVAILABLE = "The place is unavailable";
  private static final String MSG_REGISTRATION_CLOSED = "The registration is temporarily closed";
  private static final String MSG_NOT_ENOUGH_DIRTY_POINTS = "Not enough dirty points";

  public Response process(HashMap paramHashMap, Server paramServer)
    throws IOException
  {
    Response localResponse = new Response("JOINTOURNAMENT");
    Player localPlayer = paramServer.getCurrentPlayer();
    if (localPlayer == null)
    {
      localResponse.setResultStatus(false, "Authorization first");
      return localResponse;
    }
    if (paramHashMap.containsKey("t"))
    {
      int i = ParamParser.getInt(paramHashMap, "t");
      Tournament localTournament = Server.getTournamentByID(i);
      if (localTournament == null)
      {
        log.error("Tournament with ID=" + i + " not found");
        localResponse.setResultStatus(false, "Bad parameters");
        return localResponse;
      }
      int j;
      if (localTournament.getTournamentType() == 5)
      {
        if (paramHashMap.containsKey("p"))
        {
          if (paramHashMap.containsKey("d"))
          {
            ComplexSitAndGo localComplexSitAndGo = (ComplexSitAndGo)localTournament;
            Desk localDesk = localComplexSitAndGo.getDeskById(ParamParser.getInt(paramHashMap, "d"));
            if (localDesk == null)
            {
              log.error("Desk with PARAM_DESK=" + ParamParser.getInt(paramHashMap, "d") + " not found");
              localResponse.setResultStatus(false, "Bad parameters");
              return localResponse;
            }
            j = localComplexSitAndGo.join(localPlayer, localDesk, ParamParser.getInt(paramHashMap, "p"));
          }
          else {
            log.error("PARAM_DESK not found");
            localResponse.setResultStatus(false, "Bad parameters");
            return localResponse;
          }
        }
        else {
          log.error("PARAM_PLACE_NUMBER not found");
          localResponse.setResultStatus(false, "Bad parameters");
          return localResponse;
        }
      }
      else if (localTournament.getTournamentType() == 1)
      {
        j = localTournament.join(localPlayer);
      }
      else {
        localResponse.setResultStatus(false, "Bad parameters");
        return localResponse;
      }
      if (j == 1)
      {
        localResponse.setResultStatus(true);
        return localResponse;
      }
      if (j == 2)
      {
        localResponse.setResultStatus(false, "Not Enough Money");
        return localResponse;
      }
      if (j == 0)
      {
        if (localTournament.unjoin(localPlayer))
        {
          localResponse.setResultStatus(false, "You have Unregistered from this tournament");
          return localResponse;
        }

      }

      if (j == 3)
      {
        localResponse.setResultStatus(false, "Tournament already started");
        return localResponse;
      }
      if (j == 4)
      {
        localResponse.setResultStatus(false, "There are no tournament desks yet");
        return localResponse;
      }
      if (j == 5)
      {
        localResponse.setResultStatus(false, "The place is unavailable");
        return localResponse;
      }
      if (j == 6)
      {
        localResponse.setResultStatus(false, "The registration is temporarily closed");
        return localResponse;
      }
      if (j == 7)
      {
        localResponse.setResultStatus(false, "Not enough dirty points");
        return localResponse;
      }

      localResponse.setResultStatus(false, "Bad parameters");
      return localResponse;
    }

    localResponse.setResultStatus(false, "Bad parameters");
    return localResponse;
  }
}