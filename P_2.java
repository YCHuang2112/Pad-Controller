
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.ImageIcon;
import java.util.Timer;
import java.util.TimerTask;
import java.time.*;
import java.security.SecureRandom;
import java.lang.Thread;

import java.io.File;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;

import java.io.*;
import sun.audio.*;
import javax.sound.sampled.*;


public class P_2 extends JPanel  implements KeyListener,MouseListener, MouseMotionListener 
{
	private int []numberOfPlugIn;
	
	private boolean halt = false, unhalt = false, pre_halt = false;
	
	private final Color[] converge_circle_color = {new Color(254,223,225), new Color(0,92,175), new Color(193,105,60), new Color(239,187,36), new Color(220,184,121)};
	private int converge_circle_color_index = 0;
	private final int converge_circle_color_index_max = 5;
	private int converge_circle_color_mode = 2;// mode 1: input color; mode 2: color choose
	
	private hitComponent hit1;
    private int sleepTime = 50;//sleepTime for schedutle 
	private int x,y,index;
	private int xi, yj;
	//factor determine the game time
	private int TIME_SCALE = 1;//determine the game's time unit: TIME_SCALE * (ms)
	private int divide_to_S = 1000 / TIME_SCALE;
	
	
	private int speed_controller = 16;//for speed controlling
	private int SPEED_HEIGHT_DEVIDENT = 800;   // devide gameboard y_axis to ( SPEED_HEIGHT_DEVIDENT ) compartment
	private int hit_time_error_tollerent = 20; // y-axis value
	private int error_accumulation = 0;
	
