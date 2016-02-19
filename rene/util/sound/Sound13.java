package rene.util.sound;

import java.io.*;
import javax.sound.sampled.*;

public class Sound13 implements Sound
{	String Name;
	byte Buffer[];
	AudioFormat AFormat;
	public Sound13 (String name)
	{	setName(name);
		load(name);
	}
	public void setName (String name)
	{	Name=name;
	}
	public String getName ()
	{	return Name;
	}
	public void load (String file)
	{	try
		{	InputStream in = 
				getClass().getResourceAsStream(file);
			load(in);
		}
		catch (Exception e)
		{	System.out.println(e);
		}
	}
	public void load (InputStream in)
	{	try
		{	byte buffer[]=new byte[10000];
			int n=0,size=10000;
			while (true)
			{	int c=in.read();
				if (c<0) break;
				if (n>=size)
				{	byte b[]=new byte[size*2];
					System.arraycopy(buffer,0,b,0,size);
					buffer=b; size=size*2;
				}
				buffer[n++]=(byte)c;
			}
			in=new ByteArrayInputStream(buffer);
			AudioInputStream audio=AudioSystem.getAudioInputStream(in);
			AFormat=audio.getFormat();
			Buffer=new byte[audio.available()];
			audio.read(Buffer);
			audio.close();
		}
		catch (Exception e)
		{	System.out.println(e);
		}
	}
	public void start ()
	{	try
		{	DataLine.Info info=new DataLine.Info(SourceDataLine.class,AFormat);
			SourceDataLine line=(SourceDataLine)AudioSystem.getLine(info);
			line.open(AFormat);
			line.start();
			line.write(Buffer,0,Buffer.length);
			line.drain();
			line.close();
		}
		catch (Exception e)
		{	System.out.println(e);
		}
	}
	public static void main (String args[])
	{	Sound13 s=new Sound13("/ctmelody.wav");
		s.start();
		s.start();
		System.exit(0);
	}
}
