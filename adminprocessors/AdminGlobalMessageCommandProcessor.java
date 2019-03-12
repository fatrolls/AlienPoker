package adminprocessors;

import game.Desk;
import game.chat.Chat;
import game.chat.ChatMessage;
import game.chat.ChatTimer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import server.Server;

public class AdminGlobalMessageCommandProcessor
  implements AdminXMLResponse
{
  public static final String PARAM_TOURNMENT_ID = "t";
  public static final String DEFAULT_ENCODING = "ISO-8859-1";
  private static final String MSG_TOURNAMENT_WAS_DELETED = "Message was added";

  public Hashtable add(String msg)
  {
    Hashtable response = new Hashtable();

    ArrayList desks = Server.getDesksList();

    ChatMessage chatMessage = new ChatMessage();

    int count = 0;

    Iterator it = desks.iterator();

    synchronized (desks)
    {
      while (it.hasNext())
      {
        Desk desk = (Desk)it.next();

        int deskId = desk.getID();

        chatMessage = new ChatMessage();
        chatMessage.setDeskId(deskId);
        chatMessage.setPlaceNumber(99);
        chatMessage.setText(msg);
        chatMessage.setTime(ChatTimer.getCurrentTime());
        desk.getChat().add(chatMessage);

        count++;
      }
    }

    response.put("STATUS", "OK");
    response.put("RESPONSE", "Message was added to " + count + " tables");

    return response;
  }
}