package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

public class ConditionalExprNode extends Node implements Expression {

    public ConditionalExprNode(final Node cond, final Node then, final Node els) {
        super(cond, then, els);
        Util.expect(cond instanceof Expression);
        Util.expect(then instanceof Expression);
        Util.expect(then instanceof Expression);
    }

    @Override
    public String getSource(int indent) {
        return String.format("%s ? %s : %s", child(0).getSource(indent), child(1).getSource(indent), child(2).getSource(indent));
    }

}
