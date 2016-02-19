package jagoclient.igs;

import java.io.*;

import jagoclient.sound.*;
import jagoclient.Dump;
import jagoclient.dialogs.*;
import jagoclient.Global;

import rene.util.parser.*;
import rene.util.list.*;

/**
This class handles the line by line input from the server,
parsing it for command numbers and sub-numbers.
<P>
Furthermore, it will filter the input and distribute it to
the distributors. The class initializes a list of distributors.
All distributors must chain themselves to this list. They can
unchain themselves, if they do no longer wish to receive
input.
<P>
The input is done via a BufferedReader, which does the encoding
stuff.
*/

public class IgsStream
{	String Line;
	char C[];
	final int linesize=4096;
	int L;
	int Number;
	String Command;
	ListClass DistributorList;
	PrintWriter Out;
	BufferedReader In;
	ConnectionFrame CF;
	
	/**
	The in and out streams to the server are opened by the ConnectionFrame.
	However, the input stream is used for a BufferedReader, which does
	local decoding. The output stream is already assumed to be using
	the correct encoding.
	@see jagoclient.igs.ProxyIgsStream
	*/
	public IgsStream (ConnectionFrame cf, InputStream in, 
		PrintWriter out)
	{	CF=cf;
		Out=out;
		Line="";
		L=0;
		Number=0;
		Dump.println("--> IgsStream opened");
		DistributorList=new ListClass();
		C=new char[linesize];
		initstream(in);
	}
	
	/**
	This initializes a BufferedReader to do the decoding.
	*/
	public void initstream (InputStream in)
	{	try
		{	InputStream ina;
			String encoding=CF.Encoding;
			if (encoding.startsWith("!"))
			{	ina=in;
				encoding=encoding.substring(1);
			}
			else ina=new TelnetStream(CF,in,Out);
			if (encoding.equals(""))
				In=new BufferedReader(new InputStreamReader(ina));
			else
				In=new BufferedReader(new InputStreamReader(ina,encoding));
		}
		catch (UnsupportedEncodingException e)
		{	CF.append(e.toString()+"\n");
			In=new BufferedReader(new InputStreamReader(in));
		}
		catch (IllegalArgumentException e)
		{	CF.append(e.toString()+"\n");
			In=new BufferedReader(new InputStreamReader(in));
		}
	}

	int lastcr=0;

	public char read () throws IOException
	{	while (true)
		{	int c=In.read();
			if (c==-1) throw new IOException();
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
			else lastcr=0;
			return (char)c;
		}
	}
	
	/** test, if there is another character waiting */
	public boolean available () throws IOException
	{	return In.ready();
	}

	public void close () throws IOException
	{	In.close();
	}

	public BufferedReader getInputStream ()
	{	return In;
	}

	/**
	This reads a complete line from the server.
	*/
	void readlineprim () throws IOException
	{	StringParser sp;
		char b=read();
		L=0;
		while (true)
		{	if (L>=linesize || b=='\n')
			{	if (L>=linesize) Dump.println("IGS : Buffer overflow");
				break;
			}
			C[L++]=b;
			b=read();
		}
		Number=0;
		Command="";
		int i;
		for (i=0; i<L; i++)
		{	if (C[i]<'0' || C[i]>'9') break;
		}
		if (i>0)
		{	Number=Integer.parseInt(new String(C,0,i),10);
			Command=new String(C,i+1,L-(i+1));
		}
		else
		{	Number=0;
			Command=new String(C,0,L);
		}
		L=0;
	}

	/**
	Processes the input, until there is a line, which is not
	suited for the specified distributor.
	*/
	void sendall (Distributor dis) throws IOException
	{	while (true)
		{	readlineprim();
			Dump.println("IGS: "+Command);
			if (Number==dis.number()) dis.send(Command);
			else
			{	if (dis.once())
				{	unchain(dis);
					dis.finished();
				}
				break;
			}
		}
		dis.allsended();
	}

	/**
	Sames as above, but the distrubutor get the input with
	the String prepended.
	*/
	void sendall (String s, Distributor dis) throws IOException
	{	while (true)
		{	readlineprim();
			Dump.println("IGS: "+Command);
			if (Number==11) dis.send(s+Command);
			else
			{	if (dis.once())
				{	unchain(dis);
					dis.finished();
				}
				break;
			}
		}
		dis.allsended();
	}

