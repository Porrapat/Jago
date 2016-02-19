package jagoclient.gmp;

import java.awt.*;
import java.io.*;
import java.util.*;

import jagoclient.Global;
import jagoclient.gui.*;

class GMPWait extends CloseDialog
{	public GMPWait (GMPConnection f)
	{	super(f,Global.resourceString("Play_Go"),true);
		setLayout(new BorderLayout());
		add("Center",new MyLabel(Global.resourceString("Negotiating_with_Program")));
		Panel p=new MyPanel();
		p.add(new ButtonAction(this,Global.resourceString("Abort")));		
		add("South",p);
		Global.setpacked(this,"gmpwait",300,150,f);
		show();
	}
	public void doAction (String o)
	{	setVisible(false); dispose();
	}
}

class OkAdapter
{	public void gotOk() {}
}

public class GMPConnection extends CloseFrame
	implements GMPInterface
{	int Handicap=GMPConnector.EVEN;
	int MyColor=GMPConnector.WHITE;
	int Rules=GMPConnector.JAPANESE;
	int BoardSize=19;
	
	TextField Program;
	IntField HandicapField,BoardSizeField;
	Checkbox White;

	public GMPConnection (Frame f)
	{	super(Global.resourceString("Play_Go"));
		setLayout(new BorderLayout());
		Panel center=new MyPanel();
		center.setLayout(new GridLayout(0,2));
		center.add(new MyLabel(Global.resourceString("Go_Protocol_Server")));
		center.add(Program=new TextFieldAction(this,"",
			Global.getParameter("gmpserver","gnugo.exe"),16));
		center.add(new MyLabel(Global.resourceString("Board_size")));
		center.add(BoardSizeField=new IntField(this,"BoardSize",
			Global.getParameter("gmpboardsize",19)));
		center.add(new MyLabel(Global.resourceString("Handicap")));
		center.add(HandicapField=new IntField(this,"Handicap",
			Global.getParameter("gmphandicap",9)));
		center.add(new MyLabel(Global.resourceString("Play_White")));
		center.add(White=new CheckboxAction(this,""));
		White.setState(Global.getParameter("gmpwhite",true));
		add("Center",new Panel3D(center));
		Panel south=new MyPanel();
		south.add(new ButtonAction(this,Global.resourceString("Play")));
		add("South",new Panel3D(south));
		Global.setpacked(this,"gmpconnection",300,150,f);
		seticon("iboard.gif");
		setVisible(true);
	}
	GMPConnector C;
	GMPGoFrame F;
	GMPConnection Co=this;
	public void doAction (String o)
	{	if (o.equals(Global.resourceString("Play")))
		{	String text=Program.getText();
			StringTokenizer t;
			if (text.startsWith("\""))
				t=new StringTokenizer(Program.getText(),"\"");
			else
				t=new StringTokenizer(Program.getText()," ");
			if (!t.hasMoreTokens()) return;
			String s=t.nextToken();
			File f=new File(s);
			if (!f.exists())
			{	rene.dialogs.Warning w=
				new rene.dialogs.Warning(this,
					"Program not found!","Warning",true);
				w.center(this);
				w.setVisible(true);
				return;			
			}
			C=new GMPConnector(Program.getText());
			Global.setParameter("gmpserver",Program.getText());
			C.setGMPInterface(this);
			Handicap=HandicapField.value(0,9);
			Global.setParameter("gmphandicap",Handicap);
			if (Handicap==0) Handicap=1;
			BoardSize=BoardSizeField.value(7,19);
			Global.setParameter("gmpboardsize",BoardSize);
			MyColor=White.getState()?GMPConnector.WHITE:GMPConnector.BLACK;
			Global.setParameter("gmpwhite",White.getState());
			try
			{	setOk(new OkAdapter ()
					{	public void gotOk ()
						{	F=new GMPGoFrame(Co,BoardSize,
								White.getState()?1:-1);
							F.setVisible(true);
							Co.setVisible(false); Co.dispose();
							if (Handicap>1) handicap(Handicap);
						}
					}
				);
				C.connect();
				new GMPWait(this);
				Ok=null;
			}
			catch (Exception e)
			{	rene.dialogs.Warning w=
				new rene.dialogs.Warning(this,
					"Error : "+e.toString(),"Warning",true);
				w.center(this);
				w.setVisible(true);			
			}
		}
	}
	public int getHandicap ()
	{	return Handicap;
	}
	public int getColor ()
	{	return MyColor;
	}
	public int getRules ()
	{	return Rules;
	}
	public int getBoardSize ()
	{	return BoardSize;
	}
	public void gotMove (int color, int pos)
	{	pos--;
		int i=pos%BoardSize;
		int j=pos/BoardSize;
		if (i<0 || j<0) F.gotPass(color);
		if (color==MyColor) F.gotSet(color,i,BoardSize-j-1);
		else F.gotMove(color,i,BoardSize-j-1);
	}
	
	OkAdapter Ok=null;
	public void setOk (OkAdapter ok) { Ok=ok; }
	public void gotOk ()
	{	if (Ok!=null) Ok.gotOk();
		Ok=null;
	}
	public void gotAnswer (int a)
	{
	}
	
	public int I,J;
	
	public void moveset (int i, int j)
	{	int pos=(BoardSize-j-1)*BoardSize+i+1;
		I=i; J=j;
		try
		{	setOk(new OkAdapter ()
				{	public void gotOk ()
					{	F.gotMove(MyColor,I,J);
					}
				}
			);
			C.move(MyColor,pos);
		}
		catch (Exception e) {}
	}
	
	public void pass ()
	{	try
		{	C.move(MyColor,0);
		}
		catch (Exception e) {}
	}
	
	public void undo ()
	{	try
		{	C.takeback(2);
		}
		catch (Exception e) {}
	}
	
	public void setblack (int i, int j)
	{	F.gotSet(GMPConnector.BLACK,i,j);
	}

	public void handicap (int n)
	{	int S=BoardSize;
		int h=(S<13)?3:4;
		if (n>5)
		{	setblack(h-1,S/2); setblack(S-h,S/2);
		}
		if (n>7)
		{	setblack(S/2,h-1); setblack(S/2,S-h);
		}
		switch (n)
		{	case 9 :
			case 7 :
			case 5 :
				setblack(S/2,S/2);
			case 8 :
			case 6 :
			case 4 :
				setblack(S-h,S-h);
			case 3 :
				setblack(h-1,h-1);
			case 2 :
				setblack(h-1,S-h);
			case 1 :
				setblack(S-h,h-1);
		}
		F.color(GMPConnector.WHITE);
	}
	
	public boolean askUndo ()
	{	setOk(new OkAdapter ()
			{	public void gotOk ()
				{	F.doundo(2);
				}
			}
		);
		try
		{	C.send(6,2);
		}
		catch (Exception e)
		{}
		return false;
	}
	
	public void doclose ()
	{	if (C!=null) C.doclose();
		super.doclose();
	}
}
