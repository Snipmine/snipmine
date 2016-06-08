package de.unisaarland.cs.st.alsclo.snipmine.repoman;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.RateLimitHandler;

import java.io.IOException;

public class RepoMan {

    private final GitHub client;

    private final RepoBag bag;
    private final BestFirstIterator iter1;

    public RepoMan(final RepoBag bag) throws IOException {
        this.bag = bag;
        GitHubBuilder builder = new GitHubBuilder();
        builder.withOAuthToken("9ec14dda1104a6bceef8c9e082db25f0cfb93239");
        builder.withRateLimitHandler(RateLimitHandler.WAIT);
        this.client = builder.build();
        iter1 = new BestFirstIterator(client);
    }

    public Repository getNext() {
        Repository next;
        do {
            if (iter1.hasNext()) {
                next = iter1.next();
            } else {
                throw new IllegalStateException();
            }
        } while (bag.contains(next));
        bag.finish(next);
        return next;
    }

    public static void main(String[] args) throws IOException {
        RepoMan r = new RepoMan(new RepoBag());
        for (int i = 0; i < 300; i++) {
            System.out.println(r.getNext());
        }
    }

}
