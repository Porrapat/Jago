package rene.viewer;

import rene.util.list.*;

class TextPosition
{	ListElement L;
	int LCount;
	int LPos;
	public TextPosition (ListElement l, int lcount, int lpos)
	{	L=l; LCount=lcount; LPos=lpos;
	}
	boolean equal (TextPosition p)
	{	return p.LCount==LCount && p.LPos==LPos;
	}
	boolean before (TextPosition p)
	{	return p.LCount>LCount ||
			(p.LCount==LCount && p.LPos>LPos);
	}
	void oneleft ()
	{	if (LPos>0) LPos--;
	}
}

