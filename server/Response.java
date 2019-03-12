package server;

import utils.xml.XMLDoc;
import utils.xml.XMLTag;

public class Response
{
  private static final String R_RESULT_CODE_OK = "OK";
  private static final String R_RESULT_CODE_ERROR = "ERROR";
  private boolean resultStatus = false;
  private String resultMessage = null;
  private String parametersXML = null;
  private String onCommand = null;
  private static final String TAG_NAME_RESPONSE = "R";
  private static final String TAG_NAME_RESULT = "R";
  private static final String TAG_PARAM_CODE = "CODE";
  private static final String TAG_PARAM_MSG = "MSG";
  private static final String TAG_NAME_PARAMETERS = "P";
  private static final String TAG_NAME_QUERY = "Q";
  private static final String TAG_PARAM_CMD = "C";

  public Response()
  {
  }

  public Response(String onCommand)
  {
    this.onCommand = onCommand;
  }

  public static Response getHelloResponse()
  {
    Response response = new Response("HELLO");
    response.setResultStatus(true, "You connected to poker server v.1.025");

    return response;
  }

  public static Response getUnrecognizedCmdResponse()
  {
    Response response = new Response("UNDEFINED");
    response.setResultStatus(false, "Unrecognized command");

    return response;
  }

  public void setParametersXML(String parametersXml)
  {
    parametersXML = parametersXml;
  }

  public void setResultStatus(boolean status)
  {
    resultStatus = status;
  }

  public void setResultStatus(boolean status, String message)
  {
    resultStatus = status;
    resultMessage = message;
  }

  public String getXML()
  {
    XMLDoc xmlDoc = new XMLDoc();
    xmlDoc.startTag("R");

    XMLTag tag = xmlDoc.startTag("R");
    tag.addParam("CODE", getResultCode());
    tag.addParam("MSG", getResultMessage());
    xmlDoc.endTag();

    tag = xmlDoc.startTag("P");
    tag.setTagContent(getParametersXML());
    xmlDoc.endTag();

    xmlDoc.setAppend("");
    String xml = xmlDoc.toString();
    xmlDoc.invalidate();

    return xml;
  }

  public String getResultMessage()
  {
    return resultMessage != null ? resultMessage : "";
  }

  private String getResultCode()
  {
    String code = "OK";
    if (!resultStatus) {
      code = "ERROR";
    }
    return code;
  }

  private String getParametersXML()
  {
    return parametersXML == null ? "" : parametersXML;
  }

  public void setOnCommand(String onCommand)
  {
    this.onCommand = onCommand;
  }

  public String getOnCommand()
  {
    return onCommand;
  }
}