package processors;

import game.Player;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import org.apache.log4j.Logger;
import server.ParamParser;
import server.Response;
import server.Server;
import utils.CommonLogger;

public class SetDepositLimitCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_MONEY = "m";
  private static final String MSG_DEPOSIT_COMPLETE = "Deposit limit was saved";
  private static final String SQL_UPDATE = "update " + "users" + " set " + "us_deposit_limit" + "=? where " + "user_id" + "=?";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("SETDEPOSITLIMIT");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    float amount = ParamParser.getFloat(params, "m");
    BigDecimal am = new BigDecimal(amount).setScale(2, 5);
    if (am.compareTo(new BigDecimal(0.01D)) < 0) {
      response.setResultStatus(false, "Bad parameters");
      return response;
    }

    currentPlayer.setDepositLimit(am);
    updateDeposit(currentPlayer, am);

    response.setResultStatus(true, "Deposit limit was saved");

    return response;
  }

  public void updateDeposit(Player player, BigDecimal amount)
  {
    Connection dbConn = Server.getDbConnection();
    try
    {
      PreparedStatement pstmt = dbConn.prepareStatement(SQL_UPDATE);
      pstmt.setFloat(1, amount.floatValue());
      pstmt.setFloat(2, player.getID());
      pstmt.executeUpdate();

      pstmt.close();
    }
    catch (SQLException e) {
      CommonLogger.getLogger().warn("Cannot Update Deposit Limit: ", e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    finally {
      try {
        dbConn.close();
      }
      catch (Exception e) {
        CommonLogger.getLogger().warn("Cannot Close Connection: ", e);
      }
    }
  }
}