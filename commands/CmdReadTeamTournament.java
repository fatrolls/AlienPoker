package commands;

import game.Player;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import server.Server;
import tournaments.Tournament;
import tournaments.TournamentFactory;
import tournaments.team.Team;
import tournaments.team.TeamTournament;
import tournaments.team.TournamentTeams;
import utils.CommonLogger;

public class CmdReadTeamTournament extends Command
{
  private final List tournaments;
  private TeamTournament finalTeamTournament = null;
  private int tourId;
  public static final String SQL_TEAM_SELECT = "select distinct " + "teams" + ".*, if(tm1." + "num" + " is null, tm2." + "num" + ", tm1.num) as " + "num" + " from " + "teams" + " " + " left join " + "teams_matches" + " as tm1 on " + " " + "teams" + "." + "team_id" + " = tm1." + "team2" + " " + " and " + "teams" + "." + "tour_id" + " = tm1." + "tour_id" + " left join " + "teams_matches" + " as tm2 on " + " " + "teams" + "." + "team_id" + " = tm2." + "team1" + " " + " and " + "teams" + "." + "tour_id" + " = tm2." + "tour_id" + " where " + "teams" + "." + "tour_id" + " = ? ";
  public static final String SQL_TEAM_MEMBERS_SELECT = "select * from team_players where team_id = ? ";
  public static final String SQL_TEAM_TOURNAMENT_SELECT = "select * from " + "tournaments" + " where " + "tour_type" + " = " + 4 + " and " + "tour_id" + " = ? ";

  public CmdReadTeamTournament(List tournaments, int tourId)
  {
    this.tournaments = tournaments;
    this.tourId = tourId;
  }

