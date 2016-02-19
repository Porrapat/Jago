package jagoclient.gui;

import java.awt.*;
import java.awt.event.*;

import jagoclient.Global;

class CardPanelButton extends Button
    implements ActionListener,KeyListener
{	String Name;
	Panel P;
	CardLayout CL;
	public CardPanelButton (String text, CardLayout cl, String name, Panel p)
	{	super(text);
		Name=name; P=p; CL=cl;
		addActionListener(this);
		addKeyListener(this);
		setFont(Global.SansSerif);
	}
	public void actionPerformed (ActionEvent e)
	{	CL.show(P,Name);
	}
    public void keyPressed (KeyEvent e)
    {   if (e.getKeyCode()==KeyEvent.VK_ENTER)
        {   CL.show(P,Name);
        }
    }
    public void keyReleased (KeyEvent e) {}
    public void keyTyped (KeyEvent e) {}
}

/**
A simplified card panel. The panel has a south component, which displays
buttons, which switch the center component.
*/

public class CardPanel extends Panel
{	Panel P,Bp;
	CardLayout CL;
	public CardPanel ()
	{	setLayout(new BorderLayout());
		P=new MyPanel();
		P.setLayout(CL=new CardLayout());
		add("Center",new Panel3D(P));
		Bp=new MyPanel();
		add("South",new Panel3D(Bp));
	}
	/**
	Adds a component to the card panel.
	The name is used to create a button with this label.
	*/
	public void add (Component c, String name)
	{	P.add(name,c);
		Bp.add(new CardPanelButton(name,CL,name,P));
	}
}
