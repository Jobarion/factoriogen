grammar Language;

assignment: var=VarName '=' x=completeExpression;

completeExpression: expr;

expr
    : '(' wrapped=expr ')'
    | left=expr (op=MUL | op=DIV | op=MOD) right=expr
    | left=expr (op=ADD | op=SUB) right=expr
    | left=expr (op=LSH | op=RSH) right=expr
    | left=expr op=BAND right=expr
    | left=expr op=BOR right=expr
    | left=expr op=BXOR right=expr
    | numberLit=IntLiteral
    | var=VarName ('(' vecAccessor=StringLiteral ')')?
    | 'sum(' sumExpr=expr ')'
    | 'count(' countExpr=expr ')'
    | NETWORK_IN
    ;

ifExpr: 'if' '(' ifCond=boolExpr ')' ifPart=ifStatement ('else' elsePart=elseStatement)?;
elseStatement: statement;
ifStatement: statement;

whileExpr: 'while' '(' loopCond=boolExpr ')' loopStatement=loopBody;

loopBody: statement;

boolExprComponent
    : expr
    | 'any(' anyExpr=completeExpression ')'
    | 'all(' allExpr=completeExpression ')';

boolExpr
    : '(' boolExpr ')'
    | NOT negated=boolExpr
    | left=boolExpr op=(AND | OR | XOR) right=boolExpr
    | leftComponent=boolExprComponent op=(LT | GT | LEQ | GEQ | EQ | NEQ) rightComponent=boolExprComponent;

block: '{' blockStatement+ '}';

blockStatement: statement;

statement
    : block
    | assignment ';'
    | ifExpr
    | whileExpr;

VarName: [a-zA-Z][a-zA-Z0-9_]*;
Type: 'int';
IntLiteral: '-'?[0-9]+;
StringLiteral: '"'[-a-z]'"';

ADD: '+';
SUB: '-';
MUL: '*';
DIV: '/';
MOD: '%';
LSH: '>>';
RSH: '<<';
BAND: '&';
BOR: '|';
BXOR: '^';

AND: '&&';
OR: '||';
XOR: '^';
NOT: '!';

LT: '<';
GT: '>';
LEQ: '<=';
GEQ: '>=';
EQ: '==';
NEQ: '!=';

NETWORK_IN: 'IN';

WS : [ \t\n\r]+ -> skip ;
WS_OPT: (' ' | '\t')+;
