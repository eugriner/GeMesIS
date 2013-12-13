import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import org.joda.time.DateTime;
import java.io.InterruptedIOException;
import java.lang.Math;

/**
 * 
 * @author  Hanoch Griner and Tiago Tobias
 * @version 28/11/2013
 */
public class Core implements Runnable
{
    // Parametros constantes. 
    
    // Medição, tempo será em milisegundos
    // Intervalo de tempo que tentará realizar uma medida
    private volatile int TIME_BETWEEN_MESUREMENT = 2000;
    // Tempo de espera até a proxima medida em caso de falha
    private volatile int WAITING_TIME_AFTER_FAILURE = 30000;
    // Tempo de espera depois de um numero grande de falhas LARGE_NUMBER_OF_FAILURES
    private volatile int WAITING_TIME_AFTER_LOTS_FAILURE = 180000;
    // Numero grande de falhas
    private volatile int LARGE_NUMBER_FAILURES = 40;
    // Tempo de espera entre gravaçoes bem sucedidas no Datalog
    private volatile int TIME_BETWEEN_LOGWRITE = 180000;
    
    
    // Motores
    // Limite de passos na horizontal
    private volatile int LIMIT_AZIMUTH = 48;
    // Limite de passos na vertical
    private volatile int LIMIT_ZENITH = 48;
    // Tempo de espera entre cada comando para os motores em MILLISECONDS
    private volatile int TIME_WAITING_ENGINE = 100;
    
    //Sensores
    //Os sensores seguem esse formato
        //2 1
        // 5     0
        //3 4
    //Numero de sensores
    private volatile int N_SENSORS = 6;
    //Se tiver abaixo desse valor o sensor esta apontado para o sol
    private volatile int SUN_INTENSITY = 100;
    //Valor que garante que a luz no sensor central não é difusa
    private volatile int CLOUD_DIFFERENCE = 200;
    //Valor de diferença pequena entre sensores
    private volatile int LOW_DIFFERENCE_BETWEEN_SENSORS = 30;
    //
    private boolean isOn;
    private boolean sunLocated;
    private boolean sunTraked;

    //MA
    private Engine engineZ;
    //MB
    private Engine engineA;
    private ArrayList<Sensor> sensors;
    private Position lastSunPosition;
    private Coordinates gps;
    private Data firstSunPositionOfDay;
    private int failuresCounter;

    public Core()
    {
        //Informa se o sol foi encontrado ou não
        sunLocated = false;
        gps = new Coordinates();
        //Interrompe o laço do start e para todo o programa
        isOn = false;
        //motores para realizar moviemnto horizontal e vertical
        //mb
        engineA = new Engine("B");
        //ma
        engineZ = new Engine("A");
        //sensores
        sensors = new ArrayList<Sensor>();
        //Os sensores seguem esse formato
        //2 1
        // 5     0
        //3 4
        for(int i = 0; i < N_SENSORS; i++)
            sensors.add(new Sensor(i));
        //Posição que o sol foi "encontrado" da ultima vez
        lastSunPosition = new Position(0,0);
        //contador de falhas
        failuresCounter = 0;
        
        sunTraked = false;
        System.out.println("Core has been created!");
    }
    
    /**
     * 
     */
    public void run()
    {
        isOn = true;
        System.out.println("Start Core!");
        while(isOn)
        {
            if(!sunLocated)
            {
                //procura o sol em todo ceu
                sunLocated = lookForSun();
                //caso nao ache o sol incrementa 1
                if(!sunLocated)
                {
                    failure();
                    System.out.println("Sun was not located!");
                }
            }
            else
            {
                //rastreia  o sol depois de o ter achado
                System.out.println("Sun was located! - Start Traking");
                sunLocated = startTrackSun();
            }
        }
    }
    
    /**
     * 
     */
    public void stop()
    {
        //isOn = false;
    }
    
    /**
     * 
     */
    private boolean lookForSun()
    {
        boolean foundIt = false;
        //começa a fazer a varredura do ceu partindo do 0,0 até que varra o ceu todo ou encontre o sol
        for(int i = 0; i <= LIMIT_AZIMUTH && !foundIt; i++)
        {
            int j = 0;
            for(; j <= LIMIT_ZENITH && !foundIt; j++)
            {
                goTo(i,j);
                foundIt = checkSensorFoundSun();
            }
            //Verifica se não chegou a final
            if(i != LIMIT_AZIMUTH)
            {
                //Muda o azimuth para começar medir de tras para frente em zenith
                i++;
                j--;
                for(; j >= 0 && !foundIt; j--)
                {
                    goTo(i,j);
                    foundIt = checkSensorFoundSun();
                }
            }
        }
        return foundIt;
    }
    
