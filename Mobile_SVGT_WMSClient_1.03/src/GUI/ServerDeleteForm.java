package GUI;

import javax.microedition.lcdui.*;

import GUI.CONTROL.GUIController;
import GUI.MODEL.ServerListItem;

public class ServerDeleteForm extends Form implements CommandListener
{
        private GUIController controller;
        private Command deleteCommand=new Command("Delete",Command.OK,1);
        private Command cancelCommand=new Command("Cancel",Command.CANCEL,1);
        private Command sureCommand=new Command("Sure",Command.OK,1);
        private Command cancelACommand=new Command("Cancel",Command.CANCEL,1);
		//Alert to notice!!!!!!
        Alert a = new Alert("Alert", "        Are you sure to delete the server?",null, AlertType.INFO);
        private Display display;
        private Displayable parent;

        private StringItem servernameTextField;
        private StringItem serverurlTextField;
        private ServerListItem item;
        
        public ServerDeleteForm(String title,Display d,Displayable dp,GUIController control)
        {
        	    super(title);
                display = d;
                parent=dp;

                addCommand(deleteCommand);
                addCommand(cancelCommand);
                setCommandListener(this);
                controller=control;
                
                //sever URL
                //we do not need to edit them,so they are StringItem
                servernameTextField=new StringItem("Server Name: ","");
                serverurlTextField=new StringItem("Server URL: ","");
            	append(servernameTextField);
            	append(serverurlTextField);;
 

        }

public void setServer(ServerListItem item){
	 this.item=item;
	servernameTextField.setText(item.getServerName());
	serverurlTextField.setText(item.getServerURL());
    	}


        
 public void commandAction(Command c, Displayable d) {
        	if(c==deleteCommand) {

                //a.setTimeout(Alert.FOREVER);

                a.addCommand(sureCommand);
                a.addCommand(cancelACommand);
                a.setCommandListener(this);
        		controller.setCurrent(a);
        	}
        	else if(d==a && c==sureCommand){
    			Object[] args={item};
				controller.handleEvent(GUIController.EventID.EVENT_DELETE,args);
        		/////////////////////////////////////////////////////////        		
                //create an alert
                ///Alert deleteInfo= new Alert("Save RMSServerUrl","",null,AlertType.INFO);
                ///deleteInfo.setTimeout(Alert.FOREVER);
                ///if(rmsDB.save(app)) {
                	///deleteInfo.setString("Success!");
                ///}
                ///else {
                	////deleteInfo.setString("Fail!");
                ///} 
    	            //display.setCurrent(parent);
                //////////////////////////////////////////////////
        	}
        	else if(d==a && c==cancelACommand){

        		d=parent;
        		controller.setCurrent(parent);
        	}
        	else if(c==cancelCommand) {
        		controller.handleEvent(GUIController.EventID.EVENT_DELETE_BACK,null);
                    //display.setCurrent(parent);
        	}
            }
        	

        }

