package jagoclient.igs.connection;

import java.awt.*;

import jagoclient.gui.*;
import jagoclient.dialogs.*;
import jagoclient.Go;
import jagoclient.Global;

import rene.util.list.*;

public class EditConnection extends CloseDialog
{	ListClass CList;
	Connection C;
	TextField Name, Server, Port, User, Password, Encoding;
	Go G;
	Choice MChoice;
	Frame F;
	public EditConnection (CloseFrame f, ListClass clist, Connection c,
		Go go)
	{	super(f,Global.resourceString("Edit_Connection"),true);
		G=go; F=f;
		CList=clist; C=c;
		Panel p1=new MyPanel();
		p1.setLayout(new GridLayout(0,2));
		p1.add(new MyLabel(Global.resourceString("Name")));
		p1.add(Name=new FormTextField(""+C.Name));
		p1.add(new MyLabel(Global.resourceString("Server")));
		p1.add(Server=new FormTextField(C.Server));
		p1.add(new MyLabel(Global.resourceString("Port__Use_23_for_Telnet_")));
		p1.add(Port=new FormTextField(""+C.Port));
		p1.add(new MyLabel(Global.resourceString("User__empty_for_manual_login_")));
		p1.add(User=new FormTextField(""+C.User));
		p1.add(new MyLabel(Global.resourceString("Password__empty_for_prompt_")));
		p1.add(Password=new FormTextField(""+C.Password));
		p1.add(new MyLabel(Global.resourceString("Move_Style__move__if_unknown_")));
		p1.add(MChoice=new Choice());
		MChoice.setFont(Global.SansSerif);
		MChoice.add(Global.resourceString("move"));
		MChoice.add(Global.resourceString("move_number_time"));
		MChoice.add(Global.resourceString("move_time"));
		switch (C.MoveStyle)
		{   case Connection.MOVE : MChoice.select(Global.resourceString("move")); break;
		    case Connection.MOVE_N_TIME : MChoice.select(Global.resourceString("move_number_time")); break;
		    case Connection.MOVE_TIME : MChoice.select(Global.resourceString("move_time")); break;
		}
		p1.add(new MyLabel(Global.resourceString("Encoding")));
		p1.add(Encoding=new FormTextField(""+C.Encoding));
		add("Center",new Panel3D(p1));
		Password.setEchoChar('*');
		Panel p=new MyPanel();
		p.add(new ButtonAction(this,Global.resourceString("Set")));
		p.add(new ButtonAction(this,Global.resourceString("Add")));
		p.add(new ButtonAction(this,Global.resourceString("Cancel")));
		p.add(new MyLabel(" "));
		p.add(new ButtonAction(this,Global.resourceString("Help")));
		add("South",new Panel3D(p));
		Global.setpacked(this,"edit",300,200,F);
		validate(); show();
		Name.requestFocus();
	}
	public EditConnection (CloseFrame F, ListClass clist,
		Go go)
	{	super(F,Global.resourceString("Edit_Connection"),true);
		G=go;
		CList=clist;
		Panel p1=new MyPanel();
		p1.setLayout(new GridLayout(0,2));
		p1.add(new MyLabel(Global.resourceString("Name")));
		p1.add(Name=new FormTextField(Global.resourceString("Server_shortcut__IGS_")));
		p1.add(new MyLabel(Global.resourceString("Server")));
		p1.add(Server=new FormTextField(Global.resourceString("Server_name__igs_nuri_net_")));
		p1.add(new MyLabel(Global.resourceString("Port__Use_23_for_Telnet_")));
		p1.add(Port=new FormTextField(Global.resourceString("Port__6969_")));
		p1.add(new MyLabel(Global.resourceString("User__empty_for_manual_login_")));
		p1.add(User=new FormTextField(Global.resourceString("User_name__kingkong_"))); p1.add(new
		MyLabel(Global.resourceString("Password__empty_for_prompt_")));
		p1.add(Password=new FormTextField("")); p1.add(new
		MyLabel(Global.resourceString("Move_Style__move__if_unknown_")));
		p1.add(MChoice=new Choice());
		MChoice.setFont(Global.SansSerif);
		MChoice.add(Global.resourceString("move"));
		MChoice.add(Global.resourceString("move_number_time"));
		MChoice.add(Global.resourceString("move_time"));
		MChoice.select(Global.resourceString("move"));
		add("Center",new Panel3D(p1)); p1.add(new
		MyLabel(Global.resourceString("Encoding")));
		p1.add(Encoding=new FormTextField(
			Global.isApplet()?"ASCII":System.getProperty("file.encoding")));
		Password.setEchoChar('*');
		Panel p=new MyPanel();
		p.add(new ButtonAction(this,Global.resourceString("Add")));
		p.add(new ButtonAction(this,Global.resourceString("Cancel")));
		p.add(new MyLabel(" "));
		p.add(new ButtonAction(this,Global.resourceString("Help")));
		add("South",new Panel3D(p));
		Global.setpacked(this,"edit",300,200,F);
		validate(); show();
	}
  	public void doAction (String o)
  	{	Global.notewindow(this,"edit");
  		if (Global.resourceString("Set").equals(o))
  		{	C.Name=Name.getText();
  			C.Server=Server.getText();
  			try
  			{	C.Port=Integer.parseInt(Port.getText());
  			}
  			catch (NumberFormatException ex)
  			{	C.Port=6969;
  			}
  			finally
  			{	C.User=User.getText();
  				C.Password=Password.getText();
  				switch (MChoice.getSelectedIndex())
  				{   case 0 : C.MoveStyle=Connection.MOVE; break;
  				    case 1 : C.MoveStyle=Connection.MOVE_N_TIME; break;
  				    case 2 : C.MoveStyle=Connection.MOVE_TIME; break;
  				}
  				C.Encoding=Encoding.getText();
  				G.updatelist();
  				setVisible(false); dispose();
  			}
  		}
  		else if (Global.resourceString("Add").equals(o))
  		{	Connection C=new Connection("[?] [?] [?] [?] [?] [?]");
  			C.Name=Name.getText();
  			C.Server=Server.getText();
  			try
  			{	C.Port=Integer.parseInt(Port.getText());
  			}
  			catch (NumberFormatException ex)
  			{	C.Port=6969;
  			}
  			finally
  			{	C.User=User.getText();
  				C.Password=Password.getText();
  				switch (MChoice.getSelectedIndex())
  				{   case 0 : C.MoveStyle=Connection.MOVE; break;
  				    case 1 : C.MoveStyle=Connection.MOVE_N_TIME; break;
  				    case 2 : C.MoveStyle=Connection.MOVE_TIME; break;
  				}
  				C.Encoding=Encoding.getText();
  				if (G.find(C.Name)!=null)
  				{	C.Name=C.Name+" DUP";
  				}
  				CList.append(new ListElement(C));
  				G.updatelist();
  				setVisible(false); dispose();
  			}
  		}
  		else if (Global.resourceString("Cancel").equals(o))
  		{	setVisible(false); dispose(); 
  		}
  		else if (Global.resourceString("Help").equals(o))
  		{	new HelpDialog(F,"configure"); 
  		}
		else super.doAction(o);
  	}
}

