package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;

import java.util.HashSet;
import java.util.Set;

public class IdentifierNode extends Node implements Expression {

    private final String name;
    private String type;
    private boolean isDeclaration;

    public IdentifierNode(final String name, final String type, final boolean isDeclaration) {
        super(type.hashCode());
        this.name = name;
        this.type = type;
        this.isDeclaration = isDeclaration;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        final IdentifierNode that = (IdentifierNode) o;
        if (isDeclaration != that.isDeclaration) return false;
        if (!name.equals(that.name)) return false;
        return type != null ? type.equals(that.type) : that.type == null;

    }

    @Override
    public String getSource(int indent) {
        return "`" + name + "`";
    }

    public String getType() {
        return type;
    }
    public String getName() {
        return name;
    }

    @Override
    public Set<IdentifierNode> getInputs(final Set<String> s) {
        Set<IdentifierNode> r = new HashSet<>();
        if (type == null) {
            return r;
        }
        if (isDeclaration) {
            s.add(name);
        } else {
            if (!s.contains(name)) {
                r.add(this);
            }
        }
        return r;
    }

    @Override
    public Set<IdentifierNode> getOutputs() {
        Set<IdentifierNode> r = new HashSet<>();
        if (isDeclaration && type != null) {
            r.add(this);
        }
        return r;
    }
}
