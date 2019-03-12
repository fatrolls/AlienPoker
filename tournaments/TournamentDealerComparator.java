package tournaments;

import game.Card;
import game.cards.comparators.DefaultComparator;

public class TournamentDealerComparator extends DefaultComparator
{
  public int compare(Object o1, Object o2)
  {
    Card c1 = (Card)o1;
    Card c2 = (Card)o2;

    if (c1.getValue() > c2.getValue()) {
      return -10;
    }

    if (c1.getValue() < c2.getValue()) {
      return 10;
    }

    int i1 = 0; int i2 = 0;

    switch (c1.getSuite()) { case 2:
      i1 = 1; break;
    case 3:
      i1 = 2; break;
    case 4:
      i1 = 3; break;
    case 1:
      i1 = 4;
    }

    switch (c2.getSuite()) { case 2:
      i2 = 1; break;
    case 3:
      i2 = 2; break;
    case 4:
      i2 = 3; break;
    case 1:
      i2 = 4;
    }

    if (i1 < i2) {
      return -9;
    }
    return 9;
  }
}