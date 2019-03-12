package commands;

import game.Player;
import game.notes.NotesStorage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import server.Server;

public class CmdLoadNotes extends Command
{
  public boolean execute()
    throws IOException
  {
    String query = "select * from notes";
    boolean status;
    try
    {
      PreparedStatement statement = getDbConnection().prepareStatement(query);
      ResultSet result = statement.executeQuery();

      ArrayList playersList = Server.getPlayersList();

      while (result.next())
      {
        Player owner = Player.getPlayerByID(playersList, result.getInt("player_owner"));
        Player to = Player.getPlayerByID(playersList, result.getInt("player_to"));
        String note = result.getString("message");
        int rating = result.getInt("rating");
        boolean chat = result.getBoolean("chat");
        NotesStorage.loadNoteAndRatingForPlayer(owner, to, note, rating, chat);
      }

      result.close();
      statement.close();
      status = true;
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }

    return status;
  }
}