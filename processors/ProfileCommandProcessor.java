package processors;

import game.Player;
import game.playerclub.ClubMember;
import game.playerclub.ClubPlayers;
import game.playerclub.PlayersClub;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import server.Response;
import server.Server;
import utils.xml.XMLParam;

public class ProfileCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_MONEY_TYPE = "m";
  public static final String DEFAULT_ENCODING = "ISO-8859-1";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("PROFILE");

    Player player = server.getCurrentPlayer();
    if (player != null)
      synchronized (player) {
        response.setResultStatus(true);

        ArrayList additionalParams = new ArrayList(8);
        additionalParams.add(new XMLParam("DEPOSITL", player.getDepositLimit().toString()));
        additionalParams.add(new XMLParam("ADDRESS", player.getAddress()));
        additionalParams.add(new XMLParam("STATE", player.getState()));
        additionalParams.add(new XMLParam("PHONE", player.getPhone()));
        additionalParams.add(new XMLParam("ZIP", player.getZip()));

        ClubMember cm = PlayersClub.getInstance().getClubPlayers().getMemberById(player.getID());
        if (cm == null) {
          additionalParams.add(new XMLParam("DPTS", 0));
          additionalParams.add(new XMLParam("ISCLUBMEMBER", 0));
          additionalParams.add(new XMLParam("JTPC", 0));
        } else {
          additionalParams.add(new XMLParam("DPTS", cm.getPoints().toString()));
          additionalParams.add(new XMLParam("ISCLUBMEMBER", 1));
          additionalParams.add(new XMLParam("JTPC", (float)((new Date().getTime() - cm.getRegDate().getTime()) / 1000L / 60L / 60L)));
        }

        if (params.containsKey("m")) {
          int moneyType = 0;
          try {
            moneyType = Integer.parseInt(URLDecoder.decode((String)params.get("m"), "ISO-8859-1"));
          } catch (Exception ex) {
            ex.printStackTrace();
          }

          if (moneyType == 0)
            response.setParametersXML(player.toXML(additionalParams, 0));
          else
            response.setParametersXML(player.toXML(additionalParams, 1));
        }
        else
        {
          response.setParametersXML(player.toXML(additionalParams, 0));
        }
      }
    else {
      response.setResultStatus(false, "Authorization first");
    }

    return response;
  }
}