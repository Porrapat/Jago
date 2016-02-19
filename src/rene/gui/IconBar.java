package rene.gui;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.io.*;

/**
These are the common things to Separators and Incons.
*/

interface IconBarElement
{	public int width ();
	public void getPosition (int x, int y);
	public Point getPosition ();
	public void setEnabled (boolean flag);
	public String getName ();
}

/**
A simple separator between icons.
*/

class Separator implements IconBarElement
{	final int Size=4;
	public int width ()
	{	return Size;
	}
	public void getPosition (int x, int y) {}
	public Point getPosition () { return new Point(0,0); }
	public void setEnabled (boolean flag) {}
	public String getName () { return ""; }
}

class PrimitiveIcon extends Panel
	implements MouseListener,IconBarElement,Runnable
{	IconBar Bar;
	String Name;
	boolean Enabled,Pressed,Unset;
	boolean Focus=false;
	final static int Size=20;

	public PrimitiveIcon (IconBar bar, String name)
	{	Bar=bar; Name=name; Enabled=true; Pressed=false; Unset=false;
		addMouseListener(this);
		enableEvents(AWTEvent.KEY_EVENT_MASK);
		setSize(Size,Size);
	}
	
	public void processKeyEvent (KeyEvent e)
	{	Bar.getKey(e);
	}

	/**
	Paint a button with an image
	*/
	public void paint (Graphics g)
	{	g.setColor(getBackground());
		if (Enabled) g.fill3DRect(0,0,Size,Size,!Pressed);
		else g.fillRect(0,0,Size,Size);
		g.setColor(getForeground());
		if (Unset) g.drawRect(0,0,Size,Size);
		dopaint(g);		
		if (Focus) showFocus(g);
	}
	
	public void showFocus (Graphics g)
	{	g.setColor(Color.white);
		g.drawRect(4,4,1,1);
		g.drawRect(Size-5,4,1,1);
		g.drawRect(4,Size-5,1,1);
		g.drawRect(Size-5,Size-5,1,1);
	}
	
	public void dopaint (Graphics g)
	{
	}
	
	public void update (Graphics g)
	{	paint(g);
	}
	
	/**
	Repaint in pressed mode.
	*/
	public void mousePressed (MouseEvent e)
	{	if (!Enabled) return;
		Pressed=true; Unset=false; repaint();
	}

	/**
	Repaint in unpressed mode, when the user releases the button.
	*/
	public void mouseReleased (MouseEvent e)
	{	if (!Enabled) return;
		Pressed=false; repaint();
		Dimension d=getSize();
		if (e.getX()<0 || e.getX()>d.width ||
			e.getY()<0 || e.getY()>d.height) return;
		T=null;
		Bar.iconPressed(Name,e.isShiftDown(),e.isControlDown());
	}
	
	public void mouseClicked (MouseEvent e) {}
	
	Thread T;
	
	/**
	Start a thread, that waits for one second, then tells
	the icon bar to display the proper help text.
	*/
	public synchronized void mouseEntered (MouseEvent e)
	{	if (T!=null || !Global.getParameter("iconbar.showtips",true)) return;
		T=new Thread(this);
		T.start();
	}
	public void run ()
	{	try
		{	T.sleep(2000);
		}
		catch (Exception e) {}
		if (T!=null)
		{	synchronized(this)
			{	try
				{	Point P=getLocationOnScreen();
					String help=Global.name("iconhelp."+Name,"");
					if (help.equals("") && Name.length()>1)
					{	help=Global.name("iconhelp."+
							Name.substring(0,Name.length()-1)+"?","");
					}
					if (help.equals(""))
						help=Global.name("iconhelp.nohelp","No help available");
					Bar.displayHelp(this,help);
				}
				catch (Exception e) {}
			}
			try
			{	T.sleep(5000);
			}
			catch (Exception e) {}
			if (T!=null) Bar.removeHelp();
			T=null;
		}
	}

	/**
	Tell the run method, that display is no longer necessary,
	and remove the help text.
	*/
	public synchronized void mouseExited (MouseEvent e)
	{	T=null;
		Bar.removeHelp();
	}
	
	// for the IconBarElement interface
	
	public int width ()
	{	return Size;
	}
	public void getPosition (int x, int y)
	{	setLocation(x,y);
	}
	public Point getPosition ()
	{	return getLocationOnScreen();
	}
	public void setEnabled (boolean flag) 
	{	if (Enabled==flag) return;
		Enabled=flag;
		repaint();
	}
	public String getName ()
	{	return Name;
	}

	public boolean hasFocus () { return Focus; }
	public void setFocus (boolean flag) { Focus=flag; repaint(); }	
	
	public boolean isSet ()
	{	return !Unset;
	}
	
	public void unset ()
	{	Unset=true;
	}
}

