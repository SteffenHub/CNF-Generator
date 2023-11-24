import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class Main {

    /**
     * The main method of the application. It initiates a dialog to gather input data, constructs a set of rules
     * based on the data, and then iteratively tries to find a set of rules that meet the specified variance criteria.
     * During each iteration, it attempts to add a new rule and checks if the new rule set meets the criteria,
     * including the number of always true/false variables and the target variance. If a rule does not meet the
     * criteria, it is discarded, and a new rule is tried. The process continues until the target variance is achieved.
     */
    public static void main(String[] args) throws ContradictionException, IOException, TimeoutException {
        InputData iD = new Dialog().startDialog();
        // All found Rules. Insert Family Rules
        List<int[]> rules = new ArrayList<>(iD.familyRules);

        BigInteger variance = iD.variance;
        SatSolver satSolver = new SatSolver(rules);
        int[] triedFalseTrueVars = new int[]{0, 0};

        boolean varianceReached = false;
        while (!varianceReached) {
            System.out.println("-----------------------------------------------------------------------------");
            int[] nextRule;
            try {
                nextRule = getNextRule(satSolver, iD, triedFalseTrueVars, 400);
            }catch(TimeoutException e){
                System.err.println("Error during find next potential rule.");
                continue;
            }
            if (Operation.isIn(rules, nextRule)){
                System.out.println(Arrays.toString(nextRule));
                System.err.println("Rule already exists.");
                continue;
            }

            System.out.println("Add next Rule to Solver: " + Arrays.toString(nextRule));
            rules.add(nextRule);
            satSolver = new SatSolver(rules);

            // satSolver checks
            try {
                if (!handleIsSatisfiableWithRuleAndAddRuleToSatSolver(satSolver, nextRule) ||
                        !handleAlwaysFalseVars(satSolver, iD.numberOfVariables, iD.falseVars) ||
                        !handleAlwaysTrueVars(satSolver, iD.numberOfVariables, iD.trueVars)) {
                    rules.remove(nextRule);
                    continue;
                }
            }catch(TimeoutException e){
                System.err.println("Sat Solver calculation failed. Timeout. I am taking this rule out again");
                rules.remove(nextRule);
                continue;
            }

            variance = getVariance(rules, iD.numberOfVariables);
            if (variance.compareTo(new BigInteger("-1")) == 0) {
                System.err.println("Something went wrong with c2d when finding the variance. I am taking this rule out again");
                rules.remove(nextRule);
                continue;
            }
            System.out.println("The variance with the new rule is now: " + Operation.pointsToBigInt(variance));
            System.out.println("Still missing: " + Operation.pointsToBigInt(variance.subtract(iD.goalVariance)));

            // If the variance is too small
            if (variance.compareTo(iD.goalVariance.subtract(iD.goalVarianceDeviation)) < 0) {
                System.err.println("that pushes the variance too hard I take the rule out again");
                rules.remove(nextRule);
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

    /**
     * Generates the next rule to be tested. It tries to balance the number of always true/false
     * variables against the target value in 'iD' first.
     * If the target is not met, it randomly generates a new rule containing one variable which
     * should be always true or false. Example: [-3] or [76]
     * If number of always true/false variables is met, this function will generate the next rule
     * containing negated random chosen variables, which aren't always true/false by now.
     * Example: [-7, -34, -92]
     *
     * @param satSolver The SAT solver instance to get always true/false variables.
     * @param iD The input data object containing parameters for rule generation.
     * @param triedFalseTrueVars An array tracking the number of attempts for finding true/false variables.
     * @param breakTries The number of tries for finding always true/false rules. After which the method will
     *                   decrement the input goal for always true/false variables.
     * @return An array representing the next rule to be added to the solver.
     * @throws TimeoutException If the SAT solver calculation takes too long.
     */
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
     * Checks if adding a given rule to the SAT solver creates a contradiction. If no contradiction is found,
     * the rule is added to the solver. The method returns a boolean indicating whether the rule was successfully
     * added without causing a contradiction.
     *
     * @param satSolver The SAT solver instance to which the rule will be added.
     * @param nextRule The rule to be added to the SAT solver.
     * @return True if the rule is added successfully; false if the rule creates a contradiction.
     * @throws TimeoutException If the SAT solver calculation takes too long.
     */
    public static boolean handleIsSatisfiableWithRuleAndAddRuleToSatSolver(SatSolver satSolver, int[] nextRule) throws TimeoutException {
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

    /**
     * Evaluates whether the current set of rules in the SAT solver meets the target for the number of variables
     * that are always false. If the number of always false variables exceeds the goal, the method returns false.
     *
     * @param satSolver The SAT solver instance to evaluate.
     * @param numberOfVariables The total number of variables in consideration.
     * @param goalFalseVars The target number of variables that should always be false.
     * @return True if the goal for always false variables is met or under the limit; false otherwise.
     * @throws TimeoutException If the SAT solver calculation takes too long.
     */
    public static boolean handleAlwaysFalseVars(SatSolver satSolver, int numberOfVariables, int goalFalseVars) throws TimeoutException {
        int falseVarsNow = SolverUsages.getAlwaysFalseVars(satSolver, numberOfVariables).length;
        if (!(falseVarsNow <= goalFalseVars)) {
            System.err.println("This rule would exclude too many variables and make it no longer possible to select them: " + falseVarsNow + "/" + goalFalseVars);
            return false;
        }
        return true;
    }

    /**
     * Evaluates whether the current set of rules in the SAT solver meets the target for the number of variables
     * that are always true. If the number of always true variables exceeds the goal, the method returns false.
     *
     * @param satSolver The SAT solver instance to evaluate.
     * @param numberOfVariables The total number of variables in consideration.
     * @param goalTrueVars The target number of variables that should always be true.
     * @return True if the goal for always true variables is met or under the limit; false otherwise.
     * @throws TimeoutException If the SAT solver calculation takes too long.
     */
    public static boolean handleAlwaysTrueVars(SatSolver satSolver, int numberOfVariables, int goalTrueVars) throws TimeoutException {
        int trueVarsNow = SolverUsages.getAlwaysTrueVars(satSolver, numberOfVariables).length;
        if (!(trueVarsNow <= goalTrueVars)) {
            System.err.println("This rule would make too many variables always true and force the selection of this variable: " + trueVarsNow + "/" + goalTrueVars);
            return false;
        }
        return true;
    }

    /**
     * Computes the variance for a given set of SAT problem rules. This method applies the rules, represented as
     * integer arrays, to teh c2d solver and calculates the variance based on the number of variables. In case of
     * an error during computation, a default error value is returned (BigInteger(-1)).
     *
     * @param allRules The list of rules for the SAT problem, with each rule as an array of integers.
     * @param numberOfVariables The total number of variables in the SAT problem.
     * @return The variance as a BigInteger, or a default error value if the calculation fails.
     */
    public static BigInteger getVariance(List<int[]> allRules, int numberOfVariables) {
        try {
            return Operation.getVariance(allRules, numberOfVariables);
        } catch (Exception e) {
            return new BigInteger("-1");
        }
    }
}