package game.pokerrounds.stakescalculators;

import game.Game;
import game.Place;
import game.pokerrounds.StakesPokerRound;
import java.math.BigDecimal;

public class PotLimitPokerStakesCalculator
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
    BigDecimal difference = stakesPokerRound.getGame().getBetsAmount().subtract(place.getStakingAmount()).setScale(2, 5);

    stakesPokerRound.setCanCheck(stakesPokerRound.getAndResetPredefinedCanCheck());
    stakesPokerRound.setNeedBet(stakesPokerRound.getAndResetPredefinedNeedBet());
    BigDecimal gaba = stakesPokerRound.getGame().getBetsAndGameAmount();
    stakesPokerRound.setMaxStake(place.getAmount().floatValue() > gaba.floatValue() ? gaba : place.getAmount());
    stakesPokerRound.setNeedCall(new BigDecimal(0));
    stakesPokerRound.setNeedRaise(stakesPokerRound.getAndResetPredefinedNeedRaise());
    stakesPokerRound.setAllIn(false);

    if (difference.floatValue() == 0.0F) {
      if (stakesPokerRound.canBet())
      {
        if ((stakesPokerRound.getNeedBet().floatValue() <= 0.0F) || (stakesPokerRound.getNeedRaise().floatValue() <= 0.0F)) {
          stakesPokerRound.setCanCheck(true);
        }

        stakesPokerRound.setNeedBet(stakesPokerRound.getStakeBase());

        if (stakesPokerRound.getNeedBet().floatValue() >= place.getAmount().floatValue()) {
          stakesPokerRound.setAllIn(true);
          stakesPokerRound.setNeedBet(place.getAmount());
        }
      }
      else {
        stakesPokerRound.setNeedRaise(stakesPokerRound.getStakeBase());
        if (stakesPokerRound.getRaisesCount() == 0) {
          stakesPokerRound.setCanCheck(true);
        }
        else {
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
        stakesPokerRound.setNeedRaise(difference.multiply(new BigDecimal(2)).setScale(2, 5));
        if (stakesPokerRound.getNeedRaise().compareTo(place.getAmount()) == 0) {
          stakesPokerRound.setAllIn(true);
        }
        else if (stakesPokerRound.getNeedRaise().floatValue() > place.getAmount().floatValue()) {
          stakesPokerRound.setNeedRaise(new BigDecimal(0));
        }
      }

    }

    if (stakesPokerRound.isAllIn())
      stakesPokerRound.setMaxStake(new BigDecimal(0));
    else
      stakesPokerRound.setMaxStake(place.getAmount().floatValue() > stakesPokerRound.getNeedCall().multiply(new BigDecimal(2)).setScale(2, 5).add(gaba).setScale(2, 5).floatValue() ? stakesPokerRound.getNeedCall().multiply(new BigDecimal(2)).setScale(2, 5).add(gaba).setScale(2, 5) : place.getAmount().setScale(2, 5));
  }
}