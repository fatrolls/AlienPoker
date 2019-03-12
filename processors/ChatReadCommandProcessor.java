package processors;

import game.Desk;
import game.Place;
import game.Player;
import game.chat.Chat;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;
import utils.xml.XMLTag;

public class ChatReadCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_DESK = "d";
  public static final String PARAM_TIME = "l";
  public static final String DEFAULT_ENCODING = "ISO-8859-1";
  public static final String MSG_MESSAGE_SEND = "Your Message Was Sent";
  public static final String MSG_DESK_NOT_FOUND = "Desk not found";
  public static final String MSG_WRONG_PLAYER_TABLE = "You are not sitting at this table";
  private static final String PARAM_DESK_TOURNAMET_ID = "t";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("CHATREAD");
    int deskId = 0;
    long chatTime = 0L;

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    if ((params.containsKey("d")) && (params.containsKey("l"))) {
      String desc = URLDecoder.decode((String)params.get("d"), "ISO-8859-1").trim();
      String time = URLDecoder.decode((String)params.get("l"), "ISO-8859-1").trim();

      if ((desc.length() == 0) || (time.length() == 0)) {
        response.setResultStatus(false, "Bad parameters");
      }
      try
      {
        deskId = Integer.parseInt(desc);
        chatTime = Long.parseLong(time);
      } catch (Exception ex) {
        response.setResultStatus(false, "Bad parameters");
      }
      Desk desk;
      Desk desk;
      if (params.containsKey("t"))
        desk = Desk.getDeskByID(server.getTournamentDesks(ParamParser.getInt(params, "t")), deskId);
      else {
        desk = Desk.getDeskByID(server.getDesks(), deskId);
      }

      if (desk != null)
      {
        Place place = desk.getPlayerPlace(currentPlayer);
        if (place != null)
        {
          Chat chat = desk.getChat().getChat(chatTime);
          response.setResultStatus(true);

          XMLTag tag = chat.toXMLTag();
          String xml = tag.toString();
          tag.invalidate();

          response.setParametersXML(xml);
        } else {
          response.setResultStatus(false, "You are not sitting at this table");
        }
      }
      else {
        response.setResultStatus(false, "Desk not found");
      }
    }
    else {
      response.setResultStatus(false, "Bad parameters");
    }

    return response;
  }
}