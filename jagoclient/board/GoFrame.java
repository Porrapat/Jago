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

import rene.gui.IconBar;
import rene.gui.IconBarListener;

/**
Display a dialog to edit game information.
*/

class EditInformation extends CloseDialog
{	Node N;
	TextField Black,White,BlackRank,WhiteRank,Date,Time,
		Komi,Result,Handicap,GameName;
	GoFrame F;
	public EditInformation (GoFrame f, Node n)
	{	super(f,Global.resourceString("Game_Information"),false);
		N=n; F=f;
		Panel p=new MyPanel();
		p.setLayout(new GridLayout(0,2));
		p.add(new MyLabel(Global.resourceString("Game_Name")));
		p.add(GameName=new FormTextField(n.getaction("GN")));
		p.add(new MyLabel(Global.resourceString("Date")));
		p.add(Date=new FormTextField(n.getaction("DT")));
		p.add(new MyLabel(Global.resourceString("Black")));
		p.add(Black=new FormTextField(n.getaction("PB")));
		p.add(new MyLabel(Global.resourceString("Black_Rank")));
		p.add(BlackRank=new FormTextField(n.getaction("BR")));
		p.add(new MyLabel(Global.resourceString("White")));
		p.add(White=new FormTextField(n.getaction("PW")));
		p.add(new MyLabel(Global.resourceString("White_Rank")));
		p.add(WhiteRank=new FormTextField(n.getaction("WR")));
		p.add(new MyLabel(Global.resourceString("Result")));
		p.add(Result=new FormTextField(n.getaction("RE")));
		p.add(new MyLabel(Global.resourceString("Time")));
		p.add(Time=new FormTextField(n.getaction("TM")));
		p.add(new MyLabel(Global.resourceString("Komi")));
		p.add(Komi=new FormTextField(n.getaction("KM")));
		p.add(new MyLabel(Global.resourceString("Handicap")));
		p.add(Handicap=new FormTextField(n.getaction("HA")));
		add("Center",p);
		Panel pb=new MyPanel();
		pb.add(new ButtonAction(this,Global.resourceString("OK")));
		pb.add(new ButtonAction(this,Global.resourceString("Cancel")));
		add("South",pb);
		Global.setpacked(this,"editinformation",350,450);
		show();
	}
	public void doAction (String o)
	{	Global.notewindow(this,"editinformation");
		if (Global.resourceString("OK").equals(o))
		{	N.setaction("GN",GameName.getText());
			N.setaction("PB",Black.getText());
			N.setaction("PW",White.getText());
			N.setaction("BR",BlackRank.getText());
			N.setaction("WR",WhiteRank.getText());
			N.setaction("DT",Date.getText());
			N.setaction("TM",Time.getText());
			N.setaction("KM",Komi.getText());
			N.setaction("RE",Result.getText());
			N.setaction("HA",Handicap.getText());
			if (!GameName.getText().equals(""))
				F.setTitle(GameName.getText());
		}
		setVisible(false); dispose();
	}
}

/**
A dialog to get the present encoding.
*/

class GetEncoding extends GetParameter
{	GoFrame GCF;
	public GetEncoding (GoFrame gcf)
	{	super(gcf,Global.resourceString("Encoding__empty__default_"),
		Global.resourceString("Encoding"),gcf,true,"encoding");
		if (!Global.isApplet())
			set(Global.getParameter("encoding",
			System.getProperty("file.encoding")));
		GCF=gcf;
		show();
	}
	public boolean tell (Object o, String S)
	{	if (S.equals("")) Global.removeParameter("encoding");
		else Global.setParameter("encoding",S);
		return true;
	}
}

class GetSearchString extends CloseDialog
{	GoFrame GF;
	TextFieldAction T;
	static boolean Active=false;
	public GetSearchString (GoFrame gf)
	{	super(gf,Global.resourceString("Search"),false);
		if (Active) return;
		add("North",new MyLabel(Global.resourceString("Search_String")));
		add("Center",T=new TextFieldAction(this,"Input",25));
		Panel p=new MyPanel();
		p.add(new ButtonAction(this,Global.resourceString("Search")));
		p.add(new ButtonAction(this,Global.resourceString("Cancel")));
		add("South",p);
		Global.setpacked(this,"getparameter",300,150);
		validate();
		T.addKeyListener(this);
		T.setText(Global.getParameter("searchstring","++"));
		GF=gf;
		show();
		Active=true;
	}
	public void doAction (String s)
	{	if (s.equals(Global.resourceString("Search"))
		|| s.equals("Input"))
		{	Global.setParameter("searchstring",T.getText());
			GF.search();
		}
		else if (s.equals(Global.resourceString("Cancel")))
		{	setVisible(false); dispose(); Active=false;
		}
	}
	public void dispose ()
	{	Active=false;
		super.dispose();
	}
}

/**
Ask the user for permission to close the board frame.
*/

class CloseQuestion extends Question
{	GoFrame GF;
	boolean Result=false;
	public CloseQuestion (GoFrame g)
	{	super(g,Global.resourceString("Really_trash_this_board_"),
		Global.resourceString("Close_Board"),g,true);
		GF=g;
		show();
	}
	public void tell (Question q, Object o, boolean f)
	{	q.setVisible(false); q.dispose();
		Result=f;
	}
}

class SizeQuestion extends GetParameter
// Ask the board size.
{	public SizeQuestion (GoFrame g)
	{	super(g,Global.resourceString("Size_between_5_and_29"),
			Global.resourceString("Board_size"),g,true);
		show();
	}
	public boolean tell (Object  o, String s)
	{	int n;
		try
		{	n=Integer.parseInt(s);
			if (n<5 || n>59) return false;
		}
		catch (NumberFormatException e)
		{	return false;
		}
		((GoFrame)o).doboardsize(n);
		return true;
	}
}

/**
Ask the user for permission to close the board frame.
*/

class TextMarkQuestion extends CloseDialog
{	GoFrame G;
	TextField T;
	Checkbox C;
	public TextMarkQuestion (GoFrame g, String t)
	{	super(g,Global.resourceString("Text_Mark"),false);
		G=g;
		setLayout(new BorderLayout());
		add("Center",new SimplePanel(
			new MyLabel(Global.resourceString("String")),1,
			T=new TextFieldAction(this,t),2));
		T.setText(t);
		Panel ps=new MyPanel();
		ps.add(C=new CheckboxAction(this,Global.resourceString("Auto_Advance")));
		C.setState(Global.getParameter("autoadvance",true));
		ps.add(new ButtonAction(this,Global.resourceString("Set")));
		ps.add(new ButtonAction(this,Global.resourceString("Close")));
		add("South",ps);
		Global.setpacked(this,"gettextmarkquestion",300,150);
		show();
	}
	public void doAction (String o)
	{	Global.notewindow(this,"gettextmarkquestion");
		Global.setParameter("autoadvance",C.getState());
		if (o.equals(Global.resourceString("Set")))
		{	G.setTextmark(T.getText());
			Global.setParameter("textmark",T.getText());
		}
		else if (o.equals(Global.resourceString("Close")))
		{	close(); setVisible(false); dispose();
		}
	}
	public boolean close ()
	{	G.TMQ=null;
		return true;
	}
	public void advance ()
	{	if (!C.getState()) return;
		String s=T.getText();
		if (s.length()==1)
		{	char c=s.charAt(0);
			c++;
			T.setText(""+c);
			G.setTextmark(T.getText());
		}
		else
		{	try
			{	int n=Integer.parseInt(s);
				n=n+1;
				T.setText(""+n);
				G.setTextmark(T.getText());
			}
			catch (Exception e) {	}
		}
	}
}

