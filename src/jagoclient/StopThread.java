package jagoclient;

/**
A thread with a Stopped flag.
*/

public class StopThread extends Thread
{	boolean Stopped=false;
	public boolean stopped ()
	{	return Stopped;
	}
	public void stopit ()
	{	Stopped=true;
	}
}