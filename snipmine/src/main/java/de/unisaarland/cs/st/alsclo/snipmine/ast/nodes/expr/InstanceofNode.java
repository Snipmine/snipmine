package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.type.ArrayTypeNode;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.type.TypeNode;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

/**
 * @author Alex Schlosser
 */
public class InstanceofNode extends Node implements Expression{

    public InstanceofNode(Node expr, Node type) {
        super(expr, type);
        Util.expect(expr instanceof Expression);
        Util.expect(Util.instanceOfAny(type, ArrayTypeNode.class, TypeNode.class));
    }

    @Override
    public String getSource(final int indent) {
        return child(0).getSource(indent) + " instanceof " + child(1).getSource(indent);
    }
}
