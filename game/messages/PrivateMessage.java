package game.messages;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class PrivateMessage extends StateMessage
{
  private BigDecimal bet = new BigDecimal(0);
  private BigDecimal call = new BigDecimal(0);
  private BigDecimal raise = new BigDecimal(0);
  private BigDecimal max = new BigDecimal(0);
  private BigDecimal bringIn = new BigDecimal(0);
  private boolean check = false;
  private boolean isAllIn = false;

  public PrivateMessage(int code, int remainingTime)
  {
    super(0, code);
    setRemainingTime(remainingTime);
  }

  public PrivateMessage(int id, int code, int remainingTime)
  {
    super(id, code);
    setRemainingTime(remainingTime);
  }

  private PrivateMessage(int code, int remainingTime, boolean check, BigDecimal bet, BigDecimal call, BigDecimal raise, BigDecimal max)
  {
    super(0, code);
    setRemainingTime(remainingTime);

    this.check = check;
    this.bet = bet;
    this.call = call;
    this.raise = raise;
    this.max = max;
  }

  public PrivateMessage(int code, int remainingTime, boolean check, BigDecimal bet, BigDecimal call, BigDecimal raise, BigDecimal bringIn, BigDecimal max)
  {
    this(code, remainingTime, check, bet, call, raise, max);

    this.bringIn = bringIn;
  }

  public PrivateMessage(int code, int remainingTime, boolean check, BigDecimal bet, BigDecimal call, BigDecimal raise, BigDecimal max, boolean isAllIn)
  {
    this(code, remainingTime, check, bet, call, raise, max);
    this.isAllIn = isAllIn;
  }

  public PrivateMessage(int code, int remainingTime, boolean check, BigDecimal bet, BigDecimal call, BigDecimal raise, BigDecimal max, boolean isAllIn, BigDecimal bringIn)
  {
    this(code, remainingTime, check, bet, call, raise, max, isAllIn);

    this.bringIn = bringIn;
  }

  public String toXML()
    throws UnsupportedEncodingException
  {
    XMLDoc doc = new XMLDoc();
    XMLTag tag = doc.startTag("M");
    tag.addParam("ID", getID());
    tag.addParam("CODE", getCode());
    tag.addParam("TIME", getRemainingTime());

    if ((getCode() == 15) || (getCode() == 21)) {
      if ((max.floatValue() > 0.0F) && ((bet.floatValue() > 0.0F) || (raise.floatValue() > 0.0F)))
        tag.addParam("MAX", max.floatValue());
      else {
        tag.addParam("MAX", "");
      }
    }

    if (bet.floatValue() == 0.0F) {
      tag.addParam("BET", "");
    }
    else {
      tag.addParam("BET", bet.floatValue());
    }

    if (call.floatValue() == 0.0F) {
      tag.addParam("CALL", "");
    }
    else {
      tag.addParam("CALL", call.floatValue());
    }

    if (raise.floatValue() == 0.0F) {
      tag.addParam("RAISE", "");
    }
    else {
      tag.addParam("RAISE", raise.floatValue());
    }

    if (bringIn.floatValue() > 0.0F) {
      tag.addParam("BRINGIN", bringIn.floatValue());
    }

    if (check) {
      tag.addParam("CHECK", 1);
    }
    else {
      tag.addParam("CHECK", 0);
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
}