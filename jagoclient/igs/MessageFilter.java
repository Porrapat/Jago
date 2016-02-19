package jagoclient.igs;

import java.awt.*;
import java.io.*;
import java.net.*;

import jagoclient.Global;
import jagoclient.gui.*;

import rene.util.list.*;

class SingleMessageFilter
{	public String Name,Start,End,Contains;
	public boolean BlockComplete,Positive;
	public SingleMessageFilter (String n, String s, String e, String c,
		boolean bc, boolean pos)
	{	Name=n; Start=s; End=e; Contains=c;
		BlockComplete=bc; Positive=pos;
	}
	public boolean matches (String s)
	{	if (!Start.equals("") && !s.startsWith(Start)) return false;
		if (!End.equals("") && !s.endsWith(End)) return false;
		if (!Contains.equals("") && s.indexOf(Contains)<0) return false;
		return true;
	}
	public boolean positive () { return Positive; }
}

/**
A message filter can be either positive or negative. It is used
to either block messages, or see messages, even when there source
is blocked by global flags.
<p>
The filter is determined by a start string, a string it must contain
or an end string. Filters are loaded at program start from filter.cfg.
This is done by a call to the load method.
<p>
The MessageFilter class has a list of SingleMessageFilter to check
the message against.
*/

public class MessageFilter
{	ListClass F;
	public static final int BLOCK_COMPLETE=2;
	public static final int BLOCK_POPUP=1;
	public MessageFilter ()
	{	F=new ListClass();
		load();
	}
	public int blocks (String s)
	{	ListElement e=F.first();
		while (e!=null)
		{	SingleMessageFilter f=(SingleMessageFilter)e.content();
			if (!f.positive() && f.matches(s))
			{	if (f.BlockComplete) return BLOCK_COMPLETE;
				else return BLOCK_POPUP;
			}
			e=e.next();
		}
		return 0;
	}
	public boolean posfilter (String s)
	{	ListElement e=F.first();
		while (e!=null)
		{	SingleMessageFilter f=(SingleMessageFilter)e.content();
			if (f.positive() && f.matches(s))
			{	return true;
			}
			e=e.next();
		}
		return false;
	}
	/**
	Load the message filters from filter.cfg.
	*/
	public void load ()
	{	try
		{	BufferedReader in=Global.getStream("filter.cfg");
			while (true)
			{	String name=in.readLine();
				if (name==null || name.equals("")) break;
				boolean pos=false;
				if (name.startsWith("+++++"))
				{	pos=true; name=name.substring(5);
					if (name.equals("")) break;
				}
				String start=in.readLine();
				if (start==null) break;
				String end=in.readLine();
				if (end==null) break;
				String contains=in.readLine();
				if (contains==null) break;
				String blockcomplete=in.readLine();
				if (blockcomplete==null) break;
				F.append(new ListElement(
					new SingleMessageFilter(name,start,end,contains,
						blockcomplete.equals("true"),pos)));
			}
			in.close();
		}	
		catch (Exception e)
		{	return;
		}
	}
	public void save ()
	{	if (Global.isApplet()) return;
		try
		{	PrintWriter out=new PrintWriter(
				new FileOutputStream(Global.home()+"filter.cfg"));
			ListElement l=F.first();
			while (l!=null)
			{	SingleMessageFilter p=(SingleMessageFilter)l.content();
				if (p.positive()) out.println("+++++"+p.Name);
				else out.println(p.Name);
				out.println(p.Start);
				out.println(p.End);
				out.println(p.Contains);
				out.println(p.BlockComplete);
				l=l.next();
			}
			out.close();
		}	
		catch (IOException e)
		{	return;
		}
	}
	public void edit ()
	{	new MessageFilterEdit(F);
	}
}

