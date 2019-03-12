package processors;

import game.Desk;
import game.Place;
import game.Player;
import game.chat.Chat;
import game.chat.ChatMessage;
import game.chat.ChatTimer;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;

public class ChatAddCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_DESK = "d";
  public static final String PARAM_MESSAGE = "m";
  public static final String DEFAULT_ENCODING = "ISO-8859-1";
  public static final String MSG_MESSAGE_SEND = "Your Message Was Sent";
  public static final String MSG_DESK_NOT_FOUND = "Desk not found";
  public static final String MSG_WRONG_PLAYER_TABLE = "You are not sitting at this table";
  private static final String PARAM_DESK_TOURNAMET_ID = "t";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("CHATADD");
    int deskId = 0;

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    if ((params.containsKey("d")) && (params.containsKey("m"))) {
      String desc = URLDecoder.decode((String)params.get("d"), "ISO-8859-1").trim();
      String message = URLDecoder.decode((String)params.get("m"), "ISO-8859-1").trim();

      if ((desc.length() == 0) || (message.length() == 0)) {
        response.setResultStatus(false, "Bad parameters");
      }
      try
      {
        deskId = Integer.parseInt(desc);
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
          int placeNumber = place.getNumber();
          ChatMessage chatMessage = new ChatMessage();
          chatMessage.setDeskId(deskId);
          chatMessage.setPlaceNumber(placeNumber);
          chatMessage.setText(message);
          chatMessage.setTime(ChatTimer.getCurrentTime());
          desk.getChat().add(chatMessage);
          response.setResultStatus(true, "Your Message Was Sent");
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