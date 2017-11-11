package GUI;

import javax.microedition.lcdui.*;

import GUI.CONTROL.GUIController;


public class ServerAddForm extends Form implements CommandListener
{
        private GUIController controller;
        private Command saveCommand=new Command("Save",Command.OK,1);
        private Command cancelCommand=new Command("Cancel",Command.CANCEL,1);
        private Display display;
        private Displayable parent;

        private TextField servernameTextField;
        private TextField serverurlTextField;
        
        public ServerAddForm(String title,Display d,Displayable dp,GUIController control)
        {
        	    super(title);
                display = d;
                parent=dp;

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
 
public void clear(){
	servernameTextField.setString("");
	serverurlTextField.setString("http://");
    	}


 
        public void commandAction(Command c, Displayable d) {
        	if(c==saveCommand) {
	            String servername=servernameTextField.getString();
	            String serverurl=serverurlTextField.getString();
                //url and user name must be set
	            if((servername.length()==0)||(serverurl.length()==0)){
                // url and user name must be set	
                    Alert a = new Alert("Error", "Server name or address can not be empty.",null, AlertType.ERROR);
                    a.setTimeout(Alert.FOREVER);
                    controller.setCurrent(a,this);
                    //System.out.println("Empty!!!");
	            	return;
	            }
               
				Object[] args={servername,serverurl};
				controller.handleEvent(GUIController.EventID.EVENT_NEW_SAVE,args); 
			
				///////////////////////////////////////////////
        		    ///TextField tfurl;
        		    ///tfurl=(TextField) get(0);
                    //app.setLength(Integer.parseInt(tf.getString()));
                   
				
                    //create an alert
                    ///Alert saveInfo= new Alert("Save RMSServerUrl","",null,AlertType.INFO);
                    ///saveInfo.setTimeout(Alert.FOREVER);
                    ///if(rmsDB.save(app)) {
                        ///saveInfo.setString("Success!");
                    ///}
                    ///else {
                        ///saveInfo.setString("Fail!");
                    ///} 
        	            ///display.setCurrent(parent);
				////////////////////////////////////////////////
        	}
        	else if(c==cancelCommand) {
                    ///display.setCurrent(parent);
    				controller.handleEvent(GUIController.EventID.EVENT_NEW_BACK,null);
        	}
            }
        	

        }

