package game.pokerrounds.stakescalculators;

import game.Place;
import game.pokerrounds.StakesPokerRound;
import java.math.BigDecimal;

public class LimitPokerStakesCalculator
  implements PokerStakesCalculator
{
  private Place place = null;
  private StakesPokerRound stakesPokerRound = null;

  public void setPlace(Place place)
  {
    this.place = place;
  }

  public void setStakesPokerRound(StakesPokerRound stakesPokerRound)
  {
    this.stakesPokerRound = stakesPokerRound;
  }

  public void calculate()
  {
    BigDecimal difference = stakesPokerRound.getCurrentStake().subtract(place.getStakingAmount()).setScale(2, 5);

    synchronized (stakesPokerRound) {
      stakesPokerRound.setCanCheck(stakesPokerRound.getAndResetPredefinedCanCheck());
      stakesPokerRound.setNeedBet(stakesPokerRound.getAndResetPredefinedNeedBet());
      stakesPokerRound.setMaxStake(new BigDecimal(0));
      stakesPokerRound.setNeedCall(new BigDecimal(0));
      stakesPokerRound.setNeedRaise(stakesPokerRound.getAndResetPredefinedNeedRaise());
      stakesPokerRound.setAllIn(false);
    }

    if (difference.floatValue() == 0.0F) {
      if (stakesPokerRound.canBet())
      {
        if ((stakesPokerRound.getNeedBet().floatValue() <= 0.0F) || (stakesPokerRound.getNeedRaise().floatValue() <= 0.0F))
        {
          stakesPokerRound.setCanCheck(true);
        }

        stakesPokerRound.setNeedBet(stakesPokerRound.getStakeBase());

        if (stakesPokerRound.getNeedBet().floatValue() >= place.getAmount().floatValue()) {
          stakesPokerRound.setAllIn(true);
          stakesPokerRound.setNeedBet(place.getAmount());
        }
      }
      else
      {
        stakesPokerRound.setNeedRaise(stakesPokerRound.getStakeBase());

        if (stakesPokerRound.getRaisesCount() == 0) {
          stakesPokerRound.setCanCheck(true);
        }
        else if (stakesPokerRound.canRaise()) {
          stakesPokerRound.setCanCheck(false);
        }

        if (stakesPokerRound.getNeedRaise().floatValue() >= place.getAmount().floatValue()) {
          stakesPokerRound.setAllIn(true);
        }
      }

    }
    else if (difference.floatValue() >= place.getAmount().floatValue()) {
      stakesPokerRound.setAllIn(true);
      stakesPokerRound.setNeedCall(place.getAmount());
    }
    else {
      stakesPokerRound.setNeedCall(difference);
      if (stakesPokerRound.canRaise()) {
        stakesPokerRound.setNeedRaise(difference.add(stakesPokerRound.getStakeBase()).setScale(2, 5));

        if (stakesPokerRound.getNeedRaise().compareTo(place.getAmount()) == 0) {
          stakesPokerRound.setAllIn(true);
        }
        else if (stakesPokerRound.getNeedRaise().floatValue() > place.getAmount().floatValue())
          stakesPokerRound.setNeedRaise(new BigDecimal(0));
      }
    }
  }
}