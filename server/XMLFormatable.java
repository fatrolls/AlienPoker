package server;

import java.io.UnsupportedEncodingException;

public abstract interface XMLFormatable
{
  public abstract String toXML()
    throws UnsupportedEncodingException;
}