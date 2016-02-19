package rene.util;

import java.io.*;

/**
This is a static class to determine extensions etc. from a file name.
*/

public class FileName
{	static public int ChopLength=48; 
	static public String purefilename (String filename)
	{	char a[]=filename.toCharArray(); 
		int i=a.length-1; 
		char fs=File.separatorChar; 
		while (i>=0)
		{	if (a[i]==fs || a[i]=='/' || i==0)
			{	if (i==0) i=-1; 
				if (i<a.length-1)
				{	int j=a.length-1; 
					while (j>i && a[j]!='.') j--; 
					if (j>i+1) return new String(a,i+1,j-i-1); 
					else return ""; 
				}
				else return ""; 
			}
			i--; 
		}
		return filename; 
	}
	static public String path (String filename)
	{	char a[]=filename.toCharArray(); 
		int i=a.length-1; 
		char fs=File.separatorChar; 
		while (i>0)
		{	if (a[i]==fs || a[i]=='/')
			{	return new String(a,0,i); 
			}
			i--; 
		}
		return ""; 
	}
	static public String filename (String filename)
	{	char a[]=filename.toCharArray(); 
		int i=a.length-1; 
		char fs=File.separatorChar; 
		while (i>0)
		{	if (a[i]==fs || a[i]=='/')
			{	if (i+1<a.length) return new String(a,i+1,a.length-i-1); 
				else return ""; 
			}
			i--; 
		}
		return filename; 
	}
	static public String extension (String filename)
	{	char a[]=filename.toCharArray(); 
		int i=a.length-1; 
		char fs=File.separatorChar; 
		while (i>0)
		{	if (a[i]=='.')
			{	if (i+1<a.length) return new String(a,i+1,a.length-i-1); 
				else return ""; 
			}
			if (a[i]==fs || a[i]=='/') break; 
			i--; 
		}
		return ""; 
	}
	static public String chop (String filename)
		// chop the filename to 32 characters
	{	if (filename.length()>ChopLength)
		{	filename="..."+filename.substring(filename.length()-ChopLength); 
		}
		return filename; 
	}
	static public String relative (String dir, String filename)
	{	dir=dir+System.getProperty("file.separator"); 
		if (filename.startsWith(dir))
		{	return filename.substring(dir.length()); 
		}
		else return filename; 
	}
	static public String canonical (String filename)
	{	File f=new File(filename); 
		try
		{	String s=f.getCanonicalPath();
			if (s.length()>2 && s.charAt(1)==':')
				s=s.substring(0,2).toLowerCase()+s.substring(2);
			return s; 
		}
		catch (Exception e) {	return f.getAbsolutePath(); }
	}

}
