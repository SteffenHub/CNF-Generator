import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.math.BigInteger;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        InputData iD = new Dialog().startDialog();
        // All found Rules. Insert Family Rules
        List<int[]> rules = new ArrayList<>(iD.familyRules);

        Random rand = new Random();
        BigInteger varianz = Operation.getVariance(rules, iD.numberOfVariables);
        SatSolver satSolver = new SatSolver(rules);
        int triedFalseVars = 0;
        int triedTrueVars = 0;
        boolean machWeiter = true;
        while (machWeiter) {
            if (triedTrueVars > 400) {
                triedTrueVars = 0;
                --iD.trueVars;
                if (iD.trueVars < 0) {
                    iD.trueVars = 0;
                }
            }
            if (triedFalseVars > 400) {
                triedFalseVars = 0;
                --iD.falseVars;
                if (iD.falseVars < 0) {
                    iD.falseVars = 0;
                }
            }
            System.out.println("-----------------------------------------------------------------------------");
            boolean wirdWiederGeloescht = false;
            int[] neueRegel;
            if (iD.trueVars > countTrueVars(satSolver, iD.numberOfVariables)) {
                ++triedTrueVars;
                int[] possibleTrueVars = getPossibleTrueVars(satSolver, iD.numberOfVariables);
                int index = rand.nextInt(possibleTrueVars.length);
                neueRegel = new int[]{possibleTrueVars[index]};
            } else if (iD.falseVars > countFalseVars(satSolver, iD.numberOfVariables)) {
                ++triedFalseVars;
                int[] possibleFalseVars = getPossibleFalseVars(satSolver, iD.numberOfVariables);
                int index = rand.nextInt(possibleFalseVars.length);
                neueRegel = new int[]{-possibleFalseVars[index]};
            } else {
                int naechsteRegelSize = iD.minRuleSize + rand.nextInt(iD.maxRuleSize + 1 - iD.minRuleSize);
                neueRegel = new int[naechsteRegelSize];
                for (int i = 0; i < naechsteRegelSize; i++) {
                    int neuePR = rand.nextInt(iD.numberOfVariables);
                    neueRegel[i] = -(neuePR + 1);
                }
            }
            System.out.println("Fuege Regel hinzu: " + Arrays.toString(neueRegel));
            rules.add(neueRegel);

            if (!checkForSatisfibiltiy(satSolver, neueRegel)) {
                System.err.println("Diese Regel erzeugt eine Kontradiktion, ich nehme sie wieder raus");
                rules.remove(neueRegel);
                satSolver = new SatSolver(rules);
                continue;
            }

            int falseVarsBisJetzt = countFalseVars(satSolver, iD.numberOfVariables);
            if (!(falseVarsBisJetzt <= iD.falseVars)) {
                System.err.println("Diese Regel würde zu viele variablen ausschließen und nicht mehr möglich machen diese zu wählen: " + falseVarsBisJetzt + "/" + iD.falseVars);
                rules.remove(neueRegel);
                satSolver = new SatSolver(rules);
                continue;
            }
            int trueBisJetzt = countTrueVars(satSolver, iD.numberOfVariables);
            if (!(trueBisJetzt <= iD.trueVars)) {
                System.err.println("Diese Regel würde zu viele variablen immer wahr machen: " + trueBisJetzt + "/" + iD.trueVars);
                rules.remove(neueRegel);
                satSolver = new SatSolver(rules);
                continue;
            }
            varianz = getVariance(rules, iD.numberOfVariables);
            if (varianz.compareTo(new BigInteger("-1")) == 0) {
                System.err.println("Something went wrong with c2d when finding the variance. I am taking this rule out again");
                rules.remove(neueRegel);
                satSolver = new SatSolver(rules);
                continue;
            }
            System.out.println("The variance with the rules is now: " + Operation.punkteInBigInt(varianz));
            System.out.println("Still missing: " + Operation.punkteInBigInt(varianz.subtract(iD.goalVariance)));
            // If the variance is too small
            if (varianz.compareTo(iD.goalVariance.subtract(iD.goalVarianceDeviation)) < 0) {
                System.err.println("das drückt die Varianz zu doll ich nehme die Regel wieder raus");
                rules.remove(neueRegel);
                satSolver = new SatSolver(rules);
                continue;
            }
            // If an optimal Variance is reached -> Stop calculation
            if (varianz.compareTo(iD.goalVariance.subtract(iD.goalVarianceDeviation)) > 0 && varianz.compareTo(iD.goalVariance.add(iD.goalVarianceDeviation)) < 0) {
                System.out.println("Ich habe ein Regelwerk gefunden, dass die Varianz erfüllt");
                machWeiter = false;
            }
            System.out.println("->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->");
            System.out.println();
            System.out.println("Die nehmen wir");
            System.out.println();
            System.out.println("->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->");
            System.out.println("-----------------------------------------------------------------------------");
        }

        List<String> fileOutput = TxtConverter.convertRulesToStringListCNF(rules, iD, varianz, countFalseVars(satSolver,
                iD.numberOfVariables), countTrueVars(satSolver, iD.numberOfVariables));
        for (String line : fileOutput) {
            System.out.println(line);
        }
        TxtReaderWriter.writeListOfStrings("cnfBuilder" + iD.numberOfVariables + "Vars" + "Variance" + varianz + ".txt", fileOutput);
    }

    public static BigInteger getVariance(List<int[]> allRules, int numberOfVariables) {
        try {
            return Operation.getVariance(allRules, numberOfVariables);
        } catch (Exception e) {
            return new BigInteger("-1");
        }
    }

    /**
     * Tries to add the new Rule to the SatSolver
     *
     * @param satSolver The SatSolver filled with all previous rules
     * @param newRule   The new Rule that should be testet
     * @return true if rule is added and satisfiable. False if adding Rule ends in a Contradiction
     */
    public static boolean checkForSatisfibiltiy(SatSolver satSolver, int[] newRule) {
        try {
            satSolver.addRule(newRule);
            return true;
        } catch (ContradictionException e) {
            return false;
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