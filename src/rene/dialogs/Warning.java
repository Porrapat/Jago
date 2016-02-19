package rene.dialogs;

import java.awt.*;
import java.awt.event.*;

import rene.gui.*;

/**
This is a simple warning dialog. May be used as modal or non-modal
dialog.
*/

public class Warning extends CloseDialog 
    implements ActionListener
{	public boolean Result;
	Frame F;
	public Warning (Frame f, String c, String title, boolean flag)
	{	super(f,title,flag);
		F=f;
		Panel pc=new MyPanel();
		FlowLayout fl=new FlowLayout();
		pc.setLayout(fl);
		fl.setAlignment(FlowLayout.CENTER);
		pc.add(new MyLabel(" "+c+" "));
		add("Center",pc);
		Panel p=new MyPanel();
		p.add(new ButtonAction(this,Global.name("close"),"Close"));
		add("South",p);
		pack();
	}
	public Warning (Frame f, String c, String title)
	{	this(f,c,title,true);
	}
}
