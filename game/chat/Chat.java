package game.chat;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import server.XMLFormatable;
import utils.Log;
import utils.xml.XMLTag;

public class Chat
{
  private final LinkedList messages;
  private static final int MAX_MESSAGES = 20;
  private static final String TAG_CHAT = "CHAT";
  private static final String PARAM_MESSAGES = "MESSAGES";

  public Chat()
  {
    messages = new LinkedList();
  }

  public Chat(LinkedList messages)
  {
    if (messages == null) {
      this.messages = new LinkedList();
    }
    else
      this.messages = messages;
  }

  public void add(ChatMessage chatMessage)
  {
    if (chatMessage != null) {
      synchronized (messages) {
        if (messages.size() >= 20) {
          messages.remove(0);
        }
        messages.add(chatMessage);
      }
    }
    else {
      Log.out("Class Chat -> Error: chatMessage=null at addMessage method");
    }

    Log.out("" + messages.size());
  }

  public int size()
  {
    return messages.size();
  }

  public XMLTag toXMLTag()
  {
    XMLTag tag = new XMLTag("CHAT");
    StringBuffer buffer = new StringBuffer();

    synchronized (messages) {
      Iterator it = messages.iterator();
      while (it.hasNext()) {
        XMLFormatable msg = (XMLFormatable)it.next();
        try {
          buffer.append(msg.toXML());
        }
        catch (UnsupportedEncodingException e) {
          throw new RuntimeException(e);
        }
      }
    }

    tag.addParam("MESSAGES", size());
    tag.setTagContent(buffer.toString());

    return tag;
  }

  public Chat getChat(long chatTime)
  {
    LinkedList list;
    synchronized (messages) {
      list = (LinkedList)messages.clone();
    }
    Chat chat = new Chat(list);
    chat.removeOldMessages(chatTime);
    return chat;
  }

  private void removeOldMessages(long chatTime)
  {
    int i = 0;
    synchronized (messages) {
      while (i < messages.size()) {
        ChatMessage chatMessage = (ChatMessage)messages.get(i);
        if (chatMessage.getTime() <= chatTime) {
          messages.remove(i);
        }
        else
          i++;
      }
    }
  }
}