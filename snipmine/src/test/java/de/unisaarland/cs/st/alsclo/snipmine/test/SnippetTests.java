package de.unisaarland.cs.st.alsclo.snipmine.test;

import de.unisaarland.cs.st.alsclo.snipmine.ast.ASTBuilder;
import de.unisaarland.cs.st.alsclo.snipmine.ast.ASTContainer;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

public class SnippetTests {


    @Test
    public void test() {
        final String code = "import java.util.*;public class Test{public void a(){List<String> l = new LinkedList<>(); for(String o:l){System.out.println(o);continue;}}}";
        final ASTContainer con = ASTBuilder.build(code);
        Node block = con.iterator().next();
        Collection<Node> snippets = block.computePossibleSnippets(x -> {
            System.out.println(x);
            x.getInputs(new HashSet<>()).forEach(y -> System.out.println("INput: "+ y.getType()));
        });
        assertEquals(9, snippets.size());
    }
}
