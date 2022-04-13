import java.io.*;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.event.*; 

import java.io.*;
import sun.audio.*;
import javax.sound.sampled.*;

public class dB_Monitor extends PlugIn implements ActionListener
{
	private WavFile wavFile;
	private double[][] buffer;
	public int framePerSec;//每秒畫格數
	private int samplePerFrame;//每畫格取樣數
	private final JButton playButton;

	private double[][] dB;//分貝，最大0dB
	private int dB_count;//畫完設為-1，計算畫到第幾影格
	
	private Timer timer=new Timer();
	private TimerTask tem;
	
	public dB_Monitor(String fileName,int order)
	{
		super(fileName,"dB monitor",order);
		
		framePerSec = 25;//fps這裡改
		
		playButton=new JButton("play");
		dB_count=-1;//畫完設為-1
		
		tem = new TimerTask()
		{
			@Override
			public void run()
			{
				repaint();
			}
		};
		timer.schedule(tem,0,(int)(1000./framePerSec));
		playButton.addActionListener(this);
		add(playButton,BorderLayout.SOUTH);
		work();
	}
	
	
	public void paint(Graphics g)
	{
		super.paint(g);
		if((dB_count!=-1))
		{
			if(dB_count<dB[0].length)
			{
				g.setColor(Color.BLACK);
				for(int i=0;i<numberOfChannel;i++)
				{
					//System.out.println(dB[i][dB_count]);
					g.fillRect(getWidth()/numberOfChannel*i,getHeight()-(int)((80+dB[i][dB_count])*5),getWidth()/numberOfChannel*(i+1), getHeight());
				}
				dB_count++;
			}
		}
		else
		{
			dB_count=-1;//畫完設為-1
		}
	}
	public void assign(double volumeSend,double panning,String fileName_Read)
	{
		this.fileName_Read=fileName_Read;
		this.volumeSend=100;
		this.panning=0;
	}	
	public void playSound(final String url)
	{
		try
		{
			InputStream in=getClass().getResourceAsStream(url);
			AudioStream audio=new AudioStream(in);
			//if(!key[15])
				AudioPlayer.player.start(audio);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
	@Override
	public void work()
	{
		super.readWav();
				
		samplePerFrame=(int)(numFrames/framePerSec);
		System.out.println(numFrames);
		dB=new double [numberOfChannel][(int)(numFrames/samplePerFrame)];//總影格數，一影格一個dB值，左右分開


		for(int j=0;j<numFrames/samplePerFrame;j++)//j為目前影格
		{
			double []rms=new double[numberOfChannel];

			for(int s=(int)samplePerFrame*j ; s<(int)samplePerFrame*(j+1); s++)//把全部的值作運算
				for(int i=0;i<numberOfChannel;i++)//左右聲道分別算
					rms[i] += buffer_Read[i][s]*buffer_Read[i][s];//累加平方
			for(int i=0;i<numberOfChannel;i++)
			{
				rms[i]/= (int)samplePerFrame;//除以單影格取樣數
				rms[i]=Math.sqrt(rms[i]);//開根號
				dB[i][j] = 20*Math.log10(rms[i]);//轉成dB
						//System.out.println(dB[i][j]);
			}
		}//抓dB值
		try
		{
			wavFile_Write= WavFile.newWavFile(new File(fileName_Read+"_"+order+".wav"),numberOfChannel,numFrames,bitDepth,sampleRate);

			double [][]buffer_Write = new double[numberOfChannel][(int)(numFrames)];
			for (int s=0;s<numFrames;s++)
				for(int i=0;i<numberOfChannel;i++)	
					buffer_Write[i][s] = buffer_Read[i][s];				
			wavFile_Write.writeFrames(buffer_Write,(int)numFrames);
			wavFile_Write.close();// Close the wavFile
		}
		catch (Exception e)
		{
			System.err.println(e);
		}//複製檔案
		
	}
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==playButton)
		{
			dB_count=0;//重新設值
			if(super.order!=0)
				playSound(String.format(super.fileName_Read+"_"+(order-1)+".wav"));
			else
				playSound(String.format(super.fileName_Read+".wav"));
		}
	}
	/*public void run()
	{
		while(true)
		{
			if(dB_count!=-1)//畫完設為-1
				repaint();
			try
			{
				System.out.println(dB_count);
				Thread.sleep((long)(1000./framePerSec));
			}
			catch(InterruptedException exception)
			{
				Thread.currentThread().interrupt();
			}
			
		}
	}*/
}