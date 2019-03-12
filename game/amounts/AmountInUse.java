package game.amounts;

import game.Desk;
import game.Place;
import game.Player;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import tournaments.Tournament;

public class AmountInUse
{
  private static final BigDecimal ZERO_BIGDECIMAL = new BigDecimal(0).setScale(2, 5);
  private Player player;
  private final HashMap desksMap = new HashMap();
  private final HashMap tournamentsMap = new HashMap();

  private BigDecimal totalAmount = ZERO_BIGDECIMAL;

  public AmountInUse(Player player) {
    this.player = player;
  }

  public Player getPlayer() {
    return player;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public void recordDeskAmount(Desk desk) {
    Place place = desk.getPlayerPlace(player);

    if (place != null)
    {
      synchronized (desksMap) {
        desksMap.put(desk, place.getAmount());
      }
    }
    else
    {
      synchronized (desksMap) {
        desksMap.remove(desk);
      }
    }

    recalculateAmount();
  }

  public void recordTournamentAmount(Tournament tournament) {
    synchronized (tournamentsMap) {
      tournamentsMap.put(tournament, tournament.getBuyIn().add(tournament.getFee()));
    }

    recalculateAmount();
  }

  public void deleteTournamentRecord(Tournament tournament) {
    synchronized (tournamentsMap) {
      if (tournamentsMap.containsKey(tournament)) {
        tournamentsMap.remove(tournament);
      }
    }

    recalculateAmount();
  }

  public synchronized void recalculateAmount()
  {
    BigDecimal sum = ZERO_BIGDECIMAL;

    synchronized (desksMap) {
      Iterator iter = desksMap.values().iterator();
      while (iter.hasNext()) {
        BigDecimal value = (BigDecimal)iter.next();
        sum = sum.add(value);
      }
    }

    synchronized (tournamentsMap) {
      Iterator iter = tournamentsMap.values().iterator();
      while (iter.hasNext()) {
        BigDecimal value = (BigDecimal)iter.next();
        sum = sum.add(value);
      }
    }

    totalAmount = sum.setScale(2, 5);
  }

  public BigDecimal getTotalAmount()
  {
    return totalAmount;
  }
}