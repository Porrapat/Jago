package jagoclient.igs;

import java.io.*;

import jagoclient.Dump;

/**
This is a stream, which filters Telnet commands.
*/

public class TelnetStream extends FilterInputStream
{	InputStream In;
	PrintWriter Out;
	ConnectionFrame CF;
	
	int lastcr=0;
	
	public TelnetStream (ConnectionFrame cf, InputStream in, 
		PrintWriter out)
	{	super(in);
		In=in;
		Out=out;
		CF=cf;
	}
		
	public int read () throws IOException
	{	while (true)
		{	int c=In.read();
			if (c==255) // Telnet ??
			{	int command=In.read();
				Dump.println("Telnet received!"+command);
				if (command==253)
				{	c=In.read();
					CF.Outstream.write(255);
					CF.Outstream.write(252); 
					CF.Outstream.write(c);
				}
				else if (command==246)
				{	CF.Outstream.write(255);
					CF.Outstream.write(241); 
				}
				if (c==-1) return c;
				return 0;
			}
			if (c>=0 && c<=9) return 0;
			if (c==12) return 0;
			return c;
		}
	}
	
	public int read (byte b[], int off, int len)
		throws IOException
	{	int i=0;
		int c=read();
		if (c==0) c=' ';
		b[off+i]=(byte)c;
		i++;
		if (c==-1) return 1;
		while (In.available()>0 && i<len)
		{	c=read();
			if (c==0) c=' ';
			b[off+i]=(byte)c;
			i++;
			if (c==-1) break;
		}
		return i;
	}
	
}
