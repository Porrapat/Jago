package jagoclient.igs;

import java.awt.*;

/**
This interface is used isolate the board from the comment window.
*/

public interface OutputListener
{   public void append (String s);
    public void append (String s, Color c);
}