package jagoclient.gui;

import java.awt.*;

import jagoclient.Global;

/**
A TextField with a background and font as specified in the Global class.
*/

public class GrayTextField extends TextField
{	public GrayTextField (String s)
	{	super(s,25);
		setBackground(Global.gray);
		setFont(Global.SansSerif);
	}
	public GrayTextField ()
	{	super(25);
		setBackground(Global.gray);
		setFont(Global.SansSerif);
	}
	public GrayTextField (int n)
	{	super(n);
		setBackground(Global.gray);
		setFont(Global.SansSerif);
	}
}
