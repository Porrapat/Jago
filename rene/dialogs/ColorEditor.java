package rene.dialogs;

import java.awt.*;
import java.awt.event.*;

import rene.gui.*;

class ColorScrollbar extends Panel 
    implements AdjustmentListener, DoActionListener
{	public int Value;
	ColorEditor CE;
	Scrollbar SB;
	IntField L;
	public ColorScrollbar (ColorEditor ce, String s, int value)
	{	CE=ce;
		setLayout(new GridLayout(1,0));
		Value=value;
		Panel p=new MyPanel();
		p.setLayout(new GridLayout(1,0));
		p.add(new MyLabel(s));
		p.add(L=new IntField(this,"L",Value,4));
		add(p);
		add(SB=new Scrollbar(Scrollbar.HORIZONTAL,value,40,0,295));
		SB.addAdjustmentListener(this);
	}
	public void doAction (String o)
	{	if ("L".equals(o))
		{	Value=L.value(0,255);
			SB.setValue(Value);
			CE.setcolor();
		}
	}
	public void itemAction (String o, boolean flag) {}
	public void adjustmentValueChanged (AdjustmentEvent e)
	{	Value=SB.getValue();
		L.set(Value);
		SB.setValue(Value);
		CE.setcolor();
	}
	public int value () { return Value; }
}

/**
A dialog to edit a color. The result is stored in the Global
parameters under the specified name string.
@see rene.gui.Global
*/

public class ColorEditor extends CloseDialog
{	ColorScrollbar Red, Green, Blue;
	Label RedLabel,GreenLabel,BlueLabel;
	Color C;
	Panel CP;
	String Name;
	public ColorEditor (Frame F, String s, Color c)
	{	super(F,Global.name("coloreditor.title"),true);
		Name=s;
		C=Global.getParameter(s,c);
		if (C==null) C=new Color(255,255,255);
		Panel p=new MyPanel();
		p.setLayout(new GridLayout(0,1));
		p.add(Red=new ColorScrollbar(this,
			Global.name("coloreditor.red"),C.getRed()));
		p.add(Green=new ColorScrollbar(this,
			Global.name("coloreditor.green"),C.getGreen()));
		p.add(Blue=new ColorScrollbar(this,
			Global.name("coloreditor.blue"),C.getBlue()));
		add("Center",new Panel3D(p));
		Panel pb=new MyPanel();
		pb.add(new ButtonAction(this,Global.name("OK"),"OK"));
		pb.add(new ButtonAction(this,Global.name("abort"),"Close"));
		add("South",new Panel3D(pb));
		CP=new MyPanel();
		CP.add(new MyLabel(Global.name("coloreditor.color")));
		CP.setBackground(C);
		add("North",new Panel3D(CP));
		pack();
	}
	public void doAction (String o)
	{	if ("Close".equals(o))
		{	doclose();
		}
		else if ("OK".equals(o))
		{	Global.setParameter(Name,C);
			doclose();
		}
	}
	public void setcolor ()
	{	C=new Color(Red.value(),Green.value(),Blue.value());
		CP.setBackground(C);
		CP.repaint();
	}
}
