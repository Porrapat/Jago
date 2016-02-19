package rene.gui;

import java.awt.*;
import java.awt.event.*;

public class MyList extends java.awt.List
	implements KeyListener
{	public MyList (int n)
	{	super(n);
		if (Global.NormalFont!=null) setFont(Global.NormalFont);
		if (Global.Background!=null) setBackground(Global.Background);
		addKeyListener(this);
	}
	public void keyPressed (KeyEvent e) {}
	public void keyReleased (KeyEvent e)
	{	if (e.getKeyCode()==KeyEvent.VK_SPACE)
		{	processActionEvent(new ActionEvent(this,
				ActionEvent.ACTION_PERFORMED,""));
		}
	}
	public void keyTyped (KeyEvent e) {}
}
