package rene.gui;

import java.awt.*;
import java.awt.event.*;

/**
A TextField with a modifyable background and font. This is used in
DoActionListener interfaces.
*/

public class TextFieldAction extends TextField
{	protected ActionTranslator T;
	public TextFieldAction (DoActionListener t, String name, String s)
	{	super(s);
		if (Global.NormalFont!=null) setFont(Global.NormalFont);
		if (Global.Background!=null) setBackground(Global.Background);
		T=new ActionTranslator(t,name);
		addActionListener(T);
	}
	public TextFieldAction (DoActionListener t, String name)
	{	if (Global.NormalFont!=null) setFont(Global.NormalFont);
		if (Global.Background!=null) setBackground(Global.Background);
		T=new ActionTranslator(t,name);
		addActionListener(T);
	}
	public TextFieldAction (DoActionListener t, String name, int n)
	{	super(n);
		if (Global.NormalFont!=null) setFont(Global.NormalFont);
		if (Global.Background!=null) setBackground(Global.Background);
		T=new ActionTranslator(t,name);
		addActionListener(T);
	}
	public TextFieldAction (DoActionListener t, String name, String s, int n)
	{	super(s,n);
		if (Global.NormalFont!=null) setFont(Global.NormalFont);
		if (Global.Background!=null) setBackground(Global.Background);
		T=new ActionTranslator(t,name);
		addActionListener(T);
	}
	public void triggerAction ()
	{	T.trigger();
	}
}
