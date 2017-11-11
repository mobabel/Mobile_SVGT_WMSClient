package GUI;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.*;

import GUI.MODEL.SETTING;


import GUI.CONTROL.GUIController;


/*--------------------------------------------------
* Class ImageCanvas
*
* Draw mutable image
*-------------------------------------------------*/
public class ImageCanvas extends Canvas implements Runnable{

    private Displayable parent;
	private GUIController controller;
  
    public Image originalPNG = null;
    private Image TempPNG = null;
    private Image tmp = null;
    private String message = "Raster Image";
    
	/** The events dispatching thread */
	Thread thread;
	/* The current display */
    private Display display;
	/* The current loading status */
	boolean loaded = false;
	/* The clock image */
	private Image wait=null;
	
	/* The UI modes */

	public final static int MODE_PAN = 0; // Pan mode=== Default mode

	public final static int MODE_ZOOM = 1; // Zoom mode

	public final static int MODE_ORIGVIEW = 2; // MAX
	
	private static int zoomcheck   = 0;
	
    /**ratio = srcW/newW or srcH/newH*/
    private static double ratio=0;

	/* The mode index */
	int index;

	/* The current mode */
	int mode = MODE_PAN;
	
	int x, y, width, height;
	static final int MENU_HEIGHT = 18;

	
  public ImageCanvas(Display display){
	  this.display = display;
	  width = getWidth();
      height = getHeight();
	  
      //Thread t=new Thread(this);
      //t.start();
    try
    {
      //tmp = Image.createImage(240,320);	
      // Get graphics object to draw onto the image        
      //Graphics g = tmp.getGraphics();
      // Specify a font face, style and size
      //Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
      //g.setFont(font);

      // Draw a filled (black) rectangle
      //g.setColor(0, 0, 0);
      //g.fillRoundRect(0,0, originalPNG.getWidth()-1, originalPNG.getHeight()-1, 20, 20); 
      // Center text horizontally in the image. Draw text in white
      //g.setColor(255, 255, 255);           
      //g.drawString(message,(tmp.getWidth() / 2) - (font.stringWidth(message) / 2), 0, Graphics.TOP | Graphics.LEFT);
    }
    catch (Exception e)
    {
      System.err.println("Error during image creation: "+e.getMessage());
    }    
  } 
  
	/** Starts the events dispatching thread */
	public synchronized void start() {
		thread = new Thread(this);
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}
	  
	/**
	 * Inits this canvas. Loads icons
	 */
	public void init() {
		try {
			wait = Image.createImage("/tinyline/wait.png");
			loaded = false;	
		} catch (Exception e) {
			alertError("Resources (helvetica.svg and/or icons) could not be loaded!");
		}
	}
  
	/**
	 * Draws the canvas
	 * 
	 * @param g
	 *            The Graphics surface.
	 */
	public void paint(Graphics g) {
		// 
		if (loaded==true) {
			  clear(g);
			    // Center the image on the display
			    if (TempPNG == null){
			    	TempPNG=originalPNG;
				    g.drawImage(TempPNG, PAN_XO+SETTING.Srceen_WIDTH/2, PAN_YO+SETTING.Srceen_HEIGHT/2- MENU_HEIGHT, g.HCENTER | g.VCENTER);
				    System.out.println("TempPNG width is: "+TempPNG.getWidth());
			    }
		        else if (TempPNG != null){
			    g.drawImage(TempPNG, PAN_XO+SETTING.Srceen_WIDTH/2, PAN_YO+SETTING.Srceen_HEIGHT/2- MENU_HEIGHT, g.HCENTER | g.VCENTER);
			    System.out.println("TempPNG width is: "+TempPNG.getWidth());
	            }
		} 
		else // if(loaded=false)
		{
			// draw the clock
			System.out.println("Load Icon");
			clear(g);
			g.setColor(0x000000);
			g.drawString("Wait ...", width / 2, height / 2, Graphics.LEFT| Graphics.TOP);
			if (wait != null) {
				g.drawImage(wait, 0, height - MENU_HEIGHT, g.TOP | g.LEFT);
			}
		}
	}
  public void clear(Graphics g){
	  //redraw the backgroud color
	  g.setColor(0xffffff);//255,255,255
	  g.fillRect(0,0,width,height);
  }

  
	/**
	 * The events dispatching thread run()
	 */
	public void run() {
		Thread currentThread = Thread.currentThread();
		try {
			while (currentThread == thread) {
				//eventQueue.handleEvent(eventQueue.getNextEvent());
				try {
					Thread.currentThread().sleep(50);
					//repaint();
				} catch (InterruptedException e) {
				}
				Thread.currentThread().yield();
			}
		} catch (Throwable thr) {
			thr.printStackTrace();
			alertError("Internal Error");
		}
	}
	
