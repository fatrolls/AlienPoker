package tournaments.sitandgo;

import commands.safeupdaters.AddWinnerToHistory;
import commands.safeupdaters.FinishTournamentHistory;
import commands.safeupdaters.StartTournamentHistory;
import game.Desk;
import game.Game;
import game.Place;
import game.PlacesList;
import game.Player;
import game.amounts.PlayerAmount;
import game.messages.CommonStateMessagesList;
import game.playerclub.ClubPlayers;
import game.playerclub.PlayersClub;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import tournaments.FeeList;
import tournaments.GameLevel;
import tournaments.ParametersForReset;
import tournaments.Tournament;
import tournaments.TournamentLevels;
import tournaments.TournamentWinners;
import tournaments.TournamentWinners.WinnerPlace;
import utils.Log;
import utils.xml.XMLTag;

public class SitAndGoTournament extends Tournament
{
  public static final String OUT_PARAM_NUM = "NUM";
  public static final String OUT_PARAM_PRIZE = "PRIZE";
  private static final long SIT_AND_GO_SLEEP = 4000L;
  private static final double SIT_AND_GO_FIRST_PLACE = 0.5D;
  private static final double SIT_AND_GO_SECOND_PLACE = 0.3D;
  private static final double SIT_AND_GO_THIRD_PLACE = 0.2D;
  public static final int LEAVE_DESK_DELAY = 1000;
  private static final long SITANDGO_PRE_END_PAUSE = 10000L;

  public SitAndGoTournament(int id, String name, int tournamentType, int subType, int game, int gameType, int moneyType, BigDecimal tournamentAmount, BigDecimal maxBet, BigDecimal minBet, Date beginDate, Date regDate, BigDecimal buyIn, int increaseLevelAfter, long timeOnLevel, long break_period, long breakLength, int maxPlayersAtTheTable, int reBuys, int addons, BigDecimal addonsAmount, BigDecimal reBuysAmount, BigDecimal fee, int minStartPlayersCount, int tourSpeed)
  {
    super(id, name, tournamentType, subType, game, gameType, moneyType, tournamentAmount, maxBet, minBet, beginDate, regDate, buyIn, increaseLevelAfter, timeOnLevel, break_period, breakLength, maxPlayersAtTheTable, reBuys, addons, addonsAmount, reBuysAmount, fee, minStartPlayersCount, tourSpeed);

    currentMaxBet = maxBet;
    currentMinBet = minBet;

    GameLevel newLevel = tournamentLevels.getNextGameLevel(0);
    currentAnte = newLevel.getAnte();
    currentBringIn = newLevel.getBringIn();
    currentMinBet = newLevel.getMinBet();
    currentMaxBet = newLevel.getMaxBet();

    ArrayList beginDesks = createTournamentsDesksTables(1, moneyType, gameType, currentMaxBet, currentMinBet, currentAnte, currentBringIn, game, buyIn, minPlayerRate, maxPlayersAtTheTable);

    if (beginDesks.size() > 0) {
      Desk desk = (Desk)beginDesks.get(0);
      GameLevel level = tournamentLevels.getDeskLevel(desk.getID());
      desk.setMinBet(level.getMinBet());
      desk.setMaxBet(level.getMaxBet());
    }

    synchronized (desksList) {
      desksList.clear();
      desksList.addAll(beginDesks);
    }

    begin = false;
    end = false;
  }

