package tournaments;

import game.Player;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class TournamentWinners
{
  private Tournament tournament;
  private final ArrayList players = new ArrayList();
  private int playersCount = 0;
  private int currentPlace = 0;

  public TournamentWinners(Tournament tournament) {
    this.tournament = tournament;
  }

  public boolean hasWinner(Player player) {
    synchronized (players) {
      int size = players.size();
      for (int i = 0; i < size; i++) {
        WinnerPlace p = (WinnerPlace)players.get(i);
        if (p.getPlayer().getID() == player.getID()) {
          return true;
        }
      }
    }
    return false;
  }

  public WinnerPlace getWinnerPlace(Player player) {
    synchronized (players) {
      int size = players.size();
      for (int i = 0; i < size; i++) {
        WinnerPlace p = (WinnerPlace)players.get(i);
        if (p.getPlayer().getID() == player.getID()) {
          return p;
        }
      }
    }
    return null;
  }

  public ArrayList getWinnerByPlace(int place)
  {
    ArrayList list = new ArrayList();
    synchronized (players) {
      int size = players.size();
      for (int i = 0; i < size; i++) {
        WinnerPlace p = (WinnerPlace)players.get(i);
        if (p.getPlace() == place) {
          list.add(p.getPlayer());
        }
      }
    }
    return list;
  }

  private synchronized void updatePlayersCount() {
    if (playersCount == 0)
      synchronized (tournament.getPlayersList()) {
        playersCount = tournament.getPlayersList().size();
        currentPlace = (playersCount + 1);
      }
  }

  public void addWinner(Player player)
  {
    updatePlayersCount();
    synchronized (players) {
      if (!hasWinner(player))
        synchronized (this) {
          currentPlace -= 1;
          players.add(new WinnerPlace(player, currentPlace));
        }
    }
  }

  private void addWinner(Player player, int currentPlace)
  {
    updatePlayersCount();
    synchronized (players) {
      if (!hasWinner(player))
        synchronized (this) {
          players.add(new WinnerPlace(player, currentPlace));
        }
    }
  }

  public void addWinners(HashMap eliminated)
  {
    ArrayList list = new ArrayList(eliminated.size());
    Iterator iter = eliminated.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry entry = (Map.Entry)iter.next();
      list.add(new PlayerAndAmount((Player)entry.getKey(), (BigDecimal)entry.getValue()));
    }
    Collections.sort(list, new Comparator() {
      public int compare(Object o1, Object o2) {
        return ((TournamentWinners.PlayerAndAmount)o1).getAmount().compareTo(((TournamentWinners.PlayerAndAmount)o2).getAmount()) * -1;
      }
    });
    int size = list.size();
    ArrayList winnersList = new ArrayList();
    PlayerAndAmount oldPlayerAndAmount = null;
    for (int i = 0; i < size; i++) {
      PlayerAndAmount playerAndAmount = (PlayerAndAmount)list.get(i);
      if (oldPlayerAndAmount == null) {
        oldPlayerAndAmount = playerAndAmount;
        winnersList.add(playerAndAmount.getPlayer());
      } else {
        if (oldPlayerAndAmount.getAmount().compareTo(playerAndAmount.getAmount()) == 0) {
          winnersList.add(playerAndAmount.getPlayer());
        } else {
          addWinners(winnersList);
          winnersList.clear();
          winnersList.add(playerAndAmount.getPlayer());
        }
        oldPlayerAndAmount = playerAndAmount;
      }
    }
    addWinners(winnersList);
  }

  public void addWinners(ArrayList list) {
    if (list.size() > 0) {
      updatePlayersCount();
      currentPlace -= list.size();
      synchronized (this) {
        Iterator iter = list.iterator();
        while (iter.hasNext()) {
          Player player = (Player)iter.next();
          addWinner(player, currentPlace);
        }
      }
    }
  }

  public ArrayList getWinnersList() {
    return players;
  }

  public Tournament getTournament()
  {
    return tournament;
  }

  public class WinnerPlace
  {
    private Player player;
    private int place;

    protected WinnerPlace(Player player, int place)
    {
      this.player = player;
      this.place = place;
    }

    public Player getPlayer() {
      return player;
    }

    public int getPlace() {
      return place;
    }
  }

  private class PlayerAndAmount
  {
    private Player player;
    private BigDecimal amount;

    public PlayerAndAmount(Player player, BigDecimal amount)
    {
      this.player = player;
      this.amount = amount;
    }

    public Player getPlayer() {
      return player;
    }

    public BigDecimal getAmount() {
      return amount;
    }
  }
}