package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.stmt;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr.Expression;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;

public class SynchronizedNode extends Node implements Statement {

    public SynchronizedNode(final Node expr, final Node stmt) {
        super(expr, stmt);
        Util.expect(expr instanceof Expression);
        Util.expect(stmt instanceof Statement);
    }

    @Override
    public Collection<Node> computePossibleSnippets(final Consumer<Node> col) {
        final LinkedList<Node> result = new LinkedList<>();
        final Node expr = child(0);
        child(1).computePossibleSnippets(col).forEach(n -> {
            result.add(new SynchronizedNode(expr, n));
        });
        result.add(new SynchronizedNode(expr, Placeholder.INSTANCE));
        result.forEach(col);
        return result;
    }

    @Override
    public String getSource(int indent) {
        return "synchronized (" + child(0).getSource(indent) + ") " + child(1).getSource(indent);
    }

}
