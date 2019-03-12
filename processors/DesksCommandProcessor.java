package processors;

import commands.CmdSelectDesks;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import server.ParamParser;
import server.Response;
import server.Server;

public class DesksCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_POKER_TYPE = "p";
  public static final String PARAM_POKER_LIMIT_TYPE = "l";
  public static final String PARAMS_DELIMITER_REGEX = "\\|";
  private static final String PARAM_DESK_TOURNAMET_ID = "t";
  public static final String PARAM_TOURNAMENT_TYPE = "w";
  private static final String PARAM_TOURNAMET_SUB_TYPE = "s";
  private static final String PARAM_MONEY_TYPE = "m";
  private static final String PARAM_EMPTY_TABLES = "e";
  private static final String PARAM_GAME_SPEED = "g";
  private static final String PARAM_TOURNAMENT_STATUS = "o";
  private static final String PARAM_PRIVATE = "v";
  private static final String MSG_BAD_REQUEST = "Bad Request";
  private static final long TIMER_INTERVAL = 3000L;
  private static Timer timer = new Timer();

  private static ArrayList cachedDesks = new ArrayList();

  public Response process(HashMap params, Server server) throws IOException
  {
    Response response = new Response("DESKS");

    if (server.getCurrentPlayer() == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    CmdSelectDesks cmd = new CmdSelectDesks();

    if ((params.containsKey("t")) && (params.containsKey("w")))
    {
      ArrayList statusList = cmd.getTournamentStatus();
      if (params.containsKey("o")) {
        String paramTournamentStatus = (String)params.get("o");
        String[] tournamentStatus = paramTournamentStatus.split("\\|");
        for (int i = 0; i < tournamentStatus.length; i++) {
          int status = Integer.valueOf(tournamentStatus[i]).intValue();
          if ((status == 1) || (status == 2) || (status == 4) || (status == 8)) {
            statusList.add(new Integer(status));
          }
        }

      }

      cmd.setTournament(ParamParser.getInt(params, "w"));
      if (params.containsKey("s"))
        cmd.setTournamentSubType(ParamParser.getInt(params, "s"));
      else {
        cmd.setTournamentSubType(1);
      }

    }

    boolean error = false;
    try
    {
      ArrayList moneyTypes = cmd.getMoneyTypes();
      ArrayList pokerTypes = cmd.getPokerTypes();
      ArrayList limitTypes = cmd.getLimitTypes();
      ArrayList speedTypes = cmd.getSpeedTypes();
      ArrayList emptyTypes = cmd.getEmptyTypes();

      if (params.containsKey("m")) {
        String paramValue = (String)params.get("m");
        String[] values = paramValue.split("\\|");
        for (int i = 0; i < values.length; i++) {
          moneyTypes.add(Integer.valueOf(values[i]));
        }

      }

      if (params.containsKey("p")) {
        String paramValue = (String)params.get("p");
        String[] values = paramValue.split("\\|");
        for (int i = 0; i < values.length; i++) {
          pokerTypes.add(Integer.valueOf(values[i]));
        }
      }

      if (params.containsKey("l")) {
        String paramValueLimit = (String)params.get("l");

        String[] valuesLimit = paramValueLimit.split("\\|");
        for (int i = 0; i < valuesLimit.length; i++) {
          int limit = Integer.valueOf(valuesLimit[i]).intValue();
          if ((limit == 1) || (limit == 2) || (limit == 3))
            limitTypes.add(Integer.valueOf(valuesLimit[i]));
          else {
            error = true;
          }
        }

      }

      if (params.containsKey("g")) {
        String paramValue = (String)params.get("g");
        String[] values = paramValue.split("\\|");
        for (int i = 0; i < values.length; i++) {
          speedTypes.add(Integer.valueOf(values[i]));
        }
      }

      if (params.containsKey("e")) {
        String paramValue = (String)params.get("e");
        String[] values = paramValue.split("\\|");
        for (int i = 0; i < values.length; i++) {
          emptyTypes.add(Integer.valueOf(values[i]));
        }
      }

      cmd.setDesks(cachedDesks);

      if ((params.containsKey("v")) && 
        (ParamParser.getInt(params, "v") == 1)) {
        cmd.setOnlyPrivate(true);
      }

      cmd.select();
    }
    catch (Exception e)
    {
      error = true;
    }

    if (error) {
      response.setResultStatus(false, "Bad parameters");
    }
    else
    {
      String xml = cmd.toXML();
      if (xml == null) {
        response.setResultStatus(false, "Bad Request");
      } else {
        response.setResultStatus(true);
        response.setParametersXML(xml);
      }

    }

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
      ArrayList list;
      synchronized (Server.getDesksList()) {
        list = new ArrayList(Server.getDesksList().size());
        list.addAll(Server.getDesksList());
      }
      DesksCommandProcessor.access$002(list);
    }
  }
}