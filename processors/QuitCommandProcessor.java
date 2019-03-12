package processors;

import java.io.IOException;
import java.util.HashMap;
import server.Response;
import server.Server;

public class QuitCommandProcessor
  implements RequestCommandProcessor
{
  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("QUIT");

    server.invalidateCurrentPlayer();
    server.stopConnectionListening();

    response.setResultStatus(true, "Goodbye...");

    return response;
  }
}