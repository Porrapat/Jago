package jagoclient.partner;

import java.awt.*;

import jagoclient.gui.*;
import jagoclient.Go;
import jagoclient.Global;
import jagoclient.StopThread;
import jagoclient.partner.partner.*;

import rene.util.list.*;

class OpenPartnerFrameUpdate extends StopThread
{	OpenPartnerFrame OPF;
	public OpenPartnerFrameUpdate (OpenPartnerFrame f)
	{	OPF=f;
		start();
	}
	public void run ()
	{	while (stopped())
		{	try { sleep(30000); } catch (Exception e) {}
			OPF.refresh();
		}
	}
}

/**
This is a frame, which displays a list of all open partner servers.
It contains buttons to connect to one of one of them and
to refresh the list.
*/

public class OpenPartnerFrame extends CloseFrame
{	Go G;
	java.awt.List L;
	OpenPartnerFrameUpdate OPFU;
	public OpenPartnerFrame (Go go)
	{	super(Global.resourceString("Open_Partners"));
		G=go;
		MenuBar mb=new MenuBar();
		setMenuBar(mb);
		Menu m=new MyMenu(Global.resourceString("Options"));
		m.add(new MenuItemAction(this,Global.resourceString("Close")));
		mb.add(m);
		setLayout(new BorderLayout());
		L=new java.awt.List();
		L.setFont(Global.SansSerif);
		refresh();
		add("Center",L);
		Panel bp=new MyPanel();
		bp.add(new ButtonAction(this,Global.resourceString("Connect")));
		bp.add(new ButtonAction(this,Global.resourceString("Refresh")));
		bp.add(new MyLabel(" "));
		bp.add(new ButtonAction(this,Global.resourceString("Close")));
		add("South",bp);
		Global.setwindow(this,"openpartner",300,200);
		seticon("ijago.gif");
		show();
		OPFU=new OpenPartnerFrameUpdate(this);
	}
	public void doAction (String o)
	{	if (o.equals(Global.resourceString("Refresh")))
		{	refresh();
		}
		else if (o.equals(Global.resourceString("Close")))
		{	doclose();
		}
		else if (o.equals(Global.resourceString("Connect")))
		{	connect();
		}
		else super.doAction(o);
	}
	public void refresh ()
	{	ListClass PL=Global.OpenPartnerList;
		L.removeAll();
		if (PL==null) return;
		ListElement le=PL.first();
		while (le!=null)
		{	L.add(((Partner)le.content()).Name);
			le=le.next();
		}
	}
	public void doclose ()
	{	G.OPF=null;
		OPFU.stopit();
		Global.notewindow(this,"openpartner");		
		super.doclose();
	}
	public void connect ()
	{	ListElement le=Global.OpenPartnerList.first();
		String s=L.getSelectedItem();
		while (le!=null)
		{	Partner p=(Partner)le.content();
			if (p.Name.equals(s))
			{	PartnerFrame cf=
					new PartnerFrame(Global.resourceString("Connection_to_")+p.Name,false);
				Global.setwindow(cf,"partner",500,400);
				new ConnectPartner(p,cf);
				return;
			}
			le=le.next();
		}
	}
}
