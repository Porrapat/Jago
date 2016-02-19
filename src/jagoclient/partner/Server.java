package jagoclient.partner;

import java.io.*;
import java.net.*;

import jagoclient.datagram.*;
import jagoclient.Global;
import jagoclient.Dump;
import jagoclient.partner.partner.*;

import rene.util.list.*;

/**
This is the server thread for partner connections. If anyone connects
to the server, a new PartnerFrame will open to handle the connection.
If the server starts, it will open a new PartnerServerThread, which
checks for datagrams that announce open partners.
*/

public class Server extends Thread
{	int Port;
	boolean Public;
	static public PartnerServerThread PST=null;
	ServerSocket SS;
	/**
	@param p the server port
	@param publ server is public or not
	*/
	public Server (int p, boolean publ)
	{	Port=p; Public=publ;
		start();
	}
	public void run ()
	{	if (PST==null) 
			PST=new PartnerServerThread(Global.getParameter("serverport",6970)+2);
		try { sleep(1000); } catch (Exception e) {}
		try
		{	SS=new ServerSocket(Port);
			while (true)
			{	Socket S=SS.accept();
				if (Global.Busy) // user set the busy checkbox
				{	PrintWriter o=new PrintWriter(
						new DataOutputStream(S.getOutputStream()),true);
					o.println("@@busy");
					S.close();
					continue;
				}
				PartnerFrame cf=
					new PartnerFrame(Global.resourceString("Server"),true);
				Global.setwindow(cf,"partner",500,400);
				cf.show();
				cf.open(S);
			}
		}
		catch (Exception e)
		{	Dump.println("Server Error");
		}
	}
	
	/**
	This is called, when the server is opened. It will announce
	the opening to known servers by a datagram.
	*/
	public void open ()
	{	if (Public)
		{	ListElement pe=Global.PartnerList.first();
			while (pe!=null)
			{	Partner p=(Partner)pe.content();
				if (p.State>0)
				{	DatagramMessage d=new DatagramMessage();
					d.add("open");
					d.add(Global.getParameter("yourname","Unknown"));
					try
					{	String s=InetAddress.getLocalHost().toString();
						d.add(s.substring(s.lastIndexOf('/')+1));
					}
					catch (Exception e) { d.add("Unknown Host"); }
					d.add(""+Global.getParameter("serverport",6970));
					d.add(""+p.State);
					d.send(p.Server,p.Port+2);
				}
				pe=pe.next();
			}
		}
		Global.Busy=false;
	}
	/**
	This is called, when the server is closed. It will announce
	the closing to known servers by a datagram.
	*/
	public void close ()
	{	if (!Public) return;
		ListElement pe=Global.PartnerList.first();
		DatagramMessage d=new DatagramMessage();
		d.add("close");
		d.add(Global.getParameter("yourname","Unknown"));
		try
		{	String s=InetAddress.getLocalHost().toString();
			d.add(s.substring(s.lastIndexOf('/')+1));
		}
		catch (Exception e) { d.add("Unknown Host"); }
		while (pe!=null)
		{	Partner p=(Partner)pe.content();
			if (p.State>0) d.send(p.Server,p.Port+2);
			pe=pe.next();
		}
		Global.Busy=true;
	}
}
