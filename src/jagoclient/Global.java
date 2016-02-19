package jagoclient;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import jagoclient.igs.*;

import rene.util.list.*;
import rene.util.parser.*;

class GlobalObject
{
}

/**
This class stores global parameters. It is equivalent to global
parameters in a non-OO programming environment.
<P>
The most important class of parameters is a list of keys and values.
It is implemented as a hash table. Paremeters can be strings, integers,
boolean values and colors. Function keys are stored as strings with
key f1 etc.
<P>
Another global variable is the start directory of Jago, where the
program expects its help files, unless the -home parameter is used.
Also the URL for the WWW applet version is stored here. The parameters
are retrieved from that URL, if applicable.
<P>
There is static RessourceBundle, which contains the label names etc. in
local versions. The file is "JagoResource.properties" (or 
"JagoResource_de.properties" and similar).
*/

public class Global extends rene.gui.Global
{	public static Component C;
	public static String Dir,Home;
	public static Frame F;
	public static MessageFilter MF=null;
	public static ListClass PartnerList=null;
	public static ListClass OpenPartnerList=null;
	public static boolean UseUrl=false;
	public static boolean IsApplet=false;
	public static URL Url;
	public static boolean Busy=true;
	public static Color gray=Color.gray;
	public static Font SansSerif,Monospaced,BigMonospaced,BoardFont;
	public static Hashtable WindowList;
	public static String Version="Version 4.53";
	public static int Silent=0;

	/** initialze the Jago Ressource bundle */
	static
	{	WindowList=new Hashtable();
		Dir=""; Home="";
		initBundle("JagoResource");
		if (B==null) initBundle("jagoclient/JagoResource");
	}
	
	/** load the message filters */
	public static void loadmessagefilter ()
	{	MF=new MessageFilter();
	}

	/** sets a global component for getting images and such */
	public static void setcomponent (Component c)
	{	C=c;
	}
	/** create an image */
	public static Image createImage (int w, int h)
	{	return C.createImage(w,h);
	}
	
	/** 
	Helper function for correctly open a stream to either
	an URL or a file in the current directory. URLs are used,
	when the applet starts from a server. If the opening fails,
	a ressource in the / (root) directory is tried. This
	allows for overwriting resources with local files.
	*/
	public static InputStream getDataStream (String filename)
	{	try
		{	if (useurl())
			{	return new URL(url(),filename).openStream();
			}
			else
			{	return new FileInputStream(Global.home()+filename);
			}
		}
		catch (Exception e)
		{	Object G=new GlobalObject();
			return G.getClass().getResourceAsStream("/"+filename);
		}
	}
	
	public static BufferedReader getStream (String filename, String encoding)
	{	try
		{	return new BufferedReader(
				new InputStreamReader(getDataStream(filename),encoding));
		}
		catch (UnsupportedEncodingException e)
		{	return getStream(filename);
		}
	}
	
	public static BufferedReader getStream (String filename)
	{	return new BufferedReader(
			new InputStreamReader(getDataStream(filename)));
	}
	
	public static BufferedReader getEncodedStream (String filename)
	{	String encoding=getParameter("HELP_ENCODING","");
		if (encoding.equals("")) return getStream(filename);
		else return getStream(filename,encoding);
	}
	
	/**
	Read the paramters from a file (normally go.cfg). This method
	uses getStream to either open an URL, a local file or a resource.
	<P>
	If there is the language parameter, a new resource bundle with
	that locale is loaded.
	*/
	public static void readparameter (String filename)
	{	File f=new File(home()+filename);
		loadProperties(getDataStream(filename));			
		String lang=getParameter("language","");
		if (!lang.equals(""))
		{	String langsec="";
			if (lang.length()>3 && lang.charAt(2)=='_')
			{	langsec=lang.substring(3);
				lang=lang.substring(0,2);
			}
			Locale.setDefault(new Locale(lang,langsec));
			initBundle("JagoResource");
		}
		gray=getColor("globalgray",new Color(220,220,220));
	}
	
	/** write the parmeter to the parameter file (normally go.cfg) */
	public static void writeparameter (String filename)
	{	if (isApplet()) return;
		saveProperties("Jago Properties",home()+"go.cfg");
	}

	public static void saveMessageFilter ()	
	{	if (MF!=null) MF.save();
	}
	
