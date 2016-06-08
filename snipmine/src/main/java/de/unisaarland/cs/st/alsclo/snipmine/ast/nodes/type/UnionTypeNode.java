package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.type;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

import java.util.Arrays;

/**
 * @author Alex Schlosser
 */
public class UnionTypeNode extends Node {
    public UnionTypeNode(final Node[] types) {
        super(types);
        Util.expect(types.length > 0);
        Util.expect(Arrays.stream(types).allMatch(x -> x instanceof TypeNode));
    }

    @Override
    public String getSource(final int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(child(0).getSource(indent));
        for (int i = 1; i < getChildCount(); i++) {
            sb.append('|');
            sb.append(child(i).getSource(indent));
        }
        return sb.toString();
    }
}
