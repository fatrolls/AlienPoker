package game;

import commands.safeupdaters.PlayerAmountUpdater;
import defaultvalues.DefaultValue;
import game.amounts.PlayerAmount;
import game.buddylist.BuddyList;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import utils.xml.XMLDoc;
import utils.xml.XMLParam;
import utils.xml.XMLTag;

public class Player
{
  public static final BigDecimal ZERO_DECIMAL = DefaultValue.ZERO_BIDECIMAL;
  public static final String DB_PLAYERS_TABLE = "users";
  public static final String DB_ID_FIELD = "user_id";
  public static final String DB_LOGIN_FIELD = "us_login";
  public static final String DB_PASSWORD_FIELD = "us_password";
  public static final String DB_AMOUNT_FIELD = "us_amount";
  public static final String DB_REAL_AMOUNT_FIELD = "us_real_amount";
  public static final String DB_CITY_FIELD = "us_city";
  public static final String DB_COUNTRY_FIELD = "us_country";
  public static final String DB_FNAME_FIELD = "us_fname";
  public static final String DB_LNAME_FIELD = "us_lname";
  public static final String DB_EMAIL_FIELD = "us_email";
  public static final String DB_ADDRESS_FIELD = "us_address";
  public static final String DB_REGISTER_DATE_FIELD = "us_reg_date";
  public static final String DB_FROZEN_AMOUNT_FIELD = "us_frozen_amount";
  public static final String DB_RATING_FIELD = "us_rating";
  public static final String DB_PHONE_FIELD = "us_phone";
  public static final String DB_STATE_FIELD = "us_state";
  public static final String DB_ZIP_FIELD = "us_zip";
  public static final String DB_REG_STATUS_FIELD = "us_reg_status";
  public static final String DB_PLAYER_STATUS_FIELD = "us_player_status";
  public static final String DB_AVATAR_FIELD = "us_avatar";
  public static final String DB_BIRTHDAY_FIELD = "us_birthday";
  public static final String DB_DEPOSIT_LIMIT = "us_deposit_limit";
  public static final String DB_HIDDEN = "us_hidden";
  public static final String DB_GENDER_FIELD = "us_gender";
  public static final String OUT_PARAM_TAG_NAME = "PLAYER";
  public static final String OUT_PARAM_ID = "ID";
  public static final String OUT_PARAM_LOGIN = "LOGIN";
  public static final String OUT_PARAM_CITY = "CITY";
  public static final String OUT_PARAM_AMOUNT = "AMOUNT";
  public static final String OUT_PARAM_COUNTRY = "COUNTRY";
  public static final String OUT_PARAM_FIRST_NAME = "FNAME";
  public static final String OUT_PARAM_LAST_NAME = "LNAME";
  public static final String OUT_PARAM_EMAIL = "EMAIL";
  public static final String OUT_PARAM_BIRTHD = "BIRTHD";
  public static final String OUT_PARAM_BIRTHM = "BIRTHM";
  public static final String OUT_PARAM_BIRTHY = "BIRTHY";
  public static final String OUT_PARAM_FREE_AMOUNT = "FREEAMOUNT";
  public static final String OUT_PARAM_REAL_AMOUNT = "REALAMOUNT";
  public static final String OUT_PARAM_IN_GAME_MONEY = "INGAME";
  public static final String OUT_PARAM_DEPOSIT_LIMIT = "DEPOSITL";
  public static final String OUT_PARAM_ADDRESS = "ADDRESS";
  public static final String OUT_PARAM_STATE = "STATE";
  public static final String OUT_PARAM_ZIP = "ZIP";
  public static final String OUT_PARAM_PHONE = "PHONE";
  public static final String OUT_PARAM_HIDDEN = "HIDD";
  public static final String OUT_PARAM_GENDER = "GENDER";
  private final BuddyList byddyList = new BuddyList(this);
  private String login;
  private String password;
  private int ID;
  private BigDecimal amount = ZERO_DECIMAL;
  private BigDecimal realAmount = ZERO_DECIMAL;
  private BigDecimal depositLimit = ZERO_DECIMAL;
  private int gender;
  private final PlayerAmount playerAmount = new PlayerAmount(this);

