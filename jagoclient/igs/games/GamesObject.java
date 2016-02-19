package jagoclient.igs.games;

import java.io.*;
import java.awt.*;

import jagoclient.gui.*;
import jagoclient.dialogs.*;
import jagoclient.Global;

import rene.util.list.*;
import rene.util.parser.*;
import rene.util.sort.*;
import rene.viewer.*;

/**
This is a SortObject for sorting games by W player rank.
@see jagoclient.sort.Sorter
*/

public class GamesObject implements SortObject
{	String S;
	int V;
	String White,Black;
	public GamesObject (String s)
	{	S=s;
		if (S.indexOf(']')==3) S=" "+S;
		StringParser p=new StringParser(s);
		p.upto(']'); p.skip("]");
		White=p.upto('[').trim();
		p.skip("[");
		String h=p.parseword(']');
		p.upto('.'); p.skip(".");
		Black=p.upto('[').trim();
		if (p.error())
		{	V=-50; return;
		}
		p=new StringParser(h);
		if (p.isint())
		{	V=p.parseint();
			if (p.skip("k")) V=100-V;
			else if (p.skip("d")) V=100+V;
			else if (p.skip("p")) V+=200;
		}
		else V=0;
	}
	String game () { return S; }
	public int compare (SortObject o)
	{	GamesObject g=(GamesObject)o;
		if (V<g.V) return 1;
		else if (V>g.V) return -1;
		else return 0;
	}
	
	public boolean friend ()
	{	String friends=Global.getParameter("friends","");
		return friends.indexOf(" "+White)>=0 || friends.indexOf(" "+Black)>=0;
	}
}
