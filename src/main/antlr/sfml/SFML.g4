// GOALS:
// - label tag operations; INPUT FROM * WITH ID "*phyto*"
// - test binary operator precidence for sets and numeric expressions; https://stackoverflow.com/questions/64257414/antlr4-operator-precedence-changes
// - OUTPUT 5 BUNDLES OF 3 SAME *shard*, 1 stick TO EMPTY infuser
// - OUTPUT 5 BUNDLES OF 1 *seed* IN SLOT 0, 1 *ingot* TO EMPTY altar
// -- bundles, EMPTY qualifier, IN SLOT(s) qualifier

grammar SFML;
@header {
package ca.teamdman.langs;
}
@lexer::members {
    public boolean INCLUDE_UNUSED = false; // we want syntax highlighting to not break on unexpected tokens
}

program : name? trigger* EOF;

name: NAME string ;

trigger : EVERY interval DO block END           #TimerTrigger
        | EVERY REDSTONE PULSE DO block END     #PulseTrigger
        ;

interval: numberExpression? GLOBAL? durationUnit (OFFSET BY numberExpression durationUnit?)?;
durationUnit: (TICKS | TICK | SECONDS | SECOND);

numberExpression  : NUMBER                                      # NumberExpressionLiteral
                  | LPAREN numberExpression RPAREN              # NumberExpressionParen
                  | numberExpression CARET numberExpression     # NumberExpressionExponential
                  | numberExpression ASTERISK numberExpression  # NumberExpressionMultiplication
                  | numberExpression SLASH numberExpression     # NumberExpressionDivision
                  | numberExpression PLUS numberExpression      # NumberExpressionAddition
                  | numberExpression DASH numberExpression      # NumberExpressionSubtraction
                  | numberExpression PERCENT numberExpression   # NumberExpressionModulus
                  ;

block           : statement*;
statement       : inputStatement
                | outputStatement
                | ifStatement
                | forgetStatement
                ;

inputStatement  : INPUT inputResourceLimits? resourceExclusion? FROM EACH? resourceAccess
                | FROM EACH? resourceAccess INPUT inputResourceLimits? resourceExclusion?
                ;
outputStatement : OUTPUT outputResourceLimits? resourceExclusion? TO emptyslots? EACH? resourceAccess
                | TO emptyslots? EACH? resourceAccess OUTPUT outputResourceLimits? resourceExclusion?
                ;

forgetStatement : FORGET label? (COMMA label)* COMMA?;

label           : (identifier)  #RawLabel
                | string        #StringLabel
                ;

inputResourceLimits   : resourceLimitList; // separate for different defaults
outputResourceLimits  : resourceLimitList; // separate for different defaults

resourceLimitList   : resourceLimit (COMMA resourceLimit)* COMMA?;
resourceLimit       : limit? resourceIdDisjunction with?
                    | limit with?
                    | with
                    ;

limit   : quantity retention    #QuantityRetentionLimit
        | retention             #RetentionLimit
        | quantity              #QuantityLimit
        ;

quantity        : numberExpression EACH?;
retention       : RETAIN numberExpression EACH?;

resourceExclusion : EXCEPT resourceIdList;

resourceId  : (identifier) (COLON (identifier)? (COLON (identifier)? (COLON (identifier)?)?)?)? # Resource
            | string                                                                            # StringResource
            ;

resourceIdList          : resourceId (COMMA resourceId)* COMMA?;
resourceIdDisjunction   : resourceId (OR resourceId)* OR?;


with    : WITH withClause
        | WITHOUT withClause
        ;

withClause  : LPAREN withClause RPAREN           # WithParen
            | NOT withClause                     # WithNegation
            | withClause AND withClause          # WithConjunction
            | withClause OR withClause           # WithDisjunction
            | (TAG HASHTAG?|HASHTAG) tagMatcher  # WithTag
            ;

tagMatcher  : identifier COLON identifier (SLASH identifier)*
            | identifier (SLASH identifier)*
            ;

ifStatement     : IF boolexpr THEN block (ELSE IF boolexpr THEN block)* (ELSE block)? END;
boolexpr        : TRUE                              #BooleanTrue
                | FALSE                             #BooleanFalse
                | LPAREN boolexpr RPAREN            #BooleanParen
                | NOT boolexpr                      #BooleanNegation
                | boolexpr AND boolexpr             #BooleanConjunction
                | boolexpr OR boolexpr              #BooleanDisjunction
                | setOp? resourceAccess HAS comparisonOp numberExpression resourceIdDisjunction? with? (EXCEPT resourceIdList)?  #BooleanHas
                | REDSTONE (comparisonOp numberExpression)?   #BooleanRedstone
                ;

comparisonOp    : GT
                | LT
                | EQ
                | LE
                | GE
                | GT_SYMBOL
                | LT_SYMBOL
                | EQ_SYMBOL
                | LE_SYMBOL
                | GE_SYMBOL
                ;
setOp           : OVERALL
                | SOME
                | EVERY
                | EACH
                | ONE
                | LONE
                ;

resourceAccess : labelExpression (COMMA labelExpression)* roundrobin? sideQualifier? slotQualifier?;

labelExpression : label                                      # LabelExpressionSingle
                | LPAREN labelExpression RPAREN              # LabelExpressionParen
                | labelExpression EXCEPT    labelExpression  # LabelExpressionExclusion
                | labelExpression INTERSECT labelExpression  # LabelExpressionIntersection
                | labelExpression UNION     labelExpression  # LabelExpressionUnion
                ;

roundrobin : ROUND ROBIN BY (LABEL | BLOCK);

