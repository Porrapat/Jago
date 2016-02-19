import java.io.*;
import java.net.*;

class RelayInputThread extends Thread
{	DataInputStream In;
	PrintStream Out;
	RelayThread RT;
	RelayInputThread (DataInputStream in, PrintStream out, RelayThread rt)
	{	In=in;
		Out=out;
		RT=rt;
		start();
	}
	public void run ()
	{	try
		{	while (true)
			{	byte b=In.readByte();
				Out.write(b);
			}
		}
		catch (Exception e)
		{}
		RT.closeall();
	}
}

class RelayThread extends Thread
{	Socket S,IGS;
	DataInputStream In,IgsIn;
	PrintStream Out,IgsOut;
	RelayServer RS;
	boolean Closed=false;
	RelayThread (Socket s, RelayServer rs)
	{	S=s; RS=rs;
		start();
	}
	public void run ()
	{	try
		{	In=new DataInputStream(S.getInputStream());
			Out=new PrintStream(
				new DataOutputStream(S.getOutputStream()));
			String server=In.readLine();
			int port=Integer.parseInt(In.readLine());
			IGS=new Socket(server,port);
			IgsOut=new PrintStream(
				new DataOutputStream(IGS.getOutputStream()));
			IgsIn=new DataInputStream(IGS.getInputStream());
			new RelayInputThread(In,IgsOut,this);
			while (true)
			{	byte b=IgsIn.readByte();
				Out.write(b);
			}
		}
		catch (Exception e) {}
		closeall();
	}
	void closeall ()
	{	try
		{	In.close();
		}
		catch (Exception e) {}
		try
		{	Out.close();
		}
		catch (Exception e) {}
		try
		{	IgsIn.close();
		}
		catch (Exception e) {}
		try
		{	IgsOut.close();
		}
		catch (Exception e) {}
		try
		{	IGS.close();
		}
		catch (Exception e) {}
		try
		{	S.close();
		}
		catch (Exception e) {}
		if (!Closed) { RS.tellclosed(); Closed=true; }
	}
}

class RelayServer extends Thread
{	int Port,Max,N;
	public RelayServer (int p, int m)
	{	Port=p;
		Max=m;
		N=0;
		run();
	}
	public void run ()
	{	try
		{	ServerSocket ss=new ServerSocket(Port);
			while (true)
			{	Socket S=ss.accept();
				if (N<Max)
				{	N++;
					new RelayThread(S,this);
				}
			}
		}
		catch (Exception e)
		{	System.out.println("Server Error");
		}
	}
	void tellclosed ()
	{	N--;
	}
}

class Relay
{	public static void main (String args[])
	{	int port,max;
		try
		{	port=Integer.parseInt(args[0]);
			max=Integer.parseInt(args[1]);
		}
		catch (Exception e) { port=6971; max=32; }
		new RelayServer(port,max);
	}
}
