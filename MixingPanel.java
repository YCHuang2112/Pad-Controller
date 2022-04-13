import java.awt.*;
import javax.swing.*;

public class MixingPanel extends JPanel
{
	//PadController padController=new PadController();
	//TouchPad touchPad=new TouchPad();
	PlugInPanel plugInPanel[]=new PlugInPanel[PadController.numberOfKey+PadController.numberOfMouse];
	JPanel plugP=new JPanel();
	CardLayout center_panel_layout=new CardLayout();
	public MixingPanel()
	{
		for(int i=0;i<PadController.numberOfKey+PadController.numberOfMouse;i++)
			plugInPanel[i]=new PlugInPanel(i);
		
		
		//setLayout(new GridLayout(1,2));
		//add(padController);
		
		plugP.setLayout(center_panel_layout);
		for(int i=0;i<PadController.numberOfKey+PadController.numberOfMouse;i++)
			plugP.add(plugInPanel[i], "card"+i);
		
		center_panel_layout.show(plugP, "card0");

		add(plugP);
		//add(touchPad);
	}
	public int changeMixingPanel(int n)
	{
		center_panel_layout.show(plugP, "card"+n);
		return plugInPanel[n].getCurrentOrder();
	}
}