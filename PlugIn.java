
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public abstract class PlugIn extends JFrame
{
	protected WavFile wavFile_Read;
	protected WavFile wavFile_Write;

	protected String fileName_Read;
	protected int sampleRate;
	protected int numberOfChannel;
	protected int bitDepth;
	protected long numFrames;
	protected double[][] buffer_Read;
	protected int order;

	protected double volumeSend;//in percent
	protected double panning;//in degree

	PlugIn()
	{
		order=0;
		setSize(300,400);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
	}
	PlugIn(String fileName,String name,int order)
	{
		super(fileName+"-"+name+"-"+order);
		this.order=order;
		assign(fileName);
		setSize(300, 400);
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
	}
	public void assign(double volumeSend, double panning, String fileName_Read)
	{
		this.fileName_Read = fileName_Read;
		this.volumeSend = volumeSend/100;
		this.panning = Math.PI/4*(panning/100);
	}
	public void assign(String fileName_Read)
	{
		this.fileName_Read = fileName_Read;
		//System.out.println(this.fileName_Read);
		this.volumeSend = 100;
		this.panning = 0;
	}
	public void assign(double volumeSend, double panning)
	{
		this.volumeSend = volumeSend/100;
		this.panning = Math.PI/4*(panning/100);
	}
	public void readWav()
	{
		try
		{
			if(order==0)
				wavFile_Read = WavFile.openWavFile(new File(fileName_Read+".wav"));
			else
				wavFile_Read = WavFile.openWavFile(new File(fileName_Read+"_"+(order-1)+".wav"));
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
		sampleRate=(int)wavFile_Read.getSampleRate();    // Samples per second
		numFrames=wavFile_Read.getNumFrames();
		numberOfChannel=wavFile_Read.getNumChannels();
		bitDepth=wavFile_Read.getValidBits();
		buffer_Read = new double[numberOfChannel][(int)(numFrames)];	
		
		try
		{
			wavFile_Read.readFrames(buffer_Read,(int)(numFrames));
			wavFile_Read.close();
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
	}
	public abstract void work();

}