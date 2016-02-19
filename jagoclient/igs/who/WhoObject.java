package jagoclient.igs.who;

import java.io.*;
import java.awt.*;

import rene.util.sort.*;
import rene.util.parser.*;

import jagoclient.Global;

/**
This is needed for the Sorter class.
@see rene.util.sort.Sorter
*/

public class WhoObject implements SortObject
{	String S,Name,Stat;
	public int V;
	boolean SortName;
	public WhoObject (String s, boolean sortname)
	{	S=s; SortName=sortname;
		if (s.length()<=30)
		{	V=-50; Name=""; Stat=""; return;
		}
		Stat=s.substring(0,5);
		StringParser p=new StringParser(s.substring(30));
		String h=p.parseword();
		p=new StringParser(h);
		if (p.isint())
		{	V=p.parseint();
			if (p.skip("k")) V=100-V;
			else if (p.skip("d")) V=100+V;
			else if (p.skip("p")) V+=200;
			else if (p.skip("NR")) V=0;
		}
		else V=-50;
		if (s.length()<14) Name="";
		else
		{	p=new StringParser(s.substring(12));
			Name=p.parseword();
		}
	}
	String who () { return S; }
	public int compare (SortObject o)
	{	WhoObject g=(WhoObject)o;
		if (SortName)
		{	return Name.compareTo(g.Name);
		}
		else
		{	if (V<g.V) return 1;
			else if (V>g.V) return -1;
			else return 0;
		}
	}
	public boolean looking ()
	{	return Stat.indexOf('!')>=0;
	}
	public boolean quiet ()
	{	return Stat.indexOf('Q')>=0;
	}
	public boolean silent ()
	{	return Stat.indexOf('X')>=0;
	}
	public boolean friend ()
	{	return Global.getParameter("friends","").indexOf(" "+Name)>=0;
	}
	public boolean marked ()
	{	return Global.getParameter("marked","").indexOf(" "+Name)>=0;
	}
}
