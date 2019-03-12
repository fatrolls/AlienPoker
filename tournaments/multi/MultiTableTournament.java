package tournaments.multi;

import commands.safeupdaters.FinishTournamentHistory;
import commands.safeupdaters.StartTournamentHistory;
import game.Desk;
import game.Game;
import game.Place;
import game.PlacesList;
import game.Player;
import game.messages.CommonStateMessagesList;
import game.messages.PrivateStateMessagesList;
import game.playerclub.ClubPlayers;
import game.playerclub.PlayersClub;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;
import tournaments.FeeList;
import tournaments.GameLevel;
import tournaments.Tournament;
import tournaments.Tournament.EliminatedPlayer;
import tournaments.Tournament.RankingPlayer;
import tournaments.Tournament.RedistributablePlayer;
import tournaments.TournamentLevels;
import tournaments.payout.MultiTournamentPayoutTable;
import utils.CommonLogger;
import utils.Log;

public class MultiTableTournament extends Tournament
{
  protected static final long MULTITOURNAMENT_SLEEP_PAUSE = 5000L;
  protected static final long MULTITOURNAMENT_PRE_END_PAUSE = 30000L;
  protected static final long DROP_DESK_DELAY = 120000L;
  private static final float REDISTRIBUTABLE_DESK_LOAD_FACTOR = 1.6F;
  private final HashMap rebuysHash = new HashMap();
  private final HashMap desksBreaksHash = new HashMap();
  private final HashMap seatersBuffer = new HashMap();
  private static final long REBUYS_TIME = 30000L;
  protected static final long MUTLTITOURNAMENT_SLEEP = 4000L;

  public MultiTableTournament(int id, String name, int tournamentType, int subType, int game, int gameType, int moneyType, BigDecimal tournamentAmount, BigDecimal maxBet, BigDecimal minBet, Date beginDate, Date regDate, BigDecimal buyIn, int increaseLevelAfter, long timeOnLevel, long break_period, long breakLength, int maxPlayersAtTheTable, int reBuys, int addons, BigDecimal addonsAmount, BigDecimal reBuysAmount, BigDecimal fee, int minStartPlayersCount, int tourSpeed)
  {
    super(id, name, tournamentType, subType, game, gameType, moneyType, tournamentAmount, maxBet, minBet, beginDate, regDate, buyIn, increaseLevelAfter, timeOnLevel, break_period, breakLength, maxPlayersAtTheTable, reBuys, addons, addonsAmount, reBuysAmount, fee, minStartPlayersCount, tourSpeed);
    currentMaxBet = maxBet;
    currentMinBet = minBet;

    GameLevel newLevel = tournamentLevels.getNextGameLevel(0);
    currentAnte = newLevel.getAnte();
    currentBringIn = newLevel.getBringIn();
    currentMinBet = newLevel.getMinBet();
    currentMaxBet = newLevel.getMaxBet();
  }

