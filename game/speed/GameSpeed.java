package game.speed;

public class GameSpeed
{
  public static final int SPEED_STANDART = 0;
  public static final int SPEED_FAST = 1;
  private int type = 0;

  public GameSpeed(int type) {
    this.type = type;
  }

  public int getType() {
    return type;
  }

  public static boolean isCorrectGameSpeed(int speed) {
    return (speed == 0) || (speed == 1);
  }
}