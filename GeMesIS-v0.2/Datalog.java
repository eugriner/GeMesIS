import java.io.BufferedWriter;
import java.io.FileWriter; 
import java.io.File; 
import java.util.Calendar;
import java.text.SimpleDateFormat;

/**
 * 
 * @author  Hanoch Griner and Tiago Tobias
 * @version 28/11/2013
 */
public class Datalog
{
    public static void writeLog(String log) {
        BufferedWriter writer = null;
        try {
            //create a temporary file
            String fileName = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            fileName += ".txt";
            File logFile = new File(fileName);
            // This will output the full path where the file will be written to...
            System.out.println(logFile.getCanonicalPath());
            //true serve para não sobrescrever o log, e sim inserir no já existente, caso ele exista.
            writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.write(log);
            writer.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
    }
}