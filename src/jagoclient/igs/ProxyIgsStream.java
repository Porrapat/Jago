package jagoclient.igs;

import java.io.*;

import jagoclient.Dump;

/**
This is a specified IGS stream, which reads byte over a telnet proxy.
Thus telnet commands must be filtered. Consequently, it must use
a byte stream and cannot translate into locales.
*/

public class ProxyIgsStream extends IgsStream
{	DataInputStream In;
	
	public ProxyIgsStream (ConnectionFrame cf, InputStream in, 
		PrintWriter out)
	{	super(cf,in,out);
	}
		
	public void initstream (InputStream in)
	{	In=new DataInputStream(in);
	}

	public char read () throws IOException
	{	while (true)
		{	byte c=In.readByte();
			if (c==-1) // Telnet ??
			{	c=In.readByte();
				Dump.println("Telnet received!"+(int)(256+c));
				if (c==-3)
				{	c=In.readByte();
					CF.Outstream.write(255);
					CF.Outstream.write(252); 
					CF.Outstream.write(c);
					continue;
				}
				else if (c==-5)
				{	c=In.readByte();
					continue;
				}
			}
			if (c==10)
			{	if (lastcr==13)
				{	lastcr=0;
					continue;
				}
				lastcr=10;
				return '\n';
			}
			else if (c==13)
			{	if (lastcr==10)
				{	lastcr=0;
					continue;
				}
				lastcr=13;
				return '\n';
			}
			return (char)c;
		}
	}
	
	public boolean available ()
	{	try
		{	return (In.available()>0);
		}
		catch (IOException e) { return false; }
	}

	public void close () throws IOException
	{	In.close();
	}

}
