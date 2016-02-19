package jagoclient;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import jagoclient.dialogs.*;
import jagoclient.gui.*;
import jagoclient.board.*;
import jagoclient.sound.*;
import jagoclient.partner.*;
import jagoclient.gmp.*;

/**
Get the port for the partner server.
*/

class GetPort extends GetParameter
{	MainFrame GCF;
	public GetPort (MainFrame gcf, int port)
	{	super(gcf,Global.resourceString("Server_Port"),
			Global.resourceString("Port"),gcf,true,"port");
		set(""+port);
		GCF=gcf;
		show();
	}
	public boolean tell (Object o, String S)
	{	int port=6970;
		try
		{	port=Integer.parseInt(S);
			Global.setParameter("serverport",port);
		}
		catch (NumberFormatException ex) {}
		return true;
	}
}

/**
Get some advanced options.
*/

class AdvancedOptionsEdit extends CloseDialog
{	Checkbox Pack,SetIcon,UseSystemViewer,UseSystemLister,UseConfirmation,
		WhoWindow,GamesWindow,KoRule;
	/**
	Initialize all dialog items.
	The main layout is a Nx1 grid with check boxes.
	*/
	public AdvancedOptionsEdit (Frame f)
	{	super(f,Global.resourceString("Advanced_Options"),true);
		setLayout(new BorderLayout());
		Panel p=new MyPanel();
		p.setLayout(new GridLayout(0,1));
		p.add(UseConfirmation=new Checkbox(Global.resourceString("Confirmations")));
		UseConfirmation.setState(Global.getParameter("confirmations",true));
		UseConfirmation.setFont(Global.SansSerif);
		p.add(KoRule=new Checkbox(Global.resourceString("Obey_Ko_Rule")));
		KoRule.setState(Global.getParameter("korule",true));
		KoRule.setFont(Global.SansSerif);
		p.add(WhoWindow=new Checkbox(Global.resourceString("Show_Who_Window")));
		WhoWindow.setState(Global.getParameter("whowindow",true));
		WhoWindow.setFont(Global.SansSerif);
		p.add(GamesWindow=new Checkbox(Global.resourceString("Show_Games_Window")));
		GamesWindow.setState(Global.getParameter("gameswindow",true));
		GamesWindow.setFont(Global.SansSerif);
		p.add(Pack=new Checkbox(Global.resourceString("Pack_some_of_the_dialogs")));
		Pack.setState(Global.getParameter("pack",true));
		Pack.setFont(Global.SansSerif);
		p.add(SetIcon=new Checkbox(Global.resourceString("Set_own_icon__buggy_in_windows__")));
		SetIcon.setState(Global.getParameter("icons",false));
		SetIcon.setFont(Global.SansSerif);
		p.add(UseSystemViewer=new Checkbox(Global.resourceString("Use_AWT_TextArea")));
		UseSystemViewer.setState(Global.getParameter("systemviewer",false));
		UseSystemViewer.setFont(Global.SansSerif);
		p.add(UseSystemLister=new Checkbox(Global.resourceString("Use_AWT_List")));
		UseSystemLister.setState(Global.getParameter("systemlister",false));
		UseSystemLister.setFont(Global.SansSerif);
		add("Center",new Panel3D(p));
		Panel ps=new MyPanel();
		ps.add(new ButtonAction(this,Global.resourceString("OK")));
		ps.add(new ButtonAction(this,Global.resourceString("Cancel")));
		ps.add(new MyLabel(" "));
		ps.add(new ButtonAction(this,Global.resourceString("Help")));
		add("South",new Panel3D(ps));
		Global.setpacked(this,"advancedoptionsedit",300,150,f);
		show();
	}
	
	public void doAction (String o)
	{	if (o.equals(Global.resourceString("OK")))
		{	setVisible(false); dispose();
			Global.setParameter("pack",Pack.getState());
			Global.setParameter("icons",SetIcon.getState());
			Global.setParameter("systemviewer",UseSystemViewer.getState());
			Global.setParameter("systemlister",UseSystemLister.getState());
			Global.setParameter("confirmations",UseConfirmation.getState());
			Global.setParameter("whowindow",WhoWindow.getState());
			Global.setParameter("gameswindow",GamesWindow.getState());
			Global.setParameter("korule",KoRule.getState());
		}
		else if (o.equals(Global.resourceString("Cancel")))
		{	setVisible(false); dispose();
		}
		else if (o.equals(Global.resourceString("Help")))
		{	new Help("advanced");
		}
	}
}

/**
Get the name for the partner client.
*/

