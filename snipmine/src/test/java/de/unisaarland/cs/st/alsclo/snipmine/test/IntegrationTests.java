package de.unisaarland.cs.st.alsclo.snipmine.test;

import de.unisaarland.cs.st.alsclo.snipmine.ast.ASTBuilder;
import de.unisaarland.cs.st.alsclo.snipmine.ast.ASTContainer;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr.IdentifierNode;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class IntegrationTests {

    @Test
    public void test() throws IOException {
        final ASTContainer con = ASTBuilder.build(IOUtils.toString(IntegrationTests.class.getResourceAsStream("/Qsort.java")));
        Node block = con.iterator().next();
        Set<IdentifierNode> inputs = new HashSet<>();
        inputs.add(new IdentifierNode("a0", "int[]", false));
        inputs.add(new IdentifierNode("si0", "int", false));
        inputs.add(new IdentifierNode("ei0", "int", false));
        inputs.add(new IdentifierNode("qsort", "void(int[],int,int)", false));
        assertEquals(inputs, block.getInputs(new HashSet<>()));
        Set<IdentifierNode> o = block.getOutputs();
        assertEquals(1, o.size());
        assertEquals(new IdentifierNode("tmp0", "int", true), o.iterator().next());
        Collection<Node> snippets = block.computePossibleSnippets(x->System.out.println(x + "\n"));
    }
}
