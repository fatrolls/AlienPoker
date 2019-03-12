package commands;

import game.Player;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import utils.Log;

public class CmdReadPlayers extends Command
{
  private ArrayList players = new ArrayList();

  public ArrayList getList() {
    return players;
  }

  public boolean execute() throws IOException {
    PreparedStatement statement = null;
    boolean status = false;
    String query = "SELECT u.* from users u";
    try
    {
      statement = getDbConnection().prepareStatement(query);
      ResultSet result = statement.executeQuery();

      while (result.next()) {
        Player player = new Player();

        player.setID(result.getInt("user_id"));
        player.setLogin(result.getString("us_login"));
        player.setPassword(result.getString("us_password"));
        player.setFreeAmount(new BigDecimal(result.getFloat("us_amount")).setScale(2, 5));
        player.setRealAmount(new BigDecimal(result.getFloat("us_real_amount")).setScale(2, 5));
        player.setCity(result.getString("us_city"));
        player.setCountry(result.getString("us_country"));
        player.setEmail(result.getString("us_email"));
        player.setFirstName(result.getString("us_fname"));
        player.setLastName(result.getString("us_lname"));
        player.setAvatar(result.getString("us_avatar"));
        player.setActive(result.getBoolean("us_player_status"));
        player.setAddress(result.getString("us_address"));
        player.setPhone(result.getString("us_phone"));
        player.setState(result.getString("us_state"));
        player.setZip(result.getString("us_zip"));
        player.setDepositLimit(new BigDecimal(result.getFloat("us_deposit_limit")).setScale(2, 5));
        player.setGender(result.getInt("us_gender"));
        Date date = result.getDate("us_birthday");
        synchronized (player.getBirthday()) {
          player.getBirthday().setTime(date);
        }

        players.add(player);
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
        if (statement != null)
          statement.close();
      }
      catch (Exception e)
      {
        Log.out(e.getMessage());
      }
    }

    return status;
  }
}