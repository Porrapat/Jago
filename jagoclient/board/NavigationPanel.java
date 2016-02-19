package jagoclient.board;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import jagoclient.gui.*;
import jagoclient.Global;

public class NavigationPanel extends MyPanel
{	Board B;
	final int size=5;
	int w,h;
	boolean OverflowRequest,Overflow;
	boolean Adjust;
	int AdjustX;
	Vector Parents;
	Color BoardColor;

	public NavigationPanel (Board b)
	{	B=b;
		BoardColor=Global.getColor("boardcolor",170,120,70);
	}
	
	public void paint (Graphics g)
	{	Overflow=Adjust=false;
		AdjustX=0;
		Parents=new Vector();
		int currentline=0;
		w=getSize().width;
		h=getSize().height;
		g.clearRect(0,0,w,h);
		TreeNode Pos=B.Pos;
		Parents.addElement(Pos);
		TreeNode ParentPos=Pos.parentPos();
		TreeNode StartPos=Pos;
		int x=size*2,y=size*3;
		if (ParentPos!=null)
		{	Parents.addElement(ParentPos);
			currentline++;
			for (int i=0; i<h/(3*size)/3; i++)
			{	if (ParentPos.parentPos()==null) break;
				ParentPos=ParentPos.parentPos();
				Parents.addElement(ParentPos);
				currentline++;
			}
			if (ParentPos.parentPos()!=null)
			{	g.setColor(Color.black);
				g.drawLine(size*2,size*2,size*2,size);
			}
			StartPos=ParentPos;
		}
		paint(g,StartPos,x,y,Pos,0,currentline);
		if (OverflowRequest)
		{	Overflow=true;
			g.clearRect(0,0,w,h);
			paint(g,StartPos,x,y,Pos,0,currentline);
			if (Adjust)
			{	g.clearRect(0,0,w,h);
				paint(g,StartPos,x-AdjustX,y,Pos,0,currentline);
			}
		}
	}
	
	public int paint (Graphics g, TreeNode pos, int x, int y,
		TreeNode current, int line, int currentline)
	{	if (!Overflow && x>w)
		{	OverflowRequest=true;
			return x;
		}
		if (pos==current)
		{	g.setColor(Color.red.darker());
			g.fillRect(x-size,y-size,size*2,size*2);
			if (Overflow && !Adjust && x>w)
			{	Adjust=true;
				AdjustX=x-w/2;
				return x;
			}
		}
		else if (pos.node().main())
		{	g.setColor(BoardColor);
			g.fillRect(x-size,y-size,size*2,size*2);			
		}
		else
		{	g.setColor(Color.gray);
			g.fillRect(x-size,y-size,size*2,size*2);			
		}
		g.setColor(Color.black);
		g.drawRect(x-size,y-size,size*2,size*2);
		if (!pos.haschildren()) return x;
		if (y+2*size>=h)
		{	g.setColor(Color.black);
			g.drawLine(x,y+size,x,y+2*size);
			return x;
		
		}
		TreeNode p=pos.firstChild();
		if (Overflow && !inParents(pos))
		{	g.setColor(Color.black);
			g.drawLine(x,y+size,x,y+2*size);
			int x0=x;
			x=paint(g,p,x,y+3*size,current,line+1,currentline);
			if (Board.getNext(p)!=null)
			{	g.setColor(Color.black);
				g.drawLine(x0,y+size*3/2,x0+size,y+size*3/2);
			}
		}
		else
		{	int i=0;
			int x0=x;
			while (p!=null)
			{	if (i==0)
				{	g.setColor(Color.black);
					g.drawLine(x,y+size,x,y+2*size);
					x=paint(g,p,x,y+3*size,current,line+1,currentline);
				}
				else
				{	g.setColor(Color.black);
					g.drawLine(x0,y+size*3/2,x+3*size,y+size*3/2);
					x0=x;
					g.drawLine(x+3*size,y+size*3/2,x+3*size,y+2*size);
					x=paint(g,p,x+3*size,y+3*size,current,line+1,currentline);
				}
				p=Board.getNext(p);
				i++;
			}
		}
		return x;
	}
	
	boolean inParents (TreeNode pos)
	{	Enumeration e=Parents.elements();
		while (e.hasMoreElements())
		{	if ((TreeNode)e.nextElement()==pos) return true;
		}
		return false;
	}
}
