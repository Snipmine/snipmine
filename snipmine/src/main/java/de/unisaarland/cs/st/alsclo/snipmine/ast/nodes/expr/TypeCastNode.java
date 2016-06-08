package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.type.ArrayTypeNode;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.type.TypeNode;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

public class TypeCastNode extends Node implements Expression {

    public TypeCastNode(final Node type, final Node expr) {
        super(type, expr);
        Util.expect(Util.instanceOfAny(type, TypeNode.class, ArrayTypeNode.class));
        Util.expect(expr instanceof Expression);
    }

    @Override
    public String getSource(int indent) {
        return String.format("(%s) %s", child(0).getSource(indent), child(1).getSource(indent));
    }

}
