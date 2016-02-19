package jagoclient.igs.who;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

import jagoclient.gui.*;
import jagoclient.igs.*;
import jagoclient.dialogs.*;
import jagoclient.Global;

/**
Ask to tell the chosen user something, using the IGS tell command.
*/

public class TellQuestion extends CloseDialog
{	ConnectionFrame F;
	TextField T;
	TextField User;
	/**
	@param f the connection frame, which is used to send the output to IGS.
	@param user the user name of the person, which is to receive the message.
	*/
	public TellQuestion (Frame fr, ConnectionFrame f, String user)
	{	super(fr,Global.resourceString("Tell"),false);
		F=f;
		add("North",new SimplePanel(
			new MyLabel(Global.resourceString("To_")),0.4,
			User=new GrayTextField(user),0.6));
		add("Center",T=new TextFieldAction(this,Global.resourceString("Tell")));
		T.addKeyListener(new KeyAdapter ()
			{	public void keyReleased (KeyEvent e)
				{	String s=Global.getFunctionKey(e.getKeyCode());
					if (s.equals("")) return;
					T.setText(s); 
				}
			}
		);
		Panel p=new MyPanel();
		p.add(new ButtonAction(this,Global.resourceString("Tell")));
		p.add(new ButtonAction(this,Global.resourceString("Message")));
		p.add(new ButtonAction(this,Global.resourceString("Cancel")));
		add("South",new Panel3D(p));
		Global.setpacked(this,"tell",200,150,f);
		validate(); show();
	}
	public void windowOpened (WindowEvent e)
	{	T.requestFocus();
	}
	public void doAction (String o)
	{	Global.notewindow(this,"tell");   
		if (Global.resourceString("Tell").equals(o))
		{	if (!T.getText().equals(""))
			{	F.out("tell "+User.getText()+" "+T.getText());
			}
			setVisible(false); dispose();
		}
		else if (Global.resourceString("Message").equals(o))
		{	if (!T.getText().equals(""))
			{	F.out("message "+User.getText()+" "+T.getText());
			}
			setVisible(false); dispose();
		}
		else if (Global.resourceString("Cancel").equals(o))
		{	setVisible(false); dispose();
		}
		else super.doAction(o);
	}
}
