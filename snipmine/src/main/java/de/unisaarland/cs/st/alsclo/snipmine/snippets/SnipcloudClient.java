package de.unisaarland.cs.st.alsclo.snipmine.snippets;

import de.unisaarland.cs.st.alsclo.snipmine.Config;
import de.unisaarland.cs.st.alsclo.snipmine.ast.ASTContainer;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr.IdentifierNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snipcloud.client.ApiEndpoint;
import org.snipcloud.client.BasicAuthentication;
import org.snipcloud.client.RestClient;
import org.snipcloud.core.Snippet;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Alex Schlosser
 */
public class SnipcloudClient {
    private static final Logger L = LoggerFactory.getLogger(SnipcloudClient.class);

    private final BlockingQueue<RestClient> pool = new LinkedBlockingQueue<>();

    public SnipcloudClient() {
        ApiEndpoint api = new ApiEndpoint() {
            @Override
            public KeyStore getKeyStore() {
                return null;
            }

            @Override
            public URI getBaseUrl() {
                try {
                    return new URI(Config.getSnipcloudUrl());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean isProxy() {
                return false;
            }
        };
        long id = new RestClient(api).getUserByName(Config.getSnipcloudUser()).getId();
        for (int i = 0; i < Config.getConcurrentConnections(); i++) {
            pool.add(new RestClient(api, new BasicAuthentication(id, Config.getSnipcloudPw())));
        }
    }

    private static String blur(String s) {
        return s.replaceAll("\\s", "").replaceAll("`.*?`", "");
    }

    private Stream<String> importTags(List<String> imports) {
        return imports.stream().map(x -> "sm:import:" + x + ",1");
    }

    private Stream<String> inputTags(Set<IdentifierNode> in) {
        return in.stream().map(x -> "sm:input:" + x.toString() + "=" + x.getType());
    }

    private Stream<String> outputTags(Set<IdentifierNode> out) {
        return out.stream().map(x -> "sm:output:" + x.toString() + "=" + x.getType());
    }

    public void addSnippet(ASTContainer con, Node ast) {
        String hashcodeTag = "sm:hc:" + Integer.toHexString(ast.hashCode());
        String source = ast.getSource(0);
        List<String> tags = new ArrayList<>();
        tags.add(hashcodeTag);
        tags.add("sm:order:" + ast.getOrder());
        tags.add("sm:depth:" + ast.getDepth());
        tags.add("sm:src:" + con.getPath());
        importTags(con.getImports()).forEach(tags::add);
        inputTags(ast.getInputs(new HashSet<>())).forEach(tags::add);
        outputTags(ast.getOutputs()).forEach(tags::add);

        //Get an available restclient
        RestClient client;
        try {
            client = pool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Get all snippets with tag snipmine-hc:<hash>
        List<Snippet> bucket = client.getSnippetsByTag(hashcodeTag);
        Optional<Snippet> o = bucket.stream().filter(x -> blur(source).equals(blur(x.getCode()))).findAny();
        // if one of the snippets is equal to the current one
        if (o.isPresent()) {
            // fix its tags and increase usecount
            Snippet s = o.get();
            client.use(s.getId());
            List<String> remote = s.getTags().stream().map(t -> {
                if (t.startsWith("sm:import:")) {
                    String[] imp = t.split(",");
                    for (String localTag : tags) {
                        if (localTag.startsWith(imp[0])) {
                            tags.remove(localTag);
                            return imp[0] + "," + (Integer.parseInt(imp[1]) + 1);
                        }
                    }
                }
                return t;
            }).collect(Collectors.toList());
            tags.stream().filter(x -> x.startsWith("sm:import:")).forEach(remote::add);
            client.setTags(s.getId(), remote);
        } else {
            // submit this as a new snippet
            client.newSnippet(new Snippet(tags, source));
        }
        //Put the client instance back in the pool
        pool.add(client);
    }

    public static void main(String[] args) {
        System.out.println(blur("`Placeholder` f`glarb`"));
    }
}
