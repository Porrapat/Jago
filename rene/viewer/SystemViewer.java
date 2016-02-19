package rene.viewer;

import java.awt.*;
import java.io.*;

public class SystemViewer extends Viewer
{   TextArea T;
    public SystemViewer ()
    {   super("dummy");
        setLayout(new BorderLayout());
        add("Center",T=new TextArea());
    }
    public void appendLine (String s)
    {   T.append(s+"\n");
    }
	public void appendLine (String s, Color c)
	{	appendLine(s);
	}
    public void append (String s)
    {   T.append(s);
    }
    public void append (String s, Color c)
    {   append(s);
    }
    public void setText (String s)
    {   T.setText(s);
    }
	public void doUpdate (boolean showlast)
	{	T.repaint();
	}
    public void setFont (Font s)
    {	T.setFont(s);
    }
    public void save (PrintWriter fo)
    {   fo.print(T.getText());
    	fo.flush();
    }
}
