package GUI;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

public class Wait4ParseAnimation extends GameCanvas implements Runnable {
	    private boolean mTrucking;
	    private int mTheta;
	    private int mBorder;
	    private int mDelay;
	    private int mWidth;
	    private int mHeight;
	    public Wait4ParseAnimation() {
	        super(true);
	        mTheta = 0;
	        mBorder = 50;//
	        mDelay = 10;// rotate speed
	    }
	    public void start() {
	        mTrucking = true;
	        Thread t = new Thread(this);
	        t.start();
	    }
	    public void stop() {
	        mTrucking = false;
	    }
	    public void render(Graphics g) {
	        mWidth = getWidth(); 
	        mHeight = getHeight();
	        // Clear the Canvas.
	        //Set the backgroud color
	        g.setGrayScale(255);
            //g.setColor(8,65,99);//azury
	        g.fillRect(0, 0, mWidth - 1, mHeight - 1);
	        int x = mBorder;
	        int y = mBorder;
	        int w = mWidth - mBorder * 2;
	        int h = mHeight - mBorder * 2;
	        for (int i = 0; i < 8; i++) {
	            //g.setGrayScale((8 - i) * 32 - 16);
	            g.setColor(((8 - i) * 32 - 24),((8 - i) * 32 - 2),((8 - i) * 32 - 16));
	            //Set the edge color
	            g.fillArc(x, y, w, h, mTheta + i * 10, 10);
	            //Set the fill color
	            g.fillArc(x, y, w, h, (mTheta + 180) % 360 + i * 10, 10);
	        }
	    }
	    public void run() {
	        Graphics g = getGraphics();
	        while (mTrucking) {
	            mTheta = (mTheta + 1) % 360;
	            render(g);
	            flushGraphics();
	            try { Thread.sleep(mDelay); } catch (InterruptedException ie) {}
	        }
	    }
}