	/**
	The most important method of this class.
	<P>
	This method reads input from the server line by line, filtering
	out and answering Telnet protocol characters. If it receives a
	full line it will interpret it and return true. Otherwise,
	it will return false. The line can be read from the Line
	variable. Incomplete lines happen only at the start of the
	connection during login.
	<P>
	Interpreting a lines means determining its command number and
	an eventual sub-command number. Both are used to determine
	the right distributor for this command, if there is one. Otherwise,
	the function returns true and InputThread handles the command.
	<P>
	The protocol is not very logic, and nor is the structure of this
	method. It has cases for several distributors. Probably, this
	code should a static method of the distributor.
	@see jagoclient.igs.InputThread
	*/
	public boolean readline () throws IOException
	{	boolean full;
		StringParser sp;
		outerloop: while (true)
		{	full=false;
			char b=0;
			b=read();
			while (true)
			{	if (L>=linesize)
				{	Dump.println("IGS : Buffer overflow");
					throw new IOException("Buffer Overflow");
				}
				if (b=='\n')
				{	full=true; break;
				}
				C[L++]=b;
				if (!available()) break;
				b=read();
			}
			// Dump.println(L+" characters received from server");
			Line=new String(C,0,L);
			Dump.println("IGS: "+Line);
			Number=0;
			Command="";
			if (full)
			{	int i;
				for (i=0; i<L; i++)
				{	if (C[i]<'0' || C[i]>'9') break;
				}
				if (i>0 && C[i]==' ')
				{	try
					{	Number=Integer.parseInt(new String(C,0,i),10);
					}
					catch (Exception e) { break; }
					Command=new String(C,i+1,L-(i+1));
				}
				else
				{	Number=100;
					Command=new String(C,0,L);
				}
				L=0;
				loop1: while (true)
				{	if (Number==21 && 
						(Command.startsWith("{Game") || 
						Command.startsWith("{ Game"))
						)
					{	sp=new StringParser(Command);
						sp.skip("{");
						sp.skipblanks();
						sp.skip("Game");
						sp.skipblanks();
						if (!sp.isint()) continue outerloop;
						int G=sp.parseint(':');
						Distributor dis=findDistributor(15,G);
						if (dis!=null)
						{	Dump.println("Sending comment to game "+G);
							dis.send(Command);
							L=0; continue outerloop;
						}
						if (Global.getParameter("reducedoutput",true)
							&& !Global.posfilter(Command))
						{	continue outerloop;
						}
					}
					else if (Number==21 && !CF.Waitfor.equals("") &&
						Command.indexOf(CF.Waitfor)>=0)
					{	new Message(CF,Command);
					}
					else if (Number==21 && Command.startsWith("{")
						&& Global.getParameter("reducedoutput",true) &&
						!Global.posfilter(Command))
					{	continue outerloop;
					}
					else if (Number==21 && Global.posfilter(Command))
					{	JagoSound.play("message","wip",true);
					}
					else if (Number==11 && Command.startsWith("Kibitz"))
					{	sp=new StringParser(Command);
						sp.upto('['); if (sp.error()) continue outerloop;
						sp.upto(']'); if (sp.error()) continue outerloop;
						sp.upto('['); if (sp.error()) continue outerloop;
						sp.skip("[");
						sp.skipblanks();
						if (!sp.isint()) continue outerloop;
						int G=sp.parseint(']');
						Distributor dis=findDistributor(15,G);
						if (dis!=null)
						{	Dump.println("Sending kibitz to game "+G);
							dis.send(Command);
							sendall("Kibitz-> ",dis);
							continue loop1;
						}
					}
					else if (Number==9 && Command.startsWith("Removing @"))
					{	sp=new StringParser(Command);
						sp.skip("Removing @");
						Distributor dis=findDistributor(15);
						if (dis!=null)
						{	Dump.println("Got "+Command);
							dis.send(Command);
							sendall(dis);
							continue loop1;
						}
						continue outerloop;
					}
					else if (Number==9 && !Command.startsWith("File"))
					{	Distributor dis=findDistributor(9);
						if (dis!=null)
						{	Dump.println("Sending information");
							dis.send(Command);
							sendall(dis);
							continue loop1;
						}
					}
					else if (Number==15 && Command.startsWith("Game"))
					{	sp=new StringParser(Command);
						sp.skip("Game");
						sp.skipblanks();
						if (!sp.isint()) continue outerloop;
						int G=sp.parseint();
						Distributor dis=findDistributor(15,G);
						if (dis!=null)
						{	Dump.println("Sending to game "+G);
							dis.send(Command);
								sendall(dis);
								continue loop1;
						}
						dis=findDistributor(15,-1);
						if (dis!=null)
						{	Dump.println("Game "+G+" started");
							dis.game(G);
							dis.send(Command);
							sendall(dis);
							continue loop1;
						}
						continue outerloop;
					}
					else if (Number==32)
					{	sp=new StringParser(Command);
						int G=sp.parseint(':');
						sp.skip(":"); sp.skipblanks();
						if (G==0)
						{	Number=9; continue loop1;
						}
						Distributor dis=findDistributor(32,G);
						if (dis!=null)
						{	Dump.println("Sending to channel "+G);
							dis.send(sp.upto((char)0));
							continue outerloop;
						}
						ChannelDistributor cd=
							new ChannelDistributor(CF,this,Out,G);
						cd.send(sp.upto((char)0));
						continue outerloop;
					}
					else if (Number==22)
					{	Distributor dis=findDistributor(22);
						if (dis!=null)
						{	dis.send(Command);
							sendall(dis);
							continue loop1;
						}
						IgsGoFrame gf=new IgsGoFrame(CF,"Peek game");
						Status s=new Status(gf,this,Out);
						s.PD.send(Command);
						sendall(s.PD);
					}
					else if (Number==9 && Command.startsWith("-- "))
					{	Distributor dis=findDistributor(32);
						if (dis!=null)
						{	Dump.println("Sending to channel "+dis.game());
							dis.send(Command);
							continue outerloop;
						}
						continue outerloop;
					}
					else if (Number!=9)
					{	Distributor dis=findDistributor(Number);
						if (dis!=null)
						{	dis.send(Command);
							sendall(dis);
							continue loop1;
						}
					}
					break;
				}
				L=0;
			}
			break;
		}
		return full;
	}
	
