package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.type;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

public class ArrayTypeNode extends Node {

    private final int dimensions;

    public ArrayTypeNode(final Node elemtype, final int dimensions) {
        super(dimensions, elemtype);
        Util.expect(dimensions > 0);
        Util.expect(elemtype instanceof TypeNode);
        this.dimensions = dimensions;
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj) && ((ArrayTypeNode) obj).dimensions == dimensions;
    }

    @Override
    public String getSource(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(child(0).getSource(indent));
        for (int i = 0; i < dimensions; i++) {
            sb.append("[]");
        }
        return sb.toString();
    }
}
