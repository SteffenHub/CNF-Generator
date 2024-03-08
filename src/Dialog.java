import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.math.BigInteger;
import java.util.*;

/**
 * The Dialog class provides the dialog with the user.
 * The user is asked for the input data for the CNF problem.
 */
public class Dialog {

    /**
     * The scanner for the user input.
     */
    private Scanner scanner;

    /**
     * Starts the dialog with the user.
     * The user is asked for the input data for the CNF problem.
     *
     * @return The input data for the CNF problem.
     * @throws TimeoutException If the solver times out.
     */
    public InputData startDialog() throws TimeoutException {
        this.scanner = new Scanner(System.in);
        InputData iD = new InputData();
        iD.seed = this.askForSeed();
        iD.randomGenerator = new Random(iD.seed);
        iD.countSolver = this.askForCountSolver();
        iD.numberOfVariables = this.askHowManyVariables();
        iD.useFamilies = this.askWantFamilies();
        iD.familyRules = new ArrayList<>();
        if (iD.useFamilies) {
            int[] familySize = this.askForFamilySize();
            iD.minFamilySize = familySize[0];
            iD.maxFamilySize = familySize[1];
            iD.familyRules = this.generateAndPrintFamilyRules(iD.numberOfVariables, iD.minFamilySize, iD.maxFamilySize, iD.randomGenerator);
        }else{
            // Add all variables as rule [var1 or not var1] to rules,so the solver knew all variables
            for (int i = 1; i <= iD.numberOfVariables; i++) {
                iD.familyRules.add(new int[]{i, -i});
            }
        }
        iD.variance = this.getAndPrintVariance(iD.familyRules, iD.numberOfVariables, iD.minFamilySize, iD.maxFamilySize, iD.countSolver, iD.randomGenerator);
        BigInteger[] goalVarianceAndDeviation = this.askForGoalVariance();
        iD.goalVariance = goalVarianceAndDeviation[0];
        iD.goalVarianceDeviation = goalVarianceAndDeviation[1];
        int[] rulesSize = this.askForRuleSize();
        iD.minRuleSize = rulesSize[0];
        iD.maxRuleSize = rulesSize[1];
        try {
            SatSolver satSolver = new SatSolver(iD.familyRules);
            iD.falseVars = this.askForFalseVars(satSolver, iD.numberOfVariables);
            iD.trueVars = this.askForTrueVars(satSolver, iD.numberOfVariables);
        }catch (ContradictionException e){
            System.err.println("failed to find always true/false vars. Please start Dialog again.");
            return startDialog();
        }
        this.scanner.close();
        return iD;
    }

    private long askForSeed(){
        System.out.println("Do you want to use a specific seed for the random generator?");
        System.out.println("Example: '698234689'. If you don't want to use a seed type 'None'");
        System.out.println("If 'None' is chosen a random seed on the basis of current time and date will be used");
        String line = this.scanner.nextLine();
        if (line.equals("None")) {
            Random rand = new Random();
            return rand.nextLong();
        }
        try{
            return Long.parseLong(line);
        }catch(Exception e){
            System.out.println("Invalid input! Example: '698234689'. If you don't want to use a seed type 'None'");
        }
        return this.askForSeed();
    }

    private String askForCountSolver(){
        System.out.println("Which counting solver do you want to use. Choose between 'c2d' or 'sharpSAT'.");
        String line = this.scanner.nextLine();
        if (line.equals("c2d")){
            return line;
        } else if (line.equals("sharpSAT")) {
            return line;
        }else{
            System.err.println("Unknown solver for counting: " + line + " Choose between 'c2d' or 'sharpSAT'." );
            return askForCountSolver();
        }
    }

    /**
     * Asks the user if he wants to use family rules.
     *
     * @return True if the user wants to use family rules, false otherwise.
     */
    private boolean askWantFamilies(){
        System.out.println("Do you want to use family rules? type: 'true' or 'false'");
        String line = this.scanner.nextLine();
        if (line.equals("true")){
            return true;
        } else if (line.equals("false")) {
            return false;
        }else{
            System.err.println("I did not understand the input. Please type: 'true' or 'false'");
            return askWantFamilies();
        }
    }

    /**
     * Asks the user for the number of variables in the CNF problem.
     *
     * @return The number of variables in the CNF problem.
     */
    private int askHowManyVariables(){
        System.out.println("How many Variables should exist?");
        return Integer.parseInt(this.scanner.nextLine());
    }

    /**
     * Asks the user for the minimum and maximum size of the families.
     *
     * @return An array containing the minimum and maximum size of the families. Example: [1, 4] = 1-4
     */
    private int[] askForFamilySize(){
        System.out.println("How large may families be min-max. Example: 1-4");
        String minMaxFamSizeInput = this.scanner.nextLine();
        String[] split = minMaxFamSizeInput.split("-");
        int minFamSize = Integer.parseInt(split[0]);
        int maxFamSize = Integer.parseInt(split[1]);
        return new int[]{minFamSize, maxFamSize};
    }

    /**
     * Generates the family rules for the CNF problem and prints them to the console.
     * The size of the families are randomly determined in range minFamSize to maxFamSize.
     * The actual family rules are generated by calling the buildFamilyRules method.
     *
     * @param numberOfVariables The number of variables in the CNF problem.
     * @param minFamSize The minimum size of a family.
     * @param maxFamSize The maximum size of a family.
     * @return A list of family rules.
     */
    private List<int[]> generateAndPrintFamilyRules(int numberOfVariables, int minFamSize, int maxFamSize, Random rand){
        List<int[]> familyRules = new ArrayList<>();
        int varRun = 0;
        while (varRun < numberOfVariables) {
            int famSize = minFamSize + rand.nextInt(maxFamSize + 1 - minFamSize);

            if (varRun + famSize > numberOfVariables) {
                famSize = numberOfVariables - varRun;
            }
            int[] newFamRule = new int[famSize];
            int innerCount = 0;
            for (int i = varRun; i < varRun + famSize; i++) {
                newFamRule[innerCount] = i + 1;
                ++innerCount;
            }
            varRun = varRun + famSize;
            familyRules.addAll(buildFamilyRules(newFamRule));
        }
        for (int[] famRule : familyRules) {
            System.out.println(Arrays.toString(famRule));
        }
        System.out.println("These were the families!");
        return familyRules;
    }

    /**
     * Generates the family rules for a given family.
     * The family rules are generated by combining the variables of the family with each other.
     * For example, if the family is [1, 2, 3], the family rules are [[1, 2, 3], [-1, -2], [-1, -3], [-2, -3]].
     * Represents an XOR constraint between the variables of the family.
     *
     * @param family The family for which the family rules should be generated.
     * @return A list of family rules.
     */
    private List<int[]> buildFamilyRules(int[] family) {
        List<int[]> result = new ArrayList<>();
        result.add(family);
        for (int i = 0; i < family.length; i++) {
            for (int j = i + 1; j < family.length; j++) {
                result.add(new int[]{-family[i], -family[j]});
            }
        }
        return result;
    }

    /**
     * Calculates the variance of the family rules and prints it to the console.
     * If the variance cannot be calculated, new family rules are generated and the method is called again.
     *
     * @param familyRules The family rules.
     * @param numberOfVariables The number of variables in the CNF problem.
     * @param minFamilySize The minimum size of a family.
     * @param maxFamilySize The maximum size of a family.
     * @return The variance of the family rules as BigInteger.
     */
    private BigInteger getAndPrintVariance(List<int[]> familyRules, int numberOfVariables, int minFamilySize, int maxFamilySize, String countSolver, Random rand){
        try {
            BigInteger variance = Operation.getVariance(countSolver, familyRules, numberOfVariables);
            System.out.println("Only the (family) rules result in a variance of: " + variance);
            System.out.println("respectively");
            System.out.println(Operation.pointsToBigInt(variance));
            return variance;
        }catch(Exception e){
            System.out.println(e.toString());
            System.err.println("failed to find variance. Build new familyRules");
            familyRules = generateAndPrintFamilyRules(numberOfVariables, minFamilySize, maxFamilySize, rand);
            return getAndPrintVariance(familyRules, numberOfVariables, minFamilySize, maxFamilySize, countSolver, rand);
        }
    }

    /**
     * Asks the user for the variance that should be maintained, with the other rules added independently of the families.
     *
     * @return The User input for goal variance as BigInteger.
     */
    private BigInteger[] askForGoalVariance(){
        System.out.println("What variance should be maintained, with the other rules added independently of the families?");
        System.out.println("Write WITHOUT points. The result can deviate up to 5% from the variance entered.");
        BigInteger goalVariance = new BigInteger(this.scanner.nextLine());
        BigInteger possibleDeviation = Operation.calculatePercentage(goalVariance, 5);
        System.out.println("Maximum deviation in the result: " + possibleDeviation);
        return new BigInteger[]{goalVariance, possibleDeviation};
    }

    /**
     * Asks the user for the minimum and maximum size of the other rules.
     *
     * @return An array containing the minimum and maximum size of the other rules.
     */
    private int[] askForRuleSize(){
        System.out.println("what lengths should the other family independent rules have. e.g. (2-6)");
        System.out.println("You should start with min Rule size = 2. If rule size is 1 the variable in this rule will be always false");
        String minMaxRuleSizeInput = scanner.nextLine();
        String[] split = minMaxRuleSizeInput.split("-");
        int minRuleSize = Integer.parseInt(split[0]);
        int maxRuleSize = Integer.parseInt(split[1]);
        return new int[]{minRuleSize, maxRuleSize};
    }

    /**
     * Asks the user for the percentage of variables that should be always false.
     *
     * @param satSolver The SatSolver object
     * @param numberOfVariables The number of variables in the CNF problem.
     * @return The number of variables that should be always false.
     * @throws TimeoutException If the solver times out.
     */
    private int askForFalseVars(SatSolver satSolver, int numberOfVariables) throws TimeoutException {
        System.out.println("What percentage of the variables should be False. Slice as decimal number. e.g. 0.11 or 0.04");
        System.out.println("Now there are " + SolverUsages.getAlwaysFalseVars(satSolver, numberOfVariables).length + " False Vars in rule set");
        double percentageFalseVarsDouble = Double.parseDouble(scanner.nextLine());
        int falseVars = (int) (numberOfVariables * percentageFalseVarsDouble);
        System.out.println("This results in " + falseVars + " variables that should always be False");
        return falseVars;
    }

    /**
     * Asks the user for the percentage of variables that should be always true.
     *
     * @param satSolver The SatSolver object
     * @param numberOfVariables The number of variables in the CNF problem.
     * @return The number of variables that should be always true.
     * @throws TimeoutException If the solver times out.
     */
    private int askForTrueVars(SatSolver satSolver, int numberOfVariables) throws TimeoutException {
        System.out.println("What percentage of the variables should be True. Slice as decimal number. e.g. 0.09");
        System.out.println("Now there are " + SolverUsages.getAlwaysTrueVars(satSolver, numberOfVariables).length + " True Vars in rule set");
        double percentageTrueVarsDouble = Double.parseDouble(scanner.nextLine());
        int trueVars = (int) (numberOfVariables * percentageTrueVarsDouble);
        System.out.println("This results in " + trueVars + " variables that should always be True");
        return trueVars;
    }
}
