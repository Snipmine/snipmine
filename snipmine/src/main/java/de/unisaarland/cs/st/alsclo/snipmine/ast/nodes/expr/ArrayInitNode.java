package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

public class ArrayInitNode extends Node implements Expression {

    public ArrayInitNode(final Node[] exprs) {
        super(exprs);
        Util.expect(Util.allInstanceOf(exprs, Expression.class));
    }

    @Override
    public String getSource(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (getChildCount() > 0) {
            sb.append(child(0).getSource(indent));
            for (int i = 1; i < getChildCount(); i++) {
                sb.append(", ");
                sb.append(child(i).getSource(indent));
            }
        }
        sb.append("}");
        return sb.toString();
    }

}
