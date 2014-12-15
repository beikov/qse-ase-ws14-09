grammar PWatch;

import PWatch;

start
    : conditional_expression EOF;

conditional_expression
    : conditional_term
    | conditional_expression OR conditional_term
    ;

conditional_term
    : conditional_factor
    | conditional_term AND conditional_factor
    ;

conditional_factor
    : (NOT)? conditional_primary;

conditional_primary
    : simple_cond_expression
    | '('conditional_expression')'
    ;

simple_cond_expression
    : comparison_expression;


comparison_expression
    : datetime_expression comparison_operator datetime_expression
    | arithmetic_expression comparison_operator arithmetic_expression
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

arithmetic_expression
    : arithmetic_term
    | arithmetic_expression ( '+' | '-' ) arithmetic_term
    ;

arithmetic_term
    : arithmetic_factor
    | arithmetic_term ( '*' | '/' ) arithmetic_factor
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