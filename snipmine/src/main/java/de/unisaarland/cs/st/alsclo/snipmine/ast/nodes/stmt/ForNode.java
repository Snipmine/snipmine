package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.stmt;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr.DeclExprNode;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr.Expression;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ForNode extends Node implements Statement {

    public ForNode(final Node init, final Node cond, final Node update, final Node body) {
        super(init, cond, update, body);
        Util.expect(init == null || init instanceof DeclExprNode);
        Util.expect(cond == null || cond instanceof Expression);
        Util.expect(update == null || update instanceof Expression);
        Util.expect(body instanceof Statement);
    }

    @Override
    public Collection<Node> computePossibleSnippets(final Consumer<Node> col) {
        Collection<Node> result = child(3).computePossibleSnippets(col).stream().map(n -> new ForNode(child(0), child(1), child(2), n))
                .collect(Collectors.toList());
        result.add(new ForNode(child(0), child(1), child(2), Placeholder.INSTANCE));
        result.forEach(col);
        return result;
    }

    private String nullSource(int indent, int c) {
        return child(c) == null ? "" : child(c).getSource(indent);
    }

    @Override
    public String getSource(int indent) {
        return "for(" + nullSource(indent, 0) + "; " + nullSource(indent, 1) + "; " +
                nullSource(indent, 2) + ") " + child(3).getSource(indent);
    }

}
