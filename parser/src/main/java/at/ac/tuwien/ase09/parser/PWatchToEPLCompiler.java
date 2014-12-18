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
import org.antlr.v4.runtime.tree.TerminalNode;

import at.ac.tuwien.ase09.parser.PWatchParser.AdditiveExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.AndExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticAttributeContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticComparisonExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticFunctionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticLiteralContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticNestedContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Arithmetic_factorContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Comparison_operatorContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Conditional_factorContext;
import at.ac.tuwien.ase09.parser.PWatchParser.CurrentTimestampContext;
import at.ac.tuwien.ase09.parser.PWatchParser.DateTimeAttributeContext;
import at.ac.tuwien.ase09.parser.PWatchParser.DateTimeComparisonExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Equality_comparison_operatorContext;
import at.ac.tuwien.ase09.parser.PWatchParser.FactorExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.MultiplicativeExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.NestedExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.OrExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.SimpleArithmeticFactorContext;
import at.ac.tuwien.ase09.parser.PWatchParser.SimpleArithmeticTermContext;
import at.ac.tuwien.ase09.parser.PWatchParser.SimpleExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.StartContext;
import at.ac.tuwien.ase09.parser.PWatchParser.StringAttributeContext;
import at.ac.tuwien.ase09.parser.PWatchParser.StringComparisonExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.StringLiteralContext;
import at.ac.tuwien.ase09.parser.PWatchParser.TermExpressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.TimestampLiteralContext;

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
        
        ParserRuleContext ctx = parser.start();
        
        PWatchToEPLCompiler visitor = new PWatchToEPLCompiler(tokens);
        CharSequence result = visitor.visit(ctx);
        StringBuilder sb = new StringBuilder(result.length() + 100);
        sb.append("SELECT ");
        sb.append(result);
        return result.toString();
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
	public CharSequence visitStringAttribute(StringAttributeContext ctx) {
		return ctx.getText();
	}

	@Override
	public CharSequence visitCurrentTimestamp(CurrentTimestampContext ctx) {
		return ctx.getText();
	}

	@Override
	public CharSequence visitDateTimeAttribute(DateTimeAttributeContext ctx) {
		return ctx.getText();
	}

	@Override
	public CharSequence visitTimestampLiteral(TimestampLiteralContext ctx) {
		return ctx.getText();
	}

	@Override
	public CharSequence visitStringLiteral(StringLiteralContext ctx) {
		return ctx.getText();
	}

	@Override
	public CharSequence visitStringComparisonExpression(StringComparisonExpressionContext ctx) {
		return handleBinaryExpression(ctx.left, ctx.op, ctx.right);
	}

	@Override
	public CharSequence visitArithmeticComparisonExpression(ArithmeticComparisonExpressionContext ctx) {
		return handleBinaryExpression(ctx.left, ctx.op, ctx.right);
	}

	@Override
	public CharSequence visitDateTimeComparisonExpression(DateTimeComparisonExpressionContext ctx) {
		return handleBinaryExpression(ctx.left, ctx.op, ctx.right);
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
		return handleBinaryExpression(ctx.left, "AND", ctx.factor);
	}

	@Override
	public CharSequence visitTermExpression(TermExpressionContext ctx) {
		return ctx.term.accept(this);
	}

	@Override
	public CharSequence visitOrExpression(OrExpressionContext ctx) {
		return handleBinaryExpression(ctx.left, "OR", ctx.term);
	}

	@Override
	public CharSequence visitStart(StartContext ctx) {
		return ctx.conditional_expression().accept(this);
	}

	@Override
	public CharSequence visitArithmetic_factor(Arithmetic_factorContext ctx) {
		CharSequence value = ctx.value.accept(this);
		StringBuilder sb;
		
		if (ctx.sign != null) {
			sb = new StringBuilder(value.length() + 1);
			sb.append(ctx.sign.getText());
		} else {
			sb = new StringBuilder(value.length());
		}
		
		sb.append(value);
		return sb;
	}

	@Override
	public CharSequence visitSimpleArithmeticTerm(SimpleArithmeticTermContext ctx) {
		return ctx.term.accept(this);
	}

	@Override
	public CharSequence visitSimpleArithmeticFactor(SimpleArithmeticFactorContext ctx) {
		return ctx.factor.accept(this);
	}

	@Override
	public CharSequence visitMultiplicativeExpression(MultiplicativeExpressionContext ctx) {
		return handleBinaryExpression(ctx.left, ctx.op.getText(), ctx.factor);
	}

	@Override
	public CharSequence visitAdditiveExpression(AdditiveExpressionContext ctx) {
		return handleBinaryExpression(ctx.left, ctx.op.getText(), ctx.term);
	}

	private CharSequence handleBinaryExpression(ParserRuleContext leftCtx, ParserRuleContext opCtx, ParserRuleContext rightCtx) {
		return handleBinaryExpression(leftCtx, opCtx.accept(this), rightCtx);
	}

	private CharSequence handleBinaryExpression(ParserRuleContext leftCtx, CharSequence op, ParserRuleContext rightCtx) {
		CharSequence left = leftCtx.accept(this);
		CharSequence right = rightCtx.accept(this);
		
		StringBuilder sb = new StringBuilder(left.length() + right.length() + op.length() + 2);
		sb.append(left).append(' ');
		sb.append(op).append(' ');
		sb.append(right);
		
		return sb;
	}
}
