package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.type.TypeNode;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;
import org.apache.commons.lang3.ArrayUtils;

public class ConstructorCallNode extends Node implements Expression{

    public ConstructorCallNode(final Node type, final Node[] args) {
        super(ArrayUtils.addAll(new Node[]{type}, args));
        Util.expect(type instanceof TypeNode);
        Util.expect(Util.allInstanceOf(args, Expression.class));
    }

    @Override
    public String getSource(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("new ");
        sb.append(child(0).getSource(indent));
        sb.append("(");
        if (getChildCount() > 1) {
            sb.append(child(1).getSource(indent));
            for (int i = 2; i < getChildCount(); i++) {
                sb.append(", ");
                sb.append(child(i).getSource(indent));
            }
        }
        sb.append(")");
        return sb.toString();
    }

}
