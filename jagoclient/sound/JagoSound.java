package jagoclient.sound;

import java.io.*;
import java.awt.*;
import java.net.*;
import sun.audio.*;

import jagoclient.Global;

import rene.util.sound.*;

public class JagoSound
{	static SoundList SL=new SoundList();
	static synchronized public void play 
		(String file, String simplefile, boolean beep)
	{	if (Global.getParameter("nosound",true)) return;
		if (Global.getParameter("beep",true))
		{	if (beep) SoundList.beep();
			return;
		}
		if (Global.getParameter("simplesound",true)) file=simplefile;
		if (file.equals("")) return;
		if (SL.busy()) return;
		if (Global.getJavaVersion()>=1.3)
			SL.play("/jagoclient/au/"+file+".wav");
		else
			SL.play("/jagoclient/au/"+file+".au");
	}
	static public void play (String file)
	{	if (SL.busy()) return;
		play(file,"wip",false);
	}
}

