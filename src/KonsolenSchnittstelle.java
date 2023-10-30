import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Die Schnittstelle zur Konsole.
 * Hier koennen Befehle in der Konsole ausgefuehrt werden
 */
public class KonsolenSchnittstelle {

    /**
     * Fuehrt den angegeben Befehl in der Konsole aus.
     *
     * @param cmdString der Befehl z.B.:"c2d -in R.cnf"
     * @return
     */
    public static String[] konsolenEingabe(String cmdString) throws InterruptedException, IOException {
        Process proc = Runtime.getRuntime().exec(cmdString);

        InputStream inputStream = proc.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        List<String> outputLines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            outputLines.add(line);
        }
        proc.waitFor();
        return outputLines.toArray(new String[0]);
    }
}