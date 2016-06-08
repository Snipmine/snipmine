package de.unisaarland.cs.st.alsclo.snipmine.repoman;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Alex Schlosser
 */
public class RepoBag {

    public SortedSet<Repository> closed;

    public RepoBag() {
        closed = new TreeSet<>();
    }

    public RepoBag(File src) throws IOException {
        Gson g = new Gson();
        JsonParser p = new JsonParser();
        closed = g.fromJson(p.parse(new FileReader(src)), new TypeToken<SortedSet<Repository>>() {
        }.getType());
    }

    public void writeTo(File dst) throws IOException {
        Gson g = new Gson();
        String json = g.toJson(closed);
        FileWriter w = new FileWriter(dst);
        w.write(json);
        w.close();
    }

    public boolean contains(Repository r) {
        return closed.contains(r);
    }

    public void finish(Repository r) {
        closed.add(r);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepoBag repoBag = (RepoBag) o;
        return closed.equals(repoBag.closed);
    }

    @Override
    public int hashCode() {
        return closed.hashCode();
    }
}
