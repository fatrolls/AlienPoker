package wardobe;

public class WardobeState
{
  private static final WardobeState availableForBuying = new WardobeState(1);
  private static final WardobeState notAvailableForBuying = new WardobeState(2);

  private static final WardobeState weared = new WardobeState(3);
  private static final WardobeState notWeared = new WardobeState(4);
  private int state;

  public WardobeState(int state)
  {
    this.state = state;
  }

  public int getState() {
    return state;
  }

  public static WardobeState getState(int state) {
    switch (state) {
    case 1:
      return availableForBuying;
    case 2:
      return notAvailableForBuying;
    case 3:
      return weared;
    case 4:
      return notWeared;
    }

    return notAvailableForBuying;
  }

  public static WardobeState getAvailableForBuying()
  {
    return availableForBuying;
  }

  public static WardobeState getNotAvailableForBuying() {
    return notAvailableForBuying;
  }

  public static WardobeState getWeared() {
    return weared;
  }

  public static WardobeState getNotWeared() {
    return notWeared;
  }
}