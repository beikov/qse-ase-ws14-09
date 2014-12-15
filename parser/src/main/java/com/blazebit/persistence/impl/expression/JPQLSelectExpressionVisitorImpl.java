/*
 * Copyright 2014 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blazebit.persistence.impl.expression;

import com.blazebit.persistence.impl.predicate.AndPredicate;
import com.blazebit.persistence.impl.predicate.BetweenPredicate;
import com.blazebit.persistence.impl.predicate.BinaryExpressionPredicate;
import com.blazebit.persistence.impl.predicate.EqPredicate;
import com.blazebit.persistence.impl.predicate.GePredicate;
import com.blazebit.persistence.impl.predicate.GtPredicate;
import com.blazebit.persistence.impl.predicate.InPredicate;
import com.blazebit.persistence.impl.predicate.IsNullPredicate;
import com.blazebit.persistence.impl.predicate.LePredicate;
import com.blazebit.persistence.impl.predicate.LikePredicate;
import com.blazebit.persistence.impl.predicate.LtPredicate;
import com.blazebit.persistence.impl.predicate.MemberOfPredicate;
import com.blazebit.persistence.impl.predicate.Negatable;
import com.blazebit.persistence.impl.predicate.NotPredicate;
import com.blazebit.persistence.impl.predicate.OrPredicate;
import com.blazebit.persistence.impl.predicate.Predicate;
import com.blazebit.persistence.parser.JPQLSelectExpressionBaseVisitor;
import com.blazebit.persistence.parser.JPQLSelectExpressionLexer;
import com.blazebit.persistence.parser.JPQLSelectExpressionParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 *
 * @author Moritz Becker
 */
public class JPQLSelectExpressionVisitorImpl extends JPQLSelectExpressionBaseVisitor<Expression> {

    private final CommonTokenStream tokens;

    public JPQLSelectExpressionVisitorImpl(CommonTokenStream tokens) {
        this.tokens = tokens;
    }

    @Override
    public Expression visitFunctions_returning_numerics_default(JPQLSelectExpressionParser.Functions_returning_numerics_defaultContext ctx) {
        return handleFunction(ctx.getStart().getText(), ctx);
    }

    @Override
    public Expression visitOuter_expression(JPQLSelectExpressionParser.Outer_expressionContext ctx) {
        return handleFunction(ctx.getStart().getText(), ctx);
    }

    @Override
    public Expression visitCoalesce_expression(JPQLSelectExpressionParser.Coalesce_expressionContext ctx) {
        return handleFunction(ctx.getStart().getText(), ctx);
    }

    @Override
    public Expression visitFunctions_returning_numerics_size(JPQLSelectExpressionParser.Functions_returning_numerics_sizeContext ctx) {
        FunctionExpression func = handleFunction(ctx.getStart().getText(), ctx);
        ((PathExpression) func.getExpressions().get(0)).setUsedInCollectionFunction(true);
        return func;
    }

    @Override
    public Expression visitStringFunction(JPQLSelectExpressionParser.StringFunctionContext ctx) {
        return handleFunction(ctx.getStart().getText(), ctx);
    }

    @Override
    public Expression visitAggregateExpression(JPQLSelectExpressionParser.AggregateExpressionContext ctx) {
        return new AggregateExpression(ctx.distinct != null, ctx.funcname.getText(), (PathExpression) ctx.aggregate_argument().accept(this));
    }

    @Override
    public Expression visitCountStar(JPQLSelectExpressionParser.CountStarContext ctx) {
        return new AggregateExpression();
    }

    @Override
    public Expression visitNullif_expression(JPQLSelectExpressionParser.Nullif_expressionContext ctx) {
        return handleFunction(ctx.getStart().getText(), ctx);
    }

