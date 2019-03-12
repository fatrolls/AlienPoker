package processors;

import game.Desk;
import game.Player;
import java.io.IOException;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;
import utils.Log;
import waitinglist.Waiter;
import waitinglist.WaitingList;
import waitinglist.WaitingRequirements;

public class RemoveFromWaitingListCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_DESK = "d";
  public static final String PARAM_MIN_PLAYERS = "p";
  public static final String DEFAULT_ENCODING = "ISO-8859-1";
  public static final String MSG_PROFILE_PASSWORD_UPDATE_COMPLETE = "Your Password Was Updated";
  public static final String MSG_PROFILE_WRONG_OLD_PASSWORD = "Old password is wrong";
  private static final String MSG_REMOVE_FROM_LIST_SUCCESS = "You was removed from Waiting List";
  private static final String PARAM_DESK_TOURNAMET_ID = "t";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("REMOVEFROMWAITINGLIST");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    if ((params.containsKey("d")) && (params.containsKey("p")))
    {
      int deskId;
      int minPlayers;
      try {
        deskId = Integer.parseInt((String)params.get("d"));
        minPlayers = Integer.parseInt((String)params.get("p"));
      } catch (Exception ex) {
        Log.out("RemoveFromWaitingListCommandProcessor - ERROR: " + ex.getMessage());
        response.setResultStatus(false, "Bad parameters");
        return response;
      }
      Desk desk;
      Desk desk;
      if (params.containsKey("t"))
        desk = Desk.getDeskByID(server.getTournamentDesks(ParamParser.getInt(params, "t")), deskId);
      else {
        desk = Desk.getDeskByID(server.getDesks(), deskId);
      }

      if (desk == null) {
        response.setResultStatus(false, "Bad parameters");
        return response;
      }

      WaitingList waitingList = server.getWaitingList();

      WaitingRequirements waitingRequirements = new WaitingRequirements(desk, minPlayers);
      Waiter waiter = new Waiter(currentPlayer, waitingList);
      waitingList.removeIgnoreMinPlayers(waitingRequirements, waiter);

      response.setResultStatus(true, "You was removed from Waiting List");
    } else if (params.containsKey("d")) {
      int deskId;
      try { deskId = Integer.parseInt((String)params.get("d"));
      } catch (Exception ex) {
        Log.out("RemoveFromWaitingListCommandProcessor - ERROR: " + ex.getMessage());
        response.setResultStatus(false, "Bad parameters");
        return response;
      }
      Desk desk;
      Desk desk;
      if (params.containsKey("t"))
        desk = Desk.getDeskByID(server.getTournamentDesks(ParamParser.getInt(params, "t")), deskId);
      else {
        desk = Desk.getDeskByID(server.getDesks(), deskId);
      }

      if (desk == null) {
        response.setResultStatus(false, "Bad parameters");
        return response;
      }

      WaitingRequirements w = new WaitingRequirements(desk);
      Waiter waiter = new Waiter(currentPlayer, server.getWaitingList());
      server.getWaitingList().remove(w, waiter);

      WaitingRequirements w1 = new WaitingRequirements(desk, 0);

      server.getWaitingList().removeIgnoreMinPlayers(w1, waiter);

      response.setResultStatus(true, "You was removed from Waiting List");
    }
    else {
      response.setResultStatus(false, "Bad parameters");
    }

    return response;
  }
}