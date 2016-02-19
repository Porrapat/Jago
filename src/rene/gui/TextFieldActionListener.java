package rene.gui;

import java.awt.*;
import java.awt.event.*;

class TextFieldActionListener implements ActionListener
{   DoActionListener C;
    String Name;
    public TextFieldActionListener (DoActionListener c, String name)
    {   C=c; Name=name;
    }
    public void actionPerformed (ActionEvent e)
    {   C.doAction(Name);
    }
}

