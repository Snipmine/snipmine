package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.stmt;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr.LiteralNode;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class LabeledStatementNode extends Node implements Statement {

    public LabeledStatementNode(final Node i, final Node stmt) {
        super(i, stmt);
        Util.expect(i instanceof LiteralNode);
        Util.expect(stmt instanceof Statement);
    }

    @Override
    public List<Node> computePossibleSnippets(final Consumer<Node> col) {
        final LinkedList<Node> result = new LinkedList<>();
        final Node l = child(0);
        child(1).computePossibleSnippets(col).forEach(n -> {
            result.add(new LabeledStatementNode(l, n));
        });
        result.add(new LabeledStatementNode(l, Placeholder.INSTANCE));
        result.forEach(col);
        return result;
    }

    @Override
    public String getSource(int indent) {
        return child(0).getSource(indent) + ": " + child(1).getSource(indent);
    }

}
