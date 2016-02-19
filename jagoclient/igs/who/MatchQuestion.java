package jagoclient.igs.who;

import java.io.*;
import java.awt.*;

import jagoclient.gui.*;
import jagoclient.igs.*;
import jagoclient.dialogs.*;
import jagoclient.Global;

/**
Ask to match the chosen user.
*/

public class MatchQuestion extends CloseDialog
{	ConnectionFrame F;
	TextField T;
	TextField User;
	IntField BoardSize,TotalTime,ExtraTime;
	Choice ColorChoice;
	/**
	@param f the ConnectionFrame, which holds the connection.
	*/
	public MatchQuestion (Frame fr, ConnectionFrame f, String user)
	{	super(fr,Global.resourceString("Match"),false);
		F=f;
		Panel pp=new MyPanel();
		pp.setLayout(new GridLayout(0,2));
		pp.add(new MyLabel(Global.resourceString("Opponent")));
		pp.add(User=new GrayTextField(user));
		pp.add(new MyLabel(Global.resourceString("Board_Size")));
		pp.add(BoardSize=new IntField(this,"BoardSize",19));
		pp.add(new MyLabel(Global.resourceString("Your_Color")));
		pp.add(ColorChoice=new Choice());
		ColorChoice.setFont(Global.SansSerif);
		ColorChoice.add(Global.resourceString("Black"));
		ColorChoice.add(Global.resourceString("White"));
		ColorChoice.select(0);
		pp.add(new MyLabel(Global.resourceString("Time__min_")));
		pp.add(TotalTime=new IntField(this,"TotalTime",Global.getParameter("totaltime",10)));
		pp.add(new MyLabel(Global.resourceString("Extra_Time")));
		pp.add(ExtraTime=new IntField(this,"ExtraTime",Global.getParameter("extratime",10)));
		add("Center",new Panel3D(pp));
		Panel p=new MyPanel();
		p.add(new ButtonAction(this,Global.resourceString("Match")));
		p.add(new ButtonAction(this,Global.resourceString("Cancel")));
		add("South",new Panel3D(p));
		Global.setpacked(this,"match",200,150,f);
		validate(); show();
	}
	public void doAction (String o)
	{	Global.setParameter("matchwidth",getSize().width);
		Global.setParameter("matchheight",getSize().height);
		if (Global.resourceString("Match").equals(o))
		{	String s="b";
			if (ColorChoice.getSelectedIndex()==1) s="w";
			F.out("match "+User.getText()+" "+s+" "+BoardSize.value(5,29)+" "+
				TotalTime.value(0,6000)+" "+
				ExtraTime.value(0,6000));
			Global.setParameter("totaltime",TotalTime.value(0,6000));
			Global.setParameter("extratime",ExtraTime.value(0,6000));
			setVisible(false); dispose();
		}
		else if (Global.resourceString("Cancel").equals(o))
		{	setVisible(false); dispose();
		}
		else super.doAction(o);
	}
}
