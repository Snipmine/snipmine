package de.unisaarland.cs.st.alsclo.snipmine.test;

import de.unisaarland.cs.st.alsclo.snipmine.ast.ASTBuilder;
import de.unisaarland.cs.st.alsclo.snipmine.ast.ASTContainer;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import org.junit.Test;

public class RenameTests {


    @Test
    public void test() {
        final String code = "public class Test{private final int i = 0; public void a(boolean b){int a = 0; int i = 0; {int i = 0; a = i;}{int i = 0; a = i;}}}";
        final ASTContainer con = ASTBuilder.build(code);
        Node block = con.iterator().next();
        System.out.println(block.getSource(0));
    }
}
