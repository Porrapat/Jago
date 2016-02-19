package jagoclient;

import java.awt.*;

import jagoclient.sound.*;
import jagoclient.board.*;

/**
Similar to Go. The main() method starts a GoFrame.
@see jagoclient.Go
@see jagoclient.board.GoFrame
*/

public class LocalGo 
{	static GoFrame GF;
	public static void main (String args[])
	{	boolean homefound=false;
		String localgame="";
		int na=0;
		int move=0;
		while (args.length>na)
		{	if (args.length-na>=2 && args[na].startsWith("-h"))
			{	Global.home(args[na+1]); na+=2;
				homefound=true;
			}
			else if (args[na].startsWith("-d"))
			{	Dump.open("dump.dat"); na++;
			}
			else
			{	localgame=args[na]; na++;
				if (args.length>na)
				{	try
					{	move=Integer.parseInt(args[na]);
						na++;
					}
					catch (Exception e) {}
				}
				break;
			}
		}
		Global.setApplet(false);
		if (!homefound)	Global.home(System.getProperty("user.home"));
		Global.readparameter("go.cfg");
		Global.createfonts();
		Global.frame(new Frame());
		JagoSound.play("high","",true);
		if (!localgame.equals("")) openlocal(localgame,move);
		else GF=new LocalGoFrame(new Frame(),
			Global.resourceString("Local_Viewer"));
		Global.setcomponent(GF);
	}
	static void openlocal (String file, int move)
	{	GF=new LocalGoFrame(new Frame(),
			Global.resourceString("Local_Viewer"));
		GF.load(file,move);
	}
}
