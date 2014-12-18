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
    : left=datetime_expression op=comparison_operator right=datetime_expression									#ComparisonExpression
    | left=arithmetic_expression op=comparison_operator right=arithmetic_expression								#ComparisonExpression
    | {stringExpressionAllowed}? left=string_expression op=equality_comparison_operator right=string_expression	#ComparisonExpression
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
    : DATE_TIME_ATTRIBUTE
    | CURRENT_TIMESTAMP
    | TIMESTAMP_LITERAL
    ;

string_expression
	: STRING_ATTRIBUTE
	| STRING_LITERAL;

arithmetic_expression
    : arithmetic_term										#ArithmeticTerm
    | arithmetic_expression ( '+' | '-' ) arithmetic_term	#AdditiveExpression
    ;

arithmetic_term
    : arithmetic_factor										#ArithmeticFactor
    | arithmetic_term ( '*' | '/' ) arithmetic_factor		#MultiplicativeExpression
    ;

arithmetic_factor
    : ( '+' | '-' )? arithmetic_primary;

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