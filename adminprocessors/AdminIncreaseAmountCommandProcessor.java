package adminprocessors;

import game.Player;
import java.math.BigDecimal;
import java.util.Hashtable;
import server.Server;

public class AdminIncreaseAmountCommandProcessor
  implements AdminXMLResponse
{
  private static final String MSG_AMOUNT_INCREASED = "Amount Increased";
  private static final String MSG_AMOUNT_DECREASED = "Amount Decreased";

  public Hashtable increaseAmount(int userId, int moneyType, double amountD)
  {
    Hashtable response = new Hashtable();

    BigDecimal amount = new BigDecimal(amountD).setScale(2, 5);

    Player currentPlayer = Player.getPlayerByID(Server.getPlayersList(), userId);
    if (currentPlayer == null)
    {
      response.put("STATUS", "ERROR");
      response.put("RESPONSE", "Authorization first");

      return response;
    }

    currentPlayer.increaseAmount(amount, moneyType);

    response.put("STATUS", "OK");
    response.put("RESPONSE", "Amount Increased");

    return response;
  }

  public Hashtable decreaseAmount(int userId, int moneyType, double amountD)
  {
    Hashtable response = new Hashtable();

    BigDecimal amount = new BigDecimal(amountD).setScale(2, 5);

    Player currentPlayer = Player.getPlayerByID(Server.getPlayersList(), userId);
    if (currentPlayer == null)
    {
      response.put("STATUS", "ERROR");
      response.put("RESPONSE", "Authorization first");

      return response;
    }

    currentPlayer.decreaseAmount(amount, moneyType);

    response.put("STATUS", "OK");
    response.put("RESPONSE", "Amount Decreased");

    return response;
  }
}