	/**
	 * Fetch the image. If the name begins with "http:" fetch it with
	 * connector.open and http. If it starts with "/" then load it from the
	 * resource file.
	 * 
	 * @param name
	 *            of the image to load
	 * @return image created
	 * @exception IOException
	 *                if errors occuring doing loading
	 */
	public Image createImage(String name) throws IOException {
		System.out.println("boolean loaded is: "+loaded);
		if (name.startsWith("/")) {

			// Load as a resource with Image.createImage
			if (name.endsWith(".png")) {
				return Image.createImage(name);
			} else
				throw new IOException("Expecting PNG image in resource");

		} 
		else if (name.startsWith("http:")) {
			// Load from a ContentConnection
			HttpConnection hc = null;
			DataInputStream dis = null;
			InputStream is = null;
			byte[] data=null;
			byte[] contents = null;
			int length;
			int _length;
			//InputStreamReader isr = null ; //Doesnot add if not needed
			try {
				hc = (HttpConnection) Connector.open(name);
				//isr = new InputStreamReader(hc.openInputStream()) ;//Doesnot add if not needed
				
				
				System.out.println("image source is: "+name);
				int status = hc.getResponseCode();
				if (status != 200) {
					throw new IOException("HTTP Response Code = " + status);
				}
				//If it is png format file in the http,go to read, otherwise throw expection
				String type = hc.getType();
			    if (!type.equals("image/png")) {
				    alertError("Image format is not PNG but: "+type);
				    throw new IOException("Expecting image, received " + type);
			        }
			    //If it is png format file in the http, then read it
				if (name.endsWith(".png")) {
					System.out.println("Now read png file from net");
				    length = (int) hc.getLength();
				    System.out.println("length of connector is: "+length);
				   
				    
				    if (length > 0) {
					    dis = hc.openDataInputStream();
					    data = new byte[length];
					    dis.readFully(data);
					    loaded = true;
					    System.out.println("Create the image");
					    return Image.createImage(data, 0, data.length);
				         } 

				    else {
					      alertError("Content length is missing, is: "+length);
					      throw new IOException("Content length is missing");
				         }
			        } 
				//If it is png format stream in the http, then read it
				else{
//					dis = new DataInputStream(hc.openInputStream());
//			        dis.available();
//			        _length = (int) hc.getLength();
//			        
//				    if (_length < 1) {
//				          _length = dis.available();
//				          System.out.println(_length);
//				          }
//			          contents = new byte[_length];
//				        dis.readFully(contents);
//				        loaded = true;
//				        System.out.println(contents.length);
//				        return Image.createImage(contents, 0, contents.length - 1);

					System.out.println("Now read stream png file from net");
					is = hc.openInputStream();
//					ByteArrayOutputStream baos = new ByteArrayOutputStream();
//	                int ch = 0;
//	                while ((ch = is.read()) != -1)
//	                {
//	                    baos.write(ch);
//	                }
//	                byte[] imageData = baos.toByteArray();
//	                baos.close();
//	                loaded = true;
//	                return Image.createImage(imageData, 0, imageData.length);				        
				        


					byte[] imageData = null;
					imageData=getByte(name);
					loaded = true;
					return Image.createImage(imageData, 0, imageData.length);
					
//					ByteArrayInputStream bais = new ByteArrayInputStream(data);
//					dis = new DataInputStream(bais);
//					System.out.println("Step===============2");
//					byte picNum = dis.readByte();
//					Image[] surface = new Image[picNum];
//					System.out.println("Step===============3 picNum is: "+picNum);
//					try
//					{
//					    for (int i = 0; i < picNum; i++)
//					    {
//					        short picLen = dis.readShort();
//					        byte[] picData = new byte[picLen];
//					        dis.read(picData, 0, picLen);
//					        System.out.println("Step===============4 picData.length:"+picData.length);
//					        surface[i] = Image.createImage(picData, 0,picData.length);
//					        System.out.println("Step===============5");
//					    }
//					    byte[] picData = null;
//					    //return Image.createImage(surface);
//					    
//					}
//					catch(IOException ex)
//					{}			
//					System.out.println("Step===============6");
//					loaded = true;
//					return surface[picNum];
			        }
				       
			} finally {
				if (dis != null)
					dis.close();
				if (hc != null)
					hc.close();
				if (is !=null)
					is.close();
			}
		} 
		else {
			throw new IOException("Unsupported media");
		}
	}

