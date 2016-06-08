package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.stmt;

import de.unisaarland.cs.st.alsclo.snipmine.Config;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr.IdentifierNode;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class StatementListNode extends Node {

    private static final int MAXLEN = 5;

    public StatementListNode(final Node[] statements) {
        super(statements);
        Util.expect(Util.allInstanceOf(statements, Statement.class));
    }

    @Override
    public String getSource(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("    ");
        }
        String space = sb.toString();
        sb.setLength(0);
        for (int i = 0; i < getChildCount(); i++) {
            sb.append(space);
            sb.append(child(i).getSource(indent));
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public Collection<Node> computePossibleSnippets(final Consumer<Node> col) {
        if (getChildCount() > 15) return Collections.singletonList(new StatementListNode(new Node[0]));

        final LinkedList<Node> result = new LinkedList<>();
        Map<Node, Collection<Node>> subs = computeSubMap(col);
        Consumer<Node[]> translate = x -> {
            Node y = new StatementListNode(x);
            if (y.getDepth() < Config.getMaxDepth() && y.getOrder() < Config.getMaxOrder(y.getDepth())) {
                col.accept(y);
                result.add(y);
            }
        };
        Consumer<Node[]> perm = x -> substitute(0, x, new Node[x.length], subs, translate);
        for (int i = 2; i <= MAXLEN; i++) {
            permuteChildren(0, 0, new Node[i], perm);
        }
        assert result.stream().allMatch(x -> x.getChildCount() >= 2 && x.getChildCount() <= MAXLEN);
        if (getChildCount() > MAXLEN) {
            result.add(this);
            col.accept(this);
        }
        return result;
    }

    private Map<Node, Collection<Node>> computeSubMap(Consumer<Node> col) {
        Map<Node, Collection<Node>> subs = new HashMap<>();
        for (int i = 0; i < getChildCount(); i++) {
            subs.put(child(i), child(i).computePossibleSnippets(col));
        }
        return subs;
    }

    private void substitute(final int i, final Node[] perm, final Node[] data, final Map<Node, Collection<Node>> subs, final Consumer<Node[]> result){
        if (i < perm.length) {
            for (Node n : subs.get(perm[i])) {
                data[i] = n;
                substitute(i+1, perm, data, subs, result);
            }
        } else {
            result.accept(data);
        }
    }

    private void permuteChildren(final int child, final int select, final Node[] data, final Consumer<Node[]> result) {
        if (select < data.length) {
            for (int i = child; i < getChildCount(); i++) {
                data[select] = child(i);
                permuteChildren(i+1, select+1, data, result);
            }
        } else {
            result.accept(data);
        }
    }

    @Override
    public Set<IdentifierNode> getOutputs() {
        Set<IdentifierNode> r = new HashSet<>();
        for (int i = 0; i < getChildCount(); i++) {
            r.addAll(child(i).getOutputs());
        }
        return r;
    }
}