  public void run()
  {
    status = 1;

    Desk desk = null;
    synchronized (desksList) {
      if (desksList.size() > 0) {
        desk = (Desk)desksList.get(0);
      }
    }

    if (desk != null) {
      while (!begin) {
        int count = 0;
        synchronized (desk) {
          Iterator iter = desk.getPlacesList().allPlacesIterator();
          while (iter.hasNext()) {
            Place place = (Place)iter.next();
            if (place.isBusy()) count++;
          }
        }

        if (count < minStartPlayersCount) {
          try {
            Thread.sleep(4000L);
          } catch (InterruptedException e) {
            Log.out(e.getMessage());
          }
        } else {
          begin = true;
          new Thread(new StartTournamentHistory(this)).start();
        }
        updateStatus();
      }

      while (!end) {
        try {
          Thread.sleep(4000L);
          synchronized (desk) {
            if (desk.getPlayersCount() < 2) {
              end = true;
              Thread.sleep(4000L);
              Iterator iter = desk.getPlacesList().allPlacesIterator();
              while (iter.hasNext()) {
                Place place = (Place)iter.next();
                if (place.isBusy()) {
                  Player player = place.getPlayer();
                  tournamentWinners.addWinner(player);
                  TournamentWinners.WinnerPlace winnerPlace = tournamentWinners.getWinnerPlace(player);
                  if (winnerPlace != null)
                  {
                    System.out.println(player.getLogin() + " NOTIFYED LAST " + winnerPlace.getPlace());

                    desk.getGame().getPublicStateMessagesList().addCommonMessage(player.getLogin(), 102, place.getNumber(), winnerPlace.getPlace(), getAmountByWinneredPlace(winnerPlace.getPlace()), player.getID());
                    desk.getGame().notifyAboutLeaveDesk(place);
                    place.free();

                    new Thread(new AddWinnerToHistory(this, winnerPlace.getPlace(), getAmountByWinneredPlace(winnerPlace.getPlace()), player)).start();
                  }
                  else {
                    Log.out("Tournament.eliminatePlayers Error: winnerPlace is null tournament: " + getID() + " Name: " + getName());
                  }
                }
              }
            }
          }
          updateStatus();
        } catch (InterruptedException e) {
          Log.out(e.getMessage());
        }
      }

    }

    for (int place = 1; place <= 3; place++) {
      ArrayList winners = tournamentWinners.getWinnerByPlace(place);
      int size = winners.size();
      Iterator iter = winners.iterator();
      while (iter.hasNext()) {
        Player p = (Player)iter.next();
        p.increaseAmount(feeList.getFeeAmount().divide(new BigDecimal(size), 2, 5), getMoneyType());
        p.getPlayerAmount().deleteTournamentRecord(this);
      }
    }
    updateStatus();
    try
    {
      Thread.sleep(10000L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    if (end) {
      timer.cancel();
    }

    parametersForReset.launchNewTournament();

    List list = Tournament.getTournamentsList();
    synchronized (list) {
      list.remove(this);
    }

    new Thread(new FinishTournamentHistory(this)).start();
  }

  private void updateStatus()
  {
    if ((!begin) && (!end))
    {
      int playersSize;
      synchronized (playersList) {
        playersSize = playersList.size();
      }
      if (playersSize < minStartPlayersCount) {
        switch (minStartPlayersCount - playersSize) {
        case 1:
          status = 11;
          break;
        case 2:
          status = 12;
          break;
        case 3:
          status = 13;
          break;
        case 4:
          status = 14;
          break;
        case 5:
          status = 15;
          break;
        case 6:
          status = 16;
          break;
        case 7:
          status = 17;
          break;
        case 8:
          status = 18;
          break;
        case 9:
          status = 19;
          break;
        case 10:
          status = 20;
          break;
        default:
          status = 1;
        }
      }

    }
    else if (!end) {
      switch (currentLevel) {
      case 1:
        status = 31;
        break;
      case 2:
        status = 32;
        break;
      case 3:
        status = 33;
        break;
      case 4:
        status = 34;
        break;
      case 5:
        status = 35;
        break;
      case 6:
        status = 36;
        break;
      case 7:
        status = 37;
        break;
      case 8:
        status = 38;
        break;
      case 9:
        status = 39;
        break;
      case 10:
        status = 40;
        break;
      default:
        status = 2;

        break;
      } } else {
      status = 3;
    }
  }

  public boolean unjoin(Player player) {
    if (super.unjoin(player)) {
      Desk desk = null;
      synchronized (desksList) {
        if (desksList.size() > 0) {
          desk = (Desk)desksList.get(0);
        }
      }
      if (desk != null) {
        Place place = desk.getPlacesList().getPlace(player);
        if (place != null) {
          Game game = desk.getGame();
          if (game != null) {
            game.notifyAboutLeaveDesk(place);
          }
          place.free();
          Timer timer = new Timer();
          timer.schedule(new LeaveDeskMessageSender(desk, place.getNumber(), player.getLogin()), 1000L);
        }
      }
    }
    return false;
  }

  public void calculateDirtyPoints(Player player) {
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

  public BigDecimal getAmountByWinneredPlace(int place)
  {
    switch (place) {
    case 1:
      return feeList.getFeeAmount().multiply(new BigDecimal(0.5D)).setScale(2, 5);
    case 2:
      return feeList.getFeeAmount().multiply(new BigDecimal(0.3D)).setScale(2, 5);
    case 3:
      return feeList.getFeeAmount().multiply(new BigDecimal(0.2D)).setScale(2, 5);
    }
    return new BigDecimal(0);
  }

  public int join(Player player, int placeNumber)
  {
    Desk desk = null;
    synchronized (desksList) {
      if (desksList.size() > 0) {
        desk = (Desk)desksList.get(0);
      }
    }

    if (desk == null)
      return 4;
    int code;
    synchronized (desk)
    {
      int code;
      if (desk.isPlayerOnDesk(player)) {
        code = 0;
      }
      else
      {
        int code;
        if (!desk.isPlaceAvailable(placeNumber)) {
          code = 5;
        } else {
          code = join(player);
          if (code == 1)
            desk.seatPlayer(player, placeNumber, getTournamentAmount());
        }
      }
    }
    return code;
  }

  public String toDeskMenuXML(Player player) {
    return toXML(player);
  }

  public XMLTag toXMLTag()
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

    XMLTag prizeTag = new XMLTag("PZ");
    XMLTag first = new XMLTag("WPL");
    first.addParam("NUM", 1);
    first.addParam("PRIZE", 50);
    prizeTag.addNestedTag(first);
    XMLTag second = new XMLTag("WPL");
    second.addParam("NUM", 2);
    second.addParam("PRIZE", 30);
    prizeTag.addNestedTag(second);
    XMLTag third = new XMLTag("WPL");
    third.addParam("NUM", 3);
    third.addParam("PRIZE", 20);
    prizeTag.addNestedTag(third);

    tag.addNestedTag(prizeTag);

    tag.addParam("BUYIN", getBuyIn().floatValue());
    tag.addParam("FEE", fee.toString());

    return tag;
  }

  public String toXML()
  {
    XMLTag tag = toXMLTag();
    String xml = tag.toString();
    tag.invalidate();

    return xml;
  }

  public String toXML(Player player) {
    XMLTag tag = toXMLTag();
    tag.addParam("PLA", player.getAmount(getMoneyType()).floatValue());
    String xml = tag.toString();
    tag.invalidate();

    return xml;
  }

  private class LeaveDeskMessageSender extends TimerTask {
    private int placeNumber = 0;
    private Desk desk = null;
    private String login = null;

    public LeaveDeskMessageSender(Desk desk, int placeNumber, String login) {
      this.placeNumber = placeNumber;
      this.desk = desk;
      this.login = login;
    }

    public void run() {
      desk.getPublicStateMessagesList().addCommonMessage(login, 30, placeNumber, 2);
    }
  }
}