	 /**
	   * turn the resource to byte array
	   * @param file String file name
	   */
	  private byte[] getByte(String streamfile)
	  {			
		InputStream input=null;
	    byte[] myData = null;
		HttpConnection hc = null;
	    try
	    {
			hc = (HttpConnection) Connector.open(streamfile);
			input = hc.openInputStream();
		    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
   			System.out.println("Step===============1");
	      int ch = 0;	 
	      while ((ch = input.read()) != -1)
	      {
	        byteArray.write(ch);
	      }
	      for (int i = 0; i < byteArray.size(); i++)
	      {
	        myData = byteArray.toByteArray();
	      }
	      for (int i = 0; i < myData.length; i++)
	      {
	        System.out.println(myData[i] + ",");
	      }
	    }
	    catch (Exception e)
	    {}
	    return myData;
	  }
	  

	  
	  /**
	   * Image Zoom Function
	   * @param srcImage Originl Image Object
	   * @param newW Image width after zoomed
	   * @param newH Image height after zoomed
	   * @return Image object after zoomed
	   */
      public Image scaleImageZoom (Image srcImage, int newW, int newH) {
          int srcW = srcImage.getWidth();
          int srcH = srcImage.getHeight();
          if (zoomcheck==1){
              ratio=0.5;}
          else if (zoomcheck==-1){
              ratio=2;}
          else if (zoomcheck==0){
              ratio=1;}

          //horizontal direction zooming
          Image tmp = Image.createImage(newW, srcH);
          Graphics g = tmp.getGraphics();
          
      
          for (int x = 0; x < newW; x++) {
              g.setClip(x, 0, 1, srcH);
			//zoom in ratio
              g.drawImage(srcImage,(int) (x-x*ratio),0,Graphics.LEFT | Graphics.TOP);
              
          }
          
          //vertical direction zooming
          Image resultimage = Image.createImage(newW, newH);
          g = resultimage.getGraphics();
          
          
          for (int y = 0; y < newH; y++) {
              g.setClip(0, y, newW, 1);    
			//zoom in ratio
              g.drawImage(tmp,0,(int) (y-y*ratio),Graphics.LEFT | Graphics.TOP);
          
          }
          System.out.println("result image width is: " +resultimage.getWidth());
          return TempPNG=resultimage;        
      }
      
      
		/**
		 * Returns the current Image document to its original view and Position.
		 */
		public Image origView(Image orgviewImage) {
			  PAN_XO=0;
			  PAN_YO=0;
	          //Graphics g = orgviewImage.getGraphics();
	          //g.drawImage(originalPNG, PAN_XO+SETTING.Srceen_WIDTH/2, PAN_YO+SETTING.Srceen_HEIGHT/2- MENU_HEIGHT, g.HCENTER | g.VCENTER);
	          return TempPNG=orgviewImage;
	          //scaleImageZoom(originalPNG,SETTING.Srceen_WIDTH,SETTING.Srceen_HEIGHT); 

		}
	// ////////////////////////////////////////////////////////////////
		/**
		 * Selects mode.
		 * 
		 * @param newmode
		 *            The new mode id.
		 */
		public void selectMode(int newmode) {
			mode = newmode;
		}
		

