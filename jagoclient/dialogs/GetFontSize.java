package jagoclient.dialogs;

import java.awt.*;

import jagoclient.gui.*;
import jagoclient.Global;

/**
A canvas to display a sample of the chosen font.
The samples is drawn from the GetFontSize dialog.
*/

class ExampleCanvas extends Canvas
{   GetFontSize GFS;
    public ExampleCanvas (GetFontSize gfs)
    {   GFS=gfs;
    }
    public void paint (Graphics g)
    {   GFS.example(g,getSize().width,getSize().height);
    }
    public Dimension getPreferredSize ()
    {	return new Dimension(200,100);
    }
}

/**
A dialog to get the font size of the fixed font and its name.
Both items are stored as a global Parameter. The modal flag
is handled as in the Question dialg.
@see Question
*/

public class GetFontSize extends CloseDialog
{	String FontTag,SizeTag;
	TextField FontName;
	IntField FontSize;
	Choice Fonts,Mode;
	Canvas Example;
	String E=Global.resourceString("10_good_letters__A_J_");
	/**
	@param fonttag,fontdef the font name resource tag and its default value
	@param sizetag,sizedef the font size resource tag and its default value
	*/
	public GetFontSize (String fonttag, String fontdef,
		String sizetag, int sizedef, boolean flag)
	{	super(Global.frame(),Global.resourceString("Font_Size"),flag);
		setLayout(new BorderLayout());
		Panel p=new MyPanel();
		p.setLayout(new GridLayout(0,2));
		p.add(new MyLabel(Global.resourceString("Font_name")));
		p.add(FontName=new TextFieldAction(this,"FontName"));
		FontName.setText(""+Global.getParameter(fonttag,fontdef));
		p.add(new MyLabel(Global.resourceString("Available_Fonts")));
		p.add(Fonts=new ChoiceAction(this,Global.resourceString("Fonts")));
		String[] fonts=Toolkit.getDefaultToolkit().getFontList();
		if (fonts!=null)
		{	for (int i=0; i<fonts.length; i++)
			{   Fonts.add(fonts[i]);
			}
		}
		else
		{	Fonts.add("Dialog");
			Fonts.add("SansSerif");
			Fonts.add("Serif");
			Fonts.add("Monospaced");
			Fonts.add("DialogInput");
		}
		Fonts.add("Courier");
		Fonts.add("TimesRoman");
		Fonts.add("Helvetica");
		Fonts.select(FontName.getText());
		p.add(new MyLabel(Global.resourceString("Mode")));
		p.add(Mode=new ChoiceAction(this,Global.resourceString("Mode")));
		Mode.add(Global.resourceString("Plain"));
		Mode.add(Global.resourceString("Bold"));
		Mode.add(Global.resourceString("Italic"));
		String name=FontName.getText();
	    if (name.startsWith("Bold"))
	    {   FontName.setText(name.substring(4)); Mode.select(1);
	    }
	    else if (name.startsWith("Italic"))
	    {   FontName.setText(name.substring(5)); Mode.select(2);
	    }
	    else Mode.select(0);
		p.add(new MyLabel(Global.resourceString("Font_size")));
		p.add(FontSize=new IntField(this,"FontSize",
			Global.getParameter(sizetag,sizedef)));
		add("North",p);
		Example=new ExampleCanvas(this);
		add("Center",Example);
		Panel bp=new MyPanel();
		bp.add(new ButtonAction(this,Global.resourceString("OK")));
		add("South",bp);
		FontTag=fonttag; SizeTag=sizetag;
		Global.setpacked(this,"getfontsize",200,150);
		validate();
	}
	public void doAction (String o)
	{	Global.notewindow(this,"getfontsize");
    	if (Global.resourceString("OK").equals(o))
	    {   String s=FontName.getText();
    		if (mode()==Font.BOLD) s="Bold"+s;
    		else if (mode()==Font.ITALIC) s="Italic"+s;
	    	Global.setParameter(FontTag,s);
		    Global.setParameter(SizeTag,FontSize.value(3,50));
		    Global.createfonts();
    		setVisible(false); dispose();
    		tell();
    	}
		else super.doAction(o);
        Example.repaint();
	}
	public void itemAction (String s, boolean flag)
	{   FontName.setText(Fonts.getSelectedItem());
	    Example.repaint();
	}
	int mode ()
	{   if (Mode.getSelectedItem().equals(Global.resourceString("Bold")))
	        return Font.BOLD;
	    else if (Mode.getSelectedItem().equals(Global.resourceString("Italic")))
	        return Font.ITALIC;
	    else return Font.PLAIN;
	}
	public void example (Graphics g, int w, int h)
	{   Font f=new Font(FontName.getText(),mode(),FontSize.value(3,50));
	    g.setFont(f);
	    FontMetrics fm=g.getFontMetrics();
	    int wi=fm.stringWidth(E);
        g.drawString(E,(w-wi)/2,(h-fm.getAscent())/2-1);
	}
	/** to be overwritten in the non-modal case */
	public void tell ()
	{
	}
}
