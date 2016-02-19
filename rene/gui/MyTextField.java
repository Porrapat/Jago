package rene.gui;

import java.awt.*;
import java.awt.event.*;

/**
A TextField with a modifyable background and font.
*/

public class MyTextField extends TextField
{	public MyTextField (String s, int n)
	{	super(s,n);
		if (Global.NormalFont!=null) setFont(Global.NormalFont);
	}
	public MyTextField (String s)
	{	super(s);
		if (Global.NormalFont!=null) setFont(Global.NormalFont);
	}
	public MyTextField ()
	{	if (Global.NormalFont!=null) setFont(Global.NormalFont);
	}
}
