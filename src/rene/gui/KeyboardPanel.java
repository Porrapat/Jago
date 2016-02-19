package rene.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import rene.dialogs.*;
import rene.gui.*;
import rene.util.sort.*;

/**
This is used as the display panel for the keyboard editor. It displays
information about the selected keyboard item.
*/

public class KeyboardPanel extends ItemPanel
	implements KeyListener
{	TextField MenuString,ActionName,CharKey;
	Checkbox Shift,Control,Alt;
	String Name="";
	Choice C;
	ItemEditor E;

	public KeyboardPanel ()
	{	setLayout(new BorderLayout());
		
		Panel center=new Panel();
		center.setLayout(new GridLayout(0,1));
		// the menu item
		center.add(MenuString=new MyTextField("",30));
		MenuString.setEditable(false);
		// the description
		center.add(ActionName=new MyTextField());
		ActionName.setEditable(false);
		// the key
		center.add(CharKey=new MyTextField());
		CharKey.setEditable(false);
		CharKey.addKeyListener(this);
		// modifiers
		center.add(Shift=new Checkbox(Global.name("keyeditor.shift")));
		center.add(Control=new Checkbox(Global.name("keyeditor.control")));
		center.add(Alt=new Checkbox(Global.name("keyeditor.alt")));
		add("Center",center);

		Panel south=new Panel();
		south.setLayout(new BorderLayout());
		Panel c=new Panel();
		// the choice of command keys
		C=new Choice();
		if (Global.NormalFont!=null) C.setFont(Global.NormalFont);
		c.add(C);
		C.add("-------------");
		south.add("Center",c);
		// default and undefine buttons
		Panel buttons=new Panel();
		buttons.add(new ButtonAction(this,
			Global.name("keyeditor.default"),"Default"));
		buttons.add(new ButtonAction(this,
			Global.name("keyeditor.none"),"None"));
		south.add("South",buttons);
		add("South",south);
	}
	
	public void setItemEditor (ItemEditor e)
	{	E=e;
	}
	
	/**
	Build a list of available command keys.
	*/
	public void makeCommandChoice ()
	{	C.removeAll();
		C.add("");
		for (int i=1; i<=5; i++)
		{	String s=commandShortcut(i);
			C.add(i+": "+s);
		}
		C.select(0);
	}
	
	/**
	The the command shortcut number i.
	*/
	public String commandShortcut (int i)
	{	String s="command."+i;
		Enumeration e=E.getElements().elements();
		while (e.hasMoreElements())
		{	KeyboardItem k=(KeyboardItem)e.nextElement();
			if (k.getMenuString().equals(s))
			{	return k.shortcut();
			}
		}
		return "";
	}
	
	/**
	Set the key, if one is pressed inside the CharKey textfield.
	*/
	public void keyPressed (KeyEvent e)
	{	Shift.setState(e.isShiftDown());
		Control.setState(e.isControlDown());
		Alt.setState(e.isAltDown());
		CharKey.setText(
			KeyDictionary.translate(e.getKeyCode()).toLowerCase());
		C.select(0);
	}
	public void keyTyped (KeyEvent e) {}
	public void keyReleased (KeyEvent e) {}
	
	/*
	Override methods of ItemPanel
	*/
	
	/**
	Display this element on the panel.
	*/
	public void display (ItemEditorElement e)
	{	KeyboardItem k=(KeyboardItem)e;
		Name=k.getName();
		MenuString.setText(k.getMenuString());
		ActionName.setText(k.getActionName());
		CharKey.setText(k.getCharKey());
		MenuString.setText(k.getMenuString());
		Shift.setState(k.isShift());
		Control.setState(k.isControl());
		Alt.setState(k.isAlt());
		C.select(k.getCommandType());
	}
	
	/**
	Create a new keyboard element from the panel entries.
	*/
	public ItemEditorElement getElement ()
	{	int type=C.getSelectedIndex();
		return new KeyboardItem(CharKey.getText(),
			MenuString.getText(),ActionName.getText(),
			Shift.getState(),Control.getState(),Alt.getState(),type);
	}
	
	public String getName ()
	{	return Name;
	}
	
	public void setName (String s)
	{	Name=s;
		MenuString.setText(s);
	}
	
	/**
	Test on doublicate keys, and undefine them.
	*/
	public void notifyChange (Vector v, int item)
	{	KeyboardItem changed=(KeyboardItem)v.elementAt(item);
		String descr=changed.keyDescription();
		for (int i=0; i<v.size(); i++)
		{	if (i==item) continue;
			KeyboardItem k=(KeyboardItem)v.elementAt(i);
			if (k.keyDescription().equals(descr))
			{	v.setElementAt(new KeyboardItem(k.getMenuString(),"none"),i);
			}
		}
		if (changed.getMenuString().startsWith("command."))
		{	makeCommandChoice();
		}
	}
	
	/**
	React on the Default and None buttons.
	*/
	public void doAction (String o)
	{	if (o.equals("Default"))
		{	String s=MenuString.getText();
			KeyboardItem k=new KeyboardItem(s,Global.name("key."+s));
			CharKey.setText(k.getCharKey());
			Shift.setState(k.isShift());
			Control.setState(k.isControl());
			Alt.setState(k.isAlt());
		}
		else if (o.equals("None"))
		{	CharKey.setText("none");
			Shift.setState(false);
			Control.setState(false);
			Alt.setState(false);
		}
		else super.doAction(o);
	}
	
	/**
	User wishes to clear all keyboard definitions.
	*/
	public boolean extra (Vector v)
	{	v.removeAllElements();
		return true;
	}
}
