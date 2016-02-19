package jagoclient.board;

import java.awt.*;

import jagoclient.sound.*;

/**
This board overrides some methods from the Board class
for connected boards. It knows a ConnectedGoFrame to
ask for permission of operations and to report moves
to it.
*/

public class ConnectedBoard extends Board
{	ConnectedGoFrame CGF;

	public ConnectedBoard (int size, ConnectedGoFrame gf)
	{	super(size,gf);
		CGF=gf;
	}

	public synchronized void setmouse (int i, int j, int c)	
	{	if (Pos.isMain() && CGF.wantsmove()) return;
		super.setmouse(i,j,c);
	}

	/**
	In a ConnectedBoard you cannot fix the game tree this way.
	*/
	public synchronized void setmousec (int i, int j, int c) {}

	public synchronized void movemouse (int i, int j)
	{	if (Pos.haschildren()) return;
		if (P.color(i,j)!=0) return;
		if (captured==1 && capturei==i && capturej==j &&
			GF.getParameter("preventko",true)) return;
		if (Pos.isMain() && CGF.wantsmove())
		{	if (CGF.moveset(i,j))
			{	sendi=i; sendj=j;
				update(i,j); copy();
				MyColor=P.color();
			}
			JagoSound.play("click","",false);
		}
		else set(i,j); // try to set a new move
	}

	/**
	Completely remove a group (at end of game, before count), and
	note all removals. This is only allowed in end nodes and if
	the GoFrame wants the removal, it gets it.
	*/
	public synchronized void removegroup (int i0, int j0)
	{	if (Pos.haschildren()) return;
		if (P.color(i0,j0)==0) return;
		if (CGF.wantsmove() && ((Node)Pos.content()).main())
		{	CGF.moveset(i0,j0);
		}
		super.removegroup(i0,j0);
	}

	/**
	Take back the last move.
	*/
	public synchronized void undo ()
	{	if (Pos.isMain()
			&& CGF.wantsmove())
		{	if (!Pos.haschildren())
			{	if (State!=1 && State!=2) clearremovals();
				CGF.undo();
			}
			return;
		}
		super.undo();
	}

	/**
	Pass (report to the GoFrame if necessary.
	*/
	public synchronized void pass ()
	{	if (Pos.haschildren()) return;
		if (GF.blocked() && Pos.isMain()) return;
		if (Pos.isMain() && CGF.wantsmove())
		{	CGF.movepass(); return;
		}
		super.pass();
	}

	/**
	This is used to fix the game tree (after confirmation).
	Will not be possible, if the GoFrame wants moves.
	*/
	public synchronized void insertnode ()
	{	if (Pos.isLastMain() && CGF.wantsmove()) return;
		super.insertnode();
	}
	
	/**
	In a ConnectedBoard you cannot delete stones.
	*/
	public synchronized void deletemousec (int i, int j) {}
}