/**
// Get/Set the name of the current node
*/

class NodeNameEdit extends GetParameter
{	public NodeNameEdit (GoFrame g, String s)
	{	super(g,Global.resourceString("Name"),Global.resourceString("Node_Name"),g,true);
		set(s);
		show();
	}
	public boolean tell (Object  o, String s)
	{	((GoFrame)o).setname(s);
		return true;
	}
}

/**
Let the user edit the board colors (stones, shines and board).
Redraw the board, when done with OK.
*/

class BoardColorEdit extends ColorEdit
{	GoFrame GF;
	public BoardColorEdit (GoFrame F, String s, int red, int green, int blue)
	{	super(F,s,red,green,blue,true);
		GF=F;
		show();
	}
	public BoardColorEdit (GoFrame F, String s, Color c)
	{	super(F,s,c.getRed(),c.getGreen(),c.getBlue(),true);
		GF=F;
		show();
	}
	public void doAction (String o)
	{	super.doAction(o);
		if (Global.resourceString("OK").equals(o))
		{	GF.updateall();
		}
	}
}

/**
Let the user edit the board fong
Redraw the board, when done with OK.
*/

class BoardGetFontSize extends GetFontSize
{	GoFrame GF;
	public BoardGetFontSize (GoFrame F, String fontname, String deffontname,
		String fontsize, int deffontsize, boolean flag)
	{	super(fontname,deffontname,fontsize,deffontsize,flag);
		GF=F;
	}
	public void doAction (String o)
	{	super.doAction(o);
		if (Global.resourceString("OK").equals(o))
		{	Global.createfonts();
			GF.updateall();
		}
	}
}

/**
Display a dialog to edit game copyright and user.
*/

class EditCopyright extends CloseDialog
{	TextArea Copyright;
	TextField User;
	Node N;
	public EditCopyright (GoFrame f, Node n)
	{	super(f,Global.resourceString("Copyright_of_Game"),false);
		Panel p1=new MyPanel();
		N=n;
		p1.setLayout(new GridLayout(0,2));
		p1.add(new MyLabel(Global.resourceString("User")));
		p1.add(User=new GrayTextField(n.getaction("US")));
		add("North",p1);
		Panel p2=new MyPanel();
		p2.setLayout(new BorderLayout());
		p2.add("North",new MyLabel(Global.resourceString("Copyright")));
		p2.add("Center",Copyright=new TextArea("",0,0,TextArea.SCROLLBARS_VERTICAL_ONLY));
		Copyright.setBackground(Global.gray);
		add("Center",p2);
		Panel pb=new MyPanel();
		pb.add(new ButtonAction(this,Global.resourceString("OK")));
		pb.add(new ButtonAction(this,Global.resourceString("Cancel")));
		add("South",pb);
		Global.setwindow(this,"editcopyright",350,400);
		show();
		Copyright.setText(n.getaction("CP"));
	}
	public void doAction (String o)
	{	Global.notewindow(this,"editcopyright");
		if (Global.resourceString("OK").equals(o))
		{	N.setaction("US",User.getText());
			N.setaction("CP",Copyright.getText());
		}
		setVisible(false); dispose();
	}
}

/**
Ask, if a complete subtree is to be deleted.
*/

class AskUndoQuestion extends Question
{	public boolean Result=false;
	public AskUndoQuestion (Frame f)
	{	super(f,Global.resourceString("Delete_all_subsequent_moves_"),Global.resourceString("Delete_Tree"),f,true);
		show();
	}
	public void tell (Question q, Object o, boolean f)
	{	q.setVisible(false); q.dispose();
		Result=f;
	}
}

/**
Ask, if a node is to be inserted and the tree thus changed.
*/

class AskInsertQuestion extends Question
{	public boolean Result=false;
	public AskInsertQuestion (Frame f)
	{	super(f,Global.resourceString("Change_Game_Tree_"),Global.resourceString("Change_Game_Tree"),f,true);
		show();
	}
	public void tell (Question q, Object o, boolean f)
	{	q.setVisible(false); q.dispose();
		Result=f;
	}
}

/**
The GoFrame class is a frame, which contains the board,
the comment window and the navigation buttons (at least).
<P>
This class implements BoardInterface. This is done to make clear
what routines are called from the board and to give the board a
beans appearance.
<P>
The layout is a  panel of class BoardCommentPanel, containing two panels
for the board (BoardPanel) and for the comments (plus the ExtraSendField
in ConnectedGoFrame). Below is a 3D panel for the buttons. The BoardCommentPanel
takes care of the layout for its components.
<P>
This class handles all actions in it, besides the mouse actions on the
board, which are handled by Board.
<P>
Note that the Board class modifies the appearance of buttons and
takes care of the comment window, the next move label and the board
position label.
<P>
Several private classes in GoFrame.java contain dialogs to enter game
information, copyright, text marks, etc.
@see jagoclient.board.Board
*/

