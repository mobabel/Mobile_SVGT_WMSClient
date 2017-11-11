package GUI.MODEL;

public class SETTING {
	
	/** Set the initial time (ms)*/
	public static final int Initial_Time = 2000;
	
	/** Set the parsing waiting time (ms)
	 * Set a long time to wait for the parsing*/
	public static final int GethttpheadWait_Time = 10000;
	
	/** Set the parsing waiting time (ms)
	 * Set a long time to wait for the parsing*/
	public static final int ParseWait_Time = 100000;
	
	/** Set the Parsing Wait Thread Sleep_Time (ms)
	 * The time Gauge will run to end
	 * */
	public static final int ParseWaitThreadSleep_Time = 700;
	
	//////////////////////ServerListRecordStore SETTING//////////////////////
	/** RecordStore name*/
	public static final String RECORDSTORE_NAME="SERVERLIST_DB";
	
	/** The max quantity of the server list*/
	public static final int MAX_SERVERLIST = 15;
	
	/** Text keys of de default serverlist*/
	private static int TXT_ID = 0;
	/**  text / page titles*/
    public static final int TXT_T_SERVERLIST1            = TXT_ID++;
	public static final int TXT_T_SERVERLIST2            = TXT_ID++;
	public static final int TXT_T_SERVERLIST3            = TXT_ID++;
	public static final int TXT_T_SERVERLIST4            = TXT_ID++;
	public static final int TXT_T_SERVERLIST5            = TXT_ID++;
	
	/**The default server list and url*/
	protected static final char[][] TEXT_BUF = {
		  "Localhost|http://localhost/phpmywms/getmapcap.php?VERSION=1.1.1&SERVICE=WMS&REQUEST=GetCapabilities".toCharArray(),
		  "www.gis-news.de|http://www.gis-news.de/wms/getmapcap.php?VERSION=1.1.1&REQUEST=GetCapabilities&SERVICE=WMS".toCharArray(),
		  "test|http://localhost/test/GetCapabilities.xml".toCharArray(),
		  "USGS WMS|http://gisdata.usgs.net/servlet/com.esri.wms.Esrimap?REQUEST=GetCapabilities&SERVICE=wms".toCharArray(),
		  "NASA|http://onearth.jpl.nasa.gov/wms.cgi?REQUEST=GetCapabilities".toCharArray(),
	  };
	
	/**
	   * Returns specified text as character array.
	   * @param id  The id of the text.
	   * @return    A text as char array.
	   */
	  public static char[] getChars(int id)
	  {
	    return TEXT_BUF[id];
	  }
	///////////////////////////////////////////////////////////////////
	
	//////////////////////ALL Forms's SETTING//////////////////////
	/** Form name */
		public static String FORM_TITLE_SERVERSELECTFORM = "Server List";
		public static String FORM_TITLE_SERVERADDFORM = "Add a New Server";
		public static String FORM_TITLE_SERVEREDITFORM = "Edit the Server";
		public static String FORM_TITLE_SERVERDELETEFORM = "Delete the selected Server";
		public static String FORM_TITLE_PLACESEARCHFORM = "Input the X and Y coordinate please";
		public static String FORM_TITLE_ABOUTALERT = "Mobile SVGT WMS Client 1.0";  
	    public static String FORM_TITLE_LAYERSELECTFORM = "Select layers please";
	    public static String FORM_TITLE_EXCEPTIONFORM = "Error occurs now";
	    public static String FORM_TITLE_FORMATPOSITIONFORM = "Select images's format please";
	    public static String FORM_TITLE_MOBILESVGFORM = "Mobile SVGT Client";
	    public static String FORM_TITLE_MOBILEIMAGEFORM = "Mobile WMS Client";
    /////////////////////////////////////////////////////////////////// 
	  
	//////////////////////PlaceSearchForm SETTING//////////////////////
		/** The screen width and height for WTK*/
	    //WIDTH=240&HEIGHT=320
	    public static final  int     Srceen_HEIGHT=320;
	    public static final  int     Srceen_WIDTH=240;
 
    /** The map scale
     * For WTK, the pixelscale is 2.5*10^(-4) m =0.25mm
     * Scale 1=24000/(240*pixelscale)=24000/(240*0.00025)=400000
     * Scale 2=18000/(240*pixelscale)=18000/(240*0.00025)=300000
     * Scale 3=12000/(240*pixelscale)=12000/(240*0.00025)=200000
     * Scale 4=90/(240*pixelscale)=90/(240*0.00025)=1500
     * */
    public static String[] Sscale={"1:400000",
    	                           "1:300000",
    	                           "1:200000",
    	                           "1:2000",
    	                           "Raster Image Coordinate",
                                   };
	/** The real distance that will be displayed on the screen with scale option 1:400000*/
    public static final double   realheight_dis_s1=32000;
    public static final double   realwidth_dis_s1=24000; 
	/** The real distance that will be displayed on the screen with scale option 1:300000*/
    public static final double   realheight_dis_s2=24000;
    public static final double   realwidth_dis_s2=18000;
	/** The real distance that will be displayed on the screen with scale option 1:200000*/
    public static final double   realheight_dis_s3=16000;
    public static final double   realwidth_dis_s3=12000;  
	/** The real distance that will be displayed on the screen with scale option 1:3000*/
    public static final double   realheight_dis_s4=160;
    public static final double   realwidth_dis_s4=120;  
	/** The real distance that will be displayed on the screen with scale option 1:3000*/
    public static final double   realheight_dis_s5=280;
    public static final double   realwidth_dis_s5=140;  
//    public static final double   realheight_dis_s4=300;
//    public static final double   realwidth_dis_s4=150;  
    
	/** The screen width and height for Sony Ericsson K750i*/
    //WIDTH=176&HEIGHT=220
    public static final  int     Srceen_WIDTH_k750i=176;
    public static final  int     Srceen_HEIGHT_k750i=220;

/** The map scale
 * For Sony Ericsson K750i, the pixelscale is 1.622787*10^(-4) m =0.1622787mm
 * Scale 1=11424/(176*pixelscale)=11424/(176*0.0001622787)=400000
 * Scale 2=8568/(176*pixelscale)=11424/(176*0.0001622787)=300000
 * Scale 3=5712/(176*pixelscale)=11424/(176*0.0001622787)=200000
 * */
public static String[] Sscale_k750i={"1:400000",
	                           "1:300000",
	                           "1:200000"
                               };
/** The real distance that will be displayed on the screen with scale option 1:420000*/
public static final double   realheight_dis_s1_k750i=11424;
public static final double   realwidth_dis_s1_k750i=14281; 
/** The real distance that will be displayed on the screen with scale option 1:320000*/
public static final double   realheight_dis_s2_k750i=8568;
public static final double   realwidth_dis_s2_k750i=10710;
/** The real distance that will be displayed on the screen with scale option 1:220000*/
public static final double   realheight_dis_s3_k750i=5712;
public static final double   realwidth_dis_s3_k750i=7140;  
    ///////////////////////////////////////////////////////////////////
    
	//////////////////////PlaceSearchForm SETTING//////////////////////    
    
    ///////////////////////////////////////////////////////////////////
}