/**
An Icon. Loads its image from a resource. The icon can be Enabled
or Disabled. It will display a tool tip, when the mouse remains
over it for more than a second. The help text is derived from
Global.name("iconhelp."+Name).
*/

class SingleIcon extends PrimitiveIcon
{	Image I;
	Color C;
	int W,H,X,Y;
	
	/**
	Initialize the icon and load its image.
	*/
	public SingleIcon (IconBar bar, String file)
	{	super(bar,file);
		try
		{	InputStream in=getClass().getResourceAsStream(
				Bar.Resource+file+".gif");
			int pos=0;
			int n=in.available();
			byte b[]=new byte[20000];
			while (n>0)
			{	int k=in.read(b,pos,n);
				if (k<0) break;
				pos+=k;
				n=in.available();
			}
			in.close();
			I=Toolkit.getDefaultToolkit().createImage(b,0,pos);
			MediaTracker T=new MediaTracker(bar);
			T.addImage(I,0);
			T.waitForAll();
			W=I.getWidth(this);
			H=I.getHeight(this);
			X=Size/2-W/2;
			Y=Size/2-H/2;
		}
		catch (Exception e)
		{	I=null;
		}
	}
	public SingleIcon (IconBar bar, String name, Color color)
	{	super(bar,name);
		C=color;
	}
	
	public void dopaint (Graphics g)	
	{	if (I!=null) g.drawImage(I,X,Y,this);
		else if (C!=null)
		{	g.setColor(C);
			g.fillRect(3,3,Size-6,Size-6);
		}
	}
	
}

class MultipleIcon extends PrimitiveIcon
{	int N;
	Image I[];
	int Selected;
	int X[],Y[],W[],H[];
	
	public MultipleIcon (IconBar bar, String name, int number)
	{	super(bar,name);
		N=number;
		I=new Image[N];
		X=new int[N];
		Y=new int[N];
		W=new int[N];
		H=new int[N];
		MediaTracker T=new MediaTracker(bar);
		try
		{	for (int i=0; i<N; i++)
			{	try
				{	InputStream in=getClass().getResourceAsStream(
						Bar.Resource+name+i+".gif");
					int pos=0;
					int n=in.available();
					byte b[]=new byte[20000];
					while (n>0)
					{	int k=in.read(b,pos,n);
						if (k<0) break;
						pos+=k;
						n=in.available();
					}
					in.close();
					I[i]=Toolkit.getDefaultToolkit().createImage(b,0,pos);
					T.addImage(I[i],i);
				}
				catch (Exception e)
				{	I[i]=null;
				}
			}
			T.waitForAll();
			for (int i=0; i<N; i++)
			{	W[i]=I[i].getWidth(this);
				H[i]=I[i].getHeight(this);
				X[i]=Size/2-W[i]/2;
				Y[i]=Size/2-H[i]/2;
			}
		}
		catch (Exception e)
		{	for (int i=0; i<N; i++) I[i]=null;
		}
	}
	
	public MultipleIcon (IconBar bar, String name)
	{	super(bar,name);
		Selected=0;
	}

	/**
	Paint a button with an image
	*/
	public void dopaint (Graphics g)
	{	if (I[Selected]!=null)
			g.drawImage(I[Selected],X[Selected],Y[Selected],this);
	}
	
