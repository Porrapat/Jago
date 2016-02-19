package rene.dialogs;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import rene.gui.*;
import rene.viewer.*;
import rene.util.*;

/**
This is a file dialog. It is easy to handle, remembers its position
and size, and performs pattern matching. The calls needs the
rene.viewer.* class, unless you replace Lister with List everywhere.
Moreover it needs the rene.gui.Global class to store field histories
and determine the background color. Finally, it uses the FileList
class to get the list of files.
<p>
The dialog does never check for files to exists. This must be done by
the application.
<p>
There is a static main method, which demonstrates everything.
*/

public class MyFileDialog extends CloseDialog
	implements ItemListener,FilenameFilter
{	//java.awt.List Dirs,Files;
	Lister Dirs,Files;
	HistoryTextField DirField,FileField,PatternField;
	HistoryTextFieldChoice DirHistory;
	TextField Chosen;
	String CurrentDir=".";
	boolean Aborted=true;

	/**
	@param title The dialog title.
	@param action The button string for the main action (e.g. Load)
	@param saving True, if this is a saving dialog.
	*/	
	public MyFileDialog (Frame f, 
		String title, String action, boolean saving, boolean help)
	{	super(f,title,true);
		setLayout(new BorderLayout());
		
		// title prompt
		add("North",new Panel3D(Chosen=new TextFieldAction(this,"")));
		Chosen.setEditable(false);
		
		// center panels
		Panel center=new MyPanel();
		center.setLayout(new GridLayout(1,2,5,0));
		Dirs=new Lister();
		if (Global.NormalFont!=null) Dirs.setFont(Global.NormalFont);
		Dirs.addActionListener(this);
		center.add(Dirs);
		Files=new Lister();
		if (Global.NormalFont!=null) Files.setFont(Global.NormalFont);
		Files.addActionListener(this);
		Files.addItemListener(this);
		center.add(Files);
		add("Center",new Panel3D(center));
		
		// south panel
		Panel south=new MyPanel();
		south.setLayout(new BorderLayout());
		Panel enter=new MyPanel();
		enter.setLayout(new GridLayout(0,2));
		enter.add(new MyLabel(Global.name("myfiledialog.dir")));
		enter.add(DirField=new HistoryTextField(this,"Dir",32));
		DirField.setText(".");
		enter.add(new MyLabel(""));
		enter.add(DirHistory=new HistoryTextFieldChoice(DirField));
		enter.add(new MyLabel(Global.name("myfiledialog.file")));
		enter.add(FileField=new HistoryTextField(this,"File"));
		enter.add(new MyLabel(Global.name("myfiledialog.pattern")));
		enter.add(PatternField=new HistoryTextField(this,"Pattern"));
		PatternField.setText("*");
		south.add("North",new Panel3D(enter));
		Panel buttons=new MyPanel();
		buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttons.add(new ButtonAction(this,action,"Action"));
		buttons.add(new ButtonAction(this,Global.name("abort"),"Close"));
		if (help) buttons.add(new ButtonAction(this,Global.name("help"),"Help"));
		south.add("South",buttons);
		add("South",new Panel3D(south));
		
		// set sizes
		pack();
		setSize("myfiledialog");
		addKeyListener(this);
		DirField.addKeyListener(this);
		DirField.setTrigger(true);
		PatternField.addKeyListener(this);
		PatternField.setTrigger(true);
		FileField.addKeyListener(this);
		FileField.setTrigger(true);
		loadHistories();
	}

	public MyFileDialog (Frame f, 
		String title, String action, boolean saving)
	{	this(f,title,action,saving,false);
	}

	FileDialog FD;

	public MyFileDialog (Frame f,
		String title, boolean saving)
	{	super(f,"",true);
		FD=new FileDialog(f,title,
			saving?FileDialog.SAVE:FileDialog.LOAD);
	}

	public void doAction (String o)
	{	if (o.equals("Dir") || o.equals("Pattern"))
		{	if (updateDir()) updateFiles();
			PatternField.remember(PatternField.getText());
		}
		else if (o.equals("File") || o.equals("Action"))
		{	leave();
		}
		else if (o.equals("Help"))
		{	help();
		}
		else super.doAction(o);
	}
	
	public void itemStateChanged (ItemEvent e)
	{	if (e.getSource()==Files)
		{	FileField.setText(Files.getSelectedItem());
		}
	}
	
	public void actionPerformed (ActionEvent e)
	{	if (e.getSource()==Dirs)
		{	String s=Dirs.getSelectedItem();
			if (s.equals("..")) dirup();
			else dirdown(s);
		}
		if (e.getSource()==Files)
		{	leave();
		}
		else super.actionPerformed(e);
	}
	
	public void dirup ()
	{	DirField.setText(FileName.path(CurrentDir));
		if (DirField.getText().equals(""))
			DirField.setText(""+File.separatorChar);
		if (updateDir()) updateFiles();		
	}
	
	public void dirdown (String subdir)
	{	DirField.setText(CurrentDir+File.separatorChar+subdir);
		if (updateDir()) updateFiles();
	}
	
	/**
	Leave the dialog and remember settings.
	*/
	void leave ()
	{	if (FD!=null) return;
		if (!FileField.equals("")) Aborted=false;
		if (!Aborted)
		{	noteSize("myfiledialog");
			DirField.remember(DirField.getText());
			DirField.saveHistory("myfiledialog.dir.history");
			PatternField.saveHistory("myfiledialog.pattern.history");
			FileField.remember(getFilePath());
			FileField.saveHistory("myfiledialog.file.history");
		}
		doclose();
	}

	/**
	Override this to provide help in some form.
	*/	
	public void help ()
	{
	}
	
	/**
	Update the directory list.
	@return if the current content of DirField is indeed a directory.
	*/
	public boolean updateDir ()
	{	if (FD!=null) return true;
		File dir=new File(DirField.getText()+File.separatorChar);
		if (!dir.isDirectory()) return false;
		try
		{	DirField.setText(dir.getCanonicalPath());
			Chosen.setText(
				DirField.getText()+File.separatorChar+
					PatternField.getText());
		}
		catch (Exception e) { return false; }
		return true;
	}
	
	/**
	Update the file list.
	*/
	public void updateFiles ()
	{	if (FD!=null) return;
		File dir=new File(DirField.getText());
		if (!dir.isDirectory()) return;
		CurrentDir=DirField.getText();
		if (PatternField.getText().equals("")) PatternField.setText("*");
		try
		{	Files.removeAll();
			Dirs.removeAll();
			FileList l=new FileList(DirField.getText(),
				PatternField.getText(),false);
			l.setCase(Global.getParameter("filedialog.usecaps",false));
			l.search();
			l.sort();
			Enumeration e=l.files();
			while (e.hasMoreElements())
			{	File f=(File)e.nextElement();
				Files.add(FileName.filename(f.getCanonicalPath()));
			}
			Dirs.add("..");
			e=l.dirs();
			while (e.hasMoreElements())
			{	File f=(File)e.nextElement();
				Dirs.add(FileName.filename(f.getCanonicalPath()));
			}
		}
		catch (Exception e) { System.out.println(e); }
		Files.requestFocus();
	}
	
	public void setDirectory (String dir)
	{	if (FD!=null) FD.setDirectory(dir);
		else DirField.setText(dir);
	}
	
	public void setPattern (String pattern)
	{	if (FD!=null)
		{	FD.setFilenameFilter(this); // does not work
			String s=pattern.replace(' ',';');
			FD.setFile(s);
		}
		else PatternField.setText(pattern);
	}
	
	public void setFilePath (String file)
	{	if (FD!=null)
		{	FD.setFile(file);
			return;
		}
		String dir=FileName.path(file);
		if (!dir.equals(""))
		{	DirField.setText(dir);
			FileField.setText(FileName.filename(file));
		}
		else FileField.setText(file);
	}
	
	/**
	Check, if the dialog was aborted.
	*/
	public boolean isAborted ()
	{	if (FD!=null) return FD.getFile()==null || FD.getFile().equals("");
		else return Aborted;
	}
	
	/**
	@return The file plus its path.
	*/
	public String getFilePath ()
	{	if (FD!=null) 
		{	if (FD.getFile()!=null) return FD.getDirectory()+FD.getFile();
			else return "";
		}
		String file=FileField.getText();
		if (!FileName.path(file).equals("")) return file;
		else return CurrentDir+File.separatorChar+FileField.getText();
	}

	public void loadHistories ()
	{	DirField.loadHistory("myfiledialog.dir.history");
		PatternField.loadHistory("myfiledialog.pattern.history");
		FileField.loadHistory("myfiledialog.file.history");
	}

	/**
	This should be called at the start.
	*/	
	public void update ()
	{	if (FD!=null) return;
		loadHistories();
		setFilePath(FileField.getText());
		if (updateDir()) updateFiles();
		DirHistory.update();
		Aborted=true;
	}
	
	public void setVisible (boolean flag)
	{	if (FD!=null) FD.setVisible(flag);
		else super.setVisible(flag);
	}
	
	public void center (Frame f)
	{	if (FD!=null) CloseDialog.center(f,FD);
		else super.center(f);
	}

	public static void main (String args[])
	{	Frame f=new CloseFrame()
		{	public void doclose ()
			{	System.exit(0);
			}
		};
		f.setSize(500,500);
		f.setLocation(400,400);
		f.setVisible(true);
		MyFileDialog d=new MyFileDialog(f,"Title",false);
		d.center(f);
		d.update();
		d.setVisible(true);
	}

	public void focusGained (FocusEvent e)
	{	FileField.requestFocus();
	}
	
	public boolean accept (File dir, String file)
	{	return true;
	}
}
