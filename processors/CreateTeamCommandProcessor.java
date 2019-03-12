package processors;

import emails.CreateTeamMailSender;
import game.Player;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import server.Response;
import server.Server;

public class CreateTeamCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_TEAM = "p";
  public static final String PARAM_NAME = "n";
  public static final String PARAM_MESSAGE = "m";
  public static final String PARAMS_DELIMITER_REGEX = "[\\|\\s,.:;]";
  private static final String MSG_CONNOT_SEND_MESSAGE = "Cannot send message";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("CREATETEAM");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    if ((params.containsKey("p")) && (params.containsKey("n")) && (params.containsKey("m"))) {
      String teamString = URLDecoder.decode((String)params.get("p"), "ISO-8859-1").trim();
      String teamName = URLDecoder.decode((String)params.get("n"), "ISO-8859-1").trim();
      String message = URLDecoder.decode((String)params.get("m"), "ISO-8859-1").trim();

      ArrayList teamsList = new ArrayList();
      String[] values = teamString.split("[\\|\\s,.:;]");

      for (int i = 0; i < values.length; i++) {
        String str = values[i].trim();
        if (str.length() > 0) {
          teamsList.add(str);
          if (Player.searchPlayerByLogin(Server.getPlayersList(), str, false) == null) {
            response.setResultStatus(false, "Wrong login: " + str);
            return response;
          }
        }
      }
      try
      {
        email(currentPlayer, teamsList, teamName, message);

        response.setResultStatus(true);
        return response;
      }
      catch (Exception e) {
        e.printStackTrace();

        response.setResultStatus(false, "Cannot send message");
        return response;
      }
    }

    response.setResultStatus(false, "Bad parameters");

    return response;
  }

  public void email(Player currentPlayer, ArrayList teamsList, String teamName, String message)
    throws Exception
  {
    CreateTeamMailSender r = new CreateTeamMailSender();
    r.sendEmail(currentPlayer, teamsList, teamName, message);
  }
}