import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class MyPanel extends JPanel implements ActionListener {
    private JTextArea console;
    private JToggleButton onOff;
    private JLabel labelTIME_BETWEEN_MESUREMENT;
    private JLabel labelLimitZenith;
    private JLabel labelTimeBetweenLogwrite;
    private JTextField fieldTIME_BETWEEN_MESUREMENT;
    private JTextField fieldLimitZenith;
    private JTextField fieldTimeBetweenLogwrite;
    private Core core;
    public MyPanel() {
        //construct components
        console = new JTextArea (20, 20);
        onOff = new JToggleButton ("OFF", false);
        labelTIME_BETWEEN_MESUREMENT = new JLabel ("TIME BETWEEN MESUREMENT");
        fieldTIME_BETWEEN_MESUREMENT = new JTextField (5);
        labelLimitZenith = new JLabel ("Limite Zenith");
        fieldLimitZenith = new JTextField (5);
        labelTimeBetweenLogwrite = new JLabel ("Time Between Logwrite");
        fieldTimeBetweenLogwrite = new JTextField (5);

        //set components properties
        console.setEnabled (false);
        onOff.setToolTipText ("Começar e parar a medir");

        //adjust size and set layout
        setPreferredSize (new Dimension (667, 366));
        setLayout (null);

        //add components
        add (console);
        add (onOff);
        onOff.addActionListener(this);
        add (labelTIME_BETWEEN_MESUREMENT);
        add (fieldTIME_BETWEEN_MESUREMENT);
        add (labelLimitZenith);
        add (fieldLimitZenith);
        add (labelTimeBetweenLogwrite);
        add (fieldTimeBetweenLogwrite);

        
        
        //set component bounds (only needed by Absolute Positioning)
        console.setBounds (125, 30, 520, 315);
        onOff.setBounds (15, 30, 100, 25);
        labelTIME_BETWEEN_MESUREMENT.setBounds (20, 170, 100, 25);
        fieldTIME_BETWEEN_MESUREMENT.setBounds (15, 190, 100, 25);
        labelLimitZenith.setBounds (20, 225, 100, 25);
        fieldLimitZenith.setBounds (15, 245, 100, 25);
        labelTimeBetweenLogwrite.setBounds (20, 115, 100, 25);
        fieldTimeBetweenLogwrite.setBounds (15, 135, 100, 25);
        
        Thread core = new Thread(new Core());
        core.start();
        fieldLimitZenith.setText ("4");
        fieldTIME_BETWEEN_MESUREMENT.setText ("2000");
        fieldTimeBetweenLogwrite.setText ("180000");
    }
    
    // metodo de ouvinte, para tratar os eventos gerados ao clicar um botao
    public void actionPerformed(ActionEvent evt)
    {
        Object source = evt.getSource();
        if(onOff.isSelected())
        {
            onOff.setText("ON");
            fieldLimitZenith.setEnabled (false);
            fieldTIME_BETWEEN_MESUREMENT.setEnabled (false);
            fieldTimeBetweenLogwrite.setEnabled (false);
            core.run();
        }
        else
        {
            onOff.setText("OFF");
            fieldLimitZenith.setEnabled (true);
            fieldTIME_BETWEEN_MESUREMENT.setEnabled (true);
            fieldTimeBetweenLogwrite.setEnabled (true);
            core.stop();
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
