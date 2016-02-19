package jagoclient.gui;

import java.awt.*;
import java.io.*;

import jagoclient.Global;

import rene.util.list.*;

/**
A text area that takes care of the maximal length imposed by Windows
and other OSs. This should be replaced by jagoclient.viewer.Viewer
<p>
The class works much like TextArea, but takes care of its length.
@see jagoclient.viewer.Viewer
*/

public class MyTextArea extends TextArea
{   ListClass L;
	public int MaxLength;
	int Length=0;
	public MyTextArea ()
    {	setFont(Global.Monospaced);
        setBackground(Global.gray);
    	L=new ListClass();
    	MaxLength=Global.getParameter("maxlength",10000);
    }
	public MyTextArea (String s, int x, int y, int f)
    {	super(s,x,y,f);
    	setFont(Global.Monospaced);
        setBackground(Global.gray);
    	L=new ListClass();
    	MaxLength=Global.getParameter("maxlength",10000);
    	setText(s);
    }
    public void append (String s)
    {	Length+=s.length();
    	L.append(new ListElement(s));
		if (Length>MaxLength)
		{	setVisible(false);
			super.setText("");
			ListElement e=L.last();
			Length=0;
			while (Length<MaxLength/4)
			{	Length+=((String)e.content()).length();
				if (e.previous()==null) break;
				e=e.previous();
			}
			while (e!=null)
			{	super.append((String)e.content());
				e=e.next();
			}
			setVisible(true);
		}
    	else super.append(s);
    }
    public void save (PrintWriter s)
    {	ListElement e=L.first();
    	while (e!=null)
    	{	s.print((String)e.content());
    		e=e.next();
    	}
    }
    public void setText (String s)
    {	Length=s.length();
    	super.setText(s);
    	L=new ListClass();
    	L.append(new ListElement(s));
    }
    public void setEditable (boolean flag)
    {	super.setEditable(flag);
    	if (!flag) setBackground(Global.gray.brighter());
    }
}
