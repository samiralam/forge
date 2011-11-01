package net.slightlymagic.maxmtg;

import org.apache.commons.lang3.StringUtils;

/**
 * Special predicate class to perform string operations.
 * 
 * @param <T>
 *            the generic type
 */
public abstract class PredicateString<T> extends Predicate<T> {
    /** Possible operators for string operands. */
    public enum StringOp {

        /** The CONTAINS. */
        CONTAINS,
        /** The NO t_ contains. */
        NOT_CONTAINS,
        /** The EQUALS. */
        EQUALS,
        /** The NO t_ equals. */
        NOT_EQUALS
    }

    /** The operator. */
    private final StringOp operator;

    /**
     * Op.
     * 
     * @param op1
     *            the op1
     * @param op2
     *            the op2
     * @return true, if successful
     */
    protected final boolean op(final String op1, final String op2) {
        switch (this.getOperator()) {
        case CONTAINS:
            return StringUtils.containsIgnoreCase(op1, op2);
        case NOT_CONTAINS:
            return !StringUtils.containsIgnoreCase(op1, op2);
        case EQUALS:
            return op1.equalsIgnoreCase(op2);
        case NOT_EQUALS:
            return op1.equalsIgnoreCase(op2);
        default:
            return false;
        }
    }

    /**
     * Instantiates a new predicate string.
     * 
     * @param operator
     *            the operator
     */
    public PredicateString(final StringOp operator) {
        this.operator = operator;
    }

    /**
     * @return the operator
     */
    public StringOp getOperator() {
        return operator;
    }
}