class YourNameQuestion extends GetParameter
{	public YourNameQuestion (MainFrame gcf)
	{	super(gcf,Global.resourceString("Name"),
			Global.resourceString("Your_Name"),gcf,true,"yourname");
		set(Global.getParameter("yourname","Your Name"));
		show();
	}
	public boolean tell (Object o, String S)
	{	if (!S.equals("")) Global.setParameter("yourname",S);
		return true;
	}
}

/**
Get the languate locale
*/

class GetLanguage extends GetParameter
{	public boolean done=false;
	public GetLanguage (MainFrame gcf)
	{	super(gcf,"Your Locale (leave empty for default)",
			"Language",gcf,true,"language");
		String S="Your Locale";
		String T=Global.resourceString(S);
		if (!S.equals(T)) Prompt.setText(T+" ("+S+")");
		set(Locale.getDefault().toString());
		show();
	}
	public boolean tell (Object o, String S)
	{	Global.setParameter("language",S);
		done=true;
		return true;
	}
}

/**
Get IP name of the relay server, if used.
*/

class GetRelayServer extends CloseDialog
{	TextField Server;
	IntField Port;
	public GetRelayServer (Frame F)
	{	super(F,Global.resourceString("Relay_Server"),true);
		Panel p=new MyPanel();
		p.setLayout(new GridLayout(0,2));
		p.add(new MyLabel(Global.resourceString("Server")));
		p.add(Server=new GrayTextField());
		Server.setText(Global.getParameter("relayserver","localhost"));
		p.add(new MyLabel(Global.resourceString("Port")));
		p.add(Port=new IntField(this,Global.resourceString("Port"),Global.getParameter("relayport",6971)));
		add("Center",new Panel3D(p));
		Panel bp=new MyPanel();
		bp.add(new ButtonAction(this,Global.resourceString("OK")));
		bp.add(new ButtonAction(this,Global.resourceString("Cancel")));
		bp.add(new MyLabel(" "));
		bp.add(new ButtonAction(this,Global.resourceString("Help")));
		add("South",new Panel3D(bp));
		Global.setpacked(this,"getrelay",300,200,F);
		validate(); show();
	}
	public void doAction (String o)
	{	Global.notewindow(this,"getrelay");   
		if (Global.resourceString("OK").equals(o))
		{	Global.setParameter("relayserver",Server.getText());
			Global.setParameter("relayport",Port.value());
			setVisible(false); dispose();
		}
		else if (Global.resourceString("Cancel").equals(o))
		{	setVisible(false); dispose();
		}
		else if (o.equals(Global.resourceString("Help")))
		{	new Help("relayserver");
		}
		else super.doAction(o);
	}
}

/**
Get the background color.
*/

class BackgroundColorEdit extends ColorEdit
{	public BackgroundColorEdit (Frame f, String s, Color c)
	{	super(f,s,c,false);
		show();
	}
	public void addbutton (Panel p)
	{	p.add(new ButtonAction(this,Global.resourceString("System")));
	}
	public void tell (Color C)
	{	Global.gray=C;
		Global.setParameter("color.background",C);
		Global.setParameter("color.control",C);
		Global.makeColors();
	}
	public void doAction (String o)
	{	if (o.equals(Global.resourceString("System")))
		{	Global.removeParameter("color.control");
			Global.removeParameter("color.background");
			Global.gray=SystemColor.window;
			Global.makeColors();
			super.doAction(Global.resourceString("OK"));
		}
		else super.doAction(o);
	}
}

/**
The MainFrame contains menus to edit some options, get help and
set some things. A card layout with the server and partner
connections is added to this frame later.
*/

public class MainFrame extends CloseFrame
{	CheckboxMenuItem 
		StartPublicServer,TimerInTitle,BigTimer,ExtraInformation,ExtraSendField,
		DoSound,SimpleSound,BeepOnly,Warning,RelayCheck,Automatic,
		EveryMove,FineBoard,Navigation;
	MenuItem StartServer;
	public Server S=null;

