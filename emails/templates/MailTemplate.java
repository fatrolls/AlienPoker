package emails.templates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;

public class MailTemplate
{
  static Logger log = Logger.getLogger(MailTemplate.class);

  public String getTemplate(String template) { StringBuffer s = new StringBuffer();
    try
    {
      InputStream is = MailTemplate.class.getResourceAsStream(template);
      BufferedReader in = new BufferedReader(new InputStreamReader(is));
      String line;
      while ((line = in.readLine()) != null) {
        s.append(line);
      }
      in.close();
    } catch (IOException e) {
      log.error("", e);
    }
    return s.toString();
  }
}