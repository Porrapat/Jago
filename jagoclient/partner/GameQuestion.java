package jagoclient.partner;

import java.awt.*;
import java.net.*;
import java.io.*;

import jagoclient.dialogs.*;
import jagoclient.gui.*;
import jagoclient.Global;
import jagoclient.Dump;

/**
Question to start a game with user definable paramters (handicap etc.)
*/

public class GameQuestion extends CloseDialog
{	FormTextField BoardSize,Handicap,TotalTime,ExtraTime,ExtraMoves;
	Choice ColorChoice;
	PartnerFrame PF;
	public GameQuestion (PartnerFrame pf)
	{	super(pf,Global.resourceString("Game_Setup"),true);
		PF=pf;
		Panel pa=new MyPanel();
		add("Center",new Panel3D(pa));
		pa.setLayout(new GridLayout(0,2));
		pa.add(new MyLabel(Global.resourceString("Board_size")));
		pa.add(BoardSize=new FormTextField("19"));
		pa.add(new MyLabel(Global.resourceString("Your_color")));
		pa.add(ColorChoice=new Choice());
		ColorChoice.setFont(Global.SansSerif);
		ColorChoice.add(Global.resourceString("Black"));
		ColorChoice.add(Global.resourceString("White"));
		ColorChoice.select(0);
		pa.add(new MyLabel(Global.resourceString("Handicap")));
		pa.add(Handicap=new FormTextField("0"));
		pa.add(new MyLabel(Global.resourceString("Total_Time__min_")));
		pa.add(TotalTime=new FormTextField("10"));
		pa.add(new MyLabel(Global.resourceString("Extra_Time__min_")));
		pa.add(ExtraTime=new FormTextField("10"));
		pa.add(new MyLabel(Global.resourceString("Moves_per_Extra_Time")));
		pa.add(ExtraMoves=new FormTextField("24"));
		Panel pb=new MyPanel();
		pb.add(new ButtonAction(this,Global.resourceString("Request")));
		pb.add(new ButtonAction(this,Global.resourceString("Cancel")));
		add("South",new Panel3D(pb));
		Global.setpacked(this,"gamequestion",300,400,pf);
		validate();
		show();
	}
	
	public void doAction (String o)
	{	Global.notewindow(this,"gamequestion");	
		if (Global.resourceString("Request").equals(o))
		{	int s,h,tt,et,em;
			try
			{	s=Integer.parseInt(BoardSize.getText());
				h=Integer.parseInt(Handicap.getText());
				tt=Integer.parseInt(TotalTime.getText());
				et=Integer.parseInt(ExtraTime.getText());
				em=Integer.parseInt(ExtraMoves.getText());
			}
			catch (NumberFormatException ex)
			{	new Message(PF,Global.resourceString("Illegal_Number_Format_"));
				return;
			}
			if (s<5) s=5;
			if (s>29) s=29;
			if (h>9) h=9;
			if (h<0) h=0;
			if (tt<1) tt=1;
			if (et<0) et=0;
			if (em<1) em=1;
			String col="b";
			if (ColorChoice.getSelectedIndex()!=0) col="w";
			PF.dorequest(s,col,h,tt,et,em);
			setVisible(false); dispose();
		}
		else if (Global.resourceString("Cancel").equals(o))
		{	setVisible(false); dispose();
		}
		else super.doAction(o);
	}
}

