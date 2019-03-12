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

public class CmdReadTournaments extends Command
{
  private ArrayList tournaments;
  public static final String DB_PARAM_ID = "tour_id";
  public static final String DB_PARAM_NAME = "tour_name";
  public static final String DB_PARAM_TYPE = "tour_type";
  public static final String DB_PARAM_SUB_TYPE = "tour_sub_type";
  public static final String DB_PARAM_DATE = "tour_date";
  public static final String DB_PARAM_REG_START = "tour_reg_start";
  public static final String DB_PARAM_POKER = "tour_poker";
  public static final String DB_PARAM_LIMIT = "tour_limit";
  public static final String DB_PARAM_MONEY = "tour_money";
  public static final String DB_PARAM_MAX_AT_TABLE = "tour_max_at_table";
  public static final String DB_PARAM_MIN_BET = "tour_min_bet";
  public static final String DB_PARAM_MAX_BET = "tour_max_bet";
  public static final String DB_PARAM_STATUS = "tour_status";
  public static final String DB_PARAM_REG_STATUS = "tour_reg_status";
  public static final String DB_PARAM_BUY_IN = "tour_buy_in";
  public static final String DB_PARAM_RE_BUYS = "tour_re_buys";
  public static final String DB_PARAM_ADDONS = "tour_addons";
  public static final String DB_PARAM_FEE = "tour_fee";
  public static final String DB_PARAM_LEVEL_DURATION = "tour_level_duration";
  public static final String DB_PARAM_BEG_AMOUNT = "tour_beg_amount";
  public static final String DB_PARAM_TIME_ON_LEVEL = "tour_time_on_level";
  public static final String DB_PARAM_BREAK_PERIOD = "tour_break_period";
  public static final String DB_PARAM_BREAK_LENGTH_TIME = "tour_break_length";
  public static final String DB_PARAM_ADDONS_AMOUNT = "tour_addons_amount";
  public static final String DB_PARAM_REBUYS_AMOUNT = "tour_rebuys_amount";
  public static final String DB_PARAM_TOUR_FEE = "tour_fee";
  public static final String DB_PARAM_MIN_PLAYERS_TO_START = "tour_min_pls_to_start";
  public static final String DB_PARAM_TOUR_SPEED = "tour_speed";
  public static final String DB_PARAM_TOUR_TEAMS_QTY = "tour_teams_qty";
  public static final String DB_PARAM_TOUR_PLAYERS_IN_TEAM = "tour_players_in_team";
  public static final String DB_PARAM_TOUR_STAGE = "tour_stage";
  public static final String DB_PARAM_TOUR_FREEROLL = "tour_is_freeroll";
  public static final String DB_PARAM_TOUR_FREEROLL_POOL = "tour_freeroll_pool";
  public static final String SQL_TEAM_MEMBERS_SELECT = "select * from team_players where team_id = ? ";
  public static final String DB_TABLE_TOURNAMENT = "tournaments";

  public CmdReadTournaments()
  {
    tournaments = new ArrayList();
  }

