package processors;

import emails.MailSender;
import emails.templates.MailTemplate;
import feedbacks.AddFeedBackToDB;
import feedbacks.FeedBackTopics;
import game.Player;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import org.apache.log4j.Logger;
import server.ParamParser;
import server.Response;
import server.Server;
import settings.PokerSettings;

public class PostFeedBackCommandProcessor
  implements RequestCommandProcessor
{
  static final Logger log = Logger.getLogger(PostFeedBackCommandProcessor.class);
  public static final String PARAM_TOPIC_ID = "t";
  public static final String PARAM_TOPIC_NAME = "n";
  public static final String PARAM_TOPIC_MESSAGE = "m";
  private static final String PLAIN_TEMPLATE = "FeedBackMailPlain.tpl";
  private static final String HTML_TEMPLATE = "FeedBackMailHtml.tpl";

  public Response process(HashMap params, Server server)
    throws IOException
  {
    Response response = new Response("POSTFEEDBACK");

    Player currentPlayer = server.getCurrentPlayer();
    if (currentPlayer == null) {
      response.setResultStatus(false, "Authorization first");
      return response;
    }

    if (params.containsKey("m")) {
      String message = URLDecoder.decode((String)params.get("m"), "ISO-8859-1").trim();
      String topicName = null;
      if (params.containsKey("t")) {
        int topicId = ParamParser.getInt(params, "t");
        if (Server.getFeedBackTopics().hasTopic(topicId)) {
          topicName = Server.getFeedBackTopics().getTopicName(topicId);
          new AddFeedBackToDB(topicId, topicName, message).execute();
        } else {
          response.setResultStatus(false, "Bad parameters");
          return response;
        }
      }
      else if (params.containsKey("n")) {
        topicName = URLDecoder.decode((String)params.get("n"), "ISO-8859-1").trim();
        new AddFeedBackToDB(topicName, message).execute();
      }

      sendMail(server.getCurrentPlayer(), topicName, message);
    } else {
      response.setResultStatus(false, "Bad parameters");
      return response;
    }
    response.setResultStatus(true);
    return response;
  }

  private void sendMail(Player currentPlayer, String topic, String message) {
    MailSender mailSender = new MailSender();
    MailTemplate mt = new MailTemplate();
    String plain = mt.getTemplate("FeedBackMailPlain.tpl");
    String html = mt.getTemplate("FeedBackMailHtml.tpl");
    if ((plain == null) || (html == null)) {
      log.error("FeedBackMailPlain.tpl(FeedBackMailHtml.tpl) does not exist");
    }

    String plainTpl = plain.replaceAll("#fname#", currentPlayer.getFirstName()).replaceAll("#lname#", currentPlayer.getLastName()).replaceAll("#login#", currentPlayer.getLogin()).replaceAll("#email#", currentPlayer.getEmail()).replaceAll("#topic#", topic).replaceAll("#message#", message).replaceAll("#servername#", mailSender.getServerName()).replaceAll("#siteurl#", mailSender.getSiteUrl());

    String htmlTpl = html.replaceAll("#fname#", MailSender.escapeHtml(currentPlayer.getFirstName())).replaceAll("#lname#", MailSender.escapeHtml(currentPlayer.getLastName())).replaceAll("#login#", MailSender.escapeHtml(currentPlayer.getLogin())).replaceAll("#email#", MailSender.escapeHtml(currentPlayer.getEmail())).replaceAll("#topic#", topic).replaceAll("#message#", MailSender.escapeHtml(message)).replaceAll("#servername#", mailSender.getServerName()).replaceAll("#siteurl#", mailSender.getSiteUrl());

    mailSender.sendEmail(PokerSettings.getValueByName("email_for_system_messages"), PokerSettings.getValueByName("email_for_system_messages"), topic, htmlTpl, plainTpl);
  }
}