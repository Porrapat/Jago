package jagoclient.igs;

import java.io.*;
import java.awt.*;
import java.util.Vector;

/**
This distributor opens a ChannelDialog for channel n. IgsStream
sorts out the channels and calls the correct distributor.
@see jagoclient.igs.ChannelDialog
*/
public class ChannelDistributor extends Distributor
{	ConnectionFrame CF;
	PrintWriter Out;
	ChannelDialog CD;
	public ChannelDistributor
		(ConnectionFrame cf, IgsStream in, PrintWriter out, int n)
	{	super(in,32,n,false);
		CF=cf; Out=out;
		CD=new ChannelDialog(CF,Out,game(),this);
	}
	public void send (String C)
	{	if (CD==null)
		{	CD=new ChannelDialog(CF,Out,game(),this);
		}
		CD.append(C);
	}
}

