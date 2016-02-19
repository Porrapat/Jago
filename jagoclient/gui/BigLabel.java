package jagoclient.gui;

import java.awt.*;

import jagoclient.Global;

public class BigLabel extends Panel
{	Image I=null;
	Graphics GI;
	FontMetrics FM;
	int Offset;
	int W,H;
	Font F;
	public BigLabel (Font f)
	{	F=f;
		if (f!=null) setFont(f);
		FM=getFontMetrics(f);
	}
	public void paint (Graphics g)
	{	Dimension d=getSize();
		int w=d.width,h=d.height;
		if (I==null || w!=W || h!=H)
		{	W=w; H=h;
			I=createImage(W,H);
			if (I==null) return;
			GI=I.getGraphics();
			if (F!=null) GI.setFont(F);
			FM=GI.getFontMetrics();
			Offset=FM.charWidth('m')/2;
		}
		GI.setColor(Global.gray);
		GI.fillRect(0,0,W,H);
		GI.setColor(Color.black);
		drawString(GI,
			Offset,(H+FM.getAscent()-FM.getDescent())/2,FM);
		g.drawImage(I,0,0,W,H,this);
	}
	public void update (Graphics g)
	{	paint(g);
	}
	public void drawString (Graphics g, int x, int y, FontMetrics fm)
	{
	}
	public Dimension getPreferredSize ()
	{	return new Dimension(getSize().width,(FM.getAscent()+FM.getDescent())*3/2);
	}
	public Dimension getMinimumSize ()
	{	return getPreferredSize();
	}
}
