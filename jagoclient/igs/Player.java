package jagoclient.igs;

import java.io.*;

import jagoclient.Dump;
import jagoclient.igs.connection.*;

import rene.util.parser.*;

class PlayerSizeDistributor extends Distributor
{	Player P;
	public PlayerSizeDistributor (IgsStream in, Player p)
	{	super(in,22,0,true);
		P=p;
	}
	public void send (String c)
	{	P.receivesize(c);
	}
	public void finished ()
	{	P.sizefinished();
	}
}

/**
This is to process moves from the server. It will get the input
from a PlayerDistributor and process this input to get the moves
of the game.
<p>
One problem is that the size of the board can only be determined
by the status command. Thus Player will send a status command first
and parse the output to get the board size. Once it has the size,
it will send a moves command and get all moves of the game.
The status command is also used to determine several other information
about the game, such as the player names and the komi.
<p>
The next problem is that the moves may be out of order. If Player
gets a move that is later, it will neglect that move. This may happen
when the player requested a move list, but the server has not yet started
to send this list and sends a new move first. If Player gets a move
that had already be sent, it will take back moves. This may happen,
when an undo took place, and is the only way to determine undo.
*/

public class Player
{	IgsGoFrame GF;
	IgsStream In;
	PrintWriter Out;
	int N,L;
	int Expected,Got;
	PlayDistributor PD;
	PlayerSizeDistributor PSD;
	boolean HaveSize;
	int BS=19;
	String Black,White;
	
	public Player (IgsGoFrame gf, IgsStream in, PrintWriter out,
		PlayDistributor pd)
	{	GF=gf; In=in; Out=out;
		N=-1; L=1;
		Expected=0; Got=0;
		PD=pd; PSD=null;
		HaveSize=false;
	}

