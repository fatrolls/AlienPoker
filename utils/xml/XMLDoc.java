package utils.xml;

public class XMLDoc
{
  private XMLTag currentTag = null;
  private XMLTag startTag = null;
  private String append = null;

  public XMLTag startTag(String tagName)
  {
    if (startTag == null) {
      startTag = new XMLTag(tagName);
      currentTag = startTag;
    }
    else {
      XMLTag newTag = new XMLTag(tagName);
      currentTag.addNestedTag(newTag);
      currentTag = newTag;
    }

    return currentTag;
  }

  public void invalidate()
  {
    if (startTag != null)
      startTag.invalidate();
  }

  public void endTag()
  {
    currentTag = currentTag.getParent();
  }

  public String toString()
  {
    if (startTag != null) {
      String result = startTag.toString();
      if (append != null) {
        result = result + append;
      }
      return result;
    }
    return null;
  }

  public void setAppend(String append)
  {
    this.append = append;
  }
}