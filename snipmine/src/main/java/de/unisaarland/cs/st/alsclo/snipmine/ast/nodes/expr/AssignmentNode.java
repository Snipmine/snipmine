package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;
import org.eclipse.jdt.core.dom.Assignment.Operator;

public class AssignmentNode extends Node implements Expression {

    private final Operator o;

    public AssignmentNode(final Node l, final Operator o, final Node r) {
        super(o.hashCode(), l, r);
        this.o = o;
        Util.expect(l instanceof Expression);
        Util.expect(r instanceof Expression);
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj) && ((AssignmentNode) obj).o == o;
    }

    @Override
    public String getSource(int indent) {
        return child(0).getSource(indent) + " " + o.toString() + " " + child(1).getSource(indent);
    }

}
