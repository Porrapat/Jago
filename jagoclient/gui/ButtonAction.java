package jagoclient.gui;

import java.awt.*;
import java.awt.event.*;

import jagoclient.Global;

/**
Similar to ChoiceAction but for buttons.
@see jagoclient.gui.ChoiceAction
*/

public class ButtonAction extends Button
{   DoActionListener C;
    String Name;
    ActionTranslator AT;
    public ButtonAction (DoActionListener c, String s, String name)
    {   super(s);
        C=c; Name=name;
	    addActionListener(AT=new ActionTranslator(c,name));
        setFont(Global.SansSerif);
    }
    public ButtonAction (DoActionListener c, String s)
    {   this(c,s,s);
    }
    
}
