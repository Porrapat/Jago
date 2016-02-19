package jagoclient.igs.games;

import jagoclient.igs.*;

/**
The distributor to receive the games from the server.
It assumes that there is already an open games frame.
This must be so, because sometimes the games are displayed
in an old GamesFrame via refresh.
*/

public class GamesDistributor extends Distributor
{	GamesFrame P;
	public GamesDistributor (IgsStream in, GamesFrame p)
	{	super(in,7,0,true);
		P=p;
	}
	public void send (String c)
	{	P.receive(c);
	}
	public void allsended ()
	{	P.allsended();
	}
}
