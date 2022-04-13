import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Dimension;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.ImageIcon;
import javax.swing.SpringLayout;
import java.util.List;
import java.util.concurrent.*;

import java.io.*;
import sun.audio.*;
import javax.sound.sampled.*;

public class PadController extends JPanel implements MouseListener, MouseMotionListener
{
	private int[] plugin_num;
	private boolean []key;//紀錄案件有無放開
	static int numberOfKey=20;//全部有效按鍵數
	static int numberOfMouse=36;//滑鼠有效事件數
	static final String waveFileName[]=
		{	//按鍵音檔
			"C","Dm","Em","F","G","Am","Bdim",
			"Kick","Tom_low","Tom_mid","Tom_High","Snare","Crash","Ride","DrumPattern",
			"noise_G4","noise_D#6","noise_E7","noise_F#7","noise_C10",
			//滑鼠音檔
			"25pluse_C5","25pluse_C#5","25pluse_D5","25pluse_D#5","25pluse_E5","25pluse_F5",
			"25pluse_F#5","25pluse_G#5","25pluse_G#5","25pluse_A5","25pluse_A#5","25pluse_B5",
			"25pluse_C6","25pluse_C#6","25pluse_D6","25pluse_D#6","25pluse_E6","25pluse_F6",
			"25pluse_F#6","25pluse_G#6","25pluse_G#6","25pluse_A6","25pluse_A#6","25pluse_B6",
			"25pluse_C7","25pluse_C5","25pluse_C5","25pluse_C5","25pluse_C5","25pluse_C5",
			"25pluse_C5","25pluse_C5","25pluse_C5","25pluse_C5","25pluse_C5","25pluse_C5"
		};//音檔名稱
	private final char []keychar;//有效按鍵指定
	//private String currentFileName;//目前檔案名稱
	
	//private int sleepTime;
	private int x,newx,y,newy;//新舊的滑鼠轉移座標
    //private Timer time;

	//private PlugInPanel plugInPanel; 
	
	//private ExecutorService executorService;
	MixingPanel mixingPanel=new MixingPanel();

