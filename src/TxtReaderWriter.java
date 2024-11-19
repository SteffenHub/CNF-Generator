/**
 * @author: SteffenHub (https://github.com/SteffenHub)
 */
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * This class takes care of writing and reading txt files
 */
public class TxtReaderWriter {

    /**
     * default constructor
     */
    public TxtReaderWriter(){}

    /**
     * This method writes a list of strings to a file. Each String is written in a new line.
     *
     * @param nameOfFileWithEnding The name of the file with the ending. For example: "result.cnf"
     * @param contentList The list of strings to be written to the file.
     * @param printConfirmation true if there should be a confirmation in the console that the file was saved
     * @throws IOException If the file cannot be written.
     */
    public static void writeArrayOfStrings(String nameOfFileWithEnding, String[] contentList, boolean printConfirmation) throws IOException {
        FileWriter fw = new FileWriter("./" + nameOfFileWithEnding, StandardCharsets.UTF_8);
        BufferedWriter writer = new BufferedWriter(fw);
        for (String line : contentList) {
            writer.append(line);
            writer.newLine();
        }
        writer.close();
        if (printConfirmation) {
            System.out.println("The File '" + nameOfFileWithEnding + "' was saved in the same folder");
        }
    }
}
