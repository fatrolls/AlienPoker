package commands;

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

public class CmdLoadWardobes extends Command
{
  private ArrayList wardobes = new ArrayList();

  public ArrayList getWardobes() {
    return wardobes;
  }

  public boolean execute() throws IOException {
    String query = "select * from wardobes order by id";
    boolean status;
    try {
      PreparedStatement statement = getDbConnection().prepareStatement(query);
      ResultSet result = statement.executeQuery();

      while (result.next())
      {
        WardobeItem wardobeItem = new WardobeItem();
        wardobeItem.setId(result.getInt("id"));
        wardobeItem.setName(result.getString("name"));
        wardobeItem.setPrice(new BigDecimal(result.getDouble("price")).setScale(2, 5));
        wardobeItem.setTitle(result.getString("title"));
        wardobeItem.setState(WardobeState.getState(1));
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
}