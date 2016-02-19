package jagoclient.gui;

import java.awt.*;

/**
This is thread is used to wait a while, before a component requests
a focus. Otherwise, flicker may occur because of a race for focus.
*/

public class FocusRequester extends Thread
{	Component C;
	static boolean Waiting=false;
	public FocusRequester (Component c)
	{	C=c;
		if (!Waiting) start();
	}
	public void run ()
	{	Waiting=true;
		C.requestFocus();
		try { sleep(1000); } catch (Exception e) {}
		Waiting=false;
	}
}