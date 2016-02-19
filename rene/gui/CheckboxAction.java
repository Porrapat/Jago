package rene.gui;

import java.awt.*;
import java.awt.event.*;

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
A Checkbox with modifyable font.
<p>
To be used in DoActionListener interfaces.
*/

public class CheckboxAction extends Checkbox
{   public CheckboxAction (DoActionListener c, String s)
    {   super(s);
    	if (Global.NormalFont!=null) setFont(Global.NormalFont);
        addItemListener(new CheckboxActionTranslator(this,c,s));
    }
    public CheckboxAction (DoActionListener c, String s, String h)
    {   super(s);
    	if (Global.NormalFont!=null) setFont(Global.NormalFont);
        addItemListener(new CheckboxActionTranslator(this,c,h));
    }
}