    private FunctionExpression handleFunction(String name, ParseTree ctx) {
        List<Expression> funcArgs = new ArrayList<Expression>(ctx.getChildCount());
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (!(ctx.getChild(i) instanceof TerminalNode)) {
                funcArgs.add(ctx.getChild(i).accept(this));
            }
        }
        return new FunctionExpression(name, funcArgs);
    }

    @Override
    public Expression visitFunction_invocation(JPQLSelectExpressionParser.Function_invocationContext ctx) {
        List<Expression> funcArgs = new ArrayList<Expression>(ctx.getChildCount());
        funcArgs.add(ctx.string_literal().accept(this));
        for (JPQLSelectExpressionParser.Function_argContext argCtx : ctx.args) {
            funcArgs.add(argCtx.accept(this));
        }
        return new FunctionExpression(ctx.getStart().getText(), funcArgs);
    }

    @Override
    public Expression visitGeneral_subpath(JPQLSelectExpressionParser.General_subpathContext ctx) {
        List<PathElementExpression> pathElements = new ArrayList<PathElementExpression>();
        pathElements.add((PathElementExpression) ctx.general_path_start().accept(this));
        for (JPQLSelectExpressionParser.General_path_elementContext generalPathElem : ctx.general_path_element()) {
            pathElements.add((PathElementExpression) generalPathElem.accept(this));
        }
        return new PathExpression(pathElements);
    }

    @Override
    public Expression visitPath(JPQLSelectExpressionParser.PathContext ctx) {
        PathExpression result = (PathExpression) ctx.general_subpath().accept(this);
        result.getExpressions().add((PathElementExpression) ctx.general_path_element().accept(this));
        return result;
    }

    @Override
    public Expression visitSingle_element_path_expression(JPQLSelectExpressionParser.Single_element_path_expressionContext ctx) {
        return new PathExpression(new ArrayList<PathElementExpression>(Arrays.asList((PathElementExpression) ctx.general_path_start().accept(this))));
    }

    @Override
    public Expression visitSimple_path_element(JPQLSelectExpressionParser.Simple_path_elementContext ctx) {
        return new PropertyExpression(ctx.identifier().getText());
    }

    @Override
    public Expression visitCollection_member_expression(JPQLSelectExpressionParser.Collection_member_expressionContext ctx) {
        PathExpression collectionPath = (PathExpression) ctx.collection_valued_path_expression().accept(this);
        collectionPath.setUsedInCollectionFunction(true);
        return new MemberOfPredicate(ctx.entity_or_value_expression().accept(this), collectionPath, ctx.not != null);
    }

    @Override
    public Expression visitEmpty_collection_comparison_expression(JPQLSelectExpressionParser.Empty_collection_comparison_expressionContext ctx) {
        CompositeExpression expr = accept(ctx.collection_valued_path_expression());
        expr.append(ctx.Empty_function().getText());
        ((PathExpression) expr.getExpressions().get(0)).setUsedInCollectionFunction(true);
        return expr;
    }

    @Override
    public Expression visitType_discriminator(JPQLSelectExpressionParser.Type_discriminatorContext ctx) {
        return new FunctionExpression(ctx.getStart().getText(), Arrays.asList(ctx.type_discriminator_arg().accept(this)));
    }

    @Override
    public Expression visitEntryFunction(JPQLSelectExpressionParser.EntryFunctionContext ctx) {
        return new FunctionExpression(ctx.name.getText(), Arrays.asList(ctx.collection_valued_path_expression().accept(this)));
    }

    @Override
    public Expression visitKey_value_expression(JPQLSelectExpressionParser.Key_value_expressionContext ctx) {
        return new FunctionExpression(ctx.name.getText(), Arrays.asList(ctx.collection_valued_path_expression().accept(this)));
    }

    @Override
    public Expression visitArray_expression(JPQLSelectExpressionParser.Array_expressionContext ctx) {
        return new ArrayExpression((PropertyExpression) ctx.simple_path_element().accept(this), unwrap(ctx.arithmetic_expression().accept(this)));
    }

    @Override
    public Expression visitArithmeticExpressionPlusMinus(JPQLSelectExpressionParser.ArithmeticExpressionPlusMinusContext ctx) {
        CompositeExpression expr = accept(ctx.arithmetic_expression());
        expr.append(getTextWithSurroundingHiddenTokens(ctx.op));
        acceptAndCompose(expr, ctx.arithmetic_term());
        return expr;
    }

    @Override
    public Expression visitBetweenArithmetic(JPQLSelectExpressionParser.BetweenArithmeticContext ctx) {
        return new BetweenPredicate(ctx.expr.accept(this), ctx.bound1.accept(this), ctx.bound2.accept(this), ctx.not != null);
    }

    @Override
    public Expression visitBetweenDatetime(JPQLSelectExpressionParser.BetweenDatetimeContext ctx) {
        return new BetweenPredicate(ctx.expr.accept(this), ctx.bound1.accept(this), ctx.bound2.accept(this), ctx.not != null);
    }

    @Override
    public Expression visitBetweenString(JPQLSelectExpressionParser.BetweenStringContext ctx) {
        return new BetweenPredicate(ctx.expr.accept(this), ctx.bound1.accept(this), ctx.bound2.accept(this), ctx.not != null);
    }

    @Override
    public Expression visitArithmeticPrimaryParanthesis(JPQLSelectExpressionParser.ArithmeticPrimaryParanthesisContext ctx) {
        CompositeExpression expr = accept(ctx.arithmetic_expression());
        expr.prepend("(" + tokenListToString(tokens.getHiddenTokensToLeft(ctx.getStart().getTokenIndex())).toString());
        expr.append(")");
        return expr;
    }

    @Override
    public Expression visitArithmeticMultDiv(JPQLSelectExpressionParser.ArithmeticMultDivContext ctx) {
        CompositeExpression expr = accept(ctx.term);
        expr.append(getTextWithSurroundingHiddenTokens(ctx.op));
        acceptAndCompose(expr, ctx.factor);

        return expr;
    }

    @Override
    public Expression visitConditionalTerm_and(JPQLSelectExpressionParser.ConditionalTerm_andContext ctx) {
        Predicate left = (Predicate) ctx.conditional_term().accept(this);
        if (left instanceof AndPredicate) {
            ((AndPredicate) left).getChildren().add((Predicate) ctx.conditional_factor().accept(this));
            return left;
        } else {
            return new AndPredicate(left, (Predicate) ctx.conditional_factor().accept(this));
        }
    }

    @Override
    public Expression visitConditionalPrimary(JPQLSelectExpressionParser.ConditionalPrimaryContext ctx) {
        return ctx.conditional_expression().accept(this);
    }

    @Override
    public Expression visitConditional_factor(JPQLSelectExpressionParser.Conditional_factorContext ctx) {
        Predicate p = (Predicate) ctx.conditional_primary().accept(this);

        if (ctx.not != null) {
            if (p instanceof Negatable) {
                ((Negatable) p).setNegated(true);
            } else {
                p = new NotPredicate(p);
            }
        }
        return p;
    }

    @Override
    public Expression visitConditionalExpression_or(JPQLSelectExpressionParser.ConditionalExpression_orContext ctx) {
        Predicate left = (Predicate) ctx.conditional_expression().accept(this);
        if (left instanceof OrPredicate) {
            ((OrPredicate) left).getChildren().add((Predicate) ctx.conditional_term().accept(this));
            return left;
        } else {
            return new OrPredicate(left, (Predicate) ctx.conditional_term().accept(this));
        }
    }

    @Override
    public Expression visitArithmetic_factor(JPQLSelectExpressionParser.Arithmetic_factorContext ctx) {
        if (ctx.signum != null) {
            CompositeExpression expr = accept(ctx.arithmetic_primary());
            expr.prepend(ctx.signum.getText() + tokenListToString(tokens.getHiddenTokensToRight(ctx.signum.getTokenIndex())));
            return expr;
        } else {
            return ctx.arithmetic_primary().accept(this);
        }
    }

    @Override
    public Expression visitNull_comparison_expression(JPQLSelectExpressionParser.Null_comparison_expressionContext ctx) {
        return new IsNullPredicate(ctx.getChild(0).accept(this), ctx.not != null);
    }

    @Override
    public Expression visitLike_expression(JPQLSelectExpressionParser.Like_expressionContext ctx) {
        return new LikePredicate(
                ctx.string_expression().accept(this),
                ctx.pattern_value().accept(this),
                true,
                ctx.escape_character() != null ? ctx.escape_character().accept(this).toString().charAt(1) : null,
                ctx.not != null);
    }

    @Override
    public Expression visitGeneral_case_expression(JPQLSelectExpressionParser.General_case_expressionContext ctx) {
        List<WhenClauseExpression> whenClauses = new ArrayList<WhenClauseExpression>();
        for (JPQLSelectExpressionParser.When_clauseContext whenClause : ctx.when_clause()) {
            whenClauses.add((WhenClauseExpression) whenClause.accept(this));
        }
        return new GeneralCaseExpression(whenClauses, ctx.scalar_expression().accept(this));
    }

    @Override
    public Expression visitSimple_case_expression(JPQLSelectExpressionParser.Simple_case_expressionContext ctx) {
        List<WhenClauseExpression> whenClauses = new ArrayList<WhenClauseExpression>();
        for (JPQLSelectExpressionParser.Simple_when_clauseContext whenClause : ctx.simple_when_clause()) {
            whenClauses.add((WhenClauseExpression) whenClause.accept(this));
        }
        return new SimpleCaseExpression(ctx.case_operand().accept(this), whenClauses, ctx.scalar_expression().accept(this));
    }

    @Override
    public Expression visitWhen_clause(JPQLSelectExpressionParser.When_clauseContext ctx) {
        return handleWhenClause(ctx.conditional_expression(), ctx.scalar_expression());
    }

    @Override
    public Expression visitSimple_when_clause(JPQLSelectExpressionParser.Simple_when_clauseContext ctx) {
        return handleWhenClause(ctx.scalar_expression(0), ctx.scalar_expression(1));
    }

    private WhenClauseExpression handleWhenClause(ParserRuleContext condition, ParserRuleContext result) {
        return new WhenClauseExpression((Predicate) condition.accept(this), result.accept(this));
    }

    @Override
    public Expression visitErrorNode(ErrorNode node) {
        throw new SyntaxErrorException("Parsing failed: " + node.getText());
    }

    @Override
    public Expression visitIn_expression(JPQLSelectExpressionParser.In_expressionContext ctx) {
        Expression inExpr;
        if (ctx.param == null) {
            CompositeExpression compositeInExpr = accept(ctx.literal(0));
            compositeInExpr.prepend("(");
            for (int i = 1; i < ctx.literal().size(); i++) {
                compositeInExpr.append(",");
                acceptAndCompose(compositeInExpr, ctx.literal(i));
            }
            compositeInExpr.append(")");
            inExpr = unwrap(compositeInExpr);
        } else {
            inExpr = ctx.Input_parameter().accept(this);
        }
        return new InPredicate(ctx.getChild(0).accept(this), inExpr, ctx.not != null);
    }

    @Override
    public Expression visitTerminal(TerminalNode node) {
        if (node.getSymbol().getType() == JPQLSelectExpressionLexer.EOF) {
            return null;
        }
        switch (node.getSymbol().getType()) {
            case JPQLSelectExpressionLexer.Input_parameter:
                return new ParameterExpression(node.getText().substring(1));
            default:
                return new FooExpression(node.getText());
        }
    }

    @Override
    public Expression visitParseSimpleExpression(JPQLSelectExpressionParser.ParseSimpleExpressionContext ctx) {
        return unwrap(super.visitParseSimpleExpression(ctx));
    }

    @Override
    public Expression visitParseSimpleSubqueryExpression(JPQLSelectExpressionParser.ParseSimpleSubqueryExpressionContext ctx) {
        return unwrap(super.visitParseSimpleSubqueryExpression(ctx));
    }

    @Override
    public Expression visitParseOrderByClause(JPQLSelectExpressionParser.ParseOrderByClauseContext ctx) {
        return ctx.getChild(0).accept(this);
    }

    @Override
    public Expression visitEnum_literal(JPQLSelectExpressionParser.Enum_literalContext ctx) {
        return new FooExpression(ctx.path().accept(this).toString());
    }

    @Override
    public Expression visitComparisonExpression_string(JPQLSelectExpressionParser.ComparisonExpression_stringContext ctx) {
        return handleComparison(ctx.left, ctx.comparison_operator(), ctx.right);
    }

    @Override
    public Expression visitComparisonExpression_arithmetic(JPQLSelectExpressionParser.ComparisonExpression_arithmeticContext ctx) {
        return handleComparison(ctx.left, ctx.comparison_operator(), ctx.right);
    }

    @Override
    public Expression visitComparisonExpression_boolean(JPQLSelectExpressionParser.ComparisonExpression_booleanContext ctx) {
        return handleComparison(ctx.left, ctx.equality_comparison_operator(), ctx.right);
    }

    @Override
    public Expression visitComparisonExpression_datetime(JPQLSelectExpressionParser.ComparisonExpression_datetimeContext ctx) {
        return handleComparison(ctx.left, ctx.comparison_operator(), ctx.right);
    }

    @Override
    public Expression visitComparisonExpression_entity(JPQLSelectExpressionParser.ComparisonExpression_entityContext ctx) {
        return handleComparison(ctx.left, ctx.equality_comparison_operator(), ctx.right);
    }
    
    @Override
    public Expression visitComparisonExpression_enum(JPQLSelectExpressionParser.ComparisonExpression_enumContext ctx) {
        return handleComparison(ctx.left, ctx.equality_comparison_operator(), ctx.right);
    }

    BinaryExpressionPredicate handleComparison(ParseTree left, ParseTree comparisonOperator, ParseTree right) {
        BinaryExpressionPredicate pred = (BinaryExpressionPredicate) comparisonOperator.accept(this);
        pred.setLeft(left.accept(this));
        pred.setRight(right.accept(this));
        return pred;
    }

    @Override
    public Expression visitEqPredicate(JPQLSelectExpressionParser.EqPredicateContext ctx) {
        return new EqPredicate(false);
    }

    @Override
    public Expression visitNeqPredicate(JPQLSelectExpressionParser.NeqPredicateContext ctx) {
        return new EqPredicate(true);
    }

    @Override
    public Expression visitGtPredicate(JPQLSelectExpressionParser.GtPredicateContext ctx) {
        return new GtPredicate();
    }

    @Override
    public Expression visitGePredicate(JPQLSelectExpressionParser.GePredicateContext ctx) {
        return new GePredicate();
    }

    @Override
    public Expression visitLtPredicate(JPQLSelectExpressionParser.LtPredicateContext ctx) {
        return new LtPredicate();
    }

    @Override
    public Expression visitLePredicate(JPQLSelectExpressionParser.LePredicateContext ctx) {
        return new LePredicate();
    }

    @Override
    public Expression visitChildren(RuleNode node) {
        CompositeExpression result = null;
        int n = node.getChildCount();

        if (shouldVisitNextChild(node, result)) {
            if (n > 0 && shouldVisitNextChild(node, result)) {
                if (n == 1) {
                    return node.getChild(0).accept(this);
                } else {
                    result = accept(node.getChild(0));
                    for (int i = 1; i < n; i++) {
                        if (!shouldVisitNextChild(node, result)) {
                            break;
                        }

                        ParseTree c = node.getChild(i);
                        acceptAndCompose(result, c);
                    }
                }
            }
        }

        return result;
    }

    private StringBuilder getTextWithSurroundingHiddenTokens(Token token) {
        StringBuilder sb = new StringBuilder();
        List<Token> hiddenTokens = tokens.getHiddenTokensToLeft(token.getTokenIndex());
        if (hiddenTokens != null) {
            for (Token t : hiddenTokens) {
                sb.append(t.getText());
            }
        }
        sb.append(token.getText());
        hiddenTokens = tokens.getHiddenTokensToRight(token.getTokenIndex());
        if (hiddenTokens != null) {
            for (Token t : hiddenTokens) {
                sb.append(t.getText());
            }
        }
        return sb;
    }

    private CompositeExpression acceptAndCompose(CompositeExpression composite, ParseTree ruleContext) {
        Expression expr = ruleContext.accept(this);
        if (expr != null) {
            composite.append(expr);
        }

        return composite;
    }

    private CompositeExpression accept(ParseTree ruleContext) {
        Expression expr = ruleContext.accept(this);
        CompositeExpression composite;
        if (expr instanceof CompositeExpression) {
            composite = (CompositeExpression) expr;
        } else {
            composite = new CompositeExpression(new ArrayList<Expression>(Arrays.asList(expr)));
        }
        return composite;
    }

    private StringBuilder tokenListToString(List<Token> tokens) {
        StringBuilder sb = new StringBuilder();
        if (tokens != null) {
            for (Token t : tokens) {
                sb.append(t.getText());
            }
        }
        return sb;
    }

    private Expression unwrap(Expression expr) {
        if (expr instanceof CompositeExpression) {
            CompositeExpression composite = (CompositeExpression) expr;
            if (composite.getExpressions().size() == 1) {
                // recursion should not be necessary;
                return composite.getExpressions().get(0);
            }
        }
        return expr;
    }

    /**
     *
     * @param start (exclusive)
     * @param end (exclusive)
     * @return
     */
    private StringBuilder getText(Token start, Token end) {
        int startIndex = start.getTokenIndex() + 1;
        int endIndex = end.getTokenIndex();
        if (startIndex < endIndex) {
            StringBuilder sb = new StringBuilder();
            for (int i = startIndex; i < endIndex; i++) {
                sb.append(tokens.get(i).getText());
            }
            return sb;
        }
        return null;
    }
}
