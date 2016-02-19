package jagoclient.dialogs;

import java.awt.*;
import java.io.*;

import jagoclient.gui.*;
import jagoclient.Global;

import rene.viewer.*;

/**
The same as Help.java but as a dialog. This is for giving help in
modal dialogs. 
@see jagoclient.dialogs.Help
*/

public class HelpDialog extends CloseDialog
{	Viewer V; // The viewer
	Frame F;
	/**
	Display the help from subject.txt,Global.url()/subject.txt
	or from the ressource /subject.txt.
	*/
	public HelpDialog (Frame f, String subject)
	{	super(f,Global.resourceString("Help"),true);
		F=f;
		V=Global.getParameter("systemviewer",false)?new SystemViewer():new Viewer();
		V.setFont(Global.Monospaced);
		V.setBackground(Global.gray);
		try
		{	BufferedReader in;
			String s;
			try
			{	in=Global.getStream(
					"jagoclient/helptexts/"+subject+Global.resourceString("HELP_SUFFIX")+".txt");
				s=in.readLine();
			}
			catch (Exception e)
			{	try
				{	in=Global.getStream(
						subject+Global.resourceString("HELP_SUFFIX")+".txt");
					s=in.readLine();
				}
				catch (Exception ex)
				{	in=Global.getStream("jagoclient/helptexts/"+subject+".txt");
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
	
	public void doclose ()
	{	setVisible(false); dispose();
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
