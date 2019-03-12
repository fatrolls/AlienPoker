package emails;

import emails.templates.MailTemplate;
import game.Player;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.log4j.Logger;
import settings.PokerSettings;
import utils.CommonLogger;

public class CreateTeamMailSender extends MailSender
{
  private static final String PLAIN_TEMPLATE = "CreateTeamMailPlain.tpl";
  private static final String HTML_TEMPLATE = "CreateTeamMailHtml.tpl";
  private static final String MAIL_SUBJECT = "Server Notification: Create Team";

  public int sendEmail(Player currentPlayer, ArrayList teamsList, String teamName, String message)
  {
    int retCode = 0;
    try
    {
      MailTemplate mt = new MailTemplate();
      String plain = mt.getTemplate("CreateTeamMailPlain.tpl");
      String html = mt.getTemplate("CreateTeamMailHtml.tpl");
      if (plain == null) {
        CommonLogger.getLogger().warn("CLASS CreateTeamMailSender: ERROR - Plain Mail Template == null");
        return 1;
      }if (html == null) {
        CommonLogger.getLogger().warn("CLASS CreateTeamMailSender: ERROR - Html Mail Template == null");
        return 1;
      }if (retCode > 0) {
        CommonLogger.getLogger().warn("CLASS CreateTeamMailSender: ERROR - User Not Found");
        return retCode;
      }

      StringBuffer members = new StringBuffer();
      Iterator iter = teamsList.iterator();
      boolean first = true;
      while (iter.hasNext()) {
        if (!first) {
          members.append(", ");
        }
        String player = (String)iter.next();
        members.append(player.trim());
        first = false;
      }

      String plainTpl = plain.replaceAll("#fname#", currentPlayer.getFirstName()).replaceAll("#lname#", currentPlayer.getLastName()).replaceAll("#login#", currentPlayer.getLogin()).replaceAll("#email#", currentPlayer.getEmail()).replaceAll("#ID#", "" + currentPlayer.getID()).replaceAll("#teamname#", teamName).replaceAll("#members#", members.toString()).replaceAll("#message#", message).replaceAll("#servername#", getServerName()).replaceAll("#siteurl#", getSiteUrl());

      members = new StringBuffer();
      iter = teamsList.iterator();
      first = true;
      while (iter.hasNext()) {
        if (!first) {
          members.append(",&nbsp;");
        }
        String player = (String)iter.next();
        members.append(escapeHtml(player.trim()));
        first = false;
      }

      String htmlTpl = html.replaceAll("#fname#", escapeHtml(currentPlayer.getFirstName())).replaceAll("#lname#", escapeHtml(currentPlayer.getLastName())).replaceAll("#login#", escapeHtml(currentPlayer.getLogin())).replaceAll("#email#", escapeHtml(currentPlayer.getEmail())).replaceAll("#ID#", escapeHtml("" + currentPlayer.getID())).replaceAll("#teamname#", escapeHtml(teamName)).replaceAll("#members#", escapeHtml(members.toString())).replaceAll("#message#", escapeHtml(message)).replaceAll("#servername#", getServerName()).replaceAll("#siteurl#", getSiteUrl());

      sendEmail(PokerSettings.getValueByName("email_for_system_messages"), PokerSettings.getValueByName("email_for_system_messages"), "Server Notification: Create Team", htmlTpl, plainTpl);
    }
    catch (Exception e)
    {
      CommonLogger.getLogger().warn("CLASS CreateTeamMailSender: ERROR - " + e.getMessage(), e);
      e.printStackTrace();
      retCode = 2;
    }

    return retCode;
  }
}