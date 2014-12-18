package at.ac.tuwien.ase09.parser;

import java.util.BitSet;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import at.ac.tuwien.ase09.parser.PWatchParser.AdditiveExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.AndExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticAttributeContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticComparisonContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticFactorContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticFunctionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticLiteralContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticNestedContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticTermContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Arithmetic_expressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Arithmetic_factorContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Arithmetic_primaryContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Arithmetic_termContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ComparisonExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Comparison_expressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Comparison_operatorContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Conditional_expressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Conditional_factorContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Conditional_primaryContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Conditional_termContext;
import at.ac.tuwien.ase09.parser.PWatchParser.DateComparisonContext;
import at.ac.tuwien.ase09.parser.PWatchParser.DateExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Datetime_expressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Equality_comparison_operatorContext;
import at.ac.tuwien.ase09.parser.PWatchParser.FactorExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.MultiplicativeExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.NestedExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.OrExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.SimpleExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Simple_cond_expressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.StartContext;
import at.ac.tuwien.ase09.parser.PWatchParser.StringComparisonContext;
import at.ac.tuwien.ase09.parser.PWatchParser.StringExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.String_expressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.TermExpressionContext;

public class PWatchToEPLCompiler extends PWatchBaseVisitor<CharSequence> {

    private final CommonTokenStream tokens;

    public PWatchToEPLCompiler(CommonTokenStream tokens) {
        this.tokens = tokens;
    }

	public static String compile(String pwatchExpression, Long valuePaperId) {
		if (pwatchExpression == null) {
            throw new NullPointerException("pwatchExpression");
        }
		if (valuePaperId == null) {
            throw new NullPointerException("valuePaperId");
		}
        if (pwatchExpression.isEmpty()) {
            throw new IllegalArgumentException("pwatchExpression");
        }
        
        PWatchLexer lexer = new PWatchLexer(new ANTLRInputStream(pwatchExpression));
        lexer.removeErrorListeners();
        lexer.addErrorListener(ERR_LISTENER);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        boolean stringExpressionAllowed = false;
        PWatchParser parser = new PWatchParser(tokens, stringExpressionAllowed);
        parser.removeErrorListeners();
        parser.addErrorListener(ERR_LISTENER);
        
        ParserRuleContext ctx = parser.arithmetic_primary();
        
        PWatchToEPLCompiler visitor = new PWatchToEPLCompiler(tokens);
        System.out.println(ctx.toStringTree());
        return visitor.visit(ctx).toString();
	}

    protected static final ANTLRErrorListener ERR_LISTENER = new ANTLRErrorListener() {

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            throw new SyntaxErrorException("line " + line + ":" + charPositionInLine + " " + msg);
        }

