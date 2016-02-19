package jagoclient.igs;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

import jagoclient.gui.*;
import jagoclient.dialogs.*;
import jagoclient.Global;
import jagoclient.Dump;
import jagoclient.CloseConnection;
import jagoclient.igs.who.*;
import jagoclient.igs.games.*;
import jagoclient.igs.connection.*;

import rene.util.list.*;
import rene.viewer.*;

class CloseConnectionQuestion extends Question
{	public CloseConnectionQuestion (ConnectionFrame g)
	{	super(g,Global.resourceString("This_will_close_your_connection_"),Global.resourceString("Close"),g,true); 
		show(); 
	}
}

class GetWaitfor extends GetParameter
{	ConnectionFrame CF; 
	public GetWaitfor (ConnectionFrame cf)
	{	super(cf,Global.resourceString("Wait_for_"),Global.resourceString("Wait_for_Player"),cf,true); 
		CF=cf; 
		set(CF.Waitfor); 
		show(); 
	}
	public boolean tell (Object o, String s)
	{	CF.Waitfor=s; 
		return true; 
	}
}

class GetReply extends GetParameter
{	ConnectionFrame CF; 
	public GetReply (ConnectionFrame cf)
	{	super(cf,Global.resourceString("Automatic_Reply_"),Global.resourceString("Auto_Reply"),cf,true); 
		CF=cf; 
		set(CF.Reply); 
		show(); 
	}
	public boolean tell (Object o, String s)
	{	CF.Reply=s; 
		Global.setParameter("autoreply",s); 
		return true; 
	}
}

class RefreshWriter extends PrintWriter implements Runnable
{	Thread T; 
	boolean NeedsRefresh; 
	boolean Stop=false; 
	public RefreshWriter (OutputStreamWriter out, boolean flag)
	{	super(out,flag); 
		if (Global.getParameter("refresh",true))
		{	T=new Thread(this); 
			T.start(); 
		}
	}
	public void print (String s)
	{	super.print(s); 
		Dump.println("Out ---> "+s); 
		NeedsRefresh=false; 
	}
	public void printLn (String s)
	{	super.println(s); 
		Dump.println("Out ---> "+s); 
		NeedsRefresh=false; 
	}
	public void close ()
	{	super.close(); 
		Stop=true; 
	}
	public void run ()
	{	while (!Stop)
		{	NeedsRefresh=true; 
			try {	T.sleep(300000); } catch (Exception e) {	}
			if (Stop) break; 
			if (NeedsRefresh)
			{	println("ayt"); 
			}
		}
	}
}

/**
This frame contains a menu, a text area for the server output,
a text area to send commands to the server and buttons to call
who, games etc.
*/

