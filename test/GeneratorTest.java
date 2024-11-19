/**
 * @author: SteffenHub (https://github.com/SteffenHub)
 */
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;


class GeneratorTest {

    private InputData iD;
    @BeforeEach
    void setUp() {
        this.iD = getInputData(true);
    }

    private InputData getInputData(boolean useFamilyRules){
        InputData iD = new InputData();
        iD.seed = 1234;
        iD.randomGenerator = new Random(iD.seed);
        iD.countSolver = "c2d";
        iD.numberOfVariables = 10;
        iD.useFamilies = useFamilyRules;
        iD.familyRules = new ArrayList<>();
        if (iD.useFamilies) {
            int[] familySize = new int[]{1, 4};
            iD.minFamilySize = familySize[0];
            iD.maxFamilySize = familySize[1];
            iD.familyRules = new Dialog().generateAndPrintFamilyRules(iD.numberOfVariables, iD.minFamilySize, iD.maxFamilySize, iD.randomGenerator);
        }else{
            // Add all variables as rule [var1 or not var1] to rules,so the solver knew all variables
            for (int i = 1; i <= iD.numberOfVariables; i++) {
                iD.familyRules.add(new int[]{i, -i});
            }
        }
        iD.variance = new BigInteger("24"); // TODO
        BigInteger goalVari = new BigInteger("10");
        BigInteger[] goalVarianceAndDeviation = new BigInteger[]{goalVari, Operation.calculatePercentage(goalVari, 5)};
        iD.goalVariance = goalVarianceAndDeviation[0];
        iD.goalVarianceDeviation = goalVarianceAndDeviation[1];
        int[] rulesSize = new int[]{2, 4};
        iD.minRuleSize = rulesSize[0];
        iD.maxRuleSize = rulesSize[1];
        iD.falseVars = 1;
        iD.trueVars = 1;
        return iD;
    }

    @Test
    void startGenerator() throws ContradictionException, IOException, TimeoutException {
        List<String> expected = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("test/testData/cnfBuilder10VarsVariance10.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                expected.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<String> actual = new Generator().startGenerator(this.iD);

        actual.removeIf(s -> s.startsWith("c Calculation time:"));
        expected.removeIf(s -> s.startsWith("c Calculation time:"));
        assertEquals(expected, actual);
    }
}