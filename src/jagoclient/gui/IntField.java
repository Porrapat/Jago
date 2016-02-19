package jagoclient.gui;

import java.awt.*;

/**
A TextField, which holds an integer number with minimal
and maximal range.
*/

public class IntField extends TextFieldAction
{	public IntField (DoActionListener l, String name, int v)
	{	super(l,name,""+v);
	}
	public IntField (DoActionListener l, String name, int v, int cols)
	{	super(l,name,""+v,cols);
	}
	public int value ()
	{	try
		{	return Integer.parseInt(getText());
		}
		catch (NumberFormatException e)
		{	setText(""+0);
			return 0;
		}
	}
	public int value (int min, int max)
	{	int n;
		try
		{	n=Integer.parseInt(getText());
		}
		catch (NumberFormatException e)
		{	setText(""+min);
			return min;
		}
		if (n<min) { n=min; setText(""+min); }
		if (n>max) { n=max; setText(""+max); }
		return n;
	}
	public void set (int v)
	{	setText(""+v);
	}
	public boolean valid ()
	{	try
		{	Integer.parseInt(getText());
		}
		catch (NumberFormatException e)
		{	return false;
		}
		return true;
	}
}


