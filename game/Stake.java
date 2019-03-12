package game;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Stake
{
  private int type = 0;
  private boolean allIn = false;
  private Place place = null;
  private BigDecimal amount = new BigDecimal(0);
  private ArrayList parametersList = new ArrayList();
  public static final int ST_CALL = 1;
  public static final int ST_RAISE = 2;
  public static final int ST_CHECK = 3;
  public static final int ST_BET = 4;
  public static final int ST_FOLD = 5;
  public static final int ST_ANTE = 6;
  public static final int ST_SBLIND = 7;
  public static final int ST_BBLIND = 8;
  public static final int ST_DISCARD = 9;
  public static final int ST_BRING_IN = 10;
  public static final int ST_LOCKS = 11;
  public static final int ST_CHINESE = 12;
  public static final int ST_SKIPLOCKS = 13;

  public Stake(int type, Place place)
  {
    this.type = type;
    this.place = place;
  }

  public Stake(int type, Place place, ArrayList parametersList)
  {
    this(type, place);
    setParametersList(parametersList);
  }

  public Stake(int type, Place place, boolean allIn)
  {
    this.type = type;
    this.place = place;
    this.allIn = allIn;
  }

  public void setAsAllIn()
  {
    allIn = true;
  }

  public boolean isAllIn()
  {
    return allIn;
  }

  public int getType()
  {
    return type;
  }

  public Player getPlayer()
  {
    return place.getPlayer();
  }

  public void setPlace(Place place)
  {
    this.place = place;
  }

  public Place getPlace()
  {
    return place;
  }

  public BigDecimal getAmount()
  {
    return amount;
  }

  public void setAmount(BigDecimal amount)
  {
    this.amount = amount;
  }

  public ArrayList getParametersList() {
    return parametersList;
  }

  public void setParametersList(ArrayList parametersList) {
    this.parametersList = parametersList;
  }
}