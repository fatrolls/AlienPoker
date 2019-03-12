package game.messages;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class PublicMessage extends StateMessage
{
  public static final int STATE_INFO = 0;
  public static final int STATE_BEGIN = 1;
  public static final int STATE_END = 2;
  public static final String PARAM_NAME_MESSAGE_WHO = "WHO";
  public static final String PARAM_NAME_MESSAGE_STATUS = "STATUS";
  public static final String PARAM_NAME_MESSAGE_AMOUNT = "AMOUNT";
  private static final String PARAM_NAME_MESSAGE_NICK = "NICK";
  private int who = 0;
  private int status = 0;
  private BigDecimal amount = new BigDecimal(0);
  private boolean isAllIn = false;
  private String nick = "";

  public PublicMessage(int code)
  {
    super(0, code);
  }

  public PublicMessage(int id, int code)
  {
    super(id, code);
  }

  public PublicMessage(String nick, int code, int who, int status)
  {
    this(code, who, status);
    this.nick = nick;
  }

  public PublicMessage(int code, int who, int status)
  {
    this(0, code);

    this.who = who;
    this.status = status;
  }

  public PublicMessage(int id, int code, int who, int status)
  {
    this(id, code);

    this.who = who;
    this.status = status;
  }

  public PublicMessage(String nick, int code, int who, int status, BigDecimal amount)
  {
    this(code, who, status, amount);
    this.nick = nick;
  }

  public PublicMessage(int code, int who, int status, BigDecimal amount)
  {
    this(0, code);

    this.who = who;
    this.status = status;
    this.amount = amount;
  }

  public PublicMessage(String nick, int code, int who, int status, BigDecimal amount, boolean isAllIn)
  {
    this(code, who, status, amount, isAllIn);
    this.nick = nick;
  }

  public PublicMessage(int code, int who, int status, BigDecimal amount, boolean isAllIn)
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