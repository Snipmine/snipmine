package de.unisaarland.cs.st.alsclo.snipmine.ast;

import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.Node;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.expr.*;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.stmt.*;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.type.ArrayTypeNode;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.type.TypeNode;
import de.unisaarland.cs.st.alsclo.snipmine.ast.nodes.type.UnionTypeNode;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.*;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ASTBuilder extends FilteredASTVisitor {

    private final ASTContainer container;
    private final Set<String> globalIdentifiers = new HashSet<>();
    private final LinkedList<Node> stack = new LinkedList<>();

    private RenamingScope scope = null;

    private int classLevel = 0;

    private ASTBuilder(String path) {
        container = new ASTContainer(path);
    }

    public static ASTContainer build(String source) {
        return build(source, null, null, null, true);
    }

    public static ASTContainer build(final String source, String[] classpath, String[] sourcePath, String[] encodings, boolean inheritCP) {
        final ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource(source.toCharArray());
        parser.setResolveBindings(true);
        parser.setEnvironment(classpath, sourcePath, encodings, inheritCP);
        parser.setUnitName("ASTBuilder");
        return build0(new File("."), parser);
    }

    public static ASTContainer build(final File source, String[] classpath, String[] sourcePath, String[] encodings, boolean inheritCP) throws IOException {
        final ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setSource(FileUtils.readFileToString(source).toCharArray());
        parser.setResolveBindings(true);
        parser.setEnvironment(classpath, sourcePath, encodings, inheritCP);
        parser.setUnitName("ASTBuilder");
        return build0(source, parser);
    }

    private static ASTContainer build0(final File source, final ASTParser p) {
        final ASTNode n = p.createAST(null);
        final ASTBuilder b = new ASTBuilder(source.getAbsolutePath());
//        n.accept(new PrintASTVisitor());
        n.accept(b);
        return b.getContainer();
    }

    public ASTContainer getContainer() {
        return container;
    }

    @Override
    public boolean visit(final ImportDeclaration node) {
        container.addImport(node.getName().getFullyQualifiedName());
        return false;
    }

    @Override
    public boolean visit(final MethodDeclaration node) {
        stack.clear();
        scope = new RenamingScope(globalIdentifiers);
        //Don't continue if this method has no body (abstract, interface)
        return node.getBody() != null;
    }

    @Override
    public void endVisit(final MethodDeclaration node) {
        if (!stack.isEmpty()) {
            Node block = stack.pop();
            assert block instanceof BlockNode;
            container.addMethodBody(block);
        }
        scope = null;
    }

    @Override
    public boolean visit(TypeDeclaration node) {
        if (classLevel > 0) {
            throw new RuntimeException("Nested classes not supported");
        }
        classLevel++;
        return true;
    }

    @Override
    public void endVisit(final TypeDeclaration node) {
        classLevel--;
    }

    @Override
    public void endVisit(final ArrayAccess node) {
        final Node r = stack.pop();
        final Node l = stack.pop();
        stack.push(new ArrayAccessNode(l, r));
    }

    @Override
    public void endVisit(final ArrayCreation node) {
        Node init = null;
        if (node.getInitializer() != null) {
            init = stack.pop();
        }
        final Node[] dimensions = new Node[node.dimensions().size()];
        for (int i = dimensions.length - 1; i >= 0; i--) {
            dimensions[i] = stack.pop();
        }
        final Node type = stack.pop();
        stack.push(new ArrayCreationNode(type, dimensions, init));
    }

    @Override
    public void endVisit(final ArrayInitializer node) {
        final Node[] expr = new Node[node.expressions().size()];
        for (int i = expr.length - 1; i >= 0; i--) {
            expr[i] = stack.pop();
        }
        stack.push(new ArrayInitNode(expr));
    }

    @Override
    public void endVisit(final ArrayType node) {
        stack.push(new ArrayTypeNode(stack.pop(), node.getDimensions()));
    }

    @Override
    public void endVisit(final Assignment node) {
        final Node r = stack.pop();
        final Node l = stack.pop();
        stack.push(new AssignmentNode(l, node.getOperator(), r));
    }

    @Override
    public boolean visit(Block node) {
        scope = new RenamingScope(scope);
        return true;
    }

    @Override
    public void endVisit(final Block node) {
        final Node[] statements = new Node[node.statements().size()];
        for (int i = statements.length - 1; i >= 0; i--) {
            statements[i] = stack.pop();
        }
        stack.push(new BlockNode(statements));
        scope = scope.getParent();
    }

    @Override
    public void endVisit(final BooleanLiteral node) {
        stack.push(new LiteralNode(Boolean.toString(node.booleanValue())));
    }

    @Override
    public void endVisit(final BreakStatement node) {
        stack.push(new LoopControlNode(LoopControlNode.Type.BREAK, node.getLabel() == null ? null : stack.pop()));
    }

    @Override
    public void endVisit(final CastExpression node) {
        final Node expr = stack.pop();
        final Node type = stack.pop();
        stack.push(new TypeCastNode(type, expr));
    }

    @Override
    public void endVisit(final CatchClause node) {
        final Node block = stack.pop();
        final Node param = stack.pop();
        stack.push(new CatchNode(param, block));
    }

    @Override
    public void endVisit(final CharacterLiteral node) {
        stack.push(new LiteralNode(node.getEscapedValue()));
    }

    @Override
    public void endVisit(final ClassInstanceCreation node) {
        final Node[] args = new Node[node.arguments().size()];
        for (int i = args.length - 1; i >= 0; i--) {
            args[i] = stack.pop();
        }
        final Node type = stack.pop();
        stack.push(new ConstructorCallNode(type, args));
    }

    @Override
    public void endVisit(final ConditionalExpression node) {
        final Node els = stack.pop();
        final Node then = stack.pop();
        final Node cond = stack.pop();
        stack.push(new ConditionalExprNode(cond, then, els));
    }

    @Override
    public void endVisit(final ContinueStatement node) {
        stack.push(new LoopControlNode(LoopControlNode.Type.CONTINUE, node.getLabel() == null ? null : stack.pop()));
    }

    @Override
    public void endVisit(final ConstructorInvocation node) {
        if (!node.typeArguments().isEmpty()) {
            throw new RuntimeException(); //TODO
        }
        final Node[] args = new Node[node.arguments().size()];
        for (int i = args.length - 1; i >= 0; i--) {
            args[i] = stack.pop();
        }
        stack.push(new ExprStatementNode(new MethodCallNode(null, new LiteralNode("this"), args)));
    }

    @Override
    public void endVisit(final DoStatement node) {
        final Node stmt = stack.pop();
        final Node expr = stack.pop();
        stack.push(new WhileNode(WhileNode.LoopType.DO_WHILE, expr, stmt));
    }

    @Override
    public void endVisit(final EmptyStatement node) {
        stack.push(new EmptyStatementNode());
    }

    @Override
    public void endVisit(final EnhancedForStatement node) {
        final Node body = stack.pop();
        final Node expr = stack.pop();
        final Node param = stack.pop();
        stack.push(new ForEachNode(param, expr, body));
    }

    @Override
    public void endVisit(final ExpressionStatement node) {
        stack.push(new ExprStatementNode(stack.pop()));
    }

    @Override
    public boolean visit(final FieldAccess node) {
        node.getExpression().accept(this);
        //Don't visit the identifier part
        return false;
    }

    @Override
    public void endVisit(final FieldAccess node) {
        SimpleName i = node.getName();
        ITypeBinding b = node.resolveTypeBinding();
        Node expr = stack.pop();
        if (expr.equals(new LiteralNode("this"))) {
            stack.push(new IdentifierNode("this." + i.getIdentifier(), b == null ? null : b.getQualifiedName(), false));
        } else {
            stack.push(new FieldAccessNode(expr, new LiteralNode(i.getIdentifier())));
        }
    }

    @Override
    public void endVisit(final ForStatement node) {
        if (node.initializers().size() > 1 || node.updaters().size() > 1) {
            throw new RuntimeException();// TODO
        }
        Node body = stack.pop();
        Node update = null;
        if (!node.updaters().isEmpty()) {
            update = stack.pop();
        }
        Node expression = null;
        if (node.getExpression() != null) {
            expression = stack.pop();
        }
        Node init = null;
        if (!node.initializers().isEmpty()) {
            init = stack.pop();
        }
        stack.push(new ForNode(init, expression, update, body));
    }

    @Override
    public void endVisit(final IfStatement node) {
        final Node el = node.getElseStatement() == null ? null : stack.pop();
        final Node then = stack.pop();
        final Node cond = stack.pop();
        stack.push(new IfNode(cond, then, el));
    }

    @Override
    public void endVisit(final InfixExpression node) {
        int c = 2 + node.extendedOperands().size();
        final Node[] exprs = new Node[c];
        for (int i = exprs.length - 1; i >= 0; i--) {
            exprs[i] = stack.pop();
        }
        stack.push(new InfixExprNode(node.getOperator(), exprs));
    }

    @Override
    public void endVisit(final InstanceofExpression node) {
        Node type = stack.pop();
        Node expr = stack.pop();
        stack.push(new InstanceofNode(expr, type));
    }

    @Override
    public void endVisit(final LabeledStatement node) {
        final Node stmt = stack.pop();
        final Node i = stack.pop();
        stack.push(new LabeledStatementNode(i, stmt));
    }

    @Override
    public void endVisit(final MethodInvocation node) {
        final Node[] args = new Node[node.arguments().size()];
        for (int i = args.length - 1; i >= 0; i--) {
            args[i] = stack.pop();
        }
        final Node name = stack.pop();
        final Node expr = node.getExpression() == null ? null : stack.pop();
        if (expr == null || expr.equals(new LiteralNode("this"))) {
            stack.push(new MethodCallNode(null, name, args));
        } else if (name instanceof LiteralNode) {
            stack.push(new MethodCallNode(expr, name, args));
        } else {
            stack.push(new MethodCallNode(expr, new LiteralNode(((IdentifierNode) name).getName()), args));
        }
    }

    @Override
    public void endVisit(final NullLiteral node) {
        stack.push(new LiteralNode("null"));
    }

    @Override
    public void endVisit(final NumberLiteral node) {
        stack.push(new LiteralNode(node.getToken()));
    }

    @Override
    public boolean visit(final ParameterizedType node) {
        StringBuilder name = new StringBuilder(node.getType().toString());
        name.append("<");
        List a = node.typeArguments();
        for (Object o : a) {
            name.append(o.toString());
            name.append(",");
        }
        if (!a.isEmpty()) {
            name.setLength(name.length() - 1);
        }
        name.append(">");
        String type = node.resolveBinding().getQualifiedName();
        stack.push(new TypeNode(name.toString(), type));
        return false;
    }

    @Override
    public void endVisit(final ParenthesizedExpression node) {
        stack.push(new ParanethesesExprNode(stack.pop()));
    }

    @Override
    public void endVisit(final PostfixExpression node) {
        stack.push(new PostfixExprNode(stack.pop(), node.getOperator()));
    }

    @Override
    public void endVisit(final PrefixExpression node) {
        stack.push(new PrefixExprNode(node.getOperator(), stack.pop()));
    }

    @Override
    public void endVisit(final PrimitiveType node) {
        stack.push(new TypeNode(node.getPrimitiveTypeCode().toString(), node.getPrimitiveTypeCode().toString()));
    }

    @Override
    public boolean visit(final QualifiedName node) {
        return false;
    }

    @Override
    public void endVisit(final QualifiedName node) {
        stack.push(new IdentifierNode(node.getFullyQualifiedName(), node.resolveTypeBinding().getQualifiedName(), false));
    }

    @Override
    public void endVisit(final SimpleName node) {
        String name = node.getFullyQualifiedName();
        String type = null;
        IBinding b = node.resolveBinding();
        if (b != null) {
            int kind = b.getKind();
            switch (kind) {
                case IBinding.VARIABLE: {
                    ITypeBinding b2 = ((IVariableBinding) b).getType();
                    type = b2.getQualifiedName();
                    if (scope != null) {
                        if (node.isDeclaration()) {
                            int i = scope.declare(name);
                            name = name + i;
                        } else {
                            Integer i = scope.lookup(name);
                            if (i != null) {
                                name = name + i;
                            }
                        }
                    }
                }
                break;
                case IBinding.METHOD: {
                    StringBuilder sb = new StringBuilder();
                    IMethodBinding b2 = (IMethodBinding) b;
                    sb.append(b2.getReturnType().getQualifiedName());
                    sb.append("(");
                    ITypeBinding[] params = b2.getParameterTypes();
                    if (params.length > 0) {
                        for (ITypeBinding p : params) {
                            sb.append(p.getQualifiedName());
                            sb.append(",");
                        }
                        sb.setLength(sb.length() - 1);
                    }
                    sb.append(")");
                    type = sb.toString();
                    if (node.isDeclaration()) {
                        globalIdentifiers.add(name);
                    }
                }
                break;
                default:
                    globalIdentifiers.add(name);
                    stack.push(new LiteralNode(name));
                    return;
            }
            stack.push(new IdentifierNode(name, type, node.isDeclaration()));
        } else {
            stack.push(new LiteralNode(name));
        }
    }

    @Override
    public void endVisit(final QualifiedType node) {
        throw new RuntimeException();// TODO
    }

    @Override
    public void endVisit(final ReturnStatement node) {
        stack.push(new ReturnNode(node.getExpression() == null ? null : stack.pop()));
    }

    @Override
    public boolean visit(final SimpleType node) {
        return false;
    }

    @Override
    public void endVisit(final SimpleType node) {
        ITypeBinding b = node.resolveBinding();
        stack.push(new TypeNode(node.getName().toString(),
                b != null ? b.getQualifiedName() : node.getName().toString()));
    }

    @Override
    public void endVisit(final SingleVariableDeclaration node) {
        final Node init = node.getInitializer() != null ? stack.pop() : null;
        final Node i = stack.pop();
        stack.push(new DeclFragmentNode(i, init));
        endVisitDeclExpr(1);
    }

    @Override
    public void endVisit(final StringLiteral node) {
        stack.push(new LiteralNode(node.getEscapedValue()));
    }

    @Override
    public void endVisit(final SuperFieldAccess node) {
        throw new RuntimeException();// TODO
    }

    @Override
    public void endVisit(final SuperConstructorInvocation node) {
        if (node.getExpression() != null || !node.typeArguments().isEmpty()) {
            throw new RuntimeException(); //TODO
        }
        final Node[] args = new Node[node.arguments().size()];
        for (int i = args.length - 1; i >= 0; i--) {
            args[i] = stack.pop();
        }
        stack.push(new ExprStatementNode(new MethodCallNode(null, new LiteralNode("super"), args)));
    }

    @Override
    public void endVisit(final SwitchCase node) {
        throw new RuntimeException();// TODO
    }

    @Override
    public void endVisit(final SwitchStatement node) {
        throw new RuntimeException();// TODO
    }

    @Override
    public void endVisit(final SynchronizedStatement node) {
        Node stmt = stack.pop();
        Node expr = stack.pop();
        stack.push(new SynchronizedNode(expr, stmt));
    }

    @Override
    public void endVisit(final ThisExpression node) {
        if (node.getQualifier() == null) {
            stack.push(new LiteralNode("this"));
        } else {
            throw new RuntimeException();//TODO
        }
    }

    @Override
    public void endVisit(final ThrowStatement node) {
        stack.push(new ThrowNode(stack.pop()));
    }

    @Override
    public void endVisit(final TryStatement node) {
        Node fin = null;
        if (node.getFinally() != null) {
            fin = stack.pop();
        }
        Node[] catches = new Node[node.catchClauses().size()];
        for (int i = catches.length - 1; i >= 0; i--) {
            catches[i] = stack.pop();
        }
        Node body = stack.pop();
        Node[] res = new Node[node.resources().size()];
        for (int i = res.length - 1; i >= 0; i--) {
            res[i] = stack.pop();
        }
        stack.push(new TryNode(res, body, catches, fin));
    }

    @Override
    public void endVisit(final TypeLiteral node) {
        stack.push(new LiteralNode(node.getType().toString() + ".class"));
    }

    @Override
    public boolean visit(final TypeLiteral node) {
        return false;
    }

    @Override
    public void endVisit(final UnionType node) {
        final Node[] types = new Node[node.types().size()];
        for (int i = types.length - 1; i >= 0; i--) {
            types[i] = stack.pop();
        }
        stack.push(new UnionTypeNode(types));
    }

    private void endVisitDeclExpr(final int fragcount) {
        final Node[] fragments = new Node[fragcount];
        for (int i = fragments.length - 1; i >= 0; i--) {
            fragments[i] = stack.pop();
        }
        stack.push(new DeclExprNode(stack.pop(), fragments));
    }

    @Override
    public void endVisit(final VariableDeclarationExpression node) {
        endVisitDeclExpr(node.fragments().size());
    }

    @Override
    public void endVisit(final VariableDeclarationStatement node) {
        endVisitDeclExpr(node.fragments().size());
        stack.push(new ExprStatementNode(stack.pop()));
    }

    @Override
    public void endVisit(final VariableDeclarationFragment node) {
        final Node init = node.getInitializer() != null ? stack.pop() : null;
        final Node i = stack.pop();
        stack.push(new DeclFragmentNode(i, init));
    }

    @Override
    public void endVisit(final WhileStatement node) {
        final Node stmt = stack.pop();
        final Node expr = stack.pop();
        stack.push(new WhileNode(WhileNode.LoopType.WHILE, expr, stmt));
    }

}
