package de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.stmt;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * @author Alex Schlosser
 */
public class TryNode extends Node implements Statement {
    private final int resCount;
    private final int catchCount;


    public TryNode(final Node[] res, final Node body, final Node[] catches, final Node fin) {
        this(ArrayUtils.addAll(ArrayUtils.addAll(res, body), ArrayUtils.addAll(catches, fin)), res.length, catches.length);
        //TODO asserts
    }

    private TryNode(Node[] children, int resCount, int catchCount) {
        super(children);
        this.resCount = resCount;
        this.catchCount = catchCount;
    }

    private Node copy(Node altBody) {
        Node[] tmp = new Node[getChildCount()];
        for (int i = 0; i < getChildCount(); i++) {
            tmp[i] = child(i);
        }
        tmp[resCount] = altBody;
        return new TryNode(tmp, resCount, catchCount);
    }

    @Override
    public Collection<Node> computePossibleSnippets(final Consumer<Node> col) {
        //We will keep the try statement intact but permutate only the body
        //TODO catchclause snippets
        Node body = child(resCount);
        Collection<Node> subs = body.computePossibleSnippets(col)
                .stream().map(this::copy).collect(Collectors.toList());
        subs.forEach(col);
        return subs;
    }

    @Override
    public String getSource(final int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append("try ");
        if (resCount > 0) {
            sb.append('(');
            sb.append(child(0).getSource(indent));
            for (int i = 1; i < resCount; i++) {
                sb.append(';');
                sb.append(child(i).getSource(indent));
            }
            sb.append(')');
        }
        sb.append(child(resCount).getSource(indent));
        for (int i = resCount + 1; i < resCount + 1 + catchCount; i++) {
            sb.append(child(i).getSource(indent));
        }
        Node fin = child(resCount + 1 + catchCount);
        if (fin != null) {
            sb.append(" finally ");
            sb.append(fin.getSource(indent));
        }
        return sb.toString();
    }
}
