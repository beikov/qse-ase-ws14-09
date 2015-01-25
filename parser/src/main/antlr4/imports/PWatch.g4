lexer grammar PWatch;

AND : A N D;
OR : O R;
NOT : N O T | '!';

CURRENT_TIMESTAMP : C U R R E N T '_' T I M E S T A M P;

TIMESTAMP : T I M E S T A M P;

// Literal

TIMESTAMP_LITERAL
    : TIMESTAMP '(' '\'' DATE_STRING (' ' TIME_STRING)? '\'' ')';

fragment TIMESTAMP
    : T I M E S T A M P;

fragment DATE_STRING
    : DIGIT DIGIT DIGIT DIGIT '-' DIGIT DIGIT '-' DIGIT DIGIT;

fragment TIME_STRING
    : DIGIT DIGIT ':' DIGIT DIGIT ':' DIGIT DIGIT ('.' DIGIT DIGIT DIGIT)?;
    
fragment DIGIT
    : '0'..'9';
fragment DIGITS
    : DIGIT+;
fragment DIGIT_NOT_ZERO
    : '1'..'9';

STRING_LITERAL
	: '\'' ~[\']* '\''
	| '"' ~["]* '"';

NUMERIC_LITERAL
    : INTEGER ('.' DIGITS)? EXPONENT_PART?;
    
fragment INTEGER
	: '0' 
	| DIGIT_NOT_ZERO DIGITS?;
    
fragment EXPONENT_PART
   :   [eE] SIGNED_INTEGER
   ;

fragment SIGNED_INTEGER
   :   [+-]? DIGITS
   ;

// Functions

SIN : S I N;
COS : C O S;
TAN : T A N;
EXP : E X P;
LOG : L O G;
POW : P O W;
SQRT : S Q R T;

// Attributes

NUMERIC_ATTRIBUTE
    : PRICE
    | MARKET_CAP
    | ENTERPRISE_VALUE
    | TRAILING_PE
    | FORWARD_PE
    | PEG_RATIO
    | PRICE_SALES
    | PRICE_BOOK
    | ENTERPRISE_VALUE_REVENUE
    | ENTERPRISE_VALUE_EBITDA
    | PROFIT_MARGIN
    | OPERATING_MARGIN
    | RETURN_ON_ASSETS
    | RETURN_ON_EQUITY
    | REVENUE
    | REVENUE_PER_SHARE
    | QTRLY_REVENUE_GROWTH
    | GROSS_PROFIT
    | EBITDA
    | NET_INCOME_AVL_TO_COMMON
    | DILUTED_EPS
    | QTRLY_EARNINGS_GROWTH
    | TOTAL_CASH
    | TOTAL_CASH_PER_SHARE
    | TOTAL_DEBT
    | TOTAL_DEBT_EQUITY
    | CURRENT_RATIO
    | BOOK_VALUE_PER_SHARE
    | OPERATING_CASH_FLOW
    | LEVERED_FREE_CASH_FLOW
    | BETA
    | P52_WEEK_CHANGE
    | P52_WEEK_HIGH
    | P52_WEEK_LOW
    | P50_DAY_MOVING_AVERAGE
    | P200_DAY_MOVING_AVERAGE
    | AVG_VOL_3_MONTH
    | AVG_VOL_10_DAY
    | SHARES_OUTSTANDING
    | FLOAT
    | PERCENTAGE_HELD_BY_INSIDERS
    | PERCENTAGE_HELD_BY_INSTITUTIONS
    | SHARES_SHORT_CURRENT_MONTH
    | SHORT_RATIO
    | SHORT_PERCENTAGE_OF_FLOAT
    | SHARES_SHORT_PRIOR_MONTH
    | FORWARD_ANNUAL_DIVIDEND_RATE
    | FORWARD_ANNUAL_DIVIDEND_YIELD
    | TRAILING_ANNUAL_DIVIDEND_YIELD_ABSOLUTE
    | TRAILING_ANNUAL_DIVIDEND_YIELD_RELATIVE
    | P5_YEAR_AVERAGE_DIVIDEND_YIELD
    | PAYOUT_RATIO;

DATE_TIME_ATTRIBUTE  : FISCAL_YEAR_ENDS
                     | MOST_RECENT_QUARTER
                     | DIVIDEND_DATE
                     | EX_DIVIDEND_DATE;

STRING_ATTRIBUTE 	: NAME
					| CODE
					| INDEX
					| COUNTRY
					| CURRENCY;
					
FUND_STRING_ATTRIBUTE 	: NAME
						| CODE
						| CURRENCY;
BOND_STRING_ATTRIBUTE 	: NAME
						| CODE;

PRICE : P R I C E ;
MARKET_CAP : M A R K E T '_' C A P ;
ENTERPRISE_VALUE : E N T E R P R I S E '_' V A L U E ;
TRAILING_PE : T R A I L I N G '_' P E ;
FORWARD_PE : F O R W A R D '_' P E ;
PEG_RATIO : P E G '_' R A T I O ;
PRICE_SALES : P R I C E '_' S A L E S ;
PRICE_BOOK : P R I C E '_' B O O K ;
ENTERPRISE_VALUE_REVENUE : E N T E R P R I S E '_' V A L U E '_' R E V E N U E ;
ENTERPRISE_VALUE_EBITDA : E N T E R P R I S E '_' V A L U E '_' E B I T D A ;
FISCAL_YEAR_ENDS : F I S C A L '_' Y E A R '_' E N D S ;
MOST_RECENT_QUARTER : M O S T '_' R E C E N T '_' Q U A R T E R ;
PROFIT_MARGIN : P R O F I T '_' M A R G I N ;
OPERATING_MARGIN : O P E R A T I N G '_' M A R G I N ;
RETURN_ON_ASSETS : R E T U R N '_' O N '_' A S S E T S ;
RETURN_ON_EQUITY : R E T U R N '_' O N '_' E Q U I T Y ;
REVENUE : R E V E N U E ;
REVENUE_PER_SHARE : R E V E N U E '_' P E R '_' S H A R E ;
QTRLY_REVENUE_GROWTH : Q T R L Y '_' R E V E N U E '_' G R O W T H ;
GROSS_PROFIT : G R O S S '_' P R O F I T ;
EBITDA : E B I T D A ;
NET_INCOME_AVL_TO_COMMON : N E T '_' I N C O M E '_' A V L '_' T O '_' C O M M O N ;
DILUTED_EPS : D I L U T E D '_' E P S ;
QTRLY_EARNINGS_GROWTH : Q T R L Y '_' E A R N I N G S '_' G R O W T H ;
TOTAL_CASH : T O T A L '_' C A S H ;
TOTAL_CASH_PER_SHARE : T O T A L '_' C A S H '_' P E R '_' S H A R E ;
TOTAL_DEBT : T O T A L '_' D E B T ;
TOTAL_DEBT_EQUITY : T O T A L '_' D E B T '_' E Q U I T Y ;
CURRENT_RATIO : C U R R E N T '_' R A T I O ;
BOOK_VALUE_PER_SHARE : B O O K '_' V A L U E '_' P E R '_' S H A R E ;
OPERATING_CASH_FLOW : O P E R A T I N G '_' C A S H '_' F L O W ;
LEVERED_FREE_CASH_FLOW : L E V E R E D '_' F R E E '_' C A S H '_' F L O W ;
BETA : B E T A ;
P52_WEEK_CHANGE : '5' '2' '_' W E E K '_' C H A N G E ;
P52_WEEK_HIGH : '5' '2' '_' W E E K '_' H I G H ;
P52_WEEK_LOW : '5' '2' '_' W E E K '_' L O W ;
P50_DAY_MOVING_AVERAGE : '5' '0' '_' D A Y '_' M O V I N G '_' A V E R A G E ;
P200_DAY_MOVING_AVERAGE : '2' '0' '0' '_' D A Y '_' M O V I N G '_' A V E R A G E ;
AVG_VOL_3_MONTH : A V G '_' V O L '_' '3' '_' M O N T H ;
AVG_VOL_10_DAY : A V G '_' V O L '_' '1' '0' '_' D A Y ;
SHARES_OUTSTANDING : S H A R E S '_' O U T S T A N D I N G ;
FLOAT : F L O A T ;
PERCENTAGE_HELD_BY_INSIDERS : P E R C E N T A G E '_' H E L D '_' B Y '_' I N S I D E R S ;
PERCENTAGE_HELD_BY_INSTITUTIONS : P E R C E N T A G E '_' H E L D '_' B Y '_' I N S T I T U T I O N S ;
SHARES_SHORT_CURRENT_MONTH : S H A R E S '_' S H O R T '_' C U R R E N T '_' M O N T H ;
SHORT_RATIO : S H O R T '_' R A T I O ;
SHORT_PERCENTAGE_OF_FLOAT : S H O R T '_' P E R C E N T A G E '_' O F '_' F L O A T ;
SHARES_SHORT_PRIOR_MONTH : S H A R E S '_' S H O R T '_' P R I O R '_' M O N T H ;
FORWARD_ANNUAL_DIVIDEND_RATE : F O R W A R D '_' A N N U A L '_' D I V I D E N D '_' R A T E ;
FORWARD_ANNUAL_DIVIDEND_YIELD : F O R W A R D '_' A N N U A L '_' D I V I D E N D '_' Y I E L D ;
TRAILING_ANNUAL_DIVIDEND_YIELD_ABSOLUTE : T R A I L I N G '_' A N N U A L '_' D I V I D E N D '_' Y I E L D '_' A B S O L U T E ;
TRAILING_ANNUAL_DIVIDEND_YIELD_RELATIVE : T R A I L I N G '_' A N N U A L '_' D I V I D E N D '_' Y I E L D '_' R E L A T I V E ;
P5_YEAR_AVERAGE_DIVIDEND_YIELD : '5' '_' Y E A R '_' A V E R A G E '_' D I V I D E N D '_' Y I E L D ;
PAYOUT_RATIO : P A Y O U T '_' R A T I O ;
DIVIDEND_DATE : D I V I D E N D '_' D A T E ;
EX_DIVIDEND_DATE : E X '_' D I V I D E N D '_' D A T E ;

NAME : N A M E ;
CODE : C O D E ;
INDEX : I N D E X ;
CURRENCY : C U R R E N C Y ;
COUNTRY : C O U N T R Y ;


WS: [ \n\t\r]+ -> channel(HIDDEN);

fragment A: 'A';//('a'|'A');
fragment B: 'B';//('b'|'B');
fragment C: 'C';//('c'|'C');
fragment D: 'D';//('d'|'D');
fragment E: 'E';//('e'|'E');
fragment F: 'F';//('f'|'F');
fragment G: 'G';//('g'|'G');
fragment H: 'H';//('h'|'H');
fragment I: 'I';//('i'|'I');
fragment J: 'J';//('j'|'J');
fragment K: 'K';//('k'|'K');
fragment L: 'L';//('l'|'L');
fragment M: 'M';//('m'|'M');
fragment N: 'N';//('n'|'N');
fragment O: 'O';//('o'|'O');
fragment P: 'P';//('p'|'P');
fragment Q: 'Q';//('q'|'Q');
fragment R: 'R';//('r'|'R');
fragment S: 'S';//('s'|'S');
fragment T: 'T';//('t'|'T');
fragment U: 'U';//('u'|'U');
fragment V: 'V';//('v'|'V');
fragment W: 'W';//('w'|'W');
fragment X: 'X';//('x'|'X');
fragment Y: 'Y';//('y'|'Y');
fragment Z: 'Z';//('z'|'Z');