  private String city = "";
  private String country = "";
  private String avatar = "";
  private String firstName = "";
  private String lastName = "";
  private String email = "";
  private String address = "";
  private String phone = "";
  private String state = "";
  private String zip = "";
  private Calendar birthday = DefaultValue.DEFAULT_DATE;
  private boolean active = true;
  private boolean hideFromSearch = false;

  private BigDecimal dirtyPoints = DefaultValue.ZERO_BIDECIMAL;
  public static final String OUT_PARAM_IS_A_CLUBMEMBER = "ISCLUBMEMBER";
  public static final String OUT_PARAM_DIRTYPOINTS = "DPTS";
  public static final String OUT_PARAM_JOINED = "JTPC";

  public static Player getPlayerByLoginAndPass(ArrayList players, String login, String password)
  {
    Player player = null;

    synchronized (players) {
      Iterator it = players.iterator();
      while (it.hasNext()) {
        Player p = (Player)it.next();
        if ((p.getLogin().equals(login)) && (p.getPassword().equals(password))) {
          player = p;
          break;
        }
      }
      players.notifyAll();
    }

    return player;
  }

  public static Player searchPlayerByLogin(ArrayList players, String login, boolean ignoreCase)
  {
    Player player = null;

    synchronized (players) {
      Iterator it = players.iterator();
      while (it.hasNext()) {
        Player p = (Player)it.next();
        if (ignoreCase) {
          if (p.getLogin().equalsIgnoreCase(login)) {
            player = p;
            break;
          }
        }
        else if (p.getLogin().equals(login)) {
          player = p;
          break;
        }
      }

      players.notifyAll();
    }

    return player;
  }

  public static Player getPlayerByID(ArrayList players, int playerID)
  {
    Player player = null;
    synchronized (players) {
      Iterator it = players.iterator();
      while (it.hasNext()) {
        Player p = (Player)it.next();
        if (p.getID() == playerID) {
          player = p;
          break;
        }
      }
      players.notifyAll();
    }
    return player;
  }

  public int hashCode() {
    return getID();
  }

  public boolean equals(Object o) {
    if (o == null)
      return false;
    if ((o instanceof Player)) {
      return getID() == ((Player)o).getID();
    }
    return false;
  }

  public BuddyList getByddyList()
  {
    return byddyList;
  }

  public BigDecimal getDirtyPoints() {
    return dirtyPoints;
  }

  public void setDirtyPoints(BigDecimal dirtyPoints) {
    this.dirtyPoints = dirtyPoints;
  }

  public void decreaseAmount(BigDecimal value, int moneyType)
  {
    if (moneyType == 1)
      synchronized (this) {
        amount = amount.subtract(value).setScale(2, 5);
        updateDbAmount(amount, moneyType);
      }
    else if (moneyType == 0)
      synchronized (this) {
        realAmount = realAmount.subtract(value).setScale(2, 5);
        updateDbAmount(realAmount, moneyType);
      }
    else
      throw new RuntimeException("Player->decreaseAmount: moneyType != Game.MT_FUN : moneyType = " + moneyType);
  }

  private void updateDbAmount(BigDecimal amount, int moneyType)
  {
    new Thread(new PlayerAmountUpdater(this, amount, moneyType)).start();
  }

  public void increaseDirtyPoints(BigDecimal value) {
    synchronized (this) {
      dirtyPoints = dirtyPoints.add(value).setScale(2, 5);
    }
  }

  public void decreaseDirtyPoints(BigDecimal value) {
    synchronized (this) {
      dirtyPoints = dirtyPoints.subtract(value).setScale(2, 5);
    }
  }

