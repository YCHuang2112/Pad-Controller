import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class GameStart extends JPanel implements ActionListener
{
	//PlugInPanel plugInPanel[]=new PlugInPanel[PadController.numberOfKey+PadController.numberOfMouse];
	JButton game, setting;
	JLabel title;
	JPanel p, gs;
	private int[] plugin;
	PadController pad_c;
	//CardLayout center_panel_layout;
	
	
	public GameStart()
	{
		//for(int i=0;i<PadController.numberOfKey+PadController.numberOfMouse;i++)
			//plugInPanel[i]=new PlugInPanel(i);
		this.setLayout(new GridBagLayout());
		plugin = new int[PadController.numberOfKey+PadController.numberOfMouse];
		for(int i=0; i<plugin.length; i++)
			plugin[i] = -1;
		//setLayout(new GridLayout(1,2));
		//add(padController);
		title = new JLabel("    Game manu");
		title.setForeground(Color.YELLOW);
		GridBagConstraints c0 = new GridBagConstraints();
		c0.gridx = 1;
		c0.gridy = 0;
		c0.gridwidth = 3;
		c0.gridheight = 1;
		c0.weightx = 0;
		c0.weighty = 0;
		c0.fill = GridBagConstraints.BOTH;
		c0.anchor = GridBagConstraints.CENTER;
		this.add(title, c0);
		
		game = new JButton("Game Start");
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridx = 1;
		c1.gridy = 3;
		c1.gridwidth = 1;
		c1.gridheight = 1;
		c1.weightx = 0;
		c1.weighty = 0;
		c1.fill = GridBagConstraints.BOTH;
		c1.anchor = GridBagConstraints.CENTER;
		this.add(game, c1);
		
		setting = new JButton("setting");
		GridBagConstraints c2 = new GridBagConstraints();
		c2.gridx = 1;
		c2.gridy = 5;
		c2.gridwidth = 1;
		c2.gridheight = 1;
		c2.weightx = 0;
		c2.weighty = 0;
		c2.fill = GridBagConstraints.BOTH;
		c2.anchor = GridBagConstraints.CENTER;
		this.add(setting, c2);
		/*p = new JPanel();
		gs = new JPanel();*/
		
		//CardLayout center_panel_layout = new CardLayout();	
		
		//p.setLayout(center_panel_layout);

		
		//for(int i=0;i<PadController.numberOfKey+PadController.numberOfMouse;i++)
			//plugP.add(plugInPanel[i], "card"+i);
		
		//center_panel_layout.show(p, "card0");
		game.addActionListener(this);
		setting.addActionListener(this);
		//add(p);
		//add(touchPad);		
	}

	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==game)
		{
			JFrame g_s = new JFrame("Game");
			P_2 p_2 = new P_2(plugin);
			g_s.add(p_2);
			g_s.setSize(600, 600);
			g_s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			g_s.setVisible(true);
		}
		else if(e.getSource()==setting)
		{
			JFrame f_s = new JFrame("Setting");
			PadController pad_c = new PadController(plugin);
			f_s.add(pad_c);
			f_s.setSize(1920, 1080);
			f_s.setVisible(true);
		}
	}


	public static void main(String[] args)
	{
		GameStart gs = new GameStart();
		JFrame f  =new JFrame("Game manu");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gs.setBackground(Color.BLACK);
		f.add(gs);
		f.setSize(400, 600);
		f.setResizable(false);
		f.setVisible(true);
	}
}