	public int determination_line_y_coordinate(){return getHeight()*9/10 ;} // this just for hit line y-position
	
	 
	//falling brick
	private  LinkedList<hitComponent> A_key_linkedlist = new LinkedList<hitComponent>();
	private hitComponent present_A_key_hit;
	private  LinkedList<hitComponent> S_key_linkedlist = new LinkedList<hitComponent>();
	private hitComponent present_S_key_hit;
	private  LinkedList<hitComponent> D_key_linkedlist = new LinkedList<hitComponent>();
	private hitComponent present_D_key_hit;
	private  LinkedList<hitComponent> F_key_linkedlist = new LinkedList<hitComponent>();
	private hitComponent present_F_key_hit;
	//out_lowbound_nums
	private int count_out_buond_A_key_hit = 0;
	private int count_out_buond_S_key_hit = 0;
	private int count_out_buond_D_key_hit = 0;
	private int count_out_buond_F_key_hit = 0;
	
	
	/*
		for inserting the new key_linkedlist please modify the 
		followin declaration numbers and go to the 
		method:composition_generate() and modify 
		the delclaration there. Also the add the declaration of:
		1. private int count_out_bound_xxx_key_hit = 0 &
		2. private  LinkedList<hitComponent> xxx_key_linkedlist = 
		   new LinkedList<hitComponent>();
		above
		then, account for new key_listener in:  
		public void keyPressed(KeyEvent event)
	*/
	//***************************************************************************************************************
	private int[] count_out_buond_key_hit = new int[4]; //ask default init to 0
	private final int Key_List_NUM = 4;
	ArrayList< LinkedList<hitComponent> > key_linkedlist_pointer_array = new ArrayList< LinkedList<hitComponent> >();
	//***************************************************************************************************************
	/*
	key_linkedlist_pointer_array.add(A_key_linkedlist);
	key_linkedlist_pointer_array.add(S_key_linkedlist);
	key_linkedlist_pointer_array.add(D_key_linkedlist);
	key_linkedlist_pointer_array.add(F_key_linkedlist);
	*/
	//
	//transparency
	private int hit_transparency = 125;
	//used for choosing corresponding color of hit_component
	private final Color[] Color_Pair = 
	{ 
		new Color(Color.YELLOW.getRed(), Color.YELLOW.getGreen(), Color.YELLOW.getBlue(), hit_transparency),
		new Color(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue(), hit_transparency),     
		new Color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), hit_transparency),
		new Color(Color.MAGENTA.getRed(), Color.MAGENTA.getGreen(), Color.MAGENTA.getBlue(), hit_transparency)
	};
	//convege circle
	//private Converge_circle[] converge_circles;
	private LinkedList<Converge_circle> converge_circles_linkedlist = new LinkedList<Converge_circle>();
	private int CONVERGE_CIRCLE_INNER_RADIUS = 26, CONVERGE_CIRCLE_OUTTER_RADIUS = 30, CONVERGE_CIRCLE_transparency = 150;//transparency for:  outter's, inner circle's == outter + 50 
	private ArrayList<Integer> wait_to_delete_converge_circles = new ArrayList<Integer>();
	
    private Timer time; //just for schedule 
	private Instant PG_time, Catch_time, Halt_time; //responsible for telling the time
	private long time_duration = 0, for_circle_converge_duration;//time duration  former:time for the whole game  latter: used for converge speed
	//begin:
	private int converge_mode = 1;		//mode 1: fasten outter radius  to different converge speed; mode 2 : outter radius propotional to the converge speed
	//mode 1:
	private int CIRCLE_CONVERGE_DURATION_delicate_from_TIME_SCALE = TIME_SCALE; //max:TIME_SCALE min:10
	private int outter_radius_amplifier = 20;// max: CIRCLE_CONVERGE_DURATION_delicate_from_TIME_SCALE   min: 1
	//mode 2:
	private int outter_circle_to_converge_speed_propotion = 100; // max: 1; min: TIME_SCALE
	//end
	private int converge_circle_click_determine_mode = 1; //mode 1: click in inner radius; mode 2: click in outter circle
	
	private long hits, combo;
	private boolean iscombo = false;
	private long points = 0;
	//private Image image;
	//JButton btest;
	//String path;
	//MixingPanel mixingPanel=new MixingPanel();
	
	public P_2(int []numberOfPlugIn)
	{
//		sun.util.logging.PlatformLogger platformLogger = PlatformLogger.getLogger("java.util.prefs");
//platformLogger.setLevel(PlatformLogger.Level.OFF);
		this.numberOfPlugIn=numberOfPlugIn;
		setSize(600,800);
				getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("1"), "forward"+"1");
		getActionMap().put
		("forward"+"1", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("C",0);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("2"), "forward"+"2");
		getActionMap().put
		("forward"+"2", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Dm",1);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("3"), "forward"+"3");
		getActionMap().put
		("forward"+"3", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Em",2);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("4"), "forward"+"4");
		getActionMap().put
		("forward"+"4", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("F",3);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("5"), "forward"+"5");
		getActionMap().put
		("forward"+"5", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("G",4);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("6"), "forward"+"6");
		getActionMap().put
		("forward"+"6", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Am",5);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("7"), "forward"+"7");
		getActionMap().put
		("forward"+"7", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Bdim",6);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("X"), "forward"+"X");
		getActionMap().put
		("forward"+"X", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Kick",7);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("D"), "forward"+"D");
		getActionMap().put
		("forward"+"D", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Tom_low",8);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("C"), "forward"+"C");
		getActionMap().put
		("forward"+"C", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Tom_mid",9);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F"), "forward"+"F");
		getActionMap().put
		("forward"+"F", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Tom_High",10);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("V"), "forward"+"V");
		getActionMap().put
		("forward"+"V", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Snare",11);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("G"), "forward"+"G");
		getActionMap().put
		("forward"+"G", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Crash",12);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("H"), "forward"+"H");
		getActionMap().put
		("forward"+"H", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("Ride",13);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("P"), "forward"+"P");
		getActionMap().put
		("forward"+"P", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("DrumPattern",14);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("Q"), "forward"+"Q");
		getActionMap().put
		("forward"+"Q", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("noise_G4",15);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("W"), "forward"+"W");
		getActionMap().put
		("forward"+"W", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("noise_D#6",16);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("E"), "forward"+"E");
		getActionMap().put
		("forward"+"E", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("noise_E7",17);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("R"), "forward"+"R");
		getActionMap().put
		("forward"+"R", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("noise_F#7",18);
				}
			}
		);
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("T"), "forward"+"T");
		getActionMap().put
		("forward"+"T", new AbstractAction() 
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					playSound("noise_C10.wav");//,19);
				}
			}
		);
		
		
		
	   x = 500; y = 300;
	   xi = 40; yj = 0;
	   setLayout(null);
	   
	   //sleepTime = 50;//sleepTime for schedutle 
	   addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		//add(mixingPanel);
		setVisible(true);
	   this.setFocusable(true);
	   start();
	}
	
	//frame speed control
	private int Speed_control()
	{
		return speed_controller * getHeight()/SPEED_HEIGHT_DEVIDENT;
	}
	
	public static final int NOTE_ON = 0x90;
	public static final int NOTE_OFF = 0x80;
	public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
	//readin ; following is an example
	private int composition_generate() throws Exception
	{
		//declarations
		Graphics g = getGraphics();
		key_linkedlist_pointer_array.add(A_key_linkedlist);
		key_linkedlist_pointer_array.add(S_key_linkedlist);
		key_linkedlist_pointer_array.add(D_key_linkedlist);
		key_linkedlist_pointer_array.add(F_key_linkedlist);
		
		//argument setting
		int pre_time = (/*SPEED_HEIGHT_DEVIDENT * 9/10-*/ determination_line_y_coordinate())  * sleepTime/speed_controller +2000;
		if( ((SPEED_HEIGHT_DEVIDENT * 9/10 - determination_line_y_coordinate())  * sleepTime)%speed_controller != 0)
		{
			System.out.println("self_Warning: In composition_generate, height_d - line_y != k*speed_con.");
			return -1;
		}
		//filein  1. liked_list: hit_time  2. converg_circle: hit_time , duration_time 
	
		/*Choose BPM and the FILE("xxxxx.mid") for the input song  "type of xxx.mid"*/
		Sequence sequence = MidiSystem.getSequence(new File("mario.mid"));
		double BPM = 152;
BPM = 100;//for mario.mid trial
		
		long tick = 0;
		double ticksPerSecond = 0;
		double tick_to_milli = 30; ///*****????????????????default value:30
///*
		float divisionType = 0;

		if( (divisionType = sequence.getDivisionType()) == Sequence.PPQ)
		{
			ticksPerSecond =  ((double)sequence.getResolution()) * (BPM/ 60.0); //currentTempoInBeatsPerMinute / 60.0
		}

/*
		else 
		{
			double framesPerSecond = 
				(divisionType == Sequence.SMPTE_24 ? 24
				: (divisionType == Sequence.SMPTE_25 ? 25
				: (divisionType == Sequence.SMPTE_30 ? 30
				: ((divisionType == Sequence.SMPTE_30DROP) ? 
				29.97: 30)))); // default 30;
			ticksPerSecond = ((double)sequence.getResolution()) * ((double)framesPerSecond);
		}
*/
		tick_to_milli = (1000/ticksPerSecond);
//tick_to_milli = (double)sequence.getMicrosecondLength()/(double)sequence.getTickLength();
		
//*/
//*/
///*
		long time = 0;
        int trackNumber = 0;
		Track[] tracks = sequence.getTracks();
		Track track = null;
        //for (Track track :  sequence.getTracks()) 
		for(trackNumber = 0; trackNumber < tracks.length; trackNumber++)
		{
			track = tracks[trackNumber];
			System.out.println("Track " + trackNumber + ": size = " + track.size());
            System.out.println();
			//LinkedList<hitComponent> key_linkedlist = key_linkedlist_pointer_array.get(trackNumber);
			for (int i=0; i < track.size(); i++) 
			{ 
				MidiEvent event = track.get(i);
				MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) 
				{
					ShortMessage sm = (ShortMessage) message;
				
					int message_com = 0;
					if ( (message_com = sm.getCommand()) == NOTE_ON) 
					{
						tick = event.getTick();
						time = (long)(((double)tick)*tick_to_milli); //- (long)pre_time;
//System.out.printf("tick:" + tick + " tick_to_milli:" + tick_to_milli + " time:"+time + "%n");
						//sm.getChannel();
						
						//falling_bit_generate
//LinkedList<hitComponent> key_linkedlist = key_linkedlist_pointer_array.get(sm.getChannel());
LinkedList<hitComponent> key_linkedlist = null;
if(trackNumber >=2)
{
	key_linkedlist = key_linkedlist_pointer_array.get(trackNumber-2);
}
						key_linkedlist.add(new hitComponent(time));
						
						//converge_circle_generate
						SecureRandom randomNumbers = new SecureRandom();
//*/
//			/*
						if(trackNumber == tracks.length-1)
						{
							int x = randomNumbers.nextInt(80)  + 30;
							int y = randomNumbers.nextInt(80)  + 30;
			
			converge_circles_linkedlist.add(new Converge_circle(x,y,CONVERGE_CIRCLE_INNER_RADIUS,
															CONVERGE_CIRCLE_OUTTER_RADIUS, time, pre_time, 
															Color.CYAN, CONVERGE_CIRCLE_transparency));
						}
//			*/
						/*
						int key = sm.getData1();
						int octave = (key / 12)-1;
						int note = key % 12;
						String noteName = NOTE_NAMES[note];
						int velocity = sm.getData2();
						System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
						*/
///*
					} 
					else if (message_com == NOTE_OFF) 
					{
//*/
						/*
							int key = sm.getData1();
							int octave = (key / 12)-1;
							int note = key % 12;
							String noteName = NOTE_NAMES[note];
							int velocity = sm.getData2();
							System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
						*/
///*
					} 
					else if(message_com == ShortMessage.SYSTEM_RESET)
					{
//if( (sm.getData1() == 0x51) && (sm.getData2() == 0x03) )
						{
							byte[] temp_message = message.getMessage();
							System.out.printf("sm.getData1():" + sm.getData1() + " sm.getData2():" + sm.getData2() + "%n");
							for(int kk = 0; kk < message.getLength(); kk++)
							{
								System.out.printf(temp_message[kk] + "  ");
							}
							System.out.println("");
						}
					}
					else 
					{
						//System.out.println("Command:" + sm.getCommand());
					}
				} 
				else 
				{
					//System.out.println("Other message: " + message.getClass());
						byte[] temp_message = message.getMessage();
							//System.out.printf("sm.getData1():" + sm.getData1() + " sm.getData2():" + sm.getData2() + "%n");
							for(int kk = 0; kk < message.getLength(); kk++)
							{
								System.out.printf("0x%02X ",temp_message[kk]);
								
							}
								if(message.getLength() >= 6)
								{
									if( ((temp_message[0]&0xFF) == 0xFF) && ((temp_message[1]&0xFF) == 0x51) && ((temp_message[2]&0xFF) == 0x03) )
									{
										System.out.printf("Message tempo get: 0x%02X 0x%02X 0x%02X %n",temp_message[3],temp_message[4],temp_message[5]);
									}
								}
							System.out.println("");
						
				}
			}
		}
//*/
/*
A_key_linkedlist.add(new hitComponent(1,g,1000));
A_key_linkedlist.add(new hitComponent(1,g,2000));
A_key_linkedlist.add(new hitComponent(1,g,3000));
A_key_linkedlist.add(new hitComponent(1,g,5000));
A_key_linkedlist.add(new hitComponent(1,g,6000));
A_key_linkedlist.add(new hitComponent(1,g,8000));

S_key_linkedlist.add(new hitComponent(2,g,3000));
S_key_linkedlist.add(new hitComponent(2,g,4000));
S_key_linkedlist.add(new hitComponent(2,g,5000));
S_key_linkedlist.add(new hitComponent(2,g,6000));
S_key_linkedlist.add(new hitComponent(2,g,8000));
S_key_linkedlist.add(new hitComponent(2,g,9000));

D_key_linkedlist.add(new hitComponent(3,g,1000));
D_key_linkedlist.add(new hitComponent(3,g,2000));
D_key_linkedlist.add(new hitComponent(3,g,4000));
D_key_linkedlist.add(new hitComponent(3,g,6000));
D_key_linkedlist.add(new hitComponent(3,g,7000));
D_key_linkedlist.add(new hitComponent(3,g,8000));

F_key_linkedlist.add(new hitComponent(4,g,2000));
F_key_linkedlist.add(new hitComponent(4,g,4000));
F_key_linkedlist.add(new hitComponent(4,g,5000));
F_key_linkedlist.add(new hitComponent(4,g,7000));
F_key_linkedlist.add(new hitComponent(4,g,9000));
F_key_linkedlist.add(new hitComponent(4,g,11000));
F_key_linkedlist.add(new hitComponent(4,g,13000));
F_key_linkedlist.add(new hitComponent(4,g,14000));
*/
/*
SecureRandom randomNumbers = new SecureRandom();
//converge_circles = new Converge_circle[10];
int factor = 1000;
for(int count = 0; count < 50; count++)//converge_circles_linkedlist.size()
{
	int x = randomNumbers.nextInt(80)  + 30;
	int y = randomNumbers.nextInt(80)  + 30;
	converge_circles_linkedlist.add(new Converge_circle(x,y,CONVERGE_CIRCLE_INNER_RADIUS,
	CONVERGE_CIRCLE_OUTTER_RADIUS, (count+2)*factor, (randomNumbers.nextInt(4)+3)*factor+factor/50, 
	Color.CYAN, CONVERGE_CIRCLE_transparency));
}
*/
		return 0;
	}
	//used for sleep
	public static void sleep(int i) throws InterruptedException 
	{
		Thread.sleep(i);
	}
	
	//main working loop
	private boolean first_in;
	private boolean first_play_mid;
	private int time_count;
	public void start()
	{
		int cg = 0;
		try
		{
			cg = composition_generate();
		}catch(Exception ee){}
		
		if(cg == -1)
		{
			try{
				sleep(4000);
			}catch(InterruptedException e)
			{}
		}

	
			first_in = true;
			first_play_mid = true;
//System.out.printf("sp_c:"+ speed_controller +" gh:"+ getHeight()+" SHD:"+SPEED_HEIGHT_DEVIDENT+" dy:"+determination_line_y_coordinate() + " slp_t:" + sleepTime+"pretime = " + pre_time+"%n");
			//int pre_time = (/*SPEED_HEIGHT_DEVIDENT * 9/10-*/ determination_line_y_coordinate())   * sleepTime /speed_controller+2300;
			int pre_time = (/*SPEED_HEIGHT_DEVIDENT * 9/10-*/ determination_line_y_coordinate())    /Speed_control() * sleepTime;
System.out.printf("sp_c:"+ speed_controller +" gh:"+ getHeight()+" SHD:"+SPEED_HEIGHT_DEVIDENT+" dy:"+determination_line_y_coordinate() + " slp_t:" + sleepTime+"pretime = " + pre_time+"%n");

//*********************displayed music should wait "pre_time" long to then to display, that is when:("time_duration - (long)pre_time > 0")*****************
			
			//Halt_time.minusMillis( Duration.between(0,Halt_time).toMillis() );//initialize Halt_time to 0

		   time_count = 0;
	  	   time = new Timer();
	       time.schedule( new TimerTask() {
						public void run()
						{
							if(first_in)
							{
								PG_time = Instant.now();
								Halt_time = Instant.now();
								Catch_time = Instant.now(); 
								first_in = false;
							}
							
							if(!halt)
							{
								if(pre_halt)
								{
//System.out.printf("pre_halt(pre): catch-pG: %d%n",Duration.between(PG_time, Catch_time).toMillis());
									long k = Duration.between(Catch_time, Halt_time).toMillis();
									PG_time = PG_time.plusMillis(k);
									pre_halt = false;
//System.out.printf("pre_halt(af): catch-pG: %d%n",Duration.between(PG_time, Catch_time).toMillis());
								}
								Catch_time = Instant.now();
								time_duration = Duration.between(PG_time, Catch_time).toMillis();
								for_circle_converge_duration = time_duration * CIRCLE_CONVERGE_DURATION_delicate_from_TIME_SCALE/ (TIME_SCALE);
								time_duration /=  TIME_SCALE;
								if(time_count != (time_duration/sleepTime))
								{
									time_count = (int)time_duration/sleepTime;
									repaint();
								}
//System.out.printf("Hello%n");
								if(first_play_mid && (time_duration >= (long)pre_time) )
								{
System.out.printf("Play music & pretime = " + pre_time + "%n");
//try{
//sleep(4000);
//}catch(InterruptedException e)
//{}
					
									playSound("mario");
									try{
										//AudioPlayer.player.start(new AudioStream(getClass().getResourceAsStream("mario.mid")) );
									}catch(Exception e)
									{}
									first_play_mid = false;
								}
							
								//if(time_duration >= 1)
								//yj+=Speed_control();
							
							}
							else
							{
								Halt_time = Instant.now();
								pre_halt = true;
							}
						}
						
					},0,sleepTime);
	 
	}
	
	//key determine
	public void keyPressed(KeyEvent event)
	{
		
		int keyCode = event.getKeyCode();
//System.out.printf("in keyPressed: key pressed is %s%n",KeyEvent.getKeyText(keyCode));
		switch( keyCode ) 
		{ 
			case KeyEvent.VK_ESCAPE:
			if(halt){unhalt = false;}
			else{unhalt = true;}
			halt = unhalt;
			break;
/*
        case KeyEvent.VK_UP:
            y--; 
            break;
        case KeyEvent.VK_DOWN:
            // handle down
					y++;
            break;
        case KeyEvent.VK_LEFT:
            // handle left
			index--;
            break;
        case KeyEvent.VK_RIGHT :
            // handle right
			index++;
			break;
*/
		}
		char key2 = event.getKeyChar();
		switch(key2)
		{
/*
		case 'W':
				y++;
			break;

		case 'w':
				y--;
            break;
*/
		case 'Q':
		case 'q':
				//check_A_key_linkedlist();
				check_key_linkedlist(1);
				combo_confirm();
				break;
		case 'W':
		case 'w':
				//check_S_key_linkedlist();
				check_key_linkedlist(2);
				combo_confirm();
				break;
		case 'E':
		case 'e':
				//check_D_key_linkedlist();
				check_key_linkedlist(3);
				combo_confirm();
				break;
		case 'R':
		case 'r':
				//check_F_key_linkedlist();
				check_key_linkedlist(4);
				combo_confirm();
				break;

		}
		return;
		
	}
		
	public void keyReleased(KeyEvent event){}
	
	public void keyTyped(KeyEvent event){}

	
	//display disappear text for hit component
	private boolean hit_A_key_exit_response = false;
	private boolean hit_S_key_exit_response = false;
	private boolean hit_D_key_exit_response = false;
	private boolean hit_F_key_exit_response = false;
	private boolean[] hit_key_exit_response = 
	{ 
		hit_A_key_exit_response,
		hit_S_key_exit_response,
		hit_D_key_exit_response,
		hit_F_key_exit_response
	};
	private long    hit_A_key_exit_response_time;
	private long    hit_S_key_exit_response_time;
	private long    hit_D_key_exit_response_time;
	private long    hit_F_key_exit_response_time;
	private long[]    hit_key_exit_response_time = new long[Key_List_NUM];
	private int dis_to_determination_line_y(){return getHeight()/SPEED_HEIGHT_DEVIDENT;};
	private void hit_disappear_info(int order)
	{
			Graphics g = getGraphics();
			g.setColor(new Color(255,255,255));
			g.drawString("hit",getWidth()/200*20*order, determination_line_y_coordinate()-dis_to_determination_line_y());
	}
	//determine combo
	private void combo_confirm()
	{
		if(iscombo){combo++;}
	}
	//determine    legel hit   for falling bars
	private void check_key_linkedlist(int left_to_right_order)
	{
		int i = left_to_right_order-1;
		{
			LinkedList<hitComponent> key_linkedlist = key_linkedlist_pointer_array.get(i);
			long t;
			if(key_linkedlist.peekFirst()!=null)
			{
				t = key_linkedlist.peek().get_y_axis_value() -  determination_line_y_coordinate();
				t = (t>=0) ? t : -t;
//if( (t = Math.abs(key_linkedlist.peek().get_y_axis_value() -  determination_line_y_coordinate()) )  <=  hit_time_error_tollerent)
				if( t <=  hit_time_error_tollerent)
				{
//error_accumulation  
					error_accumulation  += t;
					key_linkedlist.remove();  //poll but without null
//hit_disappear_info(1);
					hit_key_exit_response[i] = true;
					hit_key_exit_response_time[i] = time_duration;
					hits++;
					iscombo = true;
				}
				else{
					iscombo = false;
				}
			}
			else
			{
				iscombo = false;
			}
		}
	}
	/*
	private void check_A_key_linkedlist(){
		long t;
		if(A_key_linkedlist.peekFirst()!=null)
		{
			if( (t = Math.abs(A_key_linkedlist.peek().get_y_axis_value() -  determination_line_y_coordinate()) )  <=  hit_time_error_tollerent)
			{
//error_accumulation  
				error_accumulation  += t;
				A_key_linkedlist.remove();  //poll but without null
	//							hit_disappear_info(1);
				hit_A_key_exit_response = true;
				hit_A_key_exit_response_time = time_duration;
				hits++;
				iscombo = true;
			}
			else{
				iscombo = false;
			}
		}
		else{
			iscombo = false;
		}
		
	};
	private void check_S_key_linkedlist(){
		long t;
		if(S_key_linkedlist.peekFirst()!=null)
		{
			if( (t = Math.abs(S_key_linkedlist.peek().get_y_axis_value() -  determination_line_y_coordinate()) )  <=  hit_time_error_tollerent)
			{
//error_accumulation  
				error_accumulation  += t;
				S_key_linkedlist.remove();  //poll but without null
								//hit_disappear_info(2);
				hit_S_key_exit_response = true;
				hit_S_key_exit_response_time = time_duration;
				hits++;
				iscombo = true;
			}
			else{
				iscombo = false;
			}
		}
		else{
			iscombo = false;
		}
	};
	private void check_D_key_linkedlist(){
		long t;
		if(D_key_linkedlist.peekFirst()!=null)
		{
			if( (t = Math.abs(D_key_linkedlist.peek().get_y_axis_value() -  determination_line_y_coordinate()) )  <=  hit_time_error_tollerent)
			{
//error_accumulation  
				error_accumulation  += t;
				D_key_linkedlist.remove();  //poll but without null
								//hit_disappear_info(3);
				hit_D_key_exit_response = true;
				hit_D_key_exit_response_time = time_duration;
				hits++;
				iscombo = true;
			}
			else{
				iscombo = false;
			}
		}
		else{
			iscombo = false;
		}
	};
	private void check_F_key_linkedlist(){
		long t;
		if(F_key_linkedlist.peekFirst()!=null)
		{
			if( (t = Math.abs(F_key_linkedlist.peek().get_y_axis_value() -  determination_line_y_coordinate()) )  <=  hit_time_error_tollerent)
			{
//error_accumulation  
				error_accumulation  += t;
				F_key_linkedlist.remove();  //poll but without null
								//hit_disappear_info(4);
				hit_F_key_exit_response = true;
				hit_F_key_exit_response_time = time_duration;
				hits++;
				iscombo = true;
			}
			else{
				iscombo = false;
			}
		}
		else{
			iscombo = false;
		}
	};
*/
	private int mouse_to_button_x, mouse_to_button_y;
	private int new_mouse_to_button_x, new_mouse_to_button_y;
	public void mousePressed(MouseEvent event)
	
	{
		check_converge_circles_linkedlist(event.getX(), event.getY());
		
		combo_confirm();
		mouse_to_button_x=event.getX()/(getWidth()/12);
		mouse_to_button_y=event.getY()/(getHeight()/3);
		//System.out.println("ASASASA"+(x+y*12+numberOfKey));
		playSound(String.format(PadController.waveFileName[mouse_to_button_x+mouse_to_button_y*12+PadController.numberOfKey]/*+".wav"*/,mouse_to_button_x+mouse_to_button_y*12+PadController.numberOfKey));//,mouse_to_button_x+mouse_to_button_y*12+PadController.numberOfKey);
	}
	
	public void mouseReleased(MouseEvent event){}
	public void mouseClicked(MouseEvent event)
	{
		/*
		for(int count = 0; count < converge_circles.length; count++)
		{	
			if(converge_circles[count].inbound(event.getX(),event.getY(),circle_time_duration)){System.out.printf("count:%d, x:%d, y:%d %n",count,event.getX(),event.getY());}
			//converge_circles[count].draw(g ,getWidth(), getHeight(), for_circle_converge_duration);
		}
		*/
		
		
		
	}
	public void mouseEntered(MouseEvent event){}
	public void mouseExited(MouseEvent event){}
	public void mouseDragged(MouseEvent event)
	{
		new_mouse_to_button_x=event.getX()/(getWidth()/(12));
		new_mouse_to_button_y=event.getY()/(getHeight()/(3));
		if((new_mouse_to_button_x!=mouse_to_button_x||new_mouse_to_button_y!=mouse_to_button_y)&&event.getX()>=0&&event.getX()<=getWidth()&&event.getY()>=0&&event.getY()<=getHeight())
		{
			playSound(String.format(PadController.waveFileName[new_mouse_to_button_x+new_mouse_to_button_y*12+PadController.numberOfKey])/*".wav"*/,new_mouse_to_button_x+new_mouse_to_button_y*12+PadController.numberOfKey);//,new_mouse_to_button_x+new_mouse_to_button_y*12+PadController.numberOfKey);
			mouse_to_button_x=new_mouse_to_button_x;
			mouse_to_button_y=new_mouse_to_button_y;
		}
	}
	public void mouseMoved(MouseEvent event){}
	
	public void playSound(String url,final int n)
	{
		try
		{
			if(numberOfPlugIn[n]!=-1)
				url=url+"_"+numberOfPlugIn[n];
			url=url+".wav";
			InputStream in=getClass().getResourceAsStream(url);
			AudioStream audio=new AudioStream(in);
			//if(!key[15])
				AudioPlayer.player.start(audio);
		}
		catch(Exception e)
		{
			//if(debugFileWriter!=null)
				//e.printStackTrace(debugFileWriter);
		}
	}
	public void playSound(String url)
	{
		try
		{
			url=url+".wav";
			InputStream in=getClass().getResourceAsStream(url);
			AudioStream audio=new AudioStream(in);
			//if(!key[15])
				AudioPlayer.player.start(audio);
		}
		catch(Exception e)
		{
			//if(debugFileWriter!=null)
				//e.printStackTrace(debugFileWriter);
		}
	}
	
	private void check_converge_circles_linkedlist(int getX, int getY)
	{
		Converge_circle cc;
//System.out.printf("MOUSE PRESSED: converge_circles_linkedlist.size() = %d %n", converge_circles_linkedlist.size());
		boolean sidenal = false;
		for(int i = converge_circles_linkedlist.size()-1; i >=0; i--)
		{
			cc = converge_circles_linkedlist.get(i);
			int inbound_return;
			if( (inbound_return = cc.inbound(getX, getY, for_circle_converge_duration, getWidth(), getHeight())) == 0)
			{
					//converge_circles_linkedlist.remove(i);
					//repaint();
				hits++;
				iscombo = true;
				sidenal = true;
				break;
			}
					else if(inbound_return==1)
					{
						break;
					}
		}
		if(!sidenal)
		{
			iscombo = false;
		}
	}
	
//converge circle    class
class Converge_circle
{
	private Image img;
	private int x_axis_value, y_axis_value;
	private long appear_time, alive_time_span;
	private int inner_radius, outter_radius;
	private Color color;
	private int transparency;
	private boolean text_print = false;
	private String text;
	
	Converge_circle(int x_axis_value, int y_axis_value, int inner_radius, int outter_radius, long appear_time, long alive_time_span, Color color, int transparency)
	{
		this.x_axis_value = x_axis_value;
		this.y_axis_value = y_axis_value;
		this.inner_radius = inner_radius;
		this.outter_radius = outter_radius;
		this.appear_time = appear_time;
		this.alive_time_span = alive_time_span;
		if(converge_circle_color_mode == 1)
		{
			this.color = color;
		}
		else if(converge_circle_color_mode ==2)
		{
			this.color = converge_circle_color[converge_circle_color_index];
			converge_circle_color_index++;
			converge_circle_color_index %= converge_circle_color_index_max;
		}
		this.transparency = transparency;
	}
	
	public long get_appear_time(){return appear_time;}
	public long get_expire_time(){return alive_time_span+appear_time;}
	
	public int get_y_axis_value(){return y_axis_value;}
	public int get_x_axis_value(){return x_axis_value;}
	
	public int get_modified_inner_radius(int panel_x, int panel_y)
	{
		double modifier;
			if(panel_x > panel_y)
			{
				modifier =  (panel_y/1200.0);
			}
			else
			{
				modifier = (panel_x/1200.0);
			}
		return (int) ((double)inner_radius*modifier);
	}

	public int draw(Graphics g, int panel_x, int panel_y, long circle_time_duration)
	{
		
		//time_duration = circle_time_duration / factor;
		long time_duration = circle_time_duration;
		if(time_duration < appear_time){return -1;}
		else if(circle_time_duration > appear_time + alive_time_span ){return 1;}
		
		if(text_print)
		{
			g.setColor(new Color(200, 220 ,250));
			g.drawString(text,(int)((double)x_axis_value*(((double)panel_x)/150.0)),(int)((double)y_axis_value*(((double)panel_y)/150.0)));
			return 0;
		}
		
		int factor = CIRCLE_CONVERGE_DURATION_delicate_from_TIME_SCALE;
		
		g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), transparency));
		double modifier = 1;
		
			if(panel_x > panel_y)
			{
				modifier *=  (panel_y/600.0);
			}
			else
			{
				modifier *= (panel_x/600.0);
			}
		
		//int rad = (int)((double)inner_radius*modifier + (double)(outter_radius-inner_radius)*( (double)((alive_time_span+appear_time)*factor - circle_time_duration) / ((double)alive_time_span*(double)factor/(double)outter_radius_amplifier)) );
		
		int rad = 0;
		//mode 1: fasten outter radius  to different converge speed; mode 2 : outter radius propotional to the converge speed
		if(converge_mode == 1)
		{
			rad = (int)((double)inner_radius*modifier + (double)(outter_radius-inner_radius)*( (double)((alive_time_span+appear_time) - circle_time_duration) / ((double)alive_time_span/(double)outter_radius_amplifier)) );
		}
		else if(converge_mode == 2)
		{
			rad = (int)(((double)inner_radius*modifier) + (double)( alive_time_span - (circle_time_duration - appear_time))/outter_circle_to_converge_speed_propotion * modifier);
		}
		//if(rad != 0)
		//{
			int x = (int)((double)x_axis_value*(((double)panel_x)/150.0) - rad);
			int y = (int)((double)y_axis_value*(((double)panel_y)/150.0) - rad);
		
			g.fillOval(x, y, (int)2*rad, (int)2*rad);
		//}
		
		g.setColor(new Color(Color.ORANGE.getRed(), Color.ORANGE.getGreen(), Color.ORANGE.getBlue(), transparency + 50));
		rad =  (int)((double)inner_radius*modifier) ;
		x = (int)((double)x_axis_value*(((double)panel_x)/150.0) - rad);
		y = (int)((double)y_axis_value*(((double)panel_y)/150.0) - rad);
