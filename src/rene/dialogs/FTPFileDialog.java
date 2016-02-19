package rene.dialogs;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import rene.gui.*;
import rene.util.regexp.*;
import rene.util.ftp.*;
import rene.util.*;

import rene.viewer.*;

public class FTPFileDialog
	extends CloseDialog
	implements Runnable
{	HistoryTextField Server,Path,User,Password;
	boolean Result=false;
	Lister L;
	String Separator=Global.getParameter("ftp.separator","/");
	String FtpDir="";
	ButtonAction Dir,OK;
	boolean Active;
	
	public FTPFileDialog (Frame f,
		String title, String prompt)
	{	super(f,title,true);
		setLayout(new BorderLayout());
		Panel p=new MyPanel();
		p.setLayout(new GridLayout(0,2));
		p.add(new MyLabel(Global.name("ftpdialog.server","Server")));
		p.add(Server=new HistoryTextField(this,"",32));
		Server.loadHistory("ftp.field.server");
		p.add(new MyLabel(Global.name("ftpdialog.filepath","File Path")));
		p.add(Path=new HistoryTextField(this,"Path",32));
		Path.loadHistory("ftp.field.path");
		p.add(new MyLabel(Global.name("ftpdialog.user","User")));
		p.add(User=new HistoryTextField(this,"",32));
		User.loadHistory("ftp.field.user");
		p.add(new MyLabel(Global.name("ftpdialog.password","Password")));
		p.add(Password=new HistoryTextField(this,"Password",32));
		Password.setEchoChar('*');
		add("North",new Panel3D(p));
		L=new Lister();
		if (Global.FixedFont!=null) L.setFont(Global.FixedFont);
		L.addActionListener(this);
		add("Center",new Panel3D(L));
		Panel ps=new MyPanel();
		ps.add(Dir=new ButtonAction(this,Global.name("ftpdialog.dir"),"Dir"));
		ps.add(OK=new ButtonAction(this,prompt,"OK"));
		ps.add(new ButtonAction(this,Global.name("abort","Abort"),"Close"));
		add("South",new Panel3D(ps));
		setSize("ftpdialog");
	}
	public void setServer (String s)
	{	Server.setText(s);
		Server.remember();
	}
	public void setPath (String s)
	{	Path.setText(s);
		Path.remember();
	}
	public void setUser (String s)
	{	User.setText(s);
		User.remember();
	}
	public void setPassword (String s)
	{	Password.setText(s);
	}
	public String getServer ()
	{	return Server.getText();
	}
	public String getPath ()
	{	return Path.getText();
	}
	public String getUser ()
	{	return User.getText();
	}
	public String getPassword ()
	{	return Password.getText();
	}
	public FTP getFTP ()
		throws IOException,UnknownHostException
	{	FTP ftp=new FTP(getServer());
		ftp.open(getUser(),Password.getText());
		return ftp;
	}
	public void doAction (String o)
	{	if (Active) return;
		noteSize("ftpdialog");
		if (o.equals("OK"))
		{	Server.remember();
			Server.saveHistory("ftp.field.server");
			Path.remember();
			Path.saveHistory("ftp.field.path");
			User.remember();
			User.saveHistory("ftp.field.user");
			Result=!Password.equals("");
			doclose();
		}
		else if (o.equals("Dir") && !Password.equals(""))
		{	Active=true;
			Dir.setEnabled(false);
			OK.setEnabled(false);
			new Thread(this).start();
		}
		else if (o.equals("Path") || o.equals("Password"))
		{	String path=Path.getText();
			if (path.endsWith("/") || path.endsWith("\\") 
				|| path.equals("") || path.equals("."))
			{	doAction("Dir");
			}
			else doAction("OK");
		}
		else super.doAction(o);
	}
	
	public void run ()
	{	L.setText("..\n");
		if (Path.getText().equals("")) Path.setText(".");
		try
		{	FTP ftp=new FTP(getServer());
			ftp.open(getUser(),Password.getText());
			String path=Path.getText();
			FtpDir=path;
			if (path.endsWith(Separator))
				path=path+".";
			Enumeration e=ftp.getDirectory(path).elements();
			while (e.hasMoreElements())
			{	String s=(String)e.nextElement();
				if (s.startsWith("d"))
					L.add(s,Color.green.darker().darker());
				else if (s.startsWith("l"))
					L.add(s,Color.blue.darker());
				else
					L.add(s);
			}
			L.doUpdate(false);
		}
		catch (Exception e)
		{	L.setText(e.toString());
			L.doUpdate(false);
		}
		Active=false;
		Dir.setEnabled(true);
		OK.setEnabled(true);
	}

	public void actionPerformed (ActionEvent e)
	{	if (e.getSource()==L)
		{	if (Active) return;
			String s=L.getSelectedItem();
			if (s.equals(".."))
			{	if (FtpDir.endsWith(Separator))
					FtpDir=FtpDir.substring(0,FtpDir.length()-1);
				Path.setText(FileName.path(FtpDir));
				doAction("Dir");
				return;
			}
			RegExp r=new RegExp(
				Global.getParameter("ftp.regexp","^([dl]*).* ([^[:white:]]+)$"),
					false);
			if (!r.match(s)) return;
			try
			{	String d=r.expand(Global.getParameter("ftp.regexp.dir","(0)"));
				s=r.expand(
					Global.getParameter("ftp.regexp.file","(1)"));
				if (d.equals("") || d.equals("l"))
				{	if (!FtpDir.endsWith(Separator)) FtpDir=FtpDir+Separator;
					Path.setText(FtpDir+s);
				}
				else
				{	if (!FtpDir.endsWith(Separator)) 
						FtpDir=FtpDir+Separator;
					Path.setText(FtpDir+s+Separator);
					doAction("Dir");
				}
			}
			catch (Exception ex) {}
		}
		else super.actionPerformed(e);
	}
	public boolean getResult () { return Result; }
}
