package jagoclient.board;

import java.awt.*;
import java.awt.image.*;

import jagoclient.StopThread;
import jagoclient.Global;

/**
This is used to create an image of the empty bard at startup or when
a board is needed (request from Board via WoodPaint).
@see jagoclient.board.WoodPaint
*/

public class EmptyPaint extends StopThread
{	Board B;
	static int W,H;
	public static int Ox,Oy,D;
	static Color C;
	boolean Shadows;
	EmptyPaint (Board b, int w, int h, Color c, boolean shadows, int ox, int oy, int d)
	{	B=b; W=w; H=h; C=c; Shadows=shadows; Ox=ox; Oy=oy; D=d;
		start();
	}
	public void run ()
	{	try
		{	setPriority(getPriority()-1);
		}
		catch (Exception e) { System.out.println(e); }
		try { sleep(100); } catch (Exception e) {}
		createwood(this,B,W,H,C,Shadows,Ox,Oy,D);
		if (!stopped()) B.updateboard();
	}

	public static Image StaticImage=null,StaticShadowImage=null;

	/**
	Create an image of the wooden board. The component is used
	to create the image.
	*/
	static public void createwood (StopThread EPT, 
		Component comp, int w, int h, Color c, boolean shadows, int ox, int oy, int d)
	{	if (w==0 || h==0) return;
		StaticImage=StaticShadowImage=null;
		int p[]=new int[w*h];
		int ps[]=null;
		if (shadows) ps=new int[w*h];
		int i,j;
		double f=9e-1;
		int col=c.getRGB();
		int blue=col&0x0000FF,green=(col&0x00FF00)>>8,red=(col&0xFF0000)>>16;
		double r,g,b;
		double x,y,dist;
		boolean fine=Global.getParameter("fineboard",true);
		for (i=0; i<h; i++)
			for (j=0; j<w; j++)
			{	if (fine)
					f=((Math.sin(18*(double)j/w)+1)/2
					+(Math.sin(3*(double)j/w)+1)/10
					+0.2*Math.cos(5*(double)i/h)+
					+0.1*Math.sin(11*(double)i/h))
					*20+0.5;
				else
					f=((Math.sin(14*(double)j/w)+1)/2
					+0.2*Math.cos(3*(double)i/h)+
					+0.1*Math.sin(11*(double)i/h))
					*10+0.5;
				f=f-Math.floor(f);
				if (f<2e-1) f=1-f/2;
				else if (f<4e-1) f=1-(4e-1-f)/2;
				else f=1;
				if (i==w-1 || (i==w-2 && j<w-2) || j==0
					|| (j==1 && i>1)) f=f/2;
				if (i==0 || (i==1 && j>1) || j>=w-1
					|| (j==w-2 && i<h-1))
				{	r=128+red*f/2; g=128+green*f/2; b=128+blue*f/2;
				}
				else
				{	r=red*f; g=green*f; b=blue*f;
				}
				p[w*i+j]=(255<<24)|((int)(r)<<16)|((int)(g)<<8)|(int)(b);
				if (shadows)
				{	f=1;
					y=Math.abs((i-(ox+d/2+(i-ox)/d*(double)d)));
					x=Math.abs((j-(oy+d/2+(j-oy)/d*(double)d)));
					dist=Math.sqrt(x*x+y*y)/d*2;
					if (dist<1.0) f=0.9*dist;
					ps[w*i+j]=(255<<24)|((int)(r*f)<<16)|((int)(g*f)<<8)|(int)(b*f);
				}
				if (EPT.stopped()) return;
			}
		if (shadows)
			StaticShadowImage=comp.createImage(
				new MemoryImageSource(w,h,ColorModel.getRGBdefault(),
						ps,0,w));
		StaticImage=comp.createImage(
				new MemoryImageSource(w,h,ColorModel.getRGBdefault(),
						p,0,w));
		W=w; H=h; D=d; Ox=ox; Oy=oy; C=c;
		savesize(comp);
	}
	
	static void savesize (ImageObserver C)
	{	Image i=StaticImage;
		if (i!=null)
		{	Global.setParameter("sboardwidth",i.getWidth(C));
			Global.setParameter("sboardheight",i.getHeight(C));
			Global.setParameter("sboardox",EmptyPaint.Ox);
			Global.setParameter("sboardoy",EmptyPaint.Oy);
			Global.setParameter("sboardd",EmptyPaint.D);
		}
	}

	static boolean haveImage (int w, int h, Color c, int ox, int oy, int d)
	{	if (StaticImage==null) return false;
		return (w==W && h==H && ox==Ox && oy==Oy && D==d && C.getRGB()==c.getRGB());
	}
}