//System.out.printf("panel_x = %d, panel_y = %d %n", panel_x, panel_y);
		g.fillOval(x, y, (int)2*rad, (int)2*rad);
		
		return 0;
	}
	
	
	public int inbound(int x, int y, long circle_time_duration, int panel_x, int panel_y)
	{
		long time_duration = circle_time_duration;
		if(time_duration < appear_time){return -1;}
		else if(circle_time_duration > appear_time + alive_time_span ){return 1;}
		
		double modifier = 1;
		
		if(panel_x > panel_y)
		{
			modifier *=  (panel_y/600.0);
		}
		else
		{
			modifier *= (panel_x/600.0);
		}
		//long rad = 0;
		double rad = 0;
		int x_tran = (int)((double)x_axis_value*(((double)panel_x)/150.0) - rad);
		int y_tran = (int)((double)y_axis_value*(((double)panel_y)/150.0) - rad);
		//long dist = (long)(x_tran-x)*(x_tran-x) + (long)(y_tran-y)*(y_tran-y);
		double dist = (x_tran-x)*(x_tran-x) + (y_tran-y)*(y_tran-y);
		if(converge_circle_click_determine_mode == 1)
		{

			//rad = (long)((double)inner_radius*modifier);
			rad = ((double)inner_radius*modifier);
//System.out.printf("x = %d, x_tran = %d, y = %d, y_tran = %d, dist = %d, rad*rad = %d%n", x, x_tran, y, y_tran, dist, rad*rad);
//System.out.printf("x = %d, x_tran = %d, y = %d, y_tran = %d, dist = %f, rad*rad = %f%n", x, x_tran, y, y_tran, dist, rad*rad);
		}
		else if(converge_circle_click_determine_mode == 2)
		{
			if(converge_mode == 1)
			{
				//rad = (long)((double)inner_radius*modifier + (double)(outter_radius-inner_radius)*( (double)((alive_time_span+appear_time) - circle_time_duration) / ((double)alive_time_span/(double)outter_radius_amplifier)) );
				rad = ((double)inner_radius*modifier + (double)(outter_radius-inner_radius)*( (double)((alive_time_span+appear_time) - circle_time_duration) / ((double)alive_time_span/(double)outter_radius_amplifier)) );
			}
			else if(converge_mode == 2)
			{
				//rad = (long)(((double)inner_radius*modifier) + (double)( alive_time_span - (circle_time_duration - appear_time))/outter_circle_to_converge_speed_propotion * modifier);
				rad = (((double)inner_radius*modifier) + (double)( alive_time_span - (circle_time_duration - appear_time))/outter_circle_to_converge_speed_propotion * modifier);
			}

		}
		if(dist <= rad*rad)
		{
			double rr = rad*rad;
			if(dist <= rr/64)
			{
				text = "perfect";
				points+= 256;
			}
			else if(dist <= rr/16)
			{
				text = "excellent";
				points+= 64;
			}
			else if(dist <= rr/4)
			{
				text = "great";
				points += 8;
			}
			else 
			{
				text = "good";
				points += 1;
			}
			text_print = true;
			
			appear_time = circle_time_duration;
			alive_time_span = 500;
			return 0;
		}
		return -1;
	}
}


