/*
 * Created on 2005-2-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package GUI.MODEL;

import java.util.*;
/**
 * ServerListItem class contains the server list's properties,
 * which include server name and server URL
 */
public class ServerListItem {
	private int _id;
    private String _serverurl=null;
    private String _servername=null;
    //  The server item vector
    protected Vector 	_items = new Vector();  
    
    /**Creates a new instance of server list **/
    public ServerListItem(String servername,String serverurl){
        this._servername=servername;
        this._serverurl=serverurl;
    }
    
    public ServerListItem(int id,byte[] data){
        this._id=id;
    	String temp=new String(data);
        int ind=temp.indexOf("|");
        if(ind!=-1){
            _servername=temp.substring(0,ind);
            _serverurl=temp.substring(ind+1);
        }
    }
    
    /** Creates a new instance of server list with record store string **/
    public ServerListItem(String storeString){
        int i = storeString.indexOf("|");
        if(i>0) {
        	_servername = storeString.substring(0,i);
        	_serverurl  = storeString.substring(i+1);
        }
    }
    
    public int getId(){
    	return _id;
    }
    
    public void setId(int id){
    	this._id=id;
    }
    
    /** Return server's URL */
    public String getServerURL(){
        return _serverurl;
    }
    
    public void setServerURL(String surl){
        this._serverurl=surl;
    }
    
    /** Return server's name */
    public String getServerName(){
        return _servername;
    }
    
    public void setServerName(String sname){
    	this._servername=sname;
    }
    
    /** Return record store string */
    public String getStoreString(){
        return _servername + "|" + _serverurl;
    }
    
    /** Return RSS feed items */
    public Vector getItems() {
        return _items;
    }
    
    //public byte[] getBytes(){
        //String temp=null;
        //if(_servername==null||_serverurl==null){
            //return null;
        //}else{
           //temp=_servername+"|"+_serverurl;
        //}
        //return temp.getBytes();
    //}
}
