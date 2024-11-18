import org.sat4j.specs.TimeoutException;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to provide some Usages with the Sat Solver
 */
public class SolverUsages {

    /**
     * default constructor
     */
    public SolverUsages(){}

    /**
     * Checks if there is a logical implication between the two given boolean variables.
     * An implication exists when the truth of (condition and consequence) enforces the falsity of (condition and not consequence).
     * For example the rule: (a -&gt; b) which is logically equivalent to (NOT(a) or b). If we pass condition = a and consequence = b true will be returned.
     * Note that if the consequence is always true(determined) and the condition can be true is always a conclusion. For example,
     * we use consequence = b and b is determined and is always true, and we use condition = a and the variable provided as condition
     * is not determined then true will be returned.
     *
     * @param condition   condition as any variable. Variable can be given positively or negatively(-3)
     * @param consequence consequence as any variable. Variable can be given positively or negatively(-3)
     * @param satSolver   the Boolean satisfiability solver to use
     * @return true if an implication exists, false otherwise
     * @throws TimeoutException if the Boolean satisfiability solver takes too long to respond. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     */
    public static boolean isLogicalConclusion(int condition, int consequence, SatSolver satSolver) throws TimeoutException {
        return satSolver.isSatisfiableWithConjunct(new int[]{condition, consequence}) &&
                !satSolver.isSatisfiableWithConjunct(new int[]{condition, -consequence});
    }

    /**
     * Checks if there is a hard implication between the two given boolean variables.
     * A hard implication exists when the truth of (condition and consequence) enforces the falsity of (condition and not consequence)
     * and the consequence is not already determined as false or true.
     * For example the rule: (a -&gt; b) which is logically equivalent to (NOT(a) or b). If NOT(b) is satisfiable,
     * and we pass condition = a and consequence = b true will be returned.
     * Note that this is not the logical conclusion, because checking NOT(b) is satisfiable is not required to be a logical conclusion.
     *
     * @param condition   condition as any variable. Variable can be given positively or negatively(-3)
     * @param consequence consequence as any variable. Variable can be given positively or negatively(-3)
     * @param satSolver   the Boolean satisfiability solver to use
     * @return true if a hard implication exists, false otherwise
     * @throws TimeoutException if the Boolean satisfiability solver takes too long to respond. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     */
    public static boolean isHardConclusion(int condition, int consequence, SatSolver satSolver) throws TimeoutException {
        return satSolver.isSatisfiableWith(-consequence) &&
                satSolver.isSatisfiableWithConjunct(new int[]{condition, consequence}) &&
                !satSolver.isSatisfiableWithConjunct(new int[]{condition, -consequence});
    }

