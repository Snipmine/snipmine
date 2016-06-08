package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.stmt;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr.DeclExprNode;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr.Expression;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;

public class ForEachNode extends Node implements Statement {

    public ForEachNode(final Node param, final Node expr, final Node body) {
        super(param, expr, body);
        Util.expect(param instanceof DeclExprNode);
        Util.expect(expr instanceof Expression);
        Util.expect(body instanceof Statement);
    }

    @Override
    public Collection<Node> computePossibleSnippets(final Consumer<Node> col) {
        final LinkedList<Node> result = new LinkedList<>();
        final Node param = child(0);
        final Node expr = child(1);
        child(2).computePossibleSnippets(col).forEach(n -> {
            result.add(new ForEachNode(param, expr, n));
        });
        result.add(new ForEachNode(param, expr, Placeholder.INSTANCE));
        result.forEach(col);
        return result;
    }

    @Override
    public String getSource(int indent) {
        return "for(" + child(0).getSource(indent) + " : " + child(1).getSource(indent) + ") " + child(2).getSource(indent);
    }

}
