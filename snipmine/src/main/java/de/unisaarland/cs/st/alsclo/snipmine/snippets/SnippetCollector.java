package de.unisaarland.cs.st.alsclo.snipmine.snippets;

import de.unisaarland.cs.st.alsclo.snipmine.Config;
import de.unisaarland.cs.st.alsclo.snipmine.ast.ASTContainer;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * @author Alex Schlosser
 */
public class SnippetCollector implements Closeable {

    final ExecutorService executor = Executors.newWorkStealingPool();

    public void collect(final ASTContainer ast, BiConsumer<ASTContainer, Node> client) {
        for (Node n : ast) {
            n.computePossibleSnippets(x -> {
                        if (x.getOrder() > 2 && x.getDepth() > 2 && x.getDepth() < Config.getMaxDepth() && x.getOrder() < Config.getMaxOrder(x.getDepth())) {
                            executor.execute(() -> client.accept(ast, x));
                        }
                    }
            );
        }
    }

    @Override
    public void close() throws IOException {
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
        if (!executor.isTerminated()) {
            throw new IOException("Not all tasks were finished!");
        }
    }
}
