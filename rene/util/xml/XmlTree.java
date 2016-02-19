package rene.util.xml;

import java.util.*;

import rene.util.list.*;
import rene.util.parser.*;

public class XmlTree extends Tree
	implements Enumeration
{	public XmlTree (XmlTag t)
	{	super(t);
	}
	public XmlTag getTag ()
	{	return (XmlTag)content();
	}
	public XmlTree xmlFirstContent ()
	{	if (firstchild()!=null) return (XmlTree)firstchild();
		else return null;
	}
	public boolean isText ()
	{	if (!haschildren()) return true;
		if (firstchild()!=lastchild()) return false;
		XmlTree t=(XmlTree)firstchild();
		XmlTag tag=t.getTag();
		if (!(tag instanceof XmlTagText)) return false;
		return true;
	}
	public String getText ()
	{	if (!haschildren()) return "";
		XmlTree t=(XmlTree)firstchild();
		XmlTag tag=t.getTag();
		return ((XmlTagText)tag).getContent();
	}
	ListElement Current;
	public Enumeration getContent ()
	{	Current=children().first();
		return this;
	}
	public boolean hasMoreElements ()
	{	return Current!=null;
	}
	public Object nextElement ()
	{	if (Current==null) return null;
		XmlTree c=(XmlTree)(Current.content());
		Current=Current.next();
		return c;
	}
	public String parseComment ()
		throws XmlReaderException
	{	StringBuffer s=new StringBuffer();
		Enumeration e=getContent();
		while (e.hasMoreElements())
		{	XmlTree tree=(XmlTree)e.nextElement();
			XmlTag tag=tree.getTag();
			if (tag.name().equals("P"))
			{	if (!tree.haschildren()) s.append("\n");
				else
				{	XmlTree h=tree.xmlFirstContent();
					String k=((XmlTagText)h.getTag()).getContent();
					k=k.replace('\n',' ');
					StringParser p=new StringParser(k);
					Vector v=p.wraplines(1000);
					for (int i=0; i<v.size(); i++)
					{	s.append((String)v.elementAt(i));
						s.append("\n");
					}
				}
			}
			else if (tag instanceof XmlTagText)
			{	String k=((XmlTagText)tag).getContent();
				StringParser p=new StringParser(k);
				Vector v=p.wraplines(1000);
				for (int i=0; i<v.size(); i++)
				{	s.append((String)v.elementAt(i));
					s.append("\n");
				}			
			}
			else
				throw new XmlReaderException("<"+tag.name()+"> not proper here.");
		}
		return s.toString();
	}	
}
