import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.math.BigInteger;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        InputData iD = new Dialog().startDialog();
        // All found Rules. Insert Family Rules
        List<int[]> rules = new ArrayList<>(iD.familyRules);

        BigInteger variance = iD.variance;
        SatSolver satSolver = new SatSolver(rules);
        int[] triedFalseTrueVars = new int[]{0, 0};

        boolean varianceReached = false;
        while (!varianceReached) {
            System.out.println("-----------------------------------------------------------------------------");
            int[] nextRule = getNextRule(satSolver, iD, triedFalseTrueVars, 400);

            System.out.println("Add next Rule to Solver: " + Arrays.toString(nextRule));
            rules.add(nextRule);

            // satSolver checks
            if (!handleIsSatifiableWithRuleAndAddRuleToSatSolver(satSolver, nextRule) ||
                    !handleAlwaysFalseVars(satSolver, iD.numberOfVariables, iD.falseVars) ||
                    !handleAlwaysTrueVars(satSolver, iD.numberOfVariables, iD.trueVars)) {
                rules.remove(nextRule);
                satSolver = new SatSolver(rules);
                continue;
            }

            variance = getVariance(rules, iD.numberOfVariables);
            if (variance.compareTo(new BigInteger("-1")) == 0) {
                System.err.println("Something went wrong with c2d when finding the variance. I am taking this rule out again");
                rules.remove(nextRule);
                satSolver = new SatSolver(rules);
                continue;
            }
            System.out.println("The variance with the new rule is now: " + Operation.pointsToBigInt(variance));
            System.out.println("Still missing: " + Operation.pointsToBigInt(variance.subtract(iD.goalVariance)));

            // If the variance is too small
            if (variance.compareTo(iD.goalVariance.subtract(iD.goalVarianceDeviation)) < 0) {
                System.err.println("that pushes the variance too hard I take the rule out again");
                rules.remove(nextRule);
                satSolver = new SatSolver(rules);
                continue;
            }
            // If an optimal Variance is reached -> Stop calculation
            if (variance.compareTo(iD.goalVariance.subtract(iD.goalVarianceDeviation)) > 0 && variance.compareTo(iD.goalVariance.add(iD.goalVarianceDeviation)) < 0) {
                System.out.println("I have found a set of rules that fulfills the variance");
                varianceReached = true;
            }
            System.out.println("->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->");
            System.out.println("\nWe take these!\n");
            System.out.println("->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->");
        }

        // save to file
        List<String> fileOutput = TxtConverter.convertRulesToStringListCNF(rules, iD, variance, SolverUsages.getAlwaysFalseVars(satSolver,
                iD.numberOfVariables).length, SolverUsages.getAlwaysTrueVars(satSolver, iD.numberOfVariables).length);
        for (String line : fileOutput) {
            System.out.println(line);
        }
        TxtReaderWriter.writeListOfStrings("cnfBuilder" + iD.numberOfVariables + "Vars" + "Variance" + variance + ".txt", fileOutput);
    }

    public static int[] getNextRule(SatSolver satSolver, InputData iD, int[] triedFalseTrueVars, int breakTries) throws TimeoutException {
        Random rand = new Random();
        int[] trueVars = SolverUsages.getAlwaysTrueVars(satSolver, iD.numberOfVariables);
        if (iD.trueVars > trueVars.length) {
            ++triedFalseTrueVars[1];
            int[] possibleTrueVars = SolverUsages.getVarsCouldBeTrue(satSolver, iD.numberOfVariables);
            int index = rand.nextInt(possibleTrueVars.length);
            // TODO If the last rules is chosen iD.trueVars will be too small in output cnf
            if (triedFalseTrueVars[1] >= breakTries) {
                triedFalseTrueVars[1] = 0;
                --iD.trueVars;
                if (iD.trueVars < 0) {
                    iD.trueVars = 0;
                }
            }
            return new int[]{possibleTrueVars[index]};
        }
        int[] falseVars = SolverUsages.getAlwaysFalseVars(satSolver, iD.numberOfVariables);
        if (iD.falseVars > falseVars.length) {
            ++triedFalseTrueVars[0];
            int[] possibleFalseVars = SolverUsages.getVarsCouldBeFalse(satSolver, iD.numberOfVariables);
            int index = rand.nextInt(possibleFalseVars.length);
            // TODO If the last rules is chosen iD.falseVars will be too small in output cnf
            if (triedFalseTrueVars[0] >= breakTries) {
                triedFalseTrueVars[0] = 0;
                --iD.falseVars;
                if (iD.falseVars < 0) {
                    iD.falseVars = 0;
                }
            }
            return new int[]{-possibleFalseVars[index]};
        } else {
            int[] newRule = new int[iD.minRuleSize + rand.nextInt(iD.maxRuleSize + 1 - iD.minRuleSize)];
            for (int i = 0; i < newRule.length; i++) {
                int newVar = -1;
                while (newVar == -1 || Operation.isIn(falseVars, newVar) || Operation.isIn(trueVars, newVar)) {
                    newVar = rand.nextInt(iD.numberOfVariables);
                }
                newRule[i] = -(newVar + 1);
            }
            return newRule;
        }
    }

    /**
     * Own function to add the rule, because we habe to catch a contradiction exception
     */
    public static boolean handleIsSatifiableWithRuleAndAddRuleToSatSolver(SatSolver satSolver, int[] nextRule) throws TimeoutException {
        if (!satSolver.isSatisfiableWithClause(nextRule)) {
            System.err.println("This rule creates a contradiction, I'll take it out again");
            return false;
        }
        try {
            satSolver.addRule(nextRule);
        } catch (ContradictionException e) {
            // should not happen, because we check before with isSatisfiableWithClause()
            return false;
        }
        return true;
    }

    public static boolean handleAlwaysFalseVars(SatSolver satSolver, int numberOfVariables, int goalFalseVars) throws TimeoutException {
        int falseVarsNow = SolverUsages.getAlwaysFalseVars(satSolver, numberOfVariables).length;
        if (!(falseVarsNow <= goalFalseVars)) {
            System.err.println("This rule would exclude too many variables and make it no longer possible to select them: " + falseVarsNow + "/" + goalFalseVars);
            return false;
        }
        return true;
    }

    public static boolean handleAlwaysTrueVars(SatSolver satSolver, int numberOfVariables, int goalTrueVars) throws TimeoutException {
        int trueVarsNow = SolverUsages.getAlwaysTrueVars(satSolver, numberOfVariables).length;
        if (!(trueVarsNow <= goalTrueVars)) {
            System.err.println("This rule would make too many variables always true and force the selection of this variable: " + trueVarsNow + "/" + goalTrueVars);
            return false;
        }
        return true;
    }

    public static BigInteger getVariance(List<int[]> allRules, int numberOfVariables) {
        try {
            return Operation.getVariance(allRules, numberOfVariables);
        } catch (Exception e) {
            return new BigInteger("-1");
        }
    }
}