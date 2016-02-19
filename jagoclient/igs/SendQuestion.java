package jagoclient.igs;

import java.awt.*;
import java.awt.event.*;

import jagoclient.gui.*;
import jagoclient.board.*;
import jagoclient.sound.*;
import jagoclient.Global;

import rene.util.parser.*;

/**
This dialog is opened by IgsGoFrame, when the "Send" button is pressed.
*/

public class SendQuestion extends CloseDialog
{	IgsGoFrame F;
	TextField T;
	Distributor Dis;
	public SendQuestion (IgsGoFrame f, Distributor dis)
	{	super(f,Global.resourceString("Send"),false);
		F=f;
		add("North",new MyLabel(Global.resourceString("Message_")));
		add("Center",T=new GrayTextField(40));
		Panel p=new MyPanel();
		p.add(new ButtonAction(this,Global.resourceString("Kibitz")));
		if (dis instanceof PlayDistributor)
			p.add(new ButtonAction(this,Global.resourceString("Say")));
		p.add(new ButtonAction(this,Global.resourceString("Cancel")));
		add("South",new Panel3D(p));
		Global.setpacked(this,"send",200,150);
		validate(); 
		Global.setpacked(this,"sendquestion",300,150,f); setVisible(true);
		Dis=dis;
	}
	public void doAction (String o)
	{	Global.notewindow(this,"send");
		if (Global.resourceString("Kibitz").equals(o))
		{	if (!T.getText().equals(""))
			{	Dis.out("kibitz "+Dis.game()+" "+T.getText());
				F.addComment("Kibitz: "+T.getText());
			}
			setVisible(false); dispose();
		}
		else if (Global.resourceString("Say").equals(o))
		{	if (!T.getText().equals(""))
			{	Dis.out("say "+T.getText());
				F.addComment("Say: "+T.getText());
			}
			setVisible(false); dispose();
		}
		else if (Global.resourceString("Cancel").equals(o))
		{	setVisible(false); dispose();
		}
		else super.doAction(o);
	}
}
