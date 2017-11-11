package GUI;

import java.util.Date;

import javax.microedition.lcdui.*;
//import Scheduler.Appointment;
import GUI.CONTROL.GUIController;
import GUI.MODEL.ServerListItem;

public class ServerEditForm extends Form implements CommandListener{
    private GUIController controller;
    private Command saveCommand = new Command("Save", Command.OK, 1);
    private Command cancelCommand = new Command("Cancel", Command.CANCEL, 1);
    private Display display;
    private Displayable parent;    

    private TextField servernameTextField;
    private TextField serverurlTextField;
    private ServerListItem item;

    
    public ServerEditForm(String title,Display d, Displayable p,GUIController control) {
    	super(title);
        display=d;
        parent=p;
    	addCommand(saveCommand);
    	addCommand(cancelCommand);
	    setCommandListener(this);
	    controller=control;
		
    //sever URL
    servernameTextField=new TextField("Server Name: ","",20,TextField.ANY);
    serverurlTextField=new TextField("Server URL: ","",100,TextField.URL);
	append(servernameTextField);
	append(serverurlTextField);
						

    }
    
	public void setServer(ServerListItem item){
		 this.item=item;
	    servernameTextField.setString(item.getServerName());
	    serverurlTextField.setString(item.getServerURL());
	}
	

    
    public void commandAction(Command c, Displayable d) {
    	if(c==saveCommand) {
    		
            String servername=servernameTextField.getString();
            String serverurl=serverurlTextField.getString();
            if((servername.length()==0)||(serverurl.length()==0)){
                Alert a = new Alert("Error", "Server name or address must be added.",null, AlertType.ERROR);
                a.setTimeout(Alert.FOREVER);
                controller.setCurrent(a,this);
                return;
            }
           
            item.setServerName(servername);
            item.setServerURL(serverurl);
			Object[] args={item};
			controller.handleEvent(GUIController.EventID.EVENT_EDIT_SAVE,args);   
    		
 ///////////////////////////////////////////////////////////////////////   		
    		    ///TextField tfurl;
    		    ///tfurl=(TextField) get(0);
               ///app.setServerUrl(tfurl.getString());
                //server url must be set
                ///if(((TextField) get(0)).getString().length()==0 ) {
                    ///Alert a = new Alert("Error", "Server address must be added.",null, AlertType.ERROR);
                    ///a.setTimeout(Alert.FOREVER);
                    ///display.setCurrent(a,this);
                   /// return;
                ///}
                //create an alert
               /// Alert editInfo= new Alert("Save Editting","",null,AlertType.INFO);
                ///editInfo.setTimeout(Alert.FOREVER);
                ///if(rmsDB.save(app)) {
                    ///editInfo.setString("Success!");
                ////}
                //else {
                    ///editInfo.setString("Fail!");
                ///} 
    	            //display.setCurrent(parent);
    	            //////////////////////////////////////////////////
    	}
    	else if(c==cancelCommand) {
    		controller.handleEvent(GUIController.EventID.EVENT_EDIT_BACK,null);
                //display.setCurrent(parent);
    	}
        }
	

}

