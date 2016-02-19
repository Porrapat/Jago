package jagoclient.board;

import java.awt.*;

/**
This panel contains two panels aside. The left panel is kept square.
*/

class CommentNavigationPanel extends Panel
{	Component C1,C2;
	public CommentNavigationPanel (Component c1, Component c2)
	{	C1=c1; C2=c2;
		add(C1);
		add(C2);
	}
	public void doLayout ()
	{	int w=getSize().width,h=getSize().height;
		int h2=w;
		if (h2>h/2) h2=h/2;
		C1.setSize(w,h-h2);
		C1.setLocation(0,0);
		C2.setSize(w,h2);
		C2.setLocation(0,h-h2);
		C1.doLayout();
		C2.doLayout();
	}
}

