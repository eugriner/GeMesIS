/**
 * 
 * @author  Hanoch Griner and Tiago Tobias
 * @version 28/11/2013
 */
public class Sensor
{
    //
    private int value;
    private ComunicationUSB sensor;
    private String name;
    
    /**
     * 
     */
    public Sensor(int name)
    {
        value = -2;
        sensor = new ComunicationUSB();
        this.name = "S" + name;
    }
    
    /**
     * 
     */
    public int getMeasurement()
    {
        value = sensor.read(name);
        return value;
    }
}