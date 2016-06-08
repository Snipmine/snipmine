package de.unisaarland.cs.st.alsclo.snipmine.repoman;

import de.unisaarland.cs.st.alsclo.snipmine.Config;
import org.kohsuke.github.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * @author Alex Schlosser
 */
public class BestFirstIterator implements Iterator<Repository> {

    private static final Logger L = LoggerFactory.getLogger(BestFirstIterator.class);

    private PagedIterator<GHRepository> iter;
    private GHRepository tmp = null;

    public BestFirstIterator(GitHub g) {
        GHRepositorySearchBuilder b = g.searchRepositories();
        b.language("java");
        b.stars(">=" + Config.getMinStars());
        b.sort(GHRepositorySearchBuilder.Sort.STARS);
        iter = b.list().iterator();
    }

    private boolean isMVNProject(GHRepository r) {
        try {
            List<GHContent> dir = r.getDirectoryContent("/");
            for (GHContent c : dir) {
                if ("pom.xml".equals(c.getName())) {
                    return true;
                }
            }
        } catch (IOException e) {
            L.error("Exception while checking for mvn repo.", e);
        }
        return false;
    }

    @Override
    public boolean hasNext() {
        while (iter.hasNext()) {
            tmp = iter.next();
            if (isMVNProject(tmp)) {
                return true;
            }
//            L.info("Skipping non-mvn repo.");
        }
        return false;
    }

    @Override
    public Repository next() {
        return new Repository(tmp.getId(), tmp.getOwnerName(), tmp.getName(), System.currentTimeMillis());
    }

}
