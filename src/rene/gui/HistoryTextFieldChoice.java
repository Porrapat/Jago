package rene.gui;

import java.awt.*;
import java.awt.event.*;

import rene.util.list.*;

public class HistoryTextFieldChoice extends MyChoice
	implements ItemListener
{	HistoryTextField T;

	public HistoryTextFieldChoice (HistoryTextField t)
	{	T=t;
		update();
		addItemListener(this);
	}
	public void itemStateChanged (ItemEvent e)
	{	String s=getSelectedItem();
		if (s.equals("   ")) return;
		T.doAction(s);
	}
	
	public void update ()
	{	removeAll();
		ListClass l=T.getHistory();
		ListElement e=l.last();
		if (e==null || ((String)e.content()).equals("")) add("   ");
		while (e!=null)
		{	String s=(String)e.content();
			if (!s.equals("")) add(s);
			e=e.previous();
		}
	}
	
}
