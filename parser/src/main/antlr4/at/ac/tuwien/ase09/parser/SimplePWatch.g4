grammar SimplePWatch;

import PWatch;

@parser::members {
private boolean stringExpressionAllowed;
private boolean isStock;
private boolean isFund;
private boolean isBond;

public SimplePWatchParser(TokenStream input, boolean stringExpressionAllowed, at.ac.tuwien.ase09.model.ValuePaperType valuePaperType){
       this(input);
       this.stringExpressionAllowed = stringExpressionAllowed;
       isStock = valuePaperType == at.ac.tuwien.ase09.model.ValuePaperType.STOCK;
       isFund = valuePaperType == at.ac.tuwien.ase09.model.ValuePaperType.FUND;
       isBond = valuePaperType == at.ac.tuwien.ase09.model.ValuePaperType.BOND;
}
}

start
    : conditional_term EOF;

conditional_term
    : factor=conditional_primary							#FactorExpression
    | left=conditional_term AND factor=conditional_primary	#AndExpression
    ;

conditional_primary
    : {isStock}? left=datetime_attribute op=comparison_operator right=datetime_literal						#DateTimeComparisonExpression
    | {isStock}? left=arithmetic_attribute op=comparison_operator right=arithmetic_literal					#ArithmeticComparisonExpression
    | {stringExpressionAllowed}? left=string_attribute op=equality_comparison_operator right=string_literal	#StringComparisonExpression
    ;

equality_comparison_operator
    : '='
    | '!='
    ;

comparison_operator
    : equality_comparison_operator
    | '>'
    | '>='
    | '<'
    | '<='
    ;
    
datetime_attribute
    : DATE_TIME_ATTRIBUTE	#DateTimeAttribute
    ;
datetime_literal
    : TIMESTAMP_LITERAL		#TimestampLiteral
    ;
    
string_attribute
	: {isStock}? STRING_ATTRIBUTE
	| {isFund}? FUND_STRING_ATTRIBUTE
	| {isBond}? BOND_STRING_ATTRIBUTE
	;
string_literal
	: STRING_LITERAL		#StringLiteral
	;
	
arithmetic_attribute
    : NUMERIC_ATTRIBUTE		#ArithmeticAttribute
    ;
arithmetic_literal
    : NUMERIC_LITERAL		#ArithmeticLiteral
    ;