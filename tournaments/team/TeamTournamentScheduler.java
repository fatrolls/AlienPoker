package tournaments.team;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TeamTournamentScheduler
{
  private TeamTournamentStage currentStage;
  private final ArrayList stages;

  public TeamTournamentScheduler(TeamTournamentStage currentStage, ArrayList stages)
  {
    this.currentStage = currentStage;
    this.stages = stages;
  }

  public TeamTournamentStage getCurrentStage() {
    return currentStage;
  }

  public TeamTournamentStage getNextStage() {
    Iterator iter = stages.iterator();
    synchronized (stages) {
      while (iter.hasNext()) {
        TeamTournamentStage stage = (TeamTournamentStage)iter.next();
        if (stage.getStage() > currentStage.getStage()) {
          return stage;
        }
      }
    }
    return null;
  }

  public boolean isLastState() {
    synchronized (stages) {
      if (stages.size() > 0) {
        TeamTournamentStage stage = (TeamTournamentStage)stages.get(stages.size() - 1);
        return stage.equals(currentStage);
      }
      return false;
    }
  }

  public List getNextStagesList()
  {
    Iterator iter = stages.iterator();
    List st = new ArrayList();
    synchronized (stages) {
      while (iter.hasNext()) {
        TeamTournamentStage stage = (TeamTournamentStage)iter.next();
        if (stage.getStage() > currentStage.getStage()) {
          st.add(stage);
        }
      }
    }
    return st;
  }
}