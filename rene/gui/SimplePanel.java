package rene.gui;

import java.awt.*;

/**
A panel with two components side by side. The size of the components
is determined by the weights.
*/

public class SimplePanel extends MyPanel
{	Component C1,C2;
	double W;
	int IX=0,IY=0;
	public SimplePanel (Component c1, double w1, Component c2, double w2)
	{	C1=c1; C2=c2; W=w1/(w1+w2);
		add(C1);
		add(C2);
	}
	public void doLayout ()
	{	int w=(int)(getSize().width*W-3*IX);
		C1.setSize(w,getSize().height-2*IY);
		C1.setLocation(IX,IY);
		C2.setSize(getSize().width-3*IX-w,getSize().height-2*IX);
		C2.setLocation(w+2*IX,IY);
		C1.doLayout();
		C2.doLayout();
	}
	public Dimension getPreferredSize ()
	{	Dimension d1=C1.getPreferredSize(),d2=C2.getPreferredSize();
		return new Dimension(d1.width+d2.width,
			Math.max(d1.height,d2.height));
	}
	public void setInsets (int x, int y)
	{	IX=x; IY=y;
	}
}
