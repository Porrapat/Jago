package rene.dialogs;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import rene.gui.*;
import rene.util.FileList;

class FileListFinder extends FileList
{	String Res;	
	public FileListFinder (String dir, String pattern, boolean recurse)
	{	super(dir,pattern,recurse);
	}
	public boolean file (File file)
	{	try
		{	Res=file.getCanonicalPath();
		}
		catch (Exception e) {}
		return false;
	}
	public String getResult ()
	{	return Res; 
	}
}

class SearchFileThread extends Thread
{	SearchFileDialog D;	
	MyList L;	
	String Dir,Pattern;
	boolean Recurse;
	public SearchFileThread (SearchFileDialog dialog,
		MyList l, String d, String p, boolean r)
	{	D=dialog; L=l; Dir=d; Pattern=p; Recurse=r;
	}
	public void run ()
	{	D.enableButtons(false);	
		L.removeAll();
		L.setEnabled(false);
		FileList f=new FileList(Dir,Pattern,Recurse);
		D.F=f;
		f.search();
		f.sort();
		Enumeration e=f.files();
		while (e.hasMoreElements())
		{	try	
			{	L.add(((File)e.nextElement()).getCanonicalPath());
			}
			catch (Exception ex) {}
		}
		L.setEnabled(true);
		D.enableButtons(true);
	}
}

/**
This is a dialog to search a subtree for a specific file. The user can
enter a directory and a file pattern containing * and ?. He can choose
between immediate search and open, or search/select/open. Abort will
result in an empty string. The calling routine checks the result file
name with getResult().
<p>
You need to specify the following properties
<pre>
searchfile.title=Search File
searchfile.directory=Directory
searchfile.pattern=Pattern
searchfile.search=Search
searchfile.searchrek=Search Subdirectories
</pre>
*/

public class SearchFileDialog extends CloseDialog
	implements Runnable, Enumeration
{	HistoryTextField Dir,Pattern;
	MyList L;
	static public int ListNumber=Global.getParameter("searchfile.number",10);
	String Result=null;
	Button ActionButton,CloseButton,SearchButton,SearchrekButton;
	public FileList F=null;
	public boolean Abort=true;
	Checkbox Mod;
	public SearchFileDialog (Frame f, String action,
		String modify, boolean modifystate)
	{	super(f,Global.name("searchfile.title"),true);
		setLayout(new BorderLayout());
		Panel north=new MyPanel();
		north.setLayout(new BorderLayout());
		Panel northa=new MyPanel();
		northa.setLayout(new BorderLayout());
		Panel north1=new MyPanel();
		north1.setLayout(new GridLayout(0,2));
		north1.add(new MyLabel(Global.name("searchfile.directory")));
		north1.add(Dir=new HistoryTextField(this,"Dir",20));
		Dir.setText(".");
		north1.add(new MyLabel(Global.name("searchfile.pattern")));
		north1.add(Pattern=new HistoryTextField(this,"TextAction",20));
		northa.add("Center",north1);
		Panel north2=new MyPanel();
		north2.add(SearchButton=
			new ButtonAction(this,Global.name("searchfile.search"),"Search"));
		north2.add(SearchrekButton=
			new ButtonAction(this,Global.name("searchfile.searchrek"),"SearchRek"));
		northa.add("South",north2);
		north.add("North",northa);
		add("North",new Panel3D(north));
		add("Center",new Panel3D(L=new MyList(ListNumber)));
		L.addActionListener(this);
		L.setMultipleMode(true);
		Panel south=new MyPanel();
		south.setLayout(new FlowLayout(FlowLayout.RIGHT));
		if (!modify.equals(""))
		{	south.add(Mod=
				new CheckboxAction(this,modify,""));
			Mod.setState(modifystate);
		}
		south.add(ActionButton=
			new ButtonAction(this,action,"Action"));
		south.add(CloseButton=
			new ButtonAction(this,Global.name("abort"),"Close"));
		add("South",new Panel3D(south));
		pack();
		Dir.loadHistory("searchfile.dir");
		Pattern.loadHistory("searchfile.pattern");
		
		// size
		setSize("searchfiledialog");
		addKeyListener(this);
		Dir.addKeyListener(this);
		Pattern.addKeyListener(this);
	}
	public SearchFileDialog (Frame f, String action)
	{	this(f,action,"",false);
	}
	public void actionPerformed (ActionEvent e)
	{	if (e.getSource()==L)
		{	action();
		}
		else super.actionPerformed(e);
	}
	public void doAction (String o)
	{	Result=null;
		if (o.equals("SearchRek")) search(true);
		else if (o.equals("Search")) search(false);
		else if (o.equals("TextAction")) { L.removeAll(); action(); }
		else if (o.equals("Action")) action();
		else if (o.equals("Help")) help ();
		else if (o.equals("Close")) { Abort=true; doclose(); }
	}
	public void help () {}
	Thread Run;
	public void search (boolean recurse)
	{	saveHistory();
		if (Run!=null && Run.isAlive()) return;
		Run=new SearchFileThread(this,L,
			Dir.getText(),Pattern.getText(),recurse);
		Run.start();
	}
	public void action ()
	{	saveHistory();
		if (Run!=null && Run.isAlive()) return;	
		Run=new Thread(this);
		Run.start();
	}
	public void enableButtons (boolean f)
	{	Pattern.setEnabled(f);
		SearchButton.setEnabled(f);
		SearchrekButton.setEnabled(f);
		ActionButton.setEnabled(f);
	}
	public void run ()
	{	Result=null;
		enableButtons(false);
		if (L.getItemCount()>0)
		{	int i=L.getSelectedIndex();	
			if (i>0) Result=L.getItem(i);
			else Result=L.getItem(0);
		}
		else
		{	FileListFinder f=new FileListFinder(Dir.getText(),
				Pattern.getText(),true);
			F=f;
			f.search();
			Result=f.getResult();
		}
		enableButtons(true);
		Abort=false;
		doclose();
	}
	public String getResult() { return Result; }
	public void focusGained (FocusEvent e)
	{	Pattern.requestFocus();
	}
	public void setVisible (boolean flag)
	{	if (flag) enableButtons(true);
		super.setVisible(flag);
	}
	public boolean close ()
	{	Abort=true;
		return true;
	}
	public void doclose ()
	{	if (F!=null) F.stopIt();
		Dir.saveHistory("searchfile.dir");
		Pattern.saveHistory("searchfile.pattern");
		noteSize("searchfiledialog");
		super.doclose();
	}
	public void saveHistory ()
	{	Dir.remember();
		Pattern.remember();
	}
	public void setPattern (String s)
	{	Pattern.setText(s);
	}
	String S[];
	int Sn;
	/**
	Get an enumeration of selected files. Should check for an aborted
	dialog before.
	*/
	public Enumeration getFiles ()
	{	S=L.getSelectedItems();
		Sn=0;
		return this;
	}
	public boolean hasMoreElements ()
	{	return Sn<S.length;
	}
	public Object nextElement ()
	{	if (Sn>=S.length) return null;
		String s=S[Sn];
		Sn++;
		return s;
	}
	public boolean isAborted ()
	{	return Abort;
	}
	public void deselectAll ()
	{	for (int i=L.getItemCount()-1; i>=0; i--)
		{	L.deselect(i);
		}
	}
	public boolean isModified ()
	{	return Mod.getState();
	}
}
