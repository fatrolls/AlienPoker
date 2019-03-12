package adminprocessors;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import tournaments.Tournament;
import tournaments.TournamentFactory;

public class AdminAddTournamentCommandProcessor
  implements AdminXMLResponse
{
  private static final String MSG_TOURNAMENT_WAS_ADDED = "Tournament was added";

  public Hashtable addTournament(int tournamentId, String tournamentName, int type, int subType, int month, int day, int year, int hour, int minute, int regMonth, int regDay, int regYear, int regHour, int regMinute, int poker, int limit, int money, int maxAtTable, int status, int regStatus, double buyInD, int reBuys, int addons, double feeD, int levelDuration, double minBetD, double maxBetD, double begAmountD, int minPlayersToStart, int timeOnLevelS, int breakPeriodS, int breakLengthS, double adonsAmountD, double reBuysAmountD, int tourSpeed, int isFreeRoll, double freeRollPool)
  {
    Hashtable response = new Hashtable();
    try
    {
      BigDecimal buyIn = new BigDecimal(buyInD).setScale(2, 5);
      BigDecimal fee = new BigDecimal(feeD).setScale(2, 5);
      BigDecimal minBet = new BigDecimal(minBetD).setScale(2, 5);
      BigDecimal maxBet = new BigDecimal(maxBetD).setScale(2, 5);
      BigDecimal begAmount = new BigDecimal(begAmountD).setScale(2, 5);
      long timeOnLevel = timeOnLevelS * 60000;
      long breakPeriod = breakPeriodS * 60000;
      long breakLength = breakLengthS * 60000;
      BigDecimal adonsAmount = new BigDecimal(adonsAmountD).setScale(2, 5);
      BigDecimal reBuysAmount = new BigDecimal(reBuysAmountD).setScale(2, 5);

      Calendar c = Calendar.getInstance();
      c.set(2, month - 1);
      c.set(5, day);
      c.set(1, year);
      c.set(11, hour);
      c.set(12, minute);
      c.set(13, 0);
      c.set(14, 0);

      Date moment = c.getTime();

      Calendar regC = Calendar.getInstance();
      regC.set(2, regMonth - 1);
      regC.set(5, regDay);
      regC.set(1, regYear);
      regC.set(11, regHour);
      regC.set(12, regMinute);
      regC.set(13, 0);
      regC.set(14, 0);

      Date regMoment = regC.getTime();

      Tournament t = TournamentFactory.createTournament(tournamentId, tournamentName, type, subType, poker, limit, money, begAmount, maxBet, minBet, moment, regMoment, buyIn, levelDuration, timeOnLevel, breakPeriod, breakLength, maxAtTable, reBuys, addons, adonsAmount, reBuysAmount, fee, minPlayersToStart, tourSpeed, 0, 0, isFreeRoll, new BigDecimal(freeRollPool).setScale(2, 5));

      new Thread(t).start();
    }
    catch (Exception ex1) {
      ex1.printStackTrace();
      response.put("STATUS", "ERROR");
      response.put("RESPONSE", "Bad parameters");
      return response;
    }

    response.put("STATUS", "OK");
    response.put("RESPONSE", "Tournament was added");
    return response;
  }
}