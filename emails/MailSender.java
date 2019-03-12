package emails;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.Logger;
import settings.PokerSettings;
import utils.Log;

public class MailSender
{
  static Logger log = Logger.getLogger(MailSender.class);
  private String smtp;
  private String serverName;
  private String siteUrl;
  private String mailLogin;
  private String mailPass;

  public MailSender()
  {
    loadConfig();
  }

  public int sendEmail(String from, String to, String subj, String html, String plain) {
    int result = 0;

    if (to == null) {
      Log.out("Wrong Email To parameter");
      return 1;
    }if (subj == null) {
      Log.out("Wrong SUBJECT parameter");
      return 1;
    }if (html == null) {
      Log.out("Wrong HTML parameter");
      return 1;
    }if (plain == null) {
      Log.out("Wrong PLAIN parameter");
      return 1;
    }

    Properties props = new Properties();

    props.put("mail.smtp.host", smtp);
    try
    {
      Authenticator authenticator = null;

      if (mailLogin.length() > 0) {
        authenticator = new MyAuthenticator(mailLogin, mailPass);
      }

      Session session = Session.getDefaultInstance(props, authenticator);
      Message msg = new MimeMessage(session);

      msg.setFrom(new InternetAddress(from));

      msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
      msg.setSubject(subj);

      MimeMultipart mp = new MimeMultipart();

      BodyPart tp = new MimeBodyPart();
      tp.setText(plain);
      mp.addBodyPart(tp);

      tp = new MimeBodyPart();
      tp.setContent(html, "text/html");
      mp.addBodyPart(tp);

      mp.setSubType("alternative");

      msg.setContent(mp);

      Transport.send(msg);
    } catch (Throwable t) {
      log.error("", t);
    }

    return result;
  }

  private void loadConfig() {
    smtp = PokerSettings.getString("smtp");
    serverName = PokerSettings.getString("servername");
    siteUrl = PokerSettings.getString("siteurl");
    mailLogin = PokerSettings.getString("maillogin");
    mailPass = PokerSettings.getString("mailpass");
  }

  public String getServerName() {
    return serverName;
  }

  public String getSiteUrl() {
    return siteUrl;
  }

  public static String escapeHtml(String str) {
    String s = str;
    s = s.replaceAll("&", "&amp;");
    s = s.replaceAll("<", "&lt;");
    s = s.replaceAll(">", "&gt;");
    s = s.replaceAll("\"", "&quot;");
    s = s.replaceAll("'", "&#039;");
    return s;
  }
  public class MyAuthenticator extends Authenticator {
    private String login;
    private String pass;

    public MyAuthenticator(String login, String pass) { this.login = login;
      this.pass = pass; }

    protected PasswordAuthentication getPasswordAuthentication()
    {
      return new PasswordAuthentication(login, pass);
    }
  }
}