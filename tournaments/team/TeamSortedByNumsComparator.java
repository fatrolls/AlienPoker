package tournaments.team;

import java.util.Comparator;

public class TeamSortedByNumsComparator
  implements Comparator
{
  public int compare(Object o1, Object o2)
  {
    if ((!(o1 instanceof Team)) || (!(o2 instanceof Team))) {
      throw new IllegalArgumentException("Illegal usage of " + TeamSortedByNumsComparator.class.getName() + " o1: " + o1.getClass().getName() + " o2: " + o2.getClass().getName());
    }
    Team team1 = (Team)o1;
    Team team2 = (Team)o2;

    if (team1.getNum() < team2.getNum()) {
      return -1;
    }
    if (team1.getNum() == team2.getNum()) {
      return 0;
    }
    return 1;
  }
}