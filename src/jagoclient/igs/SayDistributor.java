package jagoclient.igs;

import java.io.*;
import java.awt.*;

import jagoclient.Global;

/**
The SayDistributor is for say input from the server (19).
It will open a SayDialog to answer, unless messages are
not turned off.
*/

public class SayDistributor extends Distributor
{	PrintWriter Out;
	public SayDialog MD;
	ConnectionFrame CF;
	public SayDistributor (ConnectionFrame cf, IgsStream in, PrintWriter out)
	{	super(in,19,0,false);
		CF=cf; Out=out;
		MD=null;
	}
	public void send (String C)
	{	CF.append(C,Color.red.darker());
		if (CF.wantsmessages())
		{	if (MD!=null)
				MD.append(C);
			else
				MD=new SayDialog(CF,this,C,Out);
		}
	}
	public void remove ()
	{	MD=null;
	}
}
