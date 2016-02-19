package jagoclient.partner.partner;

import java.io.*;

import rene.util.parser.*;

public class Partner
{	public String Name, Server;
	public int Port;
	public boolean Valid,Trying;
	public int State=PRIVATE;
	public static final int SILENT=0,PRIVATE=1,LOCAL=2,PUBLIC=3;
	public Partner (String line)
	{	StringParser p=new StringParser(line);
		Valid=false;
		Trying=false;
		p.skip("["); Name=p.upto(']');
		p.skip("]"); p.skipblanks(); if (p.error()) return;
		p.skip("["); Server=p.parseword(']');
		p.skip("]"); p.skipblanks(); if (p.error()) return;
		p.skip("["); Port=p.parseint(']');
		p.skip("]"); p.skipblanks();
		if (p.skip("["))
		{	int s=p.parseint(']');
			if (!p.error()) State=s;
		}
		Valid=true;
	}
	public Partner (String name, String server, int port, int state)
	{	Valid=true; Trying=false;
		Name=name; Server=server; Port=port; State=state;
	}
	public void write (PrintWriter out)
	{	out.println("["+Name+"] ["+Server+"] ["+
				Port+"] ["+State+"]");
	}
	public boolean valid () { return Valid; }
}