//char[] code = { '1', 'a', '2', 'c', '3', 's','f', '4', 'a','s','g','f', '5', 'a','d','s'};
//falling bar     class
class hitComponent extends JComponent {
	private Image img;
	private int last_y_axis_value;
	private int y_axis_value;
	private long appear_time;
	
	private Color color;
	private Graphics g;
	private int left_to_right_order;

	hitComponent(long appear_time)
	{
		setVisible(true);
		this.appear_time = appear_time; 
		y_axis_value = 0;
	}
	
	hitComponent(int left_to_right_order, Graphics g, long appear_time)
	{
		setVisible(true);
		this.appear_time = appear_time; 
		this.g = g;
		this.color = Color_Pair[left_to_right_order-1];
		this.left_to_right_order = left_to_right_order;
		y_axis_value = 0;
	}
	
	public long get_appear_time(){
		return appear_time;
	}
	
	public int get_y_axis_value()
	{
		return y_axis_value;
	}
	
	public int renew_y_axis_value()
	{
			last_y_axis_value = y_axis_value;
			y_axis_value += Speed_control();
			return last_y_axis_value;
	}
/*
	public int draw(long time_duration, int line_y_coordinate, int Width, int Height)
	{
		if(appear_time > time_duration){return -1;}
		
		if(y_axis_value < line_y_coordinate + hit_time_error_tollerent)
		{
			g.setColor(color);
			g.fillRect(Width/200*left_to_right_order*20, renew_y_axis_value(), Width/20, Height/60);
		}
		else
		{
			count_out_buond_key_hit[left_to_right_order-1]++;
		}
		return 0;
	}
*/
}
	
	//print color
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
//System.out.printf("time in paint:%d %n ", time_duration);
		//
							g.setColor(new Color(255,255,255));
							int pre_time = (/*SPEED_HEIGHT_DEVIDENT * 9/10 -*/ determination_line_y_coordinate())  * sleepTime/speed_controller +600;
							if(time_duration >= (long)pre_time)
							{
								g.drawString( time_duration+ " " + pre_time + "Time passing : " + (time_duration-(long)pre_time)/divide_to_S + " s", getWidth()-150,25);
							}
							else
							{
								g.drawString("Time passing : " + 0 + " s", getWidth()-150,25);
							}
							g.drawString("Speed_Control: "+"hits : " + hits, getWidth()-150,50);
							if(iscombo){g.drawString("combo! now", getWidth()-150,75);}
							g.drawString("error : " + error_accumulation, getWidth()-150,125);


		int ww = getWidth()/200*20;
		int yy = determination_line_y_coordinate();
		int gw = getWidth()/20, gh = getHeight()/60;
		LinkedList<hitComponent> key_linkedlist = null;
		for(int i = 0; i < Key_List_NUM; i++)
		{
			g.setColor(Color_Pair[i]);
			key_linkedlist = key_linkedlist_pointer_array.get(i);
			for(hitComponent beats : key_linkedlist)
			{
				if(key_linkedlist.peekFirst()==null){break;}
				
				if(beats.get_appear_time() > time_duration) 
				{
					break;
				} 
				int t = beats.get_y_axis_value();
				if(t < yy + hit_time_error_tollerent)
				{
					g.fillRect(ww*(i+1), beats.renew_y_axis_value(), gw, gh); 
				}
				else 
				{
					count_out_buond_key_hit[i]++;
				}
			}
		}