	public void mousePressed (MouseEvent e)
	{	if (!Enabled) return;
		Dimension d=getSize();
		if (e.getX()<0 || e.getX()>d.width ||
			e.getY()<0 || e.getY()>d.height) return;
		Selected++; 
		Unset=false;
		if (Selected>=N) Selected=0;
		repaint();
		T=null;
		Bar.iconPressed(Name,e.isShiftDown(),e.isControlDown());
	}

	public void mouseReleased (MouseEvent e) {}
	public void mouseClicked (MouseEvent e) {}
	
	public void setSelected (int s)
	{	if (Selected==s) return;
		Selected=s;
		repaint();
	}
	
	public int getSelected ()
	{	return Selected;
	}
}

class ColorIcon extends MultipleIcon
{	Color Colors[];
	public ColorIcon (IconBar bar, String name, Color colors[])
	{	super(bar,name);
		N=colors.length;
		Colors=colors;
	}
	public void dopaint (Graphics g)
	{	g.setColor(Colors[Selected]);
		g.fillRect(3,3,Size-6,Size-6);
	}	
}

/**
This is much like a single icon but it displays a
pressed and an unpressed state.
*/

class ToggleIcon extends SingleIcon
{	boolean State;
	private IconGroup G;

	public ToggleIcon (IconBar bar, String file, IconGroup g)
	{	super(bar,file);
		State=false; G=g;
	}
	public ToggleIcon (IconBar bar, String file, Color c, IconGroup g)
	{	super(bar,file,c);
		State=false; G=g;
	}
	public ToggleIcon (IconBar bar, String file)
	{	this(bar,file,null);
	}

	/**
	Repaint in pressed mode.
	*/
	public void mouseReleased (MouseEvent e) {}
	public void mousePressed (MouseEvent e)
	{	if (!Enabled) return;
		Dimension d=getSize();
		if (e.getX()<0 || e.getX()>d.width ||
				e.getY()<0 || e.getY()>d.height) return;
		if (G!=null)
		{	G.toggle(this);
			Pressed=State=true;
		}
		else
		{	State=!State;
			Pressed=State; repaint();
		}
		Unset=false;
		T=null;
		Bar.iconPressed(Name,e.isShiftDown(),e.isControlDown());
	}
	public void mouseClicked (MouseEvent e) {}
	
	public boolean getState () { return State; }
	public void setState (boolean state)
	{	if (G!=null) G.toggle(this);
		else
		{	if (Pressed==state) { State=state; return; }
			Pressed=State=state;
			repaint();
		}
	}
	public void unselect ()
	{	if (G!=null) G.unselect();
	}
	public void setStateInGroup (boolean state)
	{	if (Pressed==state) { State=state; return; }
		Pressed=State=state;
		repaint();
	}
	public int countPeers ()
	{	if (G==null) return 0;
		return G.getN();
	}
	public void unset ()
	{	if (G!=null) G.unset();
		else super.unset();
	}
	public void dounset ()
	{	super.unset();
	}
}

/**
This class can add several ToggleItems and will enable only one
of them.
*/

