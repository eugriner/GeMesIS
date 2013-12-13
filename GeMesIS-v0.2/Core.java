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
    private volatile int WAITING_TIME_AFTER_FAILURE = 100;
    // Tempo de espera depois de um numero grande de falhas LARGE_NUMBER_OF_FAILURES
    private volatile int WAITING_TIME_AFTER_LOTS_FAILURE = 400;
    // Numero grande de falhas
    private volatile int LARGE_NUMBER_FAILURES = 20;
    // Tempo de espera entre gravaçoes bem sucedidas no Datalog
    private volatile int TIME_BETWEEN_LOGWRITE = 180000;
    // Numero de tentativas de re-achar o sol
    private volatile int TIMES_TO_TRY = 30;
    
    // Motores
    // Limite de passos na horizontal
    private volatile int LIMIT_AZIMUTH = 24;
    // Limite de passos na vertical
    private volatile int LIMIT_ZENITH = 24;
    // Tempo de espera entre cada comando para os motores em MILLISECONDS
    private volatile int TIME_WAITING_ENGINE = 1;
    
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
        failuresCounter = 0;
        sunTraked = false;
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
            //primeira execução, ou perdeu e nao re-achou
            if(!sunLocated && !sunTraked)
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
            //caso o sol tenha sido achado, tenta rastrear
            else if (sunLocated)
            {
                //rastreia  o sol depois de o ter achado
                if(!sunTraked)
                {
                    System.out.println("Sun was located! - Start Traking");
                }
                sunLocated = startTrackSun();
                sunTraked = true;
            }
            ///caso o sol tenha sido perdio tenta re-achar
            else if (!sunLocated && sunTraked)
            {
                System.out.println("Sun has been lost - Trying to find it again...");
                sunLocated = findItAgain();
                sunTraked = false;
            }
        }
    }
    
    /**
     * 
     */
    public void stop()
    {
        isOn = false;
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
        boolean foundIt;
        ArrayList<Integer> sV = new ArrayList<Integer>();
        int tried = 0;
        do
        {
            foundIt = false;
            for(int i = 0; i < N_SENSORS; i++)
            {
                sV.add(i, sensors.get(i).getMeasurement());
                //System.out.println(sensorsValues.get(i));
            }
            //se o sensor do meio for maior faz a medida caso não tenha novem na frente
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
            //
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
            tried++;
        } while(!foundIt || tried <= TIMES_TO_TRY);
        //retorna o que será recebido por sunLocated
        return foundIt;
    }
    
    private boolean findItAgain()
    {
        boolean foundIt = false;
        int time = 0;
        //procura o sol perto do ponto onde ele foi perdido
        do
        {
            //faz um caracol tentando achar o sol, caso ache para
            for(int i = 0; i < time && !foundIt; i++)
            {
                goUp();
                foundIt = checkSensorFoundSun();
            }
            for(int i = 0; i < time && !foundIt; i++)
            {
                goClock();
                foundIt = checkSensorFoundSun();
            }
            for(int i = 0; i < (time * 2) && !foundIt; i++)
            {
                goDown();
                foundIt = checkSensorFoundSun();
            }
            for(int i = 0; i < time*2 && !foundIt; i++)
            {
                goCounter();
                foundIt = checkSensorFoundSun();
            }
            //sai do laço se achar o sol, ou se tentou muito
            time++;
        } while(!foundIt || (TIMES_TO_TRY/4) > time);
        
        return foundIt;
    }
    
    //Método para mover as engines com só um comando e esperar o tempo necessario de cada passo.
    public void goTo(int azimuth, int zenith)
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
        return sunTraked;
    }
    
    /**
     * 
     */
    public void colectData()
    {
        
    }
    
    public int getTIME_BETWEEN_MESUREMENT() {
		return TIME_BETWEEN_MESUREMENT;
	}
	
	public void setTIME_BETWEEN_MESUREMENT(int tIME_BETWEEN_MESUREMENT) {
		TIME_BETWEEN_MESUREMENT = tIME_BETWEEN_MESUREMENT;
	}
	
	public int getWAITING_TIME_AFTER_FAILURE() {
		return WAITING_TIME_AFTER_FAILURE;
	}
	
	public void setWAITING_TIME_AFTER_FAILURE(int wAITING_TIME_AFTER_FAILURE) {
		WAITING_TIME_AFTER_FAILURE = wAITING_TIME_AFTER_FAILURE;
	}
	
	public int getWAITING_TIME_AFTER_LOTS_FAILURE() {
		return WAITING_TIME_AFTER_LOTS_FAILURE;
	}
	
	public void setWAITING_TIME_AFTER_LOTS_FAILURE(int wAITING_TIME_AFTER_LOTS_FAILURE) {
		WAITING_TIME_AFTER_LOTS_FAILURE = wAITING_TIME_AFTER_LOTS_FAILURE;
	}
	
	public int getLARGE_NUMBER_FAILURES() {
		return LARGE_NUMBER_FAILURES;
	}
	
	public void setLARGE_NUMBER_FAILURES(int lARGE_NUMBER_FAILURES) {
		LARGE_NUMBER_FAILURES = lARGE_NUMBER_FAILURES;
	}
	
	public int getTIME_BETWEEN_LOGWRITE() {
		return TIME_BETWEEN_LOGWRITE;
	}
	
	public void setTIME_BETWEEN_LOGWRITE(int tIME_BETWEEN_LOGWRITE) {
		TIME_BETWEEN_LOGWRITE = tIME_BETWEEN_LOGWRITE;
	}
	
	public int getTIMES_TO_TRY() {
		return TIMES_TO_TRY;
	}
	
	public void setTIMES_TO_TRY(int tIMES_TO_TRY) {
		TIMES_TO_TRY = tIMES_TO_TRY;
	}
	
	public int getLIMIT_AZIMUTH() {
		return LIMIT_AZIMUTH;
	}
	
	public void setLIMIT_AZIMUTH(int lIMIT_AZIMUTH) {
		LIMIT_AZIMUTH = lIMIT_AZIMUTH;
	}
	
	public int getLIMIT_ZENITH() {
		return LIMIT_ZENITH;
	}
	
	public void setLIMIT_ZENITH(int lIMIT_ZENITH) {
		LIMIT_ZENITH = lIMIT_ZENITH;
	}
	
	public int getTIME_WAITING_ENGINE() {
		return TIME_WAITING_ENGINE;
	}
	
	public void setTIME_WAITING_ENGINE(int tIME_WAITING_ENGINE) {
		TIME_WAITING_ENGINE = tIME_WAITING_ENGINE;
	}
	
	public int getN_SENSORS() {
		return N_SENSORS;
	}
	
	public void setN_SENSORS(int n_SENSORS) {
		N_SENSORS = n_SENSORS;
	}
	
	public int getSUN_INTENSITY() {
		return SUN_INTENSITY;
	}
	
	public void setSUN_INTENSITY(int sUN_INTENSITY) {
		SUN_INTENSITY = sUN_INTENSITY;
	}
	
	public int getCLOUD_DIFFERENCE() {
		return CLOUD_DIFFERENCE;
	}
	
	public void setCLOUD_DIFFERENCE(int cLOUD_DIFFERENCE) {
		CLOUD_DIFFERENCE = cLOUD_DIFFERENCE;
	}
	
	public int getLOW_DIFFERENCE_BETWEEN_SENSORS() {
		return LOW_DIFFERENCE_BETWEEN_SENSORS;
	}
	
	public void setLOW_DIFFERENCE_BETWEEN_SENSORS(int lOW_DIFFERENCE_BETWEEN_SENSORS) {
		LOW_DIFFERENCE_BETWEEN_SENSORS = lOW_DIFFERENCE_BETWEEN_SENSORS;
	}
}
