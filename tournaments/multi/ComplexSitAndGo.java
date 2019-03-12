package tournaments.multi;

import commands.safeupdaters.FinishTournamentHistory;
import commands.safeupdaters.StartTournamentHistory;
import game.Desk;
import game.PlacesList;
import game.Player;
import game.playerclub.ClubPlayers;
import game.playerclub.PlayersClub;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import org.apache.log4j.Logger;
import utils.CommonLogger;
import utils.xml.XMLTag;

public class ComplexSitAndGo extends MultiTableTournament
{
  private int primaryDesksQty;

  public ComplexSitAndGo(int id, String name, int tournamentType, int subType, int game, int gameType, int moneyType, BigDecimal tournamentAmount, BigDecimal maxBet, BigDecimal minBet, Date beginDate, Date regDate, BigDecimal buyIn, int increaseLevelAfter, long timeOnLevel, long break_period, long breakLength, int maxPlayersAtTheTable, int reBuys, int addons, BigDecimal addonsAmount, BigDecimal reBuysAmount, BigDecimal fee, int minStartPlayersCount, int tourSpeed)
  {
    super(id, name, tournamentType, subType, game, gameType, moneyType, tournamentAmount, maxBet, minBet, beginDate, regDate, buyIn, increaseLevelAfter, timeOnLevel, break_period, breakLength, maxPlayersAtTheTable, reBuys, addons, addonsAmount, reBuysAmount, fee, minStartPlayersCount, tourSpeed);
    primaryDesksQty = (minStartPlayersCount / maxPlayersAtTheTable);
  }

