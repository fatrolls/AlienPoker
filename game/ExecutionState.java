package game;

public class ExecutionState
{
  private boolean state = false;
  private int signalType = 0;
  public static final int S_TYPE_TIMER = 1;
  public static final int S_TYPE_ACTION = 2;
  public static final int S_TYPE_LEAVE_DESK = 3;

  public ExecutionState(boolean state)
  {
    this.state = state;
  }

  public boolean canExec()
  {
    return state;
  }

  public void permit()
  {
    state = true;
  }

  public void forbid()
  {
    state = false;
  }

  public void setSignalType(int signalType)
  {
    this.signalType = signalType;
  }

  public boolean isActionSignal()
  {
    return signalType == 2;
  }

  public boolean isLeaveDeskSignal()
  {
    return signalType == 3;
  }

  public boolean isTimerSignal()
  {
    return signalType == 1;
  }

  public int getSignalType()
  {
    return signalType;
  }
}