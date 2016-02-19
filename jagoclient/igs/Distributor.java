package jagoclient.igs;

import java.io.*;

/**
This class takes messages from IgsStream and handles it.
Most of the time, it has a client to send the message to.
Sometimes it will open a new client window.
<P>
The distributor has a number N, which is the command number it
is wating for. It has a second number G, which is used to store
additional information. E.g. PlayDistributor will store the
game number there. IgsStream can ask the distributor for these
numbers and determine, if it should send the message to this
distributor.
@see jagoclient.igs.IgsStream
*/

public class Distributor
{	int N; // number to expect
	int G; // game number, if applicable
	boolean Once; // needed only for one input
	IgsStream In;
	public boolean Playing;
	public Distributor (IgsStream in, int n, int game, boolean once)
	{	N=n; G=game;
		in.distributor(this);
		Once=once;
		In=in;
		Playing=false;
	}
	public int number () { return N; }
	public int game () { return G; }
	public void game (int n) {}
	public boolean once () { return Once; }
	public void send (String C) {}
	public void finished () {} // message for a once-distributor at end
	public void unchain ()
	{	In.unchain(this);
	}
	public void remove () {} // remove client
		// (called from IgsStream at connection end)
	public boolean blocked () { return false; }
	public boolean wantsmove () { return Playing; }
	public void set (int i, int j, int sec) {}
	public void pass () {}
	public void out (String s) { In.out(s); }
	public void allsended () {}
	public void refresh() {}
	public boolean started () { return false; }
	public boolean newmove () { return false; }
}

