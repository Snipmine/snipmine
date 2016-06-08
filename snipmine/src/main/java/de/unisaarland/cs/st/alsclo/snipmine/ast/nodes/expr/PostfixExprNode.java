package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;
import org.eclipse.jdt.core.dom.PostfixExpression.Operator;

public class PostfixExprNode extends Node implements Expression {

    private final Operator o;

    public PostfixExprNode(final Node l, final Operator o) {
        super(o.hashCode(), l);
        this.o = o;
        Util.expect(l instanceof Expression);
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj) && ((PostfixExprNode) obj).o == o;
    }

    @Override
    public String getSource(int indent) {
        return child(0).getSource(indent) + o.toString();
    }

}
