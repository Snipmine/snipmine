package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.stmt;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;

/**
 * @author Alex Schlosser
 */
public class Placeholder extends Node implements Statement {

    public static final Placeholder INSTANCE = new Placeholder();

    private static final int HASHCODE = 127;

    private Placeholder() {
        super(HASHCODE);
    }

    @Override
    public String getSource(int indent) {
        return "`Placeholder`";
    }
}
