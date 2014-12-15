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

import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticAttributeContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticFunctionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticLiteralContext;
import at.ac.tuwien.ase09.parser.PWatchParser.ArithmeticNestedContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Arithmetic_expressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Arithmetic_factorContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Arithmetic_primaryContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Arithmetic_termContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Comparison_expressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Comparison_operatorContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Conditional_expressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Conditional_factorContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Conditional_primaryContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Conditional_termContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Datetime_expressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Equality_comparison_operatorContext;
import at.ac.tuwien.ase09.parser.PWatchParser.Simple_cond_expressionContext;
import at.ac.tuwien.ase09.parser.PWatchParser.StartContext;

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
        boolean allowStringAttributes = false;
//        PWatchParser parser = new PWatchParser(tokens, allowStringAttributes);
        PWatchParser parser = new PWatchParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(ERR_LISTENER);
        
        ParserRuleContext ctx = parser.start();
        
        PWatchToEPLCompiler visitor = new PWatchToEPLCompiler(tokens);
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
	public CharSequence visitEquality_comparison_operator(
			Equality_comparison_operatorContext ctx) {
		// TODO Auto-generated method stub
		return super.visitEquality_comparison_operator(ctx);
	}

	@Override
	public CharSequence visitStart(StartContext ctx) {
		// TODO Auto-generated method stub
		return super.visitStart(ctx);
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
	public CharSequence visitArithmetic_term(Arithmetic_termContext ctx) {
		// TODO Auto-generated method stub
		return super.visitArithmetic_term(ctx);
	}

	@Override
	public CharSequence visitConditional_primary(Conditional_primaryContext ctx) {
		// TODO Auto-generated method stub
		return super.visitConditional_primary(ctx);
	}

	@Override
	public CharSequence visitSimple_cond_expression(
			Simple_cond_expressionContext ctx) {
		// TODO Auto-generated method stub
		return super.visitSimple_cond_expression(ctx);
	}

	@Override
	public CharSequence visitDatetime_expression(Datetime_expressionContext ctx) {
		// TODO Auto-generated method stub
		return super.visitDatetime_expression(ctx);
	}

	@Override
	public CharSequence visitComparison_expression(
			Comparison_expressionContext ctx) {
		// TODO Auto-generated method stub
		return super.visitComparison_expression(ctx);
	}

	@Override
	public CharSequence visitConditional_term(Conditional_termContext ctx) {
		// TODO Auto-generated method stub
		return super.visitConditional_term(ctx);
	}

	@Override
	public CharSequence visitConditional_expression(
			Conditional_expressionContext ctx) {
		// TODO Auto-generated method stub
		return super.visitConditional_expression(ctx);
	}

	@Override
	public CharSequence visitArithmetic_expression(
			Arithmetic_expressionContext ctx) {
		// TODO Auto-generated method stub
		return super.visitArithmetic_expression(ctx);
	}

	@Override
	public CharSequence visitArithmeticLiteral(ArithmeticLiteralContext ctx) {
		return ctx.getText();
	}

	@Override
	public CharSequence visitComparison_operator(Comparison_operatorContext ctx) {
		return ctx.getText();
	}

	@Override
	public CharSequence visitConditional_factor(Conditional_factorContext ctx) {
		// TODO Auto-generated method stub
		return super.visitConditional_factor(ctx);
	}

	@Override
	public CharSequence visitArithmeticAttribute(ArithmeticAttributeContext ctx) {
		// TODO Auto-generated method stub
		return super.visitArithmeticAttribute(ctx);
	}

	@Override
	public CharSequence visitArithmetic_factor(Arithmetic_factorContext ctx) {
		// TODO Auto-generated method stub
		return super.visitArithmetic_factor(ctx);
	}
}
