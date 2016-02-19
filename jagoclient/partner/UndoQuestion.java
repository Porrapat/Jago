package jagoclient.partner;

import java.awt.*;
import java.net.*;
import java.io.*;

import jagoclient.dialogs.*;
import jagoclient.gui.*;
import jagoclient.Global;
import jagoclient.Dump;

/**
Question to undo a move, or decline the undo request.
*/

public class UndoQuestion extends Question
{	PartnerFrame G;
	public UndoQuestion (PartnerFrame g)
	{	super(g,Global.resourceString("Partner_request_undo__Accept_"),Global.resourceString("Undo"),g,true);
		G=g;
		show();
	}
	public void tell (Question q, Object o, boolean f)
	{	q.setVisible(false); q.dispose();
	    if (f) G.doundo();
		else G.declineundo();
	}
	public boolean close ()
	{	G.declineundo();
		return true;
	}
}

