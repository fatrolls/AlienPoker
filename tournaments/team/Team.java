package tournaments.team;

import game.Player;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Team
{
  public static final String DB_PARAM_TEAM_ID = "team_id";
  public static final String DB_PARAM_TEAM_LEADER = "team_leader";
  public static final String DB_PARAM_TOURNAMENT_ID = "tour_id";
  public static final String DB_PARAM_TEAM_NAME = "team_name";
  public static final String DB_PARAM_TEAM_WON = "team_won";
  public static final String DB_PARAM_TEAM_LOOSE = "team_loose";
  public static final String DB_TEAM_TABLE = "teams";
  public static final String DB_TEAM_MEMBERS_TABLE = "team_players";
  public static final String DB_PARAM_TEAM1 = "team1";
  public static final String DB_PARAM_TEAM2 = "team2";
  public static final String DB_PARAM_TEAM_MEMBERS_PLAYER_ID = "player_id";
  public static final String DB_TEAM_HISTORY_TABLE = "teams_history";
  public static final String DB_TEAM_MATCHES_TABLE = "teams_matches";
  public static final String DB_PARAM_TEAM_HISTORY_STAGE = "stage";
  public static final String DB_PARAM_TEAM_HISTORY_OPPONENT_TEAM_ID = "opponent_team_id";
  public static final String DB_PARAM_TEAM_HISTORY_NUM = "num";
  public static final String DB_TEAM_PLAYERS_HISTORY_TABLE = "team_players_history";
  public static final String DB_TEAM_MATCHES_HISTORY_TABLE = "teams_matches_history";
  private int teamId;
  private volatile int totalPlayers;
  private final List members = new ArrayList();
  private Player teamLeader;
  private String name;
  private TeamTournament tournament = null;
  private volatile int wons = 0;
  private volatile int looses = 0;
  private int num;

  public Team(int teamId, String name, Player teamLeader, TeamTournament tournament, int num)
  {
    this.teamId = teamId;
    this.name = name;
    if (teamLeader == null) {
      throw new RuntimeException("teamLeader parameter is null");
    }
    this.teamLeader = teamLeader;
    this.tournament = tournament;
    if (tournament == null) {
      throw new RuntimeException("teamTournament parameter is null");
    }
    this.num = num;
    addMember(teamLeader);
  }

  public boolean equals(Object o) {
    if ((o instanceof Team)) {
      return ((Team)o).getTeamId() == teamId;
    }
    return false;
  }

  public int hashCode()
  {
    return teamId;
  }

  public boolean isTeamLeader(Player player) {
    return player.equals(teamLeader);
  }

  public boolean addMember(Player player) {
    synchronized (members) {
      if (hasPlayer(player)) {
        return false;
      }
      members.add(player);

      totalPlayers = members.size();
    }
    return true;
  }

  public boolean hasPlayer(Player player) {
    synchronized (members) {
      Iterator iter = members.iterator();
      while (iter.hasNext()) {
        Player pl = (Player)iter.next();
        if (pl.equals(player)) {
          return true;
        }
      }
      return false;
    }
  }

  public int getTeamId() {
    return teamId;
  }

  public int getTotalPlayers() {
    return totalPlayers;
  }

  public Player getTeamLeader() {
    return teamLeader;
  }

  public String getName() {
    return name;
  }

  public TeamTournament getTournament() {
    return tournament;
  }

  public void setTournament(TeamTournament tournament) {
    this.tournament = tournament;
  }

  public List getMembers() {
    return members;
  }

  public int getWons()
  {
    return wons;
  }

  public void incWons() {
    wons += 1;
  }

  public int getLooses() {
    return looses;
  }

  public void incLooses() {
    looses += 1;
  }

  public int getNum() {
    return num;
  }
}