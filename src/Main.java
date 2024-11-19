/**
 * @author: SteffenHub (https://github.com/SteffenHub)
 */
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.io.IOException;
import java.util.List;

/**
 * The program entry point
 */
public class Main {

    /**
     * default constructor
     */
    public Main(){}

    /**
     * The main method of the application. It initiates a dialog to gather input data, constructs a set of rules
     * based on the data, and then iteratively tries to find a set of rules that meet the specified variance criteria.
     * During each iteration, it attempts to add a new rule and checks if the new rule set meets the criteria,
     * including the number of always true/false variables and the target variance. If a rule does not meet the
     * criteria, it is discarded, and a new rule is tried. The process continues until the target variance is achieved.
     * @param args not used
     * @throws TimeoutException if the SatSolver max calculation time reached
     * @throws ContradictionException if there is a contradiction in the created cnf
     * @throws IOException error when reading ir writing files
     */
    public static void main(String[] args) throws ContradictionException, IOException, TimeoutException {
        InputData iD = new Dialog().startDialog();

        List<String> resultFile = new Generator().startGenerator(iD);

        for (String line : resultFile) {
            System.out.println(line);
        }
        // save to file
        TxtReaderWriter.writeArrayOfStrings(resultFile.get(1).split("File Name: ")[1], resultFile.toArray(new String[0]), true);
        System.out.println("There is a file 'tmp_counting_input.cnf' in the project folder, containing the same output as the result. You can delete this file, if you want.");
        // TODO delete tmp_counting_input.cnf automatically
    }
}
