package GUI;

import javax.microedition.midlet.*;
import javax.microedition.io.Connector;
import javax.microedition.lcdui.*;

import GUI.MODEL.SETTING;
import GUI.CONTROL.GUIController;


public class Common_ExceptionForm extends Form implements CommandListener{
    private GUIController controller;
    private Display display;
    private Displayable parent;

    
    private Command backCommand = new Command("Back", Command.BACK, 1);
    
    public Common_ExceptionForm(String title,Display d, Displayable p,GUIController control) {
        super(title);
        display=d;
        parent=p;
        addCommand(backCommand);
        setCommandListener(this);
        controller=control;
        
    }
    
    public void commandAction(Command c, Displayable p) {
         if(c==backCommand) {
 	 	    p=parent;
            controller.setCurrent(p);
			System.out.println("CACEL ") ;
	}
    }
}
