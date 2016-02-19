package rene.gui;

import java.awt.*;

/**
A List class with a specified font and background.
Designed for DoActionListener objects.
*/

public class ListAction extends List
{	public ListAction (DoActionListener c, String name)
	{	addActionListener(new ActionTranslator(c,name));
        if (Global.NormalFont!=null) setFont(Global.NormalFont);
		if (Global.Background!=null) setBackground(Global.Background);
	}
	public ListAction (DoActionListener c, String name, int n)
	{	super(n);	
		addActionListener(new ActionTranslator(c,name));
        if (Global.NormalFont!=null) setFont(Global.NormalFont);
		if (Global.Background!=null) setBackground(Global.Background);
	}
}
