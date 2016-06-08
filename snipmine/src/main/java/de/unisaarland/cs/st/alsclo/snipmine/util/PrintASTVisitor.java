package de.unisaarland.cs.st.alsclo.snipmine.util;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;

public class PrintASTVisitor extends ASTVisitor {

    int indent = 0;

    @Override
    public void preVisit(final ASTNode node) {
        for (int i = 0; i < indent; i++) {
            System.out.print("    ");
        }
        System.out.println(node.getClass().getSimpleName());
        indent++;
    }

    @Override
    public void postVisit(final ASTNode node) {
        indent--;
    }

}
