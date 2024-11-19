/**
 * @author: SteffenHub (https://github.com/SteffenHub)
 */
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * test class for the dialog class
 */
class DialogTest {

    /**
     * default constructor
     */
    public DialogTest(){}

    /**
     * The number of variables in the cnf
     */
    int numberOfVariables;
    /**
     * the minimum size for the families
     */
    int minFamSize;
    /**
     * the maximum size for the families
     */
    int maxFamSize;
    /**
     * the random generator used with seed
     */
    Random rand;

    /**
     * Set up method run before each test
     */
    @BeforeEach
    void setUp() {
        this.numberOfVariables = 10;
        this.minFamSize = 1;
        this.maxFamSize = 4;
        this.rand = new Random(1234);
    }

    /**
     * test if the family rules build correctly
     */
    @Test
    void generateAndPrintFamilyRules() {
        List<int[]> actual = new Dialog().generateAndPrintFamilyRules(this.numberOfVariables, this.minFamSize, this.maxFamSize, this.rand);
        List<int[]> expected = new ArrayList<>();
        expected.add(new int[]{1,2,3});
        expected.add(new int[]{-1,-2});
        expected.add(new int[]{-1,-3});
        expected.add(new int[]{-2,-3});
        expected.add(new int[]{4,5});
        expected.add(new int[]{-4,-5});
        expected.add(new int[]{6,7,8,9});
        expected.add(new int[]{-6,-7});
        expected.add(new int[]{-6,-8});
        expected.add(new int[]{-6,-9});
        expected.add(new int[]{-7,-8});
        expected.add(new int[]{-7,-9});
        expected.add(new int[]{-8,-9});
        expected.add(new int[]{10});

        assertEquals(actual.size(), expected.size());
        for (int i = 0; i < actual.size(); i++) {
            assertArrayEquals(expected.get(i), actual.get(i));
        }
    }
}