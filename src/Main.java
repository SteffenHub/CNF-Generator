import org.sat4j.specs.TimeoutException;

import java.math.BigInteger;
import java.util.*;

public class Main {

    public static int[] getNextRule(SatSolver satSolver, InputData iD, int[] triedFalseTrueVars) throws TimeoutException {
        int[] newRule;
        Random rand = new Random();
        if (iD.trueVars > countTrueVars(satSolver, iD.numberOfVariables)) {
            ++triedFalseTrueVars[1];
            int[] possibleTrueVars = getPossibleTrueVars(satSolver, iD.numberOfVariables);
            int index = rand.nextInt(possibleTrueVars.length);
            newRule = new int[]{possibleTrueVars[index]};
        } else if (iD.falseVars > countFalseVars(satSolver, iD.numberOfVariables)) {
            ++triedFalseTrueVars[0];
            int[] possibleFalseVars = getPossibleFalseVars(satSolver, iD.numberOfVariables);
            int index = rand.nextInt(possibleFalseVars.length);
            newRule = new int[]{-possibleFalseVars[index]};
        } else {
            // TODO don't use already determined True False Vars
            newRule = new int[iD.minRuleSize + rand.nextInt(iD.maxRuleSize + 1 - iD.minRuleSize)];
            for (int i = 0; i < newRule.length; i++) {
                int neuePR = rand.nextInt(iD.numberOfVariables);
                newRule[i] = -(neuePR + 1);
            }
        }
        return newRule;
    }

    public static void main(String[] args) throws Exception {
        InputData iD = new Dialog().startDialog();
        // All found Rules. Insert Family Rules
        List<int[]> rules = new ArrayList<>(iD.familyRules);

        BigInteger variance = iD.variance;
        SatSolver satSolver = new SatSolver(rules);
        int[] triedFalseTrueVars = new int[]{0, 0};
        boolean varianceReached = false;
        while (!varianceReached) {
            if (triedFalseTrueVars[1] >= 400) {
                triedFalseTrueVars[1] = 0;
                --iD.trueVars;
                if (iD.trueVars < 0) {
                    iD.trueVars = 0;
                }
            }
            if (triedFalseTrueVars[0] >= 400) {
                triedFalseTrueVars[0] = 0;
                --iD.falseVars;
                if (iD.falseVars < 0) {
                    iD.falseVars = 0;
                }
            }
            System.out.println("-----------------------------------------------------------------------------");
            int[] nextRule = getNextRule(satSolver, iD, triedFalseTrueVars);

            System.out.println("Add next Rule to Solver: " + Arrays.toString(nextRule));
            rules.add(nextRule);

            if (!satSolver.isSatisfiableWithClause(nextRule)) {
                System.err.println("This rule creates a contradiction, I'll take it out again");
                rules.remove(nextRule);
                continue;
            }
            satSolver.addRule(nextRule);

            int falseVarsNow = countFalseVars(satSolver, iD.numberOfVariables);
            if (!(falseVarsNow <= iD.falseVars)) {
                System.err.println("This rule would exclude too many variables and make it no longer possible to select them: " + falseVarsNow + "/" + iD.falseVars);
                rules.remove(nextRule);
                satSolver = new SatSolver(rules);
                continue;
            }
            int trueBisJetzt = countTrueVars(satSolver, iD.numberOfVariables);
            if (!(trueBisJetzt <= iD.trueVars)) {
                System.err.println("This rule would make too many variables always true and force the selection of this variable: " + trueBisJetzt + "/" + iD.trueVars);
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
            System.out.println();
            System.out.println("We take these!");
            System.out.println();
            System.out.println("->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->");
            System.out.println("-----------------------------------------------------------------------------");
        }

        List<String> fileOutput = TxtConverter.convertRulesToStringListCNF(rules, iD, variance, countFalseVars(satSolver,
                iD.numberOfVariables), countTrueVars(satSolver, iD.numberOfVariables));
        for (String line : fileOutput) {
            System.out.println(line);
        }
        TxtReaderWriter.writeListOfStrings("cnfBuilder" + iD.numberOfVariables + "Vars" + "Variance" + variance + ".txt", fileOutput);
    }

    public static BigInteger getVariance(List<int[]> allRules, int numberOfVariables) {
        try {
            return Operation.getVariance(allRules, numberOfVariables);
        } catch (Exception e) {
            return new BigInteger("-1");
        }
    }

    public static int countFalseVars(SatSolver satSolver, int anzahlVariablen) throws TimeoutException {
        int result = 0;
        for (int i = 0; i < anzahlVariablen; i++) {
            if (!satSolver.isSatisfiableWith(i + 1) && satSolver.isSatisfiableWith(-(i + 1))) {
                //immer false
                ++result;
            }
        }
        return result;
    }

    public static int countTrueVars(SatSolver satSolver, int anzahlVariablen) throws TimeoutException {
        int result = 0;
        for (int i = 0; i < anzahlVariablen; i++) {
            if (!satSolver.isSatisfiableWith(-(i + 1)) && satSolver.isSatisfiableWith(i + 1)) {
                // immer true
                ++result;
            }
        }
        return result;
    }

    public static int[] getPossibleTrueVars(SatSolver satSolver, int anzahlVariablen) throws TimeoutException {
        List<Integer> result = new ArrayList<>();
        for (int var = 1; var <= anzahlVariablen; var++) {
            if (satSolver.isSatisfiableWith(var)) {
                result.add(var);
            }
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    public static int[] getPossibleFalseVars(SatSolver satSolver, int anzahlVariablen) throws TimeoutException {
        List<Integer> result = new ArrayList<>();
        for (int var = 1; var <= anzahlVariablen; var++) {
            if (satSolver.isSatisfiableWith(-var)) {
                result.add(var);
            }
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

}