package jagoclient.dialogs;

import java.awt.*;
import java.io.*;
import java.net.*;

import jagoclient.gui.*;
import jagoclient.Global;

import rene.viewer.*;

/**
<P>
A dialog class for displaying help texts. The help texts
are in ASCII text files with ending .txt in the root resource
directory. If the parameter HELP_SUFFIX is in the properties
file, it will appended for local language help (such as about_de.txt).
<P>
The text will either be loaded from a file, from an URL or a ressource 
using the getStream method of Global.
@see jagoclient.Global#getStream
*/

public class Help extends CloseFrame implements Runnable
{	Viewer V; // The viewer
	/**
	Display the help from subject.txt,Global.url()/subject.txt
	or from the ressource /subject.txt.
	*/
	public Help (String subject)
	{	super(Global.resourceString("Help"));
		seticon("ihelp.gif");
		V=Global.getParameter("systemviewer",false)?new SystemViewer():new Viewer();
		V.setFont(Global.Monospaced);
		V.setBackground(Global.gray);
		try
		{	BufferedReader in;
			String s;
			try
			{	in=Global.getEncodedStream(
					"jagoclient/helptexts/"+subject+Global.resourceString("HELP_SUFFIX")+".txt");
				s=in.readLine();
			}
			catch (Exception e)
			{	try
				{	in=Global.getEncodedStream(
						subject+Global.resourceString("HELP_SUFFIX")+".txt");
					s=in.readLine();
				}
				catch (Exception ex)
				{	in=Global.getEncodedStream("jagoclient/helptexts/"+subject+".txt");
					s=in.readLine();
				}
			}
			while (s!=null)
			{	V.appendLine(s);
				s=in.readLine();
			}
			in.close();
		}
		catch (Exception e)
		{	new Message(Global.frame(),
				Global.resourceString("Could_not_find_the_help_file_"));
			doclose();
			return;
		}
		display();
	}
	
	/**
	This constructor is used to get the about.txt file from
	our homeserver (address is hard coded into the run method).
	A thread is used to wait for connection.
	*/
	public Help ()
	{   super(Global.resourceString("Help"));
		seticon("ihelp.gif");
		V=Global.getParameter("systemviewer",false)?new SystemViewer():new Viewer();
		V.setFont(Global.Monospaced);
		V.setBackground(Global.gray);
		new Thread(this).start();
	}

	public void run ()
	{	String H="";
		try
		{	BufferedReader in;
			in=new BufferedReader(new InputStreamReader(
			    new DataInputStream(
				new URL("http://www.rene-grothmann.de/jago/about.txt").openStream())));
			while (true)
			{	String s=in.readLine();
				if (s==null) break;
				V.appendLine(s);
			}
			in.close();
		}
		catch (Exception e)
		{	new Message(Global.frame(),
				Global.resourceString("Could_not_find_the_help_file_"));
			doclose();
			return;
		}
		display();
	}

	void display ()
	{	Global.setwindow(this,"help",500,400);
		add("Center",V);
		Panel p=new MyPanel();
		p.add(new ButtonAction(this,Global.resourceString("Close")));
		add("South",new Panel3D(p));
		setVisible(true);
	}
	public void doAction (String o)
	{	Global.notewindow(this,"help");
		super.doAction(o);
	}
}
