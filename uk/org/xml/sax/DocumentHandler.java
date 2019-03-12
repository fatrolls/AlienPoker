package uk.org.xml.sax;

import java.io.Writer;
import org.xml.sax.AttributeList;
import org.xml.sax.SAXException;

public abstract interface DocumentHandler extends org.xml.sax.DocumentHandler
{
  public abstract Writer startDocument(Writer paramWriter)
    throws SAXException;

  public abstract Writer startElement(String paramString, AttributeList paramAttributeList, Writer paramWriter)
    throws SAXException;
}