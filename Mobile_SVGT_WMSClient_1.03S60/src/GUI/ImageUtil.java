package GUI;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Image Zoom Function
 * @param srcImage Originl Image Object
 * @param newW Image width after zoomed
 * @param newH Image height after zoomed
 * @return Image object after zoomed
 */
public class ImageUtil {

	  
	       public final Image scale (Image srcImage, int newW, int newH) {
	           int srcW = srcImage.getWidth();
	           int srcH = srcImage.getHeight();
            int srcWnewW=2;
	           int srcHnewH=2;
	           //horizontal direction zooming
	           Image tmp = Image.createImage(newW, srcH);
	           Graphics g = tmp.getGraphics();
	           
	       
	           for (int x = 0; x < newW; x++) {
	               g.setClip(x, 0, 1, srcH);
				//zoom in ratio
	               g.drawImage(srcImage,x-x*srcWnewW,0,Graphics.LEFT | Graphics.TOP);
	               
	           }
	           
	           //vertical direction zooming
	           Image dst = Image.createImage(newW, newH);
	           g = dst.getGraphics();
	           
	           
	           for (int y = 0; y < newH; y++) {
	               g.setClip(0, y, newW, 1);    
				//zoom in ratio
	               g.drawImage(tmp,0,y-y*srcHnewH,Graphics.LEFT | Graphics.TOP);
	           
	           }
	           
	           return dst;        
	       }
	  
	  
	   }
