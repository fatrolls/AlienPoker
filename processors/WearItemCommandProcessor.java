package processors;

import commands.CmdLoadPlayerWardobes;
import commands.CmdLoadWardobes;
import game.Player;
import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import server.Response;
import server.Server;
import utils.Log;
import wardobe.WardobeItem;

public class WearItemCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_NAME = "n";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("WEARWARDOBE");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    if (!params.containsKey("n")) {
      response.setResultStatus(false, "Bad parameters");
      return response;
    }

    String name = URLDecoder.decode((String)params.get("n"), "ISO-8859-1").trim();

    CmdLoadWardobes cmd = new CmdLoadWardobes();
    Connection db = Server.getDbConnection();
    cmd.setDbConnection(db);
    cmd.execute();
    try {
      db.close();
    } catch (SQLException e) {
      throw new IOException(e.getMessage());
    }

    ArrayList wardobes = cmd.getWardobes();

    int status = 0;
    WardobeItem item = null;

    for (int i = 0; i < 2; i++)
    {
      if (i != currentPlayer.getGender())
      {
        continue;
      }
      ArrayList genderList = selectAllByGender(i, wardobes);

      Iterator iter = genderList.iterator();
      while (iter.hasNext()) {
        item = (WardobeItem)iter.next();
        if (item.getName().equals(name)) {
          status = 1;
        }

      }

    }

    if (status == 1)
    {
      CmdLoadPlayerWardobes cmd1 = new CmdLoadPlayerWardobes();
      cmd1.setPlayer(currentPlayer);
      db = Server.getDbConnection();
      cmd1.setDbConnection(db);
      cmd1.execute();
      try {
        db.close();
      } catch (SQLException e) {
        throw new IOException(e.getMessage());
      }
      wardobes = cmd1.getWardobes();

      Iterator iter = wardobes.iterator();
      while (iter.hasNext()) {
        WardobeItem item2 = (WardobeItem)iter.next();
        if (item2.getName().equals(name)) {
          item = item2;
          status = 2;
          break;
        }
      }

      if (status == 2) {
        wearWardobe(currentPlayer, item);
        response.setResultStatus(true);
      }
      else {
        response.setResultStatus(false, "Cannot find item at your stock.");
      }
    }
    else if (status == 0) {
      response.setResultStatus(false, "Cannot find this wardobe.");
    }

    return response;
  }

  private void wearWardobe(Player currentPlayer, WardobeItem item) {
    PreparedStatement statement = null;
    String query = "UPDATE player_wardobes set state=?  where id=? and user_id=?";

    Connection conn = null;
    try
    {
      conn = Server.getDbConnection();
      statement = conn.prepareStatement(query);

      statement.setInt(1, 3);
      statement.setInt(2, item.getId());
      statement.setInt(3, currentPlayer.getID());

      statement.executeUpdate();
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    finally {
      try {
        if (statement != null)
          statement.close();
      }
      catch (Exception e)
      {
        Log.out(e.getMessage());
      }
      try
      {
        if (conn != null)
          conn.close();
      }
      catch (Exception e)
      {
        Log.out(e.getMessage());
      }
    }
  }

  private ArrayList selectAllByGender(int gender, ArrayList list)
  {
    ArrayList result = new ArrayList();
    Iterator iter = list.iterator();
    while (iter.hasNext()) {
      WardobeItem item = (WardobeItem)iter.next();
      if (item.getGender() == gender) {
        result.add(item);
      }
    }
    return result;
  }
}