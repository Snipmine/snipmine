package de.unisaarland.cs.st.alsclo.snipmine.ast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Alex Schlosser
 */
public class RenamingScope {
    private final RenamingScope parent;
    private final Set<String> blacklist;
    private final Map<String, Integer> renameRules = new HashMap<>();

    public RenamingScope(RenamingScope parent) {
        this.parent = parent;
        blacklist = parent.blacklist;
    }

    public RenamingScope(Set<String> blacklist) {
        parent = null;
        this.blacklist = new HashSet<>(blacklist);
    }

    private int getNextIndex(String var) {
        int i = 0;
        while (blacklist.contains(var+i)) i++;
        return i;
    }

    public int declare(String var) {
        int i = getNextIndex(var);
        renameRules.put(var, i);
        blacklist.add(var + i);
        return i;
    }

    public Integer lookup(String var) {
        Integer i = renameRules.get(var);
        if (i == null && parent != null) {
            i = parent.lookup(var);
        }
        return i;
    }

    public RenamingScope getParent() {
        return parent;
    }
}