/*
//g.setColor(new Color(Color.GREEN.getRed(), Color.GREEN.getGreen(), Color.GREEN.getBlue(), hit_transparency));
g.setColor(Color_Pair[0]);

		for(hitComponent beats: A_key_linkedlist)
{
		//hits.next();
		if(beats.get_appear_time() > time_duration) 
		{
			break;
		}
		
		int t = beats.get_y_axis_value();

if(t < determination_line_y_coordinate() + hit_time_error_tollerent)
{
		g.fillRect(getWidth()/200*20, beats.renew_y_axis_value(), getWidth()/20, getHeight()/60); 
}
else {count_out_buond_A_key_hit++;} 
}



//g.setColor(new Color(Color.YELLOW.getRed(), Color.YELLOW.getGreen(), Color.YELLOW.getBlue(), hit_transparency));
g.setColor(Color_Pair[1]);

		for(hitComponent beats2: S_key_linkedlist)
{
		if(beats2.get_appear_time() > time_duration) 
		{
			break;
		}
		
		int t = beats2.get_y_axis_value();
if(t < determination_line_y_coordinate() + hit_time_error_tollerent)
{
		g.fillRect(getWidth()/200*40, beats2.renew_y_axis_value(), getWidth()/20, getHeight()/60); 
}
else{count_out_buond_S_key_hit++;} 
}


//g.setColor(new Color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), hit_transparency));
g.setColor(Color_Pair[2]);

		for(hitComponent beats: D_key_linkedlist)
{
		if(beats.get_appear_time() > time_duration) 
		{
			break;
		}

		int t = beats.get_y_axis_value();
		//System.out.printf("%d%n",beats.get_y_axis_value());
if(t < determination_line_y_coordinate() + hit_time_error_tollerent)
{
		g.fillRect(getWidth()/200*60, beats.renew_y_axis_value(), getWidth()/20, getHeight()/60); 
}else{count_out_buond_D_key_hit++;} 
}


//g.setColor(new Color(Color.MAGENTA.getRed(), Color.MAGENTA.getGreen(), Color.MAGENTA.getBlue(), hit_transparency));
g.setColor(Color_Pair[3]);
		for(hitComponent beats: F_key_linkedlist)
{
		if(beats.get_appear_time() > time_duration) 
		{
			break;
		}

		int t = beats.get_y_axis_value();
		//System.out.printf("%d%n",beats.get_y_axis_value());
if(t < determination_line_y_coordinate() + hit_time_error_tollerent)
{
		g.fillRect(getWidth()/200*80, beats.renew_y_axis_value(), getWidth()/20, getHeight()/60); 
}else{count_out_buond_F_key_hit++;} 
}
*/
		g.setColor(new Color(200,225,250));
		ww = getWidth()/200*20;
		yy = determination_line_y_coordinate()-dis_to_determination_line_y();
		for(int i = 0; i < Key_List_NUM; i++)
		{
			if(hit_key_exit_response[i])
			{
				g.drawString("hit",ww*(i+1), yy);
			}
			if(time_duration > hit_key_exit_response_time[i] + 500){hit_key_exit_response[i] = false;}
		}
