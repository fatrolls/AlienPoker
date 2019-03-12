package game.messages;

import server.XMLFormatable;

public abstract class StateMessage
  implements XMLFormatable
{
  public static final String TAG_NAME_STATUS_MESSAGE = "M";
  public static final String PARAM_NAME_MESSAGE_ID = "ID";
  public static final String PARAM_NAME_MESSAGE_CODE = "CODE";
  public static final String PARAM_NAME_MESSAGE_TIME = "TIME";
  public static final String PARAM_NAME_BET = "BET";
  public static final String PARAM_NAME_CALL = "CALL";
  public static final String PARAM_NAME_RAISE = "RAISE";
  public static final String PARAM_NAME_CHECK = "CHECK";
  public static final String PARAM_NAME_ALL_IN = "ALLIN";
  public static final String PARAM_NAME_MAX_STAKE = "MAX";
  public static final String PARAM_NAME_BRINGIN = "BRINGIN";
  private int ID;
  private int code;
  private int remainingTime;

  public int getType()
  {
    return 0;
  }

  public StateMessage(int ID, int code)
  {
    this.ID = ID;
    this.code = code;
  }

  public int getID()
  {
    return ID;
  }

  public void setID(int ID)
  {
    this.ID = ID;
  }

  public void setCode(int code)
  {
    this.code = code;
  }

  public int getCode()
  {
    return code;
  }

  public void setRemainingTime(int remainingTime)
  {
    this.remainingTime = remainingTime;
  }

  public int getRemainingTime()
  {
    return remainingTime;
  }
  public String toString() {
    return "class=" + getClass() + ", remainingTime=" + remainingTime + ", code=" + code + ", ID=" + ID;
  }
}