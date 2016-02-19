package jagoclient.gui;

import java.awt.*;
import java.awt.event.*;

/**
This is a callback class to act as an ActionListener, which calls
back a DoActionListener on any action passing the string name to
its doAction method. 
@see DoActionListener
@see TextFieldAction
@see jagoclient.gui.CloseFrame#doAction
@see jagoclient.gui.CloseDialog#doAction
*/

class TextFieldActionListener 
	implements ActionListener
{   DoActionListener C;
    String Name;
    public TextFieldActionListener (DoActionListener c, String name)
    {   C=c; Name=name;
    }
    public void actionPerformed (ActionEvent e)
    {   C.doAction(Name);
    }
}