	  int PAN_XO=0;
	  int PAN_YO=0;
	  int PAN_STEP=2;
		/**
		 * Handle keyboard input.
		 * 
		 * @param keyCode
		 *            pressed key.
		 */
		protected void keyRepeated(int keyCode) {
			keyPressed(keyCode);
		}

		/**
		 * Handle keyboard input.
		 * 
		 * @param keyCode
		 *            pressed key.
		 */
		protected void keyPressed(int keyCode) {
			if (!loaded)
				return;
			int action = getGameAction(keyCode);
			//SVGEvent event = null;
			switch (action) {
			case Canvas.LEFT:
                    if (mode == MODE_PAN) {
					//event = new SVGEvent(SVGEvent.EVENT_SCROLL, new TinyPoint(-PAN_STEP, 0));
                    	PAN_XO=PAN_XO-PAN_STEP;
                    	System.out.println("Left is pressed");
				} 
                    System.out.println("Left is pressed");
				break;

			case Canvas.RIGHT:
				 if (mode == MODE_PAN) {
					//event = new SVGEvent(SVGEvent.EVENT_SCROLL, new TinyPoint(PAN_STEP, 0));
					 PAN_XO=PAN_XO+PAN_STEP;
					 System.out.println("Right is pressed");
				} 
				 System.out.println("Right is pressed");
				break;

			case Canvas.UP:
				 if (mode == MODE_PAN) {
					//event = new SVGEvent(SVGEvent.EVENT_SCROLL, new TinyPoint(0,-PAN_STEP));
					 PAN_YO=PAN_YO-PAN_STEP;
					 System.out.println("Up is pressed");
				} else if (mode == MODE_ZOOM) {
					//event = new SVGEvent(SVGEvent.EVENT_ZOOM, new TinyNumber(0));
//					zoomcheck=0;
					if (zoomcheck==1){
						return;}
					if (zoomcheck<1)
					zoomcheck++;
					this.setZoomcheck(zoomcheck);
					if (zoomcheck>1){
						return;}
					else if (zoomcheck==1){
						scaleImageZoom(originalPNG,480,640);}
					else if (zoomcheck==0){
						scaleImageZoom(originalPNG,240,320);}
					System.out.println("zoomcheck: "+zoomcheck);
				}
				 System.out.println("Up is pressed");
				break;
			case Canvas.DOWN:
				 if (mode == MODE_PAN) {
					//event = new SVGEvent(SVGEvent.EVENT_SCROLL, new TinyPoint(0,PAN_STEP));
					 PAN_YO=PAN_YO+PAN_STEP;
					 System.out.println("Down is pressed");
				} else if (mode == MODE_ZOOM) {
					//event = new SVGEvent(SVGEvent.EVENT_ZOOM, new TinyNumber(1));
//					zoomcheck=0;
					if (zoomcheck==-1){
						return;}
					if (zoomcheck>-1)
					zoomcheck--;
					this.setZoomcheck(zoomcheck);
					if (zoomcheck<-1){
						return;}
					else if (zoomcheck==0){
					scaleImageZoom(originalPNG,240,320);}
					else if (zoomcheck==-1){
					scaleImageZoom(originalPNG,240,320);
							}
					System.out.println("zoomcheck: "+zoomcheck);
					}
				 System.out.println("Down is pressed");
				break;
			case Canvas.FIRE:
				if (mode == MODE_ORIGVIEW) {
					//event = new SVGEvent(SVGEvent.EVENT_FOCUSPRESSED, null);
					 System.out.println("Fire is pressed");
				}
				System.out.println("Fire is pressed");
				break;
			} // end of switch
			repaint();
		}
	  
	    /** Return the zoomcheck to check the zoom status */
	    public int getZoomcheck(){
	        return zoomcheck;
	    }
	    
	    public void setZoomcheck(int zoomcheck){
	        this.zoomcheck=zoomcheck;
	    }
	    
	void alertError(String message) {
		Alert alert = new Alert("Error", message, null, AlertType.ERROR);
		Displayable current = display.getCurrent();
		if (!(current instanceof Alert)) {
			alert.setTimeout(Alert.FOREVER);
			// This next call can't be done when current is an Alert
			display.setCurrent(alert, current);
		}
	}
	

  
} 

