package jagoclient.partner;

import java.awt.*;
import java.net.*;
import java.io.*;

import jagoclient.dialogs.*;
import jagoclient.gui.*;
import jagoclient.Global;
import jagoclient.Dump;

/**
Question to end the game, or decline that.
*/

public class EndGameQuestion extends Question
{	PartnerFrame G;
	public EndGameQuestion (PartnerFrame g)
	{	super(g,Global.resourceString("End_the_game_"),Global.resourceString("End"),g,true); G=g;
		show();
	}
	public void tell (Question q, Object o, boolean f)
	{	q.setVisible(false); q.dispose();
	    if (f) G.doendgame();
		else G.declineendgame();
	}
	public boolean close ()
	{	G.declineendgame();
		return true;
	}
}

