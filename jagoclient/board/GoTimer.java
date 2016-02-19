package jagoclient.board;

import jagoclient.StopThread;

/**
A timer for the goboard. It will call the alarm method of the
board in regular time intervals. This is used to update the
timer.
@see jagoclient.board.TimedBoard
*/

public class GoTimer extends StopThread
{	public long Interval;
	TimedBoard B;
	public GoTimer (TimedBoard b, long i)
	{	Interval=i; B=b;
		start();
	}
	public void run ()
	{	try 
		{	while (!stopped())
			{	sleep(Interval);
				B.alarm();
			}
		}
		catch (Exception e)
		{}
	}
}
