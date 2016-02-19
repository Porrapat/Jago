package jagoclient.mail;

import java.awt.*;
import java.net.*;

import jagoclient.gui.*;
import jagoclient.dialogs.*;
import jagoclient.Global;

import rene.util.mail.*;

/**
This dialog is used to send mail somewhere. The user must enter
the SMTP host, the address and his own address, the subject
and the message.
*/

public class MailDialog extends CloseDialog implements MailCallback
{	String Message;
	TextArea T;
	TextField From,To,Subject,Host;
	Frame F;
	/**
	@param s the message (may be edited by the user)
	*/
	public MailDialog (Frame f, String s)
	{	super(f,Global.resourceString("Mail_Game"),false);
		Message=s;
		F=f;
		setLayout(new BorderLayout());
		Panel p=new MyPanel();
		p.setLayout(new GridLayout(0,2));
		p.add(new MyLabel("From : "));
		p.add(From=new FormTextField(Global.getParameter("from",Global.resourceString("Your_EMail_Address"))));
		p.add(new MyLabel("To : "));
		p.add(To=new FormTextField(Global.getParameter("to",Global.resourceString("Destination_EMail_Address"))));
		p.add(new MyLabel("Subject : "));
		p.add(Subject=new FormTextField(Global.resourceString("SGF_File")));
		p.add(new MyLabel(Global.resourceString("SMTP_host___")));
		p.add(Host=new GrayTextField(Global.getParameter("smtp","SMTP host")));
		p.add(new MyLabel(Global.resourceString("This_host___")));
		try
		{	p.add(new MyLabel(InetAddress.getByName("localhost").getHostName()));
		}
		catch (Exception e)
		{	p.add(new MyLabel(Global.resourceString("Unknown_host")));
		}
		add("North",p);
		add("Center",T=new TextArea());
		T.setBackground(Global.gray);
		T.setFont(Global.Monospaced);
		T.setText(s);
		Panel bp=new MyPanel();
		bp.add(new ButtonAction(this,Global.resourceString("Send")));
		bp.add(new ButtonAction(this,Global.resourceString("Close")));
		add("South",bp);
		Global.setwindow(this,"mail",400,400);
		validate();
		show();
	}
	public void doAction (String o)
	{	Global.notewindow(this,"mail");
		if (Global.resourceString("Send").equals(o))
		{	Global.setParameter("from",From.getText());
			Global.setParameter("to",To.getText());
			Global.setParameter("smtp",Host.getText());
			SendMail s=new SendMail(Host.getText(),To.getText(),From.getText());
			s.send(Subject.getText(),Message,this);
		}
		else if (Global.resourceString("Close").equals(o))
		{	setVisible(false); dispose();
		}
		else super.doAction(o);
	}
	public void result (boolean flag, String s)
	{	new Message(Global.frame(),s);
	}
}

