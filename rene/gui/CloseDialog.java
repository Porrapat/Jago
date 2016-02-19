package rene.gui;

import java.awt.*;
import java.awt.event.*;

/**
A dialog, which can be closed by clicking on the close window
field (a cross on the top right corner in Windows 95), or by
pressing the escape key.
<p>
Moreover, the dialog is a DoActionListener, which makes it possible
to use the simplified TextFieldAction etc.
*/

public class CloseDialog extends Dialog
    implements WindowListener, ActionListener, DoActionListener, KeyListener,
    FocusListener
{	boolean Dispose=true;
	public boolean Aborted=false;	

	public CloseDialog (Frame f, String s, boolean modal)
	{	super(f,s,modal);
		if (Global.ControlBackground!=null)
			setBackground(Global.ControlBackground);
	    addWindowListener(this);
	    addKeyListener(this);
	    addFocusListener(this);
	}

	public void windowActivated (WindowEvent e) {}
	public void windowClosed (WindowEvent e) {}
	public void windowClosing (WindowEvent e)
	{   if (close()) doclose();
	}
	public void windowDeactivated (WindowEvent e) {}
	public void windowDeiconified (WindowEvent e) {}
	public void windowIconified (WindowEvent e) {}
	public void windowOpened (WindowEvent e) {}

	/**
	@return true if the dialog is closed.
	*/
	public boolean close ()
	{	return true;
	}

	/**
	Calls close(), when the escape key is pressed.
	@return true if the dialog may close.
	*/
	public boolean escape ()
	{	return close();
	}

	public void actionPerformed (ActionEvent e)
	{   doAction(e.getActionCommand());
	}
	public void doAction (String o)
	{   if ("Close".equals(o) && close())
		{	Aborted=true;
			doclose();
		}
	}
	public void itemAction (String o, boolean flag) {}

	public void keyPressed (KeyEvent e)
	{	if (e.getKeyCode()==KeyEvent.VK_ESCAPE && escape()) doclose();
	}
	public void keyReleased (KeyEvent e) {}
	public void keyTyped (KeyEvent e) {}

	/**
	Closes the dialog. This may be used in subclasses to
	do some action. Then call super.doclose()
	*/
	public void doclose ()
	{	setVisible(false); 
		if (Dispose) dispose();
	}

	public void center (Frame f)
	{	Dimension 
			si=f.getSize(),
			d=getSize(),
			dscreen=getToolkit().getScreenSize();
		Point lo=f.getLocation();
		int x=lo.x+si.width/2-d.width/2;
		int y=lo.y+si.height/2-d.height/2;
		if (x+d.width>dscreen.width) x=dscreen.width-d.width-10;
		if (x<10) x=10;
		if (y+d.height>dscreen.height) y=dscreen.height-d.height-10;
		if (y<10) y=10;
		setLocation(x,y);
	}
	static public void center (Frame f, Dialog dialog)
	{	Dimension 
			si=f.getSize(),
			d=dialog.getSize(),
			dscreen=f.getToolkit().getScreenSize();
		Point lo=f.getLocation();
		int x=lo.x+si.width/2-d.width/2;
		int y=lo.y+si.height/2-d.height/2;
		if (x+d.width>dscreen.width) x=dscreen.width-d.width-10;
		if (x<10) x=10;
		if (y+d.height>dscreen.height) y=dscreen.height-d.height-10;
		if (y<10) y=10;
		dialog.setLocation(x,y);
	}
	public void centerOut (Frame f)
	{	Dimension si=f.getSize(),d=getSize(),
			dscreen=getToolkit().getScreenSize();
		Point lo=f.getLocation();
		int x=lo.x+si.width-getSize().width+20;
		int y=lo.y+si.height/2+40;
		if (x+d.width>dscreen.width) x=dscreen.width-d.width-10;
		if (x<10) x=10;
		if (y+d.height>dscreen.height) y=dscreen.height-d.height-10;
		if (y<10) y=10;
		setLocation(x,y);
	}
	public void center ()
	{	Dimension d=getSize(),dscreen=getToolkit().getScreenSize();
		setLocation((dscreen.width-d.width)/2,
			(dscreen.height-d.height)/2);
	}
	/**
	Override to set the focus somewhere.
	*/
	public void focusGained (FocusEvent e) {}
	public void focusLost (FocusEvent e) {}

	/**
	Note window position in Global.
	*/
	public void noteSize (String name)
	{	Dimension d=getSize();
		Global.setParameter(name+".w",d.width);
		Global.setParameter(name+".h",d.height);
	}

	/**
	Set window position and size.
	*/
	public void setSize (String name)
	{	if (!Global.haveParameter(name+".w")) pack();
		else
		{	Dimension d=getSize();
			int w=Global.getParameter(name+".w",d.width);
			int h=Global.getParameter(name+".h",d.height);
			setSize(w,h);
		}
	}
	
	/**
	This inihibits dispose(), when the dialog is closed.
	*/
	public void setDispose (boolean flag)
	{	Dispose=flag;
	}
	
	public boolean isAborted ()
	{	return Aborted;
	}
}