  public boolean execute()
    throws IOException
  {
    PreparedStatement localPreparedStatement = null;
    int i = 0;
    String str = "select * from tournaments where UNIX_TIMESTAMP(tour_date) > UNIX_TIMESTAMP()";
    try
    {
      localPreparedStatement = getDbConnection().prepareStatement(str);
      ResultSet localResultSet = localPreparedStatement.executeQuery();

      while (localResultSet.next())
      {
        int j = localResultSet.getInt("tour_is_freeroll");
        BigDecimal localBigDecimal = new BigDecimal(localResultSet.getFloat("tour_freeroll_pool"));
        Tournament localTournament = TournamentFactory.createTournament(localResultSet.getInt("tour_id"), localResultSet.getString("tour_name"), localResultSet.getInt("tour_type"), localResultSet.getInt("tour_sub_type"), localResultSet.getInt("tour_poker"), localResultSet.getInt("tour_limit"), localResultSet.getInt("tour_money"), new BigDecimal(localResultSet.getFloat("tour_beg_amount")), new BigDecimal(localResultSet.getFloat("tour_max_bet")), new BigDecimal(localResultSet.getFloat("tour_min_bet")), new Date(localResultSet.getTimestamp("tour_date").getTime()), new Date(localResultSet.getTimestamp("tour_reg_start").getTime()), new BigDecimal(localResultSet.getDouble("tour_buy_in")), localResultSet.getInt("tour_level_duration"), localResultSet.getLong("tour_time_on_level"), localResultSet.getLong("tour_break_period"), localResultSet.getLong("tour_break_length"), localResultSet.getInt("tour_max_at_table"), localResultSet.getInt("tour_re_buys"), localResultSet.getInt("tour_addons"), new BigDecimal(localResultSet.getFloat("tour_addons_amount")), new BigDecimal(localResultSet.getFloat("tour_rebuys_amount")), new BigDecimal(localResultSet.getFloat("tour_fee")), localResultSet.getInt("tour_min_pls_to_start"), localResultSet.getInt("tour_speed"), localResultSet.getInt("tour_teams_qty"), localResultSet.getInt("tour_players_in_team"), j, localBigDecimal);
        int k = 1;
        if ((localTournament instanceof TeamTournament))
        {
          TeamTournament localTeamTournament = (TeamTournament)localTournament;
          int n = localResultSet.getInt("tour_status");
          localTeamTournament.setStatus(n);
          if (n == 0)
          {
            k = 0;
            if (CommonLogger.getLogger().isDebugEnabled())
              CommonLogger.getLogger().debug("Team tournaments " + localTeamTournament.getName() + " has announced status");
          }
          else {
            if ((n == 3) || (n == 4))
            {
              k = 0;
              localTournament.setStatus(n);
              if (CommonLogger.getLogger().isDebugEnabled())
                CommonLogger.getLogger().debug("Team tournaments " + localTeamTournament.getName() + " has " + (n != 3 ? "cancelled status" : "finished"));
            }
            else if (CommonLogger.getLogger().isDebugEnabled()) {
              CommonLogger.getLogger().debug("Team tournaments " + localTeamTournament.getName() + " creating...");
            }CmdReadTournamentScheduler localCmdReadTournamentScheduler = new CmdReadTournamentScheduler(localTeamTournament, localResultSet.getInt("tour_stage"));
            localCmdReadTournamentScheduler.setDbConnection(getDbConnection());
            localCmdReadTournamentScheduler.execute();
            TournamentTeams localTournamentTeams = localTeamTournament.getTournamentTeams();
            ArrayList localArrayList = loadTeams(localTeamTournament);
            for (Object localObject = localArrayList.iterator(); ((Iterator)localObject).hasNext(); )
            {
              Team localTeam = (Team)((Iterator)localObject).next();
              if (!localTournamentTeams.addTeam(localTeam)) {
                throw new RuntimeException("Cannot load team into tournament: Team : " + localTeam.getTeamId() + " tournament: " + localTeamTournament.getID());
              }
            }
            localObject = new CmdReadTeamTournamentHistory(localTeamTournament);
            ((CmdReadTeamTournamentHistory)localObject).setDbConnection(getDbConnection());
            ((CmdReadTeamTournamentHistory)localObject).execute();
            localTeamTournament.setXMLHistory(((CmdReadTeamTournamentHistory)localObject).getXml());
          }
        }
        if (k != 0)
          new Thread(localTournament).start();
        if (localTournament != null) {
          int m = localResultSet.getInt("tour_status");
          localTournament.setStatus(m);
          tournaments.add(localTournament);
        }
      }
      localResultSet.close();
      i = 1;
    }
    catch (Exception localException1)
    {
      throw new RuntimeException(localException1);
    }
    try
    {
      localPreparedStatement.close();
    }
    catch (Exception localException2)
    {
      localException2.printStackTrace();
    }
    return i;
  }

  public ArrayList getList()
  {
    return tournaments;
  }

  public void setPlayers(ArrayList paramArrayList)
  {
    tournaments = paramArrayList;
  }

  public ArrayList loadTeams(TeamTournament paramTeamTournament)
    throws SQLException
  {
    ArrayList localArrayList = new ArrayList();
    PreparedStatement localPreparedStatement = getDbConnection().prepareStatement(CmdReadTeamTournament.SQL_TEAM_SELECT);
    localPreparedStatement.setInt(1, paramTeamTournament.getID());
    Team localTeam;
    for (ResultSet localResultSet = localPreparedStatement.executeQuery(); localResultSet.next(); localArrayList.add(localTeam))
    {
      int i = localResultSet.getInt("team_id");
      int j = localResultSet.getInt("team_leader");
      String str = localResultSet.getString("team_name");
      int k = localResultSet.getInt("num");
      Player localPlayer = Player.getPlayerByID(Server.getPlayersList(), j);
      if (localPlayer == null)
        throw new RuntimeException("Cannot find teamLeader: id = " + j);
      localTeam = new Team(i, str, localPlayer, paramTeamTournament, k);
      loadMembers(localTeam);
      if (localTeam.getMembers().size() != paramTeamTournament.getPlayersInTeam()) {
        throw new RuntimeException("Players qty in " + localTeam.getName() + " team is abnormal (" + localTeam.getMembers().size() + " : need " + paramTeamTournament.getPlayersInTeam() + ")");
      }
    }
    localResultSet.close();
    localPreparedStatement.close();
    return localArrayList;
  }

  public void loadMembers(Team paramTeam)
    throws SQLException
  {
    PreparedStatement localPreparedStatement = getDbConnection().prepareStatement("select * from team_players where team_id = ? ");
    localPreparedStatement.setInt(1, paramTeam.getTeamId());
    Player localPlayer;
    for (ResultSet localResultSet = localPreparedStatement.executeQuery(); localResultSet.next(); paramTeam.addMember(localPlayer))
    {
      int i = localResultSet.getInt("player_id");
      localPlayer = Player.getPlayerByID(Server.getPlayersList(), i);
      if (localPlayer == null) {
        throw new RuntimeException("Cannot find player with id = " + i);
      }
    }
    localResultSet.close();
    localPreparedStatement.close();
  }
}