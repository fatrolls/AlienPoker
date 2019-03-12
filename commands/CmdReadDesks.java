package commands;

import game.Desk;
import game.Player;
import game.PrivateDesksStorage;
import game.privatedesks.DropPrivateDeskDbCommand;
import game.speed.GameSpeedFactory;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import utils.CommonLogger;

public class CmdReadDesks extends Command
{
  private ArrayList desks = new ArrayList();
  private ArrayList players = new ArrayList();

  private static String USERS_COUNT_VAR = "users_count";

  public boolean execute() throws IOException
  {
    PreparedStatement statement = null;
    boolean status = false;
    String query = "select d.*, count(dp.user_id) as " + USERS_COUNT_VAR + " " + "from " + "desks" + " d left join " + "desk_users" + " dp on d." + "desk_id" + "= dp." + "desk_id" + " " + "group by d." + "desk_id";
    try
    {
      statement = getDbConnection().prepareStatement(query);
      ResultSet result = statement.executeQuery();

      while (result.next()) {
        Desk desk = new Desk();

        desk.setID(result.getInt("desk_id"));
        desk.setDeskName(result.getString("d_name"));
        desk.setMoneyType(result.getInt("d_money_type"));
        desk.setLimitType(result.getInt("d_limit_type"));
        desk.setMinBet(new BigDecimal(result.getFloat("d_min_bet")));
        desk.setMaxBet(new BigDecimal(result.getFloat("d_max_bet")));

        desk.setAnte(new BigDecimal(result.getFloat("d_ante")).setScale(2, 5));
        desk.setBringIn(new BigDecimal(result.getFloat("d_bring_in")).setScale(2, 5));

        desk.setPokerType(result.getInt("d_poker_type"));
        desk.setMinAmount(new BigDecimal(result.getFloat("d_min_amount")));
        desk.setMinPlayerRate(new BigDecimal(result.getFloat("d_rate_limit")));

        desk.setRake(new BigDecimal(result.getFloat("d_rake")));

        desk.createPlaces(result.getInt("d_places"));

        boolean isPrivate = result.getInt("d_private") == 1;

        if (isPrivate) {
          int creator = result.getInt("d_owner");
          Player creatorPlayer = Player.getPlayerByID(players, creator);
          if (creatorPlayer == null) {
            CommonLogger.getLogger().warn("Cannot find the owner of private desk :" + desk.getID() + " player: " + creator);
            new DropPrivateDeskDbCommand().dropDesk(desk.getID());
            continue;
          }
          desk.setPrivateDesk(true);
          desk.setCreator(creatorPlayer);
          desk.setPassword(result.getString("d_password"));
          PrivateDesksStorage.registerPrivateDesk(desk);
        }

        desk.startUpGame(GameSpeedFactory.getGameSpeed(result.getInt("d_speed")));
        desks.add(desk);
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
      catch (Exception e) {
        CommonLogger.getLogger().warn("CmdReadDesks failed", e);
      }
    }

    return status;
  }

  public ArrayList getList()
  {
    return desks;
  }

  public void setPlayers(ArrayList players)
  {
    this.players = players;
  }
}