/*
if(hit_A_key_exit_response)
{
	//hit_disappear_info(1);
			g.drawString("hit",getWidth()/200*20*1, determination_line_y_coordinate()-dis_to_determination_line_y());
	if(time_duration > hit_A_key_exit_response_time + 500){hit_A_key_exit_response = false;}
}
if(hit_S_key_exit_response)
{
	//hit_disappear_info(1);
			g.drawString("hit",getWidth()/200*20*2, determination_line_y_coordinate()-dis_to_determination_line_y());
	if(time_duration > hit_S_key_exit_response_time + 500){hit_S_key_exit_response = false;}
}
if(hit_D_key_exit_response)
{
	//hit_disappear_info(1);
			g.drawString("hit",getWidth()/200*20*3, determination_line_y_coordinate()-dis_to_determination_line_y());
	if(time_duration > hit_D_key_exit_response_time + 500){hit_D_key_exit_response = false;}
}
if(hit_F_key_exit_response)
{
	//hit_disappear_info(1);
			g.drawString("hit",getWidth()/200*20*4, determination_line_y_coordinate()-dis_to_determination_line_y());
	if(time_duration > hit_F_key_exit_response_time + 500){hit_F_key_exit_response = false;}
}
*/
//converge_circle draw
/*
for(int count = 0; count < converge_circles.length; count++)
{	
	//if(converge_circle[count].bound(getx(),gety())){System.out.printf("count:%d, x:%d, y:%d %n"count,getx(),gety());}
	converge_circles[count].draw(g ,getWidth(), getHeight(), for_circle_converge_duration);//time_duration);
}
/*
//System.out.printf("converge_circle_draw %n");
for(Converge_circle cc: converge_circles_linkedlist)
{
	if(cc.draw(g ,getWidth(), getHeight(), for_circle_converge_duration)==1)
	{
		cc.remove();
	}
}
*/