class IconGroup
{	String Files[],Breaks[];
	IconBar Bar;
	int N;
	ToggleIcon Icons[];
	public IconGroup (IconBar bar, String files[], String breaks[])
	{	Files=files; Breaks=breaks; Bar=bar;
		init();
	}
	public IconGroup (IconBar bar, String files[])
	{	this(bar,files,files);
	}
	public void init ()
	{	N=0;
		for (int i=0; i<Files.length; i++)
			if (!Files[i].equals("")) N++;
		Icons=new ToggleIcon[N];
		int k=0;
		for (int i=0; i<Files.length; i++)
		{	if (!Files[i].equals(""))
			{	Icons[k++]=new ToggleIcon(Bar,Files[i],this);
			}
		}		
	}
	public IconGroup (IconBar bar, String name, int n)
	{	Breaks=Files=new String[n]; 
		for (int i=0; i<n; i++)
		{	Files[i]=name+i;
		}
		Bar=bar;
		init();
	}
	public IconGroup (IconBar bar, String name, Color colors[])
	{	N=colors.length;
		Breaks=Files=new String[N];
		for (int i=0; i<N; i++)
		{	Files[i]=name+i;
		}
		Bar=bar;
		Icons=new ToggleIcon[N];
		for (int i=0; i<N; i++)
		{	Icons[i]=new ToggleIcon(Bar,Files[i],colors[i],this);
		}		
	}
	public void addLeft ()
	{	int i=0;
		for (int k=0; k<Files.length; k++)
			if (Files[k].equals("")) Bar.addSeparatorLeft();
			else
			{	if (Breaks[k].startsWith("!")) Bar.addSeparatorLeft();
				Bar.addLeft(Icons[i++]);
			}
	}
	public void addRight ()
	{	int i=0;
		for (int k=0; k<Files.length; k++)
			if (Files[k].equals("")) Bar.addSeparatorRight();
			else 
			{	if (Breaks[k].startsWith("!")) Bar.addSeparatorRight();
				Bar.addRight(Icons[i++]);
			}
	}
	public void toggle (ToggleIcon icon)
	{	for (int i=0; i<N; i++)
		{	if (Icons[i]==icon) icon.setStateInGroup(true);
			else Icons[i].setStateInGroup(false);
		}
	}
	public void unselect ()
	{	for (int i=0; i<N; i++)
		{	Icons[i].setStateInGroup(false);
		}
	}
	public int getN () { return N; }
	public void unset ()
	{	for (int i=0; i<N; i++)
		{	Icons[i].dounset();
		}
	}
}

/**
An state display. Loads two images from a resource and display either
of them, depending on the enabled state.
*/

class StateDisplay extends PrimitiveIcon
{	Image IOn,IOff;
	int W,H,X,Y;
	
	/**
	Initialize the icon and load its image.
	*/
	public StateDisplay (IconBar bar, String file)
	{	super(bar,file);
		try
		{	InputStream in=getClass().getResourceAsStream(
				Bar.Resource+file+"on"+".gif");
			int pos=0;
			int n=in.available();
			byte b[]=new byte[20000];
			while (n>0)
			{	int k=in.read(b,pos,n);
				if (k<0) break;
				pos+=k;
				n=in.available();
			}
			in.close();
			IOn=Toolkit.getDefaultToolkit().createImage(b,0,pos);
			MediaTracker T=new MediaTracker(bar);
			T.addImage(IOn,0);
			in=getClass().getResourceAsStream(
				Bar.Resource+file+"off"+".gif");
			pos=0;
			n=in.available();
			byte b1[]=new byte[20000];
			while (n>0)
			{	int k=in.read(b1,pos,n);
				if (k<0) break;
				pos+=k;
				n=in.available();
			}
			in.close();
			IOff=Toolkit.getDefaultToolkit().createImage(b1,0,pos);
			T.addImage(IOff,1);
			T.waitForAll();
			W=IOn.getWidth(this);
			H=IOn.getHeight(this);
			X=0;
			Y=Size/2-H/2;
		}
		catch (Exception e)
		{	IOn=IOff=null;
		}
	}
	
	/**
	Paint a button with an image
	*/
	public void paint (Graphics g)
	{	if (Enabled && IOn!=null) g.drawImage(IOn,X,Y,this);
		else if (!Enabled && IOff!=null) g.drawImage(IOff,X,Y,this);
	}
	
	public void mousePressed (MouseEvent e) {}
	public void mouseReleased (MouseEvent e) { T=null; }
	public void mouseClicked (MouseEvent e) {}	
}

/**
This panel will display icons vertically.
*/

