package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.stmt;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr.Expression;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * @author Alex Schlosser
 */
public class ThrowNode extends Node implements Statement {

    public ThrowNode(final Node expr) {
        super(expr);
        Util.expect(expr instanceof Expression);
    }

    @Override
    public Collection<Node> computePossibleSnippets(final Consumer<Node> col) {
        col.accept(this);
        return Collections.singletonList(this);
    }

    @Override
    public String getSource(final int indent) {
        return "throw " + child(0).getSource(indent) + ";";
    }
}
