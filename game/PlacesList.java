package game;

import game.messages.CommonStateMessagesList;
import game.messages.PrivateStateMessagesList;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

public class PlacesList
{
  private ArrayList places = new ArrayList();

  private int dealerPlaceNumber = 0;
  private int totalPlaces = 0;
  private int availablePlaces = 0;

  private BigDecimal maxStake = new BigDecimal(0);
  private Desk desk = null;

  public PlacesList(int totalPlaces)
  {
    this.totalPlaces = totalPlaces;
    availablePlaces = totalPlaces;

    for (int i = 1; i <= this.totalPlaces; i++)
      places.add(new Place(i));
  }

  public int getFreePlacesCount()
  {
    Iterator it = places.iterator();
    int count = 0;
    while (it.hasNext()) {
      Place place = (Place)it.next();
      synchronized (place) {
        if (place.isFree()) {
          count++;
        }
      }
    }

    return count;
  }

  public PlacesList(int totalPlaces, Desk desk)
  {
    this(totalPlaces);
    this.desk = desk;
  }

  public int getPlayersCount()
  {
    Iterator it = allPlacesIterator();
    int count = 0;
    while (it.hasNext()) {
      Place place = (Place)it.next();
      synchronized (place) {
        if (place.isBusy()) {
          count++;
        }
      }
    }

    return count;
  }

  public void sitoutNoMoneyPlayers()
  {
    Iterator it = places.iterator();
    while (it.hasNext()) {
      Place place = (Place)it.next();
      synchronized (place) {
        if ((place.isActive()) && 
          (place.getAmount().compareTo(new BigDecimal(0).setScale(2, 5)) <= 0))
        {
          place.markAsSittingOut();
          getDesk().getPublicStateMessagesList().addCommonMessage(place.getPlayer().getLogin(), 10, place.getNumber(), 2);
          place.getStateMessagesList().addPrivateMessage(10, 2);
        }
      }
    }
  }

  public void prepareToNewHand()
  {
    Iterator it = places.iterator();
    while (it.hasNext()) {
      Place place = (Place)it.next();

      synchronized (place) {
        place.unmarkAsFold();
        place.unmarkAsAllIn();
        place.setStakingAmount(new BigDecimal(0));
        place.getCards().clear();
        place.clearStateMessages();
        place.setCanMakeLock(false);
        place.setCanPostCards(false);
        place.unsetShowCards();
      }
    }
  }

  public int currentDealerPlace()
  {
    return dealerPlaceNumber;
  }

  public void deleteMessage(int id, int place)
  {
    Place p = getPlace(place);
    p.deleteMessage(id);
  }

  public void clearStateMessages()
  {
    Iterator it = places.iterator();
    while (it.hasNext()) {
      Place p = (Place)it.next();
      p.clearStateMessages();
    }
  }

  public int getActivePlayersCount()
  {
    int activePlayers = 0;

    Iterator it = places.iterator();
    while (it.hasNext()) {
      Place place = (Place)it.next();
      synchronized (place) {
        if ((place.isBusy()) && (!place.isFold()) && (!place.isSittingOut()) && (!place.isAllIn())) {
          activePlayers++;
        }
      }
    }

    return activePlayers;
  }

  public int nextDealerPlace()
  {
    int oldDealer = dealerPlaceNumber;
    Place place = getDillerPlace(dealerPlaceNumber);

    if ((place != null) && (place.isActive())) {
      place.setAsDealer();
      dealerPlaceNumber = place.getNumber();

      if (oldDealer > 0) {
        place = getPlace(oldDealer);
        if (place != null) {
          place.unsetIsDealer();
        }
      }

      return place.getNumber();
    }

    return 0;
  }

  public Place getNextPlace(int fromPlace, boolean acceptSituouts)
  {
    int counter = fromPlace;
    int iterations = 0;

    while (iterations < totalPlaces)
    {
      if (counter >= totalPlaces) {
        counter = 0;
      }

      Place place = (Place)places.get(counter);
      synchronized (place) {
        if (acceptSituouts) {
          if ((place.isBusy()) && (!place.isFold())) {
            return place;
          }

        }
        else if ((place.isBusy()) && (!place.isSittingOut()) && (!place.isFold()) && (!place.isAllIn())) {
          return place;
        }

      }

      counter++;
      iterations++;
    }

    return null;
  }

  public Place getNextDeskPlace(int fromPlace)
  {
    int counter = fromPlace;
    if (counter >= totalPlaces) {
      counter = 0;
    }

    Place place = (Place)places.get(counter);
    synchronized (place) {
      return place;
    }
  }

  public Place getNextPlace(int fromPlace)
  {
    return getNextPlace(fromPlace, false);
  }

