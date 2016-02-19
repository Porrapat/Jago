package rene.util.sound;

import java.io.*;

public interface Sound
{	public void setName (String Name);
	public String getName ();
	public void load (String file);
	public void load (InputStream in);
	public void start ();
}
