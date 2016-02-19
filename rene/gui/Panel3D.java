package rene.gui;

import java.awt.*;

/**
Panel3D extends the Panel class with a 3D look.
*/

public class Panel3D extends Panel
{	Component C;
	/**
	Adds the component to the panel.
	This component is resized to leave 5 pixel on each side.
	*/
	public Panel3D (Component c)
	{	C=c;
		add(C);
		setBackground(C.getBackground());
	}
	public Panel3D (Component c, Color background)
	{	C=c;
		add(C);
		setBackground(background);
	}
	/**
	An empty 3D panel.
	*/
	public Panel3D ()
	{	C=null;
	}
	public void paint (Graphics g)
	{	g.setColor(getBackground());
		g.fill3DRect(0,0,getSize().width,getSize().height,true);
	}
	public void update (Graphics g)
	{	paint(g);
	}
	public void doLayout ()
	{	if (C!=null)
		{	C.setLocation(5,5);
			C.setSize(getSize().width-10,getSize().height-10);
			C.doLayout();
			Graphics g=getGraphics();
			if (g!=null) paint(g);
		}
		else super.doLayout();
	}
	public Dimension getPreferredSize ()
	{	Dimension d=C.getPreferredSize();
		return new Dimension(d.width+10,d.height+10);
	}
	public Dimension getMinimumSize ()
	{	Dimension d=C.getMinimumSize();
		return new Dimension(d.width+10,d.height+10);
	}
}
