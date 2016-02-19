package jagoclient.board;

import java.awt.*;

import jagoclient.Global;

/**
A GoFrame for local boards (not connected).
<b>Note:</b> This will exit the program, when closed.
*/

public class LocalGoFrame extends GoFrame
{	public LocalGoFrame (Frame f, String s)
	{	super(f,s);
	}
	public void doclose ()
	{	super.doclose();
		Global.writeparameter("go.cfg");
		if (!Global.isApplet()) System.exit(0);
	}
}

