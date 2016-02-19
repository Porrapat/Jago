package jagoclient.igs;

/**
The distributor for status reports (command number 22).
Reports output to a Status object.
@see jagoclient.igs.Status
*/

public class StatusDistributor extends Distributor
{	Status P;
	public StatusDistributor (IgsStream in, Status p)
	{	super(in,22,0,true);
		P=p;
	}
	public void send (String c)
	{	P.receive(c);
	}
	public void finished ()
	{	P.finished();
	}
}
