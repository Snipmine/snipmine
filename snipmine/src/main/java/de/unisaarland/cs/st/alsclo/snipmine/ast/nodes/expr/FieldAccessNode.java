package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

/**
 * @author Alex Schlosser
 */
public class FieldAccessNode extends Node implements Expression {
    public FieldAccessNode(final Node expr, final Node ident) {
        super(expr, ident);
        Util.expect(expr instanceof Expression);
        Util.expect(Util.instanceOfAny(ident, IdentifierNode.class, LiteralNode.class));
    }

    @Override
    public String getSource(final int indent) {
        return child(0).getSource(indent) + "." + child(1).getSource(indent);
    }
}
