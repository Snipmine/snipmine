package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.type.ArrayTypeNode;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.type.TypeNode;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.type.UnionTypeNode;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashSet;
import java.util.Set;

public class DeclExprNode extends Node implements Expression {

    public DeclExprNode(final Node type, final Node[] fragments) {
        super(ArrayUtils.addAll(new Node[]{type}, fragments));
        Util.expect(Util.instanceOfAny(type, TypeNode.class, ArrayTypeNode.class, UnionTypeNode.class));
        Util.expect(Util.allInstanceOf(fragments, DeclFragmentNode.class));
    }

    @Override
    public String getSource(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(child(0).getSource(indent));
        sb.append(" ");
        if (getChildCount() > 1) {
            sb.append(child(1).getSource(indent));
            for (int i = 2; i < getChildCount(); i++) {
                sb.append(", ");
                sb.append(child(i).getSource(indent));
            }
        }
        return sb.toString();
    }

    @Override
    public Set<IdentifierNode> getOutputs() {
        Set<IdentifierNode> r = new HashSet<>();
        for (int i = 1; i < getChildCount(); i++) {
            r.addAll(child(i).getOutputs());
        }
        return r;
    }
}