  public void run()
  {
    begin = false;
    regStatus = 2;
    ArrayList beginDesks;
    synchronized (playersList)
    {
      beginDesks = createTournamentsDesksTables(primaryDesksQty, moneyType, gameType, currentMaxBet, currentMinBet, currentAnte, currentBringIn, game, buyIn, minPlayerRate, maxPlayersAtTheTable);
    }

    synchronized (desksList) {
      desksList.clear();
      desksList.addAll(beginDesks);
    }

    while (!begin)
    {
      int count;
      synchronized (playersList) {
        count = playersList.size();
      }

      if (count < minStartPlayersCount) {
        if ((status == 0) && 
          (new Date().compareTo(regDate) > 0)) {
          status = 1;
          regStatus = 1;
          updateCashedXML();
        }

        try
        {
          Thread.sleep(5000L);
        } catch (InterruptedException e) {
          CommonLogger.getLogger().warn(e.getMessage(), e);
        }
      } else {
        new Thread(new StartTournamentHistory(this)).start();
        break;
      }

    }

    if (!checkStratUp())
    {
      synchronized (this) {
        status = 4;
        regStatus = 2;
      }

      try
      {
        Thread.sleep(6000L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      this.timer.cancel();

      returnMoney();

      updateCashedXML();

      return;
    }

    status = 7;
    regStatus = 2;

    begin = true;
    new Thread(new StartTournamentHistory(this)).start();

    status = 2;

    nextBreakTime = new Date(new Date().getTime() + breakPeriod + breakLength);
    nextLevelTime = new Date(new Date().getTime() + timeOnLevel);
    try
    {
      while (!end)
        try
        {
          Thread.sleep(4000L);

          synchronized (desksList) {
            int size = desksList.size();
            if (size == 1)
            {
              Desk desk = (Desk)desksList.get(0);
              synchronized (desk) {
                if (desk.getPlayersCount() < 2) {
                  end = true;

                  updateRanking(eliminateAllPlayers(desk));

                  Timer timer = new Timer();
                  timer.schedule(new MultiTableTournament.DropDeskTask(this, desk, this), 120000L);
                }
              }

            }
            else if (size == 0) {
              break;
            }
          }
        }
        catch (InterruptedException e) {
          CommonLogger.getLogger().warn(e.getMessage(), e);
        }
    }
    catch (Exception e)
    {
      CommonLogger.getLogger().warn(e.getMessage(), e);
    }

    updateRanking(new ArrayList());

    end = true;
    status = 3;
    try
    {
      Thread.sleep(30000L);
    } catch (InterruptedException e) {
      CommonLogger.getLogger().warn(e.getMessage(), e);
    }

    this.timer.cancel();
    synchronized (desksList) {
      desksList.clear();
    }
    updateCashedXML();

    new Thread(new FinishTournamentHistory(this)).start();
  }

  public void seatPlayer(Desk desk, Player player, int placeNum)
  {
    desk.seatPlayer(player, placeNum, getTournamentAmount());
  }

  public int join(Player player, int anInt) {
    throw new UnsupportedOperationException("Cannot join player");
  }

  public int join(Player player) {
    throw new UnsupportedOperationException("Cannot join player");
  }

  public int join(Player player, Desk desk, int place) {
    if ((place > 0) && (place <= getMaxPlayersAtTheTable()) && (desk.getPlacesList().isPlaceAvailable(place))) {
      int code = super.join(player);
      if (code == 1) {
        seatPlayer(desk, player, place);
      }
      return code;
    }
    return 5;
  }

  public Desk getDeskById(int id)
  {
    List list = getDesksList();
    synchronized (list) {
      Iterator iter = list.iterator();
      while (iter.hasNext()) {
        Desk desk = (Desk)iter.next();
        if (desk.getID() == id) {
          return desk;
        }
      }
      return null;
    }
  }

  public void calculateDirtyPoints(Player player)
  {
    if (PlayersClub.getInstance().getClubPlayers().isAMember(player))
    {
      if (fee.compareTo(new BigDecimal(60)) >= 0)
        player.increaseDirtyPoints(new BigDecimal(100));
      else if (fee.compareTo(new BigDecimal(40)) >= 0)
        player.increaseDirtyPoints(new BigDecimal(85));
      else if (fee.compareTo(new BigDecimal(26)) >= 0)
        player.increaseDirtyPoints(new BigDecimal(70));
      else if (fee.compareTo(new BigDecimal(16)) >= 0)
        player.increaseDirtyPoints(new BigDecimal(50));
      else if (fee.compareTo(new BigDecimal(12)) >= 0)
        player.increaseDirtyPoints(new BigDecimal(30));
      else if (fee.compareTo(new BigDecimal(9)) >= 0)
        player.increaseDirtyPoints(new BigDecimal(18));
      else if (fee.compareTo(new BigDecimal(5)) >= 0)
        player.increaseDirtyPoints(new BigDecimal(12));
      else if (fee.compareTo(new BigDecimal(2)) >= 0)
        player.increaseDirtyPoints(new BigDecimal(6));
      else if (fee.compareTo(new BigDecimal(1)) >= 0)
        player.increaseDirtyPoints(new BigDecimal(3));
    }
  }

  public String toDeskMenuXML(Player player)
  {
    XMLTag tag = new XMLTag("TT");

    tag.addParam("NM", name);
    synchronized (desksList) {
      if (desksList.size() > 0) {
        tag.addParam("PTYPE", ((Desk)desksList.get(0)).getPokerType());
        tag.addParam("LTYPE", ((Desk)desksList.get(0)).getLimitType());
        tag.addParam("PLAYERS", minStartPlayersCount);
      }
    }

    tag.addParam("CP", getTournamentAmount().floatValue());
    tag.addParam("POOL", getBuyIn().multiply(new BigDecimal(minStartPlayersCount)).setScale(2, 5).floatValue());

    tag.setTagContent(getCashedPrizeTableXML());

    tag.addParam("BUYIN", getBuyIn().floatValue());
    tag.addParam("FEE", fee.toString());

    tag.addParam("PLA", player.getAmount(getMoneyType()).floatValue());
    String xml = tag.toString();
    tag.invalidate();

    return xml;
  }
}