	public MainFrame (String c)
	{	super(c);
		seticon("ijago.gif");
		boolean constrainedapplet=c.equals(
			Global.resourceString("Jago_Applet"));
		// Menu :
		MenuBar menu=new MenuBar();
		setMenuBar(menu);
		// Actions
		Menu local=new MyMenu(Global.resourceString("Actions"));
		local.add(new MenuItemAction(this,Global.resourceString("Local_Board")));
		local.addSeparator();
		local.add(RelayCheck=new CheckboxMenuItemAction(this,Global.resourceString("Use_Relay")));
		RelayCheck.setState(Global.getParameter("userelay",false));
		local.add(new MenuItemAction(this,Global.resourceString("Relay_Server")));
		local.addSeparator();
		local.add(new MenuItemAction(this,Global.resourceString("Play_Go")));
		local.addSeparator();
		local.add(new MenuItemAction(this,Global.resourceString("Set_Language")));
		local.add(new MenuItemAction(this,"Close and Use English","CloseEnglish"));
		local.addSeparator();
		local.add(new MenuItemAction(this,Global.resourceString("Close")));
		menu.add(local);
		// Server Options
		Menu soptions=new MyMenu(Global.resourceString("Go_Server"));
		if (!Global.isApplet())
		{	soptions.add(Automatic=new CheckboxMenuItemAction(this,Global.resourceString("Automatic_Login")));
			Automatic.setState(Global.getParameter("automatic",true));
			soptions.addSeparator();
		}
		soptions.add(new MenuItemAction(this,Global.resourceString("Filter")));
		soptions.add(new MenuItemAction(this,Global.resourceString("Function_Keys")));
		menu.add(soptions);
		// Partner Options
		if (!constrainedapplet)
		{	Menu poptions=new MyMenu(Global.resourceString("Partner"));
			poptions.add(StartServer=
				new MenuItemAction(this,Global.resourceString("Start_Server")));
			poptions.add(new MenuItemAction(this,Global.resourceString("Server_Port")));
			poptions.add(new MenuItemAction(this,Global.resourceString("Your_Name")));
			poptions.add(StartPublicServer=
				new CheckboxMenuItemAction(this,Global.resourceString("Public")));
			StartPublicServer.setState(
				Global.getParameter("publicserver",true));
			menu.add(poptions);
		}
		// Options
		Menu options=new MyMenu(Global.resourceString("Options"));
		Menu bo=new MyMenu(Global.resourceString("Board_Options"));
		bo.add(Navigation=new CheckboxMenuItemAction(this,Global.resourceString("Navigation_Tree")));
		Navigation.setState(Global.getParameter("shownavigationtree",true));
		bo.add(TimerInTitle=new CheckboxMenuItemAction(this,Global.resourceString("Timer_in_Title")));
		TimerInTitle.setState(Global.getParameter("timerintitle",true));
		bo.add(BigTimer=new CheckboxMenuItemAction(this,Global.resourceString("Big_Timer")));
		BigTimer.setState(Global.getParameter("bigtimer",true));
		bo.add(ExtraInformation=new CheckboxMenuItemAction(this,Global.resourceString("Extra_Information")));
		ExtraInformation.setState(Global.getParameter("extrainformation",true));
		bo.add(ExtraSendField=new CheckboxMenuItemAction(this,Global.resourceString("Extra_Send_Field")));
		ExtraSendField.setState(Global.getParameter("extrasendfield",true));
		bo.add(FineBoard=new CheckboxMenuItemAction(this,Global.resourceString("Fine_Board")));
		FineBoard.setState(Global.getParameter("fineboard",true));
		options.add(bo);
		options.add(new MenuItemAction(this,Global.resourceString("Advanced_Options")));
		options.addSeparator();
		Menu fonts=new MyMenu(Global.resourceString("Fonts"));
		fonts.add(new MenuItemAction(this,Global.resourceString("Board_Font")));
		fonts.add(new MenuItemAction(this,Global.resourceString("Fixed_Font")));
		fonts.add(new MenuItemAction(this,Global.resourceString("Big_Font")));
		fonts.add(new MenuItemAction(this,Global.resourceString("Normal_Font")));
		options.add(fonts);
		options.add(new MenuItemAction(this,Global.resourceString("Background_Color")));
		options.addSeparator();
		options.add(DoSound=
			new CheckboxMenuItemAction(this,Global.resourceString("Sound_on")));
		DoSound.setState(!Global.getParameter("nosound",true));
		options.add(BeepOnly=new CheckboxMenuItemAction(this,Global.resourceString("Beep_only")));
		BeepOnly.setState(Global.getParameter("beep",true));
		Menu sound=new MyMenu(Global.resourceString("Sound_Options"));
		sound.add(SimpleSound=
			new CheckboxMenuItemAction(this,Global.resourceString("Simple_sound")));
		SimpleSound.setState(Global.getParameter("simplesound",true));
		sound.add(EveryMove=
			new CheckboxMenuItemAction(this,Global.resourceString("Every_move")));
		EveryMove.setState(Global.getParameter("sound.everymove",true));
		sound.add(Warning=
			new CheckboxMenuItemAction(this,Global.resourceString("Timeout_warning")));
		Warning.setState(Global.getParameter("warning",true));		
		sound.add(new MenuItemAction(this,Global.resourceString("Test_Sound")));
		options.add(sound);
		menu.add(options);
		// Help
		Menu help=new MyMenu(Global.resourceString("Help"));
		help.add(new MenuItemAction(this,Global.resourceString("About_Jago")));
		help.add(new MenuItemAction(this,Global.resourceString("Overview")));
		help.addSeparator();
		help.add(new MenuItemAction(this,Global.resourceString("Using_Windows")));
		help.add(new MenuItemAction(this,Global.resourceString("Configuring_Connections")));
		help.add(new MenuItemAction(this,Global.resourceString("Partner_Connections")));
		help.add(new MenuItemAction(this,Global.resourceString("About_Sounds")));
		help.add(new MenuItemAction(this,Global.resourceString("About_Smart_Go_Format_SGF")));
		help.add(new MenuItemAction(this,Global.resourceString("About_Filter")));
		help.add(new MenuItemAction(this,Global.resourceString("About_Function_Keys")));
		help.add(new MenuItemAction(this,Global.resourceString("Overcoming_Firewalls")));
		help.add(new MenuItemAction(this,Global.resourceString("Play_Go_Help")));
		help.addSeparator();
		help.add(new MenuItemAction(this,Global.resourceString("About_Help")));
		help.addSeparator();
		help.add(new MenuItemAction(this,Global.resourceString("On_line_Version_Information")));
		menu.setHelpMenu(help);
		pack();
		Global.setwindow(this,"mainframe",300,300);
	}

