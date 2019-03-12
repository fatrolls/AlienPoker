package commands.safeupdaters;

import game.Player;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import server.Server;
import utils.Log;

public class PlayerAmountUpdater
  implements Runnable
{
  private Player player;
  private BigDecimal amount;
  private int moneyType;
  private static final String SQL_MONEY_FUN = "update users set us_amount=? where user_id=?";
  private static final String SQL_MONEY_REAL = "update users set us_real_amount=? where user_id=?";

  public PlayerAmountUpdater(Player paramPlayer, BigDecimal paramBigDecimal, int paramInt)
  {
    amount = new BigDecimal(0);
    player = paramPlayer;
    amount = paramBigDecimal;
    moneyType = paramInt;
  }

  public void run()
  {
    System.out.println("Player: " + player.getID() + " LOGIN: " + player.getLogin() + " Amount: " + amount.floatValue() + " MoneyType: " + moneyType);
    Connection localConnection = Server.getDbConnection();
    PreparedStatement localPreparedStatement = null;
    try
    {
      if (moneyType == 1)
      {
        localPreparedStatement = localConnection.prepareStatement("update users set us_amount=? where user_id=?");
        localPreparedStatement.setFloat(1, amount.floatValue());
        localPreparedStatement.setInt(2, player.getID());
        localPreparedStatement.executeUpdate();
        localPreparedStatement.close();
      }
      else if (moneyType == 0)
      {
        localPreparedStatement = localConnection.prepareStatement("update users set us_real_amount=? where user_id=?");
        localPreparedStatement.setFloat(1, amount.floatValue());
        localPreparedStatement.setInt(2, player.getID());
        localPreparedStatement.executeUpdate();
        localPreparedStatement.close();

        localPreparedStatement = localConnection.prepareStatement("INSERT INTO real_money_log (`id`,`logtime`,`playerid`,`amount`,`notes`) VALUES (NULL,UNIX_TIMESTAMP(),'" + player.getID() + "','" + amount.floatValue() + "','PlayerAmountUpdater')");
        localPreparedStatement.executeUpdate();
        localPreparedStatement.close();
      }

      localConnection.close();
    }
    catch (SQLException localSQLException1)
    {
      if (localPreparedStatement != null)
        try
        {
          localPreparedStatement.close();
        }
        catch (SQLException localSQLException2)
        {
          Log.out(localSQLException2.getMessage());
        }
      try
      {
        localConnection.close();
      }
      catch (SQLException localSQLException3)
      {
        Log.out(localSQLException3.getMessage());
      }
      Log.out("Class PlayerAmountUpdater->run() : Error: " + localSQLException1.getMessage());
      throw new RuntimeException(localSQLException1.getMessage());
    }
  }
}