package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.stmt;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr.DeclExprNode;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;

public class CatchNode extends Node {

    public CatchNode(final Node param, final Node block) {
        super(param, block);
        Util.expect(param instanceof DeclExprNode);
        Util.expect(block instanceof Statement);
    }

    @Override
    public Collection<Node> computePossibleSnippets(final Consumer<Node> col) {
        final LinkedList<Node> result = new LinkedList<>();
        final Node p = child(0);
        child(1).computePossibleSnippets(col).forEach(n -> {
            result.add(new CatchNode(p, n));
        });
        result.add(new CatchNode(p, Placeholder.INSTANCE));
        return result;
    }

    @Override
    public String getSource(int indent) {
        return String.format("catch (%s) %s", child(0).getSource(indent), child(1).getSource(indent));
    }

}
