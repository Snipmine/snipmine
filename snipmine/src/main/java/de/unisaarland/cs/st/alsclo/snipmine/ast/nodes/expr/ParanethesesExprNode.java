package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

/**
 * @author Alex Schlosser
 */
public class ParanethesesExprNode extends Node implements Expression {
    public ParanethesesExprNode(final Node expr) {
        super(expr);
        Util.expect(expr instanceof Expression);
    }

    @Override
    public String getSource(final int indent) {
        return "(" + child(0).getSource(indent) + ")";
    }
}
