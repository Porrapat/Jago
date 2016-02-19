package rene.gui;

import java.util.*;

import rene.util.sort.*;
import rene.dialogs.*;

/**
A keyboard item. Can be constructed from a menu string (like
editor.file.open) and a key string (like control.o). Can test a key
event, if it fits.
*/

public class KeyboardItem
	implements ItemEditorElement, SortObject
{	boolean Shift,Control,Alt;
	String CharKey;
	String MenuString,ActionName;
	int CommandType=0;

	/**
	Copy constructor, for use in editing (clone).
	*/
	public KeyboardItem (KeyboardItem item)
	{	Shift=item.Shift; Control=item.Control; Alt=item.Alt;
		CharKey=item.CharKey; MenuString=item.MenuString;
		ActionName=item.ActionName; CommandType=item.CommandType;
	}

	/**
	@param charkey The keyboard descriptive character (like "page down")
	@param menustring The menu item (may have some *s added)
	@param actionname The description of the menu item.
	@param shift,control,alt Modifier flags.
	@param commandtype The command key, that is needed (0 is none).
	*/
	public KeyboardItem (String charkey, String menustring, String actionname,
		boolean shift, boolean control, boolean alt, int commandtype)
	{	Shift=shift; Control=control; Alt=alt;
		CharKey=charkey.toLowerCase();
		MenuString=menustring; ActionName=actionname;
		CommandType=commandtype;
	}

	/**
	@param menu The menu string.
	@param key The key descripion a la "esc1.shift.control.e"
	*/
	public KeyboardItem (String menu, String key)
	{	MenuString=menu;
		Shift=Control=Alt=false;
		CommandType=0;
		CharKey="";
		StringTokenizer t=new StringTokenizer(key,".");
		while (t.hasMoreTokens())
		{	String token=(String)t.nextToken();
			if (t.hasMoreTokens())
			{	if (token.equals("control")) Control=true;
				else if (token.equals("shift")) Shift=true;
				else if (token.equals("alt")) Alt=true;
				else if (token.startsWith("esc"))
					// esc should be followed by a number
				{	try
					{	CommandType=Integer.parseInt(token.substring(3));
					}
					catch (Exception e) {}
				}
				else return;
			}
			else
			{	if (key.equals("")) return;
				CharKey=token.toLowerCase();
			}
		}
		ActionName=Global.name(getStrippedMenuString());
	}

	public String getMenuString () { return MenuString; }
	public String getActionName () { return ActionName; }
	public String getCharKey () { return CharKey; }
	public boolean isShift () { return Shift; }
	public boolean isControl () { return Control; }
	public boolean isAlt () { return Alt; }
	public int getCommandType () { return CommandType; }

	/**
	Get a menu string, which is stripped from stars.
	*/
	public String getStrippedMenuString ()
	{	String s=MenuString;
		while (s.endsWith("*")) s=s.substring(0,s.length()-1);
		return s;
	}

	/**
	Compute a visible shortcut to append after the menu items (like
	"(Ctr O)". The modifiers depend on the language.
	*/
	public String shortcut ()
	{	if (CharKey.equals("none")) return "";
		String s=CharKey.toUpperCase();
		if (Alt) s=Global.name("shortcut.alt")+" "+s;
		if (Control) s=Global.name("shortcut.control")+" "+s;
		if (Shift) s=Global.name("shortcut.shift")+" "+s;
		if (CommandType>0)
			s=Keyboard.commandShortcut(CommandType)+" "+s;
		return s;
	}

	/**
	Get the name of this KeyboardItem element.
	*/
	public String getName ()
	{	return MenuString;
	}
	
	/**
	Method of SortObject, necessary to sort the keys by name.
	*/
	public int compare (SortObject o)
	{	return getName().compareTo(((KeyboardItem)o).getName());
	}
	
	/**
	Return a key description for this item (to save as parameter).
	This should be the same as the key parameter in the constructor.
	*/
	public String keyDescription ()
	{	String s=CharKey.toLowerCase();
		if (s.equals("none") || s.equals("default")) return s;
		if (Alt) s="alt."+s;
		if (Control) s="control."+s;
		if (Shift) s="shift."+s;
		if (CommandType>0) s="esc"+CommandType+"."+s;
		return s;
	}
}