    /**
     * 
     */
    public boolean startTrackSun()
    {
        //declara o sol como perdido e tenta rastrear e apontar o sensor central
        boolean foundIt = false;
        ArrayList<Integer> sV = new ArrayList<Integer>();
        for(int i = 0; i < N_SENSORS; i++)
        {
            sV.add(i, sensors.get(i).getMeasurement());
            //System.out.println(sensorsValues.get(i));
        }
        if(sV.get(5) < sV.get(4) && sV.get(5) < sV.get(3) && sV.get(5) < sV.get(2) && sV.get(5) < sV.get(1))
        {
            if(sV.get(5) < sV.get(0) - CLOUD_DIFFERENCE)
            {
                //seta a posição do sol que foi encontrado
                setSunPosition();
                //declara o sol como achado
                foundIt = true;
                failuresCounter = 0;
                System.out.println("Sun was located! - Start Traking");
            }
            else
            {
                failure();
            }
        }
        else if(sV.get(1) < sV.get(2) && sV.get(1) < sV.get(3) && sV.get(1) < sV.get(4) && sV.get(1) < sV.get(5))
        {
            if(sV.get(1) >= sV.get(2) - LOW_DIFFERENCE_BETWEEN_SENSORS)
                goDown();
            else if(sV.get(1) >= sV.get(4) - LOW_DIFFERENCE_BETWEEN_SENSORS)
                goClock();
            else
            {
                goDown();
                goClock();
            }
        }
        else if(sV.get(2) < sV.get(1) && sV.get(2) < sV.get(3) && sV.get(2) < sV.get(4) && sV.get(2) < sV.get(5))
        {
            if(sV.get(2) >= sV.get(1) - LOW_DIFFERENCE_BETWEEN_SENSORS)
                goDown();
            else if(sV.get(2) >= sV.get(3) - LOW_DIFFERENCE_BETWEEN_SENSORS)
                goCounter();
            else
            {
                goDown();
                goCounter();
            }
        }
        else if(sV.get(3) < sV.get(1) && sV.get(3) < sV.get(2) && sV.get(3) < sV.get(4) && sV.get(3) < sV.get(5))
        {
            if(sV.get(3) >= sV.get(2) - LOW_DIFFERENCE_BETWEEN_SENSORS)
                goCounter();
            else if(sV.get(3) >= sV.get(4) - LOW_DIFFERENCE_BETWEEN_SENSORS)
                goUp();
            else
            {
                goUp();
                goCounter();
            }
        }
        else if(sV.get(4) < sV.get(1) && sV.get(4) < sV.get(2) && sV.get(4) < sV.get(3) && sV.get(4) < sV.get(5))
        {
            if(sV.get(4) >= sV.get(1) - LOW_DIFFERENCE_BETWEEN_SENSORS)
                goClock();
            else if(sV.get(4) >= sV.get(3) - LOW_DIFFERENCE_BETWEEN_SENSORS)
                goUp();
            else
            {
                goUp();
                goClock();
            }
        }
        //retorna o que será recebido por sunLocated
        return (foundIt);
    }
    
    //Método para mover as engines com só um comando e esperar o tempo necessario de cada passo.
    private void goTo(int azimuth, int zenith)
    {
        //calcula a quantidade de passos que vai dar
        azimuth = azimuth - engineA.getPosition();
        zenith = zenith - engineZ.getPosition();
        //move caso seja necessário
        if(azimuth != 0)
        {
            engineA.move(azimuth);
            //espera o movimento concluir
            sleep(TIME_WAITING_ENGINE*azimuth);
        }
        if(zenith != 0)
        {
            engineZ.move(zenith);
            sleep(TIME_WAITING_ENGINE*zenith);
        }
    }
    
    //movimenta o motor um passo no sentido descrito
    public void goUp()
    {
        if(engineZ.getPosition() < LIMIT_ZENITH)
        {
            engineZ.move(1);
            sleep(TIME_WAITING_ENGINE);
        }
    }
        
    //movimenta o motor um passo no sentido descrito
    public void goDown()
    {
        if(engineZ.getPosition() > 0)
        {
            engineZ.move(-1);
            sleep(TIME_WAITING_ENGINE);
        }
    }
        
    //movimenta o motor um passo no sentido descrito
    public void goClock()
    {
        if(engineA.getPosition() < LIMIT_AZIMUTH)
        {
            engineA.move(1);
            sleep(TIME_WAITING_ENGINE);
        }
    }
        
    //movimenta o motor um passo no sentido descrito
    public void goCounter()
    {
        if(engineZ.getPosition() > 0)
        {
            engineA.move(-1);
            sleep(TIME_WAITING_ENGINE);
        }
    }
        
    //Adormece um tempo time ms
    private void sleep(int time)
    {
        try {
            TimeUnit.MILLISECONDS.sleep(Math.abs(time));
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
    
    //Verifica se algum sensor esta apontado para o sol
    private boolean checkSensorFoundSun()
    {
        boolean foundIt = false;
        //Checa se algum sensor esta apontando para o sol, do 1 ao 5
        for(int i = 1; i < N_SENSORS && !foundIt; i++)
        {
            if(sensors.get(i).getMeasurement() < SUN_INTENSITY)
                 foundIt = true;
        }
        return foundIt;
    }
    
    //Grava a posição atual do sol
    private void setSunPosition()
    {
        lastSunPosition.setAzimuth(engineA.getPosition());
        lastSunPosition.setZenith(engineZ.getPosition());
    }
    
    //conta uma falha e determina quanto tempo o programa irá esperar até a proxima tentativa
    private void failure()
    {
        failuresCounter++;
        //Poucas falhas
        if(failuresCounter < LARGE_NUMBER_FAILURES)
        {
            sleep(WAITING_TIME_AFTER_FAILURE);
        }
        else
        {
            sleep(WAITING_TIME_AFTER_LOTS_FAILURE);
        }
    }
    
    /**
     * 
     */
    public boolean isTracking()
    {
        return true;
    }
    
    /**
     * 
     */
    public void colectData()
    {
        
    }
    
    /**
     * 
     */
    public void cyclincRun()
    {
    
    }
}
