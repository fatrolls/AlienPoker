package game.chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatFilter
{
  private static final ChatFilter chatFilter = new ChatFilter();
  private static final Pattern[] patterns = { Pattern.compile("(a)sshol(e)", 2), Pattern.compile("(m)otherfucke(r)", 2), Pattern.compile("(f)ucke(r)", 2), Pattern.compile("(f)uc(k)", 2), Pattern.compile("(s)hi(t)", 2), Pattern.compile("(d)ic(k)", 2), Pattern.compile("(p)uss(y)", 2), Pattern.compile("(c)un(t)", 2), Pattern.compile("(b)itc(h)", 2) };
  private static final char CENSORED_CHARACTER = '*';

  public static ChatFilter getInstance()
  {
    return chatFilter;
  }

  public String filter(String string)
  {
    String s = string;
    int size = patterns.length;
    for (int i = 0; i < size; i++) {
      Pattern p = patterns[i];
      Matcher m = p.matcher(s);

      StringBuffer sb = new StringBuffer();
      sb.append("$1");
      int l = p.pattern().length() - 6;
      for (int j = 0; j < l; j++) {
        sb.append('*');
      }
      sb.append("$2");
      s = m.replaceAll(sb.toString());
    }

    return s;
  }

  public static void main(String[] s) {
    getInstance().filter("Fucken asshole ");
  }
}