Converge_circle cc =null;
	for(int i = 0; i < converge_circles_linkedlist.size(); i++)
		{
			cc = converge_circles_linkedlist.get(i);
			int draw_return;
			if( (draw_return = cc.draw(g ,getWidth(), getHeight(), for_circle_converge_duration/*time_duration*/)) ==1)
			{
				//converge_circles_linkedlist.remove(i);
				wait_to_delete_converge_circles.add(i);
			}
					//if(draw_return == -1){break;}
		}
		
		g.setColor(new Color(200,230, 255));
		//determination_line_y_coordinate();
		g.drawLine(0, determination_line_y_coordinate(), getWidth(), determination_line_y_coordinate());
		
		this.setBackground(new Color(0,0,50));
				g.setColor(Color.BLACK);
			
		clean_out_bound();
    }
	
	//clean out    the out-Lowbound   falling bars
	private void clean_out_bound()
	{
		
		for(int i = 0; i < Key_List_NUM; i++)
		{
			LinkedList<hitComponent> key_linkedlist = key_linkedlist_pointer_array.get(i);
			while(count_out_buond_key_hit[i] > 0)
			{
				count_out_buond_key_hit[i]--;
				key_linkedlist.remove();
			}
		}
		int k = wait_to_delete_converge_circles.size();
		int j =0;
		if(k > 0)
		{
			for(int i = 0; i < k; i++)
			{
				j = wait_to_delete_converge_circles.get(i);
				converge_circles_linkedlist.remove(j);
			}
			wait_to_delete_converge_circles.clear();
		}
			
		/*
		while(count_out_buond_A_key_hit != 0)
		{
			count_out_buond_A_key_hit--;
			A_key_linkedlist.remove();
		}
		while(count_out_buond_S_key_hit != 0)
		{
			count_out_buond_S_key_hit--;
			S_key_linkedlist.remove();
		}
		while(count_out_buond_D_key_hit != 0)
		{
			count_out_buond_D_key_hit--;
			D_key_linkedlist.remove();
		}
		while(count_out_buond_F_key_hit != 0)
		{
			count_out_buond_F_key_hit--;
			F_key_linkedlist.remove();
		}
		*/
	}
}