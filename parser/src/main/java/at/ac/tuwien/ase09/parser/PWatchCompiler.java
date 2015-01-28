package at.ac.tuwien.ase09.parser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.LexerNoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;

import at.ac.tuwien.ase09.model.ValuePaperType;
import at.ac.tuwien.ase09.model.filter.AttributeFilter;

public class PWatchCompiler {

	private static final ThreadLocal<DateFormat> DATE_FORMAT = new ThreadLocal<DateFormat>() {

		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
		
	};
    
    public static String attributeFiltersAsPWatch(List<AttributeFilter> filters) {
    	StringBuilder sb = new StringBuilder();
    	boolean first = true;
    	
    	for (AttributeFilter filter : filters) {
    		if (!filter.isEnabled()) {
    			continue;
    		} else if (first) {
    			first = false;
    		} else {
    			sb.append(" AND ");
    		}
    		
    		sb.append(filter.getAttribute().getPwatchName());
    		sb.append(' ');
    		
    		if (filter.isCurrencyType()) {
        		sb.append("= '");
        		sb.append(filter.getCurrencyValue());
        		sb.append('\'');
    		} else if (filter.isIndexType()) {
        		sb.append("= '");
        		sb.append(filter.getIndexValue());
        		sb.append('\'');
    		} else if (filter.isDateTimeType()) {
        		sb.append(filter.getOperatorType().getOperator());
        		sb.append(" TIMESTAMP('");
        		sb.append(DATE_FORMAT.get().format(filter.getDateTimeValue()));
        		sb.append("')");
    		} else if (filter.isNumericType()) {
        		sb.append(filter.getOperatorType().getOperator());
        		sb.append(' ');
        		sb.append(filter.getNumericValue());
    		} else {
        		sb.append(filter.getOperatorType().getOperator());
        		sb.append(" '");
        		sb.append(filter.getTextValue());
        		sb.append('\'');
    		}
    	}
    	
    	return sb.toString();
    }

    public static List<AttributeFilter> pwatchAsAttributeFilters(String pwatchExpression, boolean stringExpressionAllowed, ValuePaperType valuePaperType) {
    	if (valuePaperType == null) {
    		throw new NullPointerException("valuePaperType");
    	}
    	if (pwatchExpression == null || pwatchExpression.isEmpty()) {
    		return new ArrayList<AttributeFilter>(0);
    	}
    	
    	try {
	    	SimpleErrorListener errorListener = new SimpleErrorListener();
	    	SimplePWatchLexer lexer = new SimplePWatchLexer(new ANTLRInputStream(pwatchExpression));
	        lexer.removeErrorListeners();
	        lexer.addErrorListener(errorListener);
	        CommonTokenStream tokens = new CommonTokenStream(lexer);
	        SimplePWatchParser parser = new SimplePWatchParser(tokens, stringExpressionAllowed, valuePaperType);
	        parser.removeErrorListeners();
	        parser.addErrorListener(errorListener);
	
	        
	        ParserRuleContext ctx = parser.start();
	
	        SimplePWatchCompiler visitor = new SimplePWatchCompiler();
	        return visitor.visit(ctx);
    	} catch (SyntaxErrorException ex) {
        	return null;
    	}
    }
    
    public static String compileJpql(String pwatchExpression, ValuePaperType valuePaperType, Long portfolioId) {
		if (pwatchExpression == null) {
            throw new NullPointerException("pwatchExpression");
        }
        
        StringBuilder sb = new StringBuilder(100);
        sb.append("SELECT ");
    	sb.append(AdvancedPWatchCompiler.VALUE_PAPER_ALIAS);
    	if (valuePaperType == null) {
    		sb.append(" FROM ValuePaper ");
    	} else {
        	sb.append(" FROM ").append(valuePaperType.getEntityName()).append(" ");
    	}
    	sb.append(AdvancedPWatchCompiler.VALUE_PAPER_ALIAS);
    	
    	if (valuePaperType == null) {
    		valuePaperType = ValuePaperType.STOCK;
    	}
    	
        if (pwatchExpression.isEmpty()) {
            if (portfolioId != null) {
	        	sb.append(" WHERE ");
	        	appendPortfolioCondition(sb, portfolioId);
            }
        	return sb.toString();
        }
        
        PWatchLexer lexer = new PWatchLexer(new ANTLRInputStream(pwatchExpression));
        lexer.removeErrorListeners();
        lexer.addErrorListener(ERR_LISTENER);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        boolean stringExpressionAllowed = true;
        PWatchParser parser = new PWatchParser(tokens, stringExpressionAllowed, valuePaperType);
        parser.removeErrorListeners();
        parser.addErrorListener(ERR_LISTENER);
        
        ParserRuleContext ctx = parser.start();
        
        AdvancedPWatchCompiler visitor = new AdvancedPWatchCompiler();
        CharSequence result = visitor.visit(ctx);
        
        if (!visitor.usesPrice() && ! visitor.usesOtherAttrs()) {
        	throw new IllegalArgumentException("The given expression does not reference any attributes!");
        }
        if (visitor.usesPrice()) {
        	sb.append(" JOIN ");
        	sb.append(AdvancedPWatchCompiler.VALUE_PAPER_ALIAS);
        	sb.append(".priceEntries ");
        	sb.append(AdvancedPWatchCompiler.PRICE_ENTRY_ALIAS);
        	sb.append(" WHERE ");
        	sb.append(AdvancedPWatchCompiler.PRICE_ENTRY_ALIAS);
        	sb.append(".created = (SELECT MAX(e.created) FROM ValuePaperPriceEntry e WHERE e.valuePaper = ");
        	sb.append(AdvancedPWatchCompiler.VALUE_PAPER_ALIAS);
        	sb.append(") ");
        	sb.append("AND ");
        } else {
        	sb.append(" WHERE ");
        }
        
        sb.append(result);
        
        if (portfolioId != null) {
        	sb.append(" AND ");
        	appendPortfolioCondition(sb, portfolioId);
        }
        
        return sb.toString();
    }
    