  public void increaseAmount(BigDecimal value, int moneyType)
  {
    if (moneyType == 1)
      synchronized (this) {
        amount = amount.add(value).setScale(2, 5);
        updateDbAmount(amount, moneyType);
      }
    else if (moneyType == 0)
      synchronized (this) {
        realAmount = realAmount.add(value).setScale(2, 5);
        updateDbAmount(realAmount, moneyType);
      }
    else
      throw new RuntimeException("Player->decreaseAmount: moneyType != Game.MT_FUN : moneyType = " + moneyType);
  }

  public BigDecimal getFreeAmount()
  {
    return amount;
  }

  public BigDecimal getRealAmount() {
    return realAmount;
  }

  public void setRealAmount(BigDecimal realAmount) {
    this.realAmount = realAmount;
  }

  public String getCountry()
  {
    return country == null ? "" : country;
  }

  public String getCity()
  {
    return city == null ? "" : city;
  }

  public String getPassword()
  {
    return password;
  }

  public BigDecimal getAmount(int moneyType)
  {
    if (moneyType == 1)
      return amount;
    if (moneyType == 0) {
      return realAmount;
    }
    throw new RuntimeException("Player->getAmount: moneyType = " + moneyType);
  }

  public String getLogin()
  {
    return login;
  }

  public int getID()
  {
    return ID;
  }

  public void setID(int ID)
  {
    this.ID = ID;
  }

  public void setCountry(String country)
  {
    this.country = country;
  }

  public void setLogin(String login)
  {
    this.login = login;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public void setFreeAmount(BigDecimal amount)
  {
    this.amount = amount;
  }

  public void setCity(String city)
  {
    this.city = city;
  }

  public String toXML(ArrayList additionalParams, int moneyType)
    throws UnsupportedEncodingException
  {
    XMLDoc xmlDoc = new XMLDoc();
    XMLTag tag = xmlDoc.startTag("PLAYER");
    tag.addParam("ID", ID);
    tag.addParam("LOGIN", login);
    tag.addParam("CITY", city);
    tag.addParam("COUNTRY", country);
    tag.addParam("EMAIL", email);
    tag.addParam("FNAME", firstName);
    tag.addParam("LNAME", lastName);

    tag.addParam("REALAMOUNT", getAmount(moneyType).toString());
    tag.addParam("INGAME", getPlayerAmount().getInUseAmount(0).toString());
    tag.addParam("BIRTHY", birthday.get(1));
    tag.addParam("BIRTHM", birthday.get(2) + 1);
    tag.addParam("BIRTHD", birthday.get(5));
    tag.addParam("HIDD", hideFromSearch ? 1 : 0);
    tag.addParam("GENDER", gender);

    if (additionalParams != null) {
      Iterator it = additionalParams.iterator();
      while (it.hasNext()) {
        XMLParam xmlParam = (XMLParam)it.next();
        tag.addParam(xmlParam);
      }

    }

    String xml = xmlDoc.toString();
    xmlDoc.invalidate();

    return xml;
  }

  public String toXML(int moneyType) throws UnsupportedEncodingException
  {
    return toXML(null, moneyType);
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public PlayerAmount getPlayerAmount() {
    return playerAmount;
  }

  public Calendar getBirthday() {
    return birthday;
  }

  public void setBirthday(Calendar birthday) {
    this.birthday = birthday;
  }

  public BigDecimal getDepositLimit() {
    return depositLimit;
  }

  public void setDepositLimit(BigDecimal depositLimit) {
    this.depositLimit = depositLimit;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String toString() {
    return "Login: " + login + ". ID: " + ID;
  }

  public static Player getUnknownPlayer() {
    Player player = new Player();
    player.setID(0);
    player.setLogin("JohnDoe");
    player.setFirstName("John");
    player.setLastName("Doe");
    return player;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getZip() {
    return zip;
  }

  public void setZip(String zip) {
    this.zip = zip;
  }

  public boolean isHideFromSearch() {
    return hideFromSearch;
  }

  public void setHideFromSearch(boolean hideFromSearch) {
    this.hideFromSearch = hideFromSearch;
  }

  public int getGender() {
    return gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }
}