  public void run()
  {
    begin = false;
    regStatus = 2;

    while (beginDate.compareTo(new Date()) > 0) {
      try {
        if ((status == 0) && 
          (new Date().compareTo(regDate) > 0)) {
          status = 1;
          regStatus = 1;
          updateCashedXML();
        }

        Thread.sleep(5000L);
      } catch (InterruptedException e) {
        Log.out(e.getMessage());
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
    ArrayList beginDesks;
    synchronized (playersList)
    {
      int desksCount = playersList.size() % maxPlayersAtTheTable == 0 ? playersList.size() / maxPlayersAtTheTable : playersList.size() / maxPlayersAtTheTable + 1;
      beginDesks = createTournamentsDesksTables(desksCount, moneyType, gameType, currentMaxBet, currentMinBet, currentAnte, currentBringIn, game, buyIn, minPlayerRate, maxPlayersAtTheTable);
    }

    synchronized (desksList) {
      desksList.clear();
      desksList.addAll(beginDesks);
    }
    ArrayList players;
    synchronized (playersList) {
      players = new ArrayList(playersList.size());
      Iterator iter = playersList.iterator();
      while (iter.hasNext()) {
        Tournament.RankingPlayer player = (Tournament.RankingPlayer)iter.next();
        players.add(new Tournament.RedistributablePlayer(this, player, tournamentAmount));
      }

    }

    seatPlayers(beginDesks, players, maxPlayersAtTheTable);

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
                  timer.schedule(new DropDeskTask(desk, this), 120000L);
                }
              }

            }
            else if (size == 0) {
              break;
            }
          }
        }
        catch (InterruptedException e) {
          Log.out(e.getMessage());
        }
    }
    catch (Exception e)
    {
      Log.out(e.getMessage());
    }

    updateRanking(new ArrayList());

    end = true;
    status = 3;
    try
    {
      Thread.sleep(30000L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    this.timer.cancel();
    synchronized (desksList) {
      desksList.clear();
    }
    updateCashedXML();

    new Thread(new FinishTournamentHistory(this)).start();
  }

  public boolean needRedistributePlayers(Game game)
  {
    Desk desk = game.getDesk();
    synchronized (desk) {
      Iterator iter = desk.getPlacesList().allPlacesIterator();
      int allPlaces = 0;
      int playersCount = 0;
      while (iter.hasNext()) {
        Place place = (Place)iter.next();
        if (place.getPlayer() != null) {
          playersCount++;
        }
        allPlaces++;
      }
      if (playersCount > 0)
      {
        if (allPlaces / playersCount >= 1.6F) {
          return true;
        }
      }
    }

    return false;
  }

  public synchronized int redistributePlayers(Game game) {
    int redistributed = 0;

    synchronized (seatersBuffer)
    {
      if (seatersBuffer.size() > 0) {
        if (CommonLogger.getLogger().isInfoEnabled()) {
          CommonLogger.getLogger().info("SeatersBuffer activated");
        }
        Desk desk = game.getDesk();
        Iterator iter = desk.getPlacesList().allPlacesIterator();
        while (iter.hasNext()) {
          Place place = (Place)iter.next();
          if (place.getPlayer() == null)
          {
            synchronized (seatersBuffer) {
              Iterator pIter = seatersBuffer.entrySet().iterator();
              if (pIter.hasNext()) {
                Map.Entry entry = (Map.Entry)pIter.next();
                Integer deskId = (Integer)entry.getKey();
                PlayerAndAmount playerAndAmount = (PlayerAndAmount)entry.getValue();
                seatersBuffer.remove(deskId);
                synchronized (place) {
                  desk.seatPlayer(playerAndAmount.getPlayer(), place.getNumber(), playerAndAmount.getAmount());
                }

                if (CommonLogger.getLogger().isDebugEnabled()) {
                  CommonLogger.getLogger().debug("SeatersBuffer : RESEATED: " + playerAndAmount.getPlayer().getLogin());
                }

                playerAndAmount.getDesk().getGame().getPublicStateMessagesList().addCommonMessage(playerAndAmount.getPlayer().getLogin(), 104, place.getNumber(), playerAndAmount.getDesk().getID(), place.getAmount(), playerAndAmount.getPlayer().getID());
                desk.getPublicStateMessagesList().addCommonMessage(playerAndAmount.getPlayer().getLogin(), 60, place.getNumber(), 2);

                if (CommonLogger.getLogger().isInfoEnabled()) {
                  CommonLogger.getLogger().info("Public message <Reseating> was activated: Login: " + playerAndAmount.getPlayer().getLogin() + ". NewPlace: " + place.getNumber() + " DeskID: " + playerAndAmount.getDesk().getID() + " Amount: " + place.getAmount() + " PlayerID: " + playerAndAmount.getPlayer().getID());
                }

                Place tmpPlace = playerAndAmount.getDesk().getPlayerPlace(playerAndAmount.getPlayer());

                redistributed++;

                if (tmpPlace != null) {
                  playerAndAmount.getDesk().getGame().notifyAboutLeaveDesk(place);
                  tmpPlace.free();
                }

                Timer timer = new Timer();
                timer.schedule(new DropDeskTask(playerAndAmount.getDesk(), this), 360000L);

                if (CommonLogger.getLogger().isInfoEnabled()) {
                  CommonLogger.getLogger().info("DropDeskTask in SeatersBuffer activated");
                }

                break;
              }
            }
          }
        }

      }

    }

    if (needRedistributePlayers(game))
    {
      int currentDeskId = game.getDesk().getID();
      ArrayList desksList;
      synchronized (this.desksList) {
        int count = this.desksList.size();
        desksList = new ArrayList(count);
        for (int i = 0; i < count; i++) {
          Desk desk = (Desk)this.desksList.get(i);
          if ((!desk.isDeleted()) && (desk.getID() != currentDeskId)) {
            desksList.add(desk);
          }
        }

      }

      int availablePlaces = 0;

      HashMap deskPlayers = new HashMap();

      synchronized (game.getDesk()) {
        Desk desk = game.getDesk();
        Iterator iter = desk.getPlacesList().allPlacesIterator();
        while (iter.hasNext()) {
          Place place = (Place)iter.next();
          Player player = place.getPlayer();
          if (player != null) {
            deskPlayers.put(player, place.getAmount());
          }
        }
      }

      int desksCount = desksList.size();
      ArrayList shuffleList = new ArrayList(desksCount);
      for (int i = 0; i < desksCount; i++) {
        Desk desk = (Desk)desksList.get(i);
        Iterator iter = desk.getPlacesList().allPlacesIterator();
        while (iter.hasNext()) {
          Place place = (Place)iter.next();
          Player player = place.getPlayer();
          if (player == null) {
            availablePlaces++;
          }
        }

        shuffleList.add(desk);
      }

      if (deskPlayers.size() <= availablePlaces)
      {
        game.getDesk().setDeleted(true);
        HashMap playersAndNewDesks = new HashMap();
        Collections.shuffle(shuffleList);

        Iterator playersIter = deskPlayers.entrySet().iterator();
        while (playersIter.hasNext()) {
          Map.Entry entry = (Map.Entry)playersIter.next();
          Player player = (Player)entry.getKey();
          BigDecimal amount = (BigDecimal)entry.getValue();

          if ((player != null) && (amount != null)) {
            boolean notSeatedYet = true;
            for (int i = 0; (i < desksCount) && (deskPlayers.size() > 0) && (notSeatedYet); i++)
            {
              Desk desk = (Desk)desksList.get(i);
              Iterator iter = desk.getPlacesList().allPlacesIterator();
              while (iter.hasNext()) {
                Place place = (Place)iter.next();
                Player placePlayer = place.getPlayer();
                if (placePlayer == null) {
                  playersAndNewDesks.put(player, desk);

                  notSeatedYet = false;
                  synchronized (place) {
                    desk.seatPlayer(player, place.getNumber(), amount);
                  }

                  break;
                }

              }

            }

          }

        }

        Desk d = game.getDesk();
        Iterator iter = d.getPlacesList().allPlacesIterator();
        while (iter.hasNext()) {
          Place place = (Place)iter.next();
          Player pl = place.getPlayer();
          if (pl != null)
          {
            if (playersAndNewDesks.containsKey(pl)) {
              Desk newDesk = (Desk)playersAndNewDesks.get(pl);
              Place newPlace = newDesk.getPlayerPlace(pl);
              d.getGame().getPublicStateMessagesList().addCommonMessage(pl.getLogin(), 104, newPlace == null ? 0 : newPlace.getNumber(), newDesk.getID(), place.getAmount(), pl.getID());
              newDesk.getPublicStateMessagesList().addCommonMessage(pl.getLogin(), 60, newPlace == null ? 0 : newPlace.getNumber(), 2);

              if (CommonLogger.getLogger().isInfoEnabled()) {
                CommonLogger.getLogger().info("Public message <Reseating> was activated: Login: " + pl.getLogin() + ". NewPlace2: " + (newPlace == null ? 0 : newPlace.getNumber()) + " DeskID: " + newDesk.getID() + " Amount: " + place.getAmount() + " PlayerID: " + pl.getID());
              }

            }
            else if (CommonLogger.getLogger().isInfoEnabled()) {
              CommonLogger.getLogger().info("Warning: playersAndNewDesks.containsKey(pl) = false plID: " + pl.getID());
            }

            d.getGame().notifyAboutLeaveDesk(place);
            place.free();

            redistributed++;
          }

        }

        Timer timer = new Timer();
        timer.schedule(new DropDeskTask(d, this), 120000L);
      }
      else if (deskPlayers.size() == 1) {
        game.getDesk().setDeleted(true);
        Iterator playersIter = deskPlayers.entrySet().iterator();
        if (playersIter.hasNext()) {
          Map.Entry entry = (Map.Entry)playersIter.next();
          Player player = (Player)entry.getKey();
          Place place = game.getDesk().getPlayerPlace(player);
          if (place != null) {
            synchronized (seatersBuffer) {
              seatersBuffer.put(new Integer(game.getDesk().getID()), new PlayerAndAmount(player, place.getAmount(), game.getDesk()));
            }
            synchronized (this.desksList) {
              if (this.desksList.size() > 1) {
                place.getStateMessagesList().addPrivateMessage(111);

                if (CommonLogger.getLogger().isInfoEnabled()) {
                  CommonLogger.getLogger().info("Private Message <Seating> was activated. Place:" + place.getNumber());
                }
              }
            }
          }
        }
      }

    }

    return redistributed;
  }

  public void dropDeskAfterSomeTime(Desk desk) {
    Timer timer = new Timer();
    timer.schedule(new DropDeskTask(desk, this), 120000L);
  }

  public boolean canRequestRebuy(Desk desk, Player player)
  {
    Object o;
    synchronized (desksBreaksHash) {
      o = desksBreaksHash.get(new Integer(desk.getID()));
    }
    if (reBuysQty <= 0)
      return false;
    if (o == null)
    {
      Object playerRebuy;
      synchronized (rebuysHash) {
        playerRebuy = rebuysHash.get(new Integer(player.getID()));
      }
      if (playerRebuy == null) {
        return true;
      }
      Integer count = (Integer)playerRebuy;
      return count.intValue() < reBuysQty;
    }

    return false;
  }

  public boolean canRequestAddon(Desk desk)
  {
    synchronized (desksBreaksHash) {
      Object o = desksBreaksHash.get(new Integer(desk.getID()));
      if (addonsQty <= 0)
        return false;
      if (o == null) {
        return true;
      }
      Integer count = (Integer)o;
      return count.intValue() < addonsQty - 1;
    }
  }

  public void increaseRebuys(Player player)
  {
    synchronized (rebuysHash) {
      Object playerRebuy = rebuysHash.get(new Integer(player.getID()));
      if (playerRebuy == null) {
        rebuysHash.put(new Integer(player.getID()), new Integer(1));
      } else {
        Integer count = (Integer)playerRebuy;
        rebuysHash.put(new Integer(player.getID()), new Integer(count.intValue() + 1));
      }
    }
  }

  public void increaseAddons(Desk desk)
  {
    synchronized (desksBreaksHash) {
      Object deskAddon = desksBreaksHash.get(new Integer(desk.getID()));
      if (deskAddon == null) {
        desksBreaksHash.put(new Integer(desk.getID()), new Integer(1));
      } else {
        Integer count = (Integer)deskAddon;
        desksBreaksHash.put(new Integer(desk.getID()), new Integer(count.intValue() + 1));
      }
    }
  }

  private void makeBreak(Desk desk)
  {
    boolean makeAddon;
    synchronized (this) {
      makeAddon = canRequestAddon(desk);

      if ((status != 5) && (status != 6) && (status != 4) && (status != 3))
      {
        Date currentDate = new Date();
        if (nextBreakTime.compareTo(currentDate) <= 0) {
          status = 6;
          nextBreakTime = new Date(currentDate.getTime() + breakPeriod + breakLength);

          nextLevelTime = new Date(nextLevelTime.getTime() + breakLength);

          synchronized (currentAddonPlayers) {
            currentAddonPlayers.clear();
          }

          synchronized (desksList) {
            int size = desksList.size();
            for (int i = 0; i < size; i++) {
              Desk d = (Desk)desksList.get(i);
              Iterator iter = d.getPlacesList().allPlacesIterator();
              while (iter.hasNext()) {
                Player player = ((Place)iter.next()).getPlayer();
                if (player != null) {
                  currentAddonPlayers.add(player);
                }
              }
            }

          }

          Timer timer = new Timer();
          timer.schedule(new EndBreakTimerTask(2), breakLength);
        }
      }
    }

    if ((makeAddon) && (status == 6))
    {
      makeAddonRound(desk, breakLength);
      increaseAddons(desk);
    }
  }

  private void makeAddonRound(Desk desk, long breakLength)
  {
    Iterator iter = desk.getPlacesList().allPlacesIterator();
    while (iter.hasNext()) {
      Place place = (Place)iter.next();
      if ((place.isBusy()) && 
        (place.getPlayer().getAmount(getMoneyType()).compareTo(addonsPayment) >= 0))
        place.getStateMessagesList().addPrivateMessage(107, (int)breakLength / 1000, false, addonsPayment, new BigDecimal(0), addonsAmount, new BigDecimal(0), new BigDecimal(0));
    }
  }

  public void suggestRebuys(Desk desk)
  {
    synchronized (desksBreaksHash) {
      if (desksBreaksHash.get(new Integer(desk.getID())) != null) {
        return;
      }
    }

    HashMap tmpMap = new HashMap();

    Iterator iter = desk.getPlacesList().allPlacesIterator();
    while (iter.hasNext()) {
      Place place = (Place)iter.next();
      Player player = place.getPlayer();
      if ((player != null) && 
        (place.isBusy()) && (place.getAmount().floatValue() < desk.getMaxBet().floatValue()) && 
        (canRequestRebuy(desk, player))) {
        place.getStateMessagesList().addPrivateMessage(108, 30, false, reBuysPayment, new BigDecimal(0), reBuysAmount, new BigDecimal(0), new BigDecimal(0));
        synchronized (currentReBuysPlayers) {
          currentReBuysPlayers.put(new Integer(player.getID()), new Integer(1));
          tmpMap.put(new Integer(player.getID()), new Integer(1));
        }

      }

    }

    if (tmpMap.size() > 0)
    {
      try {
        Thread.sleep(30000L);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      synchronized (currentReBuysPlayers) {
        Iterator mapIter = tmpMap.entrySet().iterator();
        while (mapIter.hasNext()) {
          Map.Entry entry = (Map.Entry)mapIter.next();
          if (currentReBuysPlayers.containsKey(entry.getKey()))
            currentReBuysPlayers.remove(entry.getKey());
        }
      }
    }
  }

  public synchronized void updateDeskLevel(Desk desk)
  {
    Date currentDate = new Date();

    makeBreak(desk);

    if (currentDate.getTime() >= nextLevelTime.getTime())
    {
      currentLevel += 1;
      nextLevelTime = new Date(new Date().getTime() + timeOnLevel);

      GameLevel newLevel = tournamentLevels.getNextGameLevel(currentLevel);

      if (CommonLogger.getLogger().isDebugEnabled()) {
        CommonLogger.getLogger().debug("<Level Changed> - ID: " + desk.getID() + " <Level>: " + newLevel.getLevel() + " <Games>: " + currentLevel * 10);
      }

      desk.setMaxBet(newLevel.getMaxBet());
      desk.setMinBet(newLevel.getMinBet());
      desk.setAnte(newLevel.getAnte());
      desk.setBringIn(newLevel.getBringIn());

      desk.getGame().getPublicStateMessagesList().addCommonMessage(103, newLevel.getMinBet(), newLevel.getMaxBet(), newLevel.getAnte(), newLevel.getBringIn(), newLevel.getLevel());

      currentLevel = newLevel.getLevel();
      currentMaxBet = newLevel.getMaxBet();
      currentMinBet = newLevel.getMinBet();
      currentAnte = newLevel.getAnte();
      currentBringIn = newLevel.getBringIn();
    }

    desk.setMaxBet(currentMaxBet);
    desk.setMinBet(currentMinBet);
  }

  public BigDecimal getAmountByWinneredPlace(int place)
  {
    MultiTournamentPayoutTable m = MultiTournamentPayoutTable.getInstance();
    int playersSize;
    synchronized (playersList) {
      playersSize = playersList.size();
    }
    return feeList.getFeeAmount().multiply(m.getPercent(place, playersSize)).setScale(2, 5);
  }

  public synchronized void updateRanking(ArrayList eliminatedPlayers)
  {
    synchronized (playersList) {
      int size = playersList.size();
      for (int i = 0; i < size; i++) {
        Tournament.RankingPlayer rankingPlayer = (Tournament.RankingPlayer)playersList.get(i);
        int elSize = eliminatedPlayers.size();
        for (int j = 0; j < elSize; j++) {
          Tournament.EliminatedPlayer eliminatedPlayer = (Tournament.EliminatedPlayer)eliminatedPlayers.get(j);
          if (rankingPlayer.getPlayer().getID() == eliminatedPlayer.getPlayer().getID()) {
            rankingPlayer.setRank(eliminatedPlayer.getRank());
            rankingPlayer.setWinneredAmount(eliminatedPlayer.getAmount());
            rankingPlayer.setStatus(0);
            break;
          }
        }
      }

      Collections.sort(playersList, new PlayersSorter());

      size = playersList.size();
      for (int i = 0; i < size; i++) {
        Tournament.RankingPlayer rankingPlayer = (Tournament.RankingPlayer)playersList.get(i);
        if (rankingPlayer.getStatus() == 0) {
          break;
        }
        rankingPlayer.setRank(i + 1);
      }
      Collections.sort(playersList, new PlayersSorter());
    }
  }

  public void calculateDirtyPoints(Player player)
  {
    if (PlayersClub.getInstance().getClubPlayers().isAMember(player))
    {
      if (fee.compareTo(new BigDecimal(40)) >= 0)
        player.increaseDirtyPoints(new BigDecimal(40));
      else if (fee.compareTo(new BigDecimal(26)) >= 0)
        player.increaseDirtyPoints(new BigDecimal(30));
      else if (fee.compareTo(new BigDecimal(15)) >= 0)
        player.increaseDirtyPoints(new BigDecimal(25));
      else if (fee.compareTo(new BigDecimal(12)) >= 0)
        player.increaseDirtyPoints(new BigDecimal(15));
      else if (fee.compareTo(new BigDecimal(9)) >= 0)
        player.increaseDirtyPoints(new BigDecimal(8));
      else if (fee.compareTo(new BigDecimal(5)) >= 0)
        player.increaseDirtyPoints(new BigDecimal(5));
      else if (fee.compareTo(new BigDecimal(2)) >= 0)
        player.increaseDirtyPoints(new BigDecimal(2));
      else if (fee.compareTo(new BigDecimal(1)) >= 0)
        player.increaseDirtyPoints(new BigDecimal(1));
    }
  }

  private class PlayerAndAmount
  {
    private Player player;
    private BigDecimal amount;
    private Desk desk;

    public PlayerAndAmount(Player player, BigDecimal amount, Desk desk)
    {
      this.player = player;
      this.amount = amount;
      this.desk = desk;
    }

    public Player getPlayer() {
      return player;
    }

    public BigDecimal getAmount() {
      return amount;
    }

    public Desk getDesk() {
      return desk;
    }
  }

  private class EndBreakTimerTask extends TimerTask
  {
    private int nextStatus = 0;

    public EndBreakTimerTask(int nextStatus) {
      this.nextStatus = nextStatus;
    }

    public void run() {
      MultiTableTournament.access$002(MultiTableTournament.this, nextStatus);
      synchronized (currentAddonPlayers) {
        currentAddonPlayers.clear();
      }
    }
  }

  protected class DropDeskTask extends TimerTask
  {
    private Desk desk = null;
    private Tournament tournament = null;

    public DropDeskTask(Desk desk, Tournament t) {
      this.desk = desk;
      tournament = t;
    }

    public void run() {
      List list = tournament.getDesksList();
      synchronized (list) {
        list.remove(desk);
      }
    }
  }

  public class PlayersSorter
    implements Comparator
  {
    public PlayersSorter()
    {
    }

    public int compare(Object o1, Object o2)
    {
      Tournament.RankingPlayer r1 = (Tournament.RankingPlayer)o1;
      Tournament.RankingPlayer r2 = (Tournament.RankingPlayer)o2;

      if (r1.getStatus() != r2.getStatus()) {
        if (r1.getStatus() == 0) {
          return 8;
        }
        return -8;
      }
      if ((r1.getStatus() == 1) && (r2.getStatus() == 1)) {
        if (r1.getAmount().compareTo(r2.getAmount()) > 0)
          return -10;
        if (r1.getAmount().compareTo(r2.getAmount()) < 0)
          return 10;
      }
      else if ((r1.getStatus() == 0) && (r2.getStatus() == 0)) {
        if (r1.getRank() < r2.getRank())
          return -10;
        if (r1.getRank() > r2.getRank()) {
          return 10;
        }
      }

      return 0;
    }
  }
}