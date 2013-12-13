/**
 * 
 * @author  Hanoch Griner and Tiago Tobias
 * @version 28/11/2013
 */
public class Position
{
    //
    private double azimuth;
    private double zenith;
    private Coordinates location;
    /**
     * 
     */
    public Position(double azimuth, double zenith)
    {
        this.azimuth = azimuth;
        this.zenith = zenith;
        location = new Coordinates();
    }
    
    /**
     * 
     */
    public double getAzimuth()
    {
        return azimuth;
    }
        
    /**
     * 
     */
    public double getZenith()
    {
        return zenith;
    }
    
    /**
     * 
     */
    public Coordinates getLocation()
    {
        return location;
    }

    /**
     * 
     */
    public void setAzimuth(double azimuth)
    {
        this.azimuth = azimuth;
    }
        
    /**
     * 
     */
    public void setZenith(double zenith)
    {
        this.zenith = zenith;
    }
    
    /**
     * 
     */
    public void setLocation(Coordinates location)
    {
        this.location = location;
    }
    
    /**
     * 
     */
    public String toString()
    {
        return location.toString() + "P" + azimuth + "-" + zenith;
    }
}
