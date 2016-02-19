package jagoclient.gui;

import java.awt.*;

import jagoclient.Global;

/**
A menu with a specified font.
*/

public class MyMenu extends Menu
{   public MyMenu (String l)
    {   super(l);
        setFont(Global.SansSerif);
    }
}