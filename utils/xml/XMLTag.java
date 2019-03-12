package utils.xml;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

public class XMLTag
{
  private String tagName;
  private String tagContent;
  private ArrayList tagParams = new ArrayList();
  private boolean hasContent = false;
  private ArrayList nestedTags = new ArrayList();
  private XMLTag parentTag = null;

  public XMLTag() {
  }

  public XMLTag(String tagName) {
    this.tagName = tagName;
  }

  public void invalidate() {
    tagParams.clear();
    tagParams = null;

    Iterator it = nestedTags.iterator();
    while (it.hasNext()) {
      XMLTag tag = (XMLTag)it.next();
      tag.invalidate();
    }

    nestedTags.clear();
    nestedTags = null;

    parentTag = null;
  }

  public void addNestedTag(XMLTag tag) {
    nestedTags.add(tag);
    tag.setParent(this);

    hasContent = true;
  }

  public XMLTag getParent() {
    return parentTag;
  }

  public void setParent(XMLTag tag) {
    parentTag = tag;
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();

    buffer.append("<").append(getTagName());

    if (tagParams.size() > 0) {
      Iterator it = tagParams.iterator();
      while (it.hasNext()) {
        XMLParam param = (XMLParam)it.next();
        buffer.append(" ").append(param.toString()).append(" ");
      }
    }

    if (!hasContent) {
      buffer.append("/>");
    } else {
      buffer.append(">");

      if (nestedTags.size() > 0) {
        Iterator it = nestedTags.iterator();
        while (it.hasNext()) {
          XMLTag tag = (XMLTag)it.next();
          buffer.append(tag.toString());
        }
      } else {
        buffer.append(getTagContent());
      }
      buffer.append("</").append(getTagName()).append(">");
    }

    buffer.append('\n');

    return buffer.toString();
  }

  public ArrayList getParams() {
    return tagParams;
  }

  public void addParam(XMLParam param) {
    tagParams.add(param);
  }

  public void addParam(String name, String value) {
    try {
      tagParams.add(new XMLParam(name, URLEncoder.encode(value, "ISO-8859-1")));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  public void addParam(String name, float value) {
    tagParams.add(new XMLParam(name, value));
  }

  public void addParam(String name, int value) {
    tagParams.add(new XMLParam(name, value));
  }

  public String getTagName() {
    return tagName;
  }

  public void setTagName(String tagName) {
    this.tagName = tagName;
  }

  public void setTagContent(String tagContent) {
    this.tagContent = tagContent;
    hasContent = true;
  }

  public String getTagContent() {
    return tagContent;
  }
}