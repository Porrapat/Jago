package rene.gui;

import java.awt.*;
import java.awt.event.*;

class CheckboxTranslator implements ItemListener
{   DoActionListener C;
    String S;
    public CheckboxMenuItem CB;
    public CheckboxTranslator 
        (CheckboxMenuItem cb, DoActionListener c, String s)
    {   C=c; S=s; CB=cb;
    }
    public void itemStateChanged (ItemEvent e)
    {   C.itemAction(S,CB.getState());
    }
}

/**
A CheckboxMenuItem with modifyable font.
<p>
This is to be used in DoActionListener interfaces.
*/

public class CheckboxMenuItemAction extends CheckboxMenuItem
{   public CheckboxMenuItemAction (DoActionListener c, String s, String st)
    {   super(s);
        addItemListener(new CheckboxTranslator(this,c,st));
        if (Global.NormalFont!=null) setFont(Global.NormalFont);
    }
	public CheckboxMenuItemAction (DoActionListener c, String s)
	{	this(c,s,s);
	}
}
