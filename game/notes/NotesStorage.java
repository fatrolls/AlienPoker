package game.notes;

import game.Player;
import java.util.HashMap;

public class NotesStorage
{
  private static final HashMap notesMap = new HashMap();

  private static void setNote(PlayerNote playerNote, Player owner, Player to, boolean update)
  {
    HashMap map;
    synchronized (notesMap) {
      map = (HashMap)notesMap.get(owner);
    }

    if (map == null) {
      map = new HashMap();
      map.put(to, playerNote);
      synchronized (notesMap) {
        notesMap.put(owner, map);
      }
    } else {
      synchronized (map) {
        map.put(to, playerNote);
      }
      synchronized (notesMap) {
        notesMap.put(owner, map);
      }

    }

    if (update)
      update(owner, playerNote);
  }

  public static void update(Player owner, PlayerNote playerNote)
  {
    new Thread(new NotesSaver(owner, playerNote)).start();
  }

  public static void setRatingForPlayer(Player owner, Player to, int rating) {
    PlayerNote playerNote = getNoteAndRatingForPlayer(owner, to);
    if (playerNote == null) {
      playerNote = new PlayerNote(to, rating);
      setNote(playerNote, owner, to, true);
    } else {
      playerNote.setRating(rating);
      update(owner, playerNote);
    }
  }

  public static void setNoteForPlayer(Player owner, Player to, String note) {
    PlayerNote playerNote = getNoteAndRatingForPlayer(owner, to);
    if (playerNote == null) {
      playerNote = new PlayerNote(to, note);
      setNote(playerNote, owner, to, true);
    } else {
      playerNote.setNote(note);
      update(owner, playerNote);
    }
  }

  public static void setChatForPlayer(Player owner, Player to, boolean chat) {
    PlayerNote playerNote = getNoteAndRatingForPlayer(owner, to);
    if (playerNote == null) {
      playerNote = new PlayerNote(to, chat);
      setNote(playerNote, owner, to, true);
    } else {
      playerNote.setChat(chat);
      update(owner, playerNote);
    }
  }

  public static void loadNoteAndRatingForPlayer(Player owner, Player to, String note, int rating, boolean chat)
  {
    PlayerNote playerNote = new PlayerNote(to, rating);
    playerNote.setNote(note);
    playerNote.setChat(chat);
    setNote(playerNote, owner, to, false);
  }

  public static PlayerNote getNoteAndRatingForPlayer(Player owner, Player to)
  {
    HashMap map;
    synchronized (notesMap) {
      map = (HashMap)notesMap.get(owner);
    }

    if (map == null) {
      return null;
    }
    synchronized (map) {
      PlayerNote note = (PlayerNote)map.get(to);
      if (note == null) {
        return null;
      }
      return note;
    }
  }
}