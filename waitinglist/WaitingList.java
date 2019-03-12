package waitinglist;

import game.Desk;
import game.PlacesList;
import game.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class WaitingList
{
  private final List requirements;

  public WaitingList()
  {
    requirements = new LinkedList();
  }

  public void add(WaitingRequirements req, Waiter waiter)
  {
    synchronized (requirements) {
      Iterator iter = requirements.iterator();
      while (iter.hasNext()) {
        WaitingRequirements currentReq = (WaitingRequirements)iter.next();
        if (currentReq.equals(req)) {
          if (currentReq.getType() == 1) {
            waiter.setMinPlayers(currentReq.getMinPlayers());
          }
          currentReq.addWaiter(waiter);
          return;
        }
        if (currentReq.equalsIgnoreMinPlayers(req))
        {
          if (currentReq.getType() == 1) {
            currentReq.removeWaiter(waiter);
          }
        }

      }

      if (req.getType() == 1) {
        waiter.setMinPlayers(req.getMinPlayers());
      }
      req.addWaiter(waiter);
      requirements.add(req);
    }
  }

  public void remove(WaitingRequirements req, Waiter waiter)
  {
    synchronized (requirements) {
      Iterator iter = requirements.iterator();
      while (iter.hasNext()) {
        WaitingRequirements currentReq = (WaitingRequirements)iter.next();
        if (currentReq.equals(req))
          synchronized (currentReq) {
            currentReq.removeWaiter(waiter);
          }
      }
    }
  }

  public void removeIgnoreMinPlayers(WaitingRequirements req, Waiter waiter)
  {
    synchronized (requirements) {
      Iterator iter = requirements.iterator();
      while (iter.hasNext()) {
        WaitingRequirements currentReq = (WaitingRequirements)iter.next();
        if (currentReq.equalsIgnoreMinPlayers(req))
          currentReq.removeWaiter(waiter);
      }
    }
  }

  public void remove(Waiter waiter)
  {
    synchronized (requirements) {
      Iterator iter = requirements.iterator();
      while (iter.hasNext())
        ((WaitingRequirements)iter.next()).removeWaiter(waiter);
    }
  }

  public List getWaitersForDesk(Desk desk)
  {
    List waiters = new ArrayList();
    HashMap map = new HashMap();

    synchronized (requirements) {
      Iterator iter = requirements.iterator();
      while (iter.hasNext()) {
        WaitingRequirements w = (WaitingRequirements)iter.next();
        if (w.conatain(desk)) {
          waiters.addAll(w.getWaiters());
        } else if (w.containIgnoreMinPlayers(desk)) {
          Integer value = new Integer(w.getMinPlayers());
          if (!map.containsKey(value)) {
            List wrs = new ArrayList();
            wrs.add(w);
            map.put(value, wrs);
          } else {
            List wrs = (List)map.get(value);
            wrs.add(w);
            map.put(value, wrs);
          }

        }

      }

    }

    Iterator itr = map.keySet().iterator();
    ArrayList al = new ArrayList(map.size());
    while (itr.hasNext()) {
      Integer integer = (Integer)itr.next();
      al.add(integer);
    }
    Collections.sort(al, new IntegerComparator(null));

    Iterator i1 = al.iterator();

    ArrayList previousWaiters = new ArrayList();

    int cnt = 0;

    while (i1.hasNext()) {
      Integer i = (Integer)i1.next();

      List w1 = (List)map.get(i);
      Iterator w1Iter = w1.iterator();

      while (w1Iter.hasNext()) {
        cnt += ((WaitingRequirements)w1Iter.next()).getAvailableWaitersForDesk(desk).size();
      }

      if (waiters.size() + cnt + desk.getPlaces() >= i.intValue()) {
        Iterator i2 = ((List)map.get(i)).iterator();
        while (i2.hasNext()) {
          WaitingRequirements wr = (WaitingRequirements)i2.next();
          synchronized (wr) {
            waiters.addAll(wr.getWaiters());
          }
        }
        waiters.addAll(previousWaiters);
        previousWaiters.clear();
      } else {
        Iterator i2 = ((List)map.get(i)).iterator();
        while (i2.hasNext()) {
          WaitingRequirements wr = (WaitingRequirements)i2.next();
          synchronized (wr) {
            previousWaiters.addAll(wr.getWaiters());
          }
        }

      }

    }

    return waiters;
  }

  public synchronized void update(List desks)
  {
    removeWaitersByTimeOut();

    Iterator iter = desks.iterator();

    while (iter.hasNext()) {
      Desk desk = (Desk)iter.next();
      int free = desk.getPlacesList().getFreePlacesCount();
      if (free == 0)
      {
        continue;
      }

      List waiters = getWaitersForDesk(desk);

      desk.setWaitingPlayersCount(waiters.size());

      Collections.sort(waiters, new WaitersComparator(null));

      Iterator it = waiters.iterator();
      int cnt = 0;
      while ((it.hasNext()) && (cnt < free)) {
        Waiter waiter = (Waiter)it.next();
        if (waiter.isJoining()) {
          if (waiter.getJoinDesk().getID() == desk.getID())
            cnt++;
        }
        else {
          waiter.join(desk);
          cnt++;
        }
      }

    }

    clearEmptyRequirements();
  }

  public void removeWaitersByTimeOut()
  {
    synchronized (requirements) {
      Iterator iter = requirements.iterator();
      while (iter.hasNext())
        ((WaitingRequirements)iter.next()).removeWaitersByTimeOut();
    }
  }

  public void clearEmptyRequirements()
  {
    synchronized (requirements) {
      Iterator iter = requirements.iterator();
      while (iter.hasNext())
        if (((WaitingRequirements)iter.next()).getWaitersQty() == 0)
          iter.remove();
    }
  }

  public int getDeskWaitersCount(List desks)
  {
    Iterator iter = desks.iterator();
    if (iter.hasNext()) {
      Desk desk = (Desk)iter.next();
      List waiters = getWaitersForDesk(desk);
      return waiters.size();
    }
    return 0;
  }

  public List getJoiningWaiters(Player player)
  {
    ArrayList waiters = new ArrayList();
    synchronized (requirements) {
      Iterator iter = requirements.iterator();
      while (iter.hasNext()) {
        WaitingRequirements w = (WaitingRequirements)iter.next();
        Waiter w1 = w.getWaiter(player);
        if ((w1 != null) && 
          (w1.isJoining())) {
          waiters.add(w1);
        }
      }
    }

    return waiters;
  }

  private class IntegerComparator
    implements Comparator
  {
    private IntegerComparator()
    {
    }

    public int compare(Object o1, Object o2)
    {
      Integer w1 = (Integer)o1;
      Integer w2 = (Integer)o2;

      return w1.compareTo(w2);
    }
  }

  private class WaitersComparator
    implements Comparator
  {
    private WaitersComparator()
    {
    }

    public int compare(Object o1, Object o2)
    {
      Waiter w1 = (Waiter)o1;
      Waiter w2 = (Waiter)o2;

      if (w1.isJoining()) {
        return -10;
      }

      if (w2.isJoining()) {
        return -10;
      }

      return w2.getEnterDate().compareTo(w1.getEnterDate());
    }
  }
}