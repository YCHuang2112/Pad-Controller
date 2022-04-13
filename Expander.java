import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
public class Expander extends /*JPanel*/PlugIn implements ActionListener
{

	private double threshold;//in dB
	private double ratio;//in decimal
	private double gain;//in dB
	private boolean peakFlag;//

	private final JButton enterButton;
	private final JTextField tField;//threshold
	private final JTextField rField;//ratio
	private final JTextField gField;//gain
	private final JTextField vField;//volume send
	private final JTextField pField;//pan

	Expander(String fileName,int order)
	{
		super(fileName,"expander",order);
		setLayout(new FlowLayout());
		peakFlag=false;

		tField = new JTextField(20);
		rField = new JTextField(20);
		gField = new JTextField(20);
		vField = new JTextField(20);
		pField = new JTextField(20);
		//fileNameField=new JTextField(20);
		enterButton=new JButton("expand");

		add(tField);
		add(new JLabel("Threshold in dB"));
		add(rField);
		add(new JLabel("Ratio in decimal"));
		add(gField);
		add(new JLabel("Gain in dB"));
		add(vField);
		add(new JLabel("Volume sending in percent"));
		add(pField);
		add(new JLabel("Panning in percent, -100 is left, 100 is right"));
		//add(fileNameField);
		//add(new JLabel("File name exclusive .wav"));
		add(enterButton);

		enterButton.addActionListener(this);
	}
	public void assignAndConvert(double threshold,double ratio,double gain,double volumeSend,double panning)
	{
		super.assign(volumeSend,panning);
		this.threshold=Math.pow(10,threshold/20.);
		this.ratio=1/ratio;
		this.gain=Math.pow(10,gain/20.);

		work();
	}
	@Override
	public void work()
	{
		super.readWav();
		try
		{
			wavFile_Write= WavFile.newWavFile(new File(fileName_Read+"_"+order+".wav"),numberOfChannel,numFrames,bitDepth,sampleRate);

			double [][]buffer_Write = new double[numberOfChannel][(int)(numFrames)];
			for (int s=0;s<numFrames;s++)
			{
				for(int i=0;i<numberOfChannel;i++)
					if(Math.abs(buffer_Read[i][s]*volumeSend)<threshold)
						buffer_Write[i][s] = gain*buffer_Read[i][s]*volumeSend*ratio;
					else
						buffer_Write[i][s]=gain*buffer_Read[i][s];
			}
			for (int s=0;s<numFrames;s++)
			{
				buffer_Write[1][s]*=(Math.cos(panning)+Math.sin(panning));
				buffer_Write[0][s]*=(Math.cos(panning)-Math.sin(panning));
			}
				wavFile_Write.writeFrames(buffer_Write,(int)numFrames);
				wavFile_Write.close();// Close the wavFile
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
	}
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource()==enterButton)
			if(Double.parseDouble(tField.getText())>0)
				JOptionPane.showMessageDialog(null,"Threshold must be negative\n");
			else
				if(Double.parseDouble(rField.getText())<1||Double.parseDouble(rField.getText())>30)
					JOptionPane.showMessageDialog(null,"Ratio must be between 1 and 30\n");
				else
					if(Double.parseDouble(vField.getText())>100||Double.parseDouble(vField.getText())<0)
						JOptionPane.showMessageDialog(null,"Volume sending must be between 0 and 100\n");
					else
						if(Double.parseDouble(pField.getText())>100||Double.parseDouble(pField.getText())<-100)
							JOptionPane.showMessageDialog(null,"Panning must be between 0 and 100\n");
						else
							assignAndConvert(Double.parseDouble(tField.getText()),Double.parseDouble(rField.getText()),
								Double.parseDouble(gField.getText()),Double.parseDouble(vField.getText()),
								Double.parseDouble(pField.getText()));
	}
}