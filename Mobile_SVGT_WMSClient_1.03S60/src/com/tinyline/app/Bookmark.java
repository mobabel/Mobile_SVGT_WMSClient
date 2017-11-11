package com.tinyline.app;

public class Bookmark
{
  private static final char delimiter = '|';

  /**
   * Creates a <tt>Bookmark</tt> object from the
   * given <tt>String</tt>. It uses the following format:
   *
   * name|URL
   * @param s a string representing a <tt>Bookmark</tt>
   */
  public static Bookmark create(String s)
	{
     String pname, purl;
     int index = 0;
     // Parse name and url.
     index = s.indexOf(delimiter);
     if (index == -1)
       throw new IllegalArgumentException("Could not parse name.");
     pname = s.substring(0,index);
     purl = s.substring(index+1);
     return new Bookmark(pname, purl);
  }

  public String name, url;

  /**
   * Creates a new <tt>Bookmark</tt> with the specified
   * name and URL.
   *
   * @param name a name for the <tt>Bookmark</tt>
   * @param purl a URL for the <tt>Bookmark</tt>
   */
  protected Bookmark(String pname, String purl)
	{
     name = pname;
     url  = purl;
  }

  /**
   * Returns the serialized <tt>Bookmark</tt> object.
   */
  public String getRaw()
	{
    StringBuffer buffer = new StringBuffer();
    buffer.append(name);
    buffer.append(delimiter);
    buffer.append(url);
    return buffer.toString();
  }
}


