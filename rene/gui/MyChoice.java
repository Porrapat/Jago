package rene.gui;

import java.awt.*;

public class MyChoice extends Choice
{	public MyChoice ()
	{	if (Global.NormalFont!=null) setFont(Global.NormalFont);
		if (Global.Background!=null) setBackground(Global.Background);
	}
}
