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
    : NUMERIC_ATTRIBUTE
    | NUMERIC_LITERAL
    | '('arithmetic_expression')'
    | SIN '(' arithmetic_expression ')'
    | COS '(' arithmetic_expression ')'
    | TAN '(' arithmetic_expression ')'
    | EXP '(' arithmetic_expression ')'
    | LOG '(' arithmetic_expression ')'
    | POW '(' arithmetic_expression ',' arithmetic_expression ')'
    | SQRT '(' arithmetic_expression ')'
    ;