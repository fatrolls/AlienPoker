package utils.xml;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class XMLParam
{
  private String paramName;
  private String paramValue;

  public XMLParam()
  {
    paramName = "";
    paramValue = "";
  }

  public XMLParam(String paramName, String paramValue)
  {
    this.paramName = paramName;
    this.paramValue = paramValue;
  }

  public XMLParam(String paramName, int paramValue)
  {
    this.paramName = paramName;
    this.paramValue = Integer.toString(paramValue);
  }

  public XMLParam(String paramName, float paramValue)
  {
    this.paramName = paramName;
    NumberFormat d = DecimalFormat.getInstance();
    d.setGroupingUsed(false);
    this.paramValue = d.format(paramValue);
  }

  public void setParamName(String paramName)
  {
    this.paramName = paramName;
  }

  public void setParamValue(String paramValue)
  {
    this.paramValue = paramValue;
  }

  public void setParamValue(int paramValue)
  {
    this.paramValue = Integer.toString(paramValue);
  }

  public void setParamValue(float paramValue)
  {
    this.paramValue = Float.toString(paramValue);
  }

  public String getParamName()
  {
    return paramName;
  }

  public String getParamValue()
  {
    return paramValue;
  }

  public String toString()
  {
    return createXMLParam(getParamName(), getParamValue());
  }

  public static String createXMLParam(String name, String value)
  {
    StringBuffer param = new StringBuffer();

    param.append(name).append("=").append("\"").append(value).append("\"");

    return param.toString();
  }

  public static String createXMLParam(String name, int value)
  {
    return createXMLParam(name, Integer.toString(value));
  }

  public static String createXMLParam(String name, float value)
  {
    return createXMLParam(name, Float.toString(value));
  }
}