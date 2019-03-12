package processors;

import java.io.IOException;
import java.util.HashMap;
import server.Response;
import server.Server;

public abstract interface RequestCommandProcessor
{
  public abstract Response process(HashMap paramHashMap, Server paramServer)
    throws IOException;
}