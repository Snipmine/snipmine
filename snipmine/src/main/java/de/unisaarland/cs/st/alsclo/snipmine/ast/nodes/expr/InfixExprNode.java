package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;

public class InfixExprNode extends Node implements Expression {

    private final Operator o;

    public InfixExprNode(final Operator operator, final Node[] exprs) {
        super(operator.hashCode(), exprs);
        o = operator;
        Util.expect(Util.allInstanceOf(exprs, Expression.class));
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj) && ((InfixExprNode) obj).o == o;
    }

    @Override
    public String getSource(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(child(0).getSource(indent));
        for (int i = 1; i < getChildCount(); i++) {
            sb.append(" " + o.toString() + " ");
            sb.append(child(i).getSource(indent));
        }
        return  sb.toString();
    }

}
