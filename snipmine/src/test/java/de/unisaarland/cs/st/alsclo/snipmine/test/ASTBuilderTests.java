package de.unisaarland.cs.st.alsclo.snipmine.test;

import de.unisaarland.cs.st.alsclo.snipmine.ast.ASTBuilder;
import de.unisaarland.cs.st.alsclo.snipmine.ast.ASTContainer;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr.*;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.stmt.*;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.stmt.LoopControlNode.Type;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.type.ArrayTypeNode;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.type.TypeNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ASTBuilderTests {

    @Test
    public void testArrayAST1() {
        final String code = "public class Test{public void a(){String[] s = new String[]{};}}";
        final ASTContainer con = ASTBuilder.build(code);
        final Node type = new ArrayTypeNode(new TypeNode("String", "java.lang.String"), 1);
        final Node c = new ArrayCreationNode(type, new Node[0], new ArrayInitNode(new Node[0]));
        final Node f = new DeclFragmentNode(new IdentifierNode("s0", "java.lang.String[]", true), c);
        final Node stmt = new ExprStatementNode(new DeclExprNode(type, new Node[]{f}));
        final Node block = new BlockNode(new Node[]{stmt});
        assertEquals(8, block.getDepth());
        assertEquals(12, block.getOrder());
        assertEquals(block, con.iterator().next());
    }

    @Test
    public void testArrayAST2() {
        final String code = "public class Test{public void a(){int[][] s = new int[4][3];}}";
        final ASTContainer con = ASTBuilder.build(code);
        final Node type = new ArrayTypeNode(new TypeNode("int", "int"), 2);
        final Node c = new ArrayCreationNode(type, new Node[]{new LiteralNode("4"), new LiteralNode("3")}, null);
        final Node f = new DeclFragmentNode(new IdentifierNode("s0", "int[][]", true), c);
        final Node stmt = new ExprStatementNode(new DeclExprNode(type, new Node[]{f}));
        final Node block = new BlockNode(new Node[]{stmt});
        assertEquals(8, block.getDepth());
        assertEquals(13, block.getOrder());
        assertEquals(block, con.iterator().next());
    }

    @Test
    public void testArrayAST3() {
        final String code = "public class Test{public void a(){String[] s = new String[]{\"a\"};}}";
        final ASTContainer con = ASTBuilder.build(code);
        final Node type = new ArrayTypeNode(new TypeNode("String", "java.lang.String"), 1);
        final Node c = new ArrayCreationNode(type, new Node[0],
                new ArrayInitNode(new Node[]{new LiteralNode("\"a\"")}));
        final Node f = new DeclFragmentNode(new IdentifierNode("s0", "java.lang.String[]", true), c);
        final Node stmt = new ExprStatementNode(new DeclExprNode(type, new Node[]{f}));
        final Node block = new BlockNode(new Node[]{stmt});
        assertEquals(8, block.getDepth());
        assertEquals(13, block.getOrder());
        assertEquals(block, con.iterator().next());
    }

    @Test
    public void testLoopAST1() {
        final String code = "public class Test{public void a(){while(true){break;continue;}}}";
        final ASTContainer con = ASTBuilder.build(code);
        final Node expr = new LiteralNode("true");
        final Node br = new LoopControlNode(Type.BREAK, null);
        final Node cn = new LoopControlNode(Type.CONTINUE, null);
        final Node btmp = new BlockNode(new Node[]{br, cn});
        final Node stmt = new WhileNode(WhileNode.LoopType.WHILE, expr, btmp);
        final Node block = new BlockNode(new Node[]{stmt});
        assertEquals(6, block.getDepth());
        assertEquals(8, block.getOrder());
        assertEquals(block, con.iterator().next());
    }

    @Test
    public void testLoopAST2() {
        final String code = "public class Test{public void a(){label:while(true)break label;}}";
        final ASTContainer con = ASTBuilder.build(code);
        final Node expr = new LiteralNode("true");
        final Node br = new LoopControlNode(Type.BREAK, new IdentifierNode("label", null, false));
        final Node stmt = new LabeledStatementNode(new IdentifierNode("label", null, false), new WhileNode(WhileNode.LoopType.WHILE, expr, br));
        final Node block = new BlockNode(new Node[]{stmt});
        assertEquals(6, block.getDepth());
        assertEquals(8, block.getOrder());
        assertEquals(block, con.iterator().next());
    }

    @Test
    public void testTypeLit() {
        final String code = "public class Test{public void a(){System.out.println(Test.class);}}";
        final ASTContainer con = ASTBuilder.build(code);
        final Node print = new ExprStatementNode(new MethodCallNode(new IdentifierNode("System.out", null, false),
                new IdentifierNode("println", "void(java.lang.Object)", false), new Node[]{new LiteralNode("Test.class")}));
        final Node block = new BlockNode(new Node[]{print});
        assertEquals(5, block.getDepth());
        assertEquals(7, block.getOrder());
        assertEquals(block, con.iterator().next());
    }

    @Test
    public void testParamtypes() {
        final String code = "import java.util.*; public class Test{public void a(){List<Integer> l = new LinkedList<Integer>(); Set<String> s = new HashSet<>();}}";
        final ASTContainer con = ASTBuilder.build(code);

        final Node l = new IdentifierNode("l0", "java.util.List<java.lang.Integer>", true);
        final Node f = new DeclFragmentNode(l, new ConstructorCallNode(new TypeNode("LinkedList<Integer>", "java.util.LinkedList<java.lang.Integer>"),
                new Node[0]));
        final Node stmt = new ExprStatementNode(new DeclExprNode(new TypeNode("List<Integer>", "java.util.List<java.lang.Integer>"), new Node[]{f}));

        final Node s = new IdentifierNode("s0", "java.util.Set<java.lang.String>", true);
        final Node fs = new DeclFragmentNode(s, new ConstructorCallNode(new TypeNode("HashSet<>", "java.util.HashSet"),
                new Node[0]));
        final Node stmt2 = new ExprStatementNode(new DeclExprNode(new TypeNode("Set<String>", "java.util.Set<java.lang.String>"), new Node[]{fs}));

        final Node block = new BlockNode(new Node[]{stmt, stmt2});
//        assertEquals(8, block.getDepth());
//        assertEquals(22, block.getOrder());
        assertEquals(block, con.iterator().next());
    }

    @Test
    public void testfinal() {
        final String code = "import java.util.*; public class Test{public void a(){List l = new LinkedList(); for(Object o:l){System.out.println(o);}}}";
        final ASTContainer con = ASTBuilder.build(code);
        final Node l = new IdentifierNode("l0", "java.util.List", true);
        final Node f = new DeclFragmentNode(l, new ConstructorCallNode(new TypeNode("LinkedList", "java.util.LinkedList"),
                new Node[0]));
        final Node stmt = new ExprStatementNode(new DeclExprNode(new TypeNode("List", "java.util.List"), new Node[]{f}));
        final Node param = new DeclExprNode(new TypeNode("Object", "java.lang.Object"), new Node[]{new DeclFragmentNode(
                new IdentifierNode("o0", "java.lang.Object", true), null)});
        final Node print = new ExprStatementNode(new MethodCallNode(new IdentifierNode("System.out", null, false),
                new IdentifierNode("println", "void(java.lang.Object)", false), new Node[]{new IdentifierNode("o0", "java.lang.Object", false)}));
        final Node loop = new ForEachNode(param, new IdentifierNode("l0", "java.util.List", false), new BlockNode(new Node[]{print}));
        final Node block = new BlockNode(new Node[]{stmt, loop});
        assertEquals(8, block.getDepth());
        assertEquals(22, block.getOrder());
        assertEquals(block, con.iterator().next());
    }
}