	/** get the current directory */
	public static String dir ()
	{	return Dir;
	}
	/** set the current directory */
	public static void dir (String dir)
	{	if (isApplet()) Dir=dir+"\\";
		else Dir=dir+System.getProperty("file.separator");
	}
	/** get the home directory */
	public static String home ()
	{	return Home;
	}
	/** set the home directory */
	public static void home (String dir)
	{	if (isApplet()) Home=dir+"\\";
		else Home=dir+System.getProperty("file.separator");
	}
	
	/** getParameter for color values */
	public static Color getColor (String a, int red, int green, int blue)
	{	return getParameter(a,red,green,blue);
	}
	/** getParameter for colors */
	public static Color getColor(String a, Color c)
	{	return getParameter(a,c);
	}
	/** setParameter for colors */
	public static void setColor (String a, Color c)
	{	setParameter(a,c);
	}
	
	/** set a default invisible frame */
	public static void frame (Frame f)
	{	F=f;
	}
	/** get the default frame */
	public static Frame frame ()
	{	if (F==null) F=new Frame();
		return F;
	}
	
	/** look up, if that string filters as blocking */
	public static int blocks (String s)
	{	return MF.blocks(s);
	}

	/** look up, if that string filters as positive filter */
	public static boolean posfilter (String s)
	{	return MF.posfilter(s);
	}
	
	/** get the string belonging to a function key */
	public static String getFunctionKey (int key)
	{	int i=0;
		switch (key)
		{	case KeyEvent.VK_F1 : i=1; break;
			case KeyEvent.VK_F2 : i=2; break;
			case KeyEvent.VK_F3 : i=3; break;
			case KeyEvent.VK_F4 : i=4; break;
			case KeyEvent.VK_F5 : i=5; break;
			case KeyEvent.VK_F6 : i=6; break;
			case KeyEvent.VK_F7 : i=7; break;
			case KeyEvent.VK_F8 : i=8; break;
			case KeyEvent.VK_F9 : i=9; break;
			case KeyEvent.VK_F10 : i=10; break;
		}
		if (i==0) return "";
		return getParameter("f"+i,"");
	}
	
	/** 
	@return the used URL 
	*/
	public static URL url () { return Url; }
	/** set the used url */
	public static void url (URL url) { Url=url; UseUrl=true; IsApplet=true; }
	public static void setApplet (boolean flag) { IsApplet=flag; }
	public static boolean isApplet () { return IsApplet; }
	/**
	@return Flag, if URL is used
	*/
	public static boolean useurl () { return UseUrl; }
	
	/** create the user chosen fonts */
	public static void createfonts ()
	{   SansSerif=createfont("sansserif","SansSerif","ssfontsize",11);
	    Monospaced=createfont("monospaced","Monospaced","msfontsize",11);
	    BigMonospaced=createfont("bigmonospaced","BoldMonospaced","bigmsfontsize",22);
	    BoardFont=createfont("boardfontname","SansSerif","boardfontsize",10);
	}
	static Font createfont (String name, String def, String size, int sdef)
	{   name=getParameter(name,def);
	    if (name.startsWith("Bold"))
	    {   return new Font(name.substring(4),Font.BOLD,Global.getParameter(size,sdef));
	    }
	    else if (name.startsWith("Italic"))
	    {   return new Font(name.substring(5),Font.ITALIC,Global.getParameter(size,sdef));
	    }
	    else
	    {   return new Font(name,Font.PLAIN,Global.getParameter(size,sdef));
	    }	    
	}
	
	/**
	Set the window sizes as read from go.cfg. The paramter tags are made
	from the window name and "ypos", "xpos", "width" or "height".
	*/
	public static void setwindow (Window c, String name, int w, int h,
		boolean minsize)
	{	int x=getParameter(name+"xpos",100),y=getParameter(name+"ypos",100);
		w=getParameter(name+"width",w); h=getParameter(name+"height",h);
		if (minsize)
		{	c.pack();
			Dimension dmin=c.getSize();
			if (dmin.width>w) w=dmin.width;
			if (dmin.height>h) h=dmin.height;
		}
		Dimension d=c.getToolkit().getScreenSize();
		if (w>d.width) w=d.width;
		if (h>d.height) h=d.height;
		if (x+w>d.width) x=d.width-w;
		if (x<0) x=0;
		if (y+h>d.height) y=d.height-h;
		if (y<0) y=0;
		c.pack();
		c.setBounds(x,y,w,h);
	}
	public static void setwindow (Window c, String name, int w, int h)
	{	setwindow(c,name,w,h,true);
	}
	
