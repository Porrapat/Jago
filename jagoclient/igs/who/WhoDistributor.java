package jagoclient.igs.who;

import java.io.*;
import java.awt.*;

import jagoclient.gui.*;
import jagoclient.igs.*;
import jagoclient.dialogs.*;
import jagoclient.Global;

import rene.util.sort.*;
import rene.util.parser.*;
import rene.util.list.*;
import rene.viewer.*;

/**
A distributor for the player listing.
*/

public class WhoDistributor extends Distributor
{	WhoFrame P;
	public WhoDistributor (IgsStream in, WhoFrame p)
	{	super(in,27,0,true);
		P=p;
	}
	public void send (String c)
	{	P.receive(c);
	}
	public void allsended ()
	{	P.allsended();
	}
}
