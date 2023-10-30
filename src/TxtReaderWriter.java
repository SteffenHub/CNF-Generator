import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class takes care of writing and reading txt files
 */
public class TxtReaderWriter {

    /**
     * Reads a file and outputs the contents to a list of strings.
     *
     * @param nameOfFileWithEnding full name with file extension z.B.: "rules.txt"
     * @return Contents of the specified file
     */
    public static List<String> getTxtFromSamePath(String nameOfFileWithEnding) throws FileNotFoundException {
        List<String> txtContent = new ArrayList<>();
        File myObj = new File(nameOfFileWithEnding);
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
            //Save content line by line
            txtContent.add(myReader.nextLine());
        }
        myReader.close();
        return txtContent;
    }

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
