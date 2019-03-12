package adminprocessors;

import game.Desk;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import server.Server;
import utils.CommonLogger;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class AdminDesksCommandProcessor
  implements AdminXMLResponse
{
  private static final String OUT_PARAM_DESKS = "DESKS";
  private static final String OUT_PARAM_COUNT = "COUNT";

  public Hashtable getDesks()
  {
    Hashtable response = new Hashtable();

    List desks = Server.getDesksList();

    StringBuffer desksbuff = new StringBuffer();
    int count = 0;

    synchronized (desks)
    {
      Iterator it = desks.iterator();
      while (it.hasNext()) {
        Desk d = (Desk)it.next();
        try {
          desksbuff.append(d.toXML()).append('\n');
        } catch (UnsupportedEncodingException e) {
          CommonLogger.getLogger().warn(e);
        }
        count++;
      }

    }

    XMLDoc xmlDoc = new XMLDoc();
    XMLTag tag = xmlDoc.startTag("DESKS");
    tag.addParam("COUNT", count);
    tag.setTagContent(desksbuff.toString());

    String xml = xmlDoc.toString();
    xmlDoc.invalidate();

    response.put("STATUS", "OK");
    response.put("RESPONSE", xml);

    return response;
  }
}