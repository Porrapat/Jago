package jagoclient.board;

import java.awt.*;

import jagoclient.gui.*;
import jagoclient.Global;

public class BigTimerLabel extends BigLabel
{	int White=0,Black=0,Col=0,MWhite=-1,MBlack=-1;
	public BigTimerLabel ()
	{	super(Global.BigMonospaced);
	}
	static char a[]=new char[32];
	public void drawString (Graphics g, int x, int y, FontMetrics fm)
	{	int delta=fm.charWidth('m')/4;
		if (Global.BigMonospaced!=null) g.setFont(Global.BigMonospaced);
		if (White<0) g.setColor(Color.blue);
		else if (White<30 && Col<0) g.setColor(Color.red);
		else if (White<60 && Col<0) g.setColor(Color.red.darker());
		else if (Col<0) g.setColor(Color.green.darker());
		else g.setColor(Color.black);
		int n=OutputFormatter.formtime(a,White);
		g.drawChars(a,0,n,x,y);
		x+=fm.charsWidth(a,0,n)+delta;
		g.setFont(Global.Monospaced);
		if (MWhite>=0)
		{	a[0]=(char)('0'+(MWhite%100)/10);
			a[1]=(char)('0'+MWhite%10);
		}
		else a[0]=a[1]=' ';
		g.setColor(Color.black);
		g.drawChars(a,0,2,x,y);
		x+=fm.charsWidth(a,0,2)+delta;
		if (Global.BigMonospaced!=null) g.setFont(Global.BigMonospaced);
		if (Black<0) g.setColor(Color.blue);
		else if (Black<30 && Col>0) g.setColor(Color.red);
		else if (Black<60 && Col>0) g.setColor(Color.red.darker());
		else if (Col>0) g.setColor(Color.green.darker());
		else g.setColor(Color.black);
		n=OutputFormatter.formtime(a,Black);
		g.drawChars(a,0,n,x,y);
		x+=fm.charsWidth(a,0,n)+delta;
		g.setFont(Global.Monospaced);
		if (MBlack>=0)
		{	a[0]=(char)('0'+(MBlack%100)/10);
			a[1]=(char)('0'+MBlack%10);
		}
		else a[0]=a[1]=' ';
		g.setColor(Color.black);
		g.drawChars(a,0,2,x,y);
		if (Global.BigMonospaced!=null) g.setFont(Global.BigMonospaced);
	}
	public void setTime (int w, int b, int mw, int mb, int col)
	{	White=w; Black=b; MWhite=mw; MBlack=mb; Col=col;
	}
}
