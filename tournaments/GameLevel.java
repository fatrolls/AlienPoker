package tournaments;

import java.math.BigDecimal;

public class GameLevel
{
  private static final BigDecimal DEFAULT_VALUE = new BigDecimal(0);
  private int level = 1;
  private int games = 1;
  private BigDecimal minBet = DEFAULT_VALUE;
  private BigDecimal maxBet = DEFAULT_VALUE;
  private BigDecimal smallBlind = DEFAULT_VALUE;
  private BigDecimal bigBlind = DEFAULT_VALUE;
  private BigDecimal ante = DEFAULT_VALUE;
  private BigDecimal bringIn = DEFAULT_VALUE;

  public GameLevel(int level, int games, BigDecimal minBet, BigDecimal maxBet, BigDecimal smallBlind, BigDecimal bigBlind) {
    this.level = level;
    this.games = games;
    this.minBet = minBet;
    this.maxBet = maxBet;
    this.smallBlind = smallBlind;
    this.bigBlind = bigBlind;
  }

  public GameLevel(int level, int games, BigDecimal minBet, BigDecimal maxBet) {
    this.level = level;
    this.games = games;
    this.minBet = minBet;
    this.maxBet = maxBet;
  }

  public String toString() {
    return "Level " + level + " Game " + games + " MinBet " + minBet.floatValue() + " MaxBet " + maxBet.floatValue() + " SmallBlind " + smallBlind.floatValue() + " BigBlind " + bigBlind + " Ante " + ante.floatValue() + " BringIn " + bringIn;
  }

  public int hashCode() {
    return level * 100 + games;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public int getGames() {
    return games;
  }

  public void setGames(int games) {
    this.games = games;
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

  public BigDecimal getSmallBlind() {
    return smallBlind;
  }

  public void setSmallBlind(BigDecimal smallBlind) {
    this.smallBlind = smallBlind;
  }

  public BigDecimal getBigBlind() {
    return bigBlind;
  }

  public void setBigBlind(BigDecimal bigBlind) {
    this.bigBlind = bigBlind;
  }

  public BigDecimal getAnte() {
    return ante;
  }

  public void setAnte(BigDecimal ante) {
    this.ante = ante;
  }

  public BigDecimal getBringIn() {
    return bringIn;
  }

  public void setBringIn(BigDecimal bringIn) {
    this.bringIn = bringIn;
  }
}