public class ConnectionFrame extends CloseFrame
	implements KeyListener
{	GridBagLayout girdbag; 
	Viewer Output; 
	HistoryTextField Input; 
	GridBagLayout gridbag; 
	public WhoFrame Who; 
	public GamesFrame Games; 
	
	Socket Server; 
	PrintWriter Out; 
	public DataOutputStream Outstream; 
	String Encoding; 
	IgsStream In; 
	ReceiveThread RT; 
	String Dir; 
	TextField Game; 
	CheckboxMenuItem CheckInfo,CheckMessages,CheckErrors,ReducedOutput,
		AutoReply; 
	public int MoveStyle=Connection.MOVE; 
	TextField WhoRange; // Kyu/Dan range for the who command.
	String Waitfor; // Pop a a message, when this player connects.
	ListClass OL; // List of Output-Listeners
	String Reply; 
	
	public boolean hasClosed=false; // note that the user closed the window
	
	public ConnectionFrame (String Name, String encoding)
	{	super(Name); 
		Encoding=encoding; 
		Waitfor=""; 
		OL=new ListClass(); 
		setLayout(new BorderLayout()); 
		// Menu
		MenuBar M=new MenuBar(); 
		setMenuBar(M); 
		Menu file=new MyMenu(Global.resourceString("File")); 
		M.add(file); 
		file.add(new MenuItemAction(this,Global.resourceString("Save"))); 
		file.addSeparator(); 
		file.add(new MenuItemAction(this,Global.resourceString("Clear"))); 
		file.addSeparator(); 
		file.add(new MenuItemAction(this,Global.resourceString("Close"))); 
		Menu options=new MyMenu(Global.resourceString("Options")); 
		M.add(options); 
		options.add(AutoReply=new CheckboxMenuItemAction(this,Global.resourceString("Auto_Reply"))); 
		AutoReply.setState(false); 
		options.add(new MenuItemAction(this,Global.resourceString("Set_Reply"))); 
		Reply=Global.getParameter("autoreply","I am busy! Please, try later."); 
		options.addSeparator(); 
		options.add(CheckInfo=new CheckboxMenuItemAction(this,Global.resourceString("Show_Information"))); 
		CheckInfo.setState(Global.getParameter("showinformation",false)); 
		options.add(CheckMessages=new CheckboxMenuItemAction(this,Global.resourceString("Show_Messages"))); 
		CheckMessages.setState(Global.getParameter("showmessages",true)); 
		options.add(CheckErrors=new CheckboxMenuItemAction(this,Global.resourceString("Show_Errors"))); 
		CheckErrors.setState(Global.getParameter("showerrors",false)); 
		options.add(ReducedOutput=new CheckboxMenuItemAction(this,Global.resourceString("Reduced_Output"))); 
		ReducedOutput.setState(Global.getParameter("reducedoutput",true)); 
		options.add(new MenuItemAction(this,Global.resourceString("Wait_for_Player"))); 
		Menu help=new MyMenu(Global.resourceString("Help")); 
		help.add(new MenuItemAction(this,Global.resourceString("Terminal_Window"))); 
		help.add(new MenuItemAction(this,Global.resourceString("Server_Help"))); 
		help.add(new MenuItemAction(this,Global.resourceString("Channels"))); 
		help.add(new MenuItemAction(this,Global.resourceString("Observing_Playing"))); 
		help.add(new MenuItemAction(this,Global.resourceString("Teaching"))); 
		M.setHelpMenu(help); 
		Panel center=new MyPanel(); 
		center.setLayout(new BorderLayout()); 
		// Text
		Output=Global.getParameter("systemviewer",false)?new SystemViewer():new Viewer(); 
		Output.setFont(Global.Monospaced); 
		Output.setBackground(Global.gray); 
		center.add("Center",Output); 
		// Input
		Input=new HistoryTextField(this,"Input"); 
		Input.loadHistory("input.history"); 
		center.add("South",Input); 
		add("Center",center); 
		// Buttons:
		Panel p=new MyPanel(); 
		p.add(new ButtonAction(this,Global.resourceString("Who"))); 
		p.add(WhoRange=new HistoryTextField(this,"WhoRange",5)); 
		WhoRange.setText(Global.getParameter("whorange","20k-8d")); 
		p.add(new ButtonAction(this,Global.resourceString("Games"))); 
		p.add(new MyLabel(" ")); 
		p.add(new ButtonAction(this,Global.resourceString("Peek"))); 
		p.add(new ButtonAction(this,Global.resourceString("Status"))); 
		p.add(new ButtonAction(this,Global.resourceString("Observe"))); 
		Game=new GrayTextField(4); 
		p.add(Game); 
		add("South",new Panel3D(p)); 
		//
		Dir=new String(""); 
		seticon("iconn.gif"); 
		addKeyListener(this); 
		Input.addKeyListener(this); 
	}
	
	void movestyle (int style)
	{	MoveStyle=style; 
	}
	
	/**
	Tries to connect to the server using IgsStream.
	Upon success, it starts the ReceiveThread, which
	handles the login and then all input from the server,
	scanned by IgsStream.
	<P>
	Then it starts some default distributors, shows itself and
	returns true.
	@returns success of failure
	@see jagoclient.igs.IgsStream
	@see jagoclient.igs.ReceiveThread
	*/
	public boolean connect (String server, int port, String user, String password,
		boolean proxy)
	{	try
		{	Server=new Socket(server,port); 
			String encoding=Encoding; 
			if (encoding.startsWith("!"))
			{	encoding=encoding.substring(1); 
			}
			if (encoding.equals(""))
				Out=new RefreshWriter(
				new OutputStreamWriter(
				Outstream=new DataOutputStream(Server.getOutputStream())),
				true); 
			else Out=new RefreshWriter(
				new OutputStreamWriter(
				Outstream=new DataOutputStream(Server.getOutputStream()),encoding),
				true); 
		}
		catch (UnsupportedEncodingException e)
		{	try
			{	Out=new RefreshWriter(
				new OutputStreamWriter(
					Outstream=new DataOutputStream(Server.getOutputStream())),
					true); 
			}
			catch (Exception ex)
			{	return false; 
			}
		}
		catch (IllegalArgumentException e)
		{	try
			{	Out=new RefreshWriter(
				new OutputStreamWriter(
					Outstream=new DataOutputStream(Server.getOutputStream())),
					true); 
			}
			catch (Exception ex)
			{	return false; 
			}
		}
		catch (IOException e)
		{	return false; 
		}
		try
		{	/*if (proxy) In=
			new ProxyIgsStream(this,Server.getInputStream(),Out);
			else */In=new IgsStream(this,Server.getInputStream(),Out); 
		}
		catch (Exception e)
		{	return false; 
		}
		show(); 
		RT=new ReceiveThread(Output,In,Out,user,password,proxy,this); 
		RT.start(); 
		new PlayDistributor(this,In,Out); 
		new MessageDistributor(this,In,Out); 
		new ErrorDistributor(this,In,Out); 
		new InformationDistributor(this,In,Out); 
		new SayDistributor(this,In,Out); 
		return true; 
	}
	
	public boolean connectvia (String server, int port, String user, String password,
		String relayserver, int relayport)
	{	try
		{	Server=new Socket(relayserver,relayport); 
			String encoding=Encoding; 
			if (encoding.startsWith("!"))
			{	encoding=encoding.substring(1); 
			}
			if (encoding.equals(""))
				Out=new RefreshWriter(
				new OutputStreamWriter(
				Outstream=new DataOutputStream(Server.getOutputStream())),
				true); 
			else Out=new RefreshWriter(
				new OutputStreamWriter(
				Outstream=new DataOutputStream(Server.getOutputStream()),encoding),
				true); 
		}
		catch (UnsupportedEncodingException e)
		{	try
			{	Out=new RefreshWriter(
				new OutputStreamWriter(
					Outstream=new DataOutputStream(Server.getOutputStream())),
					true); 
			}
			catch (Exception ex)
			{	return false; 
			}
		}
		catch (IllegalArgumentException e)
		{	try
			{	Out=new RefreshWriter(
				new OutputStreamWriter(
					Outstream=new DataOutputStream(Server.getOutputStream())),
					true); 
			}
			catch (Exception ex)
			{	return false; 
			}
		}
		catch (IOException e)
		{	return false; 
		}
		try
		{	In=new IgsStream(this,Server.getInputStream(),Out); 
		}
		catch (Exception e)
		{	return false; 
		}
		Out.println(server); 
		Out.println(""+port); 
		show(); 
		RT=new ReceiveThread(Output,In,Out,user,password,false,this); 
		RT.start(); 
		new PlayDistributor(this,In,Out); 
		new MessageDistributor(this,In,Out); 
		new ErrorDistributor(this,In,Out); 
		new InformationDistributor(this,In,Out); 
		new SayDistributor(this,In,Out); 
		return true; 
	}
	
	public void doAction (String o)
	{	if (Global.resourceString("Close").equals(o))
		{	if (close()) doclose(); 
		}
		else if (Global.resourceString("Clear").equals(o))
		{	Output.setText(""); 
		}
		else if (Global.resourceString("Save").equals(o))
		{	FileDialog fd=new FileDialog(this,Global.resourceString("Save_Game"),FileDialog.SAVE); 
			if (!Dir.equals("")) fd.setDirectory(Dir); 
			fd.setFile("*.txt"); 
			fd.show(); 
			String fn=fd.getFile(); 
			if (fn==null) return; 
			Dir=fd.getDirectory(); 
			try
			{	PrintWriter fo; 
				if (Encoding.equals(""))
					fo=
					new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(fd.getDirectory()+fn)),true); 
				else fo=
					new PrintWriter(new OutputStreamWriter(
					new FileOutputStream(fd.getDirectory()+fn),Encoding),true); 
				Output.save(fo); 
				fo.close(); 
			}
			catch (IOException ex)
			{	System.err.println(Global.resourceString("Error_on__")+fn); 
			}
		}
		else if (Global.resourceString("Who").equals(o))
		{	goclient(); 
			if (Global.getParameter("whowindow",true))
			{	if (Who!=null)
				{	Who.refresh(); 
					Who.requestFocus(); 
					return; 
				}
				Who=new WhoFrame(this,Out,In,WhoRange.getText()); 
				Global.setwindow(Who,"who",300,400); 
				Who.show(); 
				Who.refresh(); 
			}
			else
			{	if (WhoRange.getText().equals("")) command("who");
				else command("who "+WhoRange.getText());
			}
		}
		else if (Global.resourceString("Games").equals(o))
		{	goclient(); 
			if (Global.getParameter("gameswindow",true))
			{	if (Games!=null)
				{	Games.refresh(); 
					Games.requestFocus(); 
					return; 
				}
				Games=new GamesFrame(this,Out,In); 
				Global.setwindow(Games,"games",500,400); 
				Games.show(); 
				Games.refresh(); 
			}
			else
			{	command("games");
			}
		}
		else if (Global.resourceString("Peek").equals(o))
		{	goclient(); 
			int n; 
			try
			{	n=Integer.parseInt(Game.getText()); 
				peek(n); 
			}
			catch (NumberFormatException ex)
			{	return; 
			}
		}
		else if (Global.resourceString("Status").equals(o))
		{	goclient(); 
			int n; 
			try
			{	n=Integer.parseInt(Game.getText()); 
				status(n); 
			}
			catch (NumberFormatException ex)
			{	return; 
			}
		}
		else if (Global.resourceString("Observe").equals(o))
		{	goclient(); 
			int n; 
			try
			{	n=Integer.parseInt(Game.getText()); 
				observe(n); 
			}
			catch (NumberFormatException ex)
			{	return; 
			}
		}
		else if (Global.resourceString("Terminal_Window").equals(o))
		{	new Help("terminal"); 
		}
		else if (Global.resourceString("Server_Help").equals(o))
		{	new Help("server"); 
		}
		else if (Global.resourceString("Channels").equals(o))
		{	new Help("channels"); 
		}
		else if (Global.resourceString("Observing_Playing").equals(o))
		{	new Help("obsplay"); 
		}
		else if (Global.resourceString("Teaching").equals(o))
		{	new Help("teaching"); 
		}
		else if ("Input".equals(o))
		{	String os=Input.getText(); 
			command(os);
		}
		else if (Global.resourceString("Wait_for_Player").equals(o))
		{	new GetWaitfor(this); 
		}
		else if (Global.resourceString("Set_Reply").equals(o))
		{	new GetReply(this); 
		}
		else super.doAction(o); 
	}
	
	public void itemAction (String o, boolean flag)
	{	if (Global.resourceString("Show_Information").equals(o))
		{	Global.setParameter("showinformation",flag); 
		}
		else if (Global.resourceString("Show_Messages").equals(o))
		{	Global.setParameter("showmessages",flag); 
		}
		else if (Global.resourceString("Show_Errors").equals(o))
		{	Global.setParameter("showerrors",flag); 
		}
		else if (Global.resourceString("Reduced_Output").equals(o))
		{	Global.setParameter("reducedoutput",flag); 
		}
	}
	
	public void command (String os)
	{	if (os.startsWith(" "))
		{	os=os.trim(); 
		}
		else append(os,Color.green.darker()); 
		if (Global.getParameter("gameswindow",true) &&
			os.toLowerCase().startsWith("games"))
		{	Input.setText(""); 
			doAction(Global.resourceString("Games")); 
		}
		else if (Global.getParameter("whowindow",true) &&
			os.toLowerCase().startsWith("who"))
		{	Input.setText(""); 
			if (os.length()>4)
			{	os=os.substring(4).trim(); 
				if (!os.equals("")) WhoRange.setText(os); 
			}
			doAction(Global.resourceString("Who")); 
		}
		else if (os.toLowerCase().startsWith("observe"))
		{	Input.setText(""); 
			if (os.length()>7)
			{	os=os.substring(7).trim(); 
				if (!os.equals("")) Game.setText(os); 
				doAction(Global.resourceString("Observe")); 
			}
			else append("Observe needs a game number",Color.red); 
		}
		else if (os.toLowerCase().startsWith("peek"))
		{	Input.setText(""); 
			if (os.length()>5)
			{	os=os.substring(5).trim(); 
				if (!os.equals("")) Game.setText(os); 
				doAction(Global.resourceString("Peek")); 
			}
			else append("Peek needs a game number",Color.red); 
		}
		else if (os.toLowerCase().startsWith("status"))
		{	Input.setText(""); 
			if (os.length()>6)
			{	os=os.substring(6).trim(); 
				if (!os.equals("")) Game.setText(os); 
				doAction(Global.resourceString("Status")); 
			}
			else append("Status needs a game number",Color.red); 
		}
		else if (os.toLowerCase().startsWith("moves"))
		{	new Message(this,Global.resourceString("Do_not_enter_this_command_here_")); 
		}
		else
		{	if (!Input.getText().startsWith(" ")) Input.remember(os); 
			Out.println(os); 
			Input.setText(""); 
		}
	}
	
	public void peek (int n)
	{	if (In.gamewaiting(n))
		{	new Message(this,Global.resourceString("There_is_already_a_board_for_this_game_")); 
			return; 
		}
		IgsGoFrame gf=new IgsGoFrame(this,Global.resourceString("Peek_game")); 
		new Peeker(gf,In,Out,n); 
	}
	
	public void status (int n)
	{	IgsGoFrame gf=new IgsGoFrame(this,Global.resourceString("Peek_game")); 
		new Status(gf,In,Out,n); 
	}
	
	public void observe (int n)
	{	if (In.gamewaiting(n))
		{	new Message(this,Global.resourceString("There_is_already_a_board_for_this_game_")); 
			return; 
		}
		IgsGoFrame gf=new IgsGoFrame(this,Global.resourceString("Observe_game")); 
		new GoObserver(gf,In,Out,n); 
	}
	
	public void doclose ()
	{	Global.notewindow(this,"connection");
		hasClosed=true; 
		Input.saveHistory("input.history"); 
		if (In!=null) In.removeall(); 
		Out.println("quit"); 
		Out.close(); 
		new CloseConnection(Server,In.getInputStream()); 
		inform(); 
		super.doclose(); 
	}
	
	public boolean close ()
	{	if (RT.isAlive())
		{	if (Global.getParameter("confirmations",true))
			{	return (new CloseConnectionQuestion(this)).result(); 
			}
		}
		return true; 
	}
	
	public void append (String s)
	{	append(s,Color.blue.darker()); 
	}
	
	public void append (String s, Color c)
	{	Output.append(s+"\n",c); 
		ListElement e=OL.first(); 
		while (e!=null)
		{	OutputListener ol=(OutputListener)e.content(); 
			ol.append(s,c); 
			e=e.next(); 
		}
	}
	
	public void addOutputListener (OutputListener l)
	{	OL.append(new ListElement(l)); 
	}
	
	public void removeOutputListener (OutputListener l)
	{	ListElement e=OL.first(); 
		while (e!=null)
		{	OutputListener ol=(OutputListener)e.content(); 
			if (ol==l)
			{	OL.remove(e); 
				return; 
			}
			e=e.next(); 
		}
	}
	
	public boolean wantsinformation ()
	{	return CheckInfo.getState() && Global.Silent<=0; 
	}
	
	public boolean wantsmessages ()
	{	return CheckMessages.getState() && Global.Silent<=0; 
	}
	
	public boolean wantserrors ()
	{	return CheckErrors.getState() && Global.Silent<=0; 
	}
	
	public void out (String s)
	{	if (s.startsWith("observe") || s.startsWith("status")
		|| s.startsWith("moves")) return; 
		Dump.println("---> "+s); 
		Out.println(s); 
	}
	
	public void goclient ()
	{	RT.goclient(); 
	}
	
	String reply ()
	{	if (AutoReply.getState()) return Reply; 
		else return ""; 
	}
	
	public void keyPressed (KeyEvent e) {	}
	public void keyTyped (KeyEvent e) {	}
	public void keyReleased (KeyEvent e)
	{	String s=Global.getFunctionKey(e.getKeyCode()); 
		if (s.equals("")) return; 
		Input.setText(s); 
	}
	
	public void windowOpened (WindowEvent e)
	{	Input.requestFocus(); 
	}
}
