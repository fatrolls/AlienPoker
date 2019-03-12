package adminprocessors;

import game.Desk;
import java.util.ArrayList;
import java.util.Hashtable;
import server.Server;

public class AdminDeleteDeskCommandProcessor
  implements AdminXMLResponse
{
  public static final String MSG_DESK_DELETED = "The desk was deleted";
  public static final String PARAM_DESK_ID = "d";
  public static final String MSG_DESK_IS_NOT_FREE = "The desk is not empty";
  private static final String MSG_ERROR_DESK_NULL = "Cannot find this desk";

  public Hashtable deleteDesk(int deskID)
  {
    Hashtable response = new Hashtable();

    Desk desk = Desk.getDeskByID(Server.getDesksList(), deskID);
    if (desk == null) {
      response.put("STATUS", "ERROR");
      response.put("RESPONSE", "Cannot find this desk");
      return response;
    }

    synchronized (desk) {
      if (desk.getPlayersCount() > 0) {
        response.put("STATUS", "ERROR");
        response.put("RESPONSE", "The desk is not empty");

        return response;
      }

      desk.setDeleted(true);
    }

    synchronized (Server.getDesksList()) {
      Server.getDesksList().remove(desk);
    }

    response.put("STATUS", "OK");
    response.put("RESPONSE", "The desk was deleted");

    return response;
  }
}