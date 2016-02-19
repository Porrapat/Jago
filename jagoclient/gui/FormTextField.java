package jagoclient.gui;

import java.awt.*;

import jagoclient.Global;

/**
A text field, which can transfer focus to the next text field,
when return is pressed.
*/

public class FormTextField extends GrayTextField
    implements DoActionListener
{	public FormTextField (String s)
	{	super();
		TextFieldActionListener T=
		    new TextFieldActionListener(this,"");
		addActionListener(T);
		setFont(Global.SansSerif);
		setText(s);
	}
	public void doAction (String o)
	{	transferFocus();
	}
	public void itemAction (String o, boolean flag) {}
}