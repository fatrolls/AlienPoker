package game.playerclub;

import game.Player;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClubPlayers
{
  private final List membersList = Collections.synchronizedList(new LinkedList());
  private final Map membersMap = Collections.synchronizedMap(new HashMap());
  public static final int ERROR_NO_ERROR = 0;
  public static final int ERROR_ALREADY_REGISTERED = 1;

  public void loadMember(ClubMember clubMember)
  {
    membersMap.put(new Integer(clubMember.getId()), clubMember);
    membersList.add(clubMember);
  }

  public int registerPlayer(Player player)
  {
    Integer id = new Integer(player.getID());
    synchronized (membersMap) {
      if (membersMap.containsKey(id)) {
        return 1;
      }
    }

    ClubMember clubMember = new ClubMember(player, new Date());
    membersMap.put(id, clubMember);
    membersList.add(clubMember);

    new Thread(new AddMemberToDb(clubMember)).start();

    return 0;
  }

  public ClubMember getMemberById(int id) {
    return (ClubMember)membersMap.get(new Integer(id));
  }

  public ClubMember getMemberById(Integer id) {
    return (ClubMember)membersMap.get(id);
  }

  public synchronized void unregisterMember(ClubMember clubMember) {
    membersList.remove(clubMember);
    membersMap.remove(new Integer(clubMember.getId()));
  }

  public boolean isAMember(Player player) {
    return getMemberById(player.getID()) != null;
  }
}