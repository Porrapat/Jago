package jagoclient.partner;

import java.awt.*;
import java.net.*;
import java.io.*;

import jagoclient.Global;
import jagoclient.Dump;

import rene.viewer.*;

/**
A thrad to expect input from a partner. The input is checked here
for commands (starting with @@).
*/

public class PartnerThread extends Thread
{	BufferedReader In;
	PrintWriter Out;
	Viewer T;
	PartnerFrame PF;
	TextField Input;
	public PartnerThread (BufferedReader in, PrintWriter out,
		TextField input, Viewer t, PartnerFrame pf)
	{	In=in; Out=out; T=t; PF=pf; Input=input;
	}
	public void run ()
	{	try
		{	while (true)
			{	String s=In.readLine();
				if (s==null || s.equals("@@@@end")) throw new IOException();
				Dump.println("From Partner: "+s);
				if (s.startsWith("@@busy"))
				{	T.append(Global.resourceString("____Server_is_busy____"));
					return;
				}
				else if (s.startsWith("@@")) PF.interpret(s);
				else
				{	T.append(s+"\n");
					Input.requestFocus();
				}
			}
		}
		catch (IOException e)
		{	T.append(Global.resourceString("_____Connection_Error")+"\n");
		}
	}
}

