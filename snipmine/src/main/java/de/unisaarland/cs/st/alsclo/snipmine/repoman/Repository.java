package de.unisaarland.cs.st.alsclo.snipmine.repoman;

public class Repository implements Comparable<Repository> {

    private final String owner;
    private final String repo;
    private final int id;
    private final long timestamp;

    public Repository(final int id, final String owner, final String repo, final long timestamp) {
        this.id = id;
        this.owner = owner;
        this.repo = repo;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getRepo() {
        return repo;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return owner + "/" + repo;
    }

    @Override
    public int compareTo(Repository o) {
        return Integer.compare(id, o.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Repository that = (Repository) o;
        if (id != that.id) return false;
        if (timestamp != that.timestamp) return false;
        if (!owner.equals(that.owner)) return false;
        return repo.equals(that.repo);

    }

    @Override
    public int hashCode() {
        int result = owner.hashCode();
        result = 31 * result + repo.hashCode();
        result = 31 * result + id;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }
}
