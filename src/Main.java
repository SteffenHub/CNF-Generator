import org.sat4j.specs.TimeoutException;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        List<int[]> regeln = new ArrayList<>();


        Scanner scanner = new Scanner(System.in);

        System.out.println("Wie viele PR-Nummern soll es geben.");
        int anzahlPRNummern = Integer.parseInt(scanner.nextLine());

        System.out.println("wie groß dürfen Familien min-max sein. Bsp: 1-4");
        String minMaxFamSizeInput = scanner.nextLine();
        String[] split = minMaxFamSizeInput.split("-");
        int minFamSize = Integer.parseInt(split[0]);
        int maxFamSize = Integer.parseInt(split[1]);

        Random zufallsgenerator = new Random();
        int prLauf = 0;
        while (prLauf < anzahlPRNummern) {
            int famGroesse = minFamSize + zufallsgenerator.nextInt(maxFamSize + 1 - minFamSize);

            if (prLauf + famGroesse > anzahlPRNummern) {
                famGroesse = anzahlPRNummern - prLauf;
            }
            int[] neueFamRegeln = new int[famGroesse];
            int innerCount = 0;
            for (int i = prLauf; i < prLauf + famGroesse; i++) {
                neueFamRegeln[innerCount] = i + 1;
                ++innerCount;
            }
            prLauf = prLauf + famGroesse;
            regeln.addAll(buildfamilyRules(neueFamRegeln));
        }
        for (int[] famRegel : regeln) {
            System.out.println(Arrays.toString(famRegel));
        }
        System.out.println("Das waren die Familien.");
        System.out.println("Nur die Familien Regeln ergeben eine Varianz von:");
        BigInteger varianz = Operation.getVariance(regeln, anzahlPRNummern);
        System.out.println(varianz);
        System.out.println("bzw.");
        System.out.println(Operation.punkteInBigInt(varianz));
        System.out.println("Welche Varianz soll beibehalten werden, mit den weiteren Regeln, die unabhaengig von den Familien hinzugefuegt werden");
        System.out.println("Schreibe OHNE Punkte. Das ergebnis kann bis zu 5% von der eingegebenen Varianz abweichen");
        BigInteger sollHabenVarianz = new BigInteger(scanner.nextLine());
        BigInteger moeglicheAbweichung = Operation.calculatePercentage(sollHabenVarianz, 5);
        System.out.println("maximale Abweichung im Ergebnis: " + moeglicheAbweichung);

        System.out.println("welche laengen sollen die anderen Familien unabhaengigen Regeln haben. z.B. (1-3)");
        String minMaxRuleSizeInput = scanner.nextLine();
        split = minMaxRuleSizeInput.split("-");
        int minRuleSize = Integer.parseInt(split[0]);
        int maxRuleSize = Integer.parseInt(split[1]);

        System.out.println("Wie viel Prozent der Variablen dürfen False sein. Scheibe als dezimalzahl. z.B. 0.11 oder 0.04");
        System.out.println("Nach dieser Eingabe Frage ich nach den True Variablen");
        double percentageFalseVarsDouble = Double.parseDouble(scanner.nextLine());
        int maximumFalseVars = (int) (anzahlPRNummern * percentageFalseVarsDouble);
        System.out.println("Das ergibt " + maximumFalseVars + " Variablen, die immer False sein dürfen");

        System.out.println("Wie viel Prozent der Variablen dürfen True sein. Scheibe als dezimalzahl. z.B. 0.09 ");
        double percentageTrueVarsDouble = Double.parseDouble(scanner.nextLine());
        int maximumTrueVars = (int) (anzahlPRNummern * percentageTrueVarsDouble);
        System.out.println("Das ergibt " + maximumTrueVars + " Variablen, die immer True sein dürfen");

        SatSolver satSolver = new SatSolver(regeln);
        int triedFalseVars = 0;
        int triedTrueVars = 0;
        boolean machWeiter = true;
        while (machWeiter) {
            if (triedTrueVars > 400){
                triedTrueVars = 0;
                --maximumTrueVars;
                if (maximumTrueVars < 0){
                    maximumTrueVars = 0;
                }
            }
            if (triedFalseVars > 400){
                triedFalseVars = 0;
                --maximumFalseVars;
                if (maximumFalseVars < 0){
                    maximumFalseVars = 0;
                }
            }
            System.out.println("-----------------------------------------------------------------------------");
            boolean wirdWiederGeloescht = false;
            int[] neueRegel;
            if (maximumTrueVars > countTrueVars(satSolver,anzahlPRNummern)) {
                ++triedTrueVars;
                int[] possibleTrueVars = getPossibleTrueVars(satSolver,anzahlPRNummern);
                int index = zufallsgenerator.nextInt(possibleTrueVars.length);
                neueRegel = new int[]{possibleTrueVars[index]};
            } else if (maximumFalseVars > countFalseVars(satSolver,anzahlPRNummern)){
                ++triedFalseVars;
                int[] possibleFalseVars = getPossibleFalseVars(satSolver,anzahlPRNummern);
                int index = zufallsgenerator.nextInt(possibleFalseVars.length);
                neueRegel = new int[]{-possibleFalseVars[index]};
            }else {
                int naechsteRegelSize = minRuleSize + zufallsgenerator.nextInt(maxRuleSize + 1 - minRuleSize);
                neueRegel = new int[naechsteRegelSize];
                for (int i = 0; i < naechsteRegelSize; i++) {
                    int neuePR = zufallsgenerator.nextInt(anzahlPRNummern);
                    neueRegel[i] = -(neuePR + 1);
                }
            }
            System.out.println("Fuege Regel hinzu: " + Arrays.toString(neueRegel));
            regeln.add(neueRegel);
            try {
                satSolver.addRule(neueRegel);
            } catch (Exception e) {
                System.err.println("Diese Regel erzeugt eine Kontradiktion, ich nehme sie wieder raus");
                regeln.remove(neueRegel);
                satSolver = new SatSolver(regeln);
                continue;
            }
            int falseVarsBisJetzt = countFalseVars(satSolver, anzahlPRNummern);
            if (!(falseVarsBisJetzt <= maximumFalseVars)) {
                System.err.println("Diese Regel würde zu viele variablen ausschließen und nicht mehr möglich machen diese zu wählen: " + falseVarsBisJetzt + "/" + maximumFalseVars);
                regeln.remove(neueRegel);
                satSolver = new SatSolver(regeln);
                continue;
            }
            int trueBisJetzt = countTrueVars(satSolver, anzahlPRNummern);
            if (!(trueBisJetzt <= maximumTrueVars)) {
                System.err.println("Diese Regel würde zu viele variablen immer wahr machen: " + trueBisJetzt + "/" + maximumTrueVars);
                regeln.remove(neueRegel);
                satSolver = new SatSolver(regeln);
                continue;
            }
            try {
                varianz = Operation.getVariance(regeln, anzahlPRNummern);
                System.out.println("Die Varianz mit der Regeln ist nun: " + Operation.punkteInBigInt(varianz));
                System.out.println("Es Fehlen noch: " + Operation.punkteInBigInt(varianz.subtract(sollHabenVarianz)));
                if (varianz.compareTo(sollHabenVarianz.subtract(moeglicheAbweichung)) < 0) {
                    System.err.println("das drückt die Varianz zu doll ich nehme die Regel wieder raus");
                    regeln.remove(neueRegel);
                    satSolver = new SatSolver(regeln);
                    wirdWiederGeloescht = true;
                } else if (varianz.compareTo(sollHabenVarianz.subtract(moeglicheAbweichung)) > 0 && varianz.compareTo(sollHabenVarianz.add(moeglicheAbweichung)) < 0) {
                    System.out.println("Ich habe ein Regelwerk gefunden, dass die Varianz erfüllt");
                    machWeiter = false;
                }
            } catch (Exception e) {
                System.err.println("Beim finden der Varianz ist etwas beim c2d schief gelaufen. Ich nehme diese und die vorherige Regel wieder raus");
                regeln.remove(neueRegel);
                regeln.remove(regeln.size() - 1);
                satSolver = new SatSolver(regeln);
                wirdWiederGeloescht = true;
            }
            if (!wirdWiederGeloescht) {
                System.out.println("->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->");
                System.out.println();
                System.out.println("Die nehmen wir");
                System.out.println();
                System.out.println("->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->->");
            }
            System.out.println("-----------------------------------------------------------------------------");
        }

        List<String> fileOutput = new ArrayList<>();
        fileOutput.add("c ");
        fileOutput.add("c Input Variance: " + sollHabenVarianz);
        fileOutput.add("c Actual Variance: " + varianz);
        fileOutput.add("c Input number of vars : " + anzahlPRNummern);
        fileOutput.add("c Input Fam size: " + minMaxFamSizeInput);
        fileOutput.add("c Input Rule size: " + minMaxRuleSizeInput);
        fileOutput.add("c Input False Variables: " + percentageFalseVarsDouble * 100 + " %");
        fileOutput.add("c Actual False Variables: " + countFalseVars(satSolver, anzahlPRNummern) + " Vars");
        fileOutput.add("c Input True Variables: " + percentageTrueVarsDouble * 100 + " %");
        fileOutput.add("c Actual True Variables: " + countTrueVars(satSolver, anzahlPRNummern) + " Vars");
        fileOutput.add("c ");
        fileOutput.add("p cnf " + anzahlPRNummern + " " + regeln.size());
        for (int[] regel : regeln) {
            fileOutput.add(Arrays.toString(regel).replace("[", "").replace("]", "").replace(",", "") + " 0");
        }
        for (String line : fileOutput) {
            System.out.println(line);
        }
        TxtReaderWriter.writeListOfStrings("cnfBuilder" + anzahlPRNummern + "Vars" + "Variance" + varianz + ".txt", fileOutput);


    }

    public static List<int[]> buildfamilyRules(int[] family) {
        List<int[]> result = new ArrayList<>();
        result.add(family);
        for (int i = 0; i < family.length; i++) {
            for (int j = i + 1; j < family.length; j++) {
                result.add(new int[]{-family[i], -family[j]});
            }
        }
        return result;
    }

    /*
    public static boolean everyVarIsPossible(SatSolver satSolver, int anzahlVariablen) throws TimeoutException {
        for (int i = 0; i < anzahlVariablen; i++) {
            if (!satSolver.isSatisfiableWith(i + 1)) {
                return false;
            }
        }
        return true;
    }
     */

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

    public static int[] getPossibleTrueVars(SatSolver satSolver, int anzahlVariablen) throws TimeoutException{
        List<Integer> result = new ArrayList<>();
        for (int var = 1; var <= anzahlVariablen; var++){
            if (satSolver.isSatisfiableWith(var)){
                result.add(var);
            }
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    public static int[] getPossibleFalseVars(SatSolver satSolver, int anzahlVariablen) throws TimeoutException{
        List<Integer> result = new ArrayList<>();
        for (int var = 1; var <= anzahlVariablen; var++){
            if (satSolver.isSatisfiableWith(-var)){
                result.add(var);
            }
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }

}