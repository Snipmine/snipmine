package de.unisaarland.cs.st.alsclo.snipmine;

import de.unisaarland.cs.st.alsclo.snipmine.ast.ASTBuilder;
import de.unisaarland.cs.st.alsclo.snipmine.ast.ASTContainer;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.snippets.SnipcloudClient;
import de.unisaarland.cs.st.alsclo.snipmine.snippets.SnippetCollector;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.maven.shared.invoker.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


public class Snipmine {

    private static final Logger L = LoggerFactory.getLogger(Snipmine.class);

    private static File generateClassPathFile(File pom) {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(pom);
        request.setGoals( Collections.singletonList("dependency:build-classpath"));
        Properties p = new Properties();
        p.setProperty("mdep.outputFile", Config.getClasspathFileName());
        request.setProperties(p);

        Invoker invoker = new DefaultInvoker();
        try {
            InvocationResult r = invoker.execute(request);
            if (r.getExecutionException() == null && r.getExitCode() == 0){
                File cp = new File(pom.getParentFile(), Config.getClasspathFileName());
                if (cp.isFile()) {
                    return cp;
                }
            }
        } catch (MavenInvocationException e) {
            L.error("Could not invoke maven", e);
        }
        L.error("Maven returned non-zero error code");
        throw new RuntimeException("Could not build classpath for project: " + pom.getAbsolutePath());
    }

    private static String[] getSourcepath(File pom) {
        File mainSources = new File(pom.getParentFile(), "src/main/java");
        File testSources = new File(pom.getParentFile(), "src/test/java");
        List<String> sourcepath = new ArrayList<>();
        if (mainSources.isDirectory()) {
            sourcepath.add(mainSources.getAbsolutePath());
        }
        if (testSources.isDirectory()) {
            sourcepath.add(testSources.getAbsolutePath());
        }
        return sourcepath.toArray(new String[sourcepath.size()]);
    }

    public static void main(final String[] args) throws IOException {
        long startTime = System.currentTimeMillis();
        int files = 0;
        int failures = 0;
        if (args.length > 0) {
            File pom = new File(args[0]);
            if (pom.isDirectory()) {
                pom = new File(pom, "pom.xml");
            }
            if (pom.isFile()) {
                String[] sourcePaths = getSourcepath(pom);
                File cp = generateClassPathFile(pom);
                for (String src : sourcePaths) {
                    Iterator<File> iter = FileUtils.iterateFiles(new File(src), new SuffixFileFilter(".java"), TrueFileFilter.INSTANCE);
                    SnipcloudClient sc = new SnipcloudClient();
                    SnippetCollector col = new SnippetCollector();
                    while (iter.hasNext()) {
                        File n = iter.next();
                        L.info("Found source file " + n.toString());
                        //TODO maybe look at encoding settings in maven
                        try {
                            files++;
                            final ASTContainer con = ASTBuilder.build(n, FileUtils.readFileToString(cp).split(File.pathSeparator), sourcePaths, null, true);
//                            for (Node node : con) {
//                                node.computePossibleSnippets((Node x)->{
//                                    assert (System.currentTimeMillis() - startTime < 15*60*1000) : "TIMEOUT";
//                                });
//                            }
//                        col.collect(con, (x,y)-> {});
//                        col.collect(con, (x,y)-> System.out.println(y));
                            col.collect(con, sc::addSnippet);
                        } catch (Exception e) {
                            failures++;
                            e.printStackTrace();
                        }
                    }
                    col.close();
                }
            } else {
                System.out.println("Could not open pom.xml file");
            }
        } else {
            System.out.println("Usage: snipmine </path/to/pom.xml>");
        }
        System.out.printf("STATISTICS Execution time: %ds, Error ratio: %d/%d\n", (System.currentTimeMillis()-startTime)/1000, failures, files);
    }
}
