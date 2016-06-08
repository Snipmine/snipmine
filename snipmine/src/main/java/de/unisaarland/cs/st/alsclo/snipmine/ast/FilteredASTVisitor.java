package de.unisaarland.cs.st.alsclo.snipmine.ast;

import org.eclipse.jdt.core.dom.*;

/**
 * @author Alex Schlosser
 */
public abstract class FilteredASTVisitor extends ASTVisitor {

    @Override
    public boolean visit(final AnnotationTypeDeclaration node) {
        return false;
    }

    @Override
    public boolean visit(final AnnotationTypeMemberDeclaration node) {
        return false;
    }

    @Override
    public boolean visit(final AnonymousClassDeclaration node) {
        return false;
    }

    @Override
    public boolean visit(final AssertStatement node) {
        return false;
    }

    @Override
    public boolean visit(final BlockComment node) {
        return false;
    }

    @Override
    public boolean visit(final CreationReference node) {
        // TODO handle later?
        throw new RuntimeException();
    }

    @Override
    public boolean visit(final EnumConstantDeclaration node) {
        // TODO handle later?
        return false;
    }

    @Override
    public boolean visit(final EnumDeclaration node) {
        // TODO handle later?
        return false;
    }

    @Override
    public boolean visit(final ExpressionMethodReference node) {
        // TODO handle later?
        throw new RuntimeException();
    }

    @Override
    public boolean visit(final Initializer node) {
        return false;
    }

    @Override
    public boolean visit(final IntersectionType node) {
        // TODO handle later?
        throw new RuntimeException();
    }

    @Override
    public boolean visit(final Javadoc node) {
        return false;
    }

    @Override
    public boolean visit(final LambdaExpression node) {
        // TODO handle later?
        throw new RuntimeException();
    }

    @Override
    public boolean visit(final LineComment node) {
        return false;
    }

    @Override
    public boolean visit(final MarkerAnnotation node) {
        return false;
    }

    @Override
    public boolean visit(final MemberRef node) {
        return false;
    }

    @Override
    public boolean visit(final MemberValuePair node) {
        return false;
    }

    @Override
    public boolean visit(final MethodRef node) {
        return false;
    }

    @Override
    public boolean visit(final MethodRefParameter node) {
        return false;
    }

    @Override
    public boolean visit(final NormalAnnotation node) {
        return false;
    }

    @Override
    public boolean visit(final Dimension node) {
        return false;
    }

    @Override
    public boolean visit(final QualifiedType node) {
        return false;
    }

    @Override
    public boolean visit(final PackageDeclaration node) {
        return false;
    }

    @Override
    public boolean visit(final SingleMemberAnnotation node) {
        return false;
    }

    @Override
    public boolean visit(final SuperMethodInvocation node) {
        // TODO handle later?
        throw new RuntimeException();
    }

    @Override
    public boolean visit(final SuperMethodReference node) {
        // TODO handle later?
        throw new RuntimeException();
    }

    @Override
    public boolean visit(final TypeMethodReference node) {
        // TODO handle later?
        throw new RuntimeException();
    }

    @Override
    public boolean visit(final WildcardType node) {
        // TODO handle later?
        throw new RuntimeException();
    }

    public boolean visit(TypeParameter node) {
        return false;
    }

    //These are enabled in the current parser

    public boolean visit(ArrayAccess node) {
        return true;
    }

    public boolean visit(ArrayCreation node) {
        return true;
    }

    public boolean visit(ArrayInitializer node) {
        return true;
    }

    public boolean visit(ArrayType node) {
        return true;
    }

    public boolean visit(Assignment node) {
        return true;
    }

    public boolean visit(Block node) {
        return true;
    }

    public boolean visit(BooleanLiteral node) {
        return true;
    }

    public boolean visit(BreakStatement node) {
        return true;
    }

    public boolean visit(CastExpression node) {
        return true;
    }

    public boolean visit(CatchClause node) {
        return true;
    }

    public boolean visit(CharacterLiteral node) {
        return true;
    }

    public boolean visit(ClassInstanceCreation node) {
        return true;
    }

    public boolean visit(CompilationUnit node) {
        return true;
    }

    public boolean visit(ConditionalExpression node) {
        return true;
    }

    public boolean visit(ConstructorInvocation node) {
        return true;
    }

    public boolean visit(ContinueStatement node) {
        return true;
    }

    public boolean visit(DoStatement node) {
        return true;
    }

    public boolean visit(EmptyStatement node) {
        return true;
    }

    public boolean visit(EnhancedForStatement node) {
        return true;
    }

    public boolean visit(ExpressionStatement node) {
        return true;
    }

    public boolean visit(FieldAccess node) {
        return true;
    }

    public boolean visit(FieldDeclaration node) {
        return true;
    }

    public boolean visit(ForStatement node) {
        return true;
    }

    public boolean visit(IfStatement node) {
        return true;
    }

    public boolean visit(ImportDeclaration node) {
        return true;
    }

    public boolean visit(InfixExpression node) {
        return true;
    }

    public boolean visit(InstanceofExpression node) {
        return true;
    }

    public boolean visit(LabeledStatement node) {
        return true;
    }

    public boolean visit(MethodDeclaration node) {
        return true;
    }

    public boolean visit(MethodInvocation node) {
        return true;
    }

    public boolean visit(Modifier node) {
        return true;
    }

    public boolean visit(NameQualifiedType node) {
        return true;
    }

    public boolean visit(NullLiteral node) {
        return true;
    }

    public boolean visit(NumberLiteral node) {
        return true;
    }

    public boolean visit(ParameterizedType node) {
        return true;
    }

    public boolean visit(ParenthesizedExpression node) {
        return true;
    }

    public boolean visit(PostfixExpression node) {
        return true;
    }

    public boolean visit(PrefixExpression node) {
        return true;
    }

    public boolean visit(PrimitiveType node) {
        return true;
    }

    public boolean visit(QualifiedName node) {
        return true;
    }

    public boolean visit(ReturnStatement node) {
        return true;
    }

    public boolean visit(SimpleName node) {
        return true;
    }

    public boolean visit(SimpleType node) {
        return true;
    }

    public boolean visit(SingleVariableDeclaration node) {
        return true;
    }

    public boolean visit(StringLiteral node) {
        return true;
    }

    public boolean visit(SuperConstructorInvocation node) {
        return true;
    }

    public boolean visit(SuperFieldAccess node) {
        return true;
    }

    public boolean visit(SwitchCase node) {
        return true;
    }

    public boolean visit(SwitchStatement node) {
        return true;
    }

    public boolean visit(SynchronizedStatement node) {
        return true;
    }

    public boolean visit(TagElement node) {
        return true;
    }

    public boolean visit(TextElement node) {
        return true;
    }

    public boolean visit(ThisExpression node) {
        return true;
    }

    public boolean visit(ThrowStatement node) {
        return true;
    }

    public boolean visit(TryStatement node) {
        return true;
    }

    public boolean visit(TypeDeclaration node) {
        return true;
    }

    public boolean visit(TypeDeclarationStatement node) {
        return true;
    }

    public boolean visit(TypeLiteral node) {
        return true;
    }

    public boolean visit(UnionType node) {
        return true;
    }

    public boolean visit(VariableDeclarationExpression node) {
        return true;
    }

    public boolean visit(VariableDeclarationStatement node) {
        return true;
    }

    public boolean visit(VariableDeclarationFragment node) {
        return true;
    }

    public boolean visit(WhileStatement node) {
        return true;
    }

}