	public boolean close ()
	{	if (Global.getParameter("confirmations",true))
		{	CloseMainQuestion CMQ=new CloseMainQuestion(this);
			if (CMQ.Result) doclose();
			return false;
		}
		else
		{	doclose();
			return false;
		}
	}
	
	public void doclose ()
	{	Global.notewindow(this,"mainframe");
		super.doclose();
		Global.writeparameter("go.cfg");
		if (S!=null) S.close();
		if (!Global.isApplet()) System.exit(0);		
	}

	public void doAction (String o)
	{   if ("CloseEnglish".equals(o))
  		{	Global.setParameter("language","en");
  			if (close()) doclose();
  		}
  		else if (Global.resourceString("Overview").equals(o))
  		{	new Help("overview");
  		}
  		else if (Global.resourceString("Using_Windows").equals(o))
  		{	new Help("windows");
  		}
  		else if (Global.resourceString("Configuring_Connections").equals(o))
  		{	new Help("configure");
  		}
  		else if (Global.resourceString("Partner_Connections").equals(o))
  		{	new Help("confpartner");
  		}
  		else if (Global.resourceString("About_Jago").equals(o))
  		{	new Help("about");
  		}
  		else if (Global.resourceString("About_Help").equals(o))
  		{	new Help("help");
  		}
  		else if (Global.resourceString("About_Sounds").equals(o))
  		{	new Help("sound");
  		}
  		else if (Global.resourceString("About_Smart_Go_Format_SGF").equals(o))
  		{	new Help("sgf");
  		}
  		else if (Global.resourceString("About_Filter").equals(o))
  		{	new Help("filter");
  		}
  		else if (Global.resourceString("About_Function_Keys").equals(o))
  		{	new Help("fkeys");
  		}
  		else if (Global.resourceString("Overcoming_Firewalls").equals(o))
  		{	new Help("firewall");
  		}
  		else if (Global.resourceString("Play_Go_Help").equals(o))
  		{	new Help("gmp");
  		}
  		else if (Global.resourceString("On_line_Version_Information").equals(o))
  		{	new Help();
  		}
  		else if (Global.resourceString("Local_Board").equals(o))
  		{	GoFrame gf=new GoFrame(new Frame(),
  				Global.resourceString("Local_Viewer"));
  		}
  		else if (Global.resourceString("Play_Go").equals(o))
  		{	new GMPConnection(this);
  		}
  		else if (Global.resourceString("Server_Port").equals(o))
  		{	new GetPort(this,Global.getParameter("serverport",6970));
  		}
  		else if (Global.resourceString("Set_Language").equals(o))
  		{	GetLanguage d=new GetLanguage(this);
  			if (d.done && close()) doclose();
  		}
		else if (Global.resourceString("Board_Font").equals(o))
		{	(new GetFontSize(
			"boardfontname",Global.getParameter("boardfontname","SansSerif"),
	        "boardfontsize",Global.getParameter("boardfontsize",10),false)).show();
		}
		else if (Global.resourceString("Normal_Font").equals(o))
		{	(new GetFontSize(
			"sansserif",Global.getParameter("sansserif","SansSerif"),
			"ssfontsize",Global.getParameter("ssfontsize",11),false)).show();
		}
		else if (Global.resourceString("Fixed_Font").equals(o))
		{	(new GetFontSize(
			"monospaced",Global.getParameter("monospaced","Monospaced"),
			"msfontsize",Global.getParameter("msfontsize",11),false)).show();
		}
		else if (Global.resourceString("Big_Font").equals(o))
		{	(new GetFontSize(
			"bigmonospaced",Global.getParameter("bigmonospaced","BoldMonospaced"),
			"bigmsfontsize",Global.getParameter("bigmsfontsize",22),false)).show();
		}
  		else if (Global.resourceString("Your_Name").equals(o))
  		{	new YourNameQuestion(this);
  		}
  		else if (Global.resourceString("Filter").equals(o))
  		{	Global.MF.edit();
  		}
  		else if (Global.resourceString("Function_Keys").equals(o))
  		{	new FunctionKeyEdit();
  		}
  		else if (Global.resourceString("Relay_Server").equals(o))
  		{	new GetRelayServer(this);
  		}
  		else if (Global.resourceString("Test_Sound").equals(o))
  		{	JagoSound.play("high","wip",true);
  		}
  		else if (Global.resourceString("Start_Server").equals(o))
  		{	if (Global.Busy)
  			{	Dump.println("Server started on "+
  					Global.getParameter("serverport",6970));
  				if (S==null)
  					S=new Server(Global.getParameter("serverport",6970),
  						Global.getParameter("publicserver",true));
  				S.open();
  				try
  				{	StartServer.setLabel(Global.resourceString("Stop_Server"));
  				}
  				catch (Exception e)
  				{	System.err.println("Motif error with setLabel");
  				}
  			}
  			else
  			{	S.close();
	  			StartServer.setLabel(Global.resourceString("Start_Server"));
  			}
  		}
  		else if (Global.resourceString("Stop_Server").equals(o))
  		{	S.close();
  			StartServer.setLabel(Global.resourceString("Start_Server"));
  		}
  		else if (Global.resourceString("Advanced_Options").equals(o))
  		{	new AdvancedOptionsEdit(this);
  		}
  		else if (Global.resourceString("Background_Color").equals(o))
  		{	new BackgroundColorEdit(this,"globalgray",Global.gray);
  		}
		else super.doAction(o);
  	}

