package tournaments.team;

import game.Desk;
import game.Player;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TeamPlayersShuffler
{
  private List teams;
  private List desks;
  private BigDecimal deskAmount;

  public TeamPlayersShuffler(List teams, List desks, BigDecimal deskAmount)
  {
    synchronized (teams) {
      this.teams = new ArrayList(teams.size());
      this.teams.addAll(teams);
    }
    synchronized (desks) {
      this.desks = new ArrayList(desks.size());
      this.desks.addAll(desks);
    }
    this.deskAmount = deskAmount;
  }

  public List shuffle()
    throws ArithmeticException, IndexOutOfBoundsException
  {
    Collections.sort(teams, new TeamSortedByNumsComparator());
    List oppositeDesks = new ArrayList(desks.size());

    if (teams.size() == 0)
      throw new IndexOutOfBoundsException("Teams QTY must be non zero");
    if ((teams.size() == 0) || (teams.size() % 2 != 0)) {
      throw new IndexOutOfBoundsException("Teams QTY must be even");
    }

    Iterator iter = teams.iterator();
    Iterator desksIter = desks.iterator();
    while (iter.hasNext()) {
      Team team = (Team)iter.next();
      if (!iter.hasNext()) {
        throw new IndexOutOfBoundsException("Teams QTY must be even");
      }
      Team oppositeTeam = (Team)iter.next();

      if (team.getTotalPlayers() != oppositeTeam.getTotalPlayers()) {
        throw new ArithmeticException("Teams and oppisiteTeams players qty must be quals(" + team.getTotalPlayers() + " != " + oppositeTeam.getTotalPlayers() + ")");
      }

      List teamMembers = team.getMembers();
      List oppositeTeamMembers = oppositeTeam.getMembers();

      System.out.println("Team1: " + team.getName() + " Team2: " + oppositeTeam.getName());

      int size = teamMembers.size();
      for (int i = 0; i < size; i++) {
        Desk desk = (Desk)desksIter.next();

        Player player1 = (Player)teamMembers.get(i);
        Player player2 = (Player)oppositeTeamMembers.get(i);

        ArrayList playersList = new ArrayList(2);
        playersList.add(player1);
        playersList.add(player2);

        Collections.shuffle(playersList);

        desk.seatPlayer((Player)playersList.get(0), 1, deskAmount);
        desk.seatPlayer((Player)playersList.get(1), 2, deskAmount);

        OppositeDesk oppositeDesk = new OppositeDesk(desk, playersList);
        oppositeDesks.add(oppositeDesk);
      }
    }

    return oppositeDesks;
  }
}