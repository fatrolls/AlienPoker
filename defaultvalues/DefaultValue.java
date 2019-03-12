package defaultvalues;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Locale;

public class DefaultValue
{
  public static final BigDecimal ZERO_BIDECIMAL = new BigDecimal(0).setScale(2, 5);
  public static final String DEFAULT_PASSWORD = "";
  public static final String EMPTY_STRING = "";
  public static final Calendar DEFAULT_DATE = Calendar.getInstance(Locale.ENGLISH);
}