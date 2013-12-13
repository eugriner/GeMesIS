import gnu.io.*;
import java.io.*;
import java.util.concurrent.TimeUnit;
public class ComunicationUSB
{
    private NRSerialPort serial;
    
    public ComunicationUSB()
    {
        //porta no windows e baud
        serial = new NRSerialPort("COM3", 9600);
    }
    
    public int read(String cmd)
    {
        //valor retornado caso não haja input
        int valuereturn = -2;
        //adiciona char de encerramento do comando
        cmd += "\n";
        System.out.print(cmd); ///retirar
        
        serial.connect();
        BufferedReader ins = new BufferedReader(new InputStreamReader(serial.getInputStream()));          
        OutputStream outs = serial.getOutputStream();

        try
        {
            outs.write(cmd.getBytes());
            String value = ins.readLine();
            //convete para inteiro
            valuereturn = Integer.parseInt(value);
        }
        catch(IOException e)
        {
            
        }
        serial.disconnect();
        System.out.println(valuereturn); ///retirar
        return valuereturn;
    }
    
    public void move(String cmd, int step)
    {
        //adiciona char de encerramento do comando
        cmd += step + "\n";
        System.out.println(cmd); ///retirar
        
        serial.connect();
        BufferedReader ins = new BufferedReader(new InputStreamReader(serial.getInputStream()));          
        OutputStream outs = serial.getOutputStream();

        try
        {
            outs.write(cmd.getBytes());
        }
        catch(IOException e)
        {
            
        }
        serial.disconnect();
    }
    
    public void disconnect()
    {
        serial.disconnect();
    }
}