        @Override
        public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
        }

        @Override
        public void reportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, ATNConfigSet configs) {
        }

        @Override
        public void reportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
        }
    };

	@Override
	public CharSequence visitArithmeticNested(ArithmeticNestedContext ctx) {
		CharSequence nested = ctx.arithmetic_expression().accept(this);
		StringBuilder sb = new StringBuilder(nested.length() + 2);
		sb.append('(');
		sb.append(nested);
		sb.append(')');
		return sb;
	}

	@Override
	public CharSequence visitArithmeticFunction(ArithmeticFunctionContext ctx) {
		StringBuilder sb = new StringBuilder();
		String functionName = ctx.getStart().getText();
		
		handleFunction(functionName, sb, ctx);
		
		return sb;
	}
	
	private void handleFunction(String functionName, StringBuilder sb, ParserRuleContext ctx) {
		sb.append(functionName);
		sb.append('(');
		
		boolean first = true;
		for (int i = 0; i < ctx.getChildCount(); i++) {
            if (!(ctx.getChild(i) instanceof TerminalNode)) {
            	if (!first) {
            		sb.append(',');
            	} else {
            		first = false;
            	}
            	
                sb.append(ctx.getChild(i).accept(this));
            }
        }
		
		sb.append(')');
	}

	@Override
	public CharSequence visitArithmeticLiteral(ArithmeticLiteralContext ctx) {
		return ctx.getText();
	}

	@Override
	public CharSequence visitArithmeticAttribute(ArithmeticAttributeContext ctx) {
		return ctx.getText();
	}

	@Override
	public CharSequence visitComparisonExpression(ComparisonExpressionContext ctx) {
		CharSequence left = ctx.left.accept(this);
		CharSequence op = ctx.op.accept(this);
		CharSequence right = ctx.right.accept(this);
		
		StringBuilder sb = new StringBuilder(left.length() + right.length() + op.length() + 2);
		sb.append(left).append(' ');
		sb.append(op).append(' ');
		sb.append(right);
		
		return sb;
	}

	@Override
	public CharSequence visitComparison_operator(Comparison_operatorContext ctx) {
		return ctx.getText();
	}

	@Override
	public CharSequence visitEquality_comparison_operator(Equality_comparison_operatorContext ctx) {
		return ctx.getText();
	}

	@Override
	public CharSequence visitSimpleExpression(SimpleExpressionContext ctx) {
		return ctx.expr.accept(this);
	}

	@Override
	public CharSequence visitNestedExpression(NestedExpressionContext ctx) {
		CharSequence expr = ctx.expr.accept(this);
		
		StringBuilder sb = new StringBuilder(expr.length() + 2);
		sb.append('(');
		sb.append(expr);
		sb.append(')');
		
		return sb;
	}

	@Override
	public CharSequence visitConditional_factor(Conditional_factorContext ctx) {
		CharSequence expr = ctx.expr.accept(this);
		StringBuilder sb;
		
		if (ctx.not != null) {
			sb = new StringBuilder(expr.length() + 4);
			
			sb.append("NOT ");
			sb.append(expr);
			return sb;
		} else {
			return expr;
		}
	}

	@Override
	public CharSequence visitFactorExpression(FactorExpressionContext ctx) {
		return ctx.factor.accept(this);
	}

	@Override
	public CharSequence visitAndExpression(AndExpressionContext ctx) {
		CharSequence left = ctx.left.accept(this);
		CharSequence right = ctx.factor.accept(this);

		StringBuilder sb = new StringBuilder(left.length() + right.length() + 5);
		sb.append(left);
		sb.append(" AND ");
		sb.append(right);
		
		return sb;
	}

	@Override
	public CharSequence visitTermExpression(TermExpressionContext ctx) {
		return ctx.term.accept(this);
	}

	@Override
	public CharSequence visitOrExpression(OrExpressionContext ctx) {
		CharSequence left = ctx.left.accept(this);
		CharSequence right = ctx.term.accept(this);

		StringBuilder sb = new StringBuilder(left.length() + right.length() + 4);
		sb.append(left);
		sb.append(" OR ");
		sb.append(right);
		
		return sb;
	}

	@Override
	public CharSequence visitStart(StartContext ctx) {
		return ctx.conditional_expression().accept(this);
	}

	@Override
	public CharSequence visitArithmeticFactor(ArithmeticFactorContext ctx) {
		// TODO Auto-generated method stub
		return super.visitArithmeticFactor(ctx);
	}

	@Override
	public CharSequence visitArithmeticTerm(ArithmeticTermContext ctx) {
		// TODO Auto-generated method stub
		return super.visitArithmeticTerm(ctx);
	}

	@Override
	public CharSequence visitMultiplicativeExpression(
			MultiplicativeExpressionContext ctx) {
		// TODO Auto-generated method stub
		return super.visitMultiplicativeExpression(ctx);
	}

	@Override
	public CharSequence visitArithmetic_factor(Arithmetic_factorContext ctx) {
		// TODO Auto-generated method stub
		return super.visitArithmetic_factor(ctx);
	}

	@Override
	public CharSequence visitAdditiveExpression(AdditiveExpressionContext ctx) {
		// TODO Auto-generated method stub
		return super.visitAdditiveExpression(ctx);
	}

	@Override
	public CharSequence visitDatetime_expression(Datetime_expressionContext ctx) {
		// TODO Auto-generated method stub
		return super.visitDatetime_expression(ctx);
	}

	@Override
	public CharSequence visitString_expression(String_expressionContext ctx) {
		// TODO Auto-generated method stub
		return super.visitString_expression(ctx);
	}
}
