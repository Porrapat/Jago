package jagoclient.dialogs;

import java.awt.*;
import java.awt.event.*;

import jagoclient.gui.*;
import jagoclient.Global;

class ColorScrollbar extends Panel 
    implements AdjustmentListener, DoActionListener
{	public int Value;
	ColorEdit CE;
	Scrollbar SB;
	IntField L;
	public ColorScrollbar (ColorEdit ce, String s, int value)
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
parameters under the specified name string. Modality is
handled as in the Question dialog.
@see jagoclient.Global
@see jagoclient.dialogs.Question
*/

public class ColorEdit extends CloseDialog
{	ColorScrollbar Red, Green, Blue;
	Label RedLabel,GreenLabel,BlueLabel;
	Color C;
	Panel CP;
	String Name;
	public ColorEdit (Frame F, String s, int red, int green, int blue, boolean flag)
	{	super(F,Global.resourceString("Edit_Color"),flag);
		Name=s;
		C=Global.getColor(s,red,green,blue);
		Panel p=new MyPanel();
		p.setLayout(new GridLayout(0,1));
		p.add(Red=new ColorScrollbar(this,Global.resourceString("Red"),C.getRed()));
		p.add(Green=new ColorScrollbar(this,Global.resourceString("Green"),C.getGreen()));
		p.add(Blue=new ColorScrollbar(this,Global.resourceString("Blue"),C.getBlue()));
		add("Center",new Panel3D(p));
		Panel pb=new MyPanel();
		pb.add(new ButtonAction(this,Global.resourceString("OK")));
		pb.add(new ButtonAction(this,Global.resourceString("Cancel")));
		addbutton(pb);
		add("South",new Panel3D(pb));
		CP=new MyPanel();
		CP.add(new MyLabel(Global.resourceString("Your_Color")));
		CP.setBackground(C);
		add("North",new Panel3D(CP));
		Global.setpacked(this,"coloredit",350,150);
		validate();
	}
	public void addbutton (Panel p)
	{
	}
	public ColorEdit (Frame F, String s, Color C, boolean flag)
	{	this(F,s,C.getRed(),C.getGreen(),C.getBlue(),flag);
	}
	public void doAction (String o)
	{	Global.notewindow(this,"coloredit");
		if (Global.resourceString("Cancel").equals(o))
		{	setVisible(false); dispose();
		}
		else if (Global.resourceString("OK").equals(o))
		{	Global.setColor(Name,C);
			tell(C);
			setVisible(false); dispose();
		}
	}
	public void setcolor ()
	{	C=new Color(Red.value(),Green.value(),Blue.value());
		CP.setBackground(C);
		CP.repaint();
	}
	public void tell (Color C)
	{
	}
	public Color color ()
	{	return C;
	}
}
