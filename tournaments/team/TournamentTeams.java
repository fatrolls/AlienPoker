package tournaments.team;

import game.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TournamentTeams
{
  public final List teams = new ArrayList();
  private TeamTournament teamTournament;

  public TournamentTeams(TeamTournament teamTournament)
  {
    this.teamTournament = teamTournament;
  }

  public boolean addTeam(Team team) {
    synchronized (teams) {
      if (hasTeam(team)) {
        return false;
      }
      teams.add(team);
      return true;
    }
  }

  public boolean hasTeam(Team team)
  {
    synchronized (teams) {
      Iterator iter = teams.iterator();
      while (iter.hasNext()) {
        if (team.equals(iter.next())) {
          return true;
        }
      }
      return false;
    }
  }

  public TeamTournament getTeamTournament() {
    return teamTournament;
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();
    synchronized (teams) {
      Iterator iter = teams.iterator();
      while (iter.hasNext()) {
        Team team = (Team)iter.next();
        buffer.append("ID: ").append(team.getTeamId()).append(" Name: ").append(team.getName()).append(" Num: ").append(team.getNum()).append(" Wons: ").append(team.getWons()).append(" Looses: ").append(team.getLooses()).append("\n");
      }
    }
    return buffer.toString();
  }

  public List getTeams()
  {
    return teams;
  }

  public Team getTeamById(int id) {
    synchronized (teams) {
      Iterator iter = teams.iterator();
      while (iter.hasNext()) {
        Team team = (Team)iter.next();
        if (team.getTeamId() == id) {
          return team;
        }
      }
      return null;
    }
  }

  public Team getPlayerTeam(Player player) {
    synchronized (teams) {
      Iterator iter = teams.iterator();
      while (iter.hasNext()) {
        Team team = (Team)iter.next();
        if (team.hasPlayer(player)) {
          return team;
        }
      }
      return null;
    }
  }

  public Team getBestTeam()
  {
    synchronized (teams) {
      if (teams.size() != 2) {
        throw new ArrayIndexOutOfBoundsException("Teams size must be exactly 2 ");
      }
      Team team1 = (Team)teams.get(0);
      Team team2 = (Team)teams.get(1);
      return team1.getWons() > team2.getWons() ? team1 : team2;
    }
  }

  public List getSortedByNumList()
  {
    ArrayList list;
    synchronized (teams) {
      list = new ArrayList(teams.size());
      list.addAll(teams);
    }
    Collections.sort(list, new TeamSortedByNumsComparator());

    return list;
  }

  public List getUnmodifiableTeamsList()
  {
    List teamsList;
    synchronized (teams) {
      teamsList = new ArrayList(teams.size());
      teamsList.addAll(teams);
    }
    return teamsList;
  }
}