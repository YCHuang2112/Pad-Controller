import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.ImageIcon;
import java.util.Timer;
import java.util.TimerTask;

import java.io.*;
import sun.audio.*;
import javax.sound.sampled.*;

public class TouchPad extends JPanel implements MouseListener, MouseMotionListener
{
	//private int sleepTime;
	private int x,newx;//新舊的滑鼠轉移座標
    //private Timer time;
	static int numberOfKey=8;//全部有效按鍵數
	static String waveFileName[]=
		{"pluck_C.wav","pluck_D.wav","pluck_D#.wav",
		"pluck_F.wav","pluck_G.wav","pluck_G#.wav","pluck_A#.wav","pluck_C6.wav"};//音檔名稱

	public TouchPad()
	{
		//position=new boolean[numberOfKey];

		setBackground(Color.RED);
		
		setFocusable(true);
		requestFocusInWindow();
		//setSize(1000,500);
	}
	private void playSound(final String url)
	{
		try
		{
			InputStream in=getClass().getResourceAsStream(url);
			AudioStream audio=new AudioStream(in);
			AudioPlayer.player.start(audio);
		}
		catch(Exception e)
		{
			//if(debugFileWriter!=null)
				//e.printStackTrace(debugFileWriter);
		}
	}
	@Override
	public void mouseClicked(MouseEvent e)
	{
		
		//System.out.printf("%d%n",e.getX()/(getWidth()/numberOfKey));
	}
	@Override
	public void mouseReleased(MouseEvent e)
	{
	}
	@Override
	public void mousePressed(MouseEvent e)
	{
		x=e.getX()/(getWidth()/numberOfKey);
		playSound(waveFileName[x]);
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
		newx=e.getX()/(getWidth()/numberOfKey);
		if(newx!=x&&e.getX()>=0&&e.getX()<=getWidth())//換新按件
		{
			playSound(waveFileName[newx]);
			x=newx;//更新按鍵
		}
	}
	@Override
	public void mouseMoved(MouseEvent e)
	{
		
	}
    public void paintComponent(Graphics g)
	{
		//g.setColor(Color.WHITE);
		//g.fillRoundRect(getWidth()/7*2+getWidth()/28,getHeight()/3*2,getWidth()/2,getHeight()/6,getWidth()/42,getHeight()/18);
    }
	
}