// The parent class for a frame containing the board, navigation buttons
// and menus.
// This board has a constructor, which initiates menus to be used as a local
// board. For Partner of IGS games there is the ConnectedGoFrame child, which
// uses another menu structure.
// Furthermore, it has methods to handle lots of user actions.
public class GoFrame extends CloseFrame
	implements FilenameFilter, KeyListener, BoardInterface, ClipboardOwner,
	IconBarListener
{	TextArea T; // For comments
	public Label L,Lm; // For board informations
	TextArea Comment; // For comments
	String Dir; // FileDialog directory
	public Board B; // The board itself
	// menu check items:
	CheckboxMenuItem SetBlack,SetWhite,Black,White,Mark,Letter,Hide,
		Square,Cross,Circle,Triangle,TextMark;
	public Color BoardColor,BlackColor,BlackSparkleColor,
		WhiteColor,WhiteSparkleColor,MarkerColor,LabelColor;
	CheckboxMenuItem Coordinates,UpperLeftCoordinates,LowerRightCoordinates;
	CheckboxMenuItem PureSGF,CommentSGF,DoSound,BeepOnly,TrueColor,Alias,
		TrueColorStones,SmallerStones,MenuLastNumber,MenuTarget,Shadows,
		BlackOnly,UseXML,UseSGF;
	public boolean BWColor=false,LastNumber=false,ShowTarget=false;
	CheckboxMenuItem MenuBWColor,ShowButtons;
	CheckboxMenuItem VHide,VCurrent,VChild,VNumbers;
	String Text=Global.getParameter("textmark","A");
	boolean Show;
	TextMarkQuestion TMQ;
	IconBar IB;
	Panel ButtonP;
	String DefaultTitle="";
	NavigationPanel Navigation;

	public GoFrame (String s)
		// For children, who set up their own menus
	{	super(s);
		DefaultTitle=s;
		seticon("iboard.gif");
		setcolors();
	}

	void setcolors ()
	{	// Take colors from Global parameters.
		BoardColor=Global.getColor("boardcolor",170,120,70);
		BlackColor=Global.getColor("blackcolor",30,30,30);
		BlackSparkleColor=Global.getColor("blacksparklecolor",120,120,120);
		WhiteColor=Global.getColor("whitecolor",210,210,210);
		WhiteSparkleColor=Global.getColor("whitesparklecolor",250,250,250);
		MarkerColor=Global.getColor("markercolor",Color.blue);
		LabelColor=Global.getColor("labelcolor",Color.pink.darker());
		Global.setColor("boardcolor",BoardColor);
		Global.setColor("blackcolor",BlackColor);
		Global.setColor("blacksparklecolor",BlackSparkleColor);
		Global.setColor("whitecolor",WhiteColor);
		Global.setColor("whitesparklecolor",WhiteSparkleColor);
		Global.setColor("markercolor",MarkerColor);
		Global.setColor("labelcolor",LabelColor);
	}

	public GoFrame (Frame f, String s)
		// Constructur for local board menus.
	{	super(s);
		DefaultTitle=s;
		// Colors
		setcolors();
		seticon("iboard.gif");
		setLayout(new BorderLayout());
		// Menu
		MenuBar M=new MenuBar();
		setMenuBar(M);
		Menu file=new MyMenu(Global.resourceString("File"));
		M.add(file);
		file.add(new MenuItemAction(this,Global.resourceString("New")));
		file.add(new MenuItemAction(this,Global.resourceString("Load")));
		file.add(new MenuItemAction(this,Global.resourceString("Save")));
		file.add(new MenuItemAction(this,Global.resourceString("Save_Position")));
		file.addSeparator();
		file.add(UseXML=
			new CheckboxMenuItemAction(this,Global.resourceString("Use_XML")));
		UseXML.setState(Global.getParameter("xml",false));
		file.add(UseSGF=
			new CheckboxMenuItemAction(this,Global.resourceString("Use_SGF")));
		UseSGF.setState(!Global.getParameter("xml",false));
		file.addSeparator();
		file.add(new MenuItemAction(this,Global.resourceString("Load_from_Clipboard")));
		file.add(new MenuItemAction(this,Global.resourceString("Copy_to_Clipboard")));
		file.addSeparator();
		file.add(new MenuItemAction(this,Global.resourceString("Mail")));
		file.add(new MenuItemAction(this,Global.resourceString("Ascii_Mail")));
		file.add(new MenuItemAction(this,Global.resourceString("Print")));
		file.add(new MenuItemAction(this,Global.resourceString("Save_Bitmap")));
		file.addSeparator();
		file.add(new MenuItemAction(this,Global.resourceString("Board_size")));
		file.addSeparator();
		file.add(new MenuItemAction(this,Global.resourceString("Add_Game")));
		file.add(new MenuItemAction(this,Global.resourceString("Remove_Game")));
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
		Menu var=new MyMenu(Global.resourceString("Nodes"));
		var.add(new MenuItemAction(this,Global.resourceString("Insert_Node")));
		var.add(new MenuItemAction(this,Global.resourceString("Insert_Variation")));
		var.addSeparator();
		var.add(new MenuItemAction(this,Global.resourceString("Next_Game")));
		var.add(new MenuItemAction(this,Global.resourceString("Previous_Game")));
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
		score.addSeparator();
		score.add(new MenuItemAction(this,Global.resourceString("Prisoner_Count")));
		Menu options=new MyMenu(Global.resourceString("Options"));
		Menu mc=new MyMenu(Global.resourceString("Coordinates"));
		mc.add(Coordinates=new CheckboxMenuItemAction(this,Global.resourceString("On")));
		Coordinates.setState(Global.getParameter("coordinates",true));
		mc.add(UpperLeftCoordinates=new CheckboxMenuItemAction(this,Global.resourceString("Upper_Left")));
		UpperLeftCoordinates.setState(Global.getParameter("upperleftcoordinates",true));
		mc.add(LowerRightCoordinates=new CheckboxMenuItemAction(this,Global.resourceString("Lower_Right")));
		LowerRightCoordinates.setState(
			Global.getParameter("lowerrightcoordinates",true));
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
		Menu variations=new MyMenu(Global.resourceString("Variation_Display"));
		variations.add(VCurrent=new CheckboxMenuItemAction(this,
			Global.resourceString("To_Current")));
		VCurrent.setState(Global.getParameter("vcurrent",true));
		variations.add(VChild=new CheckboxMenuItemAction(this,
			Global.resourceString("To_Child")));
		VChild.setState(!Global.getParameter("vcurrent",true));
		variations.add(VHide=new CheckboxMenuItemAction(this,
			Global.resourceString("Hide")));
		VHide.setState(Global.getParameter("vhide",false));
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
		options.addSeparator();
		options.add(new MenuItemAction(this,Global.resourceString("Set_Encoding")));
		options.add(
			ShowButtons=new CheckboxMenuItemAction(this,
			Global.resourceString("Show_Buttons")));
		ShowButtons.setState(Global.getParameter("showbuttons",true));
		Menu help=new MyMenu(Global.resourceString("Help"));
		help.add(new MenuItemAction(this,Global.resourceString("Board_Window")));
		help.add(new MenuItemAction(this,Global.resourceString("Making_Moves")));
		help.add(new MenuItemAction(this,Global.resourceString("Keyboard_Shortcuts")));
		help.add(new MenuItemAction(this,Global.resourceString("About_Variations")));
		help.add(new MenuItemAction(this,Global.resourceString("Playing_Games")));
		help.add(new MenuItemAction(this,Global.resourceString("Mailing_Games")));
		M.add(options);
		M.setHelpMenu(help);
		// Board
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
		Comment=new TextArea("",0,0,TextArea.SCROLLBARS_VERTICAL_ONLY);
		Comment.setFont(Global.SansSerif);
		Comment.setBackground(Global.gray);
		Panel bcp;
		if (Global.getParameter("shownavigationtree",true))
		{	Navigation=new NavigationPanel(B);
			bcp=new BoardCommentPanel(BP,
				new CommentNavigationPanel(Comment,new Panel3D(Navigation)),B);
		}
		else bcp=new BoardCommentPanel(BP,Comment,B);
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
		if (Navigation!=null) Navigation.addKeyListener(B);
		show();
		repaint();
	}
	
	public IconBar createIconBar ()
	{	IconBar I=new IconBar(this);
		I.Resource="/jagoclient/icons/";
		I.addLeft("undo");
		I.addSeparatorLeft();
		I.addLeft("allback");
		I.addLeft("fastback");
		I.addLeft("back");
		I.addLeft("forward");
		I.addLeft("fastforward");
		I.addLeft("allforward");
		I.addSeparatorLeft();
		I.addLeft("variationback");
		I.addLeft("variationforward");
		I.addLeft("variationstart");
		I.addLeft("main");
		I.addLeft("mainend");
		I.addSeparatorLeft();
		String icons[]={
			"mark","square","triangle","circle","letter","text","",
			"black","white","","setblack","setwhite","delete"};
		I.addToggleGroupLeft(icons);
		I.addSeparatorLeft();
		I.addLeft("deletemarks");
		I.addLeft("play");
		I.setIconBarListener(this);
		return I;
	}
	
	public void iconPressed (String s)
	{	if (s.equals("undo")) doAction(Global.resourceString("Undo"));
		else if (s.equals("allback")) doAction("I<<");
		else if (s.equals("fastback")) doAction("<<");
		else if (s.equals("back")) doAction("<");
		else if (s.equals("forward")) doAction(">");
		else if (s.equals("fastforward")) doAction(">>");
		else if (s.equals("allforward")) doAction(">>I");
		else if (s.equals("variationback")) doAction("<V");
		else if (s.equals("variationstart")) doAction("V");
		else if (s.equals("variationforward")) doAction("V>");
		else if (s.equals("main")) doAction("*");
		else if (s.equals("mainend")) doAction("**");
		else if (s.equals("mark")) B.mark();
		else if (s.equals("mark")) B.mark();
		else if (s.equals("square")) B.specialmark(Field.SQUARE);
		else if (s.equals("triangle")) B.specialmark(Field.TRIANGLE);
		else if (s.equals("circle")) B.specialmark(Field.CIRCLE);
		else if (s.equals("letter")) B.letter();
		else if (s.equals("text"))
		{	B.textmark(Text);
			if (TMQ==null) TMQ=new TextMarkQuestion(this,Text);
		}
		else if (s.equals("black")) B.black();
		else if (s.equals("white")) B.white();
		else if (s.equals("setblack")) B.setblack();
		else if (s.equals("setwhite")) B.setwhite();
		else if (s.equals("delete")) B.deletestones();
		else if (s.equals("deletemarks")) B.clearmarks();
		else if (s.equals("play")) B.resume();
	}

	public void doAction (String o)
	{	if (Global.resourceString("Undo").equals(o))
		{	B.undo();
		}
		else if (Global.resourceString("Close").equals(o))
		{	close();
		}
		else if (Global.resourceString("Board_size").equals(o))
		{	boardsize();
		}
		else if ("<".equals(o))
		{	B.back();
		}
		else if (">".equals(o))
		{	B.forward();
		}
		else if (">>".equals(o))
		{	B.fastforward();
		}
		else if ("<<".equals(o))
		{	B.fastback();
		}
		else if ("I<<".equals(o))
		{	B.allback();
		}
		else if (">>I".equals(o))
		{	B.allforward();
		}
		else if ("<V".equals(o))
		{	B.varleft();
		}
		else if ("V>".equals(o))
		{	B.varright();
		}
		else if ("V".equals(o))
		{	B.varup();
		}
		else if ("**".equals(o))
		{	B.varmaindown();
		}
		else if ("*".equals(o))
		{	B.varmain();
		}
		else if (Global.resourceString("Pass").equals(o))
		{	B.pass();
			notepass();
		}
		else if (Global.resourceString("Resume_playing").equals(o))
		{	B.resume();
		}
		else if (Global.resourceString("Clear_all_marks").equals(o))
		{	B.clearmarks();
		}
		else if (Global.resourceString("Undo_Adding_Removing").equals(o))
		{	B.clearremovals();
		}
		else if (Global.resourceString("Remove_groups").equals(o))
		{	B.score();
		}
		else if (Global.resourceString("Score").equals(o))
		{	String s=B.done();
			if (s!=null) new Message(this,s);
		}
		else if (Global.resourceString("Local_Count").equals(o))
		{	new Message(this,B.docount());
		}
		else if (Global.resourceString("New").equals(o))
		{	B.deltree(); B.copy();
			setTitle(DefaultTitle);
		}
		else if (Global.resourceString("Mail").equals(o)) // mail the game
		{	ByteArrayOutputStream ba=new ByteArrayOutputStream(50000);
			try
			{	if (Global.getParameter("xml",false))
				{	PrintWriter po=new PrintWriter(
					new OutputStreamWriter(ba,"UTF8"),true);
					B.saveXML(po,"utf-8");
					po.close();
				}
				else
				{	PrintWriter po=new PrintWriter(ba,true);
					B.save(po);
					po.close();
				}
			}
			catch (Exception ex) {	}
			new MailDialog(this,ba.toString());
			return;
		}
		else if (Global.resourceString("Ascii_Mail").equals(o))
			// ascii dump of the game
		{	ByteArrayOutputStream ba=new ByteArrayOutputStream(10000);
			PrintWriter po=new PrintWriter(ba,true);
			try
			{	B.asciisave(po);
			}
			catch (Exception ex) {	}
			new MailDialog(this,ba.toString());
			return;
		}
		else if (Global.resourceString("Print").equals(o)) // print the game
		{	B.print(Global.frame());
		}
		else if (Global.resourceString("Save").equals(o)) // save the game
		{	// File dialog handling
			FileDialog fd=new FileDialog(this,Global.resourceString("Save"),
				FileDialog.SAVE);
			if (!Dir.equals("")) fd.setDirectory(Dir);
			String s=((Node)B.firstnode()).getaction("GN");
			if (s!=null && !s.equals(""))
				fd.setFile(s+"."+Global.getParameter("extension",
				Global.getParameter("xml",false)?"xml":"sgf"));
			else
				fd.setFile("*."+Global.getParameter("extension",
				Global.getParameter("xml",false)?"xml":"sgf"));
			fd.setFilenameFilter(this);
			center(fd);
			fd.show();
			String fn=fd.getFile();
			if (fn==null) return;
			setGameTitle(FileName.purefilename(fn));
			Dir=fd.getDirectory();
			try // print out using the board class
			{	PrintWriter fo;
				if (Global.getParameter("xml",false))
				{	if (Global.isApplet())
					{	fo=new PrintWriter(new OutputStreamWriter(
							new FileOutputStream(fd.getDirectory()+fn),"UTF8"));
						B.saveXML(fo,"utf-8");
					}
					else
					{	String Encoding=Global.getParameter("encoding",
							System.getProperty("file.encoding")).toUpperCase();
						if (Encoding.equals(""))
						{	fo=new PrintWriter(new OutputStreamWriter(
								new FileOutputStream(fd.getDirectory()+fn),
								"UTF8"));
							B.saveXML(fo,"utf-8");
						}
						else
						{	String XMLEncoding="";
							if (Encoding.equals("CP1252") || 
								Encoding.equals("ISO8859_1"))
							{	Encoding="ISO8859_1";
								XMLEncoding="iso-8859-1";
							}
							else
							{	Encoding="UTF8";
								XMLEncoding="utf-8";
							}
							FileOutputStream fos=
								new FileOutputStream(fd.getDirectory()+fn);
							try
							{	fo=new PrintWriter(new OutputStreamWriter(
									fos,Encoding));
							}
							catch (Exception e)
							{	Encoding="UTF8";
								XMLEncoding="utf-8";
								fo=new PrintWriter(new OutputStreamWriter(
									fos,Encoding));
							}							
							B.saveXML(fo,XMLEncoding);
						}
					}
				}
				else
				{	if (Global.isApplet())
					fo=
						new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(fd.getDirectory()+fn),
						Global.getParameter("encoding","ASCII")));
					else
						fo=
						new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(fd.getDirectory()+fn),
						Global.getParameter("encoding",
						System.getProperty("file.encoding"))));
					B.save(fo);
				}
				fo.close();
			}
			catch (IOException ex)
			{	new Message(this,Global.resourceString("Write_error_")+"\n"+ex.toString());
				return;
			}
		}
		else if (Global.resourceString("Save_Position").equals(o)) // save the position
		{	// File dialog handling
			FileDialog fd=new FileDialog(this,Global.resourceString("Save Position"),
				FileDialog.SAVE);
			if (!Dir.equals("")) fd.setDirectory(Dir);
			String s=((Node)B.firstnode()).getaction("GN");
			if (s!=null && !s.equals(""))
				fd.setFile(s+"."+Global.getParameter("extension",
				Global.getParameter("xml",false)?"xml":"sgf"));
			else
				fd.setFile("*."+Global.getParameter("extension",
				Global.getParameter("xml",false)?"xml":"sgf"));
			fd.setFilenameFilter(this);
			center(fd);
			fd.show();
			String fn=fd.getFile();
			if (fn==null) return;
			Dir=fd.getDirectory();
			try // print out using the board class
			{	PrintWriter fo;
				if (Global.getParameter("xml",false))
				{	if (Global.isApplet())
					{	fo=new PrintWriter(new OutputStreamWriter(
							new FileOutputStream(fd.getDirectory()+fn),"UTF8"));
						B.saveXML(fo,"utf-8");
					}
					else
					{	String Encoding=Global.getParameter("encoding",
							System.getProperty("file.encoding")).toUpperCase();
						if (Encoding.equals(""))
						{	fo=new PrintWriter(new OutputStreamWriter(
								new FileOutputStream(fd.getDirectory()+fn),
								"UTF8"));
							B.saveXMLPos(fo,"utf-8");
						}
						else
						{	String XMLEncoding="";
							if (Encoding.equals("CP1252") || 
								Encoding.equals("ISO8859_1"))
							{	Encoding="ISO8859_1";
								XMLEncoding="iso-8859-1";
							}
							else
							{	Encoding="UTF8";
								XMLEncoding="utf-8";
							}
							FileOutputStream fos=
								new FileOutputStream(fd.getDirectory()+fn);
							try
							{	fo=new PrintWriter(new OutputStreamWriter(
									fos,Encoding));
							}
							catch (Exception e)
							{	Encoding="UTF8";
								XMLEncoding="utf-8";
								fo=new PrintWriter(new OutputStreamWriter(
									fos,Encoding));
							}							
							B.saveXMLPos(fo,XMLEncoding);
						}
					}
				}
				else
				{	if (Global.isApplet())
					fo=
						new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(fd.getDirectory()+fn),
						Global.getParameter("encoding","ASCII")));
					else
						fo=
						new PrintWriter(new OutputStreamWriter(
						new FileOutputStream(fd.getDirectory()+fn),
						Global.getParameter("encoding",
						System.getProperty("file.encoding"))));
					B.savePos(fo);
				}
				fo.close();
			}
			catch (IOException ex)
			{	new Message(this,Global.resourceString("Write_error_")+"\n"+ex.toString());
				return;
			}
		}
		else if (Global.resourceString("Save_Bitmap").equals(o)) // save the game
		{	// File dialog handling
			FileDialog fd=new FileDialog(this,Global.resourceString("Save_Bitmap"),FileDialog.SAVE);
			if (!Dir.equals("")) fd.setDirectory(Dir);
			String s=((Node)B.firstnode()).getaction("GN");
			if (s!=null && !s.equals(""))
				fd.setFile(s+"."+Global.getParameter("extension","bmp"));
			else
				fd.setFile("*."+Global.getParameter("extension","bmp"));
			fd.setFilenameFilter(this);
			center(fd);
			fd.show();
			String fn=fd.getFile();
			if (fn==null) return;
			Dir=fd.getDirectory();
			try // print out using the board class
			{	BMPFile F=new BMPFile();
				Dimension d=B.getBoardImageSize();
				F.saveBitmap(fd.getDirectory()+fn,B.getBoardImage(),d.width,d.height);
			}
			catch (Exception ex)
			{	new Message(this,Global.resourceString("Write_error_")+"\n"+ex.toString());
				return;
			}
		}
		else if (Global.resourceString("Load").equals(o)) // load a game
		{	// File dialog handling
			FileDialog fd=new FileDialog(this,Global.resourceString("Load_Game"),FileDialog.LOAD);
			if (!Dir.equals("")) fd.setDirectory(Dir);
			fd.setFilenameFilter(this);
			fd.setFile("*."+Global.getParameter("extension",
				Global.getParameter("xml",false)?"xml":"sgf"));
			center(fd);
			fd.show();
			String fn=fd.getFile();
			if (fn==null) return;
			Dir=fd.getDirectory();
			try // print out using the board class
			{	if (Global.getParameter("xml",false))
				{	InputStream in=
						new FileInputStream(fd.getDirectory()+fn);
					try
					{	B.loadXml(new XmlReader(in));
					}
					catch (XmlReaderException e)
					{	new Message(this,"Error in file!\n"+e.getText());
					}
					in.close();
				}
				else
				{	BufferedReader fi;
					if (Global.isApplet())
						fi=
						new BufferedReader(
						new InputStreamReader(
						new FileInputStream(fd.getDirectory()+fn),
						Global.getParameter("encoding","")));
					else
						fi=
						new BufferedReader(
						new InputStreamReader(
						new FileInputStream(fd.getDirectory()+fn),
						Global.getParameter("encoding",
							System.getProperty("file.encoding"))));
					try
					{	B.load(fi);
					}
					catch (IOException e)
					{	new Message(this,"Error in file!");
					}
					fi.close();
				}
			}
			catch (IOException ex)
			{	new Message(this,Global.resourceString("Read_error_")+"\n"+ex.toString());
				return;
			}
			String s=((Node)B.firstnode()).getaction("GN");
			if (s!=null && !s.equals("")) setTitle(s);
			else
			{	((Node)B.firstnode()).setaction("GN",FileName.purefilename(fn));
				setTitle(FileName.purefilename(fn));
			}
			if (fn.toLowerCase().indexOf("kogo")>=0)
				B.setVariationStyle(false,false);
		}
		else if (Global.resourceString("Load_from_Clipboard").equals(o))
		{	loadClipboard();
		}
		else if (Global.resourceString("Copy_to_Clipboard").equals(o))
		{	saveClipboard();
		}
		else if (Global.resourceString("Board_Window").equals(o))
		{	new Help("board");
		}
		else if (Global.resourceString("Making_Moves").equals(o))
		{	new Help("moves");
		}
		else if (Global.resourceString("Keyboard_Shortcuts").equals(o))
		{	new Help("keyboard");
		}
		else if (Global.resourceString("Playing_Games").equals(o))
		{	new Help("playing");
		}
		else if (Global.resourceString("About_Variations").equals(o))
		{	new Help("variations");
		}
		else if (Global.resourceString("Mailing_Games").equals(o))
		{	new Help("mail");
		}
		else if (Global.resourceString("Insert_Node").equals(o))
		{	B.insertnode();
		}
		else if (Global.resourceString("Insert_Variation").equals(o))
		{	B.insertvariation();
		}
		else if (Global.resourceString("Game_Information").equals(o))
		{	new EditInformation(this,B.firstnode());
		}
		else if (Global.resourceString("Game_Copyright").equals(o))
		{	new EditCopyright(this,B.firstnode());
		}
		else if (Global.resourceString("Prisoner_Count").equals(o))
		{	String s=
			Global.resourceString("Black__")+B.Pw+
				Global.resourceString("__White__")+B.Pb+"\n"+
				Global.resourceString("Komi")+" "+B.getKomi();
			new Message(this,s);
		}
		else if (Global.resourceString("Board_Color").equals(o))
		{	new BoardColorEdit(this,"boardcolor",BoardColor);
		}
		else if (Global.resourceString("Black_Color").equals(o))
		{	new BoardColorEdit(this,"blackcolor",BlackColor);
		}
		else if (Global.resourceString("Black_Sparkle_Color").equals(o))
		{	new BoardColorEdit(this,"blacksparklecolor",BlackSparkleColor);
		}
		else if (Global.resourceString("White_Color").equals(o))
		{	new BoardColorEdit(this,"whitecolor",WhiteColor);
		}
		else if (Global.resourceString("White_Sparkle_Color").equals(o))
		{	new BoardColorEdit(this,"whitesparklecolor",WhiteSparkleColor);
		}
		else if (Global.resourceString("Label_Color").equals(o))
		{	new BoardColorEdit(this,"labelcolor",LabelColor);
		}
		else if (Global.resourceString("Marker_Color").equals(o))
		{	new BoardColorEdit(this,"markercolor",MarkerColor);
		}
		else if (Global.resourceString("Board_Font").equals(o))
		{	(new BoardGetFontSize(this,
			"boardfontname",Global.getParameter("boardfontname","SansSerif"),
				"boardfontsize",Global.getParameter("boardfontsize",10),true)).show();
			updateall();
		}
		else if (Global.resourceString("Normal_Font").equals(o))
		{	(new BoardGetFontSize(this,
			"sansserif",Global.getParameter("sansserif","SansSerif"),
				"ssfontsize",Global.getParameter("ssfontsize",11),true)).show();
			updateall();
		}
		else if (Global.resourceString("Fixed_Font").equals(o))
		{	(new BoardGetFontSize(this,
			"monospaced",Global.getParameter("monospaced","Monospaced"),
				"msfontsize",Global.getParameter("msfontsize",11),true)).show();
			updateall();
		}
		else if (Global.resourceString("Last_50").equals(o))
		{	B.lastrange(50);
		}
		else if (Global.resourceString("Last_100").equals(o))
		{	B.lastrange(100);
		}
		else if (Global.resourceString("Node_Name").equals(o))
		{	callInsert();
		}
		else if (Global.resourceString("Goto_Next_Name").equals(o))
		{	B.gotonext();
		}
		else if (Global.resourceString("Goto_Previous_Name").equals(o))
		{	B.gotoprevious();
		}
		else if (Global.resourceString("Next_Game").equals(o))
		{	B.gotonextmain();
		}
		else if (Global.resourceString("Previous_Game").equals(o))
		{	B.gotopreviousmain();
		}
		else if (Global.resourceString("Add_Game").equals(o))
		{	B.addnewgame();
		}
		else if (Global.resourceString("Remove_Game").equals(o))
		{	B.removegame();
		}
		else if (Global.resourceString("Set_Encoding").equals(o))
		{	new GetEncoding(this);
		}
		else if (Global.resourceString("Search_Again").equals(o))
		{	search();
		}
		else if (Global.resourceString("Search").equals(o))
		{	new GetSearchString(this);
		}
		else super.doAction(o);
	}

	public void center (FileDialog d)
	{	Point lo=getLocation();
		Dimension di=getSize();
		d.setLocation(lo.x+di.width/2-100,
			lo.y+di.height/2-100);
	}

	public void search ()
	{	B.search(Global.getParameter("searchstring","++"));
	}

	public void itemAction (String o, boolean flag)
	{	if (Global.resourceString("Save_pure_SGF").equals(o))
		{	Global.setParameter("puresgf",flag);
		}
		else if (Global.resourceString("Use_SGF_Comments").equals(o))
		{	Global.setParameter("sgfcomments",flag);
		}
		else if (Global.resourceString("On").equals(o))
		{	Global.setParameter("coordinates",flag);
			updateall();
		}
		else if (Global.resourceString("Upper_Left").equals(o))
		{	Global.setParameter("upperleftcoordinates",flag);
			updateall();
		}
		else if (Global.resourceString("Lower_Right").equals(o))
		{	Global.setParameter("lowerrightcoordinates",flag);
			updateall();
		}
		else if (Global.resourceString("Set_Black").equals(o))
		{	B.setblack();
		}
		else if (Global.resourceString("Set_White").equals(o))
		{	B.setwhite();
		}
		else if (Global.resourceString("Black_to_play").equals(o))
		{	B.black();
		}
		else if (Global.resourceString("White_to_play").equals(o))
		{	B.white();
		}
		else if (Global.resourceString("Mark").equals(o))
		{	B.mark();
		}
		else if (Global.resourceString("Text").equals(o))
		{	B.textmark(Text);
			if (TMQ==null) TMQ=new TextMarkQuestion(this,Text);
		}
		else if (Global.resourceString("Square").equals(o))
		{	B.specialmark(Field.SQUARE);
		}
		else if (Global.resourceString("Triangle").equals(o))
		{	B.specialmark(Field.TRIANGLE);
		}
		else if (Global.resourceString("Cross").equals(o))
		{	B.specialmark(Field.CROSS);
		}
		else if (Global.resourceString("Circle").equals(o))
		{	B.specialmark(Field.CIRCLE);
		}
		else if (Global.resourceString("Letter").equals(o))
		{	B.letter();
		}
		else if (Global.resourceString("Delete").equals(o))
		{	B.deletestones();
		}
		else if (Global.resourceString("True_Color_Board").equals(o))
		{	Global.setParameter("beauty",flag);
			updateall();
		}
		else if (Global.resourceString("True_Color_Stones").equals(o))
		{	Global.setParameter("beautystones",flag);
			updateall();
		}
		else if (Global.resourceString("Anti_alias_Stones").equals(o))
		{	Global.setParameter("alias",flag);
			updateall();
		}
		else if (Global.resourceString("Shadows").equals(o))
		{	Global.setParameter("shadows",flag);
			updateall();
		}
		else if (Global.resourceString("Smaller_Stones").equals(o))
		{	Global.setParameter("smallerstones",flag);
			updateall();
		}
		else if (Global.resourceString("Black_Only").equals(o))
		{	Global.setParameter("blackonly",flag);
			updateall();
		}
		else if (Global.resourceString("Use_B_W_marks").equals(o))
		{	BWColor=flag;
			Global.setParameter("bwcolor",BWColor);
			updateall();
		}
		else if (Global.resourceString("Last_Number").equals(o))
		{	LastNumber=flag;
			Global.setParameter("lastnumber",LastNumber);
			B.updateall();
		}
		else if (Global.resourceString("Show_Target").equals(o))
		{	ShowTarget=flag;
			Global.setParameter("showtarget",ShowTarget);
		}
		else if (Global.resourceString("Show_Buttons").equals(o))
		{	if (flag) add("South",ButtonP);
			else remove(ButtonP);
			if (this instanceof ConnectedGoFrame)
				Global.setParameter("showbuttonsconnected",flag);
			else
				Global.setParameter("showbuttons",flag);
			setVisible(true);
			validate();
			doLayout();
			setVisible(true);
		}
		else if (Global.resourceString("Use_XML").equals(o))
		{	UseXML.setState(true); UseSGF.setState(false);
			Global.setParameter("xml",true);
		}
		else if (Global.resourceString("Use_SGF").equals(o))
		{	UseSGF.setState(true); UseXML.setState(false);
			Global.setParameter("xml",false);
		}
		else if (Global.resourceString("Hide").equals(o))
		{	B.setVariationStyle(flag,VCurrent.getState());
			Global.setParameter("vhide",flag);
		}
		else if (Global.resourceString("To_Current").equals(o))
		{	VCurrent.setState(true); VChild.setState(false);
			B.setVariationStyle(VHide.getState(),true);
		}
		else if (Global.resourceString("To_Child").equals(o))
		{	VCurrent.setState(false); VChild.setState(true);
			B.setVariationStyle(VHide.getState(),false);
		}
		else if (Global.resourceString("Continue_Numbers").equals(o))
		{	Global.setParameter("variationnumbers",flag);
		}
	}

	// This can be used to set a board position
	// The board is updated directly, if it is at the
	// last move.
	/** set a black move at i,j */
	public void black (int i, int j)
	{	B.black(i,j);
	}
	/** set a white move at i,j */
	public void white (int i, int j)
	{	B.white(i,j);
	}
	/** set a black stone at i,j */
	public void setblack (int i, int j)
	{	B.setblack(i,j);
	}
	/** set a black stone at i,j */
	public void setwhite (int i, int j)
	{	B.setwhite(i,j);
	}
	/** mark the field at i,j as territory */
	public void territory (int i, int j)
	{	B.territory(i,j);
	}
	/** Next to move */
	public void color (int c)
	{	if (c==-1) B.white();
		else B.black();
	}

	/**
	This called from the board to set the menu checks
	according to the current state.
	@param i the number of the state the Board is in.
	*/
	public void setState (int i)
	{	Black.setState(false);
		White.setState(false);
		SetBlack.setState(false);
		SetWhite.setState(false);
		Mark.setState(false);
		Letter.setState(false);
		Hide.setState(false);
		Circle.setState(false);
		Cross.setState(false);
		Triangle.setState(false);
		Square.setState(false);
		TextMark.setState(false);
		switch (i)
		{	case 1 : Black.setState(true); break;
			case 2 : White.setState(true); break;
			case 3 : SetBlack.setState(true); break;
			case 4 : SetWhite.setState(true); break;
			case 5 : Mark.setState(true); break;
			case 6 : Letter.setState(true); break;
			case 7 : Hide.setState(true); break;
			case 10 : TextMark.setState(true); break;
		}
		switch (i)
		{	case 1 : IB.setState("black",true); break;
			case 2 : IB.setState("white",true); break;
			case 3 : IB.setState("setblack",true); break;
			case 4 : IB.setState("setwhite",true); break;
			case 5 : IB.setState("mark",true); break;
			case 6 : IB.setState("letter",true); break;
			case 7 : IB.setState("delete",true); break;
			case 10 : IB.setState("text",true); break;
		}
	}

	/**
	Called from board to check the proper menu for markers.
	@param i the number of the marker type.
	*/
	public void setMarkState (int i)
	{	setState(0);
		switch (i)
		{	case Field.SQUARE : Square.setState(true); break;
			case Field.TRIANGLE : Triangle.setState(true); break;
			case Field.CROSS : Cross.setState(true); break;
			case Field.CIRCLE : Circle.setState(true); break;
		}
		switch (i)
		{	case Field.SQUARE : IB.setState("square",true); break;
			case Field.TRIANGLE : IB.setState("triangle",true); break;
			case Field.CROSS : IB.setState("mark",true); break;
			case Field.CIRCLE : IB.setState("circle",true); break;
		}
	}

	/**
	Called from board to enable and disable navigation
	buttons.
	@param i the number of the button
	@param f enable/disable the button
	*/
	public void setState (int i, boolean f)
	{	switch (i)
		{	case 1 : IB.setEnabled("variationback",f); break;
			case 2 : IB.setEnabled("variationforward",f); break;
			case 3 : IB.setEnabled("variationstart",f); break;
			case 4 : IB.setEnabled("main",f); break;
			case 5 :
				IB.setEnabled("fastforward",f);
				IB.setEnabled("forward",f);
				IB.setEnabled("allforward",f);
				break;
			case 6 :
				IB.setEnabled("fastback",f);
				IB.setEnabled("back",f);
				IB.setEnabled("allback",f);
				break;
			case 7 : 
				IB.setEnabled("mainend",f);
				IB.setEnabled("sendforward",!f);
				break;
		}
	}

	/** tests, if a name is accepted as a SGF file name */
	public boolean accept (File dir, String name)
	{	if (name.endsWith("."+Global.getParameter("extension","sgf"))) return true;
		else return false;
	}

	/**
	Called from the edit marker label dialog, when its
	text has been entered by the user.
	@param s the marker to be used by the board
	*/
	void setTextmark (String s)
	{	B.textmark(s);
	}

	/** A blocked board cannot react to the user. */
	public boolean blocked ()
	{	return false;
	}

	// The following are used from external board
	// drivers to set stones, handicap etc. (like
	// distributors for IGS commands)
	/** see, if the board is already acrive */
	public void active (boolean f) {	B.active(f); }
	/** pass the Board */
	public void pass () {	B.pass(); }
	public void setpass () {	B.setpass(); }
	/** Notify about pass */
	public void notepass () {	}
	/**
	Set a handicap to the Board.
	@param n number of stones
	*/
	public void handicap (int n) {	B.handicap(n); }
	/** set a move at i,j (called from Board) */
	public boolean moveset (int i, int j) {	return true; }
	/** pass (only proceeded from ConnectedGoFrame) */
	public void movepass () {	}
	/**
	Undo moves on the board (called from a distributor e.g.)
	@param n numbers of moves to undo.
	*/
	public void undo (int n) {	B.undo(n); }
	/** undo (only processed from ConnectedGoFrame) */
	public void undo () {	}

	public boolean close () // try to close
	{	if (Global.getParameter("confirmations",true))
		{	CloseQuestion CQ=new CloseQuestion(this);
			if (CQ.Result) 
			{	Global.notewindow(this,"board");
				doclose();
			}
			return false;
		}
		else
		{	Global.notewindow(this,"board");
			doclose();
			return false;
		}
	}

	/**
	Called from the BoardsizeQuestion dialog.
	@param s the size of the board.
	*/
	public void doboardsize (int s)
	{	B.setsize(s);
	}

	/** called by menu action, opens a SizeQuestion dialog */
	public void boardsize ()
	{	new SizeQuestion(this);
	}

	/**
	Determine the board size (for external purpose)
	@return the board size
	*/
	public int getboardsize ()
	{	return B.getboardsize();
	}

	/** add a comment to the board (called from external sources) */
	public void addComment (String s)
	{	B.addcomment(s);
	}

	public void result (int b, int w) {	}

	public void yourMove (boolean notinpos) {	}

	InputStreamReader LaterLoad=null;
	boolean LaterLoadXml;
	int LaterMove=0;
	String LaterFilename="";
	boolean Activated=false;

	/**
	Note that the board must only load a file, when it is ready.
	This is to interpret a command line argument SGF filename.
	*/
	public synchronized void load (String file, int move)
	{	LaterFilename=FileName.purefilename(file);
		LaterMove=move;
		try
		{	if (file.endsWith(".xml"))
			{	LaterLoad=new InputStreamReader(new FileInputStream(file),
				"UTF8");
				LaterLoadXml=true;
			}
			else
			{	LaterLoad=new InputStreamReader(new FileInputStream(file));
				LaterLoadXml=false;
			}
		}
		catch (Exception e) {	LaterLoad=null; }
		if (LaterLoad!=null && Activated) activate();
	}
	public void load (String file)
	{	load(file,0);
	}

	/**
	Note that the board must load a file, when it is ready.
	This is to interpret a command line argument SGF filename.
	*/
	public void load (URL file)
	{	LaterFilename=file.toString();
		try
		{	if (file.toExternalForm().endsWith(".xml"))
			{	LaterLoad=new InputStreamReader(file.openStream(),
				"UTF8");
				LaterLoadXml=true;
			}
			else
			{	LaterLoad=new InputStreamReader(file.openStream());
				LaterLoadXml=false;
			}
		}
		catch (Exception e) {	LaterLoad=null; }
	}

	/** Actually do the loading, when the board is ready. */
	public void doload (Reader file)
	{	validate();
		try
		{	B.load(new BufferedReader(file));
			file.close();
			setGameTitle(LaterFilename);
			B.gotoMove(LaterMove);
		}
		catch (Exception ex)
		{	rene.dialogs.Warning w=
			new rene.dialogs.Warning(this,ex.toString(),"Warning",true);
			w.center(this);
			w.setVisible(true);
		}
	}

	public void setGameTitle (String filename)
	{		String s=((Node)B.firstnode()).getaction("GN");
			if (s!=null && !s.equals("")) 
			{	setTitle(s);
			}
			else
			{	((Node)B.firstnode()).addaction(new Action("GN",filename));
				setTitle(filename);
			}
	}

	/** Actually do the loading, when the board is ready. */
	public void doloadXml (Reader file)
	{	validate();
		try
		{	XmlReader xml=new XmlReader(new BufferedReader(file));
			B.loadXml(xml);
			file.close();
			setGameTitle(LaterFilename);
		}
		catch (Exception ex)
		{	rene.dialogs.Warning w=
			new rene.dialogs.Warning(this,ex.toString(),"Warning",true);
			w.center(this);
			w.setVisible(true);
		}
	}

	public void loadClipboard ()
	{	try
		{	Clipboard clip=getToolkit().getSystemClipboard();
			Transferable t=clip.getContents(this);
			String S=(String)t.getTransferData(DataFlavor.stringFlavor);
			LaterFilename="Clipboard Content";
			if (XmlReader.testXml(S)) doloadXml(new StringReader(S));
			else doload(new StringReader(S));
		}
		catch (Exception e) {	}
	}

	public void saveClipboard ()
	{	try
		{	ByteArrayOutputStream ba=new ByteArrayOutputStream(50000);
			try
			{	if (Global.getParameter("xml",false))
				{	PrintWriter po=new PrintWriter(
					new OutputStreamWriter(ba,"UTF8"),true);
					B.saveXML(po,"utf-8");
					po.close();
				}
				else
				{	PrintWriter po=new PrintWriter(ba,true);
					B.save(po);
					po.close();
				}
			}
			catch (Exception ex) {	}
			String S=ba.toString();
			Clipboard clip=getToolkit().getSystemClipboard();
			StringSelection sel=new StringSelection(S);
			clip.setContents(sel,this);
		}
		catch (Exception e) {	}
	}
	public void lostOwnership (Clipboard b, Transferable s) {	}

	public synchronized void activate ()
	{	Activated=true;
		if (LaterLoad!=null)
		{	if (LaterLoadXml) doloadXml(LaterLoad);
			else doload(LaterLoad);
		}
		LaterLoad=null;
	}

	/** Repaint the board, when color or font changes. */
	public void updateall ()
	{	setcolors();
		B.updateboard();
	}

	/**
	Opens a dialog to ask for deleting of game trees. This is
	called from the Board, if the node has grandchildren.
	*/
	public boolean askUndo ()
	{	return new AskUndoQuestion(this).Result;
	}

	/**
	Called from the board, when a node is to be inserted.
	Opens a dialog asking for permission.
	*/
	public boolean askInsert ()
	{	return new AskInsertQuestion(this).Result;
	}

	/**
	Sets the name of the Board (called from a Distributor)
	@see jagoclient.igs.Distributor
	*/
	public void setname (String s)
	{	B.setname(s);
	}

	/**
	Sets the name of the current board name in a dialog
	(called from the menu)
	*/
	public void callInsert ()
	{	new NodeNameEdit(this,B.getname());
	}

	/**
	Remove a group at i,j in the board.
	*/
	public void remove (int i, int j)
	{	B.remove(i,j);
	}

	/**
	Called from the board to advance the text mark.
	*/
	public void advanceTextmark ()
	{	if (TMQ!=null) TMQ.advance();
	}

	/**
	Called from the board to set the comment of a board in the Comment
	text area.
	*/
	public void setComment (String s)
	{	Comment.setText(s);
		Comment.append("");
	}

	/**
	Called from the Board to read the comment text area.
	*/
	public String getComment ()
	{	return Comment.getText();
	}

	/**
	Called from outside to append something to the comment
	text area (e.g. from a Distributor).
	*/
	public void appendComment (String s)
	{	Comment.append(s);
	}

	/**
	Called from the board to set the Label below the board.
	*/
	public void setLabel (String s)
	{	L.setText(s);
		if (Navigation!=null) Navigation.repaint();
	}

	/**
	Called from the board to set the label for the cursor position.
	*/
	public void setLabelM (String s)
	{	Lm.setText(s);
	}

	public boolean boardShowing () {	return Show; }
	public boolean lastNumber () {	return LastNumber; }
	public boolean showTarget () {	return ShowTarget; }
	public Color blackColor () {	return BlackColor; }
	public Color whiteColor () {	return WhiteColor; }
	public Color whiteSparkleColor () {	return WhiteSparkleColor; }
	public Color blackSparkleColor () {	return BlackSparkleColor; }
	public Color markerColor (int color)
	{	switch (color)
		{	case 1 : return MarkerColor.brighter().brighter();
			case -1 : return MarkerColor.darker().darker();
			default : return MarkerColor;
		} 
	}
	public Color boardColor () { return BoardColor; }
	public Color labelColor (int color)
	{	switch (color)
		{	case 1 : return LabelColor.brighter().brighter();
			case -1 : return LabelColor.darker().darker();
			default : return LabelColor.brighter();
		} 
	}
	public boolean bwColor () {	return BWColor; }

	/**
	Process the insert key, which set the node name by opening
	the correspinding dialog.
	*/
	public void keyPressed (KeyEvent e)
	{	if (e.isActionKey())
		{	switch (e.getKeyCode())
			{	case KeyEvent.VK_INSERT : callInsert(); break;
			}
		}
		else
		{	switch (e.getKeyChar())
			{	case 'f' :
				case 'F' :
					B.search(Global.getParameter("searchstring","++")); break;
			}
		}
	}
	public void keyReleased (KeyEvent e) {	}
	public void keyTyped (KeyEvent e) {	}

	// interface routines for the BoardInterface

	public Color getColor (String S, int r, int g, int b)
	{	return Global.getColor(S,r,g,b);
	}

	public String resourceString (String S)
	{	return Global.resourceString(S);
	}

	public String version ()
	{	return Global.Version;
	}

	public boolean getParameter (String s, boolean f)
	{	return Global.getParameter(s,f);
	}

	public Font boardFont ()
	{	return Global.BoardFont;
	}

	public Frame frame ()
	{	return Global.frame();
	}

	public boolean blackOnly ()
	{	if (BlackOnly!=null)
		return BlackOnly.getState();
		return false;
	}
	
	public Color backgroundColor ()
	{	return Global.gray;
	}
}
