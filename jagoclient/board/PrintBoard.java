package jagoclient.board;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
Prints the Board in a separate Thread. This class does gets a copy of
the board so that is can print asynchronously. However, it will display the
print dialog in its constructor (i.e. blocking the event handling),
but do the printing in in a separate thread.
<P>
As to Java 1.1 the printing is not very beautiful.
*/

class PrintBoard extends Thread 
	implements ImageObserver
{   Graphics g;
    PrintJob job;
    int S,Range;
    Position P;
    Font font;
    FontMetrics fontmetrics;
    Frame F;
    public PrintBoard (Position P1, int Range1, Frame f)
    {   F=f;
		Toolkit toolkit=F.getToolkit();
		Properties printrefs=new Properties();
		job=toolkit.getPrintJob(F,"Jago",printrefs);
        S=P1.S; P=P1;
        Range=Range1;
		if (job!=null)
		{   g=job.getGraphics();
		    start();
		}
    }
    
    public void run ()
	{   int W=job.getPageDimension().width,H=job.getPageDimension().height;
	    int D=W*2/3/S,O=W/6;
	    if (D%2!=0) D++;
	    font=new Font("SansSerif",Font.BOLD,D/2);
		g.setFont(font);
	    fontmetrics=g.getFontMetrics(font);
		g.setColor(Color.black);
		int i,j;
		// Draw lines
		int y=O;
		int h=fontmetrics.getAscent()/2-1;
		for (i=0; i<S; i++)
		{	String s=""+(S-i);
			int w=fontmetrics.stringWidth(s)/2;
			g.drawString(s,O+S*D+D/2-w,y+D/2+h);
			y+=D;
		}
		int x=O;
		char a[]=new char[1];
        for (i=0; i<S; i++)
    	{	j=i;
    		if (j>7) j++;
    		a[0]=(char)('A'+j);
    		String s=new String(a);
    		int w=fontmetrics.stringWidth(s)/2;
    		g.drawString(s,x+D/2-w,O+S*D+D/2+h);
    		x+=D;
    	}
    	for (i=0; i<S; i++)
    	    for (j=0; j<S; j++)
    	    {   update1(g,O+D*i,O+D*j,i,j,D);
    	    }
    	g.dispose();
	    job.end();
	}

	public boolean imageUpdate (Image i, int f, int x, int y, int w, int h)
	{   if ((f&ImageObserver.ALLBITS)!=0)
	    {   notify();
	        return false;
	    }
	    return true;
	}

	void hand1 (Graphics g, int x, int y, int D)
	{	int s=D/10;
		if (s<2) s=2;
		g.fillRect(x+D/2-s,y+D/2-s,2*s+1,2*s+1);
	}

	/** update the field (i,j) in the print.
	in dependance of the board position P.
	display the last move mark, if applicable.
	*/
	public void update1 (Graphics g, int x, int y, int i, int j, int D)
	{	String hs;
		char c[]=new char[1];
		int n;
		g.setColor(Color.black);
		if (i>0) g.drawLine(x+0,y+D/2,x+D/2,y+D/2);
		if (i<S-1) g.drawLine(x+D/2,y+D/2,x+D,y+D/2);
		if (j>0) g.drawLine(x+D/2,y,x+D/2,y+D/2);
		if (j<S-1) g.drawLine(x+D/2,y+D/2,x+D/2,y+D);
		int i1,j1;
		if (S==19) // handicap markers
		{	int k=S/2-3;
			for (i1=3; i1<S; i1+=k)
				for (j1=3; j1<S; j1+=k)
					if (i==i1 && j==j1) hand1(g,x,y,D);
		}
		else if (S>=11) // handicap markers
		{	if (S>=15 && S%2==1)
			{	int k=S/2-3;
				for (i1=3; i1<S; i1+=k)
					for (j1=3; j1<S; j1+=k)
						if (i==i1 && j==j1) hand1(g,x,y,D);
			}
			else
			{	if (i==3 && j==3) hand1(g,x,y,D);
				if (i==S-4 && j==3) hand1(g,x,y,D);
				if (i==3 && j==S-4) hand1(g,x,y,D);
				if (i==S-4 && j==S-4) hand1(g,x,y,D);
			}
		}
		if (P.color(i,j)>0)
		{	g.setColor(Color.black);
			g.fillOval(x+1,y+1,D-3,D-3);
		}
		else if (P.color(i,j)<0)
		{	g.setColor(Color.white);
			g.fillOval(x+1,y+1,D-3,D-3);
			g.setColor(Color.black);
			g.drawOval(x+1,y+1,D-3,D-3);
		}
		if (P.marker(i,j)!=Field.NONE)
		{	if (P.color(i,j)>0) g.setColor(Color.white);
		    else g.setColor(Color.black);
			int h=D/4;
			switch (P.marker(i,j))
			{	case Field.CIRCLE :
					g.drawOval(x+D/2-h,y+D/2-h,2*h,2*h);
					break;
				case Field.CROSS :
					g.drawLine(x+D/2-h,y+D/2-h,x+D/2+h,y+D/2+h);
					g.drawLine(x+D/2+h,y+D/2-h,x+D/2-h,y+D/2+h);
					break;
				case Field.TRIANGLE :
					g.drawLine(x+D/2,y+D/2-h,x+D/2-h,y+D/2+h);
					g.drawLine(x+D/2,y+D/2-h,x+D/2+h,y+D/2+h);
					g.drawLine(x+D/2-h,y+D/2+h,x+D/2+h,y+D/2+h);
					break;
				default : g.drawRect(x+D/2-h,y+D/2-h,2*h,2*h);
			}
		}
		if (P.letter(i,j)!=0)
		{	if (P.color(i,j)>0) g.setColor(Color.white);
		    else g.setColor(Color.black);
			c[0]=(char)('a'+P.letter(i,j)-1);
			hs=new String(c);
			int w=fontmetrics.stringWidth(hs)/2;
			//int h=fontmetrics.getAscent()/2-1;
			int h=D/4;
			g.drawString(hs,x+D/2-w,y+D/2+h);
		}
    	if (P.haslabel(i,j))
		{	if (P.color(i,j)>0) g.setColor(Color.white);
		    else g.setColor(Color.black);
			hs=P.label(i,j);
			int w=fontmetrics.stringWidth(hs)/2;
			// int h=fontmetrics.getAscent()/2-1;
			int h=D/4;
			g.drawString(hs,x+D/2-w,y+D/2+h);
		}
		if (P.color(i,j)!=0 && Range>=0 && P.number(i,j)>Range)
		{	if (P.color(i,j)>0) g.setColor(Color.white);
		    else g.setColor(Color.black);
			hs=""+(P.number(i,j)%100);
			int w=fontmetrics.stringWidth(hs)/2;
			// int h=fontmetrics.getAscent()/2-1;
			int h=D/4;
			g.drawString(hs,x+D/2-w,y+D/2+h);
		}
	}

}
