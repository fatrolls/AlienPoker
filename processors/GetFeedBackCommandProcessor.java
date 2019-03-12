package processors;

import feedbacks.FeedBackTopics;
import game.Player;
import java.io.IOException;
import java.util.HashMap;
import server.Response;
import server.Server;

public class GetFeedBackCommandProcessor
  implements RequestCommandProcessor
{
  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("GETFEEDBACK");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    response.setResultStatus(true);
    response.setParametersXML(Server.getFeedBackTopics().toXML());

    return response;
  }
}