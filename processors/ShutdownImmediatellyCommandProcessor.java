package processors;

import java.io.IOException;
import java.util.HashMap;
import server.Response;
import server.Server;

public class ShutdownImmediatellyCommandProcessor
  implements RequestCommandProcessor
{
  public Response process(HashMap params, Server server)
    throws IOException
  {
    System.exit(1);
    return null;
  }
}