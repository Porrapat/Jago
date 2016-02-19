package jagoclient.igs.who;

import java.io.*;
import java.awt.*;

import jagoclient.gui.*;
import jagoclient.igs.*;
import jagoclient.dialogs.*;
import jagoclient.Global;

import rene.util.list.*;
import rene.util.sort.*;
import rene.util.parser.*;
import rene.viewer.*;

class EditButtons extends CloseDialog
{	TextField Labels[],Texts[];
	public EditButtons (Frame f)
	{	super(f,Global.resourceString("Edit_Buttons"),true);
		setLayout(new BorderLayout());
		Panel center=new MyPanel();
		Labels=new TextField[3];
		Texts=new TextField[3];
		center.setLayout(new GridLayout(0,2));
		for (int i=0; i<3; i++)
		{	Labels[i]=new FormTextField(
				Global.getParameter("who.button"+(i+1)+".label",""));
			center.add(Labels[i]);
			Texts[i]=new FormTextField(
				Global.getParameter("who.button"+(i+1)+".text",""));
			center.add(Texts[i]);
		}
		add("Center",new Panel3D(center));
		Panel south=new MyPanel();
		south.add(new ButtonAction(this,Global.resourceString("OK"),"OK"));
		south.add(new ButtonAction(this,Global.resourceString("Cancel"),"Close"));
		add("South",new Panel3D(south));
		Global.setpacked(this,"editbuttons",300,300,f);
		validate(); setVisible(true);
	}
	public void doAction (String o)
	{	if (o.equals("OK"))
		{	for (int i=0; i<3; i++)
			{	Global.setParameter("who.button"+(i+1)+".label",
					Labels[i].getText());
				Global.setParameter("who.button"+(i+1)+".text",
					Texts[i].getText());
			}
			setVisible(false); dispose();
		}
		else super.doAction(o);
	}
}

/**
Displays a frame with the player list. The list may be sorted by rank or name.
*/

