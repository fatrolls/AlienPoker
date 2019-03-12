package processors;

import commands.CmdSelectPrivateDesks;
import game.PrivateDesksStorage;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import server.Response;
import server.Server;
import utils.CommonLogger;

public class PrivateDesksCommandProcessor
  implements RequestCommandProcessor
{
  private static String cachedXml = "";
  private static final long TIMER_INTERVAL = 3000L;
  private static Timer timer = new Timer();

  public Response process(HashMap params, Server server) throws IOException
  {
    Response response = new Response("PRIVATEDESKS");

    if (server.getCurrentPlayer() == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    response.setResultStatus(true);
    response.setParametersXML(cachedXml);

    return response;
  }

  public static void startTimer()
  {
    timer.schedule(new DesksCachier(), new Date(), 3000L);
  }

  public static void stopTimer() {
    timer.cancel();
  }

  public static class DesksCachier extends TimerTask
  {
    public void run()
    {
      CmdSelectPrivateDesks cmd = new CmdSelectPrivateDesks();
      cmd.setDesks(PrivateDesksStorage.getPrivateDesksList());

      String xml = "";
      try {
        xml = cmd.toXML();
      } catch (Exception e) {
        CommonLogger.getLogger().warn("Cannot get xml", e);
      }

      PrivateDesksCommandProcessor.access$002(xml);
    }
  }
}