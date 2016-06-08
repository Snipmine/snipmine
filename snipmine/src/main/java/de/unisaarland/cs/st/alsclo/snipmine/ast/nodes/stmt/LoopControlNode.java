package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.stmt;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr.LiteralNode;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

public class LoopControlNode extends Node implements Statement {

    public enum Type {
        BREAK, CONTINUE
    }

    private final Type t;

    public LoopControlNode(final Type t, final Node label) {
        super(t.hashCode(), label);
        this.t = t;
        Util.expect(label == null || label instanceof LiteralNode);
    }

    @Override
    public Collection<Node> computePossibleSnippets(final Consumer<Node> col) {
        return Collections.singletonList(this);
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj) && ((LoopControlNode) obj).t == t;
    }

    @Override
    public String getSource(int indent) {
        return (t == Type.BREAK ? "break" : "continue") + (child(0) != null ? " " + child(0).getSource(indent) + ";" : ";");
    }

}
