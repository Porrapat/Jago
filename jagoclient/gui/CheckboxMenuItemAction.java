package jagoclient.gui;

import java.awt.*;
import java.awt.event.*;

import jagoclient.Global;

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
Similar to ChoiceAction, but for checkboxes in menus.
@see jagoclient.gui.ChoiceAction
*/

public class CheckboxMenuItemAction extends CheckboxMenuItem
{   public CheckboxMenuItemAction (DoActionListener c, String s)
    {   super(s);
        addItemListener(new CheckboxTranslator(this,c,s));
        setFont(Global.SansSerif);
    }
}
