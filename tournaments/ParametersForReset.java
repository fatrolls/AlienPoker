package tournaments;

import defaultvalues.DefaultValue;
import java.math.BigDecimal;
import java.util.Date;
import utils.Log;

public class ParametersForReset
{
  private int ID;
  private String name;
  private int tournamentType;
  private int subType;
  private int game;
  private int gameType;
  private int moneyType;
  private BigDecimal tournamentAmount;
  private BigDecimal minBet;
  private BigDecimal maxBet;
  private Date beginDate;
  private BigDecimal buyIn;
  private int increaseLevelAfter;
  private long timeOnLevel;
  private long breakPeriod;
  private long breakLength;
  private int maxPlayersAtTheTable;
  private Tournament tournament;
  private int reBuysQty;
  private int addonsQty;
  private BigDecimal addonsAmount;
  private BigDecimal reBuysAmount;
  private BigDecimal fee;
  private int minPlayersToStart;
  private int tourSpeed;
  private Date regDate;

  public ParametersForReset(Tournament t, int id, String name, int tournamentType, int subType, int game, int gameType, int moneyType, BigDecimal tournamentAmount, BigDecimal maxBet, BigDecimal minBet, Date beginDate, Date regDate, BigDecimal buyIn, int increaseLevelAfter, long timeOnLevel, long breakPeriod, long breakLength, int maxPlayersAtTheTable, int reBuys, int addons, BigDecimal addonsAmount, BigDecimal reBuysAmount, BigDecimal fee, int minPlayersToStart, int tourSpeed)
  {
    tournament = t;
    ID = id;
    this.name = name;
    this.tournamentType = tournamentType;
    this.subType = subType;
    this.game = game;
    this.gameType = gameType;
    this.moneyType = moneyType;
    this.tournamentAmount = tournamentAmount;
    this.minBet = minBet;
    this.maxBet = maxBet;
    this.beginDate = beginDate;
    this.regDate = regDate;
    this.buyIn = buyIn;
    this.increaseLevelAfter = increaseLevelAfter;
    this.timeOnLevel = timeOnLevel;
    this.breakPeriod = breakPeriod;
    this.breakLength = breakLength;
    this.maxPlayersAtTheTable = maxPlayersAtTheTable;
    reBuysQty = reBuys;
    addonsQty = addons;
    this.addonsAmount = addonsAmount;
    this.reBuysAmount = reBuysAmount;
    this.minPlayersToStart = minPlayersToStart;
    this.fee = fee;
  }

  public Tournament launchNewTournament()
  {
    Tournament t = null;
    try
    {
      t = TournamentFactory.createTournament(ID, name, tournamentType, subType, game, gameType, moneyType, tournamentAmount, maxBet, minBet, beginDate, regDate, buyIn, increaseLevelAfter, timeOnLevel, breakPeriod, breakLength, maxPlayersAtTheTable, reBuysQty, addonsQty, addonsAmount, reBuysAmount, fee, minPlayersToStart, tourSpeed, 0, 0, 0, DefaultValue.ZERO_BIDECIMAL);
      new Thread(t).start();
    } catch (Exception e) {
      Log.out(e.getMessage());
    }
    return t;
  }
}