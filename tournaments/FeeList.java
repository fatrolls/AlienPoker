package tournaments;

import game.Player;
import game.playerclub.ClubPlayers;
import game.playerclub.PlayersClub;
import java.math.BigDecimal;
import java.util.LinkedList;

public class FeeList
{
  private final LinkedList feeList = new LinkedList();
  protected BigDecimal feeAmount = new BigDecimal(0);
  private Tournament tournament;

  public FeeList(Tournament paramTournament)
  {
    tournament = paramTournament;
  }

  public int addFee(Fee paramFee) {
    synchronized (feeList) {
      if (!hasFee(paramFee.getPlayer())) {
        feeList.add(paramFee);
        synchronized (this) {
          feeAmount = feeAmount.add(paramFee.getAmount()).setScale(2, 5);
        }

        return 1;
      }
      return 0;
    }
  }

  public boolean removePlayer(Player paramPlayer)
  {
    synchronized (feeList) {
      if (hasFee(paramPlayer)) {
        int i = feeList.size();
        for (int j = 0; j < i; j++) {
          Fee localFee = (Fee)feeList.get(j);
          if (localFee.getPlayer().getID() == paramPlayer.getID()) {
            feeList.remove(j);
            feeAmount = feeAmount.subtract(localFee.getAmount()).setScale(2, 5);
            if ((tournament.isFreeRoll()) && (PlayersClub.getInstance().getClubPlayers().isAMember(paramPlayer)))
              tournament.getFreeRollPrizePool().add(localFee.getAmount()).setScale(2, 5);
            else
              paramPlayer.increaseAmount(localFee.getAmount(), tournament.getMoneyType());
            return true;
          }
        }
      }
    }
    return false;
  }

  public boolean hasFee(Player paramPlayer) {
    synchronized (feeList) {
      int i = feeList.size();
      for (int j = 0; j < i; j++) {
        Fee localFee = (Fee)feeList.get(j);
        if (localFee.getPlayer().getID() == paramPlayer.getID()) {
          return true;
        }
      }
      return false;
    }
  }

  public Fee getFee(Player paramPlayer)
  {
    synchronized (feeList) {
      int i = feeList.size();
      for (int j = 0; j < i; j++) {
        Fee localFee = (Fee)feeList.get(j);
        if (localFee.getPlayer().getID() == paramPlayer.getID()) {
          return localFee;
        }
      }
      return null;
    }
  }

  public BigDecimal getFeeAmount() {
    return feeAmount;
  }

  public int getSize()
  {
    return feeList.size();
  }
}