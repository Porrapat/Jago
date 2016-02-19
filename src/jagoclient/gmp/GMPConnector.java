package jagoclient.gmp;

import java.io.*;

import jagoclient.Dump;

class GMPCloser extends Thread
{	GMPConnector C;
	public GMPCloser (GMPConnector c)
	{	C=c;
		start();
	}
	public void run ()
	{	C.destroy();
	}
}

/**
<P>This class opens a connection to an external program, and
communicates with using pipes.</P>
<P>The communication handling is a bit difficult to understand, for it
is done asynchronically in a separate thread. The external program
sends command, which are automatically handled or answered by this
class. To be able to do this, the class needs an GMPInterface
object, which provides the necessary informations to answer
questions (such as the board size), and handles commands (such as
a move). The interface is set with setGMPInterface(). </P>
<P> Of course, commands to be sent to the external program can be
sent via the methods send(), move() etc. directly and asynchronically.
</P>
<P> The GMP protocol is a binary protocol, which is not human
readable. Moreover, it has design flaws, when the programs
disagree about the board size, handicap etc. In our case, we expect
the external program to ask for these values. If it does not ask,
a problem will arise. The communication might fail in this case. </P>
*/

public class GMPConnector
	implements Runnable
{	String Program;
	Process P;
	InputStream In,Err;
	OutputStream Out;

	/**
	@param program The GMP program's file name.
	*/	
	public GMPConnector (String program)
	{	Program=program;
	}
	
	/**
	Connect to the GMP program and open pipe streams to it (mainly In
	and Out). Then start the communication thread.
	*/
	public void connect ()
		throws IOException
	{	Runtime R=Runtime.getRuntime();
		P=R.exec(Program);
		In=P.getInputStream();
		Err=P.getErrorStream();
		Out=P.getOutputStream();
		new Thread(this).start();
	}
	
	/**
	Send a single command to Out.
	@param mine See the send() function for this.
	@param c The command number (see the answer() function).
	@param a A command argument.
	*/
	public synchronized void send (boolean mine, boolean his, int c, int a)
		throws IOException
	{	int b1=(mine?1:0);
		b1|=(his?2:0);
		int b3=makeCommandByte1(c,a);
		int b4=makeCommandByte2(a);
		int checksum=computeChecksum(b1,b3,b4);
		Out.write((byte)b1);
		Out.write((byte)checksum);
		Out.write((byte)b3);
		Out.write((byte)b4);
		Out.flush();
		Dump.println("sent "+format(b1)+" "+format(checksum)
			+" "+format(b3)+" "+format(b4)+" = "+c+","+a);
	}

	byte makeCommandByte1 (int c, int a)
	{	a=a&0x000003FF;
		return (byte)((0x0080)|(c<<4)|(a>>7));
	}
	
	byte makeCommandByte2 (int a)
	{	return (byte)((0x0080)|(a&0x0000007F));
	}
	
	byte computeChecksum (int a, int b, int c)
	{	int cs=lower(a)+lower(b)+lower(c);
		return (byte)((0x0080)|cs);
	}
	
	int lower (int a)
	{	return a&0x007F;
	}
	
	boolean Sequence=false;
	
	/**
	Send a single command. Switches the Sequence bit with each
	command. The Sequence bit is resent in answers to the command.
	*/
	public void send (int c, int a)
		throws IOException
	{	Sequence=!Sequence;
		send(Sequence,His,c,a);
	}
	
	boolean End,MineAnswer=true,His=false;
	int Command,Argument;
	
	/**
	Waits for an answer by reading four bites and interpreting
	the input. The received command number is stored in the
	Command variable, and the argument in the Argument variable.
	The sequence bit is also stored. The answer bit is checked
	if this is really an answer to my last command.
	*/
	public boolean getAnswer ()
		throws IOException
	{	int b1=read();
		while ((b1&0x0080)!=0) b1=In.read();
		int cs=read();
		int b3=read();
		int b4=read();
		Dump.println("rcvd "+format(b1)+" "+format(cs)+
			" "+format(b3)+" "+format(b4));
		if ((b1&0x0080)!=0 || (cs&0x0080)==0 || (b3&0x0080)==0 || (b4&0x0080)==0)
			return false;
		if (computeChecksum((byte)b1,(byte)b3,(byte)b4)!=(byte)cs) return false;
		Command=(b3&0x0070)>>4;
		Argument=((b3&0x0007)<<7) |(b4&0x007F);
		boolean his=((b1&0x0001)!=0);
		if (his==His) return false;
		His=his;
		if (Command!=0)
		{	MineAnswer=(b1&0x0002)!=0;
			if (MineAnswer!=Sequence)
				throw new IOException("no answer");
		}
		return true;
	}
	
	int read ()
		throws IOException
	{	int i=In.read();
		if (i<0) throw new IOException("sudden death");
		return i;
	}
	
	static String format (int i)
	{	String s="";
		for (int k=0; k<8; k++)
		{	if (i%2!=0) s="1"+s;
			else s="0"+s;
			i=i>>1;
		}
		return s;
	}

	/**
	Send OK.
	*/	
	public void ok ()
		throws IOException
	{	send(Sequence,His,0,0x03FF);
	}
	
	static public final int BLACK=2,WHITE=1;
	static public final int JAPANESE=1,SST=2;
	static public final int EVEN=1;
	
	/**
	Send a move.
	@param color The color of the move (BLACK,WHITE).
	@param pos The board position from 0 to 360.
	*/
	public void move (int color, int pos)
		throws IOException
	{	int c;
		if (color==BLACK) c=0;
		else c=0x0200;
		send(5,c|pos);
	}
	
	/**
	Take back n moves.
	@param n Number of moves to take back.
	*/
	public void takeback (int n)
		throws IOException
	{	send(6,n);
	}

	GMPInterface I=null;
	public void setGMPInterface (GMPInterface i) { I=i; }
	public void removeGMPInterface (GMPInterface i) { I=null; }

	/**
	<P>Automatically answer the incomming command. These commands may
	be questions, others, moves or OK. </P> 
	<P> Questions are answered in the following way: The game parameters
	are taken from the GMPInterface interface, which needs to provide
	the necessary information and is set by setGMPInterface. I have
	implemented ony answers to the necessary commands. Everything else
	is answered with OK. </P> 
	<P> A move or OK are sent directly to the GMPinterface. </P>
	*/
	public synchronized void answer ()
		throws IOException
	{	Dump.println(Command+" "+Argument);
		if (Command==3) // Questions
		{	switch (Argument)
			{	case 7 : // question for rule set
					if (I!=null) send(4,I.getRules());
					else send(4,1); 
					break;
				case 9 : // question for board size
					if (I!=null) send(4,I.getBoardSize());
					else send(4,19); 
					break;
				case 8 : // question for handicap
					if (I!=null) send(4,I.getHandicap());
					else send(4,1); 
					break;
				case 11 : // question for board color of myself
					if (I!=null) send(4,I.getColor());
					else send(4,WHITE);
					break;
				default : send(4,0); break;
			}
		}
		else if (Command==4) // Other commands
		{	if (I!=null) I.gotAnswer(Argument);
		}
		else if (Command==5) // Got move
		{	ok(); // acknowledge
			if (I!=null)
			{	int pos=Argument&0x01FF;
				if ((Argument&0x0200)!=0) I.gotMove(WHITE,pos);
				else I.gotMove(BLACK,pos);
			}
		}
		else if (Command==0) // OK
		{	if (I!=null) I.gotOk();
			Dump.println("Got OK");
		}
		else ok();
	}
	
	/**
	Start the IO thread. I.e., continually get something from the
	program, and auto treat it in the answer() function.
	*/
	public void run ()
	{	try
		{	while (true)
			{	getAnswer();
				answer();
			}
		}
		catch (Exception e) {}
	}
	
	public void doclose ()
	{	new GMPCloser(this);
	}

	/**
	Destroy the program and close streams.
	*/	
	public void destroy ()
	{	try
		{	P.destroy();
			P.waitFor();
			In.close();
			Out.close();
			Err.close();
		}
		catch (Exception e) {}
	}

	/**
	Test program "main", which tries to open a connection to gnugo.exe
	and communicates with the process until it dies.
	*/ 
	static public void main (String args[])
	{	Dump.terminal(true);
		try
		{	GMPConnector c=new GMPConnector("gnugo.exe");
			c.connect();
			c.P.waitFor();
		}
		catch (Exception e)
		{	System.out.println(e);
		}
	}
}
