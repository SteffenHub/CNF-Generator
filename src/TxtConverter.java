import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class takes care of converting the data to a format, that can be written to a file.
 * Or converting the read data from a file to the format, that can be used in the program.
 */
public class TxtConverter {

    /**
     * This method converts the given list of rules into a list of strings, which can be written to a file.
     * The format of each rule is a string of integers separated by spaces, where the last integer is 0.
     * The first line of the file contains the number of variables and the number of rules.
     * This method is used for the CNF problem with families.
     * It adds the actual variance and the actual number of false and true variables to the file.
     */
    public static List<String> convertRulesToStringListCNF(List<int[]> rules, InputData inputData, BigInteger variance,
                                                           int actualFalseVars, int actualTrueVars, long calculationTime) {
        List<String> fileOutput = new ArrayList<>();
        fileOutput.add("c ");
        fileOutput.add("c Input Variance: " + inputData.goalVariance);
        fileOutput.add("c Actual Variance: " + variance);
        fileOutput.add("c Input number of vars: " + inputData.numberOfVariables);
        fileOutput.add("c Input use Fam rules: " + inputData.useFamilies);
        fileOutput.add("c Input Fam size: " + inputData.minFamilySize + "-" + inputData.maxFamilySize);
        fileOutput.add("c Input Rule size: " + inputData.minRuleSize + "-" + inputData.maxRuleSize);
        fileOutput.add("c Input False Variables: " + inputData.falseVars);
        fileOutput.add("c Actual False Variables: " + actualFalseVars + " Vars");
        fileOutput.add("c Input True Variables: " + inputData.trueVars);
        fileOutput.add("c Actual True Variables: " + actualTrueVars + " Vars");
        fileOutput.add("c Calculation time: " + calculationTime/1000 + " seconds");
        fileOutput.add("c ");
        fileOutput.add("p cnf " + inputData.numberOfVariables + " " + rules.size());
        for (int[] rule : rules) {
            fileOutput.add(Arrays.toString(rule).replace("[", "").replace("]", "").replace(",", "") + " 0");
        }
        return fileOutput;
    }
}