	/**
	This is called from PlayerSizeDistributor, when it gets
	a line in answer to the status command, which Player sent
	to determine the board size.
	@see jagoclient.igs.PlayerSizeDistributor
	*/
	void receivesize (String s)
	{	Dump.println("Sizer got: "+s);
		if (L==1) White=s;
		else if (L==2) Black=s;
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
					i++;
				}
				if (i!=BS)
				{	if (i<5 || i>29) break;
					BS=i;
				}
				else break;
			}
		}
		L++;
	}

	/**
	This is called by PlayerSizeDistributor at the end of the
	status information. The method will send a moves command
	to retrieve the previous moves.
	*/
	public void sizefinished ()
	{	Dump.println("Sizer has size "+BS);
		HaveSize=true;
		GF.doboardsize(BS);
		setinformation();
		Out.println("moves "+N);
	}

	/**
	This is the main method to receive a move.
	However, if the size has not been determined yet, a
	new PlayerSizeDistributor will be opened and a status
	command will be sent to the server.
	<p>
	The move is parsed and the result is sent to the
	GoFrame. The move can be
	<ul>
	<li> a normal black or white move (e.g. H4)
	<li> a handicap setup (HANDICAP x)
	<li> a pass (PASS)
	</ul>
	<P>
	The server sends a line with the game number in front of each
	move. This line starts with "Game" and the rest of the line
	contains the times left for both players and other game information.
	This is interpret by the settime method of the IgsGoFrame.
	<p>
	The server abuses the command number 7 to send other information.
	One is the "{Game ..." comment line and the other are Kibitz
	outputs.
	@see jagoclient.igs.IgsGoFrame#settime
	*/
	void receive (String s)
	{	if (!HaveSize) // get board size first of all
		{	if (PSD==null)
			{	PSD=new PlayerSizeDistributor(In,this);
				Out.println("status "+N);
			}
			return;
		}
		// we have the board size, start parsing for moves
		StringParser p;
		int nu,i,j;
		Dump.println("Player("+N+"): "+s);
		p=new StringParser(s);
		p.skipblanks();
		if (!p.isint()) // s does not start with a number
		{	if (s.startsWith("Game")) GF.settime(s); // time information
			else if (s.startsWith("{Game")) GF.addComment(s); // comments
			else if (s.startsWith("Kibitz"))  // Kibitz output
			{   try { s=s.substring(7); } catch (Exception e) {}
			    GF.addComment(s);
			}
			else if (s.startsWith("Removing @")) // stone removals at game end
			{	p.skip("Removing @");
				p.skipblanks();
				String m=p.parseword();
				if (m.length()<2) return;
				i=m.charAt(0)-'A';
				if (i>=9) i--;
				try
				{	j=Integer.parseInt(m.substring(1))-1;
				}
				catch (NumberFormatException e)
				{	j=-1;
				}
				if (i<0 || j<0) return;
				Dump.println("Removing "+i+","+j);
				GF.remove(i,BS-1-j);
			}
			return;
		}
		// so p starts with a number
		nu=p.parseint('('); // get the move number
		if (p.error()) return;
		// test, if the move was expected
		if (Expected<nu && Got==0)
		{	Dump.println("Player denies number "+nu+" expecting "+Expected);
			Got=nu;
			return;
		}
		else if (Expected>nu)
		{	GF.undo(Expected-nu);
			Expected=nu;
		}
		// yes, the move was expected or the board was corrected
		// so that the move is the current one
		Expected=nu+1;
		p.skipblanks();
		// check for (W) or (B)
		p.skip("(");
		String c=p.parseword(')');
		if (p.error()) return;
		p.skip(")");
		p.skipblanks();
		p.skip(":");
		String m=p.parseword();
		// check for the move
		if (m.length()<2) return;
		if (m.equals("Pass")) // move is pass
		{	GF.setpass();
			return;
		}
		if (m.equals("Handicap")) // move is a handicap setup
		{	int hn;
			p.skipblanks();
			hn=p.parseint();
			GF.handicap(hn);
			return;
		}
		// move is a move (hopefully)
		i=m.charAt(0)-'A';
		if (i>=9) i--; // omit 'J'
		try
		{	j=Integer.parseInt(m.substring(1))-1;
		}
		catch (NumberFormatException e)
		{	j=-1;
		}
		if (i<0 || j<0) return;
		// send the move to the GoFrame
		Dump.println("Player interpreted: "+c+" "+i+","+j);
		if (c.equals("W")) GF.white(i,BS-1-j);
		else GF.black(i,BS-1-j);
	}

	void remove ()
	{	Dump.println("Player("+N+") has ended");
		if (PD!=null) PD.unchain();
		PD=null;
	}

	void game (int n)
	{	N=n;
	}

	/**
	This method is called from the PlayerDistributor to make a move, which
	should be sent to the server. Some servers allow client to
	pass the actual used time in seconds.
	*/
	public void set (int i, int j, int sec)
	{	if (i>=8) i++;
		char c[]=new char[1];
		c[0]=(char)('a'+i);
		String s=new String(c)+(BS-j);
		switch (GF.CF.MoveStyle)
		{   case Connection.MOVE_TIME :
		        s=s+" "+sec; break;
        	case Connection.MOVE_N_TIME :
		        s=s+" "+N+" "+sec; break;
		}
		Out.println(s);
		Dump.println("***> Player sends "+s);
	}

	/**
	Called by the PlayerDistributor to pass.
	*/
	void pass ()
	{	Out.println("pass");
		Dump.println("***> Player passes");
	}

	/**
	Called by the PlayerDistributor to refresh the move list.
	*/
	void refresh ()
	{	Got=Expected; Expected=0;
		Out.println("moves "+N);
	}

	boolean started ()
	{	return Got<=Expected;
	}

	/**
	This is used to set several information items in the GoFrame,
	which are received from the server as reply to the status command.
	*/
    public void setinformation ()
    {	StringParser p;
    	p=new StringParser(Black);
    	String BlackPlayer=p.parseword();
    	String BlackRank=p.parseword();
    	p.parseword(); p.parseword(); p.parseword(); p.parseword();
    	String Komi=p.parseword();
    	String Handicap=p.parseword();
    	p=new StringParser(White);
    	String WhitePlayer=p.parseword();
    	String WhiteRank=p.parseword();
    	GF.setinformation(BlackPlayer,BlackRank,WhitePlayer,WhiteRank,Komi,Handicap);
    }
}

