package uk.co.wilson.xml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.EmptyStackException;
import java.util.Locale;
import java.util.Stack;
import java.util.Vector;
import org.xml.sax.AttributeList;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import uk.org.xml.sax.Parser;

public class MinML
  implements Parser, Locator, uk.org.xml.sax.DocumentHandler, ErrorHandler
{
  public static final int endStartName = 0;
  public static final int emitStartElement = 1;
  public static final int emitEndElement = 2;
  public static final int possiblyEmitCharacters = 3;
  public static final int emitCharacters = 4;
  public static final int emitCharactersSave = 5;
  public static final int saveAttributeName = 6;
  public static final int saveAttributeValue = 7;
  public static final int startComment = 8;
  public static final int endComment = 9;
  public static final int incLevel = 10;
  public static final int decLevel = 11;
  public static final int startCDATA = 12;
  public static final int endCDATA = 13;
  public static final int processCharRef = 14;
  public static final int writeCdata = 15;
  public static final int exitParser = 16;
  public static final int parseError = 17;
  public static final int discardAndChange = 18;
  public static final int discardSaveAndChange = 19;
  public static final int saveAndChange = 20;
  public static final int change = 21;
  public static final int inSkipping = 0;
  public static final int inSTag = 1;
  public static final int inPossiblyAttribute = 2;
  public static final int inNextAttribute = 3;
  public static final int inAttribute = 4;
  public static final int inAttribute1 = 5;
  public static final int inAttributeValue = 6;
  public static final int inAttributeQuoteValue = 7;
  public static final int inAttributeQuotesValue = 8;
  public static final int inETag = 9;
  public static final int inETag1 = 10;
  public static final int inMTTag = 11;
  public static final int inTag = 12;
  public static final int inTag1 = 13;
  public static final int inPI = 14;
  public static final int inPI1 = 15;
  public static final int inPossiblySkipping = 16;
  public static final int inCharData = 17;
  public static final int inCDATA = 18;
  public static final int inCDATA1 = 19;
  public static final int inComment = 20;
  public static final int inDTD = 21;
  private uk.org.xml.sax.DocumentHandler extDocumentHandler = this;
  private org.xml.sax.DocumentHandler documentHandler = this;
  private ErrorHandler errorHandler = this;
  private final Stack tags = new Stack();
  private int lineNumber = 1;
  private int columnNumber = 0;
  private final int initialBufferSize;
  private final int bufferIncrement;
  private static final byte[] charClasses = { 13, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 12, -1, -1, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 8, 7, 14, 14, 14, 3, 6, 14, 14, 14, 14, 14, 11, 14, 2, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 0, 5, 1, 4, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 14, 9, 14, 10 };

  private static final String[] operands = { "കᘑᘑᘑᘑᘑᘑᘑᘑᘑᘑᘑ\025\020ᘑ", "ᜑက଀ᜑᜑᜑᜑᜑᜑᜑᜑĔȀ᠑Ĕ", "ᜑခଁᜑᜑᜑᜑᜑᜑᜑᜑᜑȕ᠑Д", "ᜑခଁᜑᤑᤑᤑᤑᤑᤑᤑᤑ̕᠑Д", "ᤑᤑᤑᤑᤑ؆ᤑᤑᤑᤑᤑДԕ᠑Д", "ᤑᤑᤑᤑᤑ؆ᤑᤑᤑᤑᤑᤑԕ᠑ᤑ", "ᨑᨑᨑᨑᨑᨑܕࠕᨑᨑᨑᨑؕ᠑ᨑ", "ܔܔܔ܎ܔܔ̇ܔܔܔܔܔܔ᠑ܔ", "ࠔࠔࠔࠎࠔࠔࠔ̇ࠔࠔࠔࠔࠔ᠑ࠔ", "ᜑဂᜑᜑᜑᜑᜑᜑᜑᜑᜑऔक᠑औ", "ᬑᬑऄᬑᬑᬑᬑᬑሕᬑᬑᬑᬑ᠑ą", "ᜑဒᜑᜑᜑᜑᜑᜑᜑᜑᜑᜑᜑ᠑ᜑ", "ᜑᰑऒᜑฒᜑᜑᜑሒᜑᜑᜑᜑ᠑ē", "ᜑᰑऒᜑฒᜑᜑᜑሒᜑᜑᜑᜑ᠑ē", "ตตตต༕ตตตตตตตต᠑ต", "ต\025ตต༕ตตตตตตตต᠑ต", "ఃᄏᄏᄎᄏᄏᄏᄏᄏᄏᄏᄏန᠑ᄏ", "ਕᄏᄏᄎᄏᄏᄏᄏᄏᄏᄏᄏᄏ᠑ᄏ", "ᴑᴑᴑᴑᴑᴑᴑᴑᴑጌᴑᐈᴑ᠑ᔕ", "ጏጏጏጏጏጏጏጏጏጏᄍጏጏ᠑ጏ", "ᐕᐕᐕᐕᐕᐕᐕᐕᐕᐕᐕ\tᐕ᠑ᐕ", "ᔊ\013ᔕᔕᔕᔕᔕᔕᔕᔕᔕᔕᔕ᠑ᔕ", "expected Element", "unexpected character in tag", "unexpected end of file found", "attribute name not followed by '='", "invalid attribute value", "expecting end tag", "empty tag", "unexpected character after <!" };

  public MinML(int initialBufferSize, int bufferIncrement)
  {
    this.initialBufferSize = initialBufferSize;
    this.bufferIncrement = bufferIncrement;
  }

  public MinML() {
    this(256, 128);
  }

  public void parse(Reader in) throws SAXException, IOException {
    Vector attributeNames = new Vector();
    Vector attributeValues = new Vector();

    AttributeList attrs = new AttributeList(attributeNames, attributeValues) { private final Vector val$attributeNames;
      private final Vector val$attributeValues;

      public int getLength() { return val$attributeNames.size(); }

      public String getName(int i)
      {
        return (String)val$attributeNames.elementAt(i);
      }

      public String getType(int i) {
        return "CDATA";
      }

      public String getValue(int i) {
        return (String)val$attributeValues.elementAt(i);
      }

      public String getType(String name) {
        return "CDATA";
      }

      public String getValue(String name) {
        int index = val$attributeNames.indexOf(name);

        return index == -1 ? null : (String)val$attributeValues.elementAt(index);
      }
    };
    MinMLBuffer buffer = new MinMLBuffer(in);
    int currentChar = 0; int charCount = 0;
    int level = 0;
    int mixedContentLevel = -1;
    String elementName = null;
    String state = operands[0];

    lineNumber = 1;
    columnNumber = 0;
    try {
      String operand;
      while (true) { charCount++;

        currentChar = buffer.nextIn == buffer.lastIn ? buffer.read() : buffer.chars[MinMLBuffer.access$008(buffer)];
        int transition;
        int transition;
        if (currentChar > 93) {
          transition = state.charAt(14);
        } else {
          int charClass = charClasses[(currentChar + 1)];

          if (charClass == -1) fatalError("Document contains illegal control character with value " + currentChar, lineNumber, columnNumber);

          if (charClass == 12) {
            if (currentChar == 13) {
              currentChar = 10;
              charCount = -1;
            }

            if (currentChar == 10) {
              if (charCount == 0)
                continue;
              if (charCount != -1) charCount = 0;

              lineNumber += 1;
              columnNumber = 0;
            }
          }

          transition = state.charAt(charClass);
        }

        columnNumber += 1;

        operand = operands[(transition >>> 8)];
        int crefState;
        switch (transition & 0xFF)
        {
        case 0:
          elementName = buffer.getString();
          if ((currentChar != 62) && (currentChar != 47));
        case 1:
          Writer newWriter = extDocumentHandler.startElement(elementName, attrs, tags.empty() ? extDocumentHandler.startDocument(buffer) : buffer.getWriter());

          buffer.pushWriter(newWriter);
          tags.push(elementName);

          attributeValues.removeAllElements();
          attributeNames.removeAllElements();

          if (mixedContentLevel != -1) mixedContentLevel++;

          if (currentChar != 47);
        case 2:
          try
          {
            String begin = (String)tags.pop();

            buffer.popWriter();
            elementName = buffer.getString();

            if ((currentChar != 47) && (!elementName.equals(begin))) {
              fatalError("end tag </" + elementName + "> does not match begin tag <" + begin + ">", lineNumber, columnNumber);
            }
            else {
              documentHandler.endElement(begin);

              if (tags.empty()) {
                documentHandler.endDocument();
                jsr 654;
              }
            }
          }
          catch (EmptyStackException e) {
            fatalError("end tag at begining of document", lineNumber, columnNumber);
          }

          if (mixedContentLevel != -1) mixedContentLevel--; break;
        case 4:
          buffer.flush();
          break;
        case 5:
          if (mixedContentLevel == -1) mixedContentLevel = 0;

          buffer.flush();

          buffer.saveChar((char)currentChar);

          break;
        case 3:
          if (mixedContentLevel != -1) buffer.flush(); break;
        case 6:
          attributeNames.addElement(buffer.getString());
          break;
        case 7:
          attributeValues.addElement(buffer.getString());
          break;
        case 8:
          if (buffer.read() == 45);
        case 9:
          if ((goto 71) || 
            ((currentChar = buffer.read()) != 45))
            continue;
          while ((currentChar = buffer.read()) == 45);
          if (currentChar != 62) continue; break;
        case 10:
          level++;

          break;
        case 11:
          if (level != 0)
          {
            level--;
          }
          break;
        case 12:
          if ((buffer.read() != 67) || 
            (buffer.read() != 68) || 
            (buffer.read() != 65) || 
            (buffer.read() != 84) || 
            (buffer.read() != 65)) continue;
          if (buffer.read() == 91);
          break;
        case 13:
          if ((currentChar = buffer.read()) == 93)
          {
            while ((currentChar = buffer.read()) == 93) buffer.write(93);

            if (currentChar != 62)
            {
              buffer.write(93);
            }
          } else {
            buffer.write(93);
            buffer.write(currentChar);
          }break;
        case 14:
          crefState = 0;

          currentChar = buffer.read();
        case 17:
        case 16:
        case 15:
        case 18:
        case 19:
        case 20:
          while (true) if ("#amp;&pos;'quot;\"gt;>lt;<".charAt(crefState) == currentChar) {
              crefState++;

              if (currentChar == 59) {
                buffer.write("#amp;&pos;'quot;\"gt;>lt;<".charAt(crefState));
              }
              else if (currentChar == 35)
              {
                currentChar = buffer.read();
                int radix;
                if (currentChar == 120) {
                  int radix = 16;
                  currentChar = buffer.read();
                } else {
                  radix = 10;
                }

                int charRef = Character.digit((char)currentChar, radix);
                while (true)
                {
                  currentChar = buffer.read();

                  int digit = Character.digit((char)currentChar, radix);

                  if (digit == -1)
                    break;
                  charRef = (char)(charRef * radix + digit);
                }

                if ((currentChar == 59) && (charRef != -1)) {
                  buffer.write(charRef);
                }
                else
                {
                  fatalError("invalid Character Entitiy", lineNumber, columnNumber); continue;
                }
              } else {
                currentChar = buffer.read(); continue;
              }
            } else {
              crefState = "\001\013\006ÿÿÿÿÿÿÿÿ\021ÿÿÿÿÿ\025ÿÿÿÿÿÿ".charAt(crefState);

              if (crefState != 255) continue; fatalError("invalid Character Entitiy", lineNumber, columnNumber); continue;

              fatalError(operand, lineNumber, columnNumber);

              jsr 94;

              buffer.write(currentChar);
              break;

              buffer.reset();
              break;

              buffer.reset();

              buffer.saveChar((char)currentChar);
            }


        case 21:
        }

      }

      state = operand;
    }
    catch (IOException e)
    {
      errorHandler.fatalError(new SAXParseException(e.toString(), null, null, lineNumber, columnNumber, e));
    }
    finally {
      jsr 6; throw localObject1; localObject2 = returnAddress; errorHandler = this;
      extDocumentHandler = this; documentHandler = this;
      tags.removeAllElements();
    }
  }

  public void parse(InputSource source) throws SAXException, IOException {
    if (source.getCharacterStream() != null)
      parse(source.getCharacterStream());
    else if (source.getByteStream() != null)
      parse(new InputStreamReader(source.getByteStream()));
    else
      parse(new InputStreamReader(new URL(source.getSystemId()).openStream()));
  }

  public void parse(String systemId) throws SAXException, IOException {
    parse(new InputSource(systemId));
  }

  public void setLocale(Locale locale) throws SAXException {
    throw new SAXException("Not supported");
  }

  public void setEntityResolver(EntityResolver resolver)
  {
  }

  public void setDTDHandler(DTDHandler handler)
  {
  }

  public void setDocumentHandler(org.xml.sax.DocumentHandler handler) {
    documentHandler = (handler == null ? this : handler);
    extDocumentHandler = this;
  }

  public void setDocumentHandler(uk.org.xml.sax.DocumentHandler handler) {
    documentHandler = (this.extDocumentHandler = handler == null ? this : handler);
    documentHandler.setDocumentLocator(this);
  }

  public void setErrorHandler(ErrorHandler handler) {
    errorHandler = (handler == null ? this : handler);
  }

  public void setDocumentLocator(Locator locator) {
  }

  public void startDocument() throws SAXException {
  }

  public Writer startDocument(Writer writer) throws SAXException {
    documentHandler.startDocument();
    return writer;
  }

  public void endDocument() throws SAXException
  {
  }

  public void startElement(String name, AttributeList attributes) throws SAXException
  {
  }

  public Writer startElement(String name, AttributeList attributes, Writer writer) throws SAXException {
    documentHandler.startElement(name, attributes);
    return writer;
  }

  public void endElement(String name) throws SAXException {
  }

  public void characters(char[] ch, int start, int length) throws SAXException {
  }

  public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
  }

  public void processingInstruction(String target, String data) throws SAXException {
  }

  public void warning(SAXParseException e) throws SAXException {
  }

  public void error(SAXParseException e) throws SAXException {
  }

  public void fatalError(SAXParseException e) throws SAXException {
    throw e;
  }

  public String getPublicId() {
    return "";
  }

  public String getSystemId()
  {
    return "";
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public int getColumnNumber() {
    return columnNumber;
  }

  private void fatalError(String msg, int lineNumber, int columnNumber) throws SAXException {
    errorHandler.fatalError(new SAXParseException(msg, null, null, lineNumber, columnNumber));
  }

  private class MinMLBuffer extends Writer
  {
    private int nextIn = 0; private int lastIn = 0;
    private char[] chars = new char[initialBufferSize];
    private final Reader in;
    private int count = 0;
    private Writer writer = this;
    private boolean flushed = false;
    private boolean written = false;

    public MinMLBuffer(Reader in)
    {
      this.in = in;
    }

    public void close() throws IOException {
      flush();
    }

    public void flush() throws IOException {
      try {
        _flush();
        if (writer != this) writer.flush(); 
      }
      finally
      {
        flushed = true;
      }
    }

    public void write(int c) throws IOException {
      written = true;
      chars[(count++)] = (char)c;
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
      written = true;
      System.arraycopy(cbuf, off, chars, count, len);
      count += len;
    }

    public void saveChar(char c) {
      written = false;
      chars[(count++)] = c;
    }

    public void pushWriter(Writer writer) {
      tags.push(this.writer);

      this.writer = (writer == null ? this : writer);

      flushed = (this.written = 0);
    }

    public Writer getWriter() {
      return writer;
    }

    public void popWriter() throws IOException {
      try {
        if ((!flushed) && (writer != this)) writer.flush(); 
      }
      finally
      {
        writer = ((Writer)tags.pop());
        flushed = (this.written = 0);
      }
    }

    public String getString() {
      String result = new String(chars, 0, count);

      count = 0;
      return result;
    }

    public void reset() {
      count = 0;
    }

    public int read() throws IOException {
      if (nextIn == lastIn) {
        if (count != 0) {
          if (written) {
            _flush();
          } else if (count >= chars.length - bufferIncrement) {
            char[] newChars = new char[chars.length + bufferIncrement];

            System.arraycopy(chars, 0, newChars, 0, count);
            chars = newChars;
          }
        }

        int numRead = in.read(chars, count, chars.length - count);

        if (numRead == -1) return -1;

        nextIn = count;
        lastIn = (count + numRead);
      }

      return chars[(nextIn++)];
    }

    private void _flush() throws IOException {
      if (count != 0)
        try {
          if (writer == this)
            try {
              documentHandler.characters(chars, 0, count);
            }
            catch (SAXException e) {
              throw new IOException(e.toString());
            }
          else
            writer.write(chars, 0, count);
        }
        finally
        {
          count = 0;
        }
    }
  }
}