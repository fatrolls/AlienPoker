package _test;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

class MyAuthenticator extends Authenticator
{
  private String login;
  private String pass;

  public MyAuthenticator(String login, String pass)
  {
    this.login = login;
    this.pass = pass;
  }

  protected PasswordAuthentication getPasswordAuthentication() {
    return new PasswordAuthentication(login, pass);
  }
}