import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * The interface to the console.
 * Commands can be executed in the console here
 */
public class ConsoleInterface {

    /**
     * runs the specified command in the console
     *
     * @param cmdString the command for example: "c2d -in R.cnf"
     * @return the output of the command
     */
    public static String[] consoleInput(String cmdString) throws InterruptedException, IOException {
        Process process = Runtime.getRuntime().exec(cmdString);

        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        List<String> outputLines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            outputLines.add(line);
        }
        process.waitFor();
        return outputLines.toArray(new String[0]);
    }
}
