package server;

import java.util.HashMap;

public class ParamParser
{
  private String input;
  private String paramName;
  private String paramValue;
  private static final int MIN_PARAM_INPUT = 3;
  private static final int PARAM_NAME_BEGIN_INDEX = 1;
  private static final int PARAM_NAME_END_INDEX = 2;

  public ParamParser(String input)
  {
    this.input = input;
  }

  public boolean parse()
  {
    if (input.length() < 3) {
      return false;
    }

    paramName = input.substring(1, 2);
    paramValue = input.substring(2);

    return true;
  }

  public String getParamValue()
  {
    return paramValue;
  }

  public String getParamName()
  {
    return paramName;
  }

  public static float getFloat(HashMap params, String paramName)
  {
    String val = (String)params.get(paramName);

    float value = 0.0F;
    try {
      value = Float.parseFloat(val);
    }
    catch (Exception e) {
    }
    return value;
  }

  public static int getInt(HashMap params, String paramName)
  {
    String val = (String)params.get(paramName);

    int value = 0;
    try {
      value = Integer.parseInt(val);
    }
    catch (Exception e) {
    }
    return value;
  }
}