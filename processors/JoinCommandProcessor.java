package processors;

import defaultvalues.DefaultValue;
import game.Desk;
import game.Game;
import game.PlacesList;
import game.Player;
import game.amounts.PlayerAmount;
import game.messages.CommonStateMessagesList;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import server.ParamParser;
import server.Response;
import server.Server;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;
import waitinglist.Waiter;
import waitinglist.WaitingList;
import waitinglist.WaitingRequirements;

public class JoinCommandProcessor
  implements RequestCommandProcessor
{
  public static final String PARAM_DESK_TOURNAMET_ID = "t";
  public static final String PARAM_DESK_ID = "d";
  public static final String PARAM_PLACE_NUMBER = "p";
  public static final String PARAM_MONEY_AMOUNT = "m";
  public static final String PARAM_PASSWORD = "o";
  private int deskID;
  private int placeNumber;
  private BigDecimal moneyAmount;
  public static final String OUT_PARAM_STATE = "STATE";
  public static final String OUT_PARAM_PLACE = "D_PLACE";
  public static final int JOIN_DESK_DELAY = 1000;
  private static final String MSG_NOT_YOUR_TURN = "Not your turn. Please Wait";
  private static final String MSG_TABLE_BUSY = "The table is busy. Please join waiting list";
  private static final String MSG_WRONG_PASSWORD = "Wrong password";

  public JoinCommandProcessor()
  {
    deskID = 0;
    placeNumber = 0;
    moneyAmount = DefaultValue.ZERO_BIDECIMAL;
  }

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("JOIN");
    Player currentPlayer = server.getCurrentPlayer();

    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    if ((params.containsKey("d")) && (params.containsKey("p")) && (params.containsKey("m"))) {
      deskID = ParamParser.getInt(params, "d");
      placeNumber = ParamParser.getInt(params, "p");
      moneyAmount = new BigDecimal(ParamParser.getFloat(params, "m"));
    }
    else {
      response.setResultStatus(false, "Bad parameters");
      return response;
    }
    Desk desk;
    Desk desk;
    if (params.containsKey("t")) {
      desk = Desk.getDeskByID(server.getTournamentDesks(ParamParser.getInt(params, "t")), deskID);
    }
    else
    {
      desk = Desk.getDeskByID(server.getDesks(), deskID);
      if ((desk != null) && 
        (desk.isPrivateDesk()) && (!desk.getPassword().equals(params.get("o")))) {
        response.setResultStatus(false, "Wrong password");
        return response;
      }

    }

    if (desk != null) {
      synchronized (desk)
      {
        List waiters = server.getWaitingList().getWaitersForDesk(desk);

        synchronized (waiters)
        {
          int size = waiters.size();
          int freePlaces = desk.getPlacesList().getFreePlacesCount();
          int availableWaitersQty = 0;

          if (size > 0)
          {
            Iterator iter = waiters.iterator();
            boolean found = false;
            while ((iter.hasNext()) && (!found)) {
              Waiter w = (Waiter)iter.next();
              if (w.getPlayer().getID() == currentPlayer.getID()) {
                if (w.isJoining()) {
                  found = w.getJoinDesk().equals(desk);
                }
              }
              else if (w.isJoining()) {
                if (w.getJoinDesk().getID() == desk.getID())
                  availableWaitersQty++;
              }
              else {
                availableWaitersQty++;
              }

            }

            if ((found) && (freePlaces == 0)) {
              response.setResultStatus(false, "Not your turn. Please Wait");
              return response;
            }if ((!found) && (availableWaitersQty >= freePlaces)) {
              response.setResultStatus(false, "The table is busy. Please join waiting list");
              return response;
            }

          }

        }

        WaitingList waitingList = server.getWaitingList();

        WaitingRequirements waitingRequirements = new WaitingRequirements(desk);
        Waiter waiter = new Waiter(currentPlayer, waitingList);
        waitingList.remove(waitingRequirements, waiter);

        JoiningRule joiningRule = new JoiningRules(null).getRule(desk.getLimitType());
        if (joiningRule.join(response, desk, currentPlayer, placeNumber, moneyAmount)) {
          XMLDoc doc = new XMLDoc();
          XMLTag tag = doc.startTag("STATE");
          tag.addParam("D_PLACE", placeNumber);
          String xml = doc.toString();
          doc.invalidate();

          Timer timer = new Timer();
          timer.schedule(new JoinMessageSender(desk, placeNumber, currentPlayer.getLogin()), 1000L);

          response.setParametersXML(xml);

          desk.getGame().initiateGameDeskClearing();
        }

      }

    }
    else
    {
      response.setResultStatus(false, "Bad parameters");
    }

    return response;
  }

  private class JoinMessageSender extends TimerTask
  {
    private int placeNumber = 0;
    private Desk desk = null;
    private String login = null;

    public JoinMessageSender(Desk desk, int placeNumber, String login)
    {
      this.placeNumber = placeNumber;
      this.desk = desk;
      this.login = login;
    }

    public void run()
    {
      desk.getPublicStateMessagesList().addCommonMessage(login, 60, placeNumber, 2);
    }
  }

  private class PotLimitJoiningRule
    implements JoiningRule
  {
    private PotLimitJoiningRule()
    {
    }

    public boolean join(Response response, Desk desk, Player player, int placeNumber, BigDecimal moneyAmount)
    {
      boolean result = false;
      if (desk.isPlayerOnDesk(player)) {
        response.setResultStatus(false, "ALREADY JOINED TO DESK");
      }
      else if (!desk.isPlaceAvailable(placeNumber)) {
        response.setResultStatus(false, "PLACE ALREADY USED");
      }
      else if ((desk.getMoneyType() != 1) && (desk.getMoneyType() != 0)) {
        response.setResultStatus(false, "Bad parameters");
      }
      else if (desk.getMinAmount().floatValue() > moneyAmount.floatValue()) {
        response.setResultStatus(false, "NEED MORE MONEY");
      }
      else if (player.getAmount(desk.getMoneyType()).floatValue() < moneyAmount.floatValue()) {
        response.setResultStatus(false, "NOT ENOUGH MONEY");
      }
      else if (moneyAmount.floatValue() > desk.getMaxAmount().floatValue()) {
        response.setResultStatus(false, "TO MUCH MONEY");
      }
      else {
        player.decreaseAmount(moneyAmount, desk.getMoneyType());
        desk.seatPlayer(player, placeNumber, moneyAmount);
        player.getPlayerAmount().recordDeskAmount(desk);

        response.setResultStatus(true);
        result = true;
      }

      return result;
    }
  }

  private class NoLimitJoiningRule
    implements JoiningRule
  {
    private NoLimitJoiningRule()
    {
    }

    public boolean join(Response response, Desk desk, Player player, int placeNumber, BigDecimal moneyAmount)
    {
      boolean result = false;
      if (desk.isPlayerOnDesk(player)) {
        response.setResultStatus(false, "ALREADY JOINED TO DESK");
      }
      else if (!desk.isPlaceAvailable(placeNumber)) {
        response.setResultStatus(false, "PLACE ALREADY USED");
      }
      else if ((desk.getMoneyType() != 1) && (desk.getMoneyType() != 0)) {
        response.setResultStatus(false, "Bad parameters");
      }
      else if (desk.getMinAmount().floatValue() > moneyAmount.floatValue()) {
        response.setResultStatus(false, "NEED MORE MONEY");
      }
      else if (player.getAmount(desk.getMoneyType()).floatValue() < moneyAmount.floatValue()) {
        response.setResultStatus(false, "NOT ENOUGH MONEY");
      }
      else if (moneyAmount.floatValue() > desk.getMaxAmount().floatValue()) {
        response.setResultStatus(false, "TO MUCH MONEY");
      }
      else {
        player.decreaseAmount(moneyAmount, desk.getMoneyType());
        desk.seatPlayer(player, placeNumber, moneyAmount);
        player.getPlayerAmount().recordDeskAmount(desk);

        response.setResultStatus(true);
        result = true;
      }

      return result;
    }
  }

  private class LimitJoiningRule
    implements JoiningRule
  {
    private LimitJoiningRule()
    {
    }

    public boolean join(Response response, Desk desk, Player player, int placeNumber, BigDecimal moneyAmount)
    {
      boolean result = false;
      if (desk.isPlayerOnDesk(player)) {
        response.setResultStatus(false, "ALREADY JOINED TO DESK");
      }
      else if (!desk.isPlaceAvailable(placeNumber)) {
        response.setResultStatus(false, "PLACE ALREADY USED");
      }
      else if ((desk.getMoneyType() != 1) && (desk.getMoneyType() != 0)) {
        response.setResultStatus(false, "Bad parameters");
      }
      else if (desk.getMinAmount().floatValue() > moneyAmount.floatValue()) {
        response.setResultStatus(false, "NEED MORE MONEY");
      }
      else if (player.getAmount(desk.getMoneyType()).floatValue() < moneyAmount.floatValue()) {
        response.setResultStatus(false, "NOT ENOUGH MONEY");
      }
      else
      {
        player.decreaseAmount(moneyAmount, desk.getMoneyType());
        desk.seatPlayer(player, placeNumber, moneyAmount);
        player.getPlayerAmount().recordDeskAmount(desk);

        response.setResultStatus(true);
        result = true;
      }

      return result;
    }
  }

  private class JoiningRules
  {
    private JoiningRules()
    {
    }

    public JoiningRule getRule(int limitType)
    {
      switch (limitType) {
      case 1:
        return new JoinCommandProcessor.LimitJoiningRule(JoinCommandProcessor.this, null);
      case 2:
        return new JoinCommandProcessor.NoLimitJoiningRule(JoinCommandProcessor.this, null);
      case 3:
        return new JoinCommandProcessor.PotLimitJoiningRule(JoinCommandProcessor.this, null);
      }
      throw new RuntimeException("error: bad limit type");
    }
  }
}