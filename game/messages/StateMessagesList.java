package game.messages;

import java.util.LinkedList;
import org.apache.log4j.Logger;
import server.XMLFormatable;

public abstract class StateMessagesList
  implements XMLFormatable
{
  Logger log = Logger.getLogger(StateMessagesList.class);
  private int max_list_size;
  protected final LinkedList messages = new LinkedList();
  private int primaryid = 1;

  public StateMessagesList() {
    max_list_size = 0;
  }

  public StateMessagesList(int max_list_size)
  {
    this.max_list_size = max_list_size;
  }

  public synchronized void addMessage(StateMessage msg)
  {
    msg.setID(primaryid);
    primaryid += 1;

    synchronized (messages) {
      if ((messages.size() > max_list_size) && (max_list_size != 0)) {
        messages.removeFirst();
      }
      log.info("msg=" + msg.toString());
      messages.addLast(msg);
    }
  }

  public void clear()
  {
    synchronized (messages) {
      messages.clear();
    }
  }
}