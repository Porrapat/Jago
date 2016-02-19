package rene.gui;

import java.awt.*;
import java.awt.event.*;

/**
A text Button with a midifyable Font. The button
may also be triggered by a keyboard return.
<P>
This button class is used in DoActionListener interfaces.
*/

public class ButtonAction extends Button
{   DoActionListener C;
    String Name;
    public ButtonAction (DoActionListener c, String s, String name)
    {   super(s);
        C=c; Name=name;
        addActionListener(new ActionTranslator(c,name));
        if (Global.NormalFont!=null) setFont(Global.NormalFont);
        if (Global.ControlBackground!=null) setBackground(Global.ControlBackground);
    }
    public ButtonAction (DoActionListener c, String s)
    {   this(c,s,s);
    }
}
