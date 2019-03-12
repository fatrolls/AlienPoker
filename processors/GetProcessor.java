package processors;

import adminprocessors.AdminIncreaseAmountCommandProcessor;
import game.Player;
import java.io.IOException;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import server.Response;
import server.Server;
import sun.misc.BASE64Encoder;

public class GetProcessor
  implements RequestCommandProcessor
{
  int transactionId = 0;
  int customer = 0;
  String transaction = "deposit";
  int amount = 500;
  String code = "";

  public Response process(HashMap params, Server server) throws IOException
  {
    System.out.println("" + params);

    Response response = new Response("GET");
    String content = "?BAD REQUEST&transaction=failed";
    response.setResultStatus(false, content);
    try {
      String r = (String)params.get("_");
      if (r == null) {
        return response;
      }

      HashMap parsedParams = new HashMap();
      StringTokenizer t = new StringTokenizer(r, "&");
      while (t.hasMoreElements()) {
        String str = (String)t.nextElement();
        StringTokenizer strTokenizer = new StringTokenizer(str, "=");
        while (strTokenizer.hasMoreElements())
          parsedParams.put(strTokenizer.nextElement(), strTokenizer.nextElement());
      }
      if (parsedParams.isEmpty()) {
        return response;
      }

      String customerId = (String)parsedParams.get("customer");
      if (customerId == null) {
        return response;
      }
      String customerPassword = "";

      ArrayList players = Server.getPlayersList();
      for (int i = 0; i < players.size(); i++) {
        Player player = (Player)players.get(i);
        int id = player.getID();
        if (id == Integer.parseInt(customerId)) {
          customerPassword = player.getPassword();
          break;
        }
      }

      String result = (customerId + customerPassword + "abrakadabra").replaceAll(" ", "").toUpperCase();
      MessageDigest md = MessageDigest.getInstance("MD5");
      md.update(result.getBytes());
      byte[] hash = md.digest();
      BASE64Encoder encoder = new BASE64Encoder();
      result = encoder.encode(hash);
      if (!result.equals((String)parsedParams.get("code"))) {
        content = "?transactionId=" + parsedParams.get("transactionId") + "&" + "customer" + "=" + customerId + "&" + "amount" + "=" + parsedParams.get("amount") + "&" + "code" + "=" + parsedParams.get("code") + "&" + "transaction" + "=failed";

        response.setResultStatus(false, content);
        return response;
      }

      Hashtable res = null;
      AdminIncreaseAmountCommandProcessor amountProc = new AdminIncreaseAmountCommandProcessor();
      if ("withdrawl".equals((String)parsedParams.get("transaction"))) {
        res = amountProc.decreaseAmount(Integer.parseInt(customerId), 0, Double.parseDouble((String)parsedParams.get("amount")));
      }
      else if ("deposit".equals((String)parsedParams.get("transaction"))) {
        res = amountProc.increaseAmount(Integer.parseInt(customerId), 0, Double.parseDouble((String)parsedParams.get("amount")));
      }

      if ((res == null) || (((String)res.get("STATUS")).equals("ERROR"))) {
        content = "?transactionId=" + parsedParams.get("transactionId") + "&" + "customer" + "=" + customerId + "&" + "amount" + "=" + parsedParams.get("amount") + "&" + "code" + "=" + parsedParams.get("code") + "&" + "transaction" + "=failed";

        response.setResultStatus(false, content);
      }
      else {
        content = "?transactionId=" + parsedParams.get("transactionId") + "&" + "customer" + "=" + customerId + "&" + "amount" + "=" + parsedParams.get("amount") + "&" + "code" + "=" + parsedParams.get("code") + "&" + "transaction" + "=successful";

        response.setResultStatus(true, content);
      }
      return response;
    } catch (Exception e) {
    }
    return response;
  }
}