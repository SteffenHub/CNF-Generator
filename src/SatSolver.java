import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.*;

import java.util.List;

/**
 * The Sat Solver class which provides the core functionality of the Sat4J Solver
 */
public class SatSolver {

    /**
     * The SatSolver as Sat4J Object
     */
    private final ISolver solver;

    /**
     * Constructor for the SatSolver class
     *
     * @param cnf The rules in the form of a CNF. Example: [[1,2],[-2,3,1]] means: ((1 OR 2) AND (NOT(2) OR 3 OR 1))
     * @throws ContradictionException If the rules are a contradiction. See: <a href="https://www.sat4j.org/maven23/org.sat4j.core/apidocs/org/sat4j/specs/ContradictionException.html">Sat4J documentation</a>
     */
    public SatSolver(int[][] cnf) throws ContradictionException {
        //create new default Solver
        this.solver = SolverFactory.newDefault();
        //add all clauses
        for (int[] clause : cnf) {
            solver.addClause(new VecInt(clause));
        }
    }

    public SatSolver(List<int[]> cnf) throws ContradictionException {
        //create new default Solver
        this.solver = SolverFactory.newDefault();
        //add all clauses
        for (int[] clause : cnf) {
            solver.addClause(new VecInt(clause));
        }
    }

    /**
     * Constructor for the SatSolver class without parameters to insert the rules later
     */
    public SatSolver() {
        //create new default Solver
        this.solver = SolverFactory.newDefault();
    }

    /**
     * Checks if Problem is satisfiable
     *
     * @return is the Problem satisfiable?
     * @throws TimeoutException if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     */
    public boolean isSatisfiable() throws TimeoutException {
        return solver.isSatisfiable();
    }

    /**
     * Check whether there is a model with the given variable.
     *
     * @param var Any variable. Variable can be given positively or negatively(-3)
     * @return Is there a Model with the given variable
     * @throws TimeoutException if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     */
    public boolean isSatisfiableWith(int var) throws TimeoutException {
        return this.solver.isSatisfiable(new VecInt(new int[]{var}));
    }

    /**
     * Check whether there is a model with the given variables. Note that these Variables connected by an AND.
     * Example: [1,-2,4] means: is there a model with (1 AND NOT(2) AND 4) is true
     *
     * @param variables Any variables. Variables can be given positively or negatively(-3)
     * @return Is there a Model with the given variables
     * @throws TimeoutException if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     */
    public boolean isSatisfiableWithConjunct(int[] variables) throws TimeoutException {
        return this.solver.isSatisfiable(new VecInt(variables));
    }

    /**
     * Adding the given clauses temporally to the ruleSet and check whether the ruleSet is still satisfiable.
     * Example: [[1,2], [3], [2,4]] means we are adding the following rules to the ruleSet:
     * (1 OR 2) AND (3) AND (2 OR 4)
     * after we check if the ruleSet is still satisfiable we delete these clauses from the ruleSet.
     *
     * @param clauses The clauses that should be checked
     * @return Can these clauses be added to the ruleSet
     * @throws TimeoutException if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     */
    public boolean isSatisfiableWithClauses(int[][] clauses) throws TimeoutException {
        IConstr[] constraints = new IConstr[clauses.length];
        boolean isSatisfiable = true;
        for (int i = 0; i < clauses.length; i++) {
            try {
                //save new constraint to remove it later
                constraints[i] = solver.addClause(new VecInt(clauses[i]));
            } catch (ContradictionException e) {
                isSatisfiable = false;
                break;
            }
        }
        if (isSatisfiable)
            isSatisfiable = this.isSatisfiable();
        for (IConstr constraint : constraints) {
            //if constraint is null you're adding a redundant rule, like variable 2 is always true, and you're adding (1 OR 2) = (1 OR TRUE) = TRUE
            if (constraint != null)
                solver.removeConstr(constraint);
        }
        return isSatisfiable;
    }

    /**
     * Check whether adding a Clause with the given variables would be possible
     * Note that these Variables connected by an OR.
     * Example: [1,-2,4] means: is there a model with (1 OR NOT(2) OR 4) is true
     *
     * @param variables Any variables. Variables can be given positively or negatively(-3)
     * @return Is there a Model with the given variables as Clause
     * @throws TimeoutException if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     */
    public boolean isSatisfiableWithClause(int[] variables) throws TimeoutException {
        IConstr constraint;
        try{
            //save new constraint to remove it later
            constraint = solver.addClause(new VecInt(variables));
        }catch (ContradictionException e){
            return false;
        }
        boolean isSatisfiable = this.isSatisfiable();
        //if constraint is null you're adding a redundant rule, like variable 2 is always true, and you're adding (1 OR 2) = (1 OR TRUE) = TRUE
        if (constraint != null)
            solver.removeConstr(constraint);
        return isSatisfiable;
    }

    /**
     * Adds a new variable to the rule set. It means, that the given variable should be always true,
     * because the variable is alone in a clause.
     *
     * @param variable Any variable. Variable can be given positively or negatively(-3)
     * @throws ContradictionException By adding the variable, the rule set turns into a contradiction. See: <a href="https://www.sat4j.org/maven23/org.sat4j.core/apidocs/org/sat4j/specs/ContradictionException.html">Sat4J documentation</a>
     */
    public void addVariable(int variable) throws ContradictionException {
        this.solver.addClause(new VecInt(new int[]{variable}));
    }

    /**
     * Adds a new rule to the rule set. The given rule is a clause and is logically connected by OR's
     *
     * @param rule Any rule as a clause. Variables inside the clause can be given positively or negatively [1,-2,4]
     * @throws ContradictionException By adding the rule, the rule set turns into a contradiction. See: <a href="https://www.sat4j.org/maven23/org.sat4j.core/apidocs/org/sat4j/specs/ContradictionException.html">Sat4J documentation</a>
     */
    public void addRule(int[] rule) throws ContradictionException {
        this.solver.addClause(new VecInt(rule));
    }

    /**
     * Find a model with the rule set.
     *
     * @return The model as int-Array. Each int represents a var. The variables can be positively or negatively. If no exist null will be returned
     * @throws TimeoutException if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     */
    public int[] getModel() throws TimeoutException {
        IProblem problem = this.solver;
        if (problem.isSatisfiable())
            return problem.model();
         else
            return null;
    }

    /**
     * Finds a Model with the given Variables. Note that these Variables connected by an AND.
     * Example: [1,-2,4] means: is there a model with (1 AND NOT(2) AND 4) is true
     *
     * @param variables Any variables. Variables can be given positively or negatively(-3)
     * @return Is there a Model with the given variables. If not null will be returned
     * @throws TimeoutException if the calculation takes too much time. See: <a href="https://www.sat4j.org/doc/core/org/sat4j/specs/TimeoutException.html">Sat4J documentation</a>
     */
    public int[] getModelWith(int[] variables) throws TimeoutException {
        return this.solver.findModel(new VecInt(variables));
    }

    /**
     * Provides the highest variable number in the current rule set.
     * If you build the rule set with Variables that only increases by 1 like the rule set contains all variables between 1 and 100,
     * this method will provide the number of variables that exist in the current rule set.
     *
     * @return The highest variable in rule set.
     */
    public int getHighestVar() {
        return solver.nVars();
    }
}