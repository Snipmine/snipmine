package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr.IdentifierNode;

import java.util.*;
import java.util.function.Consumer;

public abstract class Node {
    private final int localhashcode;
    private final int depth;
    private final int order;
    private final ArrayList<Node> children;

    public Node(Node... nodes) {
        this(0, nodes);
    }

    public Node(int localhashcode) {
        this(localhashcode, new Node[0]);
    }

    public Node(int localhashcode, Node... nodes) {
        children = new ArrayList<Node>(nodes.length);
        children.addAll(Arrays.asList(nodes));
        depth = children.stream().filter(n -> n != null).mapToInt(n -> n.depth).max().orElse(0) + 1;
        order = children.stream().filter(n -> n != null).mapToInt(n -> n.order).sum() + 1;
        this.localhashcode = localhashcode * 31 + Arrays.hashCode(nodes);
    }

    public Collection<Node> computePossibleSnippets(final Consumer<Node> col) {
        throw new UnsupportedOperationException("Snippets cannot be computed on Types or Expressions: " + this.getClass().getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (localhashcode != node.localhashcode) return false;
        if (depth != node.depth) return false;
        if (order != node.order) return false;
        return children.equals(node.children);

    }

    @Override
    public int hashCode() {
        return localhashcode;
    }

    public int getDepth() {
        return depth;
    }

    public int getOrder() {
        return order;
    }

    public Node child(int i) {
        return children.get(i);
    }

    public int getChildCount() {
        return children.size();
    }

    public abstract String getSource(int indent);

    public Set<IdentifierNode> getInputs(Set<String> s) {
        Set<IdentifierNode> r = new HashSet<>();
        if (!children.isEmpty())
            children.stream().sequential()
                    .filter(n -> n != null)
                    .flatMap(n -> n.getInputs(s).stream().sequential())
                    .forEach(r::add);
        return r;
    }

    public Set<IdentifierNode> getOutputs() {
        return Collections.emptySet();
    }

    @Override
    public String toString() {
        return getSource(0);
    }
}
