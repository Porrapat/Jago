package rene.gui;

import java.awt.*;
import java.awt.event.*;

/**
A MenuItem with modifyable font.
<p>
This it to be used in DoActionListener interfaces.
*/

class MenuItemActionTranslator implements ActionListener
{	String S;
	DoActionListener C;
	public MenuItemActionTranslator (DoActionListener c, String s)
	{	S=s; C=c;
	}
	public void actionPerformed (ActionEvent e)
	{	C.doAction(S);
	}
}

public class MenuItemAction extends MyMenuItem
{   MenuItemActionTranslator MIT;
	public MenuItemAction (DoActionListener c, String s, String st)
    {   super(s);
        addActionListener(MIT=new MenuItemActionTranslator(c,st));
    }
	public MenuItemAction (DoActionListener c, String s)
	{	this(c,s,s);
	}
	public void setString (String s)
	{	MIT.S=s;
	}
}
