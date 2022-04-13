import java.awt.*;
import javax.swing.*;

public class Test_Pad
{

   	public static void main(String[] args)
	{
		int n[]=new int [PadController.numberOfKey+PadController.numberOfMouse];
        for(int i=0;i<n.length;i++)
			n[i]=-1;
		PadController pad=new PadController(n);
		
        JFrame frame = new JFrame("Pad");
		frame.add(pad);
		//frame.add(dB,BorderLayout.EAST);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1920+frame.getInsets().left+frame.getInsets().right
			,1080+frame.getInsets().top+frame.getInsets().bottom);
        frame.setVisible(true);
	   
   	}
}