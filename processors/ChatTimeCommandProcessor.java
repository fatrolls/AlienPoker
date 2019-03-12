package processors;

import game.Player;
import game.chat.ChatTimer;
import java.io.IOException;
import java.util.HashMap;
import server.Response;
import server.Server;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class ChatTimeCommandProcessor
  implements RequestCommandProcessor
{
  public static final String MSG_CHATTIME_SEND = "Your chat time";
  private static final String OUT_PARAM_STATE = "CHATTIME";
  private static final String OUT_PARAM_VALUE = "VALUE";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("CHATTIME");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    response.setResultStatus(true);

    XMLDoc doc = new XMLDoc();
    XMLTag tag = doc.startTag("CHATTIME");
    tag.addParam("VALUE", "" + ChatTimer.getCurrentTime());
    String xml = doc.toString();
    doc.invalidate();

    response.setParametersXML(xml);

    return response;
  }
}