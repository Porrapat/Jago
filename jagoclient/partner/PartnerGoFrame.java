package jagoclient.partner;

import java.awt.*;

import jagoclient.gui.*;
import jagoclient.dialogs.*;
import jagoclient.board.*;
import jagoclient.sound.*;
import jagoclient.Global;

/**
The go frame for partner connections.
*/

public class PartnerGoFrame extends ConnectedGoFrame 
	implements TimedBoard
{	String BlackName,WhiteName;
	int BlackTime,WhiteTime,BlackMoves,WhiteMoves;
	int BlackRun,WhiteRun;
	GoTimer Timer;
	long CurrentTime;
	PartnerFrame PF;
	int Col,TotalTime,ExtraTime,ExtraMoves;
	public boolean Started,Ended;
	int Handicap;
	
	public PartnerGoFrame (PartnerFrame pf, String s,
		int col, int si, int tt, int et, int em, int ha)
	{	super(s,si,Global.resourceString("End_Game"),Global.resourceString("Count"),false,false);
		PF=pf;
		Col=col; TotalTime=tt; ExtraTime=et; ExtraMoves=em;
		BlackTime=TotalTime; WhiteTime=TotalTime;
		Handicap=ha;
		BlackRun=0; WhiteRun=0;
		Started=false; Ended=false;
		if (Col==1) BlackName=Global.resourceString("You");
		else BlackName=Global.resourceString("Opponent");
		if (Col==-1) WhiteName=Global.resourceString("You");
		else WhiteName=Global.resourceString("Opponent");
	}

	public void doAction (String o)
	{	if (Global.resourceString("Send").equals(o) || (ExtraSendField && Global.resourceString("ExtraSendField").equals(o)))
		{	if (ExtraSendField)
			{	if (!SendField.getText().equals(""))
				{	PF.out(SendField.getText());
					SendField.remember(SendField.getText());
					SendField.setText("");
				}
				return;
			}
			else
			{	new PartnerSendQuestion(this,PF);
				return;
			}
		}
		else if (Global.resourceString("End_Game").equals(o))
		{	if (Col!=B.maincolor()) return;
			PF.endgame();
			return;
		}
		else if (Global.resourceString("Count").equals(o))
		{	if (Ended || !B.ismain())
			{	String s=B.done();
				if (s!=null) new Message(this,s);
			}
			return;
		}
		else if (Global.resourceString("Undo").equals(o))
		{	if (Ended || !B.ismain()) B.undo();
			else
			{	if (Col!=B.maincolor()) return;
				B.undo();
			}
			return;
		}
		else if (Global.resourceString("Undo_Adding_Removing").equals(o))
		{	B.clearremovals();
			return;
		}
		else super.doAction(o);
	}

	public boolean blocked ()
	{	return false;
	}

	public boolean wantsmove ()
	{	return true;
	}

	public boolean moveset (int i, int j)
	{	if (!Started || Ended) return false;
		String color;
		if (B.maincolor()!=Col) return false;
		if (B.maincolor()>0) color="b";
		else color="w";
		if (Timer.isAlive()) alarm();
		int bm=BlackMoves,wm=WhiteMoves;
		if (Col>0) { if (BlackMoves>0) BlackMoves--; }
		else { if (WhiteMoves>0) WhiteMoves--; }
		if (!PF.moveset(color,i,j,BlackTime-BlackRun,BlackMoves,
			WhiteTime-WhiteRun,WhiteMoves))
		{	BlackMoves=bm; WhiteMoves=wm;
			if (Timer.isAlive()) alarm();
			return false;
		}
		return true;
	}

	public void movepass ()
	{	if (!Started || Ended) return;
		if (B.maincolor()!=Col) return;
		if (Timer.isAlive()) alarm();
		if (Col>0) { if (BlackMoves>0) BlackMoves--; }
		else { if (WhiteMoves>0) WhiteMoves--; }		
		PF.pass(BlackTime-BlackRun,BlackMoves,
			WhiteTime-WhiteRun,WhiteMoves);
	}

	public void dopass ()
	{	B.setpass();
	}

	public void undo () { PF.undo(); }

	public void undo (int n)
	{	B.undo(n);
	}

	public void settimes (int bt, int bm, int wt, int wm)
	{	BlackTime=bt; BlackRun=0;
		WhiteTime=wt; WhiteRun=0;
		WhiteMoves=wm; BlackMoves=bm;
		CurrentTime=System.currentTimeMillis();
		settitle();
	}

	public void doclose ()
	{	setVisible(false); dispose();
		PF.toFront();
		PF.boardclosed(this);
		PF.PGF=null;
	}

	String OldS="";
	void settitle ()
	{	String S;
		if (BigTimer) 
			S=WhiteName+" "+formmoves(WhiteMoves)+" - "+
			BlackName+" "+formmoves(BlackMoves);
		else
			S=WhiteName+" "+formtime(WhiteTime-WhiteRun)+" "+formmoves(WhiteMoves)+" - "+
			BlackName+" "+formtime(BlackTime-BlackRun)+" "+formmoves(BlackMoves);
		if (!S.equals(OldS))
		{	if (!TimerInTitle) TL.setText(S);
			else setTitle(S);
			OldS=S;
		}
		if (BigTimer)
		{	BL.setTime(WhiteTime-WhiteRun,BlackTime-BlackRun,WhiteMoves,BlackMoves,Col);
			BL.repaint();
		}
		if (Col>0 && B.maincolor()>0) beep(BlackTime-BlackRun);
		if (Col<0 && B.maincolor()<0) beep(WhiteTime-WhiteRun);
	}

	char form[]=new char[32];

	String formmoves (int m)
	{	if (m<0) return "";
		form[0]='(';
		int n=OutputFormatter.formint(form,1,m);
		form[n++]=')';
		return new String(form,0,n);
	}
	
	String formtime (int sec)
	{	int n=OutputFormatter.formtime(form,sec);
		return new String(form,0,n);
	}

	public void alarm ()
	{	long now=System.currentTimeMillis();
		if (B.maincolor()>0) BlackRun=(int)((now-CurrentTime)/1000);
		else WhiteRun=(int)((now-CurrentTime)/1000);
		if (Col>0 && BlackTime-BlackRun<0)
		{	if (BlackMoves<0)
			{	BlackMoves=ExtraMoves;
				BlackTime=ExtraTime; BlackRun=0;
				CurrentTime=now;
			}
			else if (BlackMoves>0)
			{	new Message(this,Global.resourceString("Black_looses_by_time_"));
				Timer.stopit();
			}
			else
			{	BlackMoves=ExtraMoves;
				BlackTime=ExtraTime; BlackRun=0;
				CurrentTime=now;
			}
		}
		else if (Col<0 && WhiteTime-WhiteRun<0)
		{	if (WhiteMoves<0)
			{	WhiteMoves=ExtraMoves;
				WhiteTime=ExtraTime; WhiteRun=0;
				CurrentTime=now;
			}
			else if (WhiteMoves>0)
			{	new Message(this,Global.resourceString("White_looses_by_time_"));
				Timer.stopit();
			}
			else
			{	WhiteMoves=ExtraMoves;
				WhiteTime=ExtraTime; WhiteRun=0;
				CurrentTime=now;
			}
		}
		settitle();
	}

	int lastbeep=0;
	public void beep (int s)
	{	if (s<0 || !Global.getParameter("warning",true)) return;
		else if (s<31 && s!=lastbeep)
		{	if (s%10==0)
			{	getToolkit().beep();
				lastbeep=s;
			}
		}
	}

	int maincolor ()
	{	return B.maincolor();
	}

	void start ()
	{	Started=true; Ended=false;
		CurrentTime=System.currentTimeMillis();
		BlackRun=0; WhiteRun=0;
		BlackMoves=-1; WhiteMoves=-1;
		Timer=new GoTimer(this,100);
		if (Handicap>0) B.handicap(Handicap);
	}
	
	void setHandicap ()
	{	if (Handicap>0) B.handicap(Handicap);
		Handicap=0;
	}

	void doscore ()
	{	B.score();
		Timer.stopit();
		Ended=true;
	}

	public void result (int b, int w)
	{	PF.out("@@result "+b+" "+w);
	}

	public void addtime (int s)
	{	if (Col>0) BlackTime+=s;
		else WhiteTime+=s;
		settitle();
	}

	public void addothertime (int s)
	{	if (Col>0) WhiteTime+=s;
		else BlackTime+=s;
		settitle();
	}

	public void yourMove (boolean notinpos)
	{	if (notinpos) JagoSound.play("yourmove","stone",true);
		else JagoSound.play("stone","click",false);
	}
}

