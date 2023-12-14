import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * This class takes care of writing and reading txt files
 */
public class TxtReaderWriter {

    public static void writeListOfStrings(String nameOfFileWithEnding, List<String> contentList) throws IOException {
        FileWriter fw = new FileWriter("./" + nameOfFileWithEnding, StandardCharsets.UTF_8);
        BufferedWriter writer = new BufferedWriter(fw);
        for (String line : contentList) {
            writer.append(line);
            writer.newLine();
        }
        writer.close();
        System.out.println("The File '" + nameOfFileWithEnding + "' was saved in the same folder");
    }


    public static void writeArrayOfStrings(String nameOfFileWithEnding, String[] contentList) throws IOException {
        FileWriter fw = new FileWriter("./" + nameOfFileWithEnding, StandardCharsets.UTF_8);
        BufferedWriter writer = new BufferedWriter(fw);
        for (String line : contentList) {
            writer.append(line);
            writer.newLine();
        }
        writer.close();
        //System.out.println("The File '" + nameOfFileWithEnding + "' was saved in the same folder");
    }
}
