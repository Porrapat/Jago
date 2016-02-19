package jagoclient.igs;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import jagoclient.gui.*;
import jagoclient.Global;
import jagoclient.sound.*;

import rene.util.parser.*;

/**
This dialog is opened, when a match request is received. It will
not parse this match request but still provide a mechanism for
automatic accept or decline.
*/

class MatchDialog extends CloseDialog
{	PrintWriter Out;
	TextField Answer;
	TextArea T;
	ConnectionFrame CF;
	InformationDistributor ID;
	String Accept,Decline,User="";
	public MatchDialog (ConnectionFrame cf, String m,
		PrintWriter out, InformationDistributor id)
	{	super(cf,Global.resourceString("Message"),false);
		CF=cf; ID=id;
		add("North",new MyLabel(Global.resourceString("Match_Request")));
		Panel pm=new MyPanel();
		pm.setLayout(new BorderLayout());
		pm.add("Center",T=new TextArea());
		T.setFont(Global.SansSerif);
		if (Global.Background!=null) T.setBackground(Global.Background);
		T.setEditable(false);
		T.setText(m);
		pm.add("South",Answer=
			new TextFieldAction(this,Global.resourceString("Send_Command")));
		Answer.addKeyListener(new KeyAdapter ()
			{	public void keyReleased (KeyEvent e)
				{	String s=Global.getFunctionKey(e.getKeyCode());
					if (s.equals("")) return;
					T.setText(s); 
				}
			}
		);
		add("Center",pm);
		Panel p=new MyPanel();
		p.add(new ButtonAction(this,Global.resourceString("Accept")));
		p.add(new ButtonAction(this,Global.resourceString("Decline")));
		p.add(new ButtonAction(this,Global.resourceString("Status")));
		p.add(new MyLabel(" "));
		p.add(new ButtonAction(this,Global.resourceString("Close")));
		p.add(new MyLabel(" "));
		p.add(new ButtonAction(this,Global.resourceString("Send_Command")));
		add("South",p);
		Out=out;
		validate();
		Global.setwindow(this,"matchdialog",300,400);
		StringParser mpa=new StringParser(m);
		mpa.upto('<'); mpa.skip("<");
		Accept=mpa.upto('>'); mpa.skip(">");
		mpa.upto('<'); mpa.skip("<");
		Decline=mpa.upto('>'); mpa.skip(">");
        if (Accept.equals("")) 
        {	StringParser mpa2=new StringParser(m);
			mpa2.upto('\"'); mpa2.skip("\"");
			Accept=mpa2.upto('\"'); mpa2.skip("\"");
			mpa2.upto('\"'); mpa2.skip("\"");
			Decline=mpa2.upto('\"'); mpa2.skip("\"");
        }
		if (Decline.startsWith("decline "))
			User=Decline.substring(8);
		if (Global.getParameter("friends","").indexOf(User)>=0)
		{	T.appendText("\n"+Global.resourceString("Opponent_is_a_friend_"));
		}
		if (Global.getParameter("marked","").indexOf(User)>=0)
		{	T.appendText("\n"+Global.resourceString("Opponent_is_marked_"));
		}
		String a=CF.reply();
		if (!a.equals(""))
		{	CF.append(Global.resourceString("Auto_reply_sent_to_")+User);
			Out.println("tell "+User+" "+a);
			Out.println("decline "+User);
		}
		else 
		{	setVisible(true);
			JagoSound.play("game","wip",true);
		}
	}
	public void windowOpened (WindowEvent e)
	{	T.requestFocus();
	}
	public void doAction (String o)
	{	Global.notewindow(this,"matchdialog");
		if (Global.resourceString("Close").equals(o))
		{	setVisible(false); dispose();
		}
		else if (Global.resourceString("Send_Command").equals(o))
		{	if (!Answer.getText().equals(""))
			{	CF.command(Answer.getText());
				Answer.setText("");
			}
		}
		else if (Global.resourceString("Accept").equals(o))
		{	Out.println(Accept);
			CF.append("--> "+Accept);
			setVisible(false); dispose();
		}
		else if (Global.resourceString("Decline").equals(o))
		{	Out.println(Decline);
			CF.append("--> "+Decline);
			setVisible(false); dispose();
		}
		else if (Global.resourceString("Status").equals(o))
		{	if (User!=null) Out.println("stats "+User);
			else Answer.setText("stats ");
		}
		else super.doAction(o);
	}
	public void append (String s)
	{	T.append(s);
	}
	public boolean close ()
	{	return true;
	}
}

