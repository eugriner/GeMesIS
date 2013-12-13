import java.util.ArrayList;
import java.util.Iterator;
/**
 * 
 * @author  Hanoch Griner and Tiago Tobias
 * @version 28/11/2013
 */
public class Data
{
    //
    private ArrayList<Double> sensorsValue;
    private Position sunPosition;
    
    /**
     * 
     */
    public Data(ArrayList<Double> sensorsValue, Position sunPosition)
    {
        this.sensorsValue = sensorsValue;
        this.sunPosition = sunPosition;
    }
    
    /**
     * 
     */
    public double getSensorValue(int numberSensor)
    {
        return sensorsValue.get(numberSensor);
    }
    
    /**
     * 
     */
    public Position getSunPosition()
    {
        return sunPosition;
    }
    
    /**
     * 
     */
    public String toString()
    {
        //data vai armazenar a String com as informações a serem gravadas no log
        String data;
        
        //adiciona a string de sunPosition e no final a letra S de sensores
        data = sunPosition.toString()+"S";
        
        //contador
        Iterator<Double> it = sensorsValue.iterator();
        while (it.hasNext())
        {
            if(it.hasNext())
                data = data + it.next() + "-";
            else
               data = data + it.next() + ";"; 
        }   
        return data;
    }
}