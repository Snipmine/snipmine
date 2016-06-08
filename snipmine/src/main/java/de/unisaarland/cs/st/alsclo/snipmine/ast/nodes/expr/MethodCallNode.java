package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;
import org.apache.commons.lang3.ArrayUtils;

public class MethodCallNode extends Node implements Expression {

    public MethodCallNode(final Node expr, final Node name, final Node[] args) {
        super(ArrayUtils.addAll(new Node[]{expr, name}, args));
        Util.expect(expr == null || expr instanceof Expression);
        Util.expect(Util.instanceOfAny(name, IdentifierNode.class, LiteralNode.class));
        Util.expect(Util.allInstanceOf(args, Expression.class));
    }

    @Override
    public String getSource(int indent) {
        StringBuilder sb = new StringBuilder();
        if (child(0) != null) {
            sb.append(child(0).getSource(indent));
            sb.append(".");
        }
        sb.append(child(1).getSource(indent));
        sb.append("(");
        if (getChildCount() > 2) {
            sb.append(child(2).getSource(indent));
            for (int i = 3; i < getChildCount(); i++) {
                sb.append(", ");
                sb.append(child(i).getSource(indent));
            }
        }
        sb.append(")");
        return sb.toString();
    }

}
