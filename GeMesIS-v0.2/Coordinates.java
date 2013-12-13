import org.joda.time.DateTime;

/**
 * 
 * @author  Hanoch Griner and Tiago Tobias
 * @version 28/11/2013
 */
public class Coordinates
{
    private double latitude;
    private double longitude;
    
    //GPS
    private static double LATITUDE = -5.843464;
    private static double LONGITUDE = -35.199442;
    
    private DateTime date;
    
    /**
     * 
     */
    public Coordinates(double latitude, double longitude)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        date = new DateTime();
    }
    
    /**
     * 
     */
    public Coordinates()
    {
        latitude = LATITUDE;
        longitude = LONGITUDE;
    }
    
    /**
     * 
     */
    public double getLatitude()
    {
        return latitude;
    }
    
    /**
     * 
     */
    public double getLongitude()
    {
        return longitude;
    }
    
    /**
     * 
     */
    public DateTime getDate()
    {
        return date;
    }
    
    /**
     * 
     */
    public DateTime getCurrentTime()
    {
        return date.now();
    }
    
    /**
     * 
     */
    public String toString()
    {
        return "D" + date.toString() + "C" + latitude + "-" + longitude;
    }
}
