package jagoclient.igs;

import java.io.*;

import jagoclient.Dump;

import rene.util.parser.*;

/**
This interprets, what the server send in response to a
status command (and to the problem command). It gets a
GoFrame to display the game status.
*/

public class Status
{	IgsGoFrame GF;
	IgsStream In;
	StatusDistributor PD;
	String Black,White;
	int L;
	
	/**
	Sends a status command to the server for game n.
	@param n the game number.
	*/
	public Status (IgsGoFrame gf, IgsStream in, PrintWriter out, int n)
	{	GF=gf; In=in;
		GF.active(false);
		PD=new StatusDistributor(in,this);
		out.println("status "+n);
		L=1;
	}
	
	/**
	A status object for an unknown game. This is used, when the
	status command has already been sent to the server.
	*/
	public Status (IgsGoFrame gf, IgsStream in, PrintWriter out)
	{	GF=gf; In=in;
		GF.active(false);
		PD=new StatusDistributor(in,this);
		L=1;
	}
	
	private String getname (String s)
	{	StringParser p=new StringParser(s);
		return p.parseword()+" ("+p.parseword()+")";
	}

	/** 
	This is called from the StatusDistributor.
	The output is interpreted and the go frame is updated.
	*/
	public void receive (String s)
	{	if (L==1) Black=s;
		else if (L==2)
		{	White=s;
			GF.settitle(getname(Black)+" - "+getname(White));
		}
		else
		{	while (true)
			{	StringParser p=new StringParser(s);
				p.skipblanks();
				if (!p.isint()) return;
				int n=p.parseint(':');
				if (p.error()) return;
				if (!p.skip(":")) return;
				p.skipblanks();
				char c;
				int i=0;
				while (!p.error())
				{	c=p.next();
					if (c=='0') GF.setblack(n,i);
					else if (c=='1') GF.setwhite(n,i);
					else if (c=='4' || c=='5') GF.territory(n,i);
					i++;
				}
				if (i!=GF.getboardsize())
				{	if (i<5 || i>29) break;
					GF.doboardsize(i);
				}
				else break;
			}
		}
		L++;
	}

	/**
	When the board status is complete the GoFrame window is
	asked to display itself.
	*/
	void finished ()
	{	Dump.println("Status is finished");
		GF.show();
		GF.active(true);
		// GF.B.showinformation();
		// GF.B.repaint();
	}

}
