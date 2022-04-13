import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.Objects;

public class BandPassFilter extends PlugIn implements ActionListener
{
	JButton bandPass;
	JLabel vl, pl, ll, ldl, hl, hdl;
	JTextField vt, pt, lt, ldt, ht, hdt;
	private int lf, hf;
	GridLayout glayout = new GridLayout(0,2);
	//private WavFile wavFile_Read;
	//private long numFrames;
	//private int numberOfChannel;
	//private long sampleRate;
	//private double[][] buffer_Read;
	
	public BandPassFilter(String fileName,int order)
	{
		super(fileName,"band pass filter",order);
		
		JPanel p = new JPanel();
		p.setLayout(glayout);

		vl = new JLabel("volume send:");
		p.add(vl);
		vt = new JTextField(10);
		p.add(vt);
		
		pl = new JLabel("panning");
		p.add(pl);
		pt = new JTextField(10);
		p.add(pt);		
			
		ll = new JLabel("low frequency(-1 to cancel this effect)");
		p.add(ll);
		lt = new JTextField(10);
		p.add(lt);
		
		ldl = new JLabel("low decay rate(input*20dB)");
		p.add(ldl);
		ldt = new JTextField(10);
		p.add(ldt);
		
		hl = new JLabel("high frequency(-1 to cancel this effect)");
		p.add(hl);
		ht = new JTextField(10);
		p.add(ht);

		hdl = new JLabel("high decay rate(input*20dB)");
		p.add(hdl);
		hdt = new JTextField(10);
		p.add(hdt);
		
		bandPass = new JButton("bandPass");
		bandPass.addActionListener(this);
		
		glayout.setHgap(20);
		glayout.setVgap(20);
		glayout.layoutContainer(p);
		
		this.add(p, BorderLayout.NORTH);
		this.add(bandPass, BorderLayout.SOUTH);
		setVisible(true);
	}
	