	public PadController(int[] volume)
	{ 
		plugin_num = volume;
		setLayout(new GridLayout(1,2));
		key=new boolean [numberOfKey];
		keychar=new char [numberOfKey];
		//currentPlugIn = null;
		
		//executorService=Executors.newCachedThreadPool();
		//plugInPanel=new PlugInPanel();

		keychar[0]='1';
		keychar[1]='2';
		keychar[2]='3';
		keychar[3]='4';
		keychar[4]='5';
		keychar[5]='6';
		keychar[6]='7';

		keychar[7]='X';
		keychar[8]='D';
		keychar[9]='C';
		keychar[10]='F';
		keychar[11]='V';
		keychar[12]='G';
		keychar[13]='H';
		keychar[14]='P';
		keychar[15]='Q';
		keychar[16]='W';
		keychar[17]='E';
		keychar[18]='R';
		keychar[19]='T';
		//keychar[15]='o';

		//executorService.execute(dB);

		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("1"), "forward"+"1");
		getActionMap().put
		("forward"+"1", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("C.wav",0);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("2"), "forward"+"2");
		getActionMap().put
		("forward"+"2", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Dm.wav",1);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("3"), "forward"+"3");
		getActionMap().put
		("forward"+"3", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Em.wav",2);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("4"), "forward"+"4");
		getActionMap().put
		("forward"+"4", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("F.wav",3);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("5"), "forward"+"5");
		getActionMap().put
		("forward"+"5", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("G.wav",4);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("6"), "forward"+"6");
		getActionMap().put
		("forward"+"6", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Am.wav",5);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("7"), "forward"+"7");
		getActionMap().put
		("forward"+"7", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Bdim.wav",6);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("X"), "forward"+"X");
		getActionMap().put
		("forward"+"X", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Kick.wav",7);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"), "forward"+"D");
		getActionMap().put
		("forward"+"D", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Tom_low.wav",8);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("C"), "forward"+"C");
		getActionMap().put
		("forward"+"C", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Tom_mid.wav",9);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F"), "forward"+"F");
		getActionMap().put
		("forward"+"F", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Tom_High.wav",10);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("V"), "forward"+"V");
		getActionMap().put
		("forward"+"V", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Snare.wav",11);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("G"), "forward"+"G");
		getActionMap().put
		("forward"+"G", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Crash.wav",12);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("H"), "forward"+"H");
		getActionMap().put
		("forward"+"H", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Ride.wav",13);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("P"), "forward"+"P");
		getActionMap().put
		("forward"+"P", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("DrumPattern.wav",14);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Q"), "forward"+"Q");
		getActionMap().put
		("forward"+"Q", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("noise_G4.wav",15);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"), "forward"+"W");
		getActionMap().put
		("forward"+"W", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("noise_D#6.wav",16);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("E"), "forward"+"E");
		getActionMap().put
		("forward"+"E", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("noise_E7.wav",17);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("R"), "forward"+"R");
		getActionMap().put
		("forward"+"R", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("noise_F#7.wav",18);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("T"), "forward"+"T");
		getActionMap().put
		("forward"+"T", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("noise_C10.wav",19);
				}
			}
		);
		
		add(new JLabel(""));
		add(mixingPanel);
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	/*@Override
	public void keyPressed(KeyEvent e)
	{
		//System.out.println("DD");
		for(int i=0;i<numberOfKey;i++)
			if((e.getKeyChar()==keychar[i])&&(!key[i]))
			{
				//dB.setValue(waveFileName[i]);
				//System.out.println(waveFileName[i]);
				key[i]=true;//按下後"是否按下"設true
				playSound(waveFileName[i]+".wav");
			}
		repaint();
	}
	@Override
	public void keyReleased(KeyEvent e)
	{
		for(int i=0;i<numberOfKey;i++)
			if(e.getKeyChar()==keychar[i])
				key[i]=false;//放開後"是否按下"設false
		repaint();
	}
	@Override
	public void keyTyped(KeyEvent e)
	{
	}*/
	@Override
	public void mouseClicked(MouseEvent e)
	{	
	}
	@Override
	public void mouseReleased(MouseEvent e)
	{
	}
	@Override
	public void mousePressed(MouseEvent e)
	{
		x=e.getX()/(getWidth()/12);
		y=e.getY()/(getHeight()/3);
		//System.out.println("ASASASA"+(x+y*12+numberOfKey));
		playSound(String.format(waveFileName[x+y*12+numberOfKey]+".wav"),x+y*12+numberOfKey);
	}
	@Override
	public void mouseEntered(MouseEvent e)
	{
		
	}
	@Override
	public void mouseExited(MouseEvent e)
	{
		
	}
	@Override
	public void mouseDragged(MouseEvent e)
	{
		newx=e.getX()/(getWidth()/(12));
		newy=e.getY()/(getHeight()/(3));
		if((newx!=x||newy!=y)&&e.getX()>=0&&e.getX()<=getWidth()&&e.getY()>=0&&e.getY()<=getHeight())//換新按件
		{
			playSound(String.format(waveFileName[newx+newy*12+numberOfKey]+".wav"),newx+newy*12+numberOfKey);
			x=newx;//更新按鍵
			y=newy;
		}
	}
	@Override
	public void mouseMoved(MouseEvent e)
	{
		
	}
	public void playSound(final String url,final int n)
	{
		try
		{
			//currentFileName=url;
			//System.out.println(currentFileName);
			plugin_num[n]=mixingPanel.changeMixingPanel(n);
			InputStream in=getClass().getResourceAsStream(url);
			AudioStream audio=new AudioStream(in);
			//if(!key[15])
				AudioPlayer.player.start(audio);
		}
		catch(Exception e)
		{
			//if(debugFileWriter!=null)
				//e.printStackTrace(debugFileWriter);
		}
	}
    public void paintComponent(Graphics g)
	{
		int paintingWidth=(int)(getWidth()/2);//paint用的width
		for(int i=0;i<numberOfKey;i++)
		{
			if(key[i])
				g.setColor(Color.BLACK);//按下變黑鍵
			else
				g.setColor(Color.WHITE);//放開變白鍵
			g.fillRoundRect(paintingWidth/7*(i%7)+paintingWidth/28,getHeight()/3*(i/7),paintingWidth/14,getHeight()/6,paintingWidth/42,getHeight()/18);
		}
		//g.setColor(Color.WHITE);
		//g.fillRoundRect(getWidth()/7*2+getWidth()/28,getHeight()/3*2,getWidth()/2,getHeight()/6,getWidth()/42,getHeight()/18);
    	}
}