package de.unisaarland.cs.st.alsclo.snipmine.test;

import de.unisaarland.cs.st.alsclo.snipmine.repoman.RepoBag;
import de.unisaarland.cs.st.alsclo.snipmine.repoman.RepoMan;
import de.unisaarland.cs.st.alsclo.snipmine.repoman.Repository;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class RepoManTests {
    private static final File tmp = new File("/tmp/fn2348r9asdil.snipmine.test.txt");

    @Test
    public void test1() throws IOException {
        RepoBag b = new RepoBag();
        RepoMan m = new RepoMan(b);
        for (int i = 0; i < 17; i++) {
            Repository r = m.getNext();
            b.finish(r);
            assertNotNull(r);
            assertNotNull(r.getOwner());
            assertTrue(r.getOwner().trim().length() > 0);
            assertTrue(r.getRepo().trim().length() > 0);
            assertNotNull(r.getRepo());
            assertTrue(r.getId() > 0);
            assertTrue(r.getTimestamp() > 0);
            assertTrue(b.contains(r));
        }
        b.writeTo(tmp);
        RepoBag b2 = new RepoBag(tmp);
        assertEquals(b, b2);
        tmp.delete();
    }

}
