package game.speed;

public class GameSpeedFactory
{
  public static GameSpeed getGameSpeed(int speed)
  {
    switch (speed) { case 0:
      return new StandartGame(0);
    case 1:
      return new SpeedGame(1); }
    throw new RuntimeException("GameSpeedFactory - unknown speed type " + speed);
  }
}