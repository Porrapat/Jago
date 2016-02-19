package jagoclient.gui;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;
import java.io.*;

import jagoclient.Global;

import rene.util.list.*;

/**
A Frame, which can be closed with the close button in the window.
Moreover, event handling is simplified with the DoActionListnener
interface. There is also a method for setting the icon of this
window.
*/
public class CloseFrame extends Frame
    implements WindowListener, ActionListener, DoActionListener
{	ListClass L;
	public CloseFrame (String s)
	{	super("");
		L=new ListClass();
	    addWindowListener(this);
	    setTitle(s);
	}
	public void windowActivated (WindowEvent e) {}
	public void windowClosed (WindowEvent e) {}
	public void windowClosing (WindowEvent e)
	{   if (close()) { doclose(); }
	}
	public void windowDeactivated (WindowEvent e) {}
	public void windowDeiconified (WindowEvent e) {}
	public void windowIconified (WindowEvent e) {}
	public void windowOpened (WindowEvent e) {}
	public boolean close ()
	{	return true;
	}
	public void actionPerformed (ActionEvent e)
	{   doAction(e.getActionCommand());
	}
	public void doAction (String o)
	{   if (Global.resourceString("Close").equals(o) && close())
		{	doclose();
		}
	}
	public void doclose ()
	{	if (Global.getParameter("menuclose",true)) setMenuBar(null);
		setVisible(false); dispose();
	}
	public void addCloseListener (CloseListener cl)
	{	L.append(new ListElement(cl));
	}
	public void inform ()
	{	ListElement e=L.first();
		while (e!=null)
		{	try
			{	((CloseListener)e.content()).isClosed();
			}
			catch (Exception ex) {}
			e=e.next();
		}
	}
	public void removeCloseListener (CloseListener cl)
	{	ListElement e=L.first();
		while (e!=null)
		{	CloseListener l=(CloseListener)e.content();
			if (l==cl) { L.remove(e); break; }
			e=e.next();
		}
	}
	public void itemAction (String o, boolean flag) {}
	// the icon things
	static Hashtable Icons=new Hashtable();
	public void seticon (String file)
	{	try
		{	Object o=Icons.get(file);
			if (o==null)
			{	Image i;
				InputStream in=getClass().getResourceAsStream("/jagoclient/gifs/"+file);
				int pos=0;
				int n=in.available();
				byte b[]=new byte[20000];
				while (n>0)
				{	int k=in.read(b,pos,n);
					if (k<0) break;
					pos+=k;
					n=in.available();
				}
				i=Toolkit.getDefaultToolkit().createImage(b,0,pos);
				MediaTracker T=new MediaTracker(this);
				T.addImage(i,0);
				T.waitForAll();
				Icons.put(file,i);
				setIconImage(i);
			}
			else
			{	setIconImage((Image)o);
			}
		} catch (Exception e) {}
	}
}
