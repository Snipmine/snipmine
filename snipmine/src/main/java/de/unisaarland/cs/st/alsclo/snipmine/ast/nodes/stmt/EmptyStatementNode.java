package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.stmt;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

public class EmptyStatementNode extends Node implements Statement {

    private static final int HASHCODE = 43;

    public EmptyStatementNode() {
        super(HASHCODE);
    }

    @Override
    public Collection<Node> computePossibleSnippets(final Consumer<Node> col) {
        return Collections.singletonList(this);
    }

    @Override
    public String getSource(int indent) {
        return ";";
    }

}
