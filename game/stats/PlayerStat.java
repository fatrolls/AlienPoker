package game.stats;

import defaultvalues.DefaultValue;
import game.Player;
import java.math.BigDecimal;
import java.util.Date;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class PlayerStat
{
  private static final BigDecimal HUNDRED_PERCENTS = new BigDecimal(100);
  private static final BigDecimal ZERO_PERCENTS = DefaultValue.ZERO_BIDECIMAL;

  private Date sessionStart = new Date();
  private long sessionGames = 0L;
  private Player player = null;
  private BigDecimal gamesWon = ZERO_PERCENTS;

  private long showdownsCount = 0L;
  private BigDecimal showdownsWon = ZERO_PERCENTS;

  private BigDecimal flopSeen = ZERO_PERCENTS;
  private BigDecimal fourthStreetSeen = ZERO_PERCENTS;

  private BigDecimal winIfFlopSeen = ZERO_PERCENTS;
  private BigDecimal winIfFourthStreetSeen = ZERO_PERCENTS;

  private long bettingCount = 0L;

  private BigDecimal fold = ZERO_PERCENTS;
  private BigDecimal check = ZERO_PERCENTS;
  private BigDecimal call = ZERO_PERCENTS;
  private BigDecimal bet = ZERO_PERCENTS;
  private BigDecimal raise = ZERO_PERCENTS;
  private BigDecimal reRaise = ZERO_PERCENTS;

  private long foldsCount = 0L;

  private BigDecimal foldPreFlop = ZERO_PERCENTS;
  private BigDecimal foldAfterFlop = ZERO_PERCENTS;
  private BigDecimal foldAfterTurn = ZERO_PERCENTS;
  private BigDecimal foldAfterRiver = ZERO_PERCENTS;

  private BigDecimal foldAfterThirdStreet = ZERO_PERCENTS;
  private BigDecimal foldAfterFourthStreet = ZERO_PERCENTS;
  private BigDecimal foldAfterFifthStreet = ZERO_PERCENTS;
  private BigDecimal foldAfterSixthStreet = ZERO_PERCENTS;
  private BigDecimal foldAfterSeventhStreet = ZERO_PERCENTS;

  private BigDecimal noFold = new BigDecimal(0);
  private static final String TAG_NAME_PLAYER_STATS = "PLSTATS";
  private static final String OUT_PARAM_SESSION_START = "START";
  private static final String OUT_PARAM_SESSION_GAMES = "GAMES";
  private static final String OUT_PARAM_GAMES_WON = "GAMESWON";
  private static final String OUT_PARAM_SHOWDOWNS_WON = "SHOWDOWNS";
  private static final String OUT_PARAM_FLOP_SEEN = "FLOPSEEN";
  private static final String OUT_PARAM_WIN_IF_FLOP_SEEN = "WINIFFLOP";
  private static final String OUT_PARAM_FOURTH_STREET_SEEN = "FOURTHSTREETSEEN";
  private static final String OUT_PARAM_WIN_IF_FOURTH_STREET_SEEN = "WINIFFOURTHSTREET";
  private static final String OUT_PARAM_FOLD = "FOLD";
  private static final String OUT_PARAM_CHECK = "CHECK";
  private static final String OUT_PARAM_CALL = "CALL";
  private static final String OUT_PARAM_BET = "BET";
  private static final String OUT_PARAM_RAISE = "RAISE";
  private static final String OUT_PARAM_RERAISE = "RERAISE";
  private static final String OUT_PARAM_FOLD_PRE_FLOP = "FOLDPREFLOP";
  private static final String OUT_PARAM_FOLD_FLOP = "FOLDFLOP";
  private static final String OUT_PARAM_FOLD_TURN = "FOLDTURN";
  private static final String OUT_PARAM_FOLD_RIVER = "FOLDRIVER";
  private static final String OUT_PARAM_FOLD_THIRD_STREET = "FOLDTHIRDSTREET";
  private static final String OUT_PARAM_FOLD_FOURTH_STREET = "FOLDFOURTHSTREET";
  private static final String OUT_PARAM_FOLD_FIFTH_STREET = "FOLDFIFTHSTREET";
  private static final String OUT_PARAM_FOLD_SIXTH_STREET = "FOLDSIXTHSTREET";
  private static final String OUT_PARAM_FOLD_SEVENTH_STREET = "FOLDSEVENTHSTREET";
  private static final String OUT_PARAM_FOLD_NO_FOLD = "NOFOLD";

  public PlayerStat(Player player)
  {
    this.player = player;
  }

  public int hashCode() {
    return player.getID();
  }

  public void reset() {
    synchronized (this)
    {
      sessionStart = new Date();
      sessionGames = 0L;
      gamesWon = ZERO_PERCENTS;

      showdownsCount = 0L;
      showdownsWon = ZERO_PERCENTS;

      flopSeen = ZERO_PERCENTS;
      winIfFlopSeen = ZERO_PERCENTS;

      bettingCount = 0L;

      fold = ZERO_PERCENTS;
      check = ZERO_PERCENTS;
      call = ZERO_PERCENTS;
      bet = ZERO_PERCENTS;
      raise = ZERO_PERCENTS;
      reRaise = ZERO_PERCENTS;

      foldsCount = 0L;

      foldPreFlop = ZERO_PERCENTS;
      foldAfterFlop = ZERO_PERCENTS;
      foldAfterTurn = ZERO_PERCENTS;
      foldAfterRiver = ZERO_PERCENTS;

      foldAfterThirdStreet = ZERO_PERCENTS;
      foldAfterFourthStreet = ZERO_PERCENTS;
      foldAfterFifthStreet = ZERO_PERCENTS;
      foldAfterSixthStreet = ZERO_PERCENTS;
      foldAfterSeventhStreet = ZERO_PERCENTS;

      noFold = ZERO_PERCENTS;
    }
  }

  public Date getSessionStart() {
    return sessionStart;
  }

  private BigDecimal increaseValue(BigDecimal oldValue, BigDecimal percent, long gamesCount)
  {
    BigDecimal result;
    BigDecimal result;
    if (gamesCount == 0L)
      result = ZERO_PERCENTS;
    else {
      result = oldValue.multiply(new BigDecimal(gamesCount));
    }

    if (gamesCount + 1L > 0L) {
      return result.add(percent).divide(new BigDecimal(gamesCount + 1L), 2, 5).setScale(0, 5);
    }
    return ZERO_PERCENTS;
  }

  public void setSessionStart(Date sessionStart)
  {
    synchronized (this) {
      this.sessionStart = sessionStart;
    }
  }

  private long getSessionDecOneGames()
  {
    return sessionGames - 1L;
  }

  public long getFoldsCount()
  {
    return foldsCount;
  }

  public long getSessionGames() {
    return sessionGames;
  }

  public void incSessionGames()
  {
    synchronized (this) {
      if (sessionGames == 0L) {
        sessionStart = new Date();
      }
      sessionGames += 1L;
    }
  }

  public BigDecimal getGamesWon() {
    return gamesWon;
  }

  public void incGamesWon(BigDecimal percent) {
    synchronized (this) {
      gamesWon = increaseValue(gamesWon, percent, getSessionDecOneGames());
    }
  }

  public long getShowdownsCount()
  {
    return showdownsCount;
  }

  public BigDecimal getShowdownsWon() {
    return showdownsWon;
  }

  public void incShowdownsWon(BigDecimal percent) {
    synchronized (this) {
      showdownsWon = increaseValue(showdownsWon, percent, showdownsCount);
      showdownsCount += 1L;
    }
  }

  public BigDecimal getFlopSeen() {
    return flopSeen;
  }

  public void incFlopSeen(BigDecimal percent) {
    synchronized (this) {
      flopSeen = increaseValue(flopSeen, percent, getSessionDecOneGames());
    }
  }

  public BigDecimal getFourthStreetSeen() {
    return fourthStreetSeen;
  }

  public void incFourthStreetSeen(BigDecimal percent) {
    synchronized (this) {
      fourthStreetSeen = increaseValue(fourthStreetSeen, percent, getSessionDecOneGames());
    }
  }

  public BigDecimal getWinIfFlopSeen() {
    return winIfFlopSeen;
  }

  public void incWinIfFlopSeen(BigDecimal percent) {
    synchronized (this) {
      winIfFlopSeen = increaseValue(winIfFlopSeen, percent, getSessionDecOneGames());
    }
  }

  public BigDecimal getWinIfFourthStreetSeen() {
    return winIfFourthStreetSeen;
  }

  public void incWinIfFourthStreetSeen(BigDecimal percent) {
    synchronized (this) {
      winIfFourthStreetSeen = increaseValue(winIfFourthStreetSeen, percent, getSessionDecOneGames());
    }
  }

  public BigDecimal getFold() {
    return fold;
  }

  public void incFold() {
    synchronized (this) {
      fold = increaseValue(fold, HUNDRED_PERCENTS, bettingCount);
      check = increaseValue(check, ZERO_PERCENTS, bettingCount);
      call = increaseValue(call, ZERO_PERCENTS, bettingCount);
      bet = increaseValue(bet, ZERO_PERCENTS, bettingCount);
      raise = increaseValue(raise, ZERO_PERCENTS, bettingCount);
      reRaise = increaseValue(reRaise, ZERO_PERCENTS, bettingCount);

      bettingCount += 1L;
    }
  }

  public BigDecimal getCheck() {
    return check;
  }

  public void incCheck() {
    synchronized (this) {
      fold = increaseValue(fold, ZERO_PERCENTS, bettingCount);
      check = increaseValue(check, HUNDRED_PERCENTS, bettingCount);
      call = increaseValue(call, ZERO_PERCENTS, bettingCount);
      bet = increaseValue(bet, ZERO_PERCENTS, bettingCount);
      raise = increaseValue(raise, ZERO_PERCENTS, bettingCount);
      reRaise = increaseValue(reRaise, ZERO_PERCENTS, bettingCount);

      bettingCount += 1L;
    }
  }

  public BigDecimal getCall() {
    return call;
  }

  public void incCall() {
    synchronized (this) {
      fold = increaseValue(fold, ZERO_PERCENTS, bettingCount);
      check = increaseValue(check, ZERO_PERCENTS, bettingCount);
      call = increaseValue(call, HUNDRED_PERCENTS, bettingCount);
      bet = increaseValue(bet, ZERO_PERCENTS, bettingCount);
      raise = increaseValue(raise, ZERO_PERCENTS, bettingCount);
      reRaise = increaseValue(reRaise, ZERO_PERCENTS, bettingCount);

      bettingCount += 1L;
    }
  }

  public BigDecimal getBet() {
    return bet;
  }

  public void incBet() {
    synchronized (this)
    {
      fold = increaseValue(fold, ZERO_PERCENTS, bettingCount);
      check = increaseValue(check, ZERO_PERCENTS, bettingCount);
      call = increaseValue(call, ZERO_PERCENTS, bettingCount);
      bet = increaseValue(bet, HUNDRED_PERCENTS, bettingCount);
      raise = increaseValue(raise, ZERO_PERCENTS, bettingCount);
      reRaise = increaseValue(reRaise, ZERO_PERCENTS, bettingCount);

      bettingCount += 1L;
    }
  }

  public BigDecimal getRaise() {
    return raise;
  }

  public void incRaise() {
    synchronized (this)
    {
      fold = increaseValue(fold, ZERO_PERCENTS, bettingCount);
      check = increaseValue(check, ZERO_PERCENTS, bettingCount);
      call = increaseValue(call, ZERO_PERCENTS, bettingCount);
      bet = increaseValue(bet, ZERO_PERCENTS, bettingCount);
      raise = increaseValue(raise, HUNDRED_PERCENTS, bettingCount);
      reRaise = increaseValue(reRaise, ZERO_PERCENTS, bettingCount);

      bettingCount += 1L;
    }
  }

  public BigDecimal getReRaise() {
    return reRaise;
  }

  public void incReRaise() {
    synchronized (this) {
      fold = increaseValue(fold, ZERO_PERCENTS, bettingCount);
      check = increaseValue(check, ZERO_PERCENTS, bettingCount);
      call = increaseValue(call, ZERO_PERCENTS, bettingCount);
      bet = increaseValue(bet, ZERO_PERCENTS, bettingCount);
      raise = increaseValue(raise, ZERO_PERCENTS, bettingCount);
      reRaise = increaseValue(reRaise, HUNDRED_PERCENTS, bettingCount);

      bettingCount += 1L;
    }
  }

  public BigDecimal getFoldPreFlop() {
    return foldPreFlop;
  }

  public void incFoldPreFlop() {
    synchronized (this) {
      foldPreFlop = increaseValue(foldPreFlop, HUNDRED_PERCENTS, foldsCount);
      foldAfterFlop = increaseValue(foldAfterFlop, ZERO_PERCENTS, foldsCount);
      foldAfterTurn = increaseValue(foldAfterTurn, ZERO_PERCENTS, foldsCount);
      foldAfterRiver = increaseValue(foldAfterRiver, ZERO_PERCENTS, foldsCount);
      noFold = increaseValue(noFold, ZERO_PERCENTS, getSessionDecOneGames() - foldsCount);

      foldsCount += 1L;
    }
  }

  public BigDecimal getFoldAfterFlop() {
    return foldAfterFlop;
  }

  public void incFoldAfterFlop() {
    synchronized (this)
    {
      foldPreFlop = increaseValue(foldPreFlop, ZERO_PERCENTS, foldsCount);
      foldAfterFlop = increaseValue(foldAfterFlop, HUNDRED_PERCENTS, foldsCount);
      foldAfterTurn = increaseValue(foldAfterTurn, ZERO_PERCENTS, foldsCount);
      foldAfterRiver = increaseValue(foldAfterRiver, ZERO_PERCENTS, foldsCount);
      noFold = increaseValue(noFold, ZERO_PERCENTS, getSessionDecOneGames() - foldsCount);

      foldsCount += 1L;
    }
  }

  public BigDecimal getFoldAfterTurn() {
    return foldAfterTurn;
  }

  public void incFoldAfterTurn() {
    synchronized (this)
    {
      foldPreFlop = increaseValue(foldPreFlop, ZERO_PERCENTS, foldsCount);
      foldAfterFlop = increaseValue(foldAfterFlop, ZERO_PERCENTS, foldsCount);
      foldAfterTurn = increaseValue(foldAfterTurn, HUNDRED_PERCENTS, foldsCount);
      foldAfterRiver = increaseValue(foldAfterRiver, ZERO_PERCENTS, foldsCount);
      noFold = increaseValue(noFold, ZERO_PERCENTS, getSessionDecOneGames() - foldsCount);

      foldsCount += 1L;
    }
  }

  public BigDecimal getFoldAfterRiver() {
    return foldAfterRiver;
  }

  public void incFoldAfterRiver() {
    synchronized (this)
    {
      foldPreFlop = increaseValue(foldPreFlop, ZERO_PERCENTS, foldsCount);
      foldAfterFlop = increaseValue(foldAfterFlop, ZERO_PERCENTS, foldsCount);
      foldAfterTurn = increaseValue(foldAfterTurn, ZERO_PERCENTS, foldsCount);
      foldAfterRiver = increaseValue(foldAfterRiver, HUNDRED_PERCENTS, foldsCount);

      noFold = increaseValue(noFold, ZERO_PERCENTS, getSessionDecOneGames() - foldsCount);

      foldsCount += 1L;
    }
  }

  public BigDecimal getFoldAfterThirdStreet() {
    return foldAfterThirdStreet;
  }

  public void incFoldAfterThirdStreet() {
    synchronized (this)
    {
      foldAfterThirdStreet = increaseValue(foldAfterThirdStreet, HUNDRED_PERCENTS, foldsCount);
      foldAfterFourthStreet = increaseValue(foldAfterFourthStreet, ZERO_PERCENTS, foldsCount);
      foldAfterFifthStreet = increaseValue(foldAfterFifthStreet, ZERO_PERCENTS, foldsCount);
      foldAfterSixthStreet = increaseValue(foldAfterSixthStreet, ZERO_PERCENTS, foldsCount);
      foldAfterSeventhStreet = increaseValue(foldAfterSeventhStreet, ZERO_PERCENTS, foldsCount);
      noFold = increaseValue(noFold, ZERO_PERCENTS, getSessionDecOneGames() - foldsCount);

      foldsCount += 1L;
    }
  }

  public BigDecimal getFoldAfterFourthStreet() {
    return foldAfterFourthStreet;
  }

  public void incFoldAfterFourthStreet() {
    synchronized (this)
    {
      foldAfterThirdStreet = increaseValue(foldAfterThirdStreet, ZERO_PERCENTS, foldsCount);
      foldAfterFourthStreet = increaseValue(foldAfterFourthStreet, HUNDRED_PERCENTS, foldsCount);
      foldAfterFifthStreet = increaseValue(foldAfterFifthStreet, ZERO_PERCENTS, foldsCount);
      foldAfterSixthStreet = increaseValue(foldAfterSixthStreet, ZERO_PERCENTS, foldsCount);
      foldAfterSeventhStreet = increaseValue(foldAfterSeventhStreet, ZERO_PERCENTS, foldsCount);
      noFold = increaseValue(noFold, ZERO_PERCENTS, getSessionDecOneGames() - foldsCount);

      foldsCount += 1L;
    }
  }

  public BigDecimal getFoldAfterFifthStreet() {
    return foldAfterFifthStreet;
  }

  public void incFoldAfterFifthStreet() {
    synchronized (this)
    {
      foldAfterThirdStreet = increaseValue(foldAfterThirdStreet, ZERO_PERCENTS, foldsCount);
      foldAfterFourthStreet = increaseValue(foldAfterFourthStreet, ZERO_PERCENTS, foldsCount);
      foldAfterFifthStreet = increaseValue(foldAfterFifthStreet, HUNDRED_PERCENTS, foldsCount);
      foldAfterSixthStreet = increaseValue(foldAfterSixthStreet, ZERO_PERCENTS, foldsCount);
      foldAfterSeventhStreet = increaseValue(foldAfterSeventhStreet, ZERO_PERCENTS, foldsCount);
      noFold = increaseValue(noFold, ZERO_PERCENTS, getSessionDecOneGames() - foldsCount);

      foldsCount += 1L;
    }
  }

  public BigDecimal getFoldAfterSixthStreet() {
    return foldAfterSixthStreet;
  }

  public void incFoldAfterSixthStreet() {
    synchronized (this)
    {
      foldAfterThirdStreet = increaseValue(foldAfterThirdStreet, ZERO_PERCENTS, foldsCount);
      foldAfterFourthStreet = increaseValue(foldAfterFourthStreet, ZERO_PERCENTS, foldsCount);
      foldAfterFifthStreet = increaseValue(foldAfterFifthStreet, ZERO_PERCENTS, foldsCount);
      foldAfterSixthStreet = increaseValue(foldAfterSixthStreet, HUNDRED_PERCENTS, foldsCount);
      foldAfterSeventhStreet = increaseValue(foldAfterSeventhStreet, ZERO_PERCENTS, foldsCount);
      noFold = increaseValue(noFold, ZERO_PERCENTS, getSessionDecOneGames() - foldsCount);

      foldsCount += 1L;
    }
  }

  public BigDecimal getFoldAfterSeventhStreet() {
    return foldAfterSeventhStreet;
  }

  public void incFoldAfterSeventhStreet() {
    synchronized (this)
    {
      foldAfterThirdStreet = increaseValue(foldAfterThirdStreet, ZERO_PERCENTS, foldsCount);
      foldAfterFourthStreet = increaseValue(foldAfterFourthStreet, ZERO_PERCENTS, foldsCount);
      foldAfterFifthStreet = increaseValue(foldAfterFifthStreet, ZERO_PERCENTS, foldsCount);
      foldAfterSixthStreet = increaseValue(foldAfterSixthStreet, ZERO_PERCENTS, foldsCount);
      foldAfterSeventhStreet = increaseValue(foldAfterSeventhStreet, HUNDRED_PERCENTS, foldsCount);
      noFold = increaseValue(noFold, ZERO_PERCENTS, getSessionDecOneGames() - foldsCount);

      foldsCount += 1L;
    }
  }

  public BigDecimal getNoFold() {
    return noFold;
  }

  public void incNoFold()
  {
    synchronized (this)
    {
      noFold = increaseValue(noFold, HUNDRED_PERCENTS, getSessionDecOneGames() - foldsCount);
    }
  }

  public Player getPlayer() {
    return player;
  }

  public long getBettingCount() {
    return bettingCount;
  }

  public void setBettingCount(long bettingCount) {
    synchronized (this)
    {
      this.bettingCount = bettingCount;
    }
  }

  public void loadSessionGames(long sessionGames) {
    this.sessionGames = sessionGames;
  }

  public void loadGamesWon(BigDecimal percent) {
    gamesWon = percent;
  }

  public void loadShowdownsCount(long showdownsCount) {
    this.showdownsCount = showdownsCount;
  }

  public void loadShowdownsWon(BigDecimal showdownsWon) {
    this.showdownsWon = showdownsWon;
  }

  public void loadFlopSeen(BigDecimal flopSeen) {
    this.flopSeen = flopSeen;
  }

  public void loadWinIfFlopSeen(BigDecimal winIfFlopSeen) {
    this.winIfFlopSeen = winIfFlopSeen;
  }

  public void loadFold(BigDecimal fold) {
    this.fold = fold;
  }

  public void loadCheck(BigDecimal check) {
    fold = check;
  }

  public void loadCall(BigDecimal call) {
    this.call = call;
  }

  public void loadBet(BigDecimal bet) {
    this.bet = bet;
  }

  public void loadRaise(BigDecimal raise) {
    this.raise = raise;
  }

  public void loadReRaise(BigDecimal reRaise) {
    this.reRaise = reRaise;
  }

  public void loadFoldsCount(long foldsCount) {
    this.foldsCount = foldsCount;
  }

  public void loadFoldPreFlop(BigDecimal foldPreFlop) {
    this.foldPreFlop = foldPreFlop;
  }

  public void loadFoldAfterFlop(BigDecimal foldAfterFlop) {
    this.foldAfterFlop = foldAfterFlop;
  }

  public void loadFoldAfterTurn(BigDecimal foldAfterTurn) {
    this.foldAfterTurn = foldAfterTurn;
  }

  public void loadFoldAfterRiver(BigDecimal foldAfterRiver) {
    this.foldAfterRiver = foldAfterRiver;
  }

  public void loadNoFold(BigDecimal noFold) {
    this.noFold = noFold;
  }

  public String toXML(int pokerType)
  {
    XMLDoc doc = new XMLDoc();
    XMLTag tag = doc.startTag("PLSTATS");

    if (sessionGames > 0L)
      tag.addParam("START", "" + sessionStart.getTime());
    else {
      tag.addParam("START", "");
    }

    tag.addParam("GAMES", "" + sessionGames);
    tag.addParam("ID", player.getID());
    tag.addParam("GAMESWON", gamesWon.intValue());
    tag.addParam("SHOWDOWNS", showdownsWon.intValue());

    if (pokerType != 7)
    {
      if ((pokerType == 2) || (pokerType == 6))
      {
        tag.addParam("FOURTHSTREETSEEN", fourthStreetSeen.intValue());
        tag.addParam("WINIFFOURTHSTREET", winIfFourthStreetSeen.intValue());

        tag.addParam("FOLDTHIRDSTREET", foldAfterThirdStreet.intValue());
        tag.addParam("FOLDFOURTHSTREET", foldAfterFourthStreet.intValue());
        tag.addParam("FOLDFIFTHSTREET", foldAfterFifthStreet.intValue());
        tag.addParam("FOLDSIXTHSTREET", foldAfterSixthStreet.intValue());
        tag.addParam("FOLDSEVENTHSTREET", foldAfterSeventhStreet.intValue());
      }
      else
      {
        tag.addParam("FLOPSEEN", flopSeen.intValue());
        tag.addParam("WINIFFLOP", winIfFlopSeen.intValue());

        tag.addParam("FOLDPREFLOP", foldPreFlop.intValue());
        tag.addParam("FOLDFLOP", foldAfterFlop.intValue());
        tag.addParam("FOLDTURN", foldAfterTurn.intValue());
        tag.addParam("FOLDRIVER", foldAfterRiver.intValue());
      }

    }

    tag.addParam("FOLD", fold.intValue());

    if (pokerType != 7) {
      tag.addParam("CHECK", check.intValue());
      tag.addParam("CALL", call.intValue());
      tag.addParam("BET", bet.intValue());
      tag.addParam("RAISE", raise.intValue());
      tag.addParam("RERAISE", reRaise.intValue());
    }

    tag.addParam("NOFOLD", noFold.intValue());

    String xml = doc.toString();
    doc.invalidate();

    return xml;
  }
}