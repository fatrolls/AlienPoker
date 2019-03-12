package wardobe;

import java.math.BigDecimal;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class WardobeItem
{
  public static final String DB_PARAM_TABLE = "wardobes";
  public static final String DB_PARAM_PLAYER_TABLE = "player_wardobes";
  public static final String DB_PARAM_ID = "id";
  public static final String DB_PARAM_NAME = "name";
  public static final String DB_PARAM_TITLE = "title";
  public static final String DB_PARAM_PRICE = "price";
  public static final String DB_PARAM_GENDER = "gender";
  public static final String DB_PARAM_TYPE = "type";
  public static final String DB_PARAM_STATE = "state";
  private int id;
  private String name;
  private BigDecimal price;
  private WardobeState state;
  private String title;
  private int gender;
  private WardobeType type;

  public int getId()
  {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public WardobeState getState() {
    return state;
  }

  public void setState(WardobeState state) {
    this.state = state;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getGender() {
    return gender;
  }

  public void setGender(int gender) {
    this.gender = gender;
  }

  public WardobeType getType() {
    return type;
  }

  public void setType(WardobeType type) {
    this.type = type;
  }

  public String toXML() {
    XMLDoc doc = new XMLDoc();
    XMLTag tag = doc.startTag("item");
    tag.addParam("name", name);
    tag.addParam("price", price.toString());
    tag.addParam("state", state.getState());
    tag.addParam("title", title);
    String xml = doc.toString();
    doc.invalidate();
    return xml;
  }
}