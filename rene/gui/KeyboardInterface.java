package rene.gui;

import java.awt.event.*;

public interface KeyboardInterface
{	/**
	Got a command string.
	@return Could handle command or not.
	*/
	public boolean keyboardCommand (KeyEvent e, String o);
	/**
	Got an escaped character. Escape works as macro key or call of
	external programs in JE.
	*/
	public boolean keyboardEscape (KeyEvent e, char c);
	/**
	Got a character input.
	*/
	public boolean keyboardChar (KeyEvent e, char c);
}
