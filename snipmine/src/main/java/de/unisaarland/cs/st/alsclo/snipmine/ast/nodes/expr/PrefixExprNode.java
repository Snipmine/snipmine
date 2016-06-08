package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;
import org.eclipse.jdt.core.dom.PrefixExpression.Operator;

/**
 * @author Alex Schlosser
 */
public class PrefixExprNode extends Node implements Expression {
    private final Operator op;

    public PrefixExprNode(final Operator op, final Node expr) {
        super(op.hashCode(), expr);
        this.op = op;
        Util.expect(expr instanceof Expression);
    }

    @Override
    public String getSource(final int indent) {
        return op.toString() + child(0).getSource(indent);
    }
}
