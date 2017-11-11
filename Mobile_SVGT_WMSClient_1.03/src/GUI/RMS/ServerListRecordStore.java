/*
 * Created on 2005-2-3
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package GUI.RMS;

import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStoreException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;



import GUI.CONTROL.GUIController;
import GUI.MODEL.ServerListItem;
import GUI.MODEL.SETTING;;

/**
 * Class Description:
 * Set some public setting of classes,
 * which were used oftenly
 */
public class ServerListRecordStore {
    private static ServerListRecordStore 	 m_slrs;
    private MIDlet          	 m_midlet;
	private static RecordStore serverlist_RS;
	private  GUIController controller;
	private boolean  valuesChanged = false;
	  
		public ServerListRecordStore(GUIController control,MIDlet midlet)throws IOException, RecordStoreException{
			System.out.println("Set another Constructor");
			controller 	= control;
			m_midlet 	= midlet;
	        openRS();
		}
	  
	    /**
	     * Singleton pattern is used to return 
	     * only one instance of record store
	     */
	    public static synchronized ServerListRecordStore getInstance( GUIController control,MIDlet midlet )throws IOException, RecordStoreException {
	        if( m_slrs == null ) {
				System.out.println("!-- Settings::getInstance --!");
				m_slrs = new ServerListRecordStore( control, midlet );
	        }
	        return m_slrs;
	    }
	  
	/**Constructor*/ 
	private ServerListRecordStore(MIDlet midlet)throws IOException, RecordStoreException{
		//controller=control;
		System.out.println("Set default Constructor");
        m_midlet = midlet;
		openRS();
	}
	/**Open RecordStore, if not then create new*/
    private synchronized void openRS() throws IOException, RecordStoreException {
        System.out.println("Begin to load RecordStore");
        
        valuesChanged = false;                 
        try {
            serverlist_RS = RecordStore.openRecordStore(SETTING.RECORDSTORE_NAME, true);
            if( serverlist_RS.getNumRecords() == 0 ) {
				System.out.println("No record");

				//If these are no record, load the default record of server
				DefaultRS();
            } else {
				System.out.println("Find record");
            }
        }catch (Exception e) {            
        }
    }
    //Close RecordStore
    public synchronized void closeRS() {
        if (serverlist_RS!= null) {
            try {
                serverlist_RS.closeRecordStore();
                serverlist_RS=null;
            } catch (RecordStoreException ex) {}
        }
    }
    
    /*
     * At the beginning of loading,
     * if there are no record in the recordstore,we can add some
     * default server list to it.
     */
	private void DefaultRS()throws IOException, RecordStoreException{
		System.out.println("load the default server list");  
		byte[] dataInitRssFeed = 
			String.valueOf(SETTING.getChars(SETTING.TXT_T_SERVERLIST1)).getBytes();
		serverlist_RS.addRecord( dataInitRssFeed, 0, dataInitRssFeed.length );
		
		dataInitRssFeed = String.valueOf(SETTING.getChars(SETTING.TXT_T_SERVERLIST2)).getBytes();
		serverlist_RS.addRecord( dataInitRssFeed, 0, dataInitRssFeed.length );
		
		dataInitRssFeed = String.valueOf(SETTING.getChars(SETTING.TXT_T_SERVERLIST3)).getBytes();
		serverlist_RS.addRecord( dataInitRssFeed, 0, dataInitRssFeed.length );
		
		dataInitRssFeed = String.valueOf(SETTING.getChars(SETTING.TXT_T_SERVERLIST4)).getBytes();
		serverlist_RS.addRecord( dataInitRssFeed, 0, dataInitRssFeed.length );
		
		dataInitRssFeed = String.valueOf(SETTING.getChars(SETTING.TXT_T_SERVERLIST5)).getBytes();
		serverlist_RS.addRecord( dataInitRssFeed, 0, dataInitRssFeed.length );
		dataInitRssFeed = null;
	}
    
    /**Add Record*/
    public int addRS(ServerListItem item) {
                   
                try {
                    byte[] data=item.getStoreString().getBytes();
                    int id=serverlist_RS.getNextRecordID();
                    System.out.println("Now add record: " + id); 
                	int rec =serverlist_RS.addRecord(data,0,data.length);
                	return id;
                } catch (RecordStoreException ex) { 
                	 System.out.println("Error: "+ex.getMessage());
                }
        
        return -1; //why return -1??
    }
    
