package jagoclient.partner;

import java.awt.*;
import java.net.*;
import java.io.*;

import jagoclient.dialogs.*;
import jagoclient.gui.*;
import jagoclient.Global;
import jagoclient.Dump;

class ClosePartnerQuestion extends Question
{	public ClosePartnerQuestion (PartnerFrame g)
	{	super(g,Global.resourceString("This_will_close_your_connection_"),Global.resourceString("Close"),g,true);
		show();
	}
	public void tell (Question q, Object o, boolean f)
	{	q.setVisible(false); q.dispose();
	    if (f) ((PartnerFrame)o).doclose();
	}
}
