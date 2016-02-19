package rene.gui;

import java.awt.*;

public class MyPanel extends Panel
{	public MyPanel ()
	{	if (Global.ControlBackground!=null)
			setBackground(Global.ControlBackground);
		repaint();
	}
	public void paint (Graphics g)
	{	super.paint(g);
		getToolkit().sync();
	}
}
