package de.unisaarland.cs.st.alsclo.snipmine.ast;

public class ASTException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 6117709088072453298L;

    public ASTException() {
        super();
    }

    public ASTException(final String reason) {
        super(reason);
    }

}
