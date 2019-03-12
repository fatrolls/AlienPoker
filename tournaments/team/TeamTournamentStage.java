package tournaments.team;

import java.util.Date;

public class TeamTournamentStage
{
  private int tournamentId;
  private int stage;
  private Date date;

  public TeamTournamentStage(int tournamentId, int stage, Date date)
  {
    this.tournamentId = tournamentId;
    this.stage = stage;
    this.date = date;
  }

  public int getTournamentId() {
    return tournamentId;
  }

  public int getStage() {
    return stage;
  }

  public Date getDate() {
    return date;
  }

  public int hashCode() {
    return stage;
  }

  public boolean equals(Object o) {
    if ((o instanceof TeamTournamentStage)) {
      return hashCode() == o.hashCode();
    }
    return false;
  }
}