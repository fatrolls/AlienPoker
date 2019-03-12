package game;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

public class MoneyRequestsList
{
  private ArrayList requests;
  private Desk desk;

  public MoneyRequestsList()
  {
    requests = new ArrayList();
    desk = null;
  }

  public boolean addMoneyRequest(Player player, BigDecimal amount) {
    Place place = desk.getPlacesList().getPlace(player);
    if ((place != null) && 
      (player.getAmount(desk.getMoneyType()).floatValue() >= amount.floatValue())) {
      MoneyRequest request = new MoneyRequest(null);
      request.setAmount(amount);
      request.setPlayer(player);

      synchronized (requests) {
        requests.add(request);
      }

    }

    return false;
  }

  public void setDesk(Desk desk)
  {
    this.desk = desk;
  }

  public void processRequests()
  {
    synchronized (requests) {
      Iterator it = requests.iterator();
      while (it.hasNext()) {
        MoneyRequest request = (MoneyRequest)it.next();

        Place place = desk.getPlacesList().getPlace(request.getPlayer());
        if (place != null) {
          request.process(place, desk.getMoneyType());
        }
      }
      requests.clear();
    }
  }

  private class MoneyRequest
  {
    private Player player;
    private BigDecimal amount = new BigDecimal(0);

    private MoneyRequest() {
    }
    public void setAmount(BigDecimal amount) { this.amount = amount;
    }

    public void setPlayer(Player player)
    {
      this.player = player;
    }

    public Player getPlayer()
    {
      return player;
    }

    public BigDecimal getAmount()
    {
      return amount;
    }

    public void process(Place place, int moneyType)
    {
      if (amount.floatValue() <= player.getAmount(moneyType).floatValue()) {
        player.decreaseAmount(amount, moneyType);
        place.incDeskAmount(amount);
      }
    }
  }
}