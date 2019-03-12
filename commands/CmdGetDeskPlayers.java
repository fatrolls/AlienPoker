package commands;

import game.Desk;
import game.Player;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Deprecated
public class CmdGetDeskPlayers extends Command
{
  private ArrayList players = new ArrayList();
  private Desk desk = null;

  public void setPlayers(ArrayList players)
  {
    this.players = players;
  }

  public void setDesk(Desk desk)
  {
    this.desk = desk;
  }

  public Desk getDesk()
  {
    return desk;
  }

  public boolean execute()
  {
    PreparedStatement statement = null;
    boolean status = false;
    String query = "select user_id,place,amount from desk_users where desk_id =?";
    try
    {
      statement = getDbConnection().prepareStatement(query);
      statement.setInt(1, desk.getID());

      ResultSet result = statement.executeQuery();
      while (result.next()) {
        int playerID = result.getInt("user_id");
        int deskPlace = result.getInt("place");
        BigDecimal deskAmount = new BigDecimal(result.getFloat("amount"));

        Player player = Player.getPlayerByID(players, playerID);

        if (player != null) {
          desk.seatPlayer(player, deskPlace, deskAmount);
        }
      }

      result.close();
      status = true;
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    finally {
      try {
        statement.close();
      }
      catch (Exception e)
      {
      }
    }
    return status;
  }
}