/**
 * 
 * @author  Hanoch Griner and Tiago Tobias
 * @version 28/11/2013
 */
public class Engine
{
    //
    private int position;  //motor com os sensores
    private ComunicationUSB engine;
    private String name;
    
    /**
     * 
     */
    public Engine(String name)
    {
        this.name = "M" + name;
        position = 0;
        engine = new ComunicationUSB();
    }
    
    /**
     * 
     */
    public int getPosition()
    {
        return position;
    }
    
    /**
     * 
     */
    public void move(int step)
    {
        engine.move(name, step);
        position += step;
    }
}
