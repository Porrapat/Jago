package rene.util.sound;

import java.awt.*;
import java.awt.event.*;

import rene.util.list.*;
import rene.gui.Global;

class SoundElement extends ListElement
{	Sound S;
	public SoundElement (String name)
	{	super(name);
		S=null;
		if (Global.getJavaVersion()>=1.3) S=new Sound13(name);
		else S=new Sound11(name);
	}
	public String name ()
	{	return (String)content();
	}
	public void play ()
	{	S.start();
	}
}

/**
This is a Sound class to play and store sounds from resources. The class
keeps a list of loaded sounds.
*/

public class SoundList implements Runnable
{	ListClass SL;
	Thread T;
	boolean Busy;
	String Name,Queued;
	
	public SoundList ()
	{	SL=new ListClass();
		T=new Thread(this);
		T.start();
		try { Thread.currentThread().sleep(0); } catch (Exception e) {}
	}
	public void run ()
	{	Busy=false;
		while (true)
		{	try 
			{	synchronized(this) { wait(); }
			}
			catch (Exception e)
			{	System.out.println(e);
			}
			Busy=true;
			while (Name!=null)
			{	playNow(Name);
				synchronized (this)
				{	Name=Queued;
					Queued=null;
				}
			}
			Busy=false;
		}
	}
	static synchronized public void beep ()
	{	Toolkit.getDefaultToolkit().beep();
	}
	public SoundElement find (String name)
	{	SoundElement se=(SoundElement)SL.first();
		while (se!=null)
		{	if (se.name().equals(name))
			{	return se;
			}
			se=(SoundElement)se.next();
		}
		return null;
	}
	public SoundElement add (String name)
	{	SoundElement e=new SoundElement(name);
		SL.append(e);
		return e;
	}
	public void playNow (String name)
	{	SoundElement e=find(name);
		if (e==null) e=add(name);
		e.play();
	}
	public synchronized void play (String name)
	{	if (busy())
		{	synchronized(this) { Queued=name; }
			return;
		}
		Name=name;
		synchronized(this) { notify(); }
	}
	public boolean busy ()
	{	return Busy;
	}
	static SoundList L=new SoundList();
	static public void main (String args[])
	{	System.out.println("Java Version "+Global.getJavaVersion());
		String Sounds[]={"high","message","click","stone","wip","yourmove","game"};
		rene.gui.CloseFrame F=new rene.gui.CloseFrame()
			{	public void doAction (String o)
				{	if (Global.getJavaVersion()>=1.3)
						L.play("/jagoclient/au/"+o+".wav");
					else
						L.play("/jagoclient/au/"+o+".au");
				}
				public void doclose ()
				{	System.exit(0);
				}
			};
		F.setLayout(new BorderLayout());
		Panel p=new Panel();
		F.add("Center",p);
		for (int i=0; i<Sounds.length; i++)
		{	Button b=new Button(Sounds[i]);
			b.addActionListener(F);
			p.add(b);
		}
		F.pack();
		F.setVisible(true);
	}
}

