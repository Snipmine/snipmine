package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.stmt;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr.Expression;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class IfNode extends Node implements Statement {

    public IfNode(final Node cond, final Node then, final Node el) {
        super(cond, then, el);
        Util.expect(cond instanceof Expression);
        Util.expect(then instanceof Statement);
        Util.expect(el == null || el instanceof Statement);
    }

    @Override
    public Collection<Node> computePossibleSnippets(final Consumer<Node> col) {
        final Node cond = child(0);
        Collection<Node> subthen = new ArrayList<>(child(1).computePossibleSnippets(col));
        subthen.add(Placeholder.INSTANCE);
        if (child(2) != null) {
            final LinkedList<Node> result = new LinkedList<>();
            final Collection<Node> subelse = child(2).computePossibleSnippets(col);
            subelse.add(Placeholder.INSTANCE);
            subelse.add(null);
            for (Node then : subthen) {
                for (Node els : subelse) {
                    result.add(new IfNode(cond, then, els));
                }
            }
            result.forEach(col);
            return result;
        } else {
            return subthen.stream().map(n -> new IfNode(cond, n, null))
                    .map(x->{col.accept(x);return x;}).collect(Collectors.toList());
        }
    }

    @Override
    public String getSource(int indent) {
        String first = "if (" + child(0).getSource(indent) + ") " + child(1).getSource(indent);
        if (child(2) != null) {
            return first + " else " + child(2).getSource(indent);
        }
        return first;
    }

}
