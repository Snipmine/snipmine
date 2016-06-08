package de.unisaarland.cs.st.alsclo.snipmine.test;

import de.unisaarland.cs.st.alsclo.snipmine.ast.ASTBuilder;
import de.unisaarland.cs.st.alsclo.snipmine.ast.ASTContainer;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.test.util.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class ASTSourceTests {

    private final String imports;
    private final String body;

    public ASTSourceTests(String imports, String body) {
        this.imports = imports;
        this.body = body;
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                /*0*/ {"", "{String[] s = new String[]{};}"},
                /*1*/ {"", "{int[][] s = new int[4][3];;}"},
                /*2*/ {"", "{String[] s = new String[]{\"a\"};String a = s[0];}"},
                /*3*/ {"", "{while(true){break;continue;}}"},
                /*4*/ {"", "{label:while(true)break label;}"},
                /*5*/ {"", "{if(false) exit(0); else print('a');}"},
                /*6*/ {"", "{return; return 0;}"},
                /*7*/ {"", "{int a = (int)0;}"},
                /*8*/ {"", "{int a; a = 0;}"},
                /*9*/ {"import java.util.*;", "{List l = new LinkedList(); for(Object o:l){System.out.println(o);}}"},
                /*10*/ {"", "{int a = b ? c : d;}"},
                /*11*/ {"", "{int a = var;}"},
        });
    }

    @Test
    public void test() {
        final ASTContainer con = ASTBuilder.build(imports + "public class Test{int var=0;public void a()" + body + "}");
        final Node block = con.iterator().next();
        String result = block.getSource(0);
        System.out.println(result);
        assertEquals(Util.blur(body), Util.blur(result));
    }
}