sideQualifier   : ALL (SIDE|SIDES)                      #AllSides
                | EACH? side (COMMA side)* (SIDE|SIDES) #ListedSides
                ;

side            : TOP
                | BOTTOM
                | NORTH
                | EAST
                | SOUTH
                | WEST
                | LEFT
                | RIGHT
                | FRONT
                | BACK
                | NULL
                ;

slotQualifier   : EACH? (SLOTS | SLOT) numberSet;
numberSet       : numberRange (COMMA numberRange)*;
numberRange     : NOT? numberExpression (DASH numberExpression)? ;


emptyslots      : EMPTY (SLOTS | SLOT) IN ;

identifier : (IDENTIFIER | REDSTONE | GLOBAL | SECOND | SECONDS | TOP | BOTTOM | LEFT | RIGHT | FRONT | BACK) ;

// GENERAL
string: STRING ;



//
// LEXER
//

// IF STATEMENT
IF      : I F ;
THEN    : T H E N ;
ELSE    : E L S E ;

HAS     : H A S ;
OVERALL : O V E R A L L ;
SOME    : S O M E ;
ONE     : O N E ;
LONE    : L O N E ;

// BOOLEAN LOGIC
TRUE    : T R U E ;
FALSE   : F A L S E ;
NOT     : N O T ;
AND     : A N D ;
OR      : O R ;

// QUANTITY LOGIC
GT        : G T ;
GT_SYMBOL : '>' ;
LT        : L T ;
LT_SYMBOL : '<' ;
EQ        : E Q ;
EQ_SYMBOL : '=' ;
LE        : L E ;
LE_SYMBOL : '<=' ;
GE        : G E ;
GE_SYMBOL : '>=' ;

// IO LOGIC
FROM    : F R O M ;
TO      : T O ;
INPUT   : I N P U T ;
OUTPUT  : O U T P U T ;
WHERE   : W H E R E ;
SLOTS   : S L O T S ;
SLOT    : S L O T ;
RETAIN  : R E T A I N ;
EACH    : E A C H ;
ALL     : A N Y ;
EXCEPT  : E X C E P T ;
INTERSECT  : I N T E R S E C T ;
UNION   : U N I O N ;
FORGET  : F O R G E T ;
EMPTY   : E M P T Y ;
IN      : I N ;

// WITH LOGIC
WITHOUT : W I T H O U T;
WITH    : W I T H ;
TAG     : T A G ;
HASHTAG : '#' ;

// ROUND ROBIN
ROUND : R O U N D ;
ROBIN : R O B I N ;
BY    : B Y ;
LABEL : L A B E L ;
BLOCK : B L O C K ;

// SIDE LOGIC
TOP     : T O P ;
BOTTOM  : B O T T O M ;
NORTH   : N O R T H ;
EAST    : E A S T ;
SOUTH   : S O U T H ;
WEST    : W E S T ;
SIDES   : S I D E S ;
SIDE    : S I D E ;
LEFT    : L E F T ;
RIGHT   : R I G H T ;
FRONT   : F R O N T ;
BACK    : B A C K ;
NULL    : N U L L ;


// TIMER TRIGGERS
TICKS   : T I C K S ;
TICK    : T I C K ;
SECONDS : S E C O N D S ;
SECOND  : S E C O N D ;
GLOBAL  : (G L O B A L) | G;
OFFSET : O F F S E T ;

// REDSTONE TRIGGER
REDSTONE        : R E D S T O N E ;
PULSE           : P U L S E;

// PROGRAM SYMBOLS
DO              : D O ;
END             : E N D ;
NAME            : N A M E ;

// GENERAL SYMBOLS
// used by triggers and as a set operator
EVERY           : E V E R Y ;

COMMA   : ',';
COLON   : ':';
SLASH   : '/';
DASH    : '-';
PERCENT : '%';
CARET   : '^';
PLUS    : '+';
LPAREN  : '(';
RPAREN  : ')';
ASTERISK: '*';

NUMBER                  : [0-9]+ ;
IDENTIFIER              : [a-zA-Z_*][a-zA-Z0-9_*]* | ASTERISK; // Note that the * in the square brackets is a literl

STRING : '"' (~'"'|'\\"')* '"' ;

LINE_COMMENT : '--' ~[\r\n]* -> channel(HIDDEN);
//LINE_COMMENT : '--' ~[\r\n]* (EOF|'\r'? '\n');

WS
        :   [ \r\t\n]+ -> channel(HIDDEN)
        ;

UNUSED
        :   {INCLUDE_UNUSED}? . -> channel(HIDDEN)
        ;

fragment A  :('a' | 'A') ;
fragment B  :('b' | 'B') ;
fragment C  :('c' | 'C') ;
fragment D  :('d' | 'D') ;
fragment E  :('e' | 'E') ;
fragment F  :('f' | 'F') ;
fragment G  :('g' | 'G') ;
fragment H  :('h' | 'H') ;
fragment I  :('i' | 'I') ;
fragment J  :('j' | 'J') ;
fragment K  :('k' | 'K') ;
fragment L  :('l' | 'L') ;
fragment M  :('m' | 'M') ;
fragment N  :('n' | 'N') ;
fragment O  :('o' | 'O') ;
fragment P  :('p' | 'P') ;
fragment Q  :('q' | 'Q') ;
fragment R  :('r' | 'R') ;
fragment S  :('s' | 'S') ;
fragment T  :('t' | 'T') ;
fragment U  :('u' | 'U') ;
fragment V  :('v' | 'V') ;
fragment W  :('w' | 'W') ;
fragment X  :('x' | 'X') ;
fragment Y  :('y' | 'Y') ;
fragment Z  :('z' | 'Z') ;