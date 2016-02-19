package rene.gui;

import java.awt.*;
import java.awt.event.*;

/**
A translator for Actions.
*/

public class ActionTranslator implements ActionListener
{   String Name;
    DoActionListener C;
    public ActionTranslator (DoActionListener c, String name)
    {   Name=name; C=c;
    }
    public void actionPerformed (ActionEvent e)
    {   C.doAction(Name);
    }
    public void trigger ()
    {	C.doAction(Name);
    }
}
