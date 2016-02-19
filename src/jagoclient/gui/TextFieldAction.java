package jagoclient.gui;

import java.awt.*;
import java.awt.event.*;

import jagoclient.Global;

/**
A TextField. The background and the font are set from global
properties. The class uses a TextFieldActionListener to listen
to returns and notify the DoActionListener passing the a
string (name) to its doAction method.
@see TextFieldActionListener
*/

public class TextFieldAction extends TextField
{	TextFieldActionListener T;
    public TextFieldAction (DoActionListener t, String name, String s)
	{	super();
		setBackground(Global.gray);
		setFont(Global.SansSerif);
		T=new TextFieldActionListener(t,name);
		addActionListener(T);
		setText(s);
	}
	public TextFieldAction (DoActionListener t, String name)
	{	setBackground(Global.gray);
		setFont(Global.SansSerif);
		T=new TextFieldActionListener(t,name);
		addActionListener(T);
	}
	public TextFieldAction (DoActionListener t, String name, int n)
	{	super(n);
		setBackground(Global.gray);
		setFont(Global.SansSerif);
		T=new TextFieldActionListener(t,name);
		addActionListener(T);
	}
	public TextFieldAction (DoActionListener t, String name, String s, int n)
	{	super(s,n);
		setFont(Global.SansSerif);
		setBackground(Global.gray);
		T=new TextFieldActionListener(t,name);
		addActionListener(T);
	}
}
