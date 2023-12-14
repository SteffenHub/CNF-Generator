import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TxtConverter {

    public static boolean[][] listOfStringOrdersToBooleanArray(List<String> ordersStringList) {
        boolean[][] ordersBool = new boolean[ordersStringList.size()][ordersStringList.get(0).split(",").length];
        for (int i = 0; i < ordersStringList.size(); i++) {
            String[] split = ordersStringList.get(i).split(",");
            boolean[] order = new boolean[split.length];
            for (int j = 0; j < split.length; j++) {
                if (split[j].equals("0")) {
                    order[j] = false;
                } else if (split[j].equals("1")) {
                    order[j] = true;
                } else {
                    throw new Error("The Orders can't be read. Found an entry that don't match '0' or '1'");
                }
            }
            ordersBool[i] = order;
        }
        return ordersBool;
    }

    /**
     * Transforms a cnf of strings to int arrays.
     * The first line is omitted if the CNF description is there e.g.: "p cnf 234 32"
     * Additionally the last element of each line is omitted to avoid the 0's at the end,
     * e.g.: "-50 3 0" becomes new int[]{-50,3}
     *
     * @param cnfStr the previously read cnf as a list of strings
     * @return the CNF as list of int arrays, without first line and zero in each line
     */
    public static List<int[]> stringListToListOfIntArrays(List<String> cnfStr) {
        ArrayList<int[]> cnfArr = new ArrayList<>();
        //go through cnf
        for (int i = 0; i < cnfStr.size(); i++) {
            if (cnfStr.get(i).charAt(0) == 'c' || cnfStr.get(i).charAt(0) == 'p') continue;
            //split line on blank
            String[] aufgeteilt = cnfStr.get(i).split(" ");
            //Reserve memory. Decrement by 1 to ignore the 0 at the end of the line.
            int[] klausel = new int[aufgeteilt.length - 1];
            for (int x = 0; x < klausel.length; x++) {
                klausel[x] = Integer.parseInt(aufgeteilt[x]);
            }
            cnfArr.add(klausel);
        }
        return cnfArr;
    }

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