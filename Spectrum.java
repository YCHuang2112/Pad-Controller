import java.awt.*;
import javax.swing.*;
import java.awt.Color;
import java.io.*;
import java.util.*;
import sun.audio.*;
import javax.sound.sampled.*;
import java.awt.event.*; 


public class Spectrum extends PlugIn implements ActionListener
{
	//private WavFile wavFile_Read;
	private Complex[][] value;
	private Complex[][] trans;
	//private double[][] buffer_Read;
	//private final int numberOfChannel;
	//private final long sampleRate;
	//private int sampleReadSuccess;
	private static long period;
	private long counter;
	private final JButton playButton;
	private java.util.Timer timer = new java.util.Timer();
	private TimerTask draw;
	private boolean flag;
	private JLabel show;
	
	
	
	public Spectrum(String fileName,int order)
	{
		super(fileName, "spectrum", order);
		flag = false;
		readWav();
		period = (long)(1024000.0/sampleRate);
		playButton=new JButton("play");
		show = new JLabel("arithmetic progression(0~22050hz)");
		
		draw = new TimerTask()
		{
			@Override
			public void run()
			{
				work();
			}
			
		};
		
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
		}//copy file
		/*
		try
		{
			wavFile_Read = WavFile.openWavFile(new File(fileName_Read+".wav"));			
		}
		catch (Exception e)
		{
			System.err.println(e);
			System.out.println("GG1");
		}
		*/
		
		value = new Complex[numberOfChannel][1024];
		trans = new Complex[numberOfChannel][1024];
		for(int i=0; i<1024; i++)
			for(int c=0; c<numberOfChannel; c++)
				value[c][i] = new Complex(0, 0);
		//numberOfChannel = wavFile_Read.getNumChannels();
		//sampleRate = wavFile_Read.getSampleRate();
		//buffer_Read = new double[numberOfChannel][1024];
		counter = 0;
		
		timer.schedule(draw, 0, period);
		System.out.println(new Date());
		playButton.addActionListener(this);
		add(show, BorderLayout.NORTH);
		add(playButton,BorderLayout.SOUTH);
	}


	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource()==playButton)
		{
			flag = true;
			counter = 0;
			if(super.order!=0)
				playSound(String.format(super.fileName_Read+"_"+(order-1)+".wav"));
			else
				playSound(String.format(super.fileName_Read+".wav"));
		}
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
	
	public void paint(Graphics g)
	{
		if(flag)
		{
			super.paint(g);
			
			if(trans[0]!=null&&trans[1]!=null)
			{
				double t = 0;
				for(int i=0; i<512; i++)
				{
					for(int c=0; c<numberOfChannel; c++)
						t+=trans[c][i].abs();
					t/=numberOfChannel;
					t*=5;
					g.drawLine(20+((getWidth()-40)/512)*i, getHeight()/2, 20+((getWidth()-40)/512)*i, (int)(getHeight()/2-t));
					//System.out.println(t);
					t = 0;
				}
			}
		}
	}
	
	
	@Override
	public void work()
	{
		try
		{
			//sampleReadSuccess = wavFile_Read.readFrames(buffer_Read,1024);
			/*if(counter>=numFrames)
			{
				//wavFile_Read.close();
				System.exit(0);
			}*/
			if(flag)
			{
				for(int i=0; i<1024; i++)
				{
					if(counter+i>=numFrames)
					{
						System.out.println(new Date());
						flag = false;
						return;
						//System.exit(0);
					}
					for(int c=0; c<numberOfChannel; c++)
						value[c][i].set(buffer_Read[c][i+(int)counter], 0);
					//System.out.println(buffer_Read[numberOfChannel*i]);
				}
				counter+=1024;
				
				for(int c=0; c<numberOfChannel; c++)
					trans[c] = FFT.fft(value[c]);
				repaint();
			}
		}
		catch(Exception e)
		{
			System.err.println(e);
			System.out.println("hihihihi");
		}
	}
	
	/*
	public static void main(String args[])
	{
		Spectrum s = new Spectrum(args[0], "Spectrum", 0);
		s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		s.setSize(1024, 1024);
		s.setVisible(true);	
	}*/	
}