	@Override
	public void work()
	{
		try
		{
			//if(lt.getText()!="-1")
			lf = Integer.parseInt(lt.getText());
			//if(ht.getText()!="-1")
			hf = Integer.parseInt(ht.getText());
			if(lf>22050||hf>22050)
			{
				bandPass.setText("high frequency or low frequency cannot exceed 22050hz, please try again");
				return;
			}
			else if(lf>hf&&(lf!=-1)&&(hf!=-1))
			{
				bandPass.setText("high frequency cannot lower than low frequency, please try again");
				return;
			}
			else if((lf==-1)&&(hf==-1))
				return;
			bandPass.setText("please wait...");
			
			assign(Double.parseDouble(vt.getText()), Double.parseDouble(pt.getText()));
			readWav();
			
			
			
			//wavFile_Read = WavFile.openWavFile(new File(song.getText()));
			//sampleRate = wavFile_Read.getSampleRate();
			//numberOfChannel = wavFile_Read.getNumChannels();
			//numFrames = wavFile_Read.getNumFrames();
			wavFile_Write = WavFile.newWavFile(new File(fileName_Read+"_"+order+".wav"), numberOfChannel, numFrames, bitDepth, sampleRate);
			System.out.println(numFrames);

			//double[][] buffer_Read = new double[numberOfChannel][(int)numFrames];
			double[][] newBuffer = new double[numberOfChannel][2048];
			//wavFile_Read.readFrames(buffer_Read, (int)numFrames);
			Complex[][] process = new Complex[numberOfChannel][4096];
			long counter = 0;
			
			for(int i=0; i<(numFrames/2048)+1; i++)
			{
				for(int j=0; j<4096; j++)
				{
					for(int c=0; c<numberOfChannel; c++)
					{
						if((j+counter-1024<0)||(j+counter-1024>=numFrames))
						{	
							process[c][j] = new Complex(0, 0);
							//if((j+counter-1024>=numFrames)&&(j<3072))
						}
						else
							process[c][j] = new Complex(buffer_Read[c][j+(int)counter-1024], 0.0);
						process[c][j] = process[c][j].times(new Complex(Math.sin((j/4096.0)*Math.PI), 0.0));
					}
				}
				

				
				
				
				
				
				
				
				
				
				
				/*
				if(i==0)
				{
					for(int i=0; i<1024; i++)//give initial zero to process window f
						process[i] = new Complex(0, 0);
					
					int end; //avoid problems on too short audio file
					numFrames > 3072 ? end = 4096 : end = 1024+numFrames;
					
					for(int i=1024; i<end; i++)
						process[i] = new Complex(buffer_Read[0][i-1024], 0);//only access one channel data, may alter the code in this line
					
					for(int i =end; i<4096; i++)
						process[i] = new Complex(0, 0);
					//else if((lt.getText()!="")&&(ht.getText()!=""))
					counter = 2048;
				}
				else if(i==(numFrames/2048))
				{
					long r = (numFrames%2048);
					for(int i=0; i<1024+r; i++)
						process[i] = new Complex(buffer_Read[0][i+counter-1024], 0);
					for(int i=1024+r; i<4096; i++)
						process[i] = new Complex(0, 0);
					counter+=r;
				}
				else 
				{
					for(int i=0; i<4096; i++)
						process[i] = new Complex(buffer_Read[0][i+counter-1024], 0);
					counter+=2048;
				}
				for(int i=0; i<4096; i++)//window f
					process[i] = process[i].times(Complex(Math.sin((i/4096.0)*Math.PI), 0));
				*/
				for(int c=0; c<numberOfChannel; c++)
				{
					process[c] = FFT.fft(process[c]);
					if(lf!=-1)//high pass
					{
						double lowpow = Double.parseDouble(ldt.getText());
						//System.out.println("0000");
						int bound = (int)(((double)lf/sampleRate)*4096);
						System.out.println(bound);
						for(int j=0; j<bound; j++)
						{
							process[c][j] = process[c][j].times(new Complex(Math.pow((double)j/bound, lowpow), 0.0));
							process[c][4095-j] = process[c][j];
							/*
							double t = Complex(0, 2*Math.PI*(i/4096.0)*sampleRate).abs()/Complex(2*Math.PI*((double)lf/sampleRate)*sampleRate, 2*Math.PI*(i/4096.0)*sampleRate).abs();
							process[i].times(Complex(t, 0));
							process[4096-i].times(Complex(t, 0));
							//process[4096-i]*=Complex(0, 2*Math.PI*(i/4096.)).abs()/Complex(2*Math.PI*((lf%360.)/360.), 2*Math.PI*(i/4096.)).abs();
							*/
						}
					}
					if(hf!=-1)//low pass
					{
						double highpow = Double.parseDouble(hdt.getText());
						//System.out.println("1111");
						int bound = (int)(((double)hf/sampleRate)*4096);
						for(int j=bound; j<2048; j++)
						{
							process[c][j] = process[c][j].times(new Complex(Math.pow((double)bound/j, highpow), 0.0));
							process[c][4095-j] = process[c][j];
							/*
							double t = Complex(2*Math.PI*((double)hf/sampleRate)*sampleRate, 0).abs()/Complex(2*Math.PI*((double)hf/sampleRate)*sampleRate, 2*Math.PI*(i/4096.0)*sampleRate).abs();
							process[i].times(Complex(t, 0));
							process[4096-i].times(Complex(t, 0));
							*/
						}
						//System.out.println("2222");
					}
					
					
					process[c] = FFT.ifft(process[c]);
					double v = Double.parseDouble(vt.getText())/100;
					int t;
					for(int j=1024; j<3072; j++)//window f
					{
						t = j+(int)counter-1024;
						process[c][j] = process[c][j].divides(new Complex(Math.sin((j/4096.0)*Math.PI), 0.0));
						if(t<numFrames)
							newBuffer[c][j-1024] = (v*process[c][j].re() + (1.0-v)*buffer_Read[c][t]);
						else
							break;
						if(c==0)
							newBuffer[c][j-1024]*=(Math.cos(panning)+Math.sin(panning));
						else
							newBuffer[c][j-1024]*=(Math.cos(panning)-Math.sin(panning));
						//System.out.println(j+(int)counter-1024);
					}
					//newBuffer[c][j-1024] = process[c][j].divides(new Complex(Math.sin((j/4096.0)*Math.PI), 0.0)).re();
				}		
				if(i==numFrames/2048)
				{
					System.out.println("times:"+i+"   remain:"+(numFrames+2048-counter));
					wavFile_Write.writeFrames(newBuffer, (int)(numFrames+2048-counter));
				}
				else
					wavFile_Write.writeFrames(newBuffer, 2048);
				
				counter+=2048;
			}
			counter = 0;
			
			wavFile_Write.close();
			bandPass.setText("finished!");
			
			
			
			//vt.setText("");
			//pt.setText("");			
			//lt.setText("");
			//ht.setText("");
			lf = hf = 0;
		}
		catch (Exception e)
		{
			System.err.println(e);
		}				
	}
	
	@Override
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource()== bandPass)
			work();
	}
	
	
	/*public static void main(String args[])
	{
		BandPassFilter bpf = new BandPassFilter();
		bpf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		bpf.setVisible(true);
	}*/
}