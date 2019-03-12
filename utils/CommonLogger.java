package utils;

import org.apache.log4j.Logger;

public class CommonLogger
{
  private static final Logger log = Logger.getLogger(CommonLogger.class);

  public static Logger getLogger() {
    return log;
  }
}