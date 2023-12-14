import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TxtConverter {

    public static List<String> convertRulesToStringListCNF(List<int[]> rules, InputData inputData, BigInteger variance,
                                                           int actualFalseVars, int actualTrueVars) {
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
        fileOutput.add("c ");
        fileOutput.add("p cnf " + inputData.numberOfVariables + " " + rules.size());
        for (int[] rule : rules) {
            fileOutput.add(Arrays.toString(rule).replace("[", "").replace("]", "").replace(",", "") + " 0");
        }
        return fileOutput;
    }
}