  public boolean execute() throws IOException
  {
    PreparedStatement statement = null;
    boolean status = false;
    try
    {
      statement = getDbConnection().prepareStatement(SQL_TEAM_TOURNAMENT_SELECT);
      statement.setInt(1, tourId);
      ResultSet result = statement.executeQuery();

      if (result.next())
      {
        int freeRoll = result.getInt("tour_is_freeroll");
        BigDecimal freeRollPool = new BigDecimal(result.getFloat("tour_freeroll_pool"));

        Tournament tournament = TournamentFactory.createTournament(result.getInt("tour_id"), result.getString("tour_name"), result.getInt("tour_type"), result.getInt("tour_sub_type"), result.getInt("tour_poker"), result.getInt("tour_limit"), result.getInt("tour_money"), new BigDecimal(result.getFloat("tour_beg_amount")), new BigDecimal(result.getFloat("tour_max_bet")), new BigDecimal(result.getFloat("tour_min_bet")), new Date(result.getTimestamp("tour_date").getTime()), new Date(result.getTimestamp("tour_reg_start").getTime()), new BigDecimal(result.getDouble("tour_buy_in")), result.getInt("tour_level_duration"), result.getLong("tour_time_on_level"), result.getLong("tour_break_period"), result.getLong("tour_break_length"), result.getInt("tour_max_at_table"), result.getInt("tour_re_buys"), result.getInt("tour_addons"), new BigDecimal(result.getFloat("tour_addons_amount")), new BigDecimal(result.getFloat("tour_rebuys_amount")), new BigDecimal(result.getFloat("tour_fee")), result.getInt("tour_min_pls_to_start"), result.getInt("tour_speed"), result.getInt("tour_teams_qty"), result.getInt("tour_players_in_team"), freeRoll, freeRollPool);

        boolean launch = true;

        if ((tournament instanceof TeamTournament)) {
          TeamTournament teamTournament = (TeamTournament)tournament;
          int tournamentStatus = result.getInt("tour_status");
          teamTournament.setStatus(tournamentStatus);

          if (tournamentStatus == 0) {
            launch = false;

            if (CommonLogger.getLogger().isDebugEnabled()) {
              CommonLogger.getLogger().debug("Team tournaments " + teamTournament.getName() + " has announced status");
            }

          }
          else
          {
            if ((tournamentStatus == 3) || (tournamentStatus == 4))
            {
              launch = false;

              tournament.setStatus(tournamentStatus);

              if (CommonLogger.getLogger().isDebugEnabled()) {
                CommonLogger.getLogger().debug("Team tournaments " + teamTournament.getName() + " has " + (tournamentStatus == 3 ? "finished" : "cancelled status"));
              }

            }
            else if (CommonLogger.getLogger().isDebugEnabled()) {
              CommonLogger.getLogger().debug("Team tournaments " + teamTournament.getName() + " creating...");
            }

            CmdReadTournamentScheduler cmdReadTournamentScheduler = new CmdReadTournamentScheduler(teamTournament, result.getInt("tour_stage"));
            cmdReadTournamentScheduler.setDbConnection(getDbConnection());
            cmdReadTournamentScheduler.execute();

            TournamentTeams tourTeams = teamTournament.getTournamentTeams();
            ArrayList list = loadTeams(teamTournament);
            Iterator iter = list.iterator();
            while (iter.hasNext()) {
              Team teamToAdd = (Team)iter.next();
              if (!tourTeams.addTeam(teamToAdd)) {
                throw new RuntimeException("Cannot load team into tournament: Team : " + teamToAdd.getTeamId() + " tournament: " + teamTournament.getID());
              }
            }

            CmdReadTeamTournamentHistory cmdReadTeamTournamentHistory = new CmdReadTeamTournamentHistory(teamTournament);
            cmdReadTeamTournamentHistory.setDbConnection(getDbConnection());
            cmdReadTeamTournamentHistory.execute();

            teamTournament.setXMLHistory(cmdReadTeamTournamentHistory.getXml());
          }

        }

        if (launch) {
          new Thread(tournament).start();
        }

        if (tournament != null)
        {
          boolean found = false;
          synchronized (tournaments) {
            Iterator iter = tournaments.iterator();
            while (iter.hasNext()) {
              Tournament t = (Tournament)iter.next();
              if (t.getID() == tournament.getID()) {
                found = true;
                break;
              }
            }
          }

          if (!found) {
            synchronized (tournaments) {
              tournaments.add(tournament);
            }
          }
        }

      }

      result.close();
      status = true;
    }
    catch (SQLException e) {
      throw new RuntimeException(e);
    }
    catch (Exception e) {
      throw new RuntimeException(e);
    }
    finally {
      try {
        statement.close();
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }

    return status;
  }

  public ArrayList loadTeams(TeamTournament teamTournament) throws SQLException {
    ArrayList teams = new ArrayList();

    PreparedStatement statement = getDbConnection().prepareStatement(SQL_TEAM_SELECT);
    statement.setInt(1, teamTournament.getID());
    ResultSet result = statement.executeQuery();

    while (result.next()) {
      int teamId = result.getInt("team_id");
      int leader = result.getInt("team_leader");
      String name = result.getString("team_name");
      int num = result.getInt("num");

      Player teamLeader = Player.getPlayerByID(Server.getPlayersList(), leader);
      if (teamLeader == null) {
        throw new RuntimeException("Cannot find teamLeader: id = " + leader);
      }

      Team team = new Team(teamId, name, teamLeader, teamTournament, num);
      loadMembers(team);
      if (team.getMembers().size() != teamTournament.getPlayersInTeam()) {
        throw new RuntimeException("Players qty in " + team.getName() + " team is abnormal (" + team.getMembers().size() + " : need " + teamTournament.getPlayersInTeam() + ")");
      }
      teams.add(team);
    }

    result.close();
    statement.close();

    return teams;
  }

  public void loadMembers(Team team)
    throws SQLException
  {
    PreparedStatement statement = getDbConnection().prepareStatement("select * from team_players where team_id = ? ");
    statement.setInt(1, team.getTeamId());
    ResultSet result = statement.executeQuery();

    while (result.next()) {
      int playerId = result.getInt("player_id");

      Player player = Player.getPlayerByID(Server.getPlayersList(), playerId);
      if (player == null) {
        throw new RuntimeException("Cannot find player with id = " + playerId);
      }

      team.addMember(player);
    }

    result.close();
    statement.close();
  }

  public TeamTournament getFinalTeamTournament() {
    return finalTeamTournament;
  }
}