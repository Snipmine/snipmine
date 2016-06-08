package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.type.ArrayTypeNode;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;
import org.apache.commons.lang3.ArrayUtils;

public class ArrayCreationNode extends Node implements Expression {

    public ArrayCreationNode(final Node type, final Node[] dimensions, final Node init) {
        super(ArrayUtils.add(ArrayUtils.addAll(new Node[]{type}, dimensions), init));
        Util.expect(type instanceof ArrayTypeNode);
        Util.expect(dimensions.length > 0 || init != null);
        Util.expect(Util.allInstanceOf(dimensions, Expression.class));
        Util.expect(init == null || init instanceof ArrayInitNode);
    }

    @Override
    public String getSource(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("new ");
        if (getChildCount() > 2) {
            sb.append(child(0).child(0).getSource(indent));
            for (int i = 1; i < getChildCount() - 1; i++) {
                sb.append("[");
                sb.append(child(i).getSource(indent));
                sb.append("]");
            }
            if (child(getChildCount() - 1) != null) {
                sb.append(child(getChildCount() - 1).getSource(indent));
            }
        } else {
            sb.append(child(0).getSource(indent));
            sb.append(child(1).getSource(indent));
        }
        return sb.toString();
    }

}
