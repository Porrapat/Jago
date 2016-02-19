package jagoclient.igs;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import jagoclient.sound.*;
import jagoclient.Global;

import rene.util.parser.*;

/**
This is used to parse messages (code 24) from the server. It will
open a MessageDialog, unless this is checked off. The reply method
of the ConnectionFrame is checked for auto replies.
*/

public class MessageDistributor extends Distributor
{	ConnectionFrame CF;
	PrintWriter Out;
	MessageDialog MD;
	String LastUser;
	
	public MessageDistributor
		(ConnectionFrame cf, IgsStream in, PrintWriter out)
	{	super(in,24,0,false);
		CF=cf; Out=out;
		MD=null;
	}
	/** got a message */
	public void send (String C)
	{	if (C.equals("")) return;
		StringParser p=new StringParser(C);
		if (!p.skip("*")) return;
		String user=p.upto('*');
		if (!p.skip("*:")) return;
		p.skipblanks();
		if (p.error()) return;
		CF.append(C,Color.red.darker());
		String a=CF.reply();
		if (!a.equals("")) // autoreply on
		{	if (LastUser==null || !LastUser.equals(user))
			{	CF.append(Global.resourceString("Auto_reply_sent_to_")+user);
				Out.println("tell "+user+" "+a);
				LastUser=user;
			}
		}
		// no autoreply
		else if (Global.blocks(C)!=0) return;
		else if (CF.wantsmessages() || Global.posfilter(C))
		{	if (MD!=null)
			{	MD.append(user,p.upto((char)0));
				JagoSound.play("wip","wip",true);
			}
			else
			{	MD=new MessageDialog(CF,user,p.upto((char)0),Out,this);
				if (Global.blocks(C)==0) JagoSound.play("message","wip",true);
			}
		}
		else
		{	JagoSound.play("wip","wip",true);
		}
	}
	public void remove ()
	{	MD=null;
	}
}
