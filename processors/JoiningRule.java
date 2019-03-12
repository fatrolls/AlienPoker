package processors;

import game.Desk;
import game.Player;
import java.math.BigDecimal;
import server.Response;

public abstract interface JoiningRule
{
  public abstract boolean join(Response paramResponse, Desk paramDesk, Player paramPlayer, int paramInt, BigDecimal paramBigDecimal);
}