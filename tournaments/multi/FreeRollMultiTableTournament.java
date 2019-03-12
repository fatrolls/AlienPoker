package tournaments.multi;

import game.Player;
import game.playerclub.ClubPlayers;
import game.playerclub.PlayersClub;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import tournaments.Fee;
import tournaments.FeeList;
import tournaments.GameLevel;
import tournaments.Tournament;
import tournaments.Tournament.RankingPlayer;
import tournaments.TournamentLevels;

public class FreeRollMultiTableTournament extends MultiTableTournament
{
  public FreeRollMultiTableTournament(int id, String name, int tournamentType, int subType, int game, int gameType, int moneyType, BigDecimal tournamentAmount, BigDecimal maxBet, BigDecimal minBet, Date beginDate, Date regDate, BigDecimal buyIn, int increaseLevelAfter, long timeOnLevel, long break_period, long breakLength, int maxPlayersAtTheTable, int reBuys, int addons, BigDecimal addonsAmount, BigDecimal reBuysAmount, BigDecimal fee, int minStartPlayersCount, int tourSpeed, BigDecimal freeRollPool)
  {
    super(id, name, tournamentType, subType, game, gameType, moneyType, tournamentAmount, maxBet, minBet, beginDate, regDate, buyIn, increaseLevelAfter, timeOnLevel, break_period, breakLength, maxPlayersAtTheTable, reBuys, addons, addonsAmount, reBuysAmount, fee, minStartPlayersCount, tourSpeed);
    currentMaxBet = maxBet;
    currentMinBet = minBet;

    GameLevel newLevel = tournamentLevels.getNextGameLevel(0);
    currentAnte = newLevel.getAnte();
    currentBringIn = newLevel.getBringIn();
    currentMinBet = newLevel.getMinBet();
    currentMaxBet = newLevel.getMaxBet();

    feeList = new FreeRollFeeList(this);

    setFreeRoll(true);
    freeRollPrizePool = freeRollPool.setScale(2, 5);
    ((FreeRollFeeList)feeList).setFeeAmount(freeRollPrizePool);
  }

  public int join(Player player)
  {
    BigDecimal needSum = buyIn.add(this.fee).setScale(2, 5);
    int code;
    synchronized (player) {
      if (player.getDirtyPoints().floatValue() < needSum.floatValue()) {
        return 7;
      }

      synchronized (this) {
        if ((!begin) && (regStatus == 1))
        {
          Fee fee = new Fee(player, this, buyIn);
          int code = ((FreeRollFeeList)feeList).addFee(fee, freeRollPrizePool);
          if (code == 1) {
            if (PlayersClub.getInstance().getClubPlayers().isAMember(player)) {
              player.decreaseDirtyPoints(needSum);
            }
            Tournament.RankingPlayer rp = new Tournament.RankingPlayer(this, player, tournamentAmount, null);
            rp.setRank(playersList.size() + 1);
            playersList.add(rp);
          }
        }
        else
        {
          int code;
          if (begin)
            code = 3;
          else {
            code = 6;
          }
        }
      }
    }

    if (code == 1) {
      updateCashedXML();
    }

    return code;
  }

  public FeeList getFeeList() {
    return feeList;
  }

  private class FreeRollFeeList extends FeeList
  {
    public FreeRollFeeList(Tournament tournament)
    {
      super();
    }

    public int addFee(Fee fee, BigDecimal amount) {
      int code = addFee(fee);
      feeAmount = amount.setScale(2, 5);
      return code;
    }

    public void setFeeAmount(BigDecimal amount) {
      feeAmount = amount.setScale(2, 5);
    }
  }
}