package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

public class ArrayAccessNode extends Node implements Expression {

    public ArrayAccessNode(Node arr, Node dim) {
        super(arr, dim);
        Util.expect(arr instanceof Expression);
        Util.expect(dim instanceof Expression);
    }

    @Override
    public String getSource(int indent) {
        return String.format("%s[%s]", child(0).getSource(indent), child(1).getSource(indent));
    }

}
