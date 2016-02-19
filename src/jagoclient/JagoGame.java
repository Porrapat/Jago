package jagoclient;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.io.*;

import jagoclient.Global;

import jagoclient.board.*;

import rene.gui.*;

/**
This applet starts a game viewer. It can either start a specific
game or can display a list of games. OtherJagoGame applets on the same
page can use this applet to start a viewer.
<p>
If the applet is to display a single game, it will show a button
"Load" to start the game display. The game is determined by the
Game applet paramter, which must contain a valid URL to a game.
<P>
If it is to display a list of games, it expects an URL to this list file as a
Games applet parameter. The list file consists pairs of lines with the
game name in the first line and the game URL in the next.
*/

public class JagoGame extends Applet
	implements ActionListener,Runnable
{	JagoGameFrame GF;
	java.awt.List L=null;
	Button B;
	String Game,Games;
	Vector Urls;
	
	/**
	Initialize the applet depending on wether there is
	a "Game" or a "Games" applet parameter.
	*/
	synchronized public void init ()
	{	Game=getParameter("Game");
		Games=getParameter("Games");
		setLayout(new BorderLayout());
		if (Games!=null && !Games.equals(""))
		{	L=new java.awt.List();
			add("Center",L);
			Urls=new Vector();
			try
			{	BufferedReader in=null;
				if (Games.startsWith("http"))
					in=new BufferedReader(new InputStreamReader(
						new DataInputStream(
							new URL(Games).openStream())));
				else				
					in=new BufferedReader(new InputStreamReader(
						new DataInputStream(
							new URL(getDocumentBase(),Games).openStream())));
				while (true)
				{	String name,value;
					name=in.readLine();
					if (name==null) break;
					value=in.readLine();
					if (value==null) break;
					L.add(name);
					Urls.addElement(value);
				}
			}
			catch (Exception ex) {}
			Panel p=new MyPanel();
			p.add(B=new Button(Global.resourceString("Load")));
			add("South",new Panel3D(p));
		}
		else
		{	add("Center",B=new Button(Global.resourceString("Load")));
		}
		B.addActionListener(this);
		Global.url(getCodeBase());
		Global.readparameter("go.cfg");
		Global.createfonts();
	}
	
	public void actionPerformed (ActionEvent e)
	{	GF=new JagoGameFrame(new Frame(),
			Global.resourceString("Jago_Viewer"));
		Global.setcomponent(GF);
		if (L!=null)
		{	if (L.getSelectedIndex()<0) return;
			Game=(String)Urls.elementAt(L.getSelectedIndex());
		}
		else
		{	Game=getParameter("Game");
		}
		new Thread(this).start();
	}
	
	/**
	This is called from JagoGame Other
	@see jagoclient.JagoGameOther
	*/
	public void run ()
	{	GF.setVisible(false);
		try
		{	if (Game!=null) 
			{	if (Game.startsWith("http")) GF.load(new URL(Game));
				else GF.load(new URL(getDocumentBase(),Game));
			}
		}
		catch (Exception ex) {}
		GF.activate();
		GF.setVisible(true);
	}
	
	public void load (String game)
	{	Game=game;
		GF=new JagoGameFrame(new Frame(),Global.resourceString("Jago_Viewer"));
		new Thread(this).start();
	}
}
