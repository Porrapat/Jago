package jagoclient.board;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import jagoclient.gui.*;
import jagoclient.Global;

import rene.gui.IconBar;

/**
A subclass of GoFrame, which has a different menu layout.
Moreover, it contains a method to add a comment from an
external source (a distributor).
*/

public class ConnectedGoFrame extends GoFrame
{	protected boolean TimerInTitle,ExtraSendField,BigTimer;
	protected HistoryTextField SendField;
	protected Label TL;
	protected BigTimerLabel BL;
	protected Menu FileMenu,Options;
	protected Panel CommentPanel;
	protected TextArea AllComments;
	CardLayout CommentPanelLayout;
	protected CheckboxMenuItem KibitzWindow,Teaching,Silent;
	/**
	Will modify the menues of the GoFrame.
	"endgame" is used for for the menu entry to end a game
	(e.g. "remove groups").
	"count" is the string to count a game (e.g. "send done").
	<P>
	Optionally, the comment uses a card layout and a second
	text area to hold the Kibitz window.
	*/
	public ConnectedGoFrame
		(String s, int si, String endgame, String count,
		boolean kibitzwindow, boolean canteach)
	{	super(s);
		setLayout(new BorderLayout());
		TimerInTitle=Global.getParameter("timerintitle",true);
		ExtraSendField=Global.getParameter("extrasendfield",true);
		BigTimer=Global.getParameter("bigtimer",true);
		// Menu
		MenuBar M=new MenuBar();
		setMenuBar(M);
		Menu file=new MyMenu(Global.resourceString("File"));
		FileMenu=file;
		M.add(file);
		file.add(new MenuItemAction(this,Global.resourceString("Save")));
		file.addSeparator();
		file.add(UseXML=
			new CheckboxMenuItemAction(this,Global.resourceString("Use_XML")));
		UseXML.setState(Global.getParameter("xml",false));
		file.add(UseSGF=
			new CheckboxMenuItemAction(this,Global.resourceString("Use_SGF")));
		UseSGF.setState(!Global.getParameter("xml",false));
		file.addSeparator();
		file.add(new MenuItemAction(this,Global.resourceString("Copy_to_Clipboard")));
		file.addSeparator();
		file.add(new MenuItemAction(this,Global.resourceString("Mail")));
		file.add(new MenuItemAction(this,Global.resourceString("Ascii_Mail")));
		file.add(new MenuItemAction(this,Global.resourceString("Print")));
		file.add(new MenuItemAction(this,Global.resourceString("Save_Bitmap")));
		file.addSeparator();
		file.add(new MenuItemAction(this,Global.resourceString("Refresh")));
		file.addSeparator();
		file.add(
			ShowButtons=new CheckboxMenuItemAction(this,
				Global.resourceString("Show_Buttons")));
		ShowButtons.setState(Global.getParameter("showbuttonsconnected",true));
		/*
		if (canteach)
		{	file.addSeparator();
			Menu teaching=new MyMenu(Global.resourceString("Teaching"));
			teaching.add(Teaching=new CheckboxMenuItemAction(this,Global.resourceString("Teaching_On")));
			Teaching.setState(false);
			teaching.add(new MenuItemAction(this,Global.resourceString("Load_Teaching_Game")));
			file.add(teaching);
		}
		*/
		file.addSeparator();
		file.add(new MenuItemAction(this,Global.resourceString("Close")));
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
		Menu var=new MyMenu(Global.resourceString("Variations"));
		var.add(new MenuItemAction(this,Global.resourceString("Insert_Node")));
		var.add(new MenuItemAction(this,Global.resourceString("Insert_Variation")));
		var.addSeparator();
		var.add(new MenuItemAction(this,Global.resourceString("Next_Game")));
		var.add(new MenuItemAction(this,Global.resourceString("Previous_Game")));
		var.addSeparator();
		var.add(new MenuItemAction(this,Global.resourceString("Set_Search_String")));
		var.add(new MenuItemAction(this,Global.resourceString("Search_Comment")));
		var.addSeparator();
		var.add(new MenuItemAction(this,Global.resourceString("Node_Name")));
		M.add(var);
		Menu score=new MyMenu(Global.resourceString("Finish_Game"));
		M.add(score);
		if (!endgame.equals("")) score.add(new MenuItemAction(this,endgame));
		score.add(new MenuItemAction(this,Global.resourceString("Local_Count")));
		score.addSeparator();
		score.add(new MenuItemAction(this,Global.resourceString("Prisoner_Count")));
		if (!count.equals("")) score.add(new MenuItemAction(this,count));
		score.addSeparator();
		score.add(new MenuItemAction(this,Global.resourceString("Game_Information")));
		score.add(new MenuItemAction(this,Global.resourceString("Game_Copyright")));
		Menu options=new MyMenu(Global.resourceString("Options"));
		Options=options;
		options.add(Silent=new CheckboxMenuItemAction(this,Global.resourceString("Silent")));
		Silent.setState(Global.getParameter("boardsilent",false));
		if (Silent.getState()) Global.Silent++;
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
		fonts.add(new MenuItemAction(this,Global.resourceString("Fixed_Font")));
		fonts.add(new MenuItemAction(this,Global.resourceString("Board_Font")));
		fonts.add(new MenuItemAction(this,Global.resourceString("Normal_Font")));
		options.add(fonts);
		Menu variations=new MyMenu(Global.resourceString("Variation_Display"));
		variations.add(VHide=new CheckboxMenuItemAction(this,
			Global.resourceString("Hide")));
		VHide.setState(Global.getParameter("vhide",false));
		variations.add(VCurrent=new CheckboxMenuItemAction(this,
			Global.resourceString("To_Current")));
		VCurrent.setState(Global.getParameter("vcurrent",true));
		variations.add(VChild=new CheckboxMenuItemAction(this,
			Global.resourceString("To_Child")));
		VChild.setState(!Global.getParameter("vcurrent",true));
		variations.addSeparator();
		variations.add(VNumbers=new CheckboxMenuItemAction(this,
			Global.resourceString("Continue_Numbers")));
		VNumbers.setState(Global.getParameter("variationnumbers",false));
		options.add(variations);
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
		if (kibitzwindow)
		{	options.addSeparator();
			options.add(KibitzWindow=new CheckboxMenuItemAction(this,Global.resourceString("Kibitz_Window")));
			KibitzWindow.setState(Global.getParameter("kibitzwindow",true));
			options.add(new MenuItemAction(this,Global.resourceString("Clear_Kibitz_Window")));
		}
		M.add(options);
		Menu help=new MyMenu(Global.resourceString("Help"));
		help.add(new MenuItemAction(this,Global.resourceString("Board_Window")));
		help.add(new MenuItemAction(this,Global.resourceString("Making_Moves")));
		help.add(new MenuItemAction(this,Global.resourceString("Keyboard_Shortcuts")));
		help.add(new MenuItemAction(this,Global.resourceString("About_Variations")));
		help.add(new MenuItemAction(this,Global.resourceString("Playing_Games")));
		help.add(new MenuItemAction(this,Global.resourceString("Mailing_Games")));
		M.setHelpMenu(help);
		// Board
		L=new MyLabel(Global.resourceString("New_Game"));
		Lm=new MyLabel("--");
		Comment=new MyTextArea("",0,0,TextArea.SCROLLBARS_VERTICAL_ONLY);
		Comment.setFont(Global.SansSerif);
		CommentPanel=new MyPanel();
		if (kibitzwindow)
		{	AllComments=new MyTextArea("",0,0,TextArea.SCROLLBARS_VERTICAL_ONLY);
			AllComments.setEditable(false);
            CommentPanel.setLayout(CommentPanelLayout=new CardLayout());
			CommentPanel.add("Comment",Comment);
			CommentPanel.add("AllComments",AllComments);
		}
		else
		{	CommentPanel.setLayout(new BorderLayout());
			CommentPanel.add("Center",Comment);
		}
		B=new ConnectedBoard(si,this);
		Panel BP=new MyPanel();
		BP.setLayout(new BorderLayout());
		BP.add("Center",B);
		// Add the label
		Panel bpp=new MyPanel();
		bpp.setLayout(new GridLayout(0,1));
		SimplePanel sp=
			new SimplePanel((Component)L,80,(Component)Lm,20);
		bpp.add(sp);
		sp.setBackground(Global.gray);
		// Add the timer label
		if (!TimerInTitle)
		{	SimplePanel btl=new SimplePanel(
				TL=new MyLabel(Global.resourceString("Start")),80,new MyLabel(""),20);
			bpp.add(btl);
			btl.setBackground(Global.gray);
		}
		BP.add("South",bpp);
		// Text Area
		Panel cp=new MyPanel();
		cp.setLayout(new BorderLayout());
		Comment.setBackground(Global.gray);
		cp.add("Center",CommentPanel);
		if (kibitzwindow && KibitzWindow.getState())
			CommentPanelLayout.show(CommentPanel,"AllComments");
		// Add the extra send field
		if (ExtraSendField)
		{	SendField=new HistoryTextField(this,"SendField");
			cp.add("South",SendField);
		}
		Panel bcp=new BoardCommentPanel(BP,cp,B);
		add("Center",bcp);
		// Add the big timer label
		if (BigTimer)
		{	BL=new BigTimerLabel();
			cp.add("North",BL);
		}
		// Navigation panel
		IB=createIconBar();
		ButtonP=new Panel3D(IB);
		if (Global.getParameter("showbuttonsconnected",true))
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

	public IconBar createIconBar ()
	{	IconBar I=new IconBar(this);
		I.Resource="/jagoclient/icons/";
		I.addLeft("undo");
		addSendForward(I);
		I.addSeparatorLeft();
		I.addLeft("allback");
		I.addLeft("fastback");
		I.addLeft("back");
		I.addLeft("forward");
		I.addLeft("fastforward");
		I.addLeft("allforward");
		I.addSeparatorLeft();
		I.addLeft("variationback");
		I.addLeft("variationstart");
		I.addLeft("variationforward");
		I.addLeft("main");
		I.addLeft("mainend");
		I.addSeparatorLeft();
		I.addLeft("send");
		I.setIconBarListener(this);
		return I;
	}
	
	public void addSendForward(IconBar I)
	{
	}
	
	public void iconPressed (String s)
	{	if (s.equals("send")) doAction(Global.resourceString("Send"));
		else super.iconPressed(s);
	}
	
	public void addComment (String s)
	// add a comment to the board (called from external sources)
	{	B.addcomment(s);
		if (AllComments!=null) AllComments.append(s+"\n");
	}

	public void addtoallcomments (String s)
	// add something to the allcomments window
	{	if (AllComments!=null) AllComments.append(s+"\n");
	}

	public void doAction (String o)
	{	if (o.equals(Global.resourceString("Clear_Kibitz_Window")))
		{	AllComments.setText("");
		}
		else super.doAction(o);
	}

	public void itemAction (String o, boolean flag)
	{	if (Global.resourceString("Kibitz_Window").equals(o))
		{	if (KibitzWindow.getState())
		        CommentPanelLayout.show(CommentPanel,"AllComments");
			else
			    CommentPanelLayout.show(CommentPanel,"Comment");
			Global.setParameter("kibitzwindow",KibitzWindow.getState());
		}
		else if (Global.resourceString("Silent").equals(o))
		{	if (flag) Global.Silent++;
			else Global.Silent--;
			Global.setParameter("boardsilent",flag);
		}
		else super.itemAction(o,flag);
	}
	
	public void windowOpened (WindowEvent e)
	{	if (ExtraSendField) SendField.requestFocus();
	}
	
	public void activate ()
	{
	}
	
	public boolean wantsmove ()
	{	return false;
	}
	
	public void doclose ()
	{	Global.Silent--;
		super.doclose();
	}
}
