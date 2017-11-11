/*
 * Created on 2005-2-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package GUI.MODEL;

import java.util.*;
/**
 * XMLItem class contains the WMS's info,
 * which include wms version, Getmap Url, wms title,Request exception format 
 * and layers' name, title,crs.
 */
public class XMLItem {
	public String  _Request_wmsversion=null; 
	public String  _Request_getmapurl=null; 
	public String  _Request_wmstitle=null; 
	public String  _Request_exceptions=null; 

	public Vector _Request_format=new Vector();
    public Vector _Request_layersname=new Vector();
    public Vector _Request_layerstitle=new Vector();
    public Vector _Request_layersCRS=new Vector();
    
    public Vector _layernamesvector = new Vector();
    public Vector _layertitlesvector= new Vector();
    public Vector _layercrssvector   = new Vector();
    
    public Vector _layercrssvector_devide   = new Vector();

    /**Creates a new instance of server list **/
    //public XMLItem(String Request_wmsversion,String Request_getmapurl,String Request_wmstitle,String Request_exception_format){
        //this._Request_wmsversion=Request_wmsversion;
        //this._Request_getmapurl=Request_getmapurl;
        //this._Request_wmstitle=Request_wmstitle;
        //this._Request_exception_format=Request_exception_format;
    //}

    /** Return WMS's version */
    public String getRequest_wmsversion(){
        return _Request_wmsversion;
    }
    
    public void setRequest_wmsversion(String sRequest_wmsversion){
        this._Request_wmsversion=sRequest_wmsversion;
    }
    
    /** Return server's Getmap URl */
    public String getRequest_getmapurl(){
        return _Request_getmapurl;
    }
    
    public void setRequest_getmapurl(String sRequest_getmapurl){
    	this._Request_getmapurl=sRequest_getmapurl;
    }
    
    /** Return WMS's title */
    public String getRequest_wmstitle(){
        return _Request_wmstitle;
    }
    
    public void setRequest_wmstitle(String sRequest_wmstitle){
    	this._Request_wmstitle=sRequest_wmstitle;
        System.out.println("Successfully set the wmstitle") ;
    }
    
    /** Return WMS's Request exception  */
    public String getRequest_exceptions(){
        return _Request_exceptions;
    }
    
    public void setRequest_exceptions(String sRequest_exceptions){
    	this._Request_exceptions=sRequest_exceptions;
    }
    
    /** Return WMS's Request format */
    public Vector getRequest_format(){
        return _Request_format;
    }
    
    public void setRequest_format(Vector vRequest_format){
    	this._Request_format=vRequest_format;
    }
    /** Return WMS's CRSs */
    public Vector getRequest_layersCRS() {
        return _Request_layersCRS;
    }
    
    public void setRequest_layersCRS(Vector vRequest_layersCRS){
    	this._Request_layersCRS=vRequest_layersCRS;
    }
    
    /** Return layer's names */
    public Vector getlayernamesvector() {
        return _layernamesvector;
    }
    
    public void setlayernamesvector(Vector vlayernames){
    	this._layernamesvector=vlayernames;
    }
    
    /** Return layer's titles */
    public Vector getlayertitlesvector() {
        return _layertitlesvector;
    }
    
    public void setlayertitlesvector(Vector vlayertitles){
    	this._layertitlesvector=vlayertitles;
    }
    
    /** Return layer's CRSs */
    public Vector getlayercrssvector() {
        return _layercrssvector;
    }
    
    public void setlayercrssvector(Vector vlayercrss){
    	this._layercrssvector=vlayercrss;
    }
    

    /**
    * Split the String, check the splitted char which is used to split, and take the substring
    * @param original Srting needed to be splitted
    * @paran regex splitted char
    * @return The vector after splitted
    */
    public void split(String original,String regex)
    {
     //Start position of String
     int startIndex = 0;
     //Store the result in vector
     Vector v = new Vector();
     //The string array of result
     //String[] str = null;
     //Start position of stored substring
     int index = 0;

     //Get the position of substring
     startIndex = original.indexOf(regex); 
     //System.out.println("0" + startIndex); 
     //If the start position if string is shorter than the length of string, means the postion is not at the end
     //-1 means at the end of string
     while(startIndex < original.length() && startIndex != -1)
         {
         String temp = original.substring(index,startIndex);
         //System.out.println(" " + startIndex);
         //get the substring
         String temp_substring=null;
         temp_substring=trim(temp,'[');
         temp_substring=trim(temp_substring,' ');
         temp_substring=trim(temp_substring,',');
         v.addElement(temp_substring);
         System.out.println("layers srs after trimmed: "+temp_substring);
         //Set the start position of the next substring
         index = startIndex + regex.length();

         //Get the position of fitted substring
         startIndex = original.indexOf(regex,startIndex + regex.length());
         }

     //Get the substring at the end
     v.addElement(original.substring(index + 1 - regex.length()));
     //Convert the Vector to array
//     str = new String[v.size()];
//     for(int i=0;i<v.size();i++)
//         {
//         str[i] = (String)v.elementAt(i);
//         }
     _layercrssvector_devide=v;
    }

    /**
     * Trim the String, delete the front and back char of the string
     * @param str Srting needed to be trimmed
     * @paran splitChar splitted char
     * @return The String that has been trimmed
     */ 
     private static String trim(String str, char splitChar) { 
         int beginIndex = 0, endIndex = str.length(); 
         for (int i = 0; i < str.length(); i++) { 
             if (str.charAt(i) != splitChar) { 
                 beginIndex = i; 
                 break; 
                 } 
             } 
         for (int i = str.length(); i > 0; i--) { 
             if (str.charAt(i - 1) != splitChar) { 
                 endIndex = i; 
                 break; 
                 } 
             } 
         return str.substring(beginIndex, endIndex); 
         } 

    
    /**Get the new vector of layers srs after the string splitting*/
  public Vector GetDevidelayercrsVector() {
     return _layercrssvector_devide;
} 
    
}
