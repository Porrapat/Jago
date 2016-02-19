package jagoclient;

import java.io.*;
import java.util.*;

/**
A class to generate debug information in a dump file.
It is a class with all static members.
*/

public class Dump
{	static PrintWriter Out=null;
	static boolean Terminal=false;
	/** 
	Open a dump file. If this is not called there will be no
	file dumps.
	*/
	public static void open (String file)
	{	try 
		{	Out=new PrintWriter(new FileOutputStream(file),true);
			Out.println("Locale: "+Locale.getDefault()+"\n");
		}
		catch (IOException e)
		{	Out=null;
		}
	}
	/** dump a string in a line */
	public static void println (String s)
	{	if (Out!=null) Out.println(s);
		if (Terminal) System.out.println(s);
	}
	/** dump a string without linefeed */
	public static void print (String s)
	{	if (Out!=null) Out.print(s);
		if (Terminal) System.out.print(s);
	}
	/** close the dump file */
	public static void close ()
	{	if (Out!=null) Out.close();
	}
	/** determine terminal dumps or not */
	public static void terminal (boolean flag)
	{	Terminal=flag;
	}
}
