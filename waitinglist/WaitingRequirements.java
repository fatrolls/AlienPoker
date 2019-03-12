package waitinglist;

import game.Desk;
import game.Player;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import utils.Log;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class WaitingRequirements
{
  public static final int ONLY_THIS_TABLE_REQ = 0;
  public static final int SAME_TABLE_REQ = 1;
  private int type;
  private int minPlayers;
  private int deskId;
  private BigDecimal minBet = new BigDecimal(0);
  private BigDecimal maxBet = new BigDecimal(0);
  private int pokerType;
  private int limitType;
  private int moneyType;
  private BigDecimal minAmount = new BigDecimal(0);

  private final List waiters = new ArrayList();
  private static final String OUT_PARAM_DESKID = "DESKID";
  private static final String OUT_PARAM_TAG_NAME = "WAITINGREQUIREMENTS";
  private static final String OUT_PARAM_POKER_TYPE = "POKERTYPE";
  private static final String OUT_PARAM_MONEY_TYPE = "MONEYTYPE";
  private static final String OUT_PARAM_MIN_BET = "MINBET";
  private static final String OUT_PARAM_MAX_BET = "MAXBET";
  private static final String OUT_PARAM_LIMIT_TYPE = "LIMITTYPE";
  private static final String OUT_PARAM_MIN_PLAYERS = "MINPLAYERS";

  public WaitingRequirements()
  {
  }

  public WaitingRequirements(Desk desk)
  {
    type = 0;
    deskId = desk.getID();
  }

  public WaitingRequirements(Desk desk, int minPlayers)
  {
    type = 1;
    this.minPlayers = minPlayers;
    minBet = desk.getMinBet();
    maxBet = desk.getMaxBet();
    pokerType = desk.getPokerType();
    limitType = desk.getLimitType();
    moneyType = desk.getMoneyType();
    minAmount = desk.getMinAmount();
  }

  public boolean equals(Object obj)
  {
    WaitingRequirements req = (WaitingRequirements)obj;
    if (type != req.type) {
      return false;
    }

    if (type == 0)
      return deskId == req.deskId;
    if (type == 1) {
      return (pokerType == req.pokerType) && (minBet.compareTo(req.minBet) == 0) && (maxBet.compareTo(req.maxBet) == 0) && (limitType == req.limitType) && (moneyType == req.moneyType) && (minAmount.compareTo(req.minAmount) == 0) && (minPlayers == req.minPlayers);
    }

    Log.out("Class WaitingRequirements - method equals - ERROR: UNKNOWN type - this.type=" + type + " req.type=" + req.type);
    return false;
  }

  public boolean equalsIgnoreMinPlayers(WaitingRequirements req)
  {
    if (type != req.type) {
      return false;
    }

    if (type == 0)
      return deskId == req.deskId;
    if (type == 1) {
      return (pokerType == req.pokerType) && (minBet.compareTo(req.minBet) == 0) && (maxBet.compareTo(req.maxBet) == 0) && (limitType == req.limitType) && (moneyType == req.moneyType) && (minAmount.compareTo(req.minAmount) == 0);
    }

    Log.out("Class WaitingRequirements - method equalsIgnoreMinPlayers - ERROR: UNKNOWN type - this.type=" + type + " req.type=" + req.type);
    return false;
  }

  public boolean conatain(Desk desk)
  {
    if (desk.isPrivateDesk()) return false;

    if (deskId == desk.getID()) {
      return true;
    }

    return (pokerType == desk.getPokerType()) && (minBet.compareTo(desk.getMinBet()) == 0) && (maxBet.compareTo(desk.getMaxBet()) == 0) && (limitType == desk.getLimitType()) && (moneyType == desk.getMoneyType()) && (minAmount.compareTo(desk.getMinAmount()) == 0) && (minPlayers == desk.getPlayersCount());
  }

  public boolean containIgnoreMinPlayers(Desk desk)
  {
    if (desk.isPrivateDesk()) return false;

    if (deskId == desk.getID()) {
      return true;
    }

    return (pokerType == desk.getPokerType()) && (minBet.compareTo(desk.getMinBet()) == 0) && (maxBet.compareTo(desk.getMaxBet()) == 0) && (limitType == desk.getLimitType()) && (moneyType == desk.getMoneyType()) && (minAmount.compareTo(desk.getMinAmount()) == 0);
  }

  public void addWaiter(Waiter w)
  {
    synchronized (waiters) {
      Iterator iter = waiters.iterator();
      while (iter.hasNext()) {
        Waiter wl = (Waiter)iter.next();
        if (wl.equals(w))
        {
          return;
        }
      }
      waiters.add(w);
    }
  }

  public void removeWaitersByTimeOut()
  {
    synchronized (waiters) {
      Iterator iter = waiters.iterator();
      while (iter.hasNext()) {
        Waiter waiter = (Waiter)iter.next();
        if ((waiter.isJoining()) && 
          (new Date().getTime() - waiter.getJoinDate().getTime() > 160000L))
          iter.remove();
      }
    }
  }

  public void removeWaiter(Waiter w)
  {
    synchronized (waiters) {
      Iterator iter = waiters.iterator();
      while (iter.hasNext()) {
        Waiter w1 = (Waiter)iter.next();
        if (w1.equals(w)) {
          iter.remove();

          return;
        }
      }
    }
  }

  public String toXML() throws UnsupportedEncodingException
  {
    XMLDoc xmlDoc = new XMLDoc();
    XMLTag tag = xmlDoc.startTag("WAITINGREQUIREMENTS");
    tag.addParam("DESKID", getDeskId());
    tag.addParam("POKERTYPE", getPokerType());
    tag.addParam("MONEYTYPE", getMoneyType());
    tag.addParam("MINBET", getMinBet().floatValue());
    tag.addParam("MAXBET", getMaxBet().floatValue());
    tag.addParam("LIMITTYPE", getLimitType());
    tag.addParam("MINPLAYERS", getPlayersCount());

    String xml = xmlDoc.toString();
    xmlDoc.invalidate();

    return xml;
  }

  public Waiter getWaiter(Player player) {
    synchronized (waiters) {
      Iterator iter = waiters.iterator();
      while (iter.hasNext()) {
        Waiter w1 = (Waiter)iter.next();
        if (w1.getPlayer().getID() == player.getID()) {
          return w1;
        }
      }
    }

    return null;
  }

  public List getAvailableWaitersForDesk(Desk desk)
  {
    synchronized (waiters) {
      List list = new ArrayList(waiters.size());
      Iterator iter = waiters.iterator();
      while (iter.hasNext()) {
        Waiter w1 = (Waiter)iter.next();
        list.add(w1);
      }
      return list;
    }
  }

  public List getWaiters()
  {
    return waiters;
  }

  public int getWaitersQty() {
    synchronized (waiters) {
      return waiters.size();
    }
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public int getMinPlayers() {
    return minPlayers;
  }

  public void setMinPlayers(int minPlayers) {
    this.minPlayers = minPlayers;
  }

  public int getDeskId() {
    return deskId;
  }

  public void setDeskId(int deskId) {
    this.deskId = deskId;
  }

  public BigDecimal getMinBet() {
    return minBet;
  }

  public void setMinBet(BigDecimal minBet) {
    this.minBet = minBet;
  }

  public BigDecimal getMaxBet() {
    return maxBet;
  }

  public void setMaxBet(BigDecimal maxBet) {
    this.maxBet = maxBet;
  }

  public int getPokerType() {
    return pokerType;
  }

  public void setPokerType(int pokerType) {
    this.pokerType = pokerType;
  }

  public int getLimitType() {
    return limitType;
  }

  public void setLimitType(int limitType) {
    this.limitType = limitType;
  }

  public int getMoneyType() {
    return moneyType;
  }

  public void setMoneyType(int moneyType) {
    this.moneyType = moneyType;
  }

  public BigDecimal getMinAmount() {
    return minAmount;
  }

  public void setMinAmount(BigDecimal minAmount) {
    this.minAmount = minAmount;
  }

  public int getPlayersCount() {
    synchronized (waiters) {
      return waiters.size();
    }
  }
}