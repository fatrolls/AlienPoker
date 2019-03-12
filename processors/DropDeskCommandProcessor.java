package processors;

import game.Desk;
import game.Player;
import game.PrivateDesksStorage;
import game.privatedesks.DropPrivateDeskDbCommand;
import java.io.IOException;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;

public class DropDeskCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_DESK_ID = "d";
  private static final String MSG_YOU_ARE_NOT_A_CREATOR = "You are not a creator";
  private static final String MSG_NOT_A_PRIVATE_DESK = "Not a private desk";
  private static final String MSG_CANNOT_DROP_DESK = "Cannot drop desk";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("DROPDESK");

    if (server.getCurrentPlayer() == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    if (params.containsKey("d")) {
      Desk desk = Desk.getDeskByID(Server.getDesksList(), ParamParser.getInt(params, "d"));
      if (desk != null) {
        if (desk.isPrivateDesk()) {
          if (desk.getCreator().getID() == server.getCurrentPlayer().getID()) {
            if (new DropPrivateDeskDbCommand().dropDesk(desk.getID()) > 0) {
              Desk.unRegisterDesk(Server.getDesksList(), desk);
              PrivateDesksStorage.unregisterDesk(desk);
              response.setResultStatus(true);
              return response;
            }
            response.setResultStatus(false, "Cannot drop desk");
            return response;
          }

          response.setResultStatus(false, "You are not a creator");
          return response;
        }

        response.setResultStatus(false, "Not a private desk");
        return response;
      }

      response.setResultStatus(false, "Bad parameters");
      return response;
    }

    response.setResultStatus(false, "Bad parameters");
    return response;
  }
}