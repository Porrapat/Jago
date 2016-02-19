package jagoclient.partner;

import java.awt.*;
import java.net.*;
import java.io.*;

import jagoclient.dialogs.*;
import jagoclient.gui.*;
import jagoclient.Global;
import jagoclient.Dump;

/**
Question to accept a result or decline it.
*/

public class ResultQuestion extends Question
{	int B,W;
	PartnerFrame G;
	/**
	@param b,w Black and White results
	*/
	public ResultQuestion (PartnerFrame g, String m, int b, int w)
	{	super(g,m,Global.resourceString("Result"),g,true); B=b; W=w; G=g;
		show();
	}
	public void tell (Question q, Object o, boolean f)
	{	q.setVisible(false); q.dispose();
	    if (f) G.doresult(B,W);
		else G.declineresult();
	}
	public boolean close ()
	{	G.declineresult();
		return true;
	}	
}

