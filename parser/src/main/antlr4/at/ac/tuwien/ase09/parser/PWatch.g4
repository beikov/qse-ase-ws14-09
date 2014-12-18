grammar PWatch;

import PWatch;

@parser::members {
private boolean stringExpressionAllowed;

public PWatchParser(TokenStream input, boolean stringExpressionAllowed){
       this(input);
       this.stringExpressionAllowed = stringExpressionAllowed;
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
    : left=datetime_expression op=comparison_operator right=datetime_expression									#DateTimeComparisonExpression
    | left=arithmetic_expression op=comparison_operator right=arithmetic_expression								#ArithmeticComparisonExpression
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

string_expression
	: STRING_ATTRIBUTE	#StringAttribute
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