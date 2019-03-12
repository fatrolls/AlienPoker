package game;

import utils.xml.XMLTag;

public class Card
{
  public static final int MT_CLUBS = 1;
  public static final int MP_SPADES = 2;
  public static final int MC_HEARTS = 3;
  public static final int MB_DIAMONDS = 4;
  public static final int TWO = 2;
  public static final int THREE = 3;
  public static final int FOUR = 4;
  public static final int FIVE = 5;
  public static final int SIX = 6;
  public static final int SEVEN = 7;
  public static final int EIGHT = 8;
  public static final int NINE = 9;
  public static final int TEN = 10;
  public static final int JACK = 11;
  public static final int QUEEN = 12;
  public static final int KING = 13;
  public static final int ACE = 14;
  public static final int JOKER = 15;
  private int suite;
  private int value;
  public static final String OUT_PARAM_CARD = "CARD";
  public static final String OUT_PARAM_SUITE = "SUITE";
  public static final String OUT_PARAM_VALUE = "VALUE";

  public Card(int suite, int value)
  {
    this.suite = suite;
    this.value = value;
  }

  public Card(int value) {
    this.value = value;
  }

  public int getSuite() {
    return suite;
  }

  public int getValue() {
    return value;
  }

  public XMLTag toXMLTag() {
    XMLTag tag = new XMLTag("CARD");
    tag.addParam("SUITE", getSuite());
    tag.addParam("VALUE", getValue());

    return tag;
  }

  public int hashCode() {
    return (10 + value) * 10 + (100 + suite) * 1000;
  }

  public boolean equals(Object o) {
    Card card = (Card)o;

    return (card.getSuite() == getSuite()) && (card.getValue() == getValue());
  }

  public static boolean isValidCard(int suit, int value)
  {
    return ((suit == 1) || (suit == 2) || (suit == 3) || (suit == 4)) && 
      (value >= 2) && (value <= 14);
  }
}