  public Place getDillerPlace(int fromPlace)
  {
    int counter = fromPlace;
    int iterations = 0;

    if (counter >= totalPlaces) {
      counter = 0;
    }

    while (iterations < totalPlaces)
    {
      Place place = (Place)places.get(counter);

      synchronized (place) {
        if ((place.isBusy()) && (!place.isSittingOut())) {
          return place;
        }
      }

      counter++;
      if (counter >= totalPlaces) {
        counter = 0;
      }
      iterations++;
    }

    return null;
  }

  public int size()
  {
    return totalPlaces;
  }

  public BigDecimal getPlayerDeskAmount(Player player)
  {
    Place place = getPlace(player);
    if (place != null) {
      return place.getAmount();
    }

    return new BigDecimal(0);
  }

  public int getPlayerPlaceNumber(Player player)
  {
    Place place = getPlace(player);
    if (place != null) {
      return place.getNumber();
    }

    return 0;
  }

  public boolean isPlaceAvailable(int number)
  {
    Place place = getPlace(number);

    if (place != null) {
      synchronized (place) {
        if (place.isFree()) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean seatPlayer(int number, Player player, BigDecimal amount)
  {
    return seatPlayer(number, player, amount, false);
  }

  public synchronized boolean seatPlayer(int number, Player player, BigDecimal amount, boolean isSitout)
  {
    Place place = getPlace(number);
    if ((place != null) && 
      (place.isFree())) {
      place.seatPlayer(player, amount, isSitout);
      availablePlaces -= 1;
      return true;
    }

    return false;
  }

  public Place getPlace(Player player)
  {
    Iterator it = places.iterator();
    while (it.hasNext()) {
      Place place = (Place)it.next();
      Player placePlayer = place.getPlayer();

      if ((placePlayer != null) && 
        (placePlayer.equals(player))) {
        return place;
      }

    }

    return null;
  }

  public Place getPlace(int number)
  {
    Iterator it = places.iterator();
    while (it.hasNext()) {
      Place place = (Place)it.next();
      if (place.getNumber() == number) {
        return place;
      }
    }

    return null;
  }

  public boolean isAvailablePlaces()
  {
    return availablePlaces > 0;
  }

  public Iterator iterator()
  {
    return new PlacesIterator();
  }

  public Iterator allPlacesIterator()
  {
    return new PlacesIterator(true);
  }

  public BigDecimal getMaxStake()
  {
    return maxStake;
  }

  public void setMaxStake(BigDecimal maxStake)
  {
    this.maxStake = maxStake;
  }

  public Desk getDesk() {
    return desk;
  }

  public Place getPrevPlace(Place place)
  {
    int index = places.indexOf(place);
    if (index != 0) {
      for (int i = index; i >= 0; i--) {
        Place p = (Place)places.get(i);
        synchronized (p) {
          if (p.isActive()) {
            return p;
          }
        }
      }

      for (int i = 0; i <= index; i++) {
        Place p = (Place)places.get(i);
        synchronized (p) {
          if (p.isActive()) {
            return p;
          }
        }
      }
    }

    return null;
  }

  private class ParametredPlacesIterator
    implements Iterator
  {
    protected int start = 0;
    protected int end = 0;
    protected boolean round = false;
    protected boolean isAllPlaces = false;

    public ParametredPlacesIterator(int start, int end)
    {
      this.start = start;
      this.end = end;
    }

    public ParametredPlacesIterator(int start, int end, boolean round)
    {
      this(start, end);
      this.round = round;
    }

    public ParametredPlacesIterator(int start, int end, boolean round, boolean isAllPlaces)
    {
      this(start, end);
      this.round = round;
      this.isAllPlaces = isAllPlaces;
    }

    public void remove()
    {
    }

    public boolean hasNext()
    {
      if (!round)
        for (int from = start; from < this$0.totalPlaces; from++) {
          Place place = this$0.getPlace(from);
          if ((place.isActive()) || (isAllPlaces))
            return true;
        }
      int from;
      int from;
      if (round) {
        from = start;
      }
      else {
        from = 1;
      }

      for (int i = from; i <= end; i++) {
        Place p = this$0.getPlace(i);
        if ((p.isActive()) || (isAllPlaces)) {
          return true;
        }
      }

      return false;
    }

    public Object next()
    {
      if (!round) {
        for (int from = start; from <= this$0.totalPlaces; from++) {
          Place place = this$0.getPlace(from);
          if ((place.isActive()) || (isAllPlaces)) {
            start = (from + 1);
            return place;
          }

          if (from == this$0.totalPlaces) {
            round = true;
          }
        }

        if (round) {
          start = 1;
        }
      }

      if (round) {
        for (int from = start; from <= end; from++) {
          Place place = this$0.getPlace(from);
          if ((place.isActive()) || (isAllPlaces)) {
            start = (from + 1);
            return place;
          }
        }
      }

      return null;
    }
  }

  private class PlacesIterator extends PlacesList.ParametredPlacesIterator
  {
    public PlacesIterator()
    {
      super(1, totalPlaces, true);
    }

    public PlacesIterator(boolean isAllPlaces)
    {
      super(1, totalPlaces, true, isAllPlaces);
    }
  }
}