public class IconBar extends Panel
	implements KeyListener, FocusListener
{	Vector Left=new Vector(),Right=new Vector();
	int W;
	Frame F;
	public final int Offset=4;
	public String Resource="/";
	int Focus=0;
	public boolean TraverseFocus=true;
	public boolean UseSize=true;
	
	public IconBar (Frame f, boolean traversefocus)
	{	F=f; TraverseFocus=traversefocus;
		if (Global.ControlBackground!=null)
			setBackground(Global.ControlBackground);
		else setBackground(SystemColor.menu);
		setLayout(null);
		W=Offset*2;
		addKeyListener(this);
		if (TraverseFocus) addFocusListener(this);
	}
	public IconBar (Frame f)
	{	this(f,true);
	}	
	
	public void keyPressed (KeyEvent e) {}
	public void keyReleased (KeyEvent e)
	{	switch (e.getKeyCode())
		{	case KeyEvent.VK_RIGHT :
				setFocus(Focus,false);
				Focus++;
				if (Focus>=Left.size()+Right.size()) Focus=0;
				while (!(getIcon(Focus) instanceof PrimitiveIcon))
				{	Focus++;
					if (Focus>=Left.size()+Right.size())
					{	Focus=0; break;
					}
				}
				setFocus(Focus,true);
				break;
			case KeyEvent.VK_LEFT :
				setFocus(Focus,false);
				Focus--;
				if (Focus<0) Focus=Left.size()+Right.size()-1;
				while (!(getIcon(Focus) instanceof PrimitiveIcon))
				{	Focus--;
					if (Focus<0)
					{	Focus=Left.size()+Right.size()-1;
						break;
					}
				}
				setFocus(Focus,true);
				break;
			case KeyEvent.VK_SPACE :
				try
				{	PrimitiveIcon icon=(PrimitiveIcon)getIcon(Focus);
					icon.mouseReleased(new MouseEvent(this,
						MouseEvent.MOUSE_RELEASED,0,0,0,0,1,false));
				}
				catch (Exception ex) {}
				break;
		}
	}
	public void keyTyped (KeyEvent e) {}
	
	/*
	public boolean isFocusTraversable ()
	{	return TraverseFocus;
	}
	*/
	
	public Object getIcon (int n)
	{	if (n<Left.size()) return Left.elementAt(n);
		else return Right.elementAt(Right.size()-1-(n-Left.size()));
	}
	
	public void focusGained (FocusEvent e)
	{	if (TraverseFocus) setFocus(Focus,true);
	}
	public void focusLost (FocusEvent e)
	{	if (TraverseFocus) setFocus(Focus,false);
	}
		
	public void setFocus (int n, boolean flag)
	{	if (!TraverseFocus) return;
		try
		{	if (n<Left.size())
			{	PrimitiveIcon icon=(PrimitiveIcon)Left.elementAt(n);
				icon.setFocus(flag);
			}
			else
			{	PrimitiveIcon icon=(PrimitiveIcon)Right.elementAt(
					Right.size()-1-(n-Left.size()));
				icon.setFocus(flag);
			}
		}
		catch (Exception e) {}
	}
	
	/**
	Add an icon
	*/
	public void addLeft (String name)
	{	addLeft(new SingleIcon(this,name));
	}
	public void addLeft (PrimitiveIcon i)
	{	Left.addElement(i);
		add(i);
		W+=i.width()+Offset;
	}

	/**
	Add an icon at the right end
	*/
	public void addRight (String name)
	{	addRight(new SingleIcon(this,name));
	}
	public void addRight (PrimitiveIcon i)
	{	Right.addElement(i);
		add(i);
		W+=i.width()+Offset;
	}

	/**
	Add a toggle icon
	*/
	public void addToggleLeft (String name)
	{	addLeft(new ToggleIcon(this,name));
	}
	public void addToggleRight (String name)
	{	addRight(new ToggleIcon(this,name));
	}
	
	/**
	Add a complete groupe of toggle items.
	*/
	public void addToggleGroupLeft (String names[], String breaks[])
	{	IconGroup g=new IconGroup(this,names,breaks);
		g.addLeft();
	}
	public void addToggleGroupRight (String names[], String breaks[])
	{	IconGroup g=new IconGroup(this,names,breaks);
		g.addRight();
	}
	public void addToggleGroupLeft (String names[])
	{	addToggleGroupLeft(names,names);
	}
	public void addToggleGroupRight (String names[])
	{	addToggleGroupRight(names,names);
	}
	public void addToggleGroupLeft (String name, int n)
	{	IconGroup g=new IconGroup(this,name,n);
		g.addLeft();
	}
	public void addToggleGroupRight (String name, int n)
	{	IconGroup g=new IconGroup(this,name,n);
		g.addRight();
	}
	public void addToggleGroupLeft (String name, Color colors[])
	{	IconGroup g=new IconGroup(this,name,colors);
		g.addLeft();
	}
	public void addToggleGroupRight (String name, Color colors[])
	{	IconGroup g=new IconGroup(this,name,colors);
		g.addRight();
	}

	/**
	Add a separator
	*/
	public void addSeparatorLeft ()
	{	Separator s=new Separator();
		Left.addElement(s);
		W+=s.width()+Offset;
	}
	public void addSeparatorRight ()
	{	Separator s=new Separator();
		Right.addElement(s);
		W+=s.width()+Offset;
	}

	/**
	Add a multiple icon (can toggle between the icons)
	*/
	public void addMultipleIconLeft (String name, int number)
	{	addLeft(new MultipleIcon(this,name,number));
	}
	public void addMultipleIconRight (String name, int number)
	{	addRight(new MultipleIcon(this,name,number));
	}
	
	/**
	Add a multiple icon (can toggle between the colors)
	*/
	public void addColorIconLeft (String name, Color colors[])
	{	addLeft(new ColorIcon(this,name,colors));
	}
	public void addColorIconRight (String name, Color colors[])
	{	addRight(new ColorIcon(this,name,colors));
	}

	/**
	Add a state display at the left end.
	*/
	public void addStateLeft (String name)
	{	addLeft(new StateDisplay(this,name));
	}
	public void addStateRight (String name)
	{	addRight(new StateDisplay(this,name));
	}
	
	/**
	Override the layout and arange the icons from the
	left and the right.
	*/
	public void doLayout ()
	{	int x;
		x=getSize().width;
		for (int k=0; k<Right.size(); k++)
		{	IconBarElement i=(IconBarElement)Right.elementAt(k);
			x-=i.width();
			i.getPosition(x,2);
			x-=Offset;
		}
		int xmax=x;
		x=0;
		for (int k=0; k<Left.size(); k++)
		{	IconBarElement i=(IconBarElement)Left.elementAt(k);
			i.getPosition(x,2);
			x+=i.width();
			x+=Offset;
			if (x+SingleIcon.Size>xmax) x=-1000;
		}
	}
	
	/**
	Override the preferred sizes.
	*/
	public Dimension getPreferredSize ()
	{	if (!UseSize) return new Dimension(10,SingleIcon.Size+4);
		return new Dimension(W+10,SingleIcon.Size+4);
	}
	public Dimension getMinimumSize ()
	{	return getPreferredSize();
	}
	
	// The IconBar can notify one IconBarListener on icon
	// clicks.
	
	IconBarListener L=null;
	
	public void setIconBarListener (IconBarListener l)
	{	L=l;
	}
	
	public void removeIconBarListener (IconBarListener l)
	{	L=null;
	}
	
	boolean Shift,Control;
	
	public void iconPressed (String name, boolean shift, boolean control)
	{	Shift=shift; Control=control;
		removeHelp();
		if (L!=null) L.iconPressed(name);
	}
	
	public boolean isShiftPressed () { return Shift; }
	public boolean isControlPressed () { return Control; }
	
	// The tool tip help, initiated by the icons.
	
	Window WHelp=null;
	
	public synchronized void displayHelp (IconBarElement i, String text)
	{	if (F==null || WHelp!=null) return;
		Point P=i.getPosition();
		WHelp=new Window(F);
		WHelp.add("Center",new MyLabel(text));
		WHelp.pack();
		Dimension d=WHelp.getSize();
		Dimension ds=getToolkit().getScreenSize();
		int x=P.x,y=P.y+i.width()+10;
		if (x+d.width>ds.width) x=ds.width-d.width;
		if (y+d.height>ds.height) y=P.y-i.width()-d.height;
		WHelp.setLocation(x,y);
		WHelp.show();
	}
	
	public synchronized void removeHelp ()
	{	if (WHelp==null) return;
		WHelp.setVisible(false);
		WHelp.dispose();
		WHelp=null;
	}
	
	private PrimitiveIcon find (String name)
	{	int k;
		for (k=0; k<Left.size(); k++)
		{	try
			{	PrimitiveIcon i=(PrimitiveIcon)Left.elementAt(k);
				if (i.getName().equals(name)) return i;
			}
			catch (Exception e) {}
		}
		for (k=0; k<Right.size(); k++)
		{	try
			{	PrimitiveIcon i=(PrimitiveIcon)Right.elementAt(k);
				if (i.getName().equals(name)) return i;
			}
			catch (Exception e) {}
		}
		return null;
	}

	/**
	Enable the tool with the specified name.
	*/
	public void setEnabled (String name, boolean flag)
	{	PrimitiveIcon icon=find(name);
		if (icon==null) return;
		icon.setEnabled(flag);
	}

	/**
	Select
	*/
	public void toggle (String name)
	{	PrimitiveIcon icon=find(name);
		if (icon==null || !(icon instanceof ToggleIcon)) return;
		((ToggleIcon)icon).setState(true);
	}
	
	/**
	Have an Icon?
	*/
	public boolean have (String name)
	{	return find(name)!=null;
	}
	
	/**
	Deselect all icons in the group of an icon
	*/
	public void unselect (String name)
	{	PrimitiveIcon icon=find(name);
		if (icon==null || !(icon instanceof ToggleIcon)) return;
		((ToggleIcon)icon).unselect();
	}
	
	/**
	Toggle an item of an item group (known by name and number).
	*/
	public void toggle (String name, int n)
	{	toggle(name+n);
	}
	
	/**
	Set the state of a single toggle icon.
	*/
	public void setState (String name, boolean flag)
	{	PrimitiveIcon icon=find(name);
		if (icon==null || !(icon instanceof ToggleIcon)) return;
		((ToggleIcon)icon).setState(flag);
	}

	/**
	Get the state of the specified toggle icon
	*/
	public boolean getState (String name)
	{	PrimitiveIcon icon=find(name);
		if (icon==null || !(icon instanceof ToggleIcon)) return false;
		return ((ToggleIcon)icon).getState();
	}
	
	/**
	Return the state of a toggle icon.
	*/
	public int getToggleState (String name)
	{	PrimitiveIcon icon=find(name+0);
		if (icon==null || !(icon instanceof ToggleIcon)) return -1;
		int n=((ToggleIcon)icon).countPeers();
		for (int i=0; i<n; i++)
		{	if (getState(name+i)) return i;
		}
		return -1;
	}

	/**
	Get the state of the specified multiple icon
	*/
	public int getMultipleState (String name)
	{	int k;
		for (k=0; k<Left.size(); k++)
		{	IconBarElement i=(IconBarElement)Left.elementAt(k);
			if (i.getName().equals(name) && i instanceof MultipleIcon)
			{	return ((MultipleIcon)i).getSelected();
			}
		}
		for (k=0; k<Right.size(); k++)
		{	IconBarElement i=(IconBarElement)Right.elementAt(k);
			if (i.getName().equals(name) && i instanceof MultipleIcon)
			{	return ((MultipleIcon)i).getSelected();
			}
		}
		return -1;
	}

	/**
	Set the state of the specified multiple icon
	*/
	public void setMultipleState (String name, int state)
	{	int k;
		for (k=0; k<Left.size(); k++)
		{	IconBarElement i=(IconBarElement)Left.elementAt(k);
			if (i.getName().equals(name) && i instanceof MultipleIcon)
			{	((MultipleIcon)i).setSelected(state);
			}
		}
		for (k=0; k<Right.size(); k++)
		{	IconBarElement i=(IconBarElement)Right.elementAt(k);
			if (i.getName().equals(name) && i instanceof MultipleIcon)
			{	((MultipleIcon)i).setSelected(state);
			}
		}
	}

	/**
	See, if the specific icon has been set.
	*/
	public boolean isSet (String name)
	{	PrimitiveIcon icon=find(name);
		if (icon==null) return false;
		return icon.isSet();
	}
	
	/**
	Set the specific icon to unset.
	*/
	public void unset (String name)
	{	PrimitiveIcon icon=find(name);
		if (icon!=null) icon.unset();
	}
	
	public void getKey (KeyEvent e)
	{	processKeyEvent(e);
	}
}
