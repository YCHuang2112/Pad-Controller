import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.*;
import java.util.ArrayList;
public class PlugInPanel extends JPanel implements ItemListener,ActionListener
{
	private final ArrayList<JComboBox<String>> plugInJComboBox;
	private PlugIn []plugIn;
	private final String []plugInName;
	private final JLabel currentFileLabel;
	private int currentOrder;
	private boolean enterItemListenerFlag=false;
	public PlugInPanel(int n)//n stands the number of wave file
	{
		setLayout(new GridLayout(8,0,30,30));
		currentOrder=    -1;
		
		plugInName=new String [8];
		plugIn=new PlugIn [7];
		plugInJComboBox=new ArrayList<JComboBox<String>>();
		currentFileLabel=new JLabel(PadController.waveFileName[n]);
		
		plugInName[0]="null";
		plugInName[1]="dB monitor";
		plugInName[2]="compressor";
		plugInName[3]="limiter";
		plugInName[4]="band pass filter";
		plugInName[5]="expander";
		plugInName[6]="gate";
		plugInName[7]="spectrum";

		add(currentFileLabel);
		
		add(new JLabel(""));
		
		for(int i=0;i<7;i++)
		{
			plugInJComboBox.add(new JComboBox<String>(plugInName));
			//plugInJComboBox.get(i).setMaximumRowCount(4);
			add(plugInJComboBox.get(i));
			add(new JLabel(""));
			plugInJComboBox.get(i).addItemListener(this);
			plugInJComboBox.get(i).addActionListener(this);
		}
		
		setFocusable(true);
		requestFocusInWindow();
	}
	@Override
	public void actionPerformed(ActionEvent event)
	{
		for(int i=0;i<7;i++)
			if(event.getSource()==plugInJComboBox.get(i))
			{	
				currentOrder=i;
				System.out.println(currentOrder);
				enterItemListenerFlag=true;
			}
	}
	@Override
	public void itemStateChanged(ItemEvent event)
	{
		if(enterItemListenerFlag&&event.getStateChange()==ItemEvent.SELECTED)
		{
			switch(plugInJComboBox.get(currentOrder).getSelectedIndex())
			{
				case 1:
					plugIn[currentOrder]=new dB_Monitor(currentFileLabel.getText(),currentOrder);
					break;
				case 2:
					plugIn[currentOrder]=new Compressor(currentFileLabel.getText(),currentOrder);
					break;
				case 3:
					plugIn[currentOrder]=new Limiter(currentFileLabel.getText(),currentOrder);
					break;
				case 4:
					plugIn[currentOrder]=new BandPassFilter(currentFileLabel.getText(),currentOrder);
					break;
				case 5:
					plugIn[currentOrder]=new Expander(currentFileLabel.getText(),currentOrder);
					break;
				case 6:
					plugIn[currentOrder]=new Gate(currentFileLabel.getText(),currentOrder);
					break;
				case 7:
					plugIn[currentOrder]=new Spectrum(currentFileLabel.getText(),currentOrder);
					break;
				
			}
			enterItemListenerFlag=false;
		}
		
	}
	
	public int getCurrentOrder()
	{
		return currentOrder;
	}
}