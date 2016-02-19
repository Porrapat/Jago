package jagoclient.board;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.net.*;
import java.awt.datatransfer.*;

import jagoclient.gui.*;
import jagoclient.dialogs.*;
import jagoclient.Global;
import jagoclient.BMPFile;
import jagoclient.Go;

import rene.util.*;
import rene.util.list.*;
import rene.util.mail.*;
import rene.util.xml.*;

import jagoclient.mail.*;

/**
A GoFrame for local boards in Applets.
*/

public class JagoGameFrame extends GoFrame
{	public JagoGameFrame (Frame f, String s)
		// Constructur for local board menus.
	{	super(s);
		// Colors
		setcolors();
		seticon("iboard.gif");
		setLayout(new BorderLayout());
		// Menu
		MenuBar M=new MenuBar();
		setMenuBar(M);
		Menu file=new MyMenu(Global.resourceString("File"));
		file.add(new MenuItemAction(this,Global.resourceString("Prisoner_Count")));
		file.addSeparator();
		file.add(
			ShowButtons=new CheckboxMenuItemAction(this,
			Global.resourceString("Show_Buttons")));
		ShowButtons.setState(Global.getParameter("showbuttons",true));
		file.addSeparator();
		file.add(new MenuItemAction(this,Global.resourceString("Close")));
		M.add(file);
		Menu set=new MyMenu(Global.resourceString("Set"));
		M.add(set);
		set.add(Mark=new CheckboxMenuItemAction(this,Global.resourceString("Mark")));
		set.add(Letter=new CheckboxMenuItemAction(this,Global.resourceString("Letter")));
		set.add(Hide=new CheckboxMenuItemAction(this,Global.resourceString("Delete")));
		Menu mark=new MyMenu(Global.resourceString("Special_Mark"));
		mark.add(Square=new CheckboxMenuItemAction(this,Global.resourceString("Square")));
		mark.add(Circle=new CheckboxMenuItemAction(this,Global.resourceString("Circle")));
		mark.add(Triangle=new CheckboxMenuItemAction(this,Global.resourceString("Triangle")));
		mark.add(Cross=new CheckboxMenuItemAction(this,Global.resourceString("Cross")));
		mark.addSeparator();
		mark.add(TextMark=new CheckboxMenuItemAction(this,Global.resourceString("Text")));
		set.add(mark);
		set.addSeparator();
		set.add(new MenuItemAction(this,Global.resourceString("Resume_playing")));
		set.addSeparator();
		set.add(new MenuItemAction(this,Global.resourceString("Pass")));
		set.addSeparator();
		set.add(SetBlack=new CheckboxMenuItemAction(this,Global.resourceString("Set_Black")));
		set.add(SetWhite=new CheckboxMenuItemAction(this,Global.resourceString("Set_White")));
		set.addSeparator();
		set.add(Black=new CheckboxMenuItemAction(this,Global.resourceString("Black_to_play")));
		set.add(White=new CheckboxMenuItemAction(this,Global.resourceString("White_to_play")));
		set.addSeparator();
		set.add(new MenuItemAction(this,Global.resourceString("Undo_Adding_Removing")));
		set.add(new MenuItemAction(this,Global.resourceString("Clear_all_marks")));
		Menu var=new MyMenu(Global.resourceString("Nodes"));
		var.add(new MenuItemAction(this,Global.resourceString("Insert_Node")));
		var.add(new MenuItemAction(this,Global.resourceString("Insert_Variation")));
		var.addSeparator();
		var.add(new MenuItemAction(this,Global.resourceString("Search")));
		var.add(new MenuItemAction(this,Global.resourceString("Search_Again")));
		var.addSeparator();
		var.add(new MenuItemAction(this,Global.resourceString("Node_Name")));
		var.add(new MenuItemAction(this,Global.resourceString("Goto_Next_Name")));
		var.add(new MenuItemAction(this,Global.resourceString("Goto_Previous_Name")));
		M.add(var);
		Menu score=new MyMenu(Global.resourceString("Finish_Game"));
		M.add(score);
		score.add(new MenuItemAction(this,Global.resourceString("Remove_groups")));
		score.add(new MenuItemAction(this,Global.resourceString("Score")));
		score.addSeparator();
		score.add(new MenuItemAction(this,Global.resourceString("Game_Information")));
		score.add(new MenuItemAction(this,Global.resourceString("Game_Copyright")));
		Menu options=new MyMenu(Global.resourceString("Options"));
		Menu mc=new MyMenu(Global.resourceString("Coordinates"));
		mc.add(Coordinates=new CheckboxMenuItemAction(this,Global.resourceString("On")));
		Coordinates.setState(Global.getParameter("coordinates",true));
		mc.add(UpperLeftCoordinates=new CheckboxMenuItemAction(this,Global.resourceString("Upper_Left")));
		UpperLeftCoordinates.setState(Global.getParameter("upperleftcoordinates",true));
		mc.add(LowerRightCoordinates=new CheckboxMenuItemAction(this,Global.resourceString("Lower_Right")));
		LowerRightCoordinates.setState(
			Global.getParameter("lowerrightcoordinates",false));
		options.add(mc);
		options.addSeparator();
		Menu colors=new MyMenu(Global.resourceString("Colors"));
		colors.add(new MenuItemAction(this,Global.resourceString("Board_Color")));
		colors.add(new MenuItemAction(this,Global.resourceString("Black_Color")));
		colors.add(new MenuItemAction(this,Global.resourceString("Black_Sparkle_Color")));
		colors.add(new MenuItemAction(this,Global.resourceString("White_Color")));
		colors.add(new MenuItemAction(this,Global.resourceString("White_Sparkle_Color")));
		colors.add(new MenuItemAction(this,Global.resourceString("Label_Color")));
		colors.add(new MenuItemAction(this,Global.resourceString("Marker_Color")));
		options.add(colors);
		options.add(MenuBWColor=new CheckboxMenuItemAction(this,Global.resourceString("Use_B_W_marks")));
		MenuBWColor.setState(Global.getParameter("bwcolor",false));
		BWColor=MenuBWColor.getState();
		options.add(PureSGF=new CheckboxMenuItemAction(this,Global.resourceString("Save_pure_SGF")));
		PureSGF.setState(Global.getParameter("puresgf",false));
		options.add(CommentSGF=new CheckboxMenuItemAction(this,Global.resourceString("Use_SGF_Comments")));
		CommentSGF.setState(Global.getParameter("sgfcomments",false));
		options.addSeparator();
		Menu fonts=new MyMenu(Global.resourceString("Fonts"));
		fonts.add(new MenuItemAction(this,Global.resourceString("Board_Font")));
		fonts.add(new MenuItemAction(this,Global.resourceString("Fixed_Font")));
		fonts.add(new MenuItemAction(this,Global.resourceString("Normal_Font")));
		options.add(fonts);
		options.addSeparator();
		options.add(MenuTarget=new CheckboxMenuItemAction(this,Global.resourceString("Show_Target")));
		MenuTarget.setState(Global.getParameter("showtarget",true));
		ShowTarget=MenuTarget.getState();
		options.add(MenuLastNumber=new CheckboxMenuItemAction(this,Global.resourceString("Last_Number")));
		MenuLastNumber.setState(Global.getParameter("lastnumber",false));
		LastNumber=MenuLastNumber.getState();
		options.add(new MenuItemAction(this,Global.resourceString("Last_50")));
		options.add(new MenuItemAction(this,Global.resourceString("Last_100")));
		options.addSeparator();
		options.add(TrueColor=new CheckboxMenuItemAction(this,Global.resourceString("True_Color_Board")));
		TrueColor.setState(Global.getParameter("beauty",true));
		options.add(TrueColorStones=new CheckboxMenuItemAction(this,Global.resourceString("True_Color_Stones")));
		TrueColorStones.setState(Global.getParameter("beautystones",true));
		options.add(Alias=new CheckboxMenuItemAction(this,Global.resourceString("Anti_alias_Stones")));
		Alias.setState(Global.getParameter("alias",true));
		options.add(Shadows=new CheckboxMenuItemAction(this,Global.resourceString("Shadows")));
		Shadows.setState(Global.getParameter("shadows",true));
		options.add(SmallerStones=new CheckboxMenuItemAction(this,Global.resourceString("Smaller_Stones")));
		SmallerStones.setState(Global.getParameter("smallerstones",false));
		options.add(BlackOnly=new CheckboxMenuItemAction(this,Global.resourceString("Black_Only")));
		BlackOnly.setState(Global.getParameter("blackonly",false));
		M.add(options);
		// Board
		Comment=new TextArea("",0,0,TextArea.SCROLLBARS_VERTICAL_ONLY);
		Comment.setFont(Global.SansSerif);
		Comment.setBackground(Global.gray);
		L=new MyLabel(Global.resourceString("New_Game"));
		Lm=new MyLabel("--");
		B=new Board(19,this);
		Panel BP=new MyPanel();
		BP.setLayout(new BorderLayout());
		BP.add("Center",B);
		// Add the label
		SimplePanel sp=
			new SimplePanel((Component)L,80,(Component)Lm,20);
		BP.add("South",sp);
		sp.setBackground(Global.gray);
		// Text Area
		Panel bcp=new BoardCommentPanel(BP,Comment,B);
		add("Center",bcp);
		// Navigation panel
		IB=createIconBar();
		ButtonP=new Panel3D(IB);
		if (Global.getParameter("showbuttons",true))
			add("South",ButtonP);
		// Directory for FileDialog
		Dir=new String("");
		Global.setwindow(this,"board",500,450,false);
		validate();
		Show=true;
		B.addKeyListener(this);
		show();
		repaint();
	}

}