	/**
	Same as setwindow, but the window will be packed, if the pack
	parameter is set (advanced options).
	*/
	public static void setpacked (Window c, String name, int w, int h)
	{	Dimension d=c.getToolkit().getScreenSize();
		int x=d.width/2,y=d.height/2;
		x=getParameter(name+"xpos",100); y=getParameter(name+"ypos",100);
		w=getParameter(name+"width",w); h=getParameter(name+"height",h);
		if (w>d.width) w=d.width;
		if (h>d.height) h=d.height;
		if (x+w>d.width) x=d.width-w;
		if (x<0) x=0;
		if (y+h>d.height) y=d.height-h;
		if (y<0) y=0;
		if (Global.getParameter("pack",true))
		{	c.pack();
			c.setLocation(x,y);
		}
		else c.setBounds(x,y,w,h);
	}
	
	/**
	Places a dialog nicely centered with a frame.
	*/
	public static void setwindow (Dialog c, String name, int w, int h, Frame f)
	{	int x=f.getLocation().x+f.getSize().width/2-c.getSize().width/2,
			y=f.getLocation().y+f.getSize().height/2-c.getSize().height/2;
		w=getParameter(name+"width",w); h=getParameter(name+"height",h);
		Dimension d=c.getToolkit().getScreenSize();
		if (w>d.width) w=d.width;
		if (h>d.height) h=d.height;
		if (x+w>d.width) x=d.width-w;
		if (x<0) x=0;
		if (y+h>d.height) y=d.height-h;
		if (y<0) y=0;
		c.setBounds(x,y,w,h);
	}
	
	/**
	Same as setwindow(Dialog,...), but packs the dialog.
	*/
	public static void setpacked (Dialog c, String name, int w, int h, Frame f)
	{	int x=f.getLocation().x+f.getSize().width/2-c.getSize().width/2,
			y=f.getLocation().y+f.getSize().height/2-c.getSize().height/2;
		w=getParameter(name+"width",w); h=getParameter(name+"height",h);
		Dimension d=c.getToolkit().getScreenSize();
		if (w>d.width) w=d.width;
		if (h>d.height) h=d.height;
		if (x+w>d.width) x=d.width-w;
		if (x<0) x=0;
		if (y+h>d.height) y=d.height-h;
		if (y<0) y=0;
		if (Global.getParameter("pack",true)) c.pack();
		else c.setSize(w,h);
		c.setLocation(x,y);
	}
	
	/**
	Same as setwindow(Dialog,...), but packs the dialog.
	*/
	public static void setpacked (Frame c, String name, int w, int h, Frame f)
	{	int x=f.getLocation().x+f.getSize().width/2-c.getSize().width/2,
			y=f.getLocation().y+f.getSize().height/2-c.getSize().height/2;
		w=getParameter(name+"width",w); h=getParameter(name+"height",h);
		Dimension d=c.getToolkit().getScreenSize();
		if (w>d.width) w=d.width;
		if (h>d.height) h=d.height;
		if (x+w>d.width) x=d.width-w;
		if (x<0) x=0;
		if (y+h>d.height) y=d.height-h;
		if (y<0) y=0;
		if (Global.getParameter("pack",true)) c.pack();
		else c.setSize(w,h);
		c.setLocation(x,y);
	}
	
	/** note the size of a window in go.cfg. */
	public static void notewindow (Component c, String name)
	{   setParameter(name+"width",c.getSize().width);
	    setParameter(name+"height",c.getSize().height);
	    setParameter(name+"xpos",c.getLocation().x);
	    setParameter(name+"ypos",c.getLocation().y);
	}
	
	/**
	Get the national translation fot the string s.
	The resource strings contain _ instead of blanks.
	If the resource is not found, the strings s (with _ replaced
	by blanks) will be used.
	*/
	public static String resourceString (String s)
	{	String res;
		s=s.replace(' ','_');
		res=name(s,"???");
		if (res.equals("???"))
		{	res=s.replace('_',' ');
			if (res.endsWith(" n"))
			{	res=res.substring(0,res.length()-2)+"\n";
			}
		}
		return res;
	}
}
