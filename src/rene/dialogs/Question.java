package rene.dialogs;

import java.awt.*;
import java.awt.event.*;

import rene.gui.*;

/**
This is a simple yes/no question. May be used as modal or non-modal
dialog. Modal Question dialogs must be overriden to do something
sensible with the tell method. In any case setVible(true) must be
called in the calling program.
<p>
The static YesString and NoString may be overriden for foreign
languages.
*/

public class Question extends CloseDialog 
    implements ActionListener
{	public int Result;
	Object O;
	Frame F;
	public static int NO=0,YES=1,ABORT=-1;
	public Question (Frame f, String c, String title, Object o, 
		boolean abort, boolean flag)
	{	super(f,title,flag);
		F=f;
		Panel pc=new MyPanel();
		FlowLayout fl=new FlowLayout();
		pc.setLayout(fl);
		fl.setAlignment(FlowLayout.CENTER);
		pc.add(new MyLabel(" "+c+" "));
		add("Center",pc);
		Panel p=new MyPanel();
		p.setLayout(new FlowLayout(FlowLayout.RIGHT));
		p.add(new ButtonAction(this,Global.name("yes"),"Yes"));
		p.add(new ButtonAction(this,Global.name("no"),"No"));
		if (abort) p.add(new ButtonAction(this,Global.name("abort"),"Abort"));
		add("South",p);
		O=o;
		pack();
	}
	public Question (Frame f, String c, String title, Object o, boolean flag)
	{	this(f,c,title,o,true,flag);
	}
	public Question (Frame f, String c, String title)
	{	this(f,c,title,null,true,true);
	}
	public Question (Frame f, String c, String title, boolean abort)
	{	this(f,c,title,null,abort,true);
	}
	public void doAction (String o)
	{	if (o.equals("Yes"))
  		{	tell(this,O,YES);
  		}
  		else if (o.equals("No"))
  		{	tell(this,O,NO);
  		}
  		else if (o.equals("Abort"))
  		{	tell(this,O,ABORT);
  			Aborted=true;
  		}
  	}
  	/**
  	Needs to be overriden for modal usage. Should dispose the dialog.
  	*/
	public void tell (Question q, Object o, int f)
	{	Result=f;
		setVisible(false); dispose();
	}
	/**
	@return if the user pressed yes.
	*/
	public boolean yes ()
	{	return Result==YES;
	}
	public int getResult ()
	{	return Result;
	}
}
