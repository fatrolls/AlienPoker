package processors;

import java.io.IOException;
import java.util.HashMap;
import server.Response;
import server.Server;
import utils.Log;

public class UndefinedCommandProcessor
  implements RequestCommandProcessor
{
  public Response process(HashMap params, Server server)
    throws IOException
  {
    Log.out("begining UNDEFINED processing...");

    Response response = new Response("UNDEFINED");
    response.setResultStatus(false, "Unrecognized command");

    Log.out("begining UNDEFINED processing...");

    return response;
  }
}