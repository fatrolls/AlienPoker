package game.playerclub;

import game.Player;

public class PlayersClub
{
  private static final PlayersClub playersClub = new PlayersClub();
  private final ClubPlayers clubPlayers = new ClubPlayers();

  public static PlayersClub getInstance()
  {
    return playersClub;
  }

  public int join(Player player) {
    return clubPlayers.registerPlayer(player);
  }

  public ClubPlayers getClubPlayers() {
    return clubPlayers;
  }

  public void loadMember(ClubMember clubMember)
  {
    clubPlayers.loadMember(clubMember);
  }
}