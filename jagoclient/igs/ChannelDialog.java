package jagoclient.igs;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import jagoclient.gui.*;
import jagoclient.dialogs.*;
import jagoclient.Global;

import rene.viewer.*;

/**
Contains a text area and a text field for anwers.
@see jagoclient.igs.ChannelDistributor
*/
public class ChannelDialog extends CloseFrame 
	implements CloseListener, KeyListener
{	PrintWriter Out;
    	TextArea T;
	ConnectionFrame CF;
	ChannelDistributor MDis;
	int N;
	HistoryTextField Answer;
	public ChannelDialog (ConnectionFrame cf,
		PrintWriter out, int n, ChannelDistributor mdis)
	{	super(Global.resourceString("Channel"));
		CF=cf; MDis=mdis; N=n;
		CF.addCloseListener(this);
		setLayout(new BorderLayout());
		MenuBar M=new MenuBar();
		Menu help=new MyMenu(Global.resourceString("Help"));
		help.add(new MenuItem(Global.resourceString("Channels")));
		M.setHelpMenu(help);
		add("North",new MyLabel(Global.resourceString("Channel_")+n));
		Panel pm=new MyPanel();
		pm.setLayout(new BorderLayout());
		pm.add("Center",T=new MyTextArea("",0,0,TextArea.SCROLLBARS_VERTICAL_ONLY));
		pm.add("South",Answer=new HistoryTextField(this,"Answer"));
		add("Center",pm);
		Panel pb=new MyPanel();
		pb.add(new ButtonAction(this,Global.resourceString("Close")));
		add("South",new Panel3D(pb));
		Out=out;
		seticon("iwho.gif");
		Global.setwindow(this,"channeldialog",500,400);
		validate(); show();
	}
	public void doAction (String o)
	{	if (Global.resourceString("Channels").equals(o))
		{	new Help("channels");
		}
		else if ("Answer".equals(o))
		{	if (!Answer.getText().equals(""))
			{	Out.println("; "+Answer.getText());
				T.append("---> "+Answer.getText()+"\n");
				Answer.setText("");
			}
		}
		else super.doAction(o);
	}
	public boolean close ()
	{	return true;
	}
	public void append (String s)
	{	T.append(s+"\n");
	}
	public boolean escape ()
	{	return false;
	}
	public void isClosed ()
	{	doclose();
	}
	public void doclose ()
	{	MDis.CD=null;
		Global.notewindow(this,"channeldialog");		
		super.doclose();
	}
	public void keyPressed (KeyEvent e)
	{	if (e.getKeyCode()==KeyEvent.VK_ESCAPE && close()) doclose();
	}
	public void keyTyped (KeyEvent e) {}
	public void keyReleased (KeyEvent e)
	{	String s=Global.getFunctionKey(e.getKeyCode());
		if (s.equals("")) return;
		Answer.setText(s); Answer.requestFocus();
	}
}
