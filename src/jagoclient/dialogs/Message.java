package jagoclient.dialogs;

import java.awt.*;
import java.util.*;

import jagoclient.gui.*;
import jagoclient.Global;

import rene.viewer.*;

/**
This class is used to display messages from the go server.
The message can have several lines.
*/

public class Message extends CloseDialog
{	Viewer T;
	public Message (Frame f, String m)
	{	super(f,Global.resourceString("Message"),false);
		add("Center",T=
			Global.getParameter("systemviewer",false)?
				new SystemViewer():new Viewer());
		T.setFont(Global.Monospaced);
		Panel p=new MyPanel();
		p.add(new ButtonAction(this,Global.resourceString("OK")));
		add("South",new Panel3D(p));
		Global.setwindow(this,"message",300,150);
		validate();
		show();
		T.setText(m);
	}
	public void doAction (String o)
	{	Global.notewindow(this,"message");	
		if (Global.resourceString("OK").equals(o))
		{	setVisible(false); dispose();
		}
		else super.doAction(o);
	}
}

