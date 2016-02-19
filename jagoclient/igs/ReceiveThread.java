package jagoclient.igs;

import java.awt.*;
import java.net.*;
import java.io.*;

import jagoclient.dialogs.*;
import jagoclient.Dump;
import jagoclient.Global;

import rene.viewer.*;

/**
This thread uses the output of IgsStream to login to server.
After the login it will fall into a loop, and echo all input
to the Output text area of the ConnectionFrame. Some elements
get filtered by this process. But most filtering is done by
IgsStream itself.
<P>
Moreover, it will toggle to client mode, if necessary.
@see jagoclient.igs.IgsStream
*/

public class ReceiveThread extends Thread
{	IgsStream In;
	PrintWriter Out;
	Viewer Output;
	String User,Password;
	boolean FileMode,Proxy;
	boolean TriedClient=false;
	ConnectionFrame CF;
		
	public ReceiveThread (Viewer output, IgsStream in, PrintWriter out,
		String user, String password, boolean proxy, ConnectionFrame cf)
	{	In=in; Out=out;
		Output=output;
		User=user; Password=password;
		FileMode=false;
		Proxy=proxy;
		CF=cf;
	}

	public void run ()
	{	boolean Auto=Global.getParameter("automatic",true)&&!User.equals("");
		try
		{	if (!Proxy) Out.println("");
			// try to auto-login into the server
			if (Auto && !Proxy)
			{	while (true)
				{	if (In.readline())
					{	Output.append(In.line()+"\n");
					}
					if (In.line().startsWith("Login"))
					{	Output.append(Global.resourceString("_____logging_in_n"));
						Out.println(User);
						break;
					}
				}
				Dump.println("--- Leaving Login section ---");
				while (true)
				{	if (In.readline())
					{	if (In.number()==1 && In.commandnumber()==1)
						{	Out.println(Password);
							Output.append(Global.resourceString("_____sending_password_n"));
							break;
						}
						else if (In.number()==1)
						{	Output.append(Global.resourceString("_____enter_commands__n"));
							break;
						}
						else Output.append(In.line()+"\n");
					}
					else
					{	if (In.line().startsWith("#>"))
						{	goclient();
						}
						else if (In.line().startsWith("Password"))
						{	Out.println(Password);
							Output.append(Global.resourceString("_____sending_password_n"));
						}
					}
				}
				Dump.println("--- Leaving Password section ---");
			}
			// end of autologin and start of loop
			boolean AskPassword=true;
			while (true)
			{	try
				{	if (In.readline())
					{	if (FileMode && In.number()!=100) FileMode=false;
						else if (In.command().equals("File")) FileMode=true;
						switch (In.number())
						{	case 1 :
								Proxy=false;
								if (!Auto && In.commandnumber()==1 && AskPassword)
								{	Output.append(Global.resourceString("____Enter_Password_____n"));
									AskPassword=false;
									break;
								}
								else if (!Auto && In.commandnumber()==0)
								{	Output.append(Global.resourceString("____Enter_Login_____n"));
									AskPassword=true;
									break;
								}
							case 40 :
							case 22 :
							case 2 :
								break;
							default :
								if (FileMode || !In.command().equals(""))
									CF.append(In.command());
						}
						if (!Auto && In.command().startsWith("Login"))
						{	AskPassword=true;
							Output.append(Global.resourceString("____Enter_Login_____n"));
						}
						if (In.command().startsWith("#>"))
						{	goclient();
						}
					}
					else if (!Auto && In.line().startsWith("Login"))
					{	Output.append(Global.resourceString("____Enter_Login_____n"));
					}
					else if (In.command().startsWith("#>"))
					{	goclient();
					}
					else if (Proxy)
					{	Output.append(In.line());
					}
				}
				catch (IOException e)
				{	throw e;
				}
				catch (Exception e)
				{	System.out.println("Exception (please report to the author)\n"+
						e.toString()+"\n");
					e.printStackTrace();
				}
			}
		}
		catch (IOException ex) // server has closed connection
		{	if (!CF.hasClosed)
			{	Output.append(Global.resourceString("_____connection_error__n")+ex+"\n");
				new Message(Global.frame(),Global.resourceString("Lost_Connection"));
				try { sleep(10000); } catch (Exception e) {}
				return;
			}
		}
	}
	
	/** 
	Called from outside to go to client mode.
	*/
	public void goclient ()
	{	if (TriedClient) return;
		Output.append(Global.resourceString("____toggle_client_true_n"));
		Out.println("toggle client true");
		TriedClient=true;
	}

}
