package _test;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import utils.Log;

public class TestMail
{
  public static void main(String[] args)
  {
    sendEmail("shumskydv@belhard.com", "loskutovav@belhard.com", "_test subj", "<h1>hello</h1>", "hi", "corp-mail-02.belhard.com", "shumskydv1", "210583");
  }

  public static int sendEmail(String from, String to, String subj, String html, String plain, String smtp, String mailLogin, String mailPass)
  {
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
    try {
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
    }
    catch (Throwable t) {
      t.printStackTrace();
      Log.out("Error: Can not send email: " + t.getMessage());
    }
    return result;
  }
}