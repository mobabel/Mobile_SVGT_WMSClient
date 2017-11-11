/*
 * Created on 2005-2-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package GUI.MODEL;

import java.util.*;
/**
 * ServerListItem class contains the server head's info,
 * which include server URL and Http head 
 */
public class ServerItem {
	private String _serverhead       = null;
    private String _serverurl  = null;
    private String _servercontenttype  = null;
   
    /** Return server's URL */
    public String getServerURL(){
        return _serverurl;
    }
    
    public void setServerURL(String surl){
        this._serverurl=surl;
    }
    
    /** Return server's head */
    public String getServerHead(){
        return _serverhead;
    }
    
    public void setServerHead(String shead){
    	this._serverhead=shead;
    	//System.out.println("Head in ServerItem is : "+shead) ;
    }
    
    /** Return server's content type */
    public String getServerContentType(){
        return _servercontenttype;
    }
    
    public void setServerContentType(String sctype){
    	this._servercontenttype=sctype;
    	//System.out.println("Head in ServerItem is : "+shead) ;
    }
}
