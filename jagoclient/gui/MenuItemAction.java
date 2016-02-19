package jagoclient.gui;

import java.awt.*;
import java.awt.event.*;

import jagoclient.Global;

/**
A menu item with a specified font.
*/

public class MenuItemAction extends MenuItem
{   ActionTranslator AT;
	public MenuItemAction (DoActionListener c, String s, String name)
    {   super(s);
	    addActionListener(AT=new ActionTranslator(c,name));
        setFont(Global.SansSerif);
    }
    public MenuItemAction (DoActionListener c, String s)
    {	this(c,s,s);
    }
    public void setString (String s)
    {	AT.setString(s);
    }
}
