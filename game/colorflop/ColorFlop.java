package game.colorflop;

import game.Card;
import game.Desk;
import game.Game;
import game.Place;
import game.Player;
import game.messages.CommonStateMessagesList;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class ColorFlop
{
  public static int FLOP_FAILED = 0;
  public static int FLOP_RED = 1;
  public static int FLOP_BLACK = 2;

  public static BigDecimal MULTIPLYER = new BigDecimal(8).setScale(2, 5);
  private Desk desk;
  private final LinkedList betsStore = new LinkedList();

  public ColorFlop(Desk desk)
  {
    this.desk = desk;
  }

  public boolean registerStake(ColorStake stake) {
    synchronized (betsStore) {
      return betsStore.add(stake);
    }
  }

  public void determineWinners(ArrayList cards)
  {
    int type = FLOP_FAILED;

    Iterator i = cards.iterator();
    int lastSuite = -1;
    while (i.hasNext()) {
      Card card = (Card)i.next();
      if ((card.getSuite() == 1) || (card.getSuite() == 2)) {
        if (lastSuite == -1) {
          lastSuite = FLOP_BLACK;
          type = FLOP_BLACK;
        }
        else if (lastSuite != FLOP_BLACK) {
          type = FLOP_FAILED;
          break;
        }
      }
      else if ((card.getSuite() == 4) || (card.getSuite() == 3)) {
        if (lastSuite == -1) {
          lastSuite = FLOP_RED;
          type = FLOP_RED;
        }
        else if (lastSuite != FLOP_RED) {
          type = FLOP_FAILED;
          break;
        }

      }

    }

    if (type == FLOP_FAILED) {
      synchronized (betsStore) {
        Iterator iter = betsStore.iterator();
        while (iter.hasNext()) {
          ColorStake stake = (ColorStake)iter.next();
          Place place = desk.getPlayerPlace(stake.getPlayer());
          int placeNum = 0;
          if (place != null) {
            placeNum = place.getNumber();
          }

          desk.getGame().getPublicStateMessagesList().addCommonMessage(stake.getPlayer().getLogin(), 152, placeNum, 2, stake.getStake(), stake.getPlayer().getID());
        }

        clearBetsStorage();
      }
      return;
    }

    synchronized (betsStore) {
      Iterator iter = betsStore.iterator();
      while (iter.hasNext()) {
        ColorStake stake = (ColorStake)iter.next();
        if (stake.getType() == type) {
          BigDecimal winneredAmount = stake.getStake().multiply(MULTIPLYER).setScale(2, 5);

          stake.getPlayer().increaseAmount(winneredAmount, desk.getMoneyType());
          Place place = desk.getPlayerPlace(stake.getPlayer());
          int placeNum = 0;
          if (place != null) {
            placeNum = place.getNumber();
          }

          desk.getGame().getPublicStateMessagesList().addCommonMessage(stake.getPlayer().getLogin(), 150, placeNum, 2, winneredAmount, stake.getPlayer().getID());
        } else {
          Place place = desk.getPlayerPlace(stake.getPlayer());
          int placeNum = 0;
          if (place != null) {
            placeNum = place.getNumber();
          }

          desk.getGame().getPublicStateMessagesList().addCommonMessage(stake.getPlayer().getLogin(), 152, placeNum, 2, stake.getStake(), stake.getPlayer().getID());
        }
      }

      clearBetsStorage();
    }
  }

  public void processFlopNotReached()
  {
    synchronized (betsStore) {
      Iterator iter = betsStore.iterator();
      while (iter.hasNext()) {
        ColorStake stake = (ColorStake)iter.next();

        stake.getPlayer().increaseAmount(stake.getStake(), desk.getMoneyType());
        Place place = desk.getPlayerPlace(stake.getPlayer());
        int placeNum = 0;
        if (place != null) {
          placeNum = place.getNumber();
        }

        desk.getGame().getPublicStateMessagesList().addCommonMessage(stake.getPlayer().getLogin(), 151, placeNum, 2, stake.getStake(), stake.getPlayer().getID());
      }

      clearBetsStorage();
    }
  }

  private void clearBetsStorage()
  {
    synchronized (betsStore) {
      betsStore.clear();
    }
  }

  public Desk getDesk()
  {
    return desk;
  }
}