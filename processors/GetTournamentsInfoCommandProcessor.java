package processors;

import java.io.IOException;
import java.util.HashMap;
import server.Response;
import server.Server;

public class GetTournamentsInfoCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_TYPE = "t";
  private static final String MSG_INVALID_TYPE = "Invalid Type";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("GETTOURNAMENTSINFO");

    response.setResultStatus(false, "Bad parameters");
    return response;
  }
}