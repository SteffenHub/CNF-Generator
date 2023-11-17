import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.rmi.server.ExportException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Hier werden paar Rechenoperationen ausgelagert, die gebraucht werden koennten.
 */
public class Operation {
    public static BigInteger getVariance(List<int[]> cnf, int numberOfVariables) throws Exception {
        String[] fileContent = new String[cnf.size() + 1];
        fileContent[0] = "p cnf " + numberOfVariables + " " + cnf.size();
        for (int i = 0; i < cnf.size(); i++) {
            StringBuilder line = new StringBuilder();
            for (int var : cnf.get(i)) {
                line.append(var).append(" ");
            }
            line.append("0");
            fileContent[i + 1] = line.toString();
        }
        TxtReaderWriter.writeArrayOfStrings("result.cnf", fileContent);

        String[] output = KonsolenSchnittstelle.konsolenEingabe("c2d -in result.cnf -count");
        System.out.println(Arrays.toString(output));

        BigInteger count = new BigInteger("-1");
        for (int i = output.length - 1; i >= 0; i--) {
            if (output[i].length() < 11){
                continue;
            }
            if (output[i].charAt(0) == 'C' &&
                    output[i].charAt(1) == 'o' &&
                    output[i].charAt(2) == 'u' &&
                    output[i].charAt(3) == 'n' &&
                    output[i].charAt(4) == 't' &&
                    output[i].charAt(5) == 'i'){
                int index = 11;
                char character = output[i].charAt(index);
                StringBuilder countString = new StringBuilder();
                while (character != ' '){
                    countString.append(character);
                    ++index;
                    character = output[i].charAt(index);
                }
                count = new BigInteger(countString.toString());
                break;
            }
        }
        if (count.compareTo(new BigInteger("-1")) == 0){
            throw new Exception("cant-read counting models");
        }else{
            return count;
        }
    }

    public static String pointsToBigInt(BigInteger number) {
        // Konvertiere das BigInteger in eine String-Repräsentation
        String numberString = number.toString();

        // Definiere das Format mit Punkten
        DecimalFormat decimalFormat = new DecimalFormat("#,###");

        // Füge die Punkte in den String ein
        StringBuilder formattedString = new StringBuilder();
        int length = numberString.length();
        for (int i = 0; i < length; i++) {
            char digit = numberString.charAt(i);
            formattedString.append(digit);

            // Füge einen Punkt ein, wenn das dritte Zeichen von rechts erreicht ist
            int distanceFromRight = length - i - 1;
            if (distanceFromRight % 3 == 0 && distanceFromRight != 0) {
                formattedString.append('.');
            }
        }
        // Rückgabe des formatierten Strings
        return formattedString.toString();
    }

    public static BigInteger calculatePercentage(BigInteger number, double percentage) {
        BigDecimal decimalNumber = new BigDecimal(number);
        BigDecimal decimalPercentage = new BigDecimal(percentage);

        // Berechnung des Prozentsatzes
        BigDecimal result = decimalNumber.multiply(decimalPercentage).divide(new BigDecimal(100), RoundingMode.HALF_UP);

        // Konvertierung des Ergebnisses in BigInteger
        BigInteger resultBigInteger = result.toBigInteger();

        return resultBigInteger;
    }

    public static boolean isIn(int[] list, int var) {
        for (int listVar : list) {
            if (listVar == var) {
                return true;
            }
        }
        return false;
    }
}