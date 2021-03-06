grammar PWatch;

import PWatch;

@parser::members {
private boolean stringExpressionAllowed;
private boolean isStock;
private boolean isFund;
private boolean isBond;

public PWatchParser(TokenStream input, boolean stringExpressionAllowed, at.ac.tuwien.ase09.model.ValuePaperType valuePaperType){
       this(input);
       this.stringExpressionAllowed = stringExpressionAllowed;
       isStock = valuePaperType == at.ac.tuwien.ase09.model.ValuePaperType.STOCK;
       isFund = valuePaperType == at.ac.tuwien.ase09.model.ValuePaperType.FUND;
       isBond = valuePaperType == at.ac.tuwien.ase09.model.ValuePaperType.BOND;
}
}

start
    : conditional_expression EOF;

conditional_expression
    : term=conditional_term							 		#TermExpression
    | left=conditional_expression OR term=conditional_term 	#OrExpression
    ;

conditional_term
    : factor=conditional_factor							  #FactorExpression
    | left=conditional_term AND factor=conditional_factor #AndExpression
    ;

conditional_factor
    : (not=NOT)? expr=conditional_primary;

conditional_primary
    : expr=comparison_expression	    #SimpleExpression
    | '('expr=conditional_expression')' #NestedExpression
    ;


comparison_expression
    : {isStock}? left=datetime_expression op=comparison_operator right=datetime_expression						#DateTimeComparisonExpression
    | {isStock}? left=arithmetic_expression op=comparison_operator right=arithmetic_expression					#ArithmeticComparisonExpression
    | {stringExpressionAllowed}? left=string_expression op=equality_comparison_operator right=string_expression	#StringComparisonExpression
    ;

equality_comparison_operator
    : '='
    | '!='
    | '<>'
    ;

comparison_operator
    : equality_comparison_operator
    | '>'
    | '>='
    | '<'
    | '<='
    ;


datetime_expression
    : DATE_TIME_ATTRIBUTE	#DateTimeAttribute
    | CURRENT_TIMESTAMP		#CurrentTimestamp
    | TIMESTAMP_LITERAL		#TimestampLiteral
    ;
    
string_attribute
	: {isStock}? STRING_ATTRIBUTE
	| {isFund}? FUND_STRING_ATTRIBUTE
	| {isBond}? BOND_STRING_ATTRIBUTE
	;

string_expression
	: string_attribute	#StringAttribute
	| STRING_LITERAL	#StringLiteral
	;
	
arithmetic_expression
    : term=arithmetic_term												#SimpleArithmeticTerm
    | left=arithmetic_expression op=( '+' | '-' ) term=arithmetic_term	#AdditiveExpression
    ;

arithmetic_term
    : factor=arithmetic_factor											#SimpleArithmeticFactor
    | left=arithmetic_term op=( '*' | '/' ) factor=arithmetic_factor	#MultiplicativeExpression
    ;

arithmetic_factor
    : sign=( '+' | '-' )? value=arithmetic_primary;

arithmetic_primary
    : NUMERIC_ATTRIBUTE												#ArithmeticAttribute
    | NUMERIC_LITERAL												#ArithmeticLiteral
    | '('arithmetic_expression')'									#ArithmeticNested
    | SIN '(' arithmetic_expression ')'								#ArithmeticFunction
    | COS '(' arithmetic_expression ')'								#ArithmeticFunction
    | TAN '(' arithmetic_expression ')'								#ArithmeticFunction
    | EXP '(' arithmetic_expression ')'								#ArithmeticFunction
    | LOG '(' arithmetic_expression ')'								#ArithmeticFunction
    | POW '(' arithmetic_expression ',' arithmetic_expression ')'   #ArithmeticFunction
    | SQRT '(' arithmetic_expression ')'							#ArithmeticFunction
    ;