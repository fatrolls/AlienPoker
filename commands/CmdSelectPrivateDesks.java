package commands;

import game.Desk;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import server.XMLFormatable;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class CmdSelectPrivateDesks
  implements XMLFormatable
{
  private List desks;

  public void setDesks(List desks)
  {
    this.desks = desks;
  }

  public String toXML() throws UnsupportedEncodingException
  {
    StringBuffer desksbuff = new StringBuffer();
    int count = 0;
    synchronized (desks) {
      Iterator it = desks.iterator();
      while (it.hasNext()) {
        Desk d = (Desk)it.next();
        desksbuff.append(d.toXML()).append('\n');
        count++;
      }
    }

    XMLDoc xmlDoc = new XMLDoc();
    XMLTag tag = xmlDoc.startTag("DESKS");
    tag.addParam("COUNT", count);
    tag.setTagContent(desksbuff.toString());

    String xml = xmlDoc.toString();
    xmlDoc.invalidate();

    return xml;
  }
}