    private static void appendPortfolioCondition(StringBuilder sb, Long portfolioId) {
    	sb.append("EXISTS(");
    	sb.append("SELECT portfolio FROM Portfolio portfolio LEFT JOIN portfolio.game g WHERE portfolio.id = " + portfolioId + " AND (g IS NULL OR ");
    	sb.append(AdvancedPWatchCompiler.VALUE_PAPER_ALIAS);
    	sb.append(" MEMBER OF g.allowedValuePapers");
    	sb.append("))");
    }

	public static String compileEpl(String pwatchExpression, Long valuePaperId) {
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
        PWatchParser parser = new PWatchParser(tokens, stringExpressionAllowed, ValuePaperType.STOCK);
        parser.removeErrorListeners();
        parser.addErrorListener(ERR_LISTENER);
        
        ParserRuleContext ctx = parser.start();
        
        AdvancedPWatchCompiler visitor = new AdvancedPWatchCompiler();
        CharSequence result = visitor.visit(ctx);
        StringBuilder sb = new StringBuilder(result.length() + 100);
        sb.append("SELECT 1 FROM ");
        
        if (!visitor.usesPrice() && ! visitor.usesOtherAttrs()) {
        	throw new IllegalArgumentException("The given expression does not reference any attributes!");
        }
        if (visitor.usesPrice()) {
        	sb.append("ValuePaperPriceEntry(valuePaperId = ");
        	sb.append(valuePaperId);
        	sb.append(").std:lastevent() AS ");
        	sb.append(AdvancedPWatchCompiler.PRICE_ENTRY_ALIAS);
        	sb.append(' ');
        }
        if (visitor.usesOtherAttrs()) {
        	sb.append("Stock(id = ");
        	sb.append(valuePaperId);
        	sb.append(").std:lastevent() AS ");
        	sb.append(AdvancedPWatchCompiler.VALUE_PAPER_ALIAS);
        	sb.append(' ');
        }
        
        sb.append("WHERE ");
        sb.append(result);
        return sb.toString();
	}

	public static String validate(String pwatchExpression, boolean simple, boolean stringExpressionAllowed, ValuePaperType valuePaperType) {
    	if (pwatchExpression == null || pwatchExpression.isEmpty()) {
    		return null;
    	}
    	if (valuePaperType == null) {
    		valuePaperType = ValuePaperType.STOCK;
    	}
    	
    	SimpleErrorListener errorListener = new SimpleErrorListener();
    	
    	try {
	    	if (simple) {
		    	SimplePWatchLexer lexer = new SimplePWatchLexer(new ANTLRInputStream(pwatchExpression));
		        lexer.removeErrorListeners();
		        lexer.addErrorListener(errorListener);
		        CommonTokenStream tokens = new CommonTokenStream(lexer);
		        SimplePWatchParser parser = new SimplePWatchParser(tokens, stringExpressionAllowed, valuePaperType);
		        parser.removeErrorListeners();
		        parser.addErrorListener(errorListener);
		        parser.start();
	    	} else {
	            PWatchLexer lexer = new PWatchLexer(new ANTLRInputStream(pwatchExpression));
	            lexer.removeErrorListeners();
	            lexer.addErrorListener(errorListener);
	            CommonTokenStream tokens = new CommonTokenStream(lexer);
	            PWatchParser parser = new PWatchParser(tokens, stringExpressionAllowed, valuePaperType);
	            parser.removeErrorListeners();
	            parser.addErrorListener(errorListener);
	            parser.start();
	    	}
    	} catch(SyntaxErrorException ex) {
        	return ex.getMessage();
    	}

		return null;
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
    
    private static class SimpleErrorListener implements ANTLRErrorListener {
    	
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        	String reason;
        	
        	if (e instanceof LexerNoViableAltException) {
        		reason = "Das Zeichen '" +  (char) e.getInputStream().LA(1) + "' ist unerwartet.";
        	} else if (offendingSymbol instanceof CommonToken) {
        		reason = "Das Zeichen '" +  ((CommonToken) offendingSymbol).getText() + "' ist unerwartet.";
        		IntervalSet expectedToken = recognizer.getATN().getExpectedTokens(recognizer.getState(), ((Parser) recognizer).getContext());
        		
        		if (expectedToken.size() > 0) {
        			reason += " Eines der folgenden Zeichen wurde erwartet ";
	        		reason += expectedToken.toString(recognizer.getTokenNames());
        		}
        	} else {
        		reason = msg;
        	}
        	throw new SyntaxErrorException("Ein Fehler wurde in der Zeile " + line + " beim Zeichen " + charPositionInLine + " festgestellt. " + reason);
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
    	
    }
}
