package jagoclient.dialogs;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import jagoclient.gui.*;
import jagoclient.Global;

/**
A general dialog to get a string parameter. Contains a
simple text field. The modal flag is handled as in the
Question class. Again, show has to be called externally.
@see jagoclient.dialogs.Question
*/

public class GetParameter extends CloseDialog
{	public boolean Result;
	Object O;
	Frame F;
	TextField T;
	String Helpfile;
	protected Label Prompt;
	public GetParameter (Frame f, String c, String title, Object o,
		boolean modalflag)
	{	this(f,c,title,o,modalflag,"");
	}
	public GetParameter (Frame f, String c, String title, Object o,
		boolean modalflag, String help)
	{	super(f,title,modalflag);
		F=f; Helpfile=help;
		Panel n=new MyPanel();
		n.setLayout(new BorderLayout());
		n.add("North",Prompt=new MyLabel(c));
		n.add("Center",T=new TextFieldAction(this,"Input",25));
		add("Center",new Panel3D(n));
		Panel p=new MyPanel();
		p.add(new ButtonAction(this,Global.resourceString("OK")));
		p.add(new ButtonAction(this,Global.resourceString("Cancel")));
		if (!help.equals(""))
		{	p.add(new MyLabel(" "));
			p.add(new ButtonAction(this,Global.resourceString("Help")));
		}
		add("South",new Panel3D(p));
		O=o;
		if (modalflag) Global.setpacked(this,"getparameter",300,150,f);
		else Global.setpacked(this,"getparameter",300,150);
		validate();
		T.addKeyListener(this);
	}
	public GetParameter (Frame f, String c, String title, Object o, char echo,
		boolean modalflag)
	{	this(f,c,title,o,echo,modalflag,"");
	}
	public GetParameter (Frame f, String c, String title, Object o, char echo,
		boolean modalflag, String help)
	{	super(f,title,modalflag);
		F=f; Helpfile=help;
		Panel n=new MyPanel();
		n.setLayout(new BorderLayout());
		n.add("North",new MyLabel(c));
		n.add("Center",T=new TextFieldAction(this,"Input",25));
		add("Center",new Panel3D(n));
		add("North",new MyLabel(c));
		T.setEchoChar(echo);
		Panel p=new MyPanel();
		p.add(new ButtonAction(this,Global.resourceString("OK")));
		p.add(new ButtonAction(this,Global.resourceString("Cancel")));
		if (!help.equals(""))
		{	p.add(new MyLabel(" "));
			p.add(new ButtonAction(this,Global.resourceString("Help")));
		}
		add("South",new Panel3D(p));
		if (modalflag) Global.setpacked(this,"getparameter",300,150,f);
		else Global.setpacked(this,"getparameter",300,150);
		validate();
		T.addKeyListener(this);
		O=o;
	}
	public void doAction (String o)
	{	Global.notewindow(this,"getparameter");
		if (Global.resourceString("Cancel").equals(o))
		{	close(); setVisible(false); dispose(); 
		}
		else if (o.equals("Input") || o.equals(Global.resourceString("OK")))
		{	if (tell(O,T.getText())) { setVisible(false); dispose(); }
		}
		else if (o.equals(Global.resourceString("Help")))
		{	new HelpDialog(F,Helpfile);
		}
		else super.doAction(o);
	}
	/**
	This is called, when the dialog is finished with a valid
	entry (User pressed OK).
	*/
	public boolean tell (Object o, String S)
	{	return true;
	}
	public void set (String s)
	{	T.setText(s);
	}
	public boolean close ()
	{	return true;
	}
	/**
	This is to be used in the modal case after the show method returns.
	@return the text you asked for
	*/
	public String getText ()
	{	return T.getText();
	}
}
