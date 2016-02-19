package rene.gui;

import java.awt.*;

public class MyCheckboxMenuItem extends CheckboxMenuItem
{	public MyCheckboxMenuItem (String s)
	{	super(s);
		if (Global.NormalFont!=null) setFont(Global.NormalFont);
	}
}
