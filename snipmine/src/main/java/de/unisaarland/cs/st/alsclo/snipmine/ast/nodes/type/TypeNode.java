package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.type;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;

public class TypeNode extends Node {

    private static final int PRIME = 31;

    private final String name;
    private String qualifiedName;

    public TypeNode(final String name, final String qualifiedName) {
        super(PRIME * (PRIME + name.hashCode()) + qualifiedName.hashCode());
        this.name = name;
        this.qualifiedName = qualifiedName;
    }

    @Override
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        TypeNode typeNode = (TypeNode) o;
        if (!name.equals(typeNode.name)) return false;
        return qualifiedName.equals(typeNode.qualifiedName);

    }

    @Override
    public String getSource(int indent) {
        return name;
    }
}