  	public void itemAction (String o, boolean flag)
  	{	if (Global.resourceString("Public").equals(o))
  		{	Global.setParameter("publicserver",flag);
  		}
  		else if (Global.resourceString("Timer_in_Title").equals(o))
  		{	Global.setParameter("timerintitle",flag);
  		}
  		else if (Global.resourceString("Navigation_Tree").equals(o))
  		{	Global.setParameter("shownavigationtree",flag);
  		}
  		else if (Global.resourceString("Fine_Board").equals(o))
  		{	Global.setParameter("fineboard",flag);
  		}
  		else if (Global.resourceString("Big_Timer").equals(o))
  		{	Global.setParameter("bigtimer",flag);
  		}
  		else if (Global.resourceString("Extra_Information").equals(o))
  		{	Global.setParameter("extrainformation",flag);
  		}
  		else if (Global.resourceString("Extra_Send_Field").equals(o))
  		{	Global.setParameter("extrasendfield",flag);
  		}
  		else if (Global.resourceString("Sound_on").equals(o))
  		{	Global.setParameter("nosound",!flag);
  		}
  		else if (Global.resourceString("Simple_sound").equals(o))
  		{	Global.setParameter("simplesound",flag);
  		}
  		else if (Global.resourceString("Every_move").equals(o))
  		{	Global.setParameter("sound.everymove",flag);
  		}
  		else if (Global.resourceString("Beep_only").equals(o))
  		{	Global.setParameter("beep",flag);
  		}
  		else if (Global.resourceString("Timeout_warning").equals(o))
  		{	Global.setParameter("warning",flag);
  		}
  		else if (Global.resourceString("Automatic_Login").equals(o))
  		{	Global.setParameter("automatic",flag);
  		}
  		else if (Global.resourceString("Use_Relay").equals(o))
  		{	Global.setParameter("userelay",flag);
  		}
  	}
  	
}