class MessageFilterEdit extends CloseFrame
{	ListClass F;
	java.awt.List L;
	public MessageFilterEdit (ListClass f)
	{	super(Global.resourceString("Message_Filter"));
		MenuBar mb=new MenuBar();
		setMenuBar(mb);
		Menu m=new MyMenu(Global.resourceString("Options"));
		m.add(new MenuItemAction(this,Global.resourceString("Close")));
		mb.add(m);
		F=f;
		L=new java.awt.List();
		L.setFont(Global.SansSerif);
		add("Center",new Panel3D(L));
		ListElement e=F.first();
		while (e!=null)
		{	L.add(((SingleMessageFilter)e.content()).Name);
			e=e.next();
		}
		Panel p=new MyPanel();
		p.add(new ButtonAction(this,Global.resourceString("Edit")));
		p.add(new ButtonAction(this,Global.resourceString("New")));
		p.add(new ButtonAction(this,Global.resourceString("Delete")));
		p.add(new MyLabel(" "));
		p.add(new ButtonAction(this,Global.resourceString("OK")));
		add("South",new Panel3D(p));
		seticon("ijago.gif");
		Global.setwindow(this,"filteredit",300,300);
		validate(); setVisible(true);
	}
	public void doAction (String o)
	{	if (Global.resourceString("Edit").equals(o))
		{	new SingleFilterEdit(this,F,selected());
		}
		else if (Global.resourceString("New").equals(o))
		{	new SingleFilterEdit(this,F,null);
		}
		else if (Global.resourceString("Delete").equals(o))
		{	removeselected();
		}
		else if (Global.resourceString("OK").equals(o))
		{	Global.saveMessageFilter();
			doclose();
		}		
		else super.doAction(o);
	}

	public void doclose ()
	{	Global.notewindow(this,"filteredit");			
		super.doclose();
	}

	SingleMessageFilter selected ()
	{	String s=L.getSelectedItem();
		if (s==null) return null;
		ListElement e=F.first();
		while (e!=null)
		{	SingleMessageFilter f=(SingleMessageFilter)e.content();
			if (f.Name.equals(s)) return f;
			e=e.next();
		}
		return null;
	}

	void removeselected ()
	{	String s=L.getSelectedItem();
		if (s==null) return;
		ListElement e=F.first();
		while (e!=null)
		{	SingleMessageFilter f=(SingleMessageFilter)e.content();
			if (f.Name.equals(s))
			{	F.remove(e);
				updatelist();
				return;
			}
			e=e.next();
		}
	}

	void updatelist ()
	{	L.removeAll();
		ListElement e=F.first();
		while (e!=null)
		{	L.add(((SingleMessageFilter)e.content()).Name);
			e=e.next();
		}
	}
}

class SingleFilterEdit extends CloseDialog
{	SingleMessageFilter MF;
	ListClass F;
	TextField N,S,E,C;
	Checkbox BC;
	MessageFilterEdit MFE;
	boolean isnew;
	Checkbox CB;
	public SingleFilterEdit (MessageFilterEdit fr, ListClass f,
		SingleMessageFilter mf)
	{	super(fr,Global.resourceString("Edit_Filter"),false);
		F=f; MF=mf; MFE=fr;
		if (MF==null)
		{	isnew=true;
			MF=new SingleMessageFilter(Global.resourceString("Name"),Global.resourceString("Starts_with"),Global.resourceString("Ends_With"),
				Global.resourceString("Contains"),false,false);
		}
		else isnew=false;
		CB=new Checkbox(Global.resourceString("Positive_Filter"));
		CB.setState(MF.Positive);
		CB.setFont(Global.SansSerif);
		add("North",CB);
		Panel p=new MyPanel();
		p.setLayout(new GridLayout(0,2));
		p.add(new MyLabel(Global.resourceString("Name")));
		p.add(N=new FormTextField(MF.Name));
		p.add(new MyLabel(Global.resourceString("Starts_with")));
		p.add(S=new FormTextField(MF.Start));
		p.add(new MyLabel(Global.resourceString("Ends_With")));
		p.add(E=new FormTextField(MF.End));
		p.add(new MyLabel(Global.resourceString("Contains")));
		p.add(C=new FormTextField(MF.Contains));
		p.add(new MyLabel(Global.resourceString("Block_completely")));
		p.add(BC=new Checkbox());
		BC.setState(MF.BlockComplete);
		add("Center",p);
		Panel bp=new MyPanel();
		bp.add(new ButtonAction(this,Global.resourceString("OK")));
		bp.add(new ButtonAction(this,Global.resourceString("Cancel")));
		add("South",bp);
		Global.setpacked(this,"singlefilteredit",300,300);
		validate();
		show();
	}
	public void doAction (String o)
	{	Global.notewindow(this,"singlefilteredit");	
		if (Global.resourceString("OK").equals(o) && !N.getText().equals(""))
		{	MF.Name=N.getText();
			MF.Start=S.getText();
			MF.End=E.getText();
			MF.Contains=C.getText();
			MF.BlockComplete=BC.getState();
			MF.Positive=CB.getState();
			if (isnew)
			{	F.append(new ListElement(MF));
			}
			MFE.updatelist();
		}
		setVisible(false); dispose();
	}

}
