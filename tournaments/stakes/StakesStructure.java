package tournaments.stakes;

import java.math.BigDecimal;

public class StakesStructure
{
  public static BigDecimal getAnte(int game, int level)
  {
    switch (game)
    {
    }

    switch (level) {
    case 1:
      return new BigDecimal(1);
    case 2:
      return new BigDecimal(1);
    case 3:
      return new BigDecimal(1);
    case 4:
      return new BigDecimal(1);
    case 5:
      return new BigDecimal(1);
    case 6:
      return new BigDecimal(1);
    case 7:
      return new BigDecimal(25);
    case 8:
      return new BigDecimal(25);
    case 9:
      return new BigDecimal(50);
    case 10:
      return new BigDecimal(50);
    case 11:
      return new BigDecimal(75);
    case 12:
      return new BigDecimal(100);
    case 13:
      return new BigDecimal(150);
    case 14:
      return new BigDecimal(200);
    case 15:
      return new BigDecimal(300);
    case 16:
      return new BigDecimal(400);
    case 17:
      return new BigDecimal(600);
    case 18:
      return new BigDecimal(1000);
    case 19:
      return new BigDecimal(1500);
    case 20:
      return new BigDecimal(2000);
    }

    return new BigDecimal(0);
  }

  public static float getSmallBlind(int game, int level)
  {
    switch (game)
    {
    case 3:
      switch (level) {
      case 1:
        return 2.0F;
      case 2:
        return 5.0F;
      case 3:
        return 10.0F;
      case 4:
        return 15.0F;
      case 5:
        return 15.0F;
      case 6:
        return 25.0F;
      case 7:
        return 25.0F;
      case 8:
        return 50.0F;
      case 9:
        return 50.0F;
      case 10:
        return 100.0F;
      case 11:
        return 200.0F;
      case 12:
        return 200.0F;
      case 13:
        return 300.0F;
      case 14:
        return 500.0F;
      case 15:
        return 500.0F;
      case 16:
        return 1000.0F;
      case 17:
        return 2000.0F;
      case 18:
        return 2000.0F;
      }

      return 0.0F;
    }

    switch (level) {
    case 1:
      return 10.0F;
    case 2:
      return 15.0F;
    case 3:
      return 25.0F;
    case 4:
      return 50.0F;
    case 5:
      return 75.0F;
    case 6:
      return 100.0F;
    case 7:
      return 100.0F;
    case 8:
      return 200.0F;
    case 9:
      return 300.0F;
    case 10:
      return 400.0F;
    case 11:
      return 600.0F;
    case 12:
      return 1000.0F;
    case 13:
      return 1500.0F;
    case 14:
      return 2000.0F;
    case 15:
      return 3000.0F;
    case 16:
      return 4000.0F;
    case 17:
      return 6000.0F;
    case 18:
      return 10000.0F;
    case 19:
      return 15000.0F;
    case 20:
      return 20000.0F;
    }

    return 0.0F;
  }

  public static float getBigBlind(int game, int level)
  {
    switch (game)
    {
    case 3:
      switch (level) {
      case 1:
        return 5.0F;
      case 2:
        return 10.0F;
      case 3:
        return 20.0F;
      case 4:
        return 40.0F;
      case 5:
        return 50.0F;
      case 6:
        return 75.0F;
      case 7:
        return 100.0F;
      case 8:
        return 150.0F;
      case 9:
        return 200.0F;
      case 10:
        return 300.0F;
      case 11:
        return 500.0F;
      case 12:
        return 750.0F;
      case 13:
        return 1000.0F;
      case 14:
        return 1500.0F;
      case 15:
        return 2000.0F;
      case 16:
        return 3000.0F;
      case 17:
        return 5000.0F;
      case 18:
        return 7500.0F;
      }

      return 0.0F;
    }

    switch (level) {
    case 1:
      return 20.0F;
    case 2:
      return 30.0F;
    case 3:
      return 50.0F;
    case 4:
      return 100.0F;
    case 5:
      return 150.0F;
    case 6:
      return 200.0F;
    case 7:
      return 200.0F;
    case 8:
      return 400.0F;
    case 9:
      return 600.0F;
    case 10:
      return 800.0F;
    case 11:
      return 1200.0F;
    case 12:
      return 2000.0F;
    case 13:
      return 3000.0F;
    case 14:
      return 4000.0F;
    case 15:
      return 6000.0F;
    case 16:
      return 8000.0F;
    case 17:
      return 12000.0F;
    case 18:
      return 20000.0F;
    case 19:
      return 30000.0F;
    case 20:
      return 40000.0F;
    }

    return 0.0F;
  }
}