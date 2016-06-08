package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;

public class LiteralNode extends Node implements Expression {

    private final String value;

    public LiteralNode(final String value) {
        super(value.hashCode());
        this.value = value;
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj) && ((LiteralNode) obj).value.equals(value);
    }

    @Override
    public String getSource(int indent) {
        return value;
    }

}
