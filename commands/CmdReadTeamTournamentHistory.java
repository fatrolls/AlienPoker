package commands;

import game.Player;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.log4j.Logger;
import server.Server;
import tournaments.team.Team;
import tournaments.team.TeamTournament;
import utils.CommonLogger;
import utils.xml.XMLTag;

public class CmdReadTeamTournamentHistory extends Command
{
  private final TeamTournament tournament;
  private TeamTournament finalTeamTournament = null;

  private Player unknownPlayer = Player.getUnknownPlayer();

  public static final String SQL_TEAM_HISTORY_SELECT = "select " + "teams_history" + ".*," + "teams_matches_history" + "." + "num" + " " + " from " + "teams_history" + " left join " + "teams_matches_history" + " on " + "teams_matches_history" + "." + "team_id" + " = " + "teams_history" + "." + "team_id" + " " + "and  " + "teams_matches_history" + "." + "tour_id" + " = " + "teams_history" + "." + "tour_id" + " where " + "teams_history" + "." + "tour_id" + " = ? order by " + "teams_matches_history" + "." + "num";
  public static final String SQL_TEAM_PLAYERS_HISTORY_SELECT = "select * from team_players_history where team_id = ? ";
  public static final String SQL_TEAM_MATCHES_HISTORY_SELECT = "select distinct stage, team_id, opponent_team_id, num from teams_matches_history where tour_id = ? order by stage, num";
  private String xml = "";

  public CmdReadTeamTournamentHistory(TeamTournament tournament)
  {
    this.tournament = tournament;
  }

  public String toXML()
  {
    return null;
  }

  public boolean execute()
    throws IOException
  {
    boolean status = false;

    LinkedHashMap teams = new LinkedHashMap();
    try
    {
      teams = loadTeams(tournament);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try
    {
      loadMembers(tournament, teams);
    } catch (SQLException e) {
      e.printStackTrace();
    }
    try
    {
      loadTeamsStages(tournament, teams);
    } catch (SQLException e) {
      e.printStackTrace();
    }

    return status;
  }

  public LinkedHashMap loadTeams(TeamTournament teamTournament) throws SQLException {
    LinkedHashMap teams = new LinkedHashMap();

    PreparedStatement statement = getDbConnection().prepareStatement(SQL_TEAM_HISTORY_SELECT);

    statement.setInt(1, teamTournament.getID());
    ResultSet result = statement.executeQuery();

    while (result.next()) {
      int teamId = result.getInt("team_id");
      int leader = result.getInt("team_leader");
      String name = result.getString("team_name");
      int won = result.getInt("team_won");
      int loose = result.getInt("team_loose");
      int num = result.getInt("num");
      int stage = result.getInt("stage");

      Player teamLeader = Player.getPlayerByID(Server.getPlayersList(), leader);
      if (teamLeader == null) {
        teamLeader = unknownPlayer;
        CommonLogger.getLogger().warn(CmdReadTeamTournamentHistory.class.getName() + " cannot find player with id: " + leader);
      }

      Team team = new Team(teamId, name, teamLeader, teamTournament, num);
      for (int i = 0; i < won; i++) {
        team.incWons();
      }
      for (int i = 0; i < loose; i++) {
        team.incLooses();
      }

      if (teams.containsKey(new Integer(team.getTeamId())))
      {
        LinkedHashMap list = (LinkedHashMap)teams.get(new Integer(team.getTeamId()));
        list.put(new Integer(stage), team);
      } else {
        LinkedHashMap list = new LinkedHashMap();
        list.put(new Integer(stage), team);
        teams.put(new Integer(team.getTeamId()), list);
      }

    }

    result.close();
    statement.close();

    return teams;
  }

  public void loadMembers(TeamTournament teamTournament, HashMap teams)
    throws SQLException
  {
    PreparedStatement statement = getDbConnection().prepareStatement("select * from team_players_history where team_id = ? ");
    Iterator iter1 = teams.entrySet().iterator();
    while (iter1.hasNext()) {
      Map.Entry entry = (Map.Entry)iter1.next();

      LinkedHashMap teamsList = (LinkedHashMap)entry.getValue();
      Team team = (Team)teamsList.get(new Integer(1));

      statement.setInt(1, team.getTeamId());
      ResultSet result = statement.executeQuery();

      while (result.next()) {
        int playerId = result.getInt("player_id");

        Player player = Player.getPlayerByID(Server.getPlayersList(), playerId);
        if (player == null) {
          player = unknownPlayer;
          CommonLogger.getLogger().warn(CmdReadTeamTournamentHistory.class.getName() + " cannot find player with id: " + playerId);
        }

        team.addMember(player);
      }

      result.close();
    }

    statement.close();
  }

  public void loadTeamsStages(TeamTournament teamTournament, LinkedHashMap teamsList)
    throws SQLException
  {
    StringBuffer buffer = new StringBuffer();

    PreparedStatement statement = getDbConnection().prepareStatement("select distinct stage, team_id, opponent_team_id, num from teams_matches_history where tour_id = ? order by stage, num");
    statement.setInt(1, teamTournament.getID());
    ResultSet result = statement.executeQuery();

    int lastNum = -1;
    int lastStage = -1;

    XMLTag stage = null;
    HashMap map = new HashMap();

    while (result.next()) {
      int teamId = result.getInt("team_id");
      int opponentTeamId = result.getInt("opponent_team_id");
      int num = result.getInt("num");
      int stageNum = result.getInt("stage");

      if (num == lastNum)
      {
        continue;
      }
      if ((stage != null) && (lastStage != stageNum)) {
        buffer.append(stage.toString());
        stage.invalidate();
        stage = null;

        map.clear();
      }

      if (stage == null) {
        stage = new XMLTag("ST");
        stage.addParam("N", stageNum);
        stage.addParam("DT", (float)new Date().getTime());
      }

      if ((map.containsKey(new Integer(teamId))) || (map.containsKey(new Integer(opponentTeamId)))) {
        continue;
      }
      map.put(new Integer(teamId), new Integer(1));
      map.put(new Integer(opponentTeamId), new Integer(1));

      XMLTag teams = new XMLTag("TS");

      Team left = getTeamById(teamsList, teamId, stageNum);

      XMLTag team = new XMLTag("TM");
      team.addParam("ID", left.getTeamId());
      team.addParam("NM", left.getName());
      team.addParam("SC", left.getWons());
      teams.addNestedTag(team);

      Team right = getTeamById(teamsList, opponentTeamId, stageNum);

      team = new XMLTag("TM");
      team.addParam("ID", right.getTeamId());
      team.addParam("NM", right.getName());
      team.addParam("SC", right.getWons());
      teams.addNestedTag(team);

      stage.addNestedTag(teams);
      lastNum = num;
      lastStage = stageNum;
    }

    if (stage != null) {
      buffer.append(stage.toString());
      stage.invalidate();
    }

    result.close();

    statement.close();

    xml = buffer.toString();
  }

  public Team getTeamById(LinkedHashMap teams, int id, int stage)
  {
    LinkedHashMap teamsList = (LinkedHashMap)teams.get(new Integer(id));
    Team team = (Team)teamsList.get(new Integer(stage));

    if (team == null) {
      System.out.println("id: " + id + " stage: " + stage);
    }

    if (team.getTeamId() == id) {
      return team;
    }

    return null;
  }

  public TeamTournament getFinalTeamTournament()
  {
    return finalTeamTournament;
  }

  public String getXml() {
    return xml;
  }
}