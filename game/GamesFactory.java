package game;

public class GamesFactory
{
  public static Game createGame(int pokerType)
  {
    switch (pokerType) {
    case 1:
      return new TexasHoldem();
    }

    return null;
  }
}