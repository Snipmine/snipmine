package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.stmt;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr.Expression;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;

public class WhileNode extends Node implements Statement {

    public enum LoopType {
        WHILE, DO_WHILE
    }

    private final LoopType type;

    public WhileNode(final LoopType type, final Node expr, final Node stmt) {
        super(type.hashCode(), expr, stmt);
        this.type = type;
        Util.expect(expr instanceof Expression);
        Util.expect(stmt instanceof Statement);
    }

    @Override
    public Collection<Node> computePossibleSnippets(final Consumer<Node> col) {
        final LinkedList<Node> result = new LinkedList<>();
        final Node expr = child(0);
        child(1).computePossibleSnippets(col).forEach(n -> {
            result.add(new WhileNode(type, expr, n));
        });
        result.add(new WhileNode(type, expr, Placeholder.INSTANCE));
        result.forEach(col);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj) && ((WhileNode) obj).type == type;
    }

    @Override
    public String getSource(int indent) {
        if (type == LoopType.WHILE) {
            return "while (" + child(0).getSource(indent) + ") " + child(1).getSource(indent);
        } else {
            return "do " + child(1).getSource(indent) + " while (" + child(0).getSource(indent) + ")";
        }
    }

}
