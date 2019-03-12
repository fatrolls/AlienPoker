package game.privatedesks;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import server.Server;
import utils.CommonLogger;

public class CreatePrivateDeskDbCommand
{
  private static final String INSERT_SQL = "insert into desks set d_limit_type= ?, d_poker_type= ?, d_name= ?, d_money_type= ?, d_min_bet= ?, d_max_bet= ?, d_ante= ?, d_bring_in= ?, d_rate_limit= ?, d_min_amount= ?, d_create_date = NOW(), d_places= ?, d_speed= ?, d_rake= ?, d_password= ?, d_private= ?, d_owner= ? ";

  public int createDesk(int limit, int pokerType, String name, int moneyType, BigDecimal minBet, BigDecimal maxBet, BigDecimal ante, BigDecimal bringIn, BigDecimal rateLimit, BigDecimal minAmount, int places, int speed, BigDecimal rake, String password, int isPrivate, int owner)
  {
    int id = 0;

    Connection dbConn = Server.getDbConnection();
    PreparedStatement pstmt = null;
    try
    {
      pstmt = dbConn.prepareStatement("insert into desks set d_limit_type= ?, d_poker_type= ?, d_name= ?, d_money_type= ?, d_min_bet= ?, d_max_bet= ?, d_ante= ?, d_bring_in= ?, d_rate_limit= ?, d_min_amount= ?, d_create_date = NOW(), d_places= ?, d_speed= ?, d_rake= ?, d_password= ?, d_private= ?, d_owner= ? ", 1);

      pstmt.setInt(1, limit);
      pstmt.setInt(2, pokerType);
      pstmt.setString(3, name);
      pstmt.setString(4, "" + moneyType);
      pstmt.setFloat(5, minBet.floatValue());
      pstmt.setFloat(6, maxBet.floatValue());
      pstmt.setFloat(7, ante.floatValue());

      pstmt.setFloat(8, bringIn.floatValue());
      pstmt.setFloat(9, rateLimit.floatValue());
      pstmt.setFloat(10, minAmount.floatValue());
      pstmt.setInt(11, places);
      pstmt.setInt(12, speed);
      pstmt.setFloat(13, rake.floatValue());
      pstmt.setString(14, password);

      pstmt.setInt(15, isPrivate);
      pstmt.setInt(16, owner);
      pstmt.executeUpdate();

      ResultSet result = pstmt.getGeneratedKeys();
      if (result.next()) {
        id = result.getInt(1);
      }

      result.close();
      pstmt.close();
      dbConn.close();
    }
    catch (SQLException e) {
      if (pstmt != null) {
        try {
          pstmt.close();
        } catch (SQLException e1) {
          CommonLogger.getLogger().warn(e1);
        }
      }
      try
      {
        dbConn.close();
      } catch (SQLException e1) {
        CommonLogger.getLogger().warn(e1);
      }

      CommonLogger.getLogger().warn("Cannot create private desk", e);
      throw new RuntimeException(e.getMessage());
    }

    return id;
  }
}