package emails;

import emails.templates.MailTemplate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;
import server.Server;
import settings.PokerSettings;
import utils.CommonLogger;

public class RegistrationPasswordMailSender extends MailSender
{
  private static final String PLAIN_TEMPLATE = "RegistrationPasswordMailPlain.tpl";
  private static final String HTML_TEMPLATE = "RegistrationPasswordMailHtml.tpl";
  private static final String MAIL_SUBJECT = "Registration Complete";

  public int sendEmail(int playerId)
  {
    String fname = ""; String lname = ""; String login = ""; String password = ""; String email = "";
    int retCode = 0;

    Connection conn = Server.getDbConnection();
    try {
      PreparedStatement pstmt = conn.prepareStatement("select us_fname, us_lname, us_login, us_password, us_email from users where user_id=?");
      pstmt.setInt(1, playerId);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        fname = rs.getString(1);
        lname = rs.getString(2);
        login = rs.getString(3);
        password = rs.getString(4);
        email = rs.getString(5);
      } else {
        retCode = 1;
      }
      rs.close();
      pstmt.close();
      conn.close();

      MailTemplate mt = new MailTemplate();
      String plain = mt.getTemplate("RegistrationPasswordMailPlain.tpl");
      String html = mt.getTemplate("RegistrationPasswordMailHtml.tpl");
      if (plain == null) {
        CommonLogger.getLogger().warn("CLASS RegistrationPasswordMailSender: ERROR - Plain Mail Template == null");
        return 1;
      }if (html == null) {
        CommonLogger.getLogger().warn("CLASS RegistrationPasswordMailSender: ERROR - Html Mail Template == null");
        return 1;
      }if (retCode > 0) {
        CommonLogger.getLogger().warn("CLASS RegistrationPasswordMailSender: ERROR - User Not Found");
        return retCode;
      }

      String plainTpl = plain.replaceAll("#fname#", fname).replaceAll("#lname#", lname).replaceAll("#login#", login).replaceAll("#password#", password).replaceAll("#servername#", getServerName()).replaceAll("#siteurl#", getSiteUrl());
      String htmlTpl = html.replaceAll("#fname#", escapeHtml(fname)).replaceAll("#lname#", escapeHtml(lname)).replaceAll("#login#", escapeHtml(login)).replaceAll("#password#", escapeHtml(password)).replaceAll("#servername#", getServerName()).replaceAll("#siteurl#", getSiteUrl());

      sendEmail(PokerSettings.getValueByName("email_for_contacts"), email, "Registration Complete", htmlTpl, plainTpl);
    }
    catch (Exception e)
    {
      CommonLogger.getLogger().warn("CLASS RegistrationPasswordMailSender: ERROR - ", e);
      try {
        conn.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      CommonLogger.getLogger().warn(e);
      retCode = 2;
    }

    return retCode;
  }
}