public class WhoFrame extends CloseFrame
	implements CloseListener
{	IgsStream In;
	PrintWriter Out;
	Lister T;
	ConnectionFrame CF;
	WhoDistributor GD;
	ListClass L;
	boolean SortName;
	boolean Closed=false;
	String Range;
	TextField WhoRange;
	CheckboxMenuItem OmitX,OmitQ,Looking,FriendsOnly;
	
	/**
	@param cf the ConnectionFrame, which calls this WhoFrame (used as CloseListener)
	@param out the output stream to the IGS server.
	@param in the input stream from the IGS server.
	@param range a range string to be used in the IGS who command.
	*/
	public WhoFrame (ConnectionFrame cf, PrintWriter out, IgsStream in, String range)
	{	super(Global.resourceString("_Who_"));
		cf.addCloseListener(this);
		Range=range;
		In=in;
		Out=out;
		MenuBar m=new MenuBar();
		Menu sort=new MyMenu(Global.resourceString("Options"));
		sort.add(new MenuItemAction(this,Global.resourceString("Sort_by_Name")));
		sort.add(new MenuItemAction(this,Global.resourceString("Sort_by_Rank")));
		sort.addSeparator();
		m.add(sort);
		sort.add(OmitX=new CheckboxMenuItemAction(this,Global.resourceString("Omit_X")));
		OmitX.setState(Global.getParameter("omitx",true));
		sort.add(OmitQ=new CheckboxMenuItemAction(this,Global.resourceString("Omit_Q")));
		OmitQ.setState(Global.getParameter("omitq",false));
		sort.add(Looking=new CheckboxMenuItemAction(this,Global.resourceString("__only")));
		Looking.setState(false);
		sort.add(FriendsOnly=new CheckboxMenuItemAction(this,Global.resourceString("Friends_only")));
		FriendsOnly.setState(false);
		Menu actions=new MyMenu(Global.resourceString("Button_Actions"));
		addmenu(actions,Global.resourceString("Tell"),"Tell");
		actions.addSeparator();
		addmenu(actions,Global.resourceString("Add_Friend"),"Add_Friend");
		addmenu(actions,Global.resourceString("Remove_Friend"),"Remove_Friend");
		addmenu(actions,Global.resourceString("Mark_Player"),"Mark");
		addmenu(actions,Global.resourceString("Unmark_Player"),"Unmark");
		actions.addSeparator();
		addmenu(actions,Global.resourceString("Match"),"Match");
		addmenu(actions,Global.resourceString("Stats"),"Stats");
		addmenu(actions,Global.getParameter("who.button1.label",
			Global.resourceString("Suggest")),"Button1");
		addmenu(actions,Global.getParameter("who.button2.label",
				Global.resourceString("Results")),"Button2");
		addmenu(actions,Global.getParameter("who.button3.label",
				Global.resourceString("Stored")),"Button3");
		actions.addSeparator();
		actions.add(new MenuItemAction(this,Global.resourceString("Edit_Buttons")));
		m.add(actions);
		Menu help=new MyMenu(Global.resourceString("Help"));
		help.add(new MenuItemAction(this,Global.resourceString("About_this_Window")));
		m.add(help);
		setMenuBar(m);
		setLayout(new BorderLayout());
		T=Global.getParameter("systemlister",false)?new SystemLister():new Lister();
		T.setFont(Global.Monospaced);
		T.setBackground(Global.gray);
		T.setText(Global.resourceString("Loading"));
		add("Center",T);
		Panel p=new MyPanel();
		p.add(new ButtonAction(this,Global.resourceString("Refresh")));
		p.add(WhoRange=new HistoryTextField(this,Global.resourceString("WhoRange"),5));
		WhoRange.setText(Range);
		p.add(new ButtonAction(this,Global.resourceString("Close")));
		p.add(new ButtonAction(this,Global.resourceString("Toggle_Looking")));
		p.add(new ButtonAction(this,Global.resourceString("Toggle_Friends")));
		add("South",new Panel3D(p));
		CF=cf;
		GD=null;
		SortName=false;
		seticon("iwho.gif");
		PopupMenu pop=new PopupMenu();
		addpop(pop,Global.resourceString("Tell"),"Tell");
		addpop(pop,Global.resourceString("Match"),"Match");
		addpop(pop,Global.resourceString("Stats"),"Stats");
		pop.addSeparator();
		addpop(pop,Global.resourceString("Add_Friend"),"Add_Friend");
		addpop(pop,Global.resourceString("Remove_Friend"),"Remove_Friend");
		addpop(pop,Global.resourceString("Mark_Player"),"Mark");
		addpop(pop,Global.resourceString("Unmark_Player"),"Unmark");
		pop.addSeparator();
		addpop(pop,Global.getParameter("who.button1.label",
			Global.resourceString("Suggest")),"Button1");
		Global.setParameter("who.button1.label",
			Global.getParameter("who.button1.label",
				Global.resourceString("Suggest")));
		Global.setParameter("who.button1.text",
			Global.getParameter("who.button1.text",
				Global.resourceString("suggest %%")));
		addpop(pop,Global.getParameter("who.button2.label",
				Global.resourceString("Results")),"Button2");
		Global.setParameter("who.button2.label",
			Global.getParameter("who.button2.label",
				Global.resourceString("Results")));
		Global.setParameter("who.button2.text",
			Global.getParameter("who.button2.text",
				Global.resourceString("results %%")));
		addpop(pop,Global.getParameter("who.button3.label",
				Global.resourceString("Stored")),"Button3");
		Global.setParameter("who.button3.label",
			Global.getParameter("who.button3.label",
				Global.resourceString("Stored")));
		Global.setParameter("who.button3.text",
			Global.getParameter("who.button3.text",
				Global.resourceString("stored %%")));
		if (T instanceof Lister) T.setPopupMenu(pop);
	}
	
	public void addpop (PopupMenu pop, String label, String action)
	{	MenuItem mi=new MenuItemAction(this,label,action);
		pop.add(mi);
	}
	
	public void addmenu (Menu pop, String label, String action)
	{	MenuItem mi=new MenuItemAction(this,label,action);
		pop.add(mi);
	}
	
	public void doAction (String o)
	{	if (Global.resourceString("Refresh").equals(o))
		{	refresh();
		}
		else if ("Tell".equals(o))
		{	String user=getuser();
			if (user.equals("")) return;
			new TellQuestion(this,CF,user);
		}
		else if ("Match".equals(o))
		{	String user=getuser();
			if (user.equals("")) return;
			new MatchQuestion(this,CF,user);
		}
		else if ("Add_Friend".equals(o))
		{	String friends=Global.getParameter("friends","");
			String user=" "+getuser();
			if (friends.indexOf(user)>=0) return;
			Global.setParameter("friends",friends+user);
			allsended();
		}
		else if ("Remove_Friend".equals(o))
		{	String friends=Global.getParameter("friends","");
			String user=" "+getuser();
			int n=friends.indexOf(user);
			if (friends.indexOf(user)<0) return;
			friends=friends.substring(0,n)+friends.substring(n+user.length());
			Global.setParameter("friends",friends);
			allsended();
		}
		else if ("Mark".equals(o))
		{	String friends=Global.getParameter("marked","");
			String user=" "+getuser();
			if (friends.indexOf(user)>=0) return;
			Global.setParameter("marked",friends+user);
			allsended();
		}
		else if ("Unmark".equals(o))
		{	String friends=Global.getParameter("marked","");
			String user=" "+getuser();
			int n=friends.indexOf(user);
			if (friends.indexOf(user)<0) return;
			friends=friends.substring(0,n)+friends.substring(n+user.length());
			Global.setParameter("marked",friends);
			allsended();
		}
		else if ("Stats".equals(o))
		{	if (getuser().equals("")) return;
			CF.out("stats "+getuser());
		}
		else if ("Button1".equals(o))
		{	if (getuser().equals("")) return;
			CF.out(replace(Global.getParameter("who.button1.text",
				"suggest %%")));
		}
		else if ("Button2".equals(o))
		{	if (getuser().equals("")) return;
			CF.out(replace(Global.getParameter("who.button2.text",
				"results %%")));
		}
		else if ("Button3".equals(o))
		{	if (getuser().equals("")) return;
			CF.out(replace(Global.getParameter("who.button3.text",
				"stored %%")));
		}
		else if (Global.resourceString("Sort_by_Name").equals(o))
		{	SortName=true;
			allsended();
		}
		else if (Global.resourceString("Sort_by_Rank").equals(o))
		{	SortName=false;
			allsended();
		}
		else if (Global.resourceString("About_this_Window").equals(o))
		{	new Help("who");
		}
		else if (o.equals(Global.resourceString("Edit_Buttons")))
		{	new EditButtons(this);
		}
		else if (o.equals(Global.resourceString("Toggle_Friends")))
		{	FriendsOnly.setState(!FriendsOnly.getState());
			allsended();
		}
		else if (o.equals(Global.resourceString("Toggle_Looking")))
		{	Looking.setState(!Looking.getState());
			allsended();
		}
		else super.doAction(o);
	}
	
	public String replace (String replacement)
	{	int pos=replacement.indexOf("%%");
		if (pos<0) return replacement;
		String begin="";
		if (pos>0) begin=replacement.substring(0,pos);
		String end="";
		if (pos+2<replacement.length()) end=replacement.substring(pos+2);
		return begin+getuser()+end;
	}
	
	public void itemAction (String o, boolean f)
	{	if (o.equals(Global.resourceString("__only")))
		{	Global.setParameter("looking",f);
			allsended();
		}
		else if (o.equals(Global.resourceString("Omit_X")))
		{	Global.setParameter("omitx",f);
			allsended();
		}
		else if (o.equals(Global.resourceString("Omit_Q")))
		{	Global.setParameter("omitq",f);
			allsended();
		}
		else if (o.equals(Global.resourceString("Friends_only")))
		{	Global.setParameter("friendsonly",f);
			allsended();
		}
	}

	public String getuser ()
	{	String s=T.getSelectedItem();
		if (s==null || s.length()<14) return "";
		StringParser p=new StringParser(s.substring(12));
		return p.parseword();
	}

	/**
	When this dialog cloeses it will unchain itself from the
	distributor, which created it.
	*/
	synchronized public boolean close ()
	{	if (GD!=null) GD.unchain();
		GD=null;
		Global.setParameter("whorange",WhoRange.getText());
		Closed=true;
		CF.removeCloseListener(this);
		CF.Who=null;
		Global.notewindow(this,"who");        
		return true;
	}

	/**
	Will send a new who command to the IGS server. To receive
	the IGS output it will create a who distributor, which will
	register with the IGSStream and send input to this who
	frame.
	*/
	synchronized public void refresh ()
	{	L=new ListClass();
		T.setText(Global.resourceString("Loading"));
		if (GD!=null) GD.unchain();
		GD=null;
		GD=new WhoDistributor(In,this);
		Out.println("who "+WhoRange.getText());
	}

	/** 
	The distributor told me that all players have been receved.
	Now unchain the distributor, parse and sort the output and
	display.
	*/
	synchronized void allsended ()
	{	if (GD!=null) GD.unchain();
		GD=null;
		if (Closed) return;
	    ListElement p=L.first();
		int i,n=0;
		while (p!=null)
		{	n++;
			p=p.next();
		}
		if (n>2)
		{	WhoObject v[]=new WhoObject[n];
			p=L.first();
			for (i=0; i<n; i++)
			{	v[i]=new WhoObject((String)p.content(),SortName);
				p=p.next();
			}
			Sorter.sort(v);
			T.setText("");
    		T.appendLine0(Global.resourceString("_Info_______Name_______Idle___Rank"));
    		Color FC=Color.green.darker(),CM=Color.red.darker();
	    	for (i=0; i<n; i++)
			{	if (!(
				(Looking.getState() && !v[i].looking()) ||
				(OmitX.getState() && v[i].silent()) ||
				(OmitQ.getState() && v[i].quiet()) ||
				(FriendsOnly.getState() && !v[i].friend())
				))
					T.appendLine0(v[i].who(),v[i].friend()?
						FC:(v[i].marked()?CM:Color.black));
    		}
    		T.doUpdate(false);
		}
		else
		{	p=L.first();
			while (p!=null)
			{	T.appendLine((String)p.content());
				p=p.next();
			}
			T.doUpdate(false);
		}
	}

	/** receive a single line of players (two players) */
	synchronized void receive (String s)
	{	if (Closed) return;
	    StringParser p=new StringParser(s);
		p.skipblanks();
		if (p.skip("Info")) return;
		if (p.skip("****")) return;
		p=new StringParser(s);
		s=p.upto('|');
		L.append(new ListElement(s));
		if (!p.skip("| ")) return;
		s=p.upto('|');
		L.append(new ListElement(s));
	}
	
	public void isClosed ()
	{	if (Global.getParameter("menuclose",true)) setMenuBar(null);
		setVisible(false); dispose();
	}

}

