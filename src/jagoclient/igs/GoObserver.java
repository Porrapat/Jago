package jagoclient.igs;

import java.io.*;

import jagoclient.Dump;
import jagoclient.Global;

import rene.util.parser.*;

/**
This is a local class to determine the size of the board, when
the observer distributor has sent the status command.
*/

class ObserveSizeDistributor extends Distributor
{	GoObserver P;
	public ObserveSizeDistributor (IgsStream in, GoObserver p)
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
Local class, which will remove the observer after a certain delay.
*/

class ObserverCloser extends Thread
{	GoObserver GO;
	public ObserverCloser (GoObserver go)
	{	GO=go;
		start();
	}
	public void run ()
	{	try
		{	sleep(10000);
		}
		catch (InterruptedException e) {}
		GO.finishremove();
	}
}

/**
A GoObserver interprets server input for an observed game. It
uses an ObserverDistributor to get this iput from IgsStream.
<P>
Since there is no other way, GoObserver first starts an
ObserveSizeDistributor (private in GoObserver.java)
and sends the status command to the
server. The mere purpose of this is to get the size of
the board in the particular game.
<P>
After it got that information, it will start an ObserveDistributor
to get the moves of the game.
<P>
One problem is the undoing of moves. The server undoes moves, by resending
old moves. This has to be taken care of.
<P>
Moreover, when the user closes the window, the unobserve command will
be sent to the server. To make sure, the GoObserver waits 10 seconds
before it removes itself (otherwise, sudden moves could arrive, which
correspond to no observer and would open a plaing window, because
that is the way the server tells you about the re-load of a game).
Most of these inconveniences are caused by a dirty server protocol.
@see jagoclient.igs.Player
*/

public class GoObserver
{	IgsGoFrame GF;
	IgsStream In;
	PrintWriter Out;
	ObserveDistributor PD;
	int N,L,BS;
	int Expected,Got;
	String Black,White;
	boolean Observing;
	String Kibitz;
	boolean Closed;
	
	public GoObserver (IgsGoFrame gf, IgsStream in, PrintWriter out, int n)
	{	GF=gf; In=in; Out=out;
		new ObserveSizeDistributor(in,this);
		Out.println("status "+n);
		N=n;
		Expected=0; Got=0;
		L=1; BS=19;
		Observing=false;
		Kibitz="";
		Closed=false;
	}

	void sended ()
	{	if (!Observing) Out.println("observe "+N);
		Observing=true;
	}

	void receivesize (String s)
	{	if (L==1) White=s;
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

	public void sizefinished ()
	{	PD=new ObserveDistributor(In,this,N);
		GF.distributor(PD);
		GF.doboardsize(BS);
		setinformation();
		Out.println("observe "+N);
		Observing=true;
	}

	public void receive (String s)
	{	if (Closed || !Observing) return;
		StringParser p;
		int nu,i,j;
		Dump.println("Observed("+N+"): "+s);
		p=new StringParser(s);
		p.skipblanks();
		if (!p.isint())
		{	if (s.startsWith("Game")) GF.settime(s);
			else if (s.indexOf("Game")>0 && s.indexOf("Game")<4) 
				GF.addComment(s);
			else if (s.startsWith("Kibitz"))
			{	if (s.startsWith("Kibitz->"))
				{	StringParser ps=new StringParser(s);
					ps.skip("Kibitz->"); ps.skipblanks();
					GF.addComment(Kibitz+": "+ps.upto((char)0));
					Kibitz="";
				}
				else
				{	StringParser ps=new StringParser(s);
					ps.skip("Kibitz"); ps.skipblanks();
					Kibitz=ps.upto(':');
				}
			}
			return;
		}
		nu=p.parseint('(');
		if (p.error()) return;
		if (Expected<nu && Got==0)
		{	Dump.println("Oberver denies number "+nu+" expecting "+Expected);
			Out.println("moves "+N);
			Got=nu;
			return;
		}
		else if (Expected>nu)
		{	GF.undo(Expected-nu);
			Expected=nu;
		}
		Expected=nu+1;
		p.skipblanks();
		p.skip("(");
		String c=p.parseword(')');
		if (p.error()) return;
		p.skip(")");
		p.skipblanks();
		p.skip(":");
		String m=p.parseword();
		if (m.length()<2) return;
		if (m.equals("Pass"))
		{	GF.setpass();
			return;
		}
		if (m.equals("Handicap"))
		{	int hn;
			p.skipblanks();
			hn=p.parseint();
			GF.handicap(hn);
			return;
		}
		i=m.charAt(0)-'A';
		if (i>=9) i--;
		try
		{	j=Integer.parseInt(m.substring(1))-1;
		}
		catch (NumberFormatException e)
		{	j=-1;
		}
		if (i<0 || j<0) return;
		Dump.println("GoObserver interpreted: "+c+" "+i+","+j);
		if (c.equals("W")) GF.white(i,BS-1-j);
		else GF.black(i,BS-1-j);
	}

	public void finished ()
	{	Dump.println("GoObserver("+N+") is finished");
	}

	public void remove ()
	{	Dump.println("GoObserver("+N+") has ended, unobserving");
		Closed=true;
		Out.println("unobserve "+N);
		new ObserverCloser(this);
	}
	
	public void finishremove ()
	{	if (PD!=null) PD.unchain();
		PD=null;
	}

	public void refresh ()
	{	Got=Expected; Expected=0;
		Out.println("moves "+N);
	}

	public boolean started ()
	{	return Observing && (Got<=Expected);
	}

	public void set (int i, int j)
	{	if (i>=8) i++;
		char c[]=new char[1];
		c[0]=(char)('a'+i);
		Out.println(new String(c)+(BS-j));
		Dump.println("***> Player sends "+new String(c)+(BS-j));
	}

	public void pass ()
	{	Out.println("pass");
		Dump.println("***> Player passes");
	}

    public boolean newmove ()
    {   return Expected>Got;
    }
    
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