	public Distributor findDistributor (int n, int g)
	{	synchronized (DistributorList)
		{	ListElement l=DistributorList.first();
			Distributor dis;
			while (l!=null)
			{	dis=(Distributor)l.content();
				if (dis.number()==n)
				{	if (dis.game()==g) return dis;
				}
				l=l.next();
			}
			return null;
		}
	}

	public Distributor findDistributor (int n)
	{	synchronized (DistributorList)
		{	ListElement l=DistributorList.first();
			Distributor dis;
			while (l!=null)
			{	dis=(Distributor)l.content();
				if (dis.number()==n) return dis;
				l=l.next();
			}
			return null;
		}
	}

	/**
	Chaines a new distributor to the distributor list.
	*/
	public void distributor (Distributor o)
	{	synchronized (DistributorList)
		{	DistributorList.append(new ListElement(o));
		}
	}

	/**
	Seeks a game distributor, which waits for that game.
	*/
	public boolean gamewaiting (int g)
	{	synchronized (DistributorList)
		{	ListElement l=DistributorList.first();
			Distributor dis;
			while (l!=null)
			{	dis=(Distributor)l.content();
				if (dis.number()==15)
				{	if (dis.game()==g) return true;
				}
				l=l.next();
			}
			return false;
		}
	}
	
	public void unchain (Distributor o)
	{	try 
		{	synchronized (DistributorList)
			{	ListElement l=DistributorList.first();
				while (l!=null)
				{	if ((Distributor)l.content()==o) DistributorList.remove(l);
					l=l.next();
				}
			}
		} 
		catch (Exception e) {}
	}

	/**
	Removes all distributors from the distributor list.
	*/
	public void removeall ()
	{	synchronized (DistributorList)
		{	ListElement l=DistributorList.first();
			while (l!=null)
			{	((Distributor)l.content()).remove();
				l=l.next();
			}
		}
	}

	public void out (String s)
	{	if (s.startsWith("observe") || s.startsWith("moves")
			|| s.startsWith("status")) return;
		Out.println(s);
		Out.flush();
		Dump.println("Sending: "+s);
	}

	public int number () { return Number; }
	public String command () { return Command; }
	public int commandnumber ()
	{	try
		{	return Integer.parseInt(Command,10);
		}
		catch (NumberFormatException e)
		{	return 0;
		}
	}
	public String line () { return Line; }
	
}

