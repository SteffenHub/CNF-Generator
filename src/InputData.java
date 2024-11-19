/**
 * @author: SteffenHub (https://github.com/SteffenHub)
 */
import java.math.BigInteger;
import java.util.List;
import java.util.Random;

/**
 * This class stores all the input data from the user.
 */
public class InputData {

    /**
     * default constructor
     */
    public InputData(){}

    /**
     * The number of variables in the CNF problem.
     */
    public int numberOfVariables;

    /**
     * The minimal family size.
     */
    public int minFamilySize;

    /**
     * The maximal family size.
     */
    public int maxFamilySize;

    /**
     * The list of family rules. Or the trivial clause like: "[1,-1]" for each variable, if no family rules are used.
     */
    public List<int[]> familyRules;

    /**
     * The variance for the family rules.
     */
    public BigInteger variance;

    /**
     * The goal variance for the CNF problem.
     */
    public BigInteger goalVariance;

    /**
     * The goal variance deviation for the CNF problem.
     */
    public BigInteger goalVarianceDeviation;

    /**
     * The minimal rule size.
     */
    public int minRuleSize;

    /**
     * The maximal rule size.
     */
    public int maxRuleSize;

    /**
     * The number of false variables.
     */
    public int falseVars;

    /**
     * The number of true variables.
     */
    public int trueVars;

    /**
     * true if the family rules should be used.
     */
    public boolean useFamilies;

    /**
     * The counting solver that should be used
     */
    public String countSolver;

    /**
     * The random generator created with the input seed
     */
    public Random randomGenerator;

    /**
     * The input seed used for the random generator
     */
    public long seed;
}
