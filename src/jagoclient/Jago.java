package jagoclient;

import java.applet.*;
import java.awt.*;

import jagoclient.Global;

/**
An applet to start a Jago frame. Does about the same things
that Go.main does, only applet specific. Basically, it will
create a MainFrame and set a Go applet into it. Then it will
display the MainFrame.
<P>
When this applet is on the go server, it should contain a
"server" applet parameter pointing to the server and a "port"
paramter for the server port.
@see jagoclient.Go
*/

public class Jago extends Applet
{	MainFrame F;
	Go go;
	boolean Started=false;
	synchronized public void init ()
	{	if (Started) return; // the applet need only start once
		Started=true;
		// intialize Global things
		Global.url(getCodeBase());
		Global.readparameter("go.cfg");
		Global.createfonts();
		Global.frame(new Frame());
		Global.loadmessagefilter();
		// see, if there is a specific IGS server
		String Server=getParameter("server");
		if (Server==null) Server="";
		int port;
		String Port=getParameter("port");
		try
		{	port=Integer.parseInt(Port);
		}
		catch (Exception e) { port=6969; }
		String Encoding=getParameter("encoding");
		if (Encoding==null) Encoding="";
		String MoveStyle=getParameter("movestyle");
		if (MoveStyle==null) MoveStyle="";
		// create a MainFrame
		F=new MainFrame(Server.equals("")?
			Global.resourceString("Jago"):
			Global.resourceString("Jago_Applet"));
		// add a Go applet to the frame
		if (Server.equals("")) F.add("Center",go=new Go());
		else F.add("Center",go=new Go(Server,port,MoveStyle,Encoding));
		go.F=F;
		go.init();
		go.start();
		// display the frame
		F.show();
	}
}
