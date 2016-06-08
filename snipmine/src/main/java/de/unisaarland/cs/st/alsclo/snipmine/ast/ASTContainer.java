package de.unisaarland.cs.st.alsclo.snipmine.ast;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ASTContainer implements Iterable<Node> {
    private final String path;
    List<String> imports = new LinkedList<>();
    List<Node> methodBodies = new LinkedList<>();

    public ASTContainer(String path) {
        this.path = path;
    }

    public void addImport(final String name) {
        imports.add(name);
    }

    public void addMethodBody(final Node n) {
        methodBodies.add(n);
    }

    public List<String> getImports() {
        return Collections.unmodifiableList(imports);
    }

    @Override
    public Iterator<Node> iterator() {
        return methodBodies.iterator();
    }

    public String getPath() {
        return path;
    }
}
