package adminprocessors;

import game.Player;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import server.Server;

public class AdminUpdateProfileCommandProcessor
  implements AdminXMLResponse
{
  private static final String MSG_ERROR_PLAYER_NOT_FOUND = "Player not found";
  private static final String MSG_ERROR_LOGIN_NOT_UNIQUE = "Sorry, but this login is owned by someone else";
  private static final String MSG_ERROR_EMAIL_NOT_UNIQUE = "Sorry, but this email is owned by someone else";
  private static final String MSG_PROFILE_WAS_UPDATED = "Profile was successfully updated";

  public Hashtable update(String fname, String lname, String country, String city, String login, String password, String email, int playerId, int day, int month, int year)
  {
    Hashtable response = new Hashtable();
    ArrayList players = Server.getPlayersList();
    synchronized (players) {
      Player player = Player.getPlayerByID(players, playerId);
      if (player == null) {
        response.put("STATUS", "ERROR");
        response.put("RESPONSE", "Player not found");
      }
      else {
        Iterator iter = players.iterator();
        while (iter.hasNext()) {
          Player p = (Player)iter.next();
          if ((p.getLogin().equalsIgnoreCase(login.trim())) && (p.getID() != player.getID())) {
            response.put("STATUS", "ERROR");
            response.put("RESPONSE", "Sorry, but this login is owned by someone else");
            return response;
          }if ((p.getEmail().equalsIgnoreCase(email.trim())) && (p.getID() != player.getID())) {
            response.put("STATUS", "ERROR");
            response.put("RESPONSE", "Sorry, but this email is owned by someone else");
            return response;
          }

        }

        if (!player.getLogin().equalsIgnoreCase(login.trim())) {
          player.setLogin(login.trim());
        }

        if (!player.getEmail().equalsIgnoreCase(email.trim())) {
          player.setEmail(email.trim());
        }

        player.setFirstName(fname.trim());
        player.setLastName(lname.trim());
        player.setCountry(country.trim());
        player.setCity(city.trim());
        player.setPassword(password.trim());
        player.setPassword(password.trim());

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.set(year, month - 1, day);
        player.setBirthday(calendar);

        response.put("STATUS", "OK");
        response.put("RESPONSE", "Profile was successfully updated");
      }

    }

    return response;
  }
}