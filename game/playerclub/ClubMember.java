package game.playerclub;

import game.Player;
import java.math.BigDecimal;
import java.util.Date;

public class ClubMember
{
  public static final int RATING_GOLD = 1;
  public static final int RATING_SILVER = 2;
  public static final int RATING_BLUE = 3;
  public static final String DB_PARAM_TABLE = "players_club";
  public static final String DB_PARAM_ID = "id";
  public static final String DB_PARAM_USER_ID = "user_id";
  public static final String DB_PARAM_RATING = "rating";
  public static final String DB_PARAM_POINTS = "points";
  public static final String DB_PARAM_REG_DATE = "reg_date";
  private Player player;
  private int rating = 3;
  private Date regDate;

  public ClubMember(Player player, Date date)
  {
    this.player = player;
    regDate = date;
  }

  public int hashCode() {
    return player.getID();
  }

  public boolean equals(Object o) {
    if ((o instanceof ClubMember)) {
      return o.hashCode() == hashCode();
    }
    return false;
  }

  public int getId()
  {
    return player.getID();
  }

  public int getRating()
  {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public BigDecimal getPoints() {
    return player.getDirtyPoints();
  }

  public void loadPoints(BigDecimal points) {
    player.setDirtyPoints(points);
  }

  public Date getRegDate() {
    return regDate;
  }

  public void setRegDate(Date regDate) {
    this.regDate = regDate;
  }
}