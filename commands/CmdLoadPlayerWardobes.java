package commands;

import game.Player;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import wardobe.WardobeItem;
import wardobe.WardobeState;
import wardobe.WardobeType;

public class CmdLoadPlayerWardobes extends Command
{
  private ArrayList wardobes = new ArrayList();
  private Player player;

  public ArrayList getWardobes()
  {
    return wardobes;
  }

  public boolean execute()
    throws IOException
  {
    String query = "select * from player_wardobes inner join wardobes on player_wardobes.id=wardobes.id where user_id=?  order by player_wardobes.id";
    boolean status;
    try
    {
      PreparedStatement statement = getDbConnection().prepareStatement(query);
      statement.setInt(1, player.getID());
      ResultSet result = statement.executeQuery();

      while (result.next())
      {
        WardobeItem wardobeItem = new WardobeItem();
        wardobeItem.setId(result.getInt("id"));
        wardobeItem.setName(result.getString("name"));
        wardobeItem.setPrice(new BigDecimal(result.getDouble("price")).setScale(2, 5));
        wardobeItem.setTitle(result.getString("title"));
        wardobeItem.setState(WardobeState.getState(result.getInt("state")));
        wardobeItem.setType(WardobeType.getWardobeType(result.getInt("type")));
        wardobeItem.setGender(result.getInt("gender"));

        wardobes.add(wardobeItem);
      }

      result.close();
      statement.close();
      status = true;
    }
    catch (SQLException e)
    {
      throw new RuntimeException(e);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }

    return status;
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }
}