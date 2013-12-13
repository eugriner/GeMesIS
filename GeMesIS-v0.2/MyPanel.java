import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class MyPanel extends JPanel implements ActionListener {
    private JTextArea console;
    private JToggleButton onOff;
    private JLabel labelTIME_BETWEEN_MESUREMENT;
    private JLabel labelWAITING_TIME_AFTER_FAILURE;
    private JLabel labelTimeBetweenLogwrite;
    private JLabel labelWAITING_TIME_AFTER_LOTS_FAILURE;
    private JLabel labelLARGE_NUMBER_FAILURES;
    private JLabel labelSUN_INTENSITY;
    private JLabel labelCLOUD_DIFFERENCE;
    private JLabel labelLOW_DIFFERENCE_BETWEEN_SENSORS;
    private JTextField fieldLOW_DIFFERENCE_BETWEEN_SENSORS;
    private JTextField fieldCLOUD_DIFFERENCE;
    private JTextField fieldSUN_INTENSITY;
    private JTextField fieldLARGE_NUMBER_FAILURES;
    private JTextField fieldWAITING_TIME_AFTER_LOTS_FAILURE;
    private JTextField fieldTIME_BETWEEN_MESUREMENT;
    private JTextField fieldWAITING_TIME_AFTER_FAILURE;
    private JTextField fieldTimeBetweenLogwrite;
    private Core core;
    private Thread thread;
    
    public MyPanel() {
        //construct components
        console = new JTextArea (20, 20);
        onOff = new JToggleButton ("OFF", false);
        labelTIME_BETWEEN_MESUREMENT = new JLabel ("Time Between Mesurement");
        fieldTIME_BETWEEN_MESUREMENT = new JTextField (25);
        labelWAITING_TIME_AFTER_FAILURE = new JLabel ("Waiting Time After Failure");
        fieldWAITING_TIME_AFTER_FAILURE = new JTextField (25);
        labelTimeBetweenLogwrite = new JLabel ("Time Between Logwrite");
        fieldTimeBetweenLogwrite = new JTextField (25);
        labelWAITING_TIME_AFTER_LOTS_FAILURE = new JLabel ("Waiting Time After Lots Failure");
        fieldWAITING_TIME_AFTER_LOTS_FAILURE = new JTextField (25);
        labelLARGE_NUMBER_FAILURES = new JLabel ("Large Number Failures");
        fieldLARGE_NUMBER_FAILURES = new JTextField (25);
        labelSUN_INTENSITY = new JLabel ("Sun Intensity");
        fieldSUN_INTENSITY = new JTextField (25);
        labelCLOUD_DIFFERENCE = new JLabel ("Cloud Difference");
        fieldCLOUD_DIFFERENCE = new JTextField (25);
        labelLOW_DIFFERENCE_BETWEEN_SENSORS = new JLabel ("Low Difference Between sensors");
        fieldLOW_DIFFERENCE_BETWEEN_SENSORS = new JTextField (25);

        //set components properties
        console.setEnabled (false);
        onOff.setToolTipText ("Começar e parar a medir");

        //adjust size and set layout
        setPreferredSize (new Dimension (800, 600));
        setLayout (null);

        //add components
        add (console);
        add (onOff);
        onOff.addActionListener(this);
        add (labelTIME_BETWEEN_MESUREMENT);
        add (fieldTIME_BETWEEN_MESUREMENT);
        add (fieldWAITING_TIME_AFTER_LOTS_FAILURE);
        add (labelWAITING_TIME_AFTER_LOTS_FAILURE);
        add (labelWAITING_TIME_AFTER_FAILURE);
        add (fieldWAITING_TIME_AFTER_FAILURE);
        add (labelTimeBetweenLogwrite);
        add (fieldTimeBetweenLogwrite);
        add (labelLARGE_NUMBER_FAILURES);
        add (fieldLARGE_NUMBER_FAILURES);
        add (labelSUN_INTENSITY);
        add (fieldSUN_INTENSITY);
        add (labelCLOUD_DIFFERENCE);
        add (fieldCLOUD_DIFFERENCE);
        add (labelLOW_DIFFERENCE_BETWEEN_SENSORS);
        add (fieldLOW_DIFFERENCE_BETWEEN_SENSORS);

        
        
        //set component bounds (only needed by Absolute Positioning)
        console.setBounds (250, 50, 520, 415);
        onOff.setBounds (15, 10, 200, 25);
        labelTIME_BETWEEN_MESUREMENT.setBounds (20, 170, 200, 25);
        fieldTIME_BETWEEN_MESUREMENT.setBounds (15, 190, 200, 25);
        labelWAITING_TIME_AFTER_FAILURE.setBounds (20, 225, 200, 25);
        fieldWAITING_TIME_AFTER_FAILURE.setBounds (15, 245, 200, 25);
        labelWAITING_TIME_AFTER_LOTS_FAILURE.setBounds (15, 65, 200, 25);
        fieldWAITING_TIME_AFTER_LOTS_FAILURE.setBounds (15, 85, 200, 25);
        labelTimeBetweenLogwrite.setBounds (20, 115, 200, 25);
        fieldTimeBetweenLogwrite.setBounds (15, 135, 200, 25);
        labelLARGE_NUMBER_FAILURES.setBounds (20, 275, 200, 25);
        fieldLARGE_NUMBER_FAILURES.setBounds (15, 295, 200, 25);
        labelSUN_INTENSITY.setBounds (20, 325, 200, 25);
        fieldSUN_INTENSITY.setBounds (15, 345, 200, 25);
        labelCLOUD_DIFFERENCE.setBounds (20, 375, 200, 25);
        fieldCLOUD_DIFFERENCE.setBounds (15, 395, 200, 25);
        labelLOW_DIFFERENCE_BETWEEN_SENSORS.setBounds (20, 425, 200, 25);
        fieldLOW_DIFFERENCE_BETWEEN_SENSORS.setBounds (15, 450, 200, 25);
        fieldWAITING_TIME_AFTER_FAILURE.setText ("30000");
        fieldTIME_BETWEEN_MESUREMENT.setText ("2000");
        fieldTimeBetweenLogwrite.setText ("180000");
        fieldWAITING_TIME_AFTER_LOTS_FAILURE.setText ("180000");
        fieldLARGE_NUMBER_FAILURES.setText ("40");
        fieldSUN_INTENSITY.setText ("100");
        fieldCLOUD_DIFFERENCE.setText ("200");
        fieldLOW_DIFFERENCE_BETWEEN_SENSORS.setText ("30");
        
        core = new Core();
        thread = new Thread(core);
    }
    
    // metodo de ouvinte, para tratar os eventos gerados ao clicar um botao
    public void actionPerformed(ActionEvent evt)
    {
        Object source = evt.getSource();
        if(onOff.isSelected())
        {
            onOff.setText("ON");
            fieldWAITING_TIME_AFTER_LOTS_FAILURE.setEnabled (false);
            fieldWAITING_TIME_AFTER_FAILURE.setEnabled (false);
            fieldTIME_BETWEEN_MESUREMENT.setEnabled (false);
            fieldTimeBetweenLogwrite.setEnabled (false);
            fieldLARGE_NUMBER_FAILURES.setEnabled (false);
            fieldSUN_INTENSITY.setEnabled (false);
            fieldCLOUD_DIFFERENCE.setEnabled (false);
            fieldLOW_DIFFERENCE_BETWEEN_SENSORS.setEnabled (false);
            System.out.println(fieldLOW_DIFFERENCE_BETWEEN_SENSORS.getText());
            thread.start();
        }
        else
        {
            onOff.setText("OFF");
            fieldWAITING_TIME_AFTER_LOTS_FAILURE.setEnabled (true);
            fieldWAITING_TIME_AFTER_FAILURE.setEnabled (true);
            fieldTIME_BETWEEN_MESUREMENT.setEnabled (true);
            fieldTimeBetweenLogwrite.setEnabled (true);
            fieldLARGE_NUMBER_FAILURES.setEnabled (true);
            fieldSUN_INTENSITY.setEnabled (true);
            fieldCLOUD_DIFFERENCE.setEnabled (true);
            fieldLOW_DIFFERENCE_BETWEEN_SENSORS.setEnabled (true);
            core.stop();
            thread.interrupt();
        }
    }
   

    public static void main (String[] args)
    {
        JFrame frame = new JFrame ("MyPanel");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add (new MyPanel());
        frame.pack();
        frame.setVisible (true);
    }
}
/**
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class MyPanel extends JPanel implements ActionListener {
    private JButton start;
    private JTextArea console;
    private JToggleButton onOff;

    public MyPanel() {
        //construct components
        start = new JButton ("Start");
        onOff = new JToggleButton ("ON/OFF", false);
        console = new JTextArea (20, 20);

        //set components properties
        console.setEnabled (false);
        onOff.setToolTipText ("Começar e parar a medir");
        
        //adjust size and set layout
        setPreferredSize (new Dimension (667, 366));
        setLayout (null);

        //add components
        add (start);
        add (console);
        
        start.addActionListener(this);

        //set component bounds (only needed by Absolute Positioning)
        start.setBounds (15, 30, 100, 20);
        console.setBounds (125, 30, 520, 315);
    }

    // metodo de ouvinte, para tratar os eventos gerados ao clicar um botao
   public void actionPerformed(ActionEvent evt){
      Object source = evt.getSource();
      console.setText("Hellow World!");
      
   }

    public static void main (String[] args) {
        JFrame frame = new JFrame ("MyPanel");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add (new MyPanel());
        frame.pack();
        frame.setVisible (true);
        Core core = new Core();
 
    }
}
*/
