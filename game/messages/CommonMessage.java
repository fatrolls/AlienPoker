package game.messages;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class CommonMessage extends StateMessage
{
  public static final int STATE_INFO = 0;
  public static final int STATE_BEGIN = 1;
  public static final int STATE_END = 2;
  public static final BigDecimal ZERO_BIG_DECIMAL = new BigDecimal(0);
  public static final String PARAM_NAME_MESSAGE_WHO = "WHO";
  public static final String PARAM_NAME_MESSAGE_STATUS = "STATUS";
  public static final String PARAM_NAME_MESSAGE_AMOUNT = "AMOUNT";
  private static final String PARAM_NAME_MESSAGE_NICK = "NICK";
  private static final String PARAM_NAME_MESSAGE_PLAYERID = "PID";
  private static final String PARAM_NAME_MESSAGE_ORDER = "ORDER";
  private static final String PARAM_NAME_MESSAGE_CURRENT_GAME_ID = "GID";
  private int who = 0;
  private int status = 0;
  private BigDecimal amount = ZERO_BIG_DECIMAL;
  private boolean isAllIn = false;
  private String nick = "";
  private String order = "";
  private int playerID = 0;
  private BigDecimal minBet = ZERO_BIG_DECIMAL;
  private BigDecimal maxBet = ZERO_BIG_DECIMAL;
  private BigDecimal ante = ZERO_BIG_DECIMAL;
  private BigDecimal bringIn = ZERO_BIG_DECIMAL;
  private int level = 0;
  private long currentGameId = 0L;

  public CommonMessage(int code)
  {
    super(0, code);
  }

  public CommonMessage(int code, long currentGameId)
  {
    super(0, code);
    this.currentGameId = currentGameId;
  }

  public CommonMessage(int id, int code)
  {
    super(id, code);
  }

  public CommonMessage(String nick, int code, int who, int status)
  {
    this(code, who, status);
    this.nick = nick;
  }

  public CommonMessage(int code, int who, int status)
  {
    this(0, code);

    this.who = who;
    this.status = status;
  }

  public CommonMessage(int code, BigDecimal minBet, BigDecimal maxBet, BigDecimal ante, BigDecimal bringIn, int level)
  {
    this(0, code);

    this.minBet = minBet;
    this.maxBet = maxBet;
    this.ante = ante;
    this.bringIn = bringIn;
    this.level = level;
  }

  public CommonMessage(int code, int who, String order)
  {
    this(code, who, 0);
    this.order = order;
  }

  public CommonMessage(int id, int code, int who, int status)
  {
    this(id, code);

    this.who = who;
    this.status = status;
  }

  public CommonMessage(String nick, int code, int who, int status, BigDecimal amount)
  {
    this(code, who, status, amount);
    this.nick = nick;
  }

  public CommonMessage(String nick, int code, int who, int status, BigDecimal amount, int playerID)
  {
    this(nick, code, who, status, amount);
    this.playerID = playerID;
  }

  public CommonMessage(int code, int who, int status, BigDecimal amount)
  {
    this(0, code);

    this.who = who;
    this.status = status;
    this.amount = amount;
  }

  public CommonMessage(String nick, int code, int who, int status, BigDecimal amount, boolean isAllIn)
  {
    this(code, who, status, amount, isAllIn);
    this.nick = nick;
  }

  public CommonMessage(int code, int who, int status, BigDecimal amount, boolean isAllIn)
  {
    this(code, who, status, amount);
    this.isAllIn = isAllIn;
  }

  public void setWho(int who)
  {
    this.who = who;
  }

  public int getWho()
  {
    return who;
  }

  public void setStatus(int status)
  {
    this.status = status;
  }

  public int getStatus()
  {
    return status;
  }

  public String toXML() throws UnsupportedEncodingException
  {
    XMLDoc doc = new XMLDoc();
    XMLTag tag = doc.startTag("M");
    tag.addParam("ID", getID());
    tag.addParam("CODE", getCode());
    tag.addParam("WHO", getWho());
    tag.addParam("AMOUNT", getAmount().floatValue());
    tag.addParam("STATUS", getStatus());
    tag.addParam("TIME", getRemainingTime());
    tag.addParam("NICK", getNick());
    if (playerID > 0) {
      tag.addParam("PID", playerID);
    }

    if (currentGameId > 0L) {
      tag.addParam("GID", "" + currentGameId);
    }

    if (level > 0) {
      tag.addParam("CLVL", level);
      tag.addParam("MAXBET", "" + maxBet.floatValue());
      tag.addParam("MINBET", "" + minBet.floatValue());
      tag.addParam("ANTE", "" + ante.floatValue());
      tag.addParam("BRINGIN", "" + bringIn.floatValue());
    }

    if (order.length() > 0) {
      tag.addParam("ORDER", order);
    }

    if (isAllIn) {
      tag.addParam("ALLIN", 1);
    }
    else {
      tag.addParam("ALLIN", 0);
    }

    String xml = doc.toString();
    doc.invalidate();

    return xml;
  }

  private BigDecimal getAmount()
  {
    return amount;
  }

  public String getNick() {
    return nick;
  }
}