import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

/**
 * Hier werden paar Rechenoperationen ausgelagert, die gebraucht werden koennten.
 */
public class Operation {

    /**
     * Calculates the variance of a given CNF (Conjunctive Normal Form) problem.
     * This method first generates the CNF data from the provided list of clauses and the number of variables.
     * Each clause is represented as an array of integers in the 'cnf' parameter. The CNF data is then written
     * to a file ending with '.cnf'. Afterward, it runs the 'c2d' console application to count the number of
     * models (satisfying assignments) for the CNF problem. The count is parsed from the 'c2d' output and returned.
     *
     * @param cnf A list of clauses, each represented as an array of integers, forming the CNF problem.
     * @param numberOfVariables The number of variables in the CNF problem.
     * @return A BigInteger representing the count of satisfying assignments (models) of the CNF problem.
     * @throws Exception If the method is unable to read the counting models from the 'c2d' output.
     */
    public static BigInteger getVariance(List<int[]> cnf, int numberOfVariables) throws Exception {
        // create the CNF output data
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

        //run c2d
        String[] output = KonsolenSchnittstelle.konsolenEingabe("c2d -in result.cnf -count");
        System.out.println(Arrays.toString(output));

        // read count from c2d output
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

    /**
     * Formats a BigInteger number into a string with points (periods) for improved readability.
     * This method converts the BigInteger number into a string and then inserts points after every
     * third digit from the right. The formatted string makes it easier to read large numbers by visually
     * breaking them into groups of three digits.
     *
     * @param number The BigInteger number to be formatted.
     * @return A string representation of the BigInteger, formatted with points for better readability.
     *         For example, a BigInteger with the value 1234567 will be formatted as "1.234.567".
     */
    public static String pointsToBigInt(BigInteger number) {
        // Convert BigInt to String
        String numberString = number.toString();

        // Add Points to the BigInt in string format
        StringBuilder formattedString = new StringBuilder();
        int length = numberString.length();
        for (int i = 0; i < length; i++) {
            char digit = numberString.charAt(i);
            formattedString.append(digit);

            // Add a point if the third char from right is met
            int distanceFromRight = length - i - 1;
            if (distanceFromRight % 3 == 0 && distanceFromRight != 0) {
                formattedString.append('.');
            }
        }
        return formattedString.toString();
    }

    /**
     * Computes the percentage of a BigInteger value.
     * This function takes a BigInteger 'number' and a double 'percentage', then calculates the specified
     * percentage of the BigInteger. The calculation is performed with BigDecimal precision to handle the
     * large scale of BigInteger and the floating-point nature of the percentage. The final result is rounded
     * to the nearest integer value using the HALF_UP rounding mode, ensuring accuracy and precision in
     * the calculation, especially in cases of large numbers or small percentage values.
     *
     * @param number The BigInteger value for which the percentage is to be calculated.
     * @param percentage A double representing the percentage to calculate. For instance, a value of 20.5
     *                   would calculate 20.5% of the BigInteger 'number'.
     * @return A BigInteger representing the calculated percentage of the original number. The result is
     *         rounded to the nearest integer using the HALF_UP rounding mode.
     */
    public static BigInteger calculatePercentage(BigInteger number, double percentage) {
        BigDecimal decimalNumber = new BigDecimal(number);
        BigDecimal decimalPercentage = new BigDecimal(percentage);

        // Calculate percentage
        BigDecimal result = decimalNumber.multiply(decimalPercentage).divide(new BigDecimal(100), RoundingMode.HALF_UP);

        // Convert result back to BigInteger
        return result.toBigInteger();
    }

    /**
     * Checks if a given integer is present in an array of integers.
     * This method iterates through each element of the provided integer array (list) and compares it
     * with the specified integer (var). If a match is found, the method returns true, indicating that
     * the integer is present in the array. If no match is found by the end of the array, the method
     * returns false.
     *
     * @param list An array of integers in which to search for the specified integer.
     * @param var The integer to be searched for within the array.
     * @return A boolean value; true if 'var' is found in 'list', false otherwise.
     */
    public static boolean isIn(int[] list, int var) {
        for (int listVar : list) {
            if (listVar == var) {
                return true;
            }
        }
        return false;
    }
}