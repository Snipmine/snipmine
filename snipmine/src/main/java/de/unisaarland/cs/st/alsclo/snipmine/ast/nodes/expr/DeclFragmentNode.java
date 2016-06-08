package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

import java.util.Set;

public class DeclFragmentNode extends Node{

    public DeclFragmentNode(final Node identifier, final Node expr) {
        super(identifier, expr);
        Util.expect(identifier instanceof IdentifierNode);
        Util.expect(expr == null || expr instanceof Expression);
    }

    @Override
    public String getSource(int indent) {
        if (child(1) != null) {
            return String.format("%s = %s", child(0).getSource(indent), child(1).getSource(indent));
        } else {
            return child(0).getSource(indent);
        }
    }

    @Override
    public Set<IdentifierNode> getOutputs() {
        return child(0).getOutputs();
    }
}
