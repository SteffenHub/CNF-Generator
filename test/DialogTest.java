/**
 * @author: SteffenHub (https://github.com/SteffenHub)
 */
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;


class DialogTest {

    int numberOfVariables;
    int minFamSize;
    int maxFamSize;
    Random rand;

    @BeforeEach
    void setUp() {
        this.numberOfVariables = 10;
        this.minFamSize = 1;
        this.maxFamSize = 4;
        this.rand = new Random(1234);
    }

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