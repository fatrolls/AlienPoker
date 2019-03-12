package processors;

import game.Player;
import game.notes.NotesStorage;
import game.notes.PlayerNote;
import java.io.IOException;
import java.util.HashMap;
import server.ParamParser;
import server.Response;
import server.Server;
import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class GetNoteCommandProcessor
  implements RequestCommandProcessor
{
  private static final String PARAM_PLAYER = "p";
  private static final String TAG_NAME_NOTE = "NOTE";
  private static final String OUT_PARAM_MESSAGE = "MESSAGE";
  private static final String OUT_PARAM_RATING = "RATING";
  private static final String OUT_PARAM_CHAT = "CHAT";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("GETNOTE");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    int playerTo = ParamParser.getInt(params, "p");
    Player player = Player.getPlayerByID(server.getPlayers(), playerTo);
    if (player == null) {
      response.setResultStatus(false, "Bad parameters");
      return response;
    }

    PlayerNote note = NotesStorage.getNoteAndRatingForPlayer(currentPlayer, player);
    boolean chat;
    String message;
    int rating;
    boolean chat;
    if (note == null) {
      String message = "";
      int rating = 0;
      chat = true;
    } else {
      message = note.getNote();
      rating = note.getRating();
      chat = note.isChat();
    }

    XMLDoc doc = new XMLDoc();
    XMLTag tag = doc.startTag("NOTE");
    tag.addParam("ID", playerTo);
    tag.addParam("MESSAGE", message);
    tag.addParam("RATING", rating);
    tag.addParam("CHAT", chat ? 1 : 0);

    response.setResultStatus(true);
    response.setParametersXML(doc.toString());
    doc.invalidate();

    return response;
  }
}