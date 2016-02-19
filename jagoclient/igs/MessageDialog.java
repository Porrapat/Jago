package jagoclient.igs;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

import jagoclient.gui.*;
import jagoclient.Global;

import rene.util.parser.*;

/**
This dialog may be opened by the MessageDistributor.
*/

class MessageDialog extends CloseFrame 
	implements CloseListener,KeyListener
{	PrintWriter Out;
	TextField Answer;
	TextArea T;
	ConnectionFrame CF;
	MessageDistributor MDis;
	Choice UserChoice;
	public MessageDialog (ConnectionFrame cf, String user, String m,
		PrintWriter out, MessageDistributor mdis)
	{	super(Global.resourceString("_Message_"));
		seticon("iwho.gif");
		cf.addCloseListener(this);
		CF=cf; MDis=mdis;
		//CF.append("From "+user+": "+m);
		Panel pm=new MyPanel();
		pm.setLayout(new BorderLayout());
		pm.add("Center",T=new TextArea("",0,0,TextArea.SCROLLBARS_VERTICAL_ONLY));
		T.setBackground(Global.gray.brighter());
		T.setFont(Global.Monospaced);
		T.setEditable(false);
		UserChoice=new Choice();
		UserChoice.setFont(Global.SansSerif);
		UserChoice.add(user);
		Answer=new HistoryTextField(this,Global.resourceString("Answer"));
		Answer.addKeyListener(new KeyAdapter ()
			{	public void keyReleased (KeyEvent e)
				{	String s=Global.getFunctionKey(e.getKeyCode());
					if (s.equals("")) return;
					T.setText(s); 
				}
			}
		);
		Panel AnswerPanel=new SimplePanel(UserChoice,1e-1,Answer,9e-1);
		pm.add("South",AnswerPanel);
		add("Center",pm);
		Panel p=new MyPanel();
		p.add(new ButtonAction(this,Global.resourceString("Close")));
		p.add(new ButtonAction(this,Global.resourceString("Answer")));
		p.add(new ButtonAction(this,Global.resourceString("Send_as_Command")));
		add("South",new Panel3D(p));
		Out=out;
		MDis.MD=this;
		Global.setwindow(this,"messagedialog",400,200);
		validate();
		show();
		T.setText("<<<< "+user+"\n");
		T.append(m);
		Answer.addKeyListener(this);
	}
	public void doAction (String o)
	{	Global.notewindow(this,"messagedialog");	
		if (Global.resourceString("Close").equals(o))
		{	doclose();
		}
		else if (Global.resourceString("Answer").equals(o))
		{	String User=UserChoice.getSelectedItem();
			if (User!=null && !Answer.getText().equals(""))
			{	Out.println("tell "+User+" "+Answer.getText());
				CF.append(Global.resourceString("Answer_")+User+": "+Answer.getText());
				T.append("\n"+Global.resourceString("_____Answer_to_")+User+"\n");
				T.append(Answer.getText());
				Answer.setText("");
			}
		}
		else if (Global.resourceString("Send_as_Command").equals(o))
		{	if (!Answer.getText().equals(""))
			{	CF.command(Answer.getText());
				Answer.setText("");
			}
		}
		else super.doAction(o);
	}
	public void windowOpened (WindowEvent e)
	{	Answer.requestFocus();
	}
	public boolean close ()
	{	return true;
	}
	public void paint (Graphics g)
	{	if (MDis.MD==null)
		{	doclose(); return;
		}
		super.paint(g);
	}
	public void append (String user, String s)
	{	T.append("\n<<<< "+user+"\n"+s);
		int n=UserChoice.getItemCount();
		String a=CF.reply();
		if (!a.equals(""))
		{	T.append("\n"+Global.resourceString("_____Auto_reply_to_")+user+"\n");
			T.append(a);
			CF.append(Global.resourceString("Auto_reply_sent_to_")+user);
			Out.println("tell "+user+" "+a);
		}
		for (int i=0; i<n; i++)
		{	if (UserChoice.getItem(i).equals(user)) return;
		}
		UserChoice.add(user);
	}
	public void isClosed ()
	{	doclose();
	}
	public void doclose ()
	{	MDis.MD=null; CF.removeCloseListener(this);
		super.doclose();
	}
	public void keyPressed (KeyEvent e)
	{	if (e.getKeyCode()==KeyEvent.VK_ESCAPE && close()) doclose();
	}
	public void keyTyped (KeyEvent e) {}
	public void keyReleased (KeyEvent e)
	{	String s=Global.getFunctionKey(e.getKeyCode());
		if (s.equals("")) return;
		Answer.setText(s);
	}
}
