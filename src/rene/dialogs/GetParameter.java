package rene.dialogs;

import java.awt.*;

import rene.gui.*;

/**
A simple dialog to scan for a parameter.
*/

public class GetParameter extends CloseDialog
{	HistoryTextField Input;
	static public int InputLength;
	String Result="";
	boolean Aborted=true;
	public GetParameter (Frame f, String title, String prompt, String action)
	{	this(f,title,prompt,action,false);
	}
	public GetParameter (Frame f, String title, String prompt, String action,
		boolean help)
	{	super(f,title,true);
		Input=new HistoryTextField(this,"Action",InputLength);
		Input.addKeyListener(this);
		init(f,title,prompt,action,help);
	}
	void init (Frame f, String title, String prompt, String action, boolean help)
	{	setLayout(new BorderLayout());
		Panel center=new MyPanel();
		center.setLayout(new GridLayout(0,1));
		center.add(new MyLabel(prompt));
		center.add(Input);
		add("Center",new Panel3D(center));
		Panel south=new MyPanel();
		south.setLayout(new FlowLayout(FlowLayout.RIGHT));
		south.add(new ButtonAction(this,action,"Action"));
		south.add(new ButtonAction(this,Global.name("abort"),"Abort"));
		if (help)
			south.add(new ButtonAction(this,Global.name("help"),"Help"));
		add("South",new Panel3D(south));
		pack();
	}
	public void doAction (String o)
	{	if (o.equals("Abort"))
		{	doclose();
		}
		else if (o.equals("Action"))
		{	Result=Input.getText();
			doclose();
			Aborted=false;
		}
		else if (o.equals("Help"))
		{	help();
		}
		else super.doAction(o);
	}
	public void set (String s)
	{	Input.setText(s);
	}
	public String getResult () { return Result; }
	public boolean aborted () { return Aborted; }
	public void help () {}
}
