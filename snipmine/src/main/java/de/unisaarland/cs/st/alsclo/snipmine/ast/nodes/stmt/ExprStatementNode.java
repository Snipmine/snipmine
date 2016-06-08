package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.stmt;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr.Expression;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr.IdentifierNode;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;

public class ExprStatementNode extends Node implements Statement {

    public ExprStatementNode(final Node expr) {
        super(expr);
        Util.expect(expr instanceof Expression);
    }

    @Override
    public Collection<Node> computePossibleSnippets(final Consumer<Node> col) {
        return Collections.singletonList(this);
    }

    @Override
    public String getSource(int indent) {
        return child(0).getSource(indent) + ";";
    }

    @Override
    public Set<IdentifierNode> getOutputs() {
        return child(0).getOutputs();
    }
}
