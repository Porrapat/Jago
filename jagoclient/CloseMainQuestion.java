package jagoclient;

import java.awt.*;

import jagoclient.gui.*;
import jagoclient.dialogs.*;
import jagoclient.MainFrame;
import jagoclient.Global;

public class CloseMainQuestion extends Question
{	MainFrame G;
	public CloseMainQuestion (MainFrame g)
	{	super((CloseFrame)g,Global.resourceString("End_Application_"),
			Global.resourceString("Exit"),(CloseFrame)g,true);
		G=g;
		show();
	}
	public void tell (Question q, Object o, boolean f)
	{	q.setVisible(false); q.dispose();
	}
}