    public boolean addRSb(ServerListItem item) {
        if(serverlist_RS==null) return false;

        boolean success=false;
        try {byte[] data=item.getStoreString().getBytes();
        int id=serverlist_RS.getNextRecordID();
        System.out.println("Now add record: " + id); 
    	int rec =serverlist_RS.addRecord(data,0,data.length);
    	//return id;
        success=true;
        }catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }
        return success;

    }
    
    /**update the Record*/
    public void updateRS(ServerListItem item) {

            try {
    			int id = item.getId();
    			System.out.println("Now edit record: " + id);  
                byte[] data=item.getStoreString().getBytes();
            	serverlist_RS.setRecord(id,data,0,data.length);
            } catch (RecordStoreException ex) { }
    }
    
    public boolean updateRSb(ServerListItem item) {
        boolean success=false;
        
        try {
			int id = item.getId();
			System.out.println("Now edit record: " + id);  
            byte[] data=item.getStoreString().getBytes();
        	serverlist_RS.setRecord(id,data,0,data.length);
            success=true;
        }catch(Exception e) {}
        
        return success;
    }
    
    /**visit one Record*/
    public ServerListItem getRS(int id) {
        ServerListItem item=null;

            try {
                System.out.println("Now visit record:" + id); 
            	item=new ServerListItem(id,serverlist_RS.getRecord(id));
            } catch (RecordStoreException ex) { }

         return item;
    }
    /**delete one Record*/
    public void deleteRS(int id) {
            try {
                System.out.println("Now delete record: " + id); 
            	serverlist_RS.deleteRecord(id);
            } catch (RecordStoreException ex) {}
    }
    
    public boolean deleteRSb(int id) {
        boolean success=false;

        try {
            System.out.println("Now delete record: " + id); 
        	serverlist_RS.deleteRecord(id);
            success=true;
        }catch(Exception e) {}
        
        return success;
    }
    
    /**visit all Record and return one vector of all record*/
    public Vector getRSs() {
    	
        Vector items=new Vector(SETTING.MAX_SERVERLIST);
        //this.openRS();
        RecordEnumeration renum=null;
        int ind=0;
        try{
            ServerListItem item=null;
        	renum=serverlist_RS.enumerateRecords(null,new ServerlistRSComparator(),false); 
			if(renum != null)
			{
            while(renum.hasPreviousElement()){
				// previousRecordId also advances the record pointer, so we have no
	            // need to call previousRecord() as well.
            	ind=renum.previousRecordId();
            	
	            //Retrieve the data from the next record
	            //and make it the current record
	            //Set up a byte array buffer and some helper streams
	            byte[] rec = new byte[serverlist_RS.getRecordSize(ind)];
	            rec = serverlist_RS.getRecord(ind);
				System.out.println("Now read record: " + ind);
        	    ///item=new ServerListItem(ind,serverlist_RS.getRecord(ind));
            	///items.addElement(item);
				if(rec != null)
				{
					try {
						item = new ServerListItem(ind, rec);
					}
		            catch (Exception e) {
		                System.out.println("Exception when addd record:" + e.toString());
		            }
		            finally {
		               //Add the server item to the Vector
						items.addElement(item);
						System.out.println("Now read record: " + item.getServerName() + "|" + item.getServerURL());
						rec = null;
						item = null;
		            }
				}
            }
			System.out.println("Record reading finish");
		}
		else
		{
			System.out.println("renums is null");
            }
            
        }catch(Exception ex){ex.printStackTrace();}
        finally{
        		try{
        			renum.destroy();
        		}catch(Exception e){}
        }//end finally
    
        return items;
    }
    //old simple Comparator
//    private class InnerComparator implements RecordComparator{
//        public int compare(byte[] rec1, byte[] rec2){
//            if(rec1.length>rec2.length)
//                return FOLLOWS;
//            else if(rec1.length<rec2.length)
//                return PRECEDES;
//            else 
//                return EQUIVALENT;
//        }
//
//    }
    

}
