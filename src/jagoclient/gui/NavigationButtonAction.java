package jagoclient.gui;

import java.awt.*;
import java.awt.event.*;

import jagoclient.Global;

/**
A lightweight button for the navigation buttons (reacts quicker).
*/

public class NavigationButtonAction extends Panel
    implements MouseListener
{   DoActionListener C;
    String Name;
    String S;
    FontMetrics FM;
    int W,H;
    boolean Pressed=false;
    boolean Focus=false;
    boolean Enabled=true;
    Image I=null;
    Graphics IG;
    public NavigationButtonAction (DoActionListener c, String s, String name)
    {   C=c; Name=name; S=s;
        addMouseListener(this);
        setFont(Global.SansSerif);
        FM=getFontMetrics(Global.SansSerif);
        int sw1=FM.stringWidth("<<<"),sw2=FM.stringWidth(S);
        if (sw2>sw1) W=sw2*5/4+2;
        else W=sw1*5/4+2;
        H=FM.getHeight()*5/4+2;
        repaint();
    }
    public NavigationButtonAction (DoActionListener c, String s)
    {   this(c,s,s);
    }
    
    public void mousePressed (MouseEvent e)
    {	Pressed=true; repaint();
    }
    public void mouseReleased (MouseEvent e)
    {	Pressed=false; repaint();
    	if (e.getX()>=0 && e.getX()<W && e.getY()>=0 && e.getY()<H)
    		C.doAction(Name);
    }
    public void mouseClicked (MouseEvent e) {}
    public void mouseEntered (MouseEvent e) {}
    public void mouseExited (MouseEvent e) {}
    public void paint (Graphics g)
    {	if (I==null)
    	{	I=createImage(W,H);
    		IG=I.getGraphics();
    		IG.setFont(getFont());
    	}
    	int w=FM.stringWidth(S);
    	IG.setColor(Global.gray);
    	IG.fill3DRect(0,0,W,H,!Pressed);
    	if (Enabled) IG.setColor(Color.black);
    	else IG.setColor(Color.gray);
    	IG.drawString(S,(W-w)/2,H-((H-FM.getHeight())/2+FM.getDescent()));
    	g.drawImage(I,0,0,W,H,this);
    }
    public void update (Graphics g)
    {	paint(g);
    }
    public Dimension getPreferredSize ()
    {	return new Dimension(W,H);
    }
    public Dimension getMinimumSize ()
    {	return new Dimension(W,H);
    }
    public void setEnabled (boolean flag)
    {	if (Enabled==flag) return;
    	Enabled=flag; repaint();
    	super.setEnabled(flag);
    }
}
