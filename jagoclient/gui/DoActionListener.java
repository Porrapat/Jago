package jagoclient.gui;

/**
An interface to simplify action processing in CloseWindow and
CloseDialog.
*/

public interface DoActionListener
{   void doAction (String o);
    void itemAction (String o, boolean flag);
}