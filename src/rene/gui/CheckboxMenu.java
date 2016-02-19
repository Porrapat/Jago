package rene.gui;

import java.awt.*;
import java.util.*;

class CheckboxMenuElement
{	public String Tag;
	public CheckboxMenuItem Item;
	public CheckboxMenuElement (CheckboxMenuItem i, String tag)
	{	Item=i; Tag=tag;
	}
}

public class CheckboxMenu
{	Vector V;
	public CheckboxMenu ()
	{	V=new Vector();
	}
	public void add (CheckboxMenuItem i, String tag)
	{	V.addElement(new CheckboxMenuElement(i,tag));
	}
	public void set (String tag)
	{	int i;
		for (i=0; i<V.size(); i++)
		{	CheckboxMenuElement e=
				(CheckboxMenuElement)V.elementAt(i);
			if (tag.equals(e.Tag)) e.Item.setState(true);
			else e.Item.setState(false);
		}
	}
}
