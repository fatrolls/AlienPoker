package game.notes;

import game.Player;

public class PlayerNote
{
  public static final int RATING_UNKNOWN = 0;
  public static final int RATING_FRIEND = 1;
  public static final int RATING_DANGER_OPPONENT = 2;
  public static final int RATING_BAD_OPPONENT = 3;
  private Player player;
  private String note;
  private int rating;
  private boolean chat;

  public PlayerNote(Player player, String note)
  {
    this.player = player;
    this.note = note;
    rating = 0;
    chat = true;
  }

  public PlayerNote(Player player, int rating) {
    this.player = player;
    note = "";
    this.rating = rating;
    chat = true;
  }

  public PlayerNote(Player player, boolean chat) {
    this.player = player;
    note = "";
    rating = 0;
    this.chat = chat;
  }

  public Player getPlayer() {
    return player;
  }

  public String getNote() {
    return note;
  }

  public int getRating() {
    return rating;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public boolean isChat() {
    return chat;
  }

  public void setChat(boolean chat) {
    this.chat = chat;
  }
}