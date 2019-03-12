package commands;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import tournaments.team.TeamTournament;
import tournaments.team.TeamTournamentScheduler;
import tournaments.team.TeamTournamentStage;

public class CmdReadTournamentScheduler extends Command
{
  public static final String DB_PARAM_TABLE_ID = "id";
  public static final String DB_PARAM_TOUR_ID = "tour_id";
  public static final String DB_PARAM_STAGE = "stage";
  public static final String DB_PARAM_START = "start_date";
  public static final String DB_TABLE_TOURNAMENT_SCHEDULER = "team_tournament_scheduler";
  public static final String SQL_SCHEDULER_SELECT = "select * from team_tournament_scheduler where tour_id=? order by id asc ";
  private TeamTournament teamTournament;
  private int currentStageNum;

  public CmdReadTournamentScheduler(TeamTournament teamTournament, int currentStageNum)
  {
    this.teamTournament = teamTournament;
    this.currentStageNum = currentStageNum;
  }

  public boolean execute() throws IOException {
    PreparedStatement statement = null;
    boolean status = false;
    try
    {
      statement = getDbConnection().prepareStatement("select * from team_tournament_scheduler where tour_id=? order by id asc ");
      statement.setInt(1, teamTournament.getID());
      ResultSet result = statement.executeQuery();
      ArrayList stages = new ArrayList();
      TeamTournamentStage currentStage = null;

      while (result.next()) {
        TeamTournamentStage stage = new TeamTournamentStage(teamTournament.getID(), result.getInt("stage"), new Date(result.getTimestamp("start_date").getTime()));
        if (stage.getStage() == currentStageNum) {
          currentStage = stage;
        }
        stages.add(stage);
      }

      result.close();
      status = true;

      if (currentStage == null) {
        throw new RuntimeException("Team tournament " + teamTournament.getName() + " hasnt currentStage");
      }

      TeamTournamentScheduler teamTournamentScheduler = new TeamTournamentScheduler(currentStage, stages);
      teamTournament.setTournamentSheduler(teamTournamentScheduler);
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
    finally {
      try {
        if (statement != null)
          statement.close();
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }

    return status;
  }
}