    /**
     * Returns an array of variables that are already determined in the given SAT Solver instance.
     * Note that the vars inside the rule set of the solver have to be ascending. So it should not happen, that the vars
     * inside the rule set are like 1,3,4 and 2 isn't defined.
     *
     * @param satSolver the SAT solver instance to check for determined variables
     * @return an array of vars that are determined positively or negatively
     * @throws TimeoutException if the Boolean satisfiability solver takes too long to respond. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     */
    public static int[] getDeterminedVars(SatSolver satSolver) throws TimeoutException {
        List<Integer> determined = new ArrayList<>();
        for (int i = 1; i <= satSolver.getHighestVar(); i++) {
            boolean satWithI = satSolver.isSatisfiableWith(i);
            // if this var isn't free to choose between positively or negatively
            if (!(satWithI && satSolver.isSatisfiableWith(-i)))
                // find out if this var have to be positively or negatively
                determined.add(satWithI ? i : -i);
        }
        return determined.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Finds all pairs of variables in the given SAT solver that are logically equivalent (or interchangeable).
     * We have to proof all Possibilities of two variables. To be equivalent, the conditions are:
     * 00 is true
     * 01 is false
     * 10 is false
     * 11 is true
     *
     * @param satSolver the SAT solver to check for variable equivalence
     * @return a 2D array of integer pairs, where each pair represents two equivalent variables
     * @throws TimeoutException if the Boolean satisfiability solver takes too long to respond. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     */
    public static int[][] findEqualVars(SatSolver satSolver) throws TimeoutException {
        List<int[]> equalVars = new ArrayList<>();
        for (int var1 = 1, cycle = 1; var1 <= satSolver.getHighestVar(); var1++) {
            //start with var1 +1. So you don't have to look twice like: 1,2 and 2,1. +1 for 1,1 or 2,2 ...
            for (int var2 = var1 + 1; var2 <= satSolver.getHighestVar(); var2++, cycle++) {
                if (satSolver.isSatisfiableWithConjunct(new int[]{var1, var2}) &&
                        satSolver.isSatisfiableWithConjunct(new int[]{-var1, -var2}) &&
                        !satSolver.isSatisfiableWithConjunct(new int[]{var1, -var2}) &&
                        !satSolver.isSatisfiableWithConjunct(new int[]{-var1, var2})
                ) {
                    equalVars.add(new int[]{var1, var2});
                }
                System.out.println(cycle + "/" + (satSolver.getHighestVar() * (satSolver.getHighestVar() - 1) / 2));
            }
        }
        return equalVars.toArray(new int[0][]);
    }

    /**
     * Determines the variables that are always false in a given SAT (Boolean satisfiability problem) instance.
     * This method iterates over all variables from 1 to 'numberOfVariables'. For each variable, it checks if
     * the SAT problem is unsatisfiable when the variable is true and satisfiable when the variable is false.
     * If both conditions are met, the variable is deemed to be always false.
     *
     * @param satSolver An instance of SatSolver, which is used to check the satisfiability of the problem.
     * @param numberOfVariables The number of variables in the SAT problem. This method will check each variable
     *                        from 1 to this number.
     * @return An array of integers, where each integer represents a variable that is always false in the
     *         given SAT problem.
     * @throws TimeoutException If the satisfiability check takes too long.
     */
    public static int[] getAlwaysFalseVars(SatSolver satSolver, int numberOfVariables) throws TimeoutException {
        List<Integer> result = new ArrayList<>();
        for (int var = 1; var <= numberOfVariables; var++) {
            if (!satSolver.isSatisfiableWith(var) && satSolver.isSatisfiableWith(-var)) {
                // always false
                result.add(var);
            }
        }
        return result.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Determines the variables that are always true in a given SAT (Boolean satisfiability problem) instance.
     * This method iterates over all variables from 1 to 'numberOfVariables'. For each variable, it checks if
     * the SAT problem is unsatisfiable when the variable is false and satisfiable when the variable is true.
     * If both conditions are met, the variable is deemed to be always true.
     *
     * @param satSolver An instance of SatSolver, which is used to check the satisfiability of the problem.
     * @param numberOfVariables The number of variables in the SAT problem. This method will check each variable
     *                        from 1 to this number.
     * @return An array of integers, where each integer represents a variable that is always true in the
     *         given SAT problem.
     * @throws TimeoutException If the satisfiability check takes too long.
     */
    public static int[] getAlwaysTrueVars(SatSolver satSolver, int numberOfVariables) throws TimeoutException {
        List<Integer> result = new ArrayList<>();
        for (int var = 1; var <= numberOfVariables; var++) {
            if (!satSolver.isSatisfiableWith(-var) && satSolver.isSatisfiableWith(var)) {
                // always true
                result.add(var);
            }
        }
        return result.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Identifies variables that could be true in a given SAT instance.
     * This method iterates through each variable from 1 to 'numberOfVariables'.
     * It checks if the SAT problem remains satisfiable when each variable is set to true. If so, the variable
     * is considered as one that could be true and is added to the result list.
     *
     * @param satSolver An instance of SatSolver, which is used to evaluate the satisfiability of the problem
     *                  with specific variable assignments.
     * @param numberOfVariables The total number of variables in the SAT problem. This method evaluates each
     *                          variable from 1 to this number to determine if it could be true.
     * @return An array of integers, each representing a variable that could be true in the SAT problem. The
     *         variables are identified by their respective integer representations.
     * @throws TimeoutException If the satisfiability check takes too long.
     */
    public static int[] getVarsCouldBeTrue(SatSolver satSolver, int numberOfVariables) throws TimeoutException {
        List<Integer> result = new ArrayList<>();
        for (int var = 1; var <= numberOfVariables; var++) {
            if (satSolver.isSatisfiableWith(var)) {
                result.add(var);
            }
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Identifies variables that could be false in a given SAT (Boolean satisfiability problem) instance.
     * This method iterates through each variable from 1 to 'numberOfVariables'.
     * It checks if the SAT problem remains satisfiable when each variable is set to false (negated). If the problem
     * is still satisfiable with the variable negated, it indicates that the variable could potentially be false
     * and is added to the result list.
     *
     * @param satSolver An instance of SatSolver, which is used to evaluate the satisfiability of the problem
     *                  with specific variable assignments (negated in this case).
     * @param numberOfVariables The total number of variables in the SAT problem. This method evaluates each
     *                          variable from 1 to this number to determine if it could be false.
     * @return An array of integers, each representing a variable that could be false in the SAT problem. The
     *         variables are identified by their respective integer representations.
     * @throws TimeoutException If the satisfiability check takes too long.
     */
    public static int[] getVarsCouldBeFalse(SatSolver satSolver, int numberOfVariables) throws TimeoutException {
        List<Integer> result = new ArrayList<>();
        for (int var = 1; var <= numberOfVariables; var++) {
            if (satSolver.isSatisfiableWith(-var)) {
                result.add(var);
            }
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }
}