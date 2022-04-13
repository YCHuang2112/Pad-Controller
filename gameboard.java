import java.awt.*;
import javax.swing.*;

public class gameboard{

   public static void main(String[] args){
   
		int n[]=new int [PadController.numberOfKey+PadController.numberOfMouse];
		for(int i=0;i<n.length;i++)
			n[i]=-1;
        P_2 panel = new P_2(n);
		
        JFrame frame = new JFrame("music beats");
	    frame.add(panel); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,600);
        frame.setVisible(true);
	   
   }
}