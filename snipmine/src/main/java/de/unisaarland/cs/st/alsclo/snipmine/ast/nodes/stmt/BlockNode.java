package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.stmt;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr.IdentifierNode;
import de.unisaarland.cs.st.alsclo.snipmine.util.Util;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BlockNode extends Node implements Statement {

    private static final int MAXGAP = 42;

    public BlockNode(final Node[] statements) {
        super(new StatementListNode(statements));
        Util.expect(Util.allInstanceOf(statements, Statement.class));
    }

    private BlockNode(final Node list) {
        super(list);
    }

    @Override
    public String getSource(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(child(0).getSource(indent + 1));
        for (int i = 0; i < indent; i++) {
            sb.append("    ");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public Collection<Node> computePossibleSnippets(final Consumer<Node> col) {
        return child(0).computePossibleSnippets(col).stream().sequential()
                .map(x -> new BlockNode(x))
                .collect(Collectors.toList());
    }

    @Override
    public Set<IdentifierNode> getOutputs() {
        return child(0).getOutputs();
    }
}
