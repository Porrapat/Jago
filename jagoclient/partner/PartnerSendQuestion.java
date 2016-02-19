package jagoclient.partner;

import java.awt.*;

import jagoclient.gui.*;
import jagoclient.dialogs.*;
import jagoclient.Global;

/**
Displays a send dialog, when the partner presses "Send" in the
GoFrame. The message is appended to the PartnerFrame.
*/

public class PartnerSendQuestion extends CloseDialog
{	PartnerGoFrame F;
	TextField T;
	PartnerFrame PF;
	public PartnerSendQuestion (PartnerGoFrame f, PartnerFrame pf)
	{	super(f,Global.resourceString("Send"),false);
		F=f; PF=pf;
		add("North",new MyLabel(Global.resourceString("Message_")));
		add("Center",T=new GrayTextField(25));
		Panel p=new MyPanel();
		p.add(new ButtonAction(this,Global.resourceString("Say")));
		p.add(new ButtonAction(this,Global.resourceString("Cancel")));
		add("South",new Panel3D(p));
		Global.setpacked(this,"partnersend",200,150);
		validate(); show();
	}
	public void doAction (String o)
	{	Global.notewindow(this,"partnersend");	
		if (Global.resourceString("Say").equals(o))
		{	if (!T.getText().equals(""))
			{	PF.out(T.getText());
				F.addComment(Global.resourceString("Said__")+T.getText());
			}
			setVisible(false); dispose();
		}
		else if (Global.resourceString(Global.resourceString("Cancel")).equals(o))
		{	setVisible(false); dispose();
		}
		else super.doAction(o);
	}
}
