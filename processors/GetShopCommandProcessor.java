package processors;

import commands.CmdLoadPlayerWardobes;
import commands.CmdLoadWardobes;
import game.Player;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import server.Response;
import server.Server;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;
import wardobe.WardobeItem;
import wardobe.WardobeType;

public class GetShopCommandProcessor
  implements RequestCommandProcessor
{
  private ArrayList cashedItems = null;

  public Response process(HashMap params, Server server) throws IOException {
    Response response = new Response("GETSHOP");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    CmdLoadWardobes cmd = new CmdLoadWardobes();
    Connection db = Server.getDbConnection();
    cmd.setDbConnection(db);
    cmd.execute();
    try {
      db.close();
    } catch (SQLException e) {
      throw new IOException(e.getMessage());
    }

    ArrayList wardobes = cmd.getWardobes();

    XMLDoc doc = new XMLDoc();
    XMLTag tag = doc.startTag("wardobe");

    String[] genders = { "man", "woman" };
    String[] letters = { "c", "h", "s", "l", "a" };

    for (int i = 0; i < 2; i++)
    {
      if (i != currentPlayer.getGender())
      {
        continue;
      }
      ArrayList genderList = selectAllByGender(i, wardobes);
      XMLTag genderTag = new XMLTag(genders[i]);

      for (int j = 0; j < 5; j++)
      {
        ArrayList list = selectAllByType(j, genderList);
        XMLTag letter = new XMLTag(letters[j]);
        Iterator iter = list.iterator();
        StringBuffer buffer = new StringBuffer();
        while (iter.hasNext()) {
          WardobeItem item = (WardobeItem)iter.next();

          WardobeItem itemFromCach = checkWardobeItem(item, currentPlayer);
          if (itemFromCach != null) {
            item = itemFromCach;
          }

          buffer.append(item.toXML());
        }
        letter.setTagContent(buffer.toString());
        genderTag.addNestedTag(letter);
      }

      tag.addNestedTag(genderTag);
    }

    String xml = tag.toString();
    tag.invalidate();

    response.setResultStatus(true);
    response.setParametersXML(xml);

    return response;
  }

  private ArrayList selectAllByGender(int gender, ArrayList list) {
    ArrayList result = new ArrayList();
    Iterator iter = list.iterator();
    while (iter.hasNext()) {
      WardobeItem item = (WardobeItem)iter.next();
      if (item.getGender() == gender) {
        result.add(item);
      }
    }
    return result;
  }

  private ArrayList selectAllByType(int type, ArrayList list) {
    ArrayList result = new ArrayList();
    Iterator iter = list.iterator();
    while (iter.hasNext()) {
      WardobeItem item = (WardobeItem)iter.next();
      if (item.getType() == WardobeType.getWardobeType(type)) {
        result.add(item);
      }
    }
    return result;
  }

  private WardobeItem checkWardobeItem(WardobeItem item, Player currentPlayer) throws IOException
  {
    cacheItems(currentPlayer);
    Iterator iter = cashedItems.iterator();
    while (iter.hasNext()) {
      WardobeItem witem = (WardobeItem)iter.next();
      if (witem.getId() == item.getId()) {
        return witem;
      }
    }
    return null;
  }

  private void cacheItems(Player currentPlayer) throws IOException
  {
    if (cashedItems == null) {
      CmdLoadPlayerWardobes cmd = new CmdLoadPlayerWardobes();
      cmd.setPlayer(currentPlayer);
      Connection db = Server.getDbConnection();
      cmd.setDbConnection(db);
      cmd.execute();
      try {
        db.close();
      } catch (SQLException e) {
        throw new IOException(e.getMessage());
      }
      cashedItems = cmd.getWardobes();
    }
  }
}