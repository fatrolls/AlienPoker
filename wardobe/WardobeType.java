package wardobe;

public class WardobeType
{
  public static final WardobeType cap = new WardobeType();
  public static final WardobeType head = new WardobeType();
  public static final WardobeType shirt = new WardobeType();
  public static final WardobeType leg = new WardobeType();
  public static final WardobeType accessories = new WardobeType();

  public static WardobeType getWardobeType(int type) {
    switch (type) {
    case 0:
      return cap;
    case 1:
      return head;
    case 2:
      return shirt;
    case 3:
      return leg;
    case 4:
      return accessories;
    }

    throw new RuntimeException("Wartobe Type is invalid: " + type);
  }
}