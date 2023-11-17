import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.math.BigInteger;
import java.util.*;

public class Dialog {

    private Scanner scanner;


    public InputData startDialog() throws TimeoutException {
        this.scanner = new Scanner(System.in);
        InputData iD = new InputData();
        iD.numberOfVariables = this.askHowManyVariables();
        iD.useFamilies = this.askWantFamilies();
        iD.familyRules = new ArrayList<>();
        if (iD.useFamilies) {
            int[] familySize = this.askForFamilySize();
            iD.minFamilySize = familySize[0];
            iD.maxFamilySize = familySize[1];
            iD.familyRules = this.generateAndPrintFamilyRules(iD.numberOfVariables, iD.minFamilySize, iD.maxFamilySize);
        }else{
            // Add all variables as rule [var1 or not var1] to rules,so the solver knew all variables
            for (int i = 1; i <= iD.numberOfVariables; i++) {
                iD.familyRules.add(new int[]{i, -i});
            }
        }
        iD.variance = this.getAndPrintVariance(iD.familyRules, iD.numberOfVariables, iD.minFamilySize, iD.maxFamilySize);
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

    private int askHowManyVariables(){
        System.out.println("How many Variables should exist?");
        return Integer.parseInt(this.scanner.nextLine());
    }

    private int[] askForFamilySize(){
        System.out.println("How large may families be min-max. Example: 1-4");
        String minMaxFamSizeInput = this.scanner.nextLine();
        String[] split = minMaxFamSizeInput.split("-");
        int minFamSize = Integer.parseInt(split[0]);
        int maxFamSize = Integer.parseInt(split[1]);
        return new int[]{minFamSize, maxFamSize};
    }

    private List<int[]> generateAndPrintFamilyRules(int numberOfVariables, int minFamSize, int maxFamSize){
        List<int[]> familyRules = new ArrayList<>();
        Random rand = new Random();
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
        for (int[] famRegel : familyRules) {
            System.out.println(Arrays.toString(famRegel));
        }
        System.out.println("These were the families!");
        return familyRules;
    }

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

    private BigInteger getAndPrintVariance(List<int[]> familyRules, int numberOfVariables, int minFamilySize, int maxFamilySize){
        try {
            BigInteger variance = Operation.getVariance(familyRules, numberOfVariables);
            System.out.println("Only the (family) rules result in a variance of: " + variance);
            System.out.println("respectively");
            System.out.println(Operation.pointsToBigInt(variance));
            return variance;
        }catch(Exception e){
            System.err.println("failed to find variance. Build new familyRules");
            familyRules = generateAndPrintFamilyRules(numberOfVariables, minFamilySize, maxFamilySize);
            return getAndPrintVariance(familyRules, numberOfVariables, minFamilySize, maxFamilySize);
        }
    }

    private BigInteger[] askForGoalVariance(){
        System.out.println("What variance should be maintained, with the other rules added independently of the families?");
        System.out.println("Write WITHOUT points. The result can deviate up to 5% from the variance entered.");
        BigInteger goalVariance = new BigInteger(this.scanner.nextLine());
        BigInteger possibleDeviation = Operation.calculatePercentage(goalVariance, 5);
        System.out.println("Maximum deviation in the result: " + possibleDeviation);
        return new BigInteger[]{goalVariance, possibleDeviation};
    }

    private int[] askForRuleSize(){
        System.out.println("what lengths should the other family independent rules have. e.g. (2-6)");
        System.out.println("You should start with min Rule size = 2. If rule size is 1 the variable in this rule will be always false");
        String minMaxRuleSizeInput = scanner.nextLine();
        String[] split = minMaxRuleSizeInput.split("-");
        int minRuleSize = Integer.parseInt(split[0]);
        int maxRuleSize = Integer.parseInt(split[1]);
        return new int[]{minRuleSize, maxRuleSize};
    }

    private int askForFalseVars(SatSolver satSolver, int numberOfVariables) throws TimeoutException {
        System.out.println("What percentage of the variables should be False. Slice as decimal number. e.g. 0.11 or 0.04");
        System.out.println("Now there are " + SolverUsages.getAlwaysFalseVars(satSolver, numberOfVariables).length + " False Vars in rule set");
        double percentageFalseVarsDouble = Double.parseDouble(scanner.nextLine());
        int falseVars = (int) (numberOfVariables * percentageFalseVarsDouble);
        System.out.println("This results in " + falseVars + " variables that should always be False");
        return falseVars;
    }

    private int askForTrueVars(SatSolver satSolver, int numberOfVariables) throws TimeoutException {
        System.out.println("What percentage of the variables should be True. Slice as decimal number. e.g. 0.09");
        System.out.println("Now there are " + SolverUsages.getAlwaysTrueVars(satSolver, numberOfVariables).length + " True Vars in rule set");
        double percentageTrueVarsDouble = Double.parseDouble(scanner.nextLine());
        int trueVars = (int) (numberOfVariables * percentageTrueVarsDouble);
        System.out.println("This results in " + trueVars + " variables that should always be True");
        return trueVars;
    }
}
