package tournaments;

import java.math.BigDecimal;
import java.util.Date;
import tournaments.multi.ComplexSitAndGo;
import tournaments.multi.FreeRollMultiTableTournament;
import tournaments.multi.MultiTableTournament;
import tournaments.team.TeamTournament;

public class TournamentFactory
{
  public static Tournament createTournament(int id, String name, int tournamentType, int subType, int game, int gameType, int moneyType, BigDecimal tournamentAmount, BigDecimal maxBet, BigDecimal minBet, Date beginDate, Date regDate, BigDecimal buyIn, int increaseLevelAfter, long timeOnLevel, long break_period, long breakLength, int maxPlayersAtTheTable, int reBuys, int addons, BigDecimal addonsAmount, BigDecimal reBuysAmount, BigDecimal fee, int minStartPlayersCount, int tourSpeed, int teamsQty, int playersInTeam, int freeRoll, BigDecimal freeRollPool)
    throws Exception
  {
    Tournament tournament;
    switch (tournamentType)
    {
    case 1:
      Tournament tournament;
      if (freeRoll == 1)
        tournament = new FreeRollMultiTableTournament(id, name, tournamentType, subType, game, gameType, moneyType, tournamentAmount, maxBet, minBet, beginDate, regDate, buyIn, increaseLevelAfter, timeOnLevel, break_period, breakLength, maxPlayersAtTheTable, reBuys, addons, addonsAmount, reBuysAmount, fee, minStartPlayersCount, tourSpeed, freeRollPool);
      else {
        tournament = new MultiTableTournament(id, name, tournamentType, subType, game, gameType, moneyType, tournamentAmount, maxBet, minBet, beginDate, regDate, buyIn, increaseLevelAfter, timeOnLevel, break_period, breakLength, maxPlayersAtTheTable, reBuys, addons, addonsAmount, reBuysAmount, fee, minStartPlayersCount, tourSpeed);
      }

      return tournament;
    case 5:
      tournament = new ComplexSitAndGo(id, name, tournamentType, subType, game, gameType, moneyType, tournamentAmount, maxBet, minBet, beginDate, regDate, buyIn, increaseLevelAfter, timeOnLevel, break_period, breakLength, maxPlayersAtTheTable, reBuys, addons, addonsAmount, reBuysAmount, fee, minStartPlayersCount, tourSpeed);

      return tournament;
    case 2:
      throw new UnsupportedOperationException("Mini tournaments is not supported yet");
    case 4:
      tournament = new TeamTournament(id, name, tournamentType, subType, game, gameType, moneyType, tournamentAmount, maxBet, minBet, beginDate, regDate, buyIn, increaseLevelAfter, timeOnLevel, break_period, breakLength, maxPlayersAtTheTable, reBuys, addons, addonsAmount, reBuysAmount, fee, minStartPlayersCount, tourSpeed, teamsQty, playersInTeam);

      return tournament;
    case 3:
    }throw new Exception("Wrong Tournament type at TournamentFactory->createTournament");
  }
}