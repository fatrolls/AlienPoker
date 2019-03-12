package game.pokerrounds;

import game.Desk;
import game.ExecutionState;
import game.Game;
import game.Place;
import game.PlacesList;
import game.Player;
import game.Stake;
import game.TexasHoldem;
import game.pokerrounds.stakescalculators.PokerStakesCalculator;
import game.pokerrounds.stakescalculators.StakesCalculatorFactory;
import game.stakeactions.BetStakeAction;
import game.stakeactions.CallStakeAction;
import game.stakeactions.CheckStakeAction;
import game.stakeactions.FoldStakeAction;
import game.stakeactions.RaiseStakeAction;
import game.stakeactions.StakeAction;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;

public abstract class StakesPokerRound
  implements PokerRound
{
  Logger log;
  private Game game;
  private ExecutionState state;
  private int startAfterPlaceNumber;
  private int actionCode;
  private final HashMap raiseMap = new HashMap();
  private int currentPlaceNumber;
  private BigDecimal stakeBase;
  private BigDecimal currentStake;
  private int roundEndPlaceNumber;
  private Stake lastStake;
  private int round;
  private int raisesCount;
  private boolean canBet;
  private static final int AVAILABLE_RAISES = 3;
  private BigDecimal needBet;
  private BigDecimal needCall;
  private BigDecimal needRaise;
  private BigDecimal needAnte;
  private BigDecimal needBringIn;
  private BigDecimal maxStake;
  private boolean canCheck;
  private boolean allIn;
  private BigDecimal defaultNeedBet;
  private BigDecimal defaultNeedRaise;
  private boolean defaultCanCheck;
  private boolean usePredefinedNeedBet;
  private boolean usePredefinedNeedRaise;
  private boolean usePredefinedCanCheck;
  private final ArrayList allInPlaces = new ArrayList();

  public StakesPokerRound()
  {
    log = Logger.getLogger(StakesPokerRound.class);
    game = null;
    state = new ExecutionState(false);
    startAfterPlaceNumber = 0;
    actionCode = 0;
    currentPlaceNumber = 0;
    stakeBase = new BigDecimal(0);
    currentStake = new BigDecimal(0);
    roundEndPlaceNumber = 0;
    lastStake = null;
    round = 0;
    raisesCount = 0;
    canBet = true;
    needBet = new BigDecimal(0);
    needCall = new BigDecimal(0);
    needRaise = new BigDecimal(0);
    needAnte = new BigDecimal(0);
    needBringIn = new BigDecimal(0);
    maxStake = new BigDecimal(0);
    canCheck = false;
    allIn = false;
    defaultNeedBet = new BigDecimal(0);
    defaultNeedRaise = new BigDecimal(0);
    defaultCanCheck = false;
    usePredefinedNeedBet = false;
    usePredefinedNeedRaise = false;
    usePredefinedCanCheck = false;
  }

  public void setAllIn(boolean paramBoolean)
  {
    allIn = paramBoolean;
  }

  public boolean canBet()
  {
    return canBet;
  }

  public int getRaisesCount()
  {
    return raisesCount;
  }

  public void betProcessed()
  {
    canBet = false;
  }

  public void setGame(Game paramGame)
  {
    game = paramGame;
  }

  public void setStartAfterPlaceNumber(int paramInt)
  {
    startAfterPlaceNumber = paramInt;
  }

  public ExecutionState getExecutionState()
  {
    return state;
  }

  public int getCurrentPlaceNumber()
  {
    return currentPlaceNumber;
  }

  public void setCurrentPlaceNumber(int paramInt)
  {
    currentPlaceNumber = paramInt;
  }

  public boolean hasReRaise(Player paramPlayer)
  {
    if (paramPlayer != null)
    {
      Object localObject1;
      synchronized (raiseMap)
      {
        localObject1 = raiseMap.get(new Integer(paramPlayer.getID()));
      }
      if (localObject1 == null) {
        return false;
      }
      return ((Integer)localObject1).intValue() >= 2;
    }

    return false;
  }

  public void incReRaise(Player paramPlayer)
  {
    if (paramPlayer != null)
      synchronized (raiseMap)
      {
        Object localObject1 = raiseMap.get(new Integer(paramPlayer.getID()));
        if (localObject1 == null)
          raiseMap.put(new Integer(paramPlayer.getID()), new Integer(1));
        else
          raiseMap.put(new Integer(paramPlayer.getID()), new Integer(((Integer)localObject1).intValue() + 1));
      }
  }

  public abstract void complited();

  public abstract void init();

  public void sleepBeforeRun() throws RuntimeException
  {
    try {
      log.debug("TexasHoldem.PAUSE");
      Thread.sleep(4000L);
    }
    catch (InterruptedException localInterruptedException)
    {
      throw new RuntimeException(localInterruptedException);
    }
  }
  public void run() {
    int i = 1;
    int j = 0;
    sleepBeforeRun();
    int k;
    do {
      if (isNeedEndGame())
      {
        getGame().checkAllInPrizeAmount();
        endGame();
        return;
      }
      if (i != 0)
      {
        j = getStartAfterPlaceNumber();
        setRoundEndPlaceNumber(j);
        i = 0;
      }
      else {
        j = getCurrentPlaceNumber();
      }
      Place localPlace1 = getGame().getPlacesList().getNextPlace(j);
      if (localPlace1 != null);
      setCurrentPlaceNumber(localPlace1.getNumber());
      definePosibleStakes(localPlace1);
      k = 0;
      int m = 0;
      if (getRoundEndPlaceNumber() == -1)
      {
        getGame().checkAllInPrizeAmount();
        endGame();
        return;
      }
      int n = localPlace1.getNumber() == getRoundEndPlaceNumber() ? 1 : 0;
      if (n == 0)
      {
        int i1 = -1;
        int i2 = 0;

        while ((localPlace1.getNumber() != i1) && (i2 < 10))
        {
          i2++;
          Place localPlace2 = getGame().getPlacesList().getNextDeskPlace(i1 != -1 ? i1 : localPlace1.getNumber());
          if (localPlace2 == null)
            break;
          i1 = localPlace2.getNumber();
          if (localPlace2.isActive())
            break;
          if (i1 == getRoundEndPlaceNumber())
          {
            System.out.println("WOW !!! ## tmpPlaceNum == getRoundEndPlaceNumber() :)");
            n = 1;
          }
        }
      }
      if (n != 0)
      {
        if (canLastPlaceContinueStaking())
        {
          if (processPlaceAction(localPlace1) > 0)
          {
            Stake localStake = getLastStake();
            if ((localStake != null) && (localStake.getType() != 2) && (localStake.getType() != 4)) {
              k = 1;
            }
            else if ((canBet()) && (localStake.getType() != 3))
              k = 1;
          }
          else {
            k = 1;
          }
        }
        else {
          k = 1;
        }
      }
      else {
        m = 1;
      }
      if (m != 0)
        processPlaceAction(localPlace1); 
    }
    while (k == 0);
    getGame().checkAllInPrizeAmount();
    complited();
  }

  boolean isNonAllInPlayersMakeTheirBets()
  {
    if (getGame().isHasAllIn())
    {
      Iterator localIterator = getGame().getPlacesList().iterator();
      BigDecimal localBigDecimal = new BigDecimal(0);
      Place localPlace;
      while (localIterator.hasNext())
      {
        localPlace = (Place)localIterator.next();
        if ((localPlace.isAllIn()) && (localPlace.getStakingAmount().floatValue() > localBigDecimal.floatValue()))
          localBigDecimal = localPlace.getStakingAmount();
      }
      if (localBigDecimal.floatValue() > 0.0F)
      {
        for (localIterator = getGame().getPlacesList().iterator(); localIterator.hasNext(); )
        {
          localPlace = (Place)localIterator.next();
          if ((localPlace.isActive()) && (!localPlace.isAllIn()) && (localPlace.getStakingAmount().floatValue() < localBigDecimal.floatValue())) {
            return false;
          }
        }
        if (!((TexasHoldem)getGame()).twoСards)
          return false;
      }
    }
    return true;
  }

  public void checkAllInPrizeAmount()
  {
    synchronized (allInPlaces)
    {
      Iterator localIterator1 = allInPlaces.iterator();

      while (localIterator1.hasNext())
      {
        Place localPlace1 = (Place)localIterator1.next();
        if (localPlace1.getAllInPretendedAmount().floatValue() <= 0.0F)
        {
          Iterator localIterator2 = getGame().getPlacesList().allPlacesIterator();
          BigDecimal localBigDecimal = new BigDecimal(0);
          while (localIterator2.hasNext())
          {
            Place localPlace2 = (Place)localIterator2.next();

            if (localPlace2.isBusy())
            {
              if (localPlace2.getStakingAmountCache().floatValue() > 0.0F)
              {
                log.info("AddingUpForAllInPrizeAmount: " + localPlace2.getPlayer().getLogin() + " = " + localPlace2.getStakingAmountCache().floatValue());

                if (localPlace2.getStakingAmountCache().floatValue() > localPlace1.getStakingAmountCache().floatValue())
                  localBigDecimal = localBigDecimal.add(localPlace1.getStakingAmountCache()).setScale(2, 5);
                else {
                  localBigDecimal = localBigDecimal.add(localPlace2.getStakingAmountCache()).setScale(2, 5);
                }
              }

            }

          }

          log.info("checkAllInPrizeAmount: " + localPlace1.getPlayer().getLogin() + " = " + localBigDecimal.floatValue());

          localPlace1.setAllInPretendedAmount(getGame().getGameAmount().add(localBigDecimal).setScale(2, 5));
        }
      }
    }
  }

  public void notifyAboutAllIn(Place paramPlace)
  {
    synchronized (allInPlaces)
    {
      if ((paramPlace.isAllIn()) && (paramPlace.getAllInAmount().floatValue() > 0.0F))
        allInPlaces.add(paramPlace);
    }
  }

  public void notifyAboutLeaveDesk(Place paramPlace)
  {
    if (paramPlace.getNumber() == getRoundEndPlaceNumber())
    {
      Place localPlace = getGame().getPlacesList().getPrevPlace(paramPlace);
      if (localPlace != null)
        setRoundEndPlaceNumber(localPlace.getNumber());
      else
        setRoundEndPlaceNumber(-1);
    }
  }

  public void notifyAboutFold(Place paramPlace)
  {
    if (paramPlace.getNumber() == getRoundEndPlaceNumber())
    {
      Place localPlace = getGame().getPlacesList().getPrevPlace(paramPlace);
      if (localPlace != null)
        setRoundEndPlaceNumber(localPlace.getNumber());
    }
  }

  public boolean canLastPlaceContinueStaking()
  {
    return canBet();
  }

  public void endGame()
  {
    if (getGame().isHasAllIn())
    {
      getGame().countFlopPercent();
      getGame().checkAllInPrizeAmount();
      getGame().updateGameAmount();
      getGame().prematureAllInEnd();
    }
    else {
      getGame().countFlopPercent();
      getGame().updateGameAmount();
      getGame().end(getGame().getCurrentGameId());
    }
  }

  public boolean isNeedEndGame()
  {
    if (getGame().getPlacesList().getActivePlayersCount() < getGame().getMinPlayers())
    {
      if ((getGame().isHasAllIn()) && (!isNonAllInPlayersMakeTheirBets()))
      {
        System.out.println("!isNonAllInPlayersMakeTheirBets");
        return false;
      }

      return true;
    }

    return false;
  }

  public void definePosibleStakes(Place paramPlace)
  {
    PokerStakesCalculator localPokerStakesCalculator = StakesCalculatorFactory.getStakesCalculator(getGame().getDesk().getLimitType());
    localPokerStakesCalculator.setPlace(paramPlace);
    localPokerStakesCalculator.setStakesPokerRound(this);
    localPokerStakesCalculator.calculate();
  }

  public boolean canRaise()
  {
    return raisesCount < 3;
  }

  public int processPlaceAction(Place paramPlace)
  {
    PlaceAction localPlaceAction = new PlaceAction();
    localPlaceAction.setOwner(this);
    localPlaceAction.setActionCode(getActionCode());
    localPlaceAction.setPlace(paramPlace);
    return localPlaceAction.execute();
  }

  public void notifyAboutRaise(Place paramPlace)
  {
    setRoundEndPlaceNumber(paramPlace.getNumber());
    setCurrentStake(paramPlace.getStakingAmount());
    raisesCount += 1;
  }

  public void notifyAboutBet(Place paramPlace)
  {
    setCurrentStake(getStakeBase());
    setRoundEndPlaceNumber(paramPlace.getNumber());
    betProcessed();
  }

  public boolean acceptStake(Stake paramStake)
  {
    Object localObject = null;
    switch (paramStake.getType())
    {
    case 3:
      localObject = new CheckStakeAction(paramStake, this);
      try {
        getGame().getPlacesList().getPlace(getCurrentPlaceNumber()).unsetDiscon();
      }
      catch (Exception localException1)
      {
      }

    case 1:
      localObject = new CallStakeAction(paramStake, this);
      try {
        getGame().getPlacesList().getPlace(getCurrentPlaceNumber()).unsetDiscon();
      }
      catch (Exception localException2)
      {
      }

    case 4:
      localObject = new BetStakeAction(paramStake, this);
      try {
        getGame().getPlacesList().getPlace(getCurrentPlaceNumber()).unsetDiscon();
      }
      catch (Exception localException3)
      {
      }

    case 2:
      localObject = new RaiseStakeAction(paramStake, this);
      try {
        getGame().getPlacesList().getPlace(getCurrentPlaceNumber()).unsetDiscon();
      }
      catch (Exception localException4)
      {
      }

    case 5:
      localObject = new FoldStakeAction(paramStake, this);
      break;
    default:
      return false;
    }
    return ((StakeAction)localObject).execute();
  }

  public void setStakeBase(BigDecimal paramBigDecimal)
  {
    stakeBase = paramBigDecimal;
  }

  public BigDecimal getStakeBase()
  {
    return stakeBase;
  }

  public Game getGame()
  {
    return game;
  }

  public void setNeedBet(BigDecimal paramBigDecimal)
  {
    needBet = paramBigDecimal;
  }

  public void setNeedCall(BigDecimal paramBigDecimal)
  {
    needCall = paramBigDecimal;
  }

  public void setNeedRaise(BigDecimal paramBigDecimal)
  {
    needRaise = paramBigDecimal;
  }

  public BigDecimal getNeedBet()
  {
    return needBet;
  }

  public BigDecimal getNeedCall()
  {
    return needCall;
  }

  public BigDecimal getNeedRaise()
  {
    return needRaise;
  }

  public int getStartAfterPlaceNumber()
  {
    return startAfterPlaceNumber;
  }

  public void setCurrentStake(BigDecimal paramBigDecimal)
  {
    currentStake = paramBigDecimal;
  }

  public BigDecimal getCurrentStake()
  {
    return currentStake;
  }

  public void setRoundEndPlaceNumber(int paramInt)
  {
    roundEndPlaceNumber = paramInt;
  }

  public int getRoundEndPlaceNumber()
  {
    return roundEndPlaceNumber;
  }

  public void setCanCheck(boolean paramBoolean)
  {
    canCheck = paramBoolean;
  }

  public boolean isCanCheck()
  {
    return canCheck;
  }

  public void setActionCode(int paramInt)
  {
    actionCode = paramInt;
  }

  public int getActionCode()
  {
    return actionCode;
  }

  public void setLastStake(Stake paramStake)
  {
    lastStake = paramStake;
  }

  public Stake getLastStake()
  {
    return lastStake;
  }

  public boolean isAllIn()
  {
    return allIn;
  }

  public void setNeedAnte(BigDecimal paramBigDecimal)
  {
    needAnte = paramBigDecimal;
  }

  public BigDecimal getNeedAnte()
  {
    return needAnte;
  }

  public void incRound()
  {
    round += 1;
  }

  public void setRound(int paramInt)
  {
    round = paramInt;
  }

  public int getRound()
  {
    return round;
  }

  public boolean hasAllInPlaces()
  {
    ArrayList localArrayList = allInPlaces;
    int i = 0;

    synchronized (localArrayList)
    {
      if (allInPlaces.size() > 0)
        i = 1;
      else
        i = 0;
    }
    return i;
  }

  public BigDecimal getMaxStake()
  {
    return maxStake;
  }

  public void setMaxStake(BigDecimal paramBigDecimal)
  {
    maxStake = paramBigDecimal;
  }

  public BigDecimal getNeedBringIn()
  {
    return needBringIn;
  }

  public void setNeedBringIn(BigDecimal paramBigDecimal)
  {
    needBringIn = paramBigDecimal;
  }

  public BigDecimal getDefaultNeedBet()
  {
    return defaultNeedBet;
  }

  public void setDefaultNeedBet(BigDecimal paramBigDecimal)
  {
    defaultNeedBet = paramBigDecimal;
  }

  public BigDecimal getDefaultNeedRaise()
  {
    return defaultNeedRaise;
  }

  public void setDefaultNeedRaise(BigDecimal paramBigDecimal)
  {
    defaultNeedRaise = paramBigDecimal;
  }

  public boolean isUsePredefinedNeedBet()
  {
    return usePredefinedNeedBet;
  }

  public void setUsePredefinedNeedBet(boolean paramBoolean)
  {
    usePredefinedNeedBet = paramBoolean;
  }

  public boolean isUsePredefinedNeedRaise()
  {
    return usePredefinedNeedRaise;
  }

  public void setUsePredefinedNeedRaise(boolean paramBoolean)
  {
    usePredefinedNeedRaise = paramBoolean;
  }

  public BigDecimal getAndResetPredefinedNeedBet()
  {
    BigDecimal localBigDecimal = defaultNeedBet;
    if (usePredefinedNeedBet)
      defaultNeedBet = new BigDecimal(0);
    usePredefinedNeedBet = false;
    return localBigDecimal;
  }

  public BigDecimal getAndResetPredefinedNeedRaise()
  {
    BigDecimal localBigDecimal = defaultNeedRaise;
    if (usePredefinedNeedRaise)
      defaultNeedRaise = new BigDecimal(0);
    usePredefinedNeedRaise = false;
    return localBigDecimal;
  }

  public boolean getAndResetPredefinedCanCheck()
  {
    boolean bool = defaultCanCheck;
    if (usePredefinedCanCheck)
      defaultCanCheck = false;
    usePredefinedCanCheck = false;
    return bool;
  }

  public boolean isDefaultCanCheck()
  {
    return defaultCanCheck;
  }

  public void setDefaultCanCheck(boolean paramBoolean)
  {
    defaultCanCheck = paramBoolean;
  }

  public boolean isUsePredefinedCanCheck()
  {
    return usePredefinedCanCheck;
  }

  public void setUsePredefinedCanCheck(boolean paramBoolean)
  {
    usePredefinedCanCheck = paramBoolean;
  }
}