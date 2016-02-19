package jagoclient.gui;

import java.awt.*;
import java.awt.event.*;

import jagoclient.Global;

class CheckboxActionTranslator implements ItemListener
{   DoActionListener C;
    String S;
    public Checkbox CB;
    public CheckboxActionTranslator
        (Checkbox cb, DoActionListener c, String s)
    {   C=c; S=s; CB=cb;
    }
    public void itemStateChanged (ItemEvent e)
    {   C.itemAction(S,CB.getState());
    }
}

/**
Similar to ChoiceAction, but for checkboxes.
@see jagoclient.gui.ChoiceAction
*/

public class CheckboxAction extends Checkbox
{   public CheckboxAction (DoActionListener c, String s)
    {   super(s);
        addItemListener(new CheckboxActionTranslator(this,c,s));
        setFont(Global.SansSerif);
   }
    public CheckboxAction (DoActionListener c, String s, String h)
    {   super(s);
        addItemListener(new CheckboxActionTranslator(this,c,h));
    }
}
