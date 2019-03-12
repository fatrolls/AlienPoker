package emails;

import emails.templates.MailTemplate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import server.Server;
import settings.PokerSettings;
import utils.Log;

public class RestorePasswordMailSender extends MailSender
{
  private static final String PLAIN_TEMPLATE = "RestorePasswordMailPlain.tpl";
  private static final String HTML_TEMPLATE = "RestorePasswordMailHtml.tpl";
  private static final String MAIL_SUBJECT = "Restore Password Email";

  public int sendEmail(String email)
  {
    String fname = ""; String lname = ""; String login = ""; String password = "";
    int retCode = 0;

    Connection conn = Server.getDbConnection();
    try {
      PreparedStatement pstmt = conn.prepareStatement("select us_fname, us_lname, us_login, us_password from users where us_email=?");
      pstmt.setString(1, email);
      ResultSet rs = pstmt.executeQuery();
      if (rs.next()) {
        fname = rs.getString(1);
        lname = rs.getString(2);
        login = rs.getString(3);
        password = rs.getString(4);
      } else {
        retCode = 1;
      }
      rs.close();
      pstmt.close();
      conn.close();

      MailTemplate mt = new MailTemplate();
      String plain = mt.getTemplate("RestorePasswordMailPlain.tpl");
      String html = mt.getTemplate("RestorePasswordMailHtml.tpl");
      if (plain == null) {
        Log.out("CLASS RestorePasswordMailSender: ERROR - Plain Mail Template == null");
        return 1;
      }if (html == null) {
        Log.out("CLASS RestorePasswordMailSender: ERROR - Html Mail Template == null");
        return 1;
      }if (retCode > 0) {
        Log.out("CLASS RestorePasswordMailSender: ERROR - User Not Found");
        return retCode;
      }

      String plainTpl = plain.replaceAll("#fname#", fname).replaceAll("#lname#", lname).replaceAll("#login#", login).replaceAll("#password#", password).replaceAll("#servername#", getServerName()).replaceAll("#siteurl#", getSiteUrl());
      String htmlTpl = html.replaceAll("#fname#", escapeHtml(fname)).replaceAll("#lname#", escapeHtml(lname)).replaceAll("#login#", escapeHtml(login)).replaceAll("#password#", escapeHtml(password)).replaceAll("#servername#", getServerName()).replaceAll("#siteurl#", getSiteUrl());

      sendEmail(PokerSettings.getValueByName("email_for_contacts"), email, "Restore Password Email", htmlTpl, plainTpl);
    }
    catch (Exception e)
    {
      Log.out("CLASS RestorePasswordMailSender: ERROR - " + e.getMessage());
      try {
        conn.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      e.printStackTrace();
      retCode